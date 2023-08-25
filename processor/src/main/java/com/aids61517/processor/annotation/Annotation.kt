package com.aids61517.processor.annotation

@Target(AnnotationTarget.CLASS)
annotation class TestClassAnnotation

@Target(AnnotationTarget.CLASS)
annotation class ViewBindingClassAnnotation

@Target(AnnotationTarget.CLASS)
annotation class TestClassAnnotationWithArgument(val name: String)