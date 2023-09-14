package com.aids61517.processor.genconfig

import com.aids61517.processor.annotation.GenConfigMap
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.writeTo

class GenConfigProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("GenConfigMap qualifiedName = ${GenConfigMap::class.qualifiedName}")
        val symbols =
            resolver.getSymbolsWithAnnotation(GenConfigMap::class.qualifiedName!!)
                .filterIsInstance<KSClassDeclaration>()
        symbols.forEach { classDeclaration ->
            logger.info("GenConfigProcessor class = $classDeclaration")
            val containsSerializable = classDeclaration.annotations
                .any {
                    val resolveType = it.annotationType.resolve()
                    resolveType.declaration.qualifiedName?.asString() == "kotlinx.serialization.Serializable"
                }

            logger.info("GenConfigProcessor containsSerializable = $containsSerializable")
            if (containsSerializable) {
                val declaredProperties = classDeclaration.getDeclaredProperties()
                    .filter {
                        it.annotations.any {
                            val resolveType = it.annotationType.resolve()
                            resolveType.declaration.qualifiedName?.asString() == "kotlinx.serialization.SerialName"
                        }
                    }
                    .toList()
                if (declaredProperties.isNotEmpty()) {
                    val fileName = classDeclaration.simpleName.asString() + "Map"
                    val fileBuilder =
                        FileSpec.builder(classDeclaration.packageName.asString(), fileName)
                    val typeName = Map::class.parameterizedBy(String::class, String::class)
                    val funBuilder = FunSpec.builder("generate$fileName")
                        .returns(typeName)

                    with(StringBuilder()) {
                        append("return mapOf(\n")

                        declaredProperties.map {
                            val ksAnnotation = it.annotations.first {
                                val resolveType = it.annotationType.resolve()
                                resolveType.declaration.qualifiedName?.asString() == "kotlinx.serialization.SerialName"
                            }
                            ksAnnotation.arguments.first().value as String
                        }.forEach {
                            append("\t\"$it\" to \"\",\n")
                        }

                        append(")")
                        toString()
                    }.let { funBuilder.addStatement(it) }

                    fileBuilder.addFunction(funBuilder.build())
                        .build()
                        .writeTo(
                            codeGenerator = codeGenerator,
                            dependencies = Dependencies(false),
                        )
                }
            }
        }

        return emptyList()
    }
}