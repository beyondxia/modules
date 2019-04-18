package com.beyondxia.plugin.utils

/**
 * Create by ChenWei on 2018/9/6 14:35
 **/
class Constant {

//    static
//    final String DIRECTORY_DEBUG = ".*/build/intermediates/classes/[a-z,A-Z]*[/]?debug.*"

    static
    final String DIRECTORY_DEBUG = ".*/build/intermediates/.*/debug.*"

//    static
//    final String DIRECTORY_DEBUG_REGEX = "build/intermediates/classes/[a-z,A-Z]*[/]?debug/"

//    static
//    final String DIRECTORY_RELEASE = ".*/build/intermediates/classes/[a-z,A-Z]*[/]?release.*"

    static
    final String DIRECTORY_RELEASE = ".*/build/intermediates/.*/release.*"

//    static
//    final String DIRECTORY_RELEASE_REGEX = "build/intermediates/classes/[a-z,A-Z]*[/]?release/"
//
//    static final String MODULE_JAR_DEBUG = ".*/build/intermediates/intermediate-jars/debug/.*"
//    static final String MODULE_JAR_RELEASE = ".*/build/intermediates/intermediate-jars/release/.*"

    //框架生成文件的class文件地址
    static final def DIRECTORYS_MODULES_API_CLASS_DEBUG = ["/modules_services_api/build/intermediates/classes/debug", "/modules_services_api/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes"] as String[]
    static final def DIRECTORYS_MODULES_API_CLASS_RELEASE = ["/modules_services_api/build/intermediates/classes/release", "/modules_services_api/build/intermediates/javac/release/compileReleaseJavaWithJavac/classes"] as String[]


}
