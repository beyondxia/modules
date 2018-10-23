package com.beyondxia.plugin.utils

import com.beyondxia.annotation.ExportService
import javassist.ClassPool
import javassist.CtClass
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * Create by ChenWei on 2018/8/29 18:50
 **/
class TransformUtil {

    static ClassPool mPool = ClassPool.getDefault()

    static void appendClassPath(Project project, String path) {
        mPool.appendClassPath(project.android.bootClasspath[0].toString())
        mPool.importPackage("android.os.Bundle")

        if (path.endsWith(".jar")) {
            //module jar
            if (path.matches(Constant.MODULE_JAR_DEBUG)) {
                mPool.appendClassPath(project.rootDir.toString() + "${File.separator}modules-services-api${File.separator}build${File.separator}intermediates${File.separator}classes${File.separator}debug")
            } else if (path.matches(Constant.MODULE_JAR_RELEASE)) {
                mPool.appendClassPath(project.rootDir.toString() + "${File.separator}modules-services-api${File.separator}build${File.separator}intermediates${File.separator}classes${File.separator}release")
            } else {
                throw new IllegalArgumentException("Illegal path ${path}")
            }
        } else {
            //module directory
            if (path.matches(Constant.DIRECTORY_DEBUG)) {
                mPool.appendClassPath(project.rootDir.toString() + "${File.separator}modules-services-api${File.separator}build${File.separator}intermediates${File.separator}classes${File.separator}debug")
            } else if (path.matches(Constant.DIRECTORY_RELEASE)) {
                mPool.appendClassPath(project.rootDir.toString() + "${File.separator}modules-services-api${File.separator}build${File.separator}intermediates${File.separator}classes${File.separator}release")
            } else {
                throw new IllegalArgumentException("Illegal path ${path}")
            }
        }

    }

    static void handleJarInput(String path, Project project) {
        File jarFile = new File(path)

        // jar包解压后的保存路径
        String jarUnZipDir = jarFile.getParent() + File.separator + jarFile.getName().replace('.jar', '')

        // 解压jar包, 返回jar包中所有class的完整类名的集合（带.class后缀）
        List classNameList = JarZipUtil.unzipJar(path, jarUnZipDir)

        //解压后的字节码中是否含有注解
        boolean hasAnnotation = false
        // 注入代码
        mPool.appendClassPath(jarUnZipDir)
        for (String className : classNameList) {
            if (className.endsWith(".class")) {
                className = className.substring(0, className.length() - 6)
                CtClass ctClass = mPool.getCtClass(className)
                def annotation = ctClass.getAnnotation(ExportService.class)
                if (annotation != null) {
                    println("&&&&&&&&&&&" + className)
                    hasAnnotation = true
                    if (ctClass.isFrozen()) {
                        ctClass.defrost()
                    }
                    String packageName = className.substring(0, className.lastIndexOf("."))
                    String originClassName = className.substring(className.lastIndexOf(".") + 1, className.length())
                    String superClassName = originClassName + "Service"
                    CtClass superCtClass = mPool.get(packageName + "." + superClassName)
                    ctClass.setSuperclass(superCtClass)
                    ctClass.writeFile(jarUnZipDir)
                    superCtClass.detach()
                }
                ctClass.detach()
            }
        }
        if (hasAnnotation) {
            // 删除原来的jar包
            jarFile.delete()
            //重新打包jar
            JarZipUtil.zipJar(jarUnZipDir, jarFile.toString())
        }
        // 删除目录
        FileUtils.deleteDirectory(new File(jarUnZipDir))


        println("delete******** completed")
    }


    static void handleDirInput(String path, Project project) {
        mPool.appendClassPath(path)
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->

                String filePath = file.absolutePath

                if (filePath.endsWith(".class")) {
                    String classPath
                    if (filePath.matches(Constant.DIRECTORY_DEBUG)) {
                        classPath = filePath.split(Constant.DIRECTORY_DEBUG_REGEX)[1]
                    } else {
                        classPath = filePath.split(Constant.DIRECTORY_RELEASE_REGEX)[1]
                    }

                    String className = classPath.substring(0, classPath.length() - 6).replace('\\', '.').replace('/', '.')
                    CtClass ctClass = mPool.getCtClass(className)
                    def annotation = ctClass.getAnnotation(ExportService.class)
                    if (annotation != null) {
                        if (ctClass.isFrozen()) {
                            ctClass.defrost()
                        }
                        System.err.println("=========" + className)
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
