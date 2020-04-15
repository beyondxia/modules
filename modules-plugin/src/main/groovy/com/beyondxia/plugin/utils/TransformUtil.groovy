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

    static void appendClassPathByDirectory(Project project, String path) {
        mPool.appendClassPath(project.android.bootClasspath[0].toString())
        mPool.importPackage("android.os.Bundle")
        if (path.replace("\\", "/").matches(Constant.DIRECTORY_DEBUG) ||
                path.replace("\\", "/").matches(Constant.DIRECTORY_KOTLIN_DEBUG)) {
            Constant.DIRECTORYS_MODULES_API_CLASS_DEBUG.each {
                def classPath = project.rootProject.project("modules_services_api").projectDir.absolutePath + SystemUtils.getPathByOs(it)
                mPool.appendClassPath(classPath)
                println("添加的classPath路径为: ${classPath}")
            }
//            mPool.appendClassPath(project.rootDir.toString() + SystemUtils.getPathByOs("/modules_services_api/build/intermediates/classes/debug"))
//            println("添加的classPath路径为:/modules_services_api/build/intermediates/classes/debug")
        } else if (path.replace("\\", "/").matches(Constant.DIRECTORY_RELEASE) ||
                path.replace("\\", "/").matches(Constant.DIRECTORY_KOTLIN_RELEASE)) {
            Constant.DIRECTORYS_MODULES_API_CLASS_RELEASE.each {
                def classPath = project.rootProject.project("modules_services_api").projectDir.absolutePath + SystemUtils.getPathByOs(it)
                mPool.appendClassPath(classPath)
                println("添加的classPath路径为: $classPath")
            }
//            mPool.appendClassPath(project.rootDir.toString() + SystemUtils.getPathByOs("/modules_services_api/build/intermediates/classes/release"))
//            println("添加的classPath路径为:/modules_services_api/build/intermediates/classes/release")
        } else {
            throw new IllegalArgumentException("Illegal path ${path} - directory")
        }

//        if (path.endsWith(".jar")) {
//            //module jar
//            if (path.replace("\\", "/").matches(Constant.MODULE_JAR_DEBUG)) {
//                mPool.appendClassPath(project.rootDir.toString() + SystemUtils.getPathByOs("/modules_services_api/build/intermediates/classes/debug"))
//            } else if (path.replace("\\", "/").matches(Constant.MODULE_JAR_RELEASE)) {
//                mPool.appendClassPath(project.rootDir.toString() + SystemUtils.getPathByOs("/modules_services_api/build/intermediates/classes/release"))
//            } else {
//                throw new IllegalArgumentException("Illegal path ${path} - .jar")
//            }
//        } else {
//            //module directory
//            if (path.replace("\\", "/").matches(Constant.DIRECTORY_DEBUG)) {
//                mPool.appendClassPath(project.rootDir.toString() + SystemUtils.getPathByOs("/modules_services_api/build/intermediates/classes/debug"))
//            } else if (path.replace("\\", "/").matches(Constant.DIRECTORY_RELEASE)) {
//                mPool.appendClassPath(project.rootDir.toString() + SystemUtils.getPathByOs("/modules_services_api/build/intermediates/classes/release"))
//            } else {
////                throw new IllegalArgumentException("Illegal path ${path} - directory")
//            }
//        }

    }

    static void handleJarInput(String path, Project project) {
        println("=========>handleJarInput:${path}<============")
        File jarFile = new File(path)
        // jar包解压后的保存路径
        String jarUnZipDir = jarFile.getParent() + File.separator + jarFile.getName().replace('.jar', '')
        //若文件夹存在，则删除
        FileUtils.deleteDirectory(new File(jarUnZipDir))

        // 解压jar包, 返回jar包中所有class的完整类名的集合（带.class后缀）
        List classNameList = JarZipUtil.unzipJar(path, jarUnZipDir)

        //解压后的字节码中是否含有注解
        boolean hasAnnotation = false
        // 注入代码
        mPool.appendClassPath(jarUnZipDir)
        for (String className : classNameList) {
            if (isValidClass(className) && classNeedHandle(className, project)) {
                className = className.substring(0, className.length() - 6)
                CtClass ctClass = mPool.getCtClass(className)
                def annotation = ctClass.getAnnotation(ExportService.class)
                if (annotation != null) {
                    println("handleJarClass:${className}")
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
            //先备份一下jar包
            File jarFileBak = new File(path.replace(".jar", "_bak.jar"))
            FileUtils.copyFile(jarFile, jarFileBak)
            // 删除原来的jar包
            jarFile.delete()
            try {
                //重新打包jar
                JarZipUtil.zipJar(jarUnZipDir, jarFile.toString())
            } catch (Exception e) {
                println("zipJar exception: ${e.getMessage()}")
                //恢复原来的jar包
                FileUtils.copyFile(jarFileBak, jarFile)
                throw e
            } finally {
                // 删除备份的jar包
                jarFileBak.delete()
            }
        }
        // 删除目录
        FileUtils.deleteDirectory(new File(jarUnZipDir))
        println("===========>handleJarInput:${path}finish<==============")
    }


    static void handleDirInput(String path, Project project) {
        mPool.appendClassPath(path)
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->

                String filePath = file.absolutePath.replace("\\", "/")

                if (isValidClass(filePath)) {
                    String classPath
//                    if (filePath.matches(Constant.DIRECTORY_DEBUG)) {
//                        classPath = filePath.split(Constant.DIRECTORY_DEBUG_REGEX)[1]
//                    } else {
//                        classPath = filePath.split(Constant.DIRECTORY_RELEASE_REGEX)[1]
//                    }
//                    println("==========================path:${path}")
//                    println("==========================filePath:${filePath}")
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
                            println("==========================handleDirClass:${className}")
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
                System.err.println("################" + excludeJar)
                return false
            }
        }

        if (jarPath == null || "" == jarPath) {
            return false
        }
        if (!jarPath.endsWith(".jar")) {
            return false
        }
        //不处理gradle依赖的jar包
        if (jarPath.contains(".gradle/caches")) {
            return false
        }
        return true
    }

}
