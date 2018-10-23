package com.beyondxia.plugin.utils

/**
 * Create by ChenWei on 2018/9/6 14:35
 **/
class Constant {

    static
    final String DIRECTORY_DEBUG = SystemUtils.getPathByOs(".*/build/intermediates/classes/[a-z,A-Z]*[/]?debug.*")

    static
    final String DIRECTORY_DEBUG_REGEX = SystemUtils.getPathByOs("build/intermediates/classes/[a-z,A-Z]*[/]?debug/")

    static
    final String DIRECTORY_RELEASE = SystemUtils.getPathByOs(".*/build/intermediates/classes/[a-z,A-Z]*[/]?release.*")

    static
    final String DIRECTORY_RELEASE_REGEX = SystemUtils.getPathByOs("build/intermediates/classes/[a-z,A-Z]*[/]?release/")

    static
    final String MODULE_JAR_DEBUG = SystemUtils.getPathByOs(".*/build/intermediates/intermediate-jars/debug/.*")
    static
    final String MODULE_JAR_RELEASE = SystemUtils.getPathByOs(".*/build/intermediates/intermediate-jars/release/.*")


}
