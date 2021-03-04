package com.beyondxia.plugin

import com.android.annotations.NonNull
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.beyondxia.plugin.utils.RegisterUtils
import com.beyondxia.plugin.utils.TransformUtil
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 *
 * Create by ChenWei on 2018/8/29 16:25
 *
 **/
class PATransform extends Transform {

    private Project mProject

    public static File fileContainsInitClass
    public static List<String> registerList = new ArrayList()


    PATransform(Project project) {
        this.mProject = project
    }

    @Override
    String getName() {
        return "ModulesTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }


    @Override
    void transform(@NonNull TransformInvocation transformInvocation) {
        //首先获取构建类型并给mPool添加classPath
        TransformUtil.appendClassPathCore(mProject)

        transformInvocation.inputs.each { TransformInput input ->
            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                TransformUtil.handleDirInput(directoryInput.file.absolutePath, mProject)
                if (mProject.modulesConfig.registerWithPlugin) {
                    RegisterUtils.scanDirectory(directoryInput.file)
                }
                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            //遍历jar文件，对jar不进行操作，但是要输出到指定目录
            input.jarInputs.each { JarInput jarInput ->
                def jarPath = jarInput.file.absolutePath
                if (TransformUtil.jarNeedHandle(jarPath, mProject)) {
                    TransformUtil.handleJarInput(jarPath, mProject)
                }
                //重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                if (mProject.modulesConfig.registerWithPlugin) {
                    if (RegisterUtils.shouldProcessPreDexJar(jarName)) {
                        RegisterUtils.scanJar(jarInput.file, dest)
                    }
                }
                FileUtils.copyFile(jarInput.file, dest)
            }

        }

        if (mProject.modulesConfig.registerWithPlugin) {
            RegisterUtils.insertCodeToInitClass()
        }

    }
}
