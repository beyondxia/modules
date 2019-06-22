package com.beyondxia.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PAServicePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("modulesConfig", ConfigExtention)
        def android = project.extensions.findByType(AppExtension.class)
        if (android != null) {
            android.registerTransform(new PATransform(project))
        }
    }
}