package com.beyondxia.plugin.utils

import com.beyondxia.plugin.PATransform
import com.beyondxia.plugin.constant.ClassConstant
import jdk.internal.org.objectweb.asm.ClassReader
import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.ClassWriter
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes
import org.apache.commons.io.IOUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class RegisterUtils {

    static TAG = "RegisterUtils-Modules -> "

    static void scanJar(File src, File desc) {

        JarFile jarFile = new JarFile(src)
        Enumeration<JarEntry> entries = jarFile.entries()

        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement()
            String jarEntryName = jarEntry.getName().replace("\\", "/")
            //记录需要修改的初始化class类所在的jar包
            if (ClassConstant.INIT_CLASS_FILE_NAME == jarEntryName) {
//                println TAG + "fileContainsInitClass: " + desc
                PATransform.fileContainsInitClass = desc
            }
            //记录注册的页面
            if (jarEntryName.startsWith(ClassConstant.SERVICE_ROOT_PACKAGE) && jarEntryName.endsWith(".class")) {
                def registerItem = jarEntryName
                        .replace("/", ".")
                        .substring(0, jarEntryName.length() - 6)
                PATransform.registerList.add(registerItem)
            }
        }
        jarFile.close()
    }


    static boolean shouldProcessPreDexJar(String path) {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }


    static void scanDirectory(File directoryFile) {
        def root = directoryFile.absolutePath.replace("\\", "/")
        if (!root.endsWith("/")) {
            root = "$root/"
        }

        directoryFile.eachFileRecurse { f ->

            def absolutePath = f.absolutePath.replace("\\", "/")

            if (f.isFile()
                    && absolutePath.contains(ClassConstant.SERVICE_ROOT_PACKAGE)
                    && f.name.endsWith("class")) {


                def registerItem = absolutePath
                        .replace(root, "")

                registerItem = registerItem
                        .replace("/", ".")
                        .substring(0, registerItem.length() - 6)

                PATransform.registerList.add(registerItem)
            }


        }
    }


    static void insertCodeToInitClass() {
//        println("$TAG insertCodeToInitClass : ~~~~~start~~~~~~")
//        println("$TAG fileContainsInitClass : ${PATransform.fileContainsInitClass}")
        if (PATransform.fileContainsInitClass != null) {
            realHandle(PATransform.fileContainsInitClass)
        }
    }

    static void realHandle(File jarFile) {

        def optJar = new File(jarFile.parent, "${jarFile.name}.opt")
        if (optJar.exists()) {
            optJar.delete()
        }

        def jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))

        def file = new JarFile(jarFile)
        def entries = file.entries()

        while (entries.hasMoreElements()) {
            def jarEntry = entries.nextElement()
            def entryName = jarEntry.name
            def zipEntry = new ZipEntry(entryName)
            def inputStream = file.getInputStream(zipEntry)
            jarOutputStream.putNextEntry(zipEntry)
//            println("$TAG entryName : $entryName")
            if (entryName == ClassConstant.INIT_CLASS_FILE_NAME) {
                def bytes = getNewInitClassByte(inputStream)
                jarOutputStream.write(bytes)
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            inputStream.close()
            jarOutputStream.closeEntry()
        }

        jarOutputStream.close()
        file.close()

        if (jarFile.exists()) {
            jarFile.delete()
        }
        optJar.renameTo(jarFile)
    }

    private static byte[] getNewInitClassByte(InputStream inputStream) {
        def cr = new ClassReader(inputStream)
        def cw = new ClassWriter(cr, 0)

        def cv = new InitClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)

        return cw.toByteArray()
    }

    static class InitClassVisitor extends ClassVisitor {

        InitClassVisitor(int i, ClassVisitor classVisitor) {
            super(i, classVisitor)
        }

        MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            def visitMethod = super.visitMethod(access, name, desc, signature, exceptions)

//            println("$TAG visitMethod : $name")
            if (name == ClassConstant.SERVICE_INIT_METHOD) {
                visitMethod = new RouteInitMethodVisitor(Opcodes.ASM5, visitMethod)
            }
            return visitMethod
        }

    }

    static class RouteInitMethodVisitor extends MethodVisitor {


        RouteInitMethodVisitor(int i, MethodVisitor methodVisitor) {
            super(i, methodVisitor)
        }

        @Override
        void visitInsn(int opcode) {
            if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
                PATransform.registerList.forEach { className ->
                    def name = className.replace("/", ".")
//                    println("$TAG visitInsn name : $name")
                    mv.visitLdcInsn(name)
                    mv.visitMethodInsn(
                            Opcodes.INVOKESTATIC,
                            ClassConstant.INIT_CLASS_NAME,
                            ClassConstant.SERVICE_DO_REGISTER_METHOD,
                            "(Ljava/lang/String;)V",
                            false)
                }
            }
            super.visitInsn(opcode)
        }

        @Override
        void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 4, maxLocals)
        }
    }


}
