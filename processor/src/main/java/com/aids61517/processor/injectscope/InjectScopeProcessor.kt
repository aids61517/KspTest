package com.aids61517.processor.injectscope

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

class InjectScopeProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {
    companion object {
        private const val viewModelScopeUserName = "com.aids61517.model.ViewModelScopeUser"
        private const val baseViewModelName = "com.aids61517.model.BaseViewModel"
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("InjectScopeProcessor")
        val handlerSet = mutableSetOf<KSClassDeclaration>()
        val viewModelSet = mutableSetOf<KSClassDeclaration>()
        val viewModelList = resolver.getNewFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter { isType(it, baseViewModelName) }

        logger.info("InjectScopeProcessor handler count = ${handlerSet.size}")
        logger.info("InjectScopeProcessor viewModel count = ${viewModelSet.size}")

        var viewModelScopeTypeName: TypeName? = null
        viewModelList.forEach { viewModelDeclaration ->
            val viewModelScopeParams = viewModelDeclaration.primaryConstructor?.parameters?.filter {
                logger.info("InjectScopeProcessor constructor param name = ${it.name?.asString()}")
                logger.info("InjectScopeProcessor constructor param type = ${it.type}")
                val typeDeclaration = it.type.resolve().declaration
                val isViewModelScopeUser = typeDeclaration is KSClassDeclaration &&
                        isType(typeDeclaration, viewModelScopeUserName)

                if (viewModelScopeTypeName == null && isViewModelScopeUser && typeDeclaration is KSClassDeclaration) {
                    logger.info("InjectScopeProcessor typeDeclaration = $typeDeclaration")
                    viewModelScopeTypeName =
                        typeDeclaration.superTypes.first {
                            it.resolve().declaration.qualifiedName?.asString() == viewModelScopeUserName
                        }.resolve().toTypeName()
                    logger.info("InjectScopeProcessor viewModelScopeTypeName = $viewModelScopeTypeName")
                }
                isViewModelScopeUser
            } ?: emptyList()
            if (viewModelScopeParams.isNotEmpty()) {
                val fileName = viewModelDeclaration.simpleName.asString() + "HandlerList"
                val fileBuilder =
                    FileSpec.builder(viewModelDeclaration.packageName.asString(), fileName)
                val returnTypeName = List::class.asClassName()
                    .parameterizedBy(viewModelScopeTypeName!!)
                val funBuilder = FunSpec.builder("generate$fileName")
                    .receiver(viewModelDeclaration.asStarProjectedType().toTypeName())
                    .returns(returnTypeName)

                with(StringBuilder()) {
                    append("return listOf(\n")

                    viewModelScopeParams.map {
                        it.name!!.asString()
                    }.forEach {
                        append("\t$it,\n")
                    }

                    append(")")
                    toString()
                }.let { funBuilder.addStatement(it) }

                fileBuilder.addFunction(funBuilder.build())
                    .build()
                    .writeTo(
                        codeGenerator = codeGenerator,
                        dependencies = Dependencies(false, viewModelDeclaration.containingFile!!),
                    )
            }
        }
//        val symbols =
//            resolver.getSymbolsWithAnnotation(GenConfigMap::class.qualifiedName!!)
//                .filterIsInstance<KSClassDeclaration>()
//        symbols.forEach { classDeclaration ->
//            logger.info("GenConfigProcessor class = $classDeclaration")
//            val containsSerializable = classDeclaration.annotations
//                .any {
//                    val resolveType = it.annotationType.resolve()
//                    resolveType.declaration.qualifiedName?.asString() == "kotlinx.serialization.Serializable"
//                }
//
//            logger.info("GenConfigProcessor containsSerializable = $containsSerializable")
//            if (containsSerializable) {
//                val declaredProperties = classDeclaration.getDeclaredProperties()
//                    .filter {
//                        it.annotations.any {
//                            val resolveType = it.annotationType.resolve()
//                            resolveType.declaration.qualifiedName?.asString() == "kotlinx.serialization.SerialName"
//                        }
//                    }
//                    .toList()
//                if (declaredProperties.isNotEmpty()) {
//                    val fileName = classDeclaration.simpleName.asString() + "Map"
//                    val fileBuilder =
//                        FileSpec.builder(classDeclaration.packageName.asString(), fileName)
//                    val typeName = Map::class.parameterizedBy(String::class, String::class)
//                    val funBuilder = FunSpec.builder("generate$fileName")
//                        .returns(typeName)
//
//                    with(StringBuilder()) {
//                        append("return mapOf(\n")
//
//                        declaredProperties.map {
//                            val ksAnnotation = it.annotations.first {
//                                val resolveType = it.annotationType.resolve()
//                                resolveType.declaration.qualifiedName?.asString() == "kotlinx.serialization.SerialName"
//                            }
//                            ksAnnotation.arguments.first().value as String
//                        }.forEach {
//                            append("\t\"$it\" to \"\",\n")
//                        }
//
//                        append(")")
//                        toString()
//                    }.let { funBuilder.addStatement(it) }
//
//                    fileBuilder.addFunction(funBuilder.build())
//                        .build()
//                        .writeTo(
//                            codeGenerator = codeGenerator,
//                            dependencies = Dependencies(false, classDeclaration.containingFile!!),
//                        )
//                }
//            }
//        }

        return emptyList()
    }

    private fun isType(
        ksClassDeclaration: KSClassDeclaration,
        viewModelScopeUserDeclaration: String,
    ): Boolean {
        return ksClassDeclaration.superTypes.any {
            it.resolve().declaration.qualifiedName?.asString() == viewModelScopeUserDeclaration
        }
    }
}