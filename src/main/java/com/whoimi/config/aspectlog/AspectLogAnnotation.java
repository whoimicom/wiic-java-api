package com.whoimi.config.aspectlog;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AspectLogAnnotation {
    public boolean pretty() default true;
    public boolean printReqRunner() default true;
    public boolean printResRunner() default true;
    public String keyword() default "";
}