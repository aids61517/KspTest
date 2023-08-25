package com.aids61517.processor

import com.aids61517.processor.annotation.TestClassAnnotation
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration

class CustomProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("qualifiedName = ${TestClassAnnotation::class.qualifiedName}")
//        logger.error("123")
        val s = resolver.getSymbolsWithAnnotation(TestClassAnnotation::class.qualifiedName!!)
        s.forEach {
            logger.info("s = $it")
        }
//            .filterIsInstance<KSClassDeclaration>()
        return emptyList()
    }
}