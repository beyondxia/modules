package com.beyondxia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by ChenWei on 2018/8/24 15:42
 **/
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface ExportMethod {
}
