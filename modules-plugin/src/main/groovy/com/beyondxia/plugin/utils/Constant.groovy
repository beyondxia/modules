package com.beyondxia.plugin.utils

/**
 * Create by ChenWei on 2018/9/6 14:35
 **/
class Constant {

    static
    final String DIRECTORY_DEBUG = ".*${File.separator}build${File.separator}intermediates${File.separator}classes${File.separator}[a-z,A-Z]*[${File.separator}]?debug.*"

    static
    final String DIRECTORY_DEBUG_REGEX = "build${File.separator}intermediates${File.separator}classes${File.separator}[a-z,A-Z]*[${File.separator}]?debug${File.separator}"

    static
    final String DIRECTORY_RELEASE = ".*${File.separator}build${File.separator}intermediates${File.separator}classes${File.separator}[a-z,A-Z]*[${File.separator}]?release.*"

    static
    final String DIRECTORY_RELEASE_REGEX = "build${File.separator}intermediates${File.separator}classes${File.separator}[a-z,A-Z]*[${File.separator}]?release${File.separator}"

    static final String MODULE_JAR_DEBUG = ".*${File.separator}build${File.separator}intermediates${File.separator}intermediate-jars${File.separator}debug${File.separator}.*"
    static final String MODULE_JAR_RELEASE = ".*${File.separator}build${File.separator}intermediates${File.separator}intermediate-jars${File.separator}release${File.separator}.*"

}
