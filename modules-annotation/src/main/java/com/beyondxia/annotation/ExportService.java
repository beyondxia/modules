package com.beyondxia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by ChenWei on 2018/8/30 08:59
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExportService {
    String moduleName() default "";

    // TODO should add the follow function here
    boolean preload() default false;
}

