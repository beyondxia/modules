package com.beyondxia.plugin.utils

import com.beyondxia.annotation.ExportService
import javassist.ClassPool
import javassist.CtClass
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * Create by ChenWei on 2018/8/29 18:50
 **/
class TransformUtil {

    static ClassPool mPool = ClassPool.getDefault()

    static void appendClassPathCore(Project project) {
        mPool.appendClassPath(project.android.bootClasspath[0].toString())
        mPool.importPackage("android.os.Bundle")

        def debugAPIDir = project.rootProject.project("modules_services_api").compileDebugJavaWithJavac.destinationDir
        def releaseAPIDir = project.rootProject.project("modules_services_api").compileReleaseJavaWithJavac.destinationDir
        if (debugAPIDir.exists()) {
            mPool.appendClassPath(debugAPIDir.absolutePath)
        }
        if (releaseAPIDir.exists()) {
            mPool.appendClassPath(releaseAPIDir.absolutePath)
        }
    }

    static void handleJarInput(String path, Project project) {

        def pathFile = new File(path)

        def tmpClassLocation = "${project.buildDir.absolutePath}${File.separator}tmp${File.separator}modules-class"
        def currentClassPath = mPool.appendClassPath(path)

        def pathJar = new JarFile(pathFile)
        def optJar = new File(pathFile.parent, "${pathFile.name}.opt")

        if (optJar.exists()) {
            optJar.delete()
        }

        def jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))
        def entries = pathJar.entries()
        while (entries.hasMoreElements()) {
            def jarEntry = entries.nextElement()
            def entryName = jarEntry.name
            def zipEntry = new ZipEntry(entryName)

            if (entryName.endsWith('.class')) {
                String cls = entryName.replace('\\', '.').replace('/', '.')
                cls = cls.substring(0, cls.length() - 6)
                try {
                    CtClass ctClass = mPool.getCtClass(cls)
                    def annotation = ctClass.getAnnotation(ExportService.class)
                    if (annotation != null) {
                        LoggerUtils.log("handleJarClass", cls)
                        if (ctClass.isFrozen()) {
                            ctClass.defrost()
                        }
                        String packageName = cls.substring(0, cls.lastIndexOf("."))
                        String originClassName = cls.substring(cls.lastIndexOf(".") + 1, cls.length())
                        String superClassName = originClassName + "Service"
                        CtClass superCtClass = mPool.get(packageName + "." + superClassName)
                        ctClass.setSuperclass(superCtClass)
                        ctClass.writeFile(tmpClassLocation)
                        superCtClass.detach()
                    }
                    ctClass.detach()
                } catch (e) {
                    LoggerUtils.log("javaAssist exception", e.message)
                }
            }

            def inputStream
            def newClassFile = new File(tmpClassLocation, entryName.replace("\\", File.separator))
            if (!newClassFile.directory && newClassFile.exists()) {
                LoggerUtils.log("newClassFile", newClassFile.absolutePath)
                inputStream = new FileInputStream(newClassFile)
            } else {
                inputStream = pathJar.getInputStream(zipEntry)
            }

            jarOutputStream.putNextEntry(zipEntry)
            jarOutputStream.write(IOUtils.toByteArray(inputStream))
            inputStream.close()
            jarOutputStream.closeEntry()
        }

        jarOutputStream.close()
        pathJar.close()

        mPool.removeClassPath(currentClassPath)

        if (pathFile.exists()) {
            FileUtils.forceDelete(pathFile)
        }
        optJar.renameTo(pathFile)

    }


    static void handleDirInput(String path, Project project) {
        mPool.appendClassPath(path)
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->

                String filePath = file.absolutePath.replace("\\", "/")

                if (isValidClass(filePath)) {
                    String classPath
                    if (path.endsWith("/") || path.endsWith("\\")) {
                        classPath = filePath.replace(path.replace("\\", "/"), "")
                    } else {
                        classPath = filePath.replace(path.replace("\\", "/") + "/", "")
                    }

                    String className = classPath.substring(0, classPath.length() - 6).replace('\\', '.').replace('/', '.')
                    if (classNeedHandle(className, project)) {
                        CtClass ctClass = mPool.getCtClass(className)
                        def annotation = ctClass.getAnnotation(ExportService.class)
                        if (annotation != null) {
                            LoggerUtils.log("handleDirClass", className)
                            if (ctClass.isFrozen()) {
                                ctClass.defrost()
                            }
                            String packageName = className.substring(0, className.lastIndexOf("."))
                            String originClassName = className.substring(className.lastIndexOf(".") + 1, className.length())
                            String superClassName = originClassName + "Service"
                            CtClass superCtClass = mPool.get(packageName + "." + superClassName)
                            ctClass.setSuperclass(superCtClass)
                            ctClass.writeFile(path)
                        }
                        ctClass.detach()
                    }
                }

            }
        }
    }

    /**
     * 文件是否是合法的class文件
     * @param classFilePath
     * @return
     */
    static boolean isValidClass(String classFilePath) {
        return classFilePath.endsWith(".class") && !classFilePath.endsWith("R.class") && !classFilePath.contains('$')
    }

    /**
     * 该class文件是否需要操作
     * @param className
     * @param project
     * @return
     */
    static boolean classNeedHandle(String className, Project project) {
        if (className == null) {
            return false
        }

        String[] classesPackage = project.modulesConfig.includeClassPackage
        if (classesPackage == null || classesPackage.length == 0) {
            return true
        }

        for (String classPackage : classesPackage) {
            if (className.startsWith(classPackage)) {
                return true
            }
        }
        return false
    }

    /**
     * 过滤不需要操作的jar包
     * @param jarPath
     * @param project
     * @return
     */
    static boolean jarNeedHandle(String jarPath, Project project) {

        def jarPathFile = new File(jarPath)
        if (!jarPathFile.exists()) {
            return false
        }
        if (!jarPathFile.isFile()) {
            return false
        }
        if (jarPathFile.length() == 0) {
            return false
        }

        for (String excludeJar : project.modulesConfig.excludeJars) {
            if (jarPath.endsWith(excludeJar)) {
                return false
            }
        }

        if (jarPath == null || "" == jarPath) {
            return false
        }
        if (!jarPath.endsWith(".jar")) {
            return false
        }
        def strs = project.modulesConfig.businessMatchStrings
        if (strs != null && strs.length != 0) {
            JarFile jarFile = new JarFile(new File(jarPath))
            Enumeration<JarEntry> entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                String jarEntryName = entries.nextElement().getName()
                for (reg in strs) {
                    if (jarEntryName.contains(reg)) {
                        jarFile.close()
                        return true
                    }
                }
            }
            jarFile.close()
            return false
        } else {
            return true
        }
    }
}
