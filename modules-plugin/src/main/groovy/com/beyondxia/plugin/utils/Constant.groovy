package com.beyondxia.plugin.utils

/**
 * Create by ChenWei on 2018/9/6 14:35
 **/
class Constant {

    static
    final String DIRECTORY_DEBUG = ".*/build/intermediates/.*/debug.*"
    static
    final String DIRECTORY_KOTLIN_DEBUG = ".*/build/.*/.*kotlin.*/debug.*"

    static
    final String DIRECTORY_RELEASE = ".*/build/intermediates/.*/release.*"
    static
    final String DIRECTORY_KOTLIN_RELEASE = ".*/build/.*/.*kotlin.*/release.*"


    //框架生成文件的class文件地址
    static final def DIRECTORYS_MODULES_API_CLASS_DEBUG = ["/build/intermediates/classes/debug", "/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes"] as String[]
    static final def DIRECTORYS_MODULES_API_CLASS_RELEASE = ["/build/intermediates/classes/release", "/build/intermediates/javac/release/compileReleaseJavaWithJavac/classes"] as String[]


}
