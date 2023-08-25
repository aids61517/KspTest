package com.aids61517.processor

import com.aids61517.processor.annotation.TestClassAnnotationWithArgument
import com.aids61517.processor.annotation.ViewBindingClassAnnotation
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.getFunctionDeclarationsByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.symbol.Variance
import com.google.devtools.ksp.validate
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.OutputStream
import java.nio.charset.Charset

class ActivityFilterProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("ActivityFilterProcessor")
        resolver.getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                it.superTypes
                    .any { it.resolve().declaration.simpleName.asString() == "ViewBindingActivity" }
            }
            .forEach {
                logger.info("ActivityFilterProcessor class = $it")
                val parentClassDeclaration = it.superTypes
                    .first { it.resolve().declaration.simpleName.asString() == "ViewBindingActivity" }
                val resolveType = parentClassDeclaration.resolve()
                parentClassDeclaration.element?.typeArguments
                logger.info("parent argument type = ${parentClassDeclaration.element?.typeArguments}")
                resolveType.arguments.forEach {
//                    logger.info("parent argument type = $it")
                }
//                it.getAllProperties()
//                    .filter { it.validate() }
//                    .find { it.simpleName.asString() == "_binding" }
//                    ?.let {
//                        val resolveType = it.type.resolve()
//                        logger.info("_binding type = ${resolveType.declaration.simpleName.asString()}")
//                    }
            }
//        resolver.getFunctionDeclarationsByName("onCreateViewBinding")
////            .filter { it.functionKind == FunctionKind.MEMBER }
//            .forEach {
//                logger.info("ActivityFilterProcessor function = ${it.functionKind}")
//            }


//        val symbols =
//            resolver.getSymbolsWithAnnotation(ViewBindingClassAnnotation::class.qualifiedName!!)
//                .filterIsInstance<KSClassDeclaration>()
//        val symbol =
//            symbols.find { it.simpleName.asString() == "ViewBindingActivity" } ?: return emptyList()
//
//        symbol.getAllProperties()
//            .filter { it.validate() }
//            .first { it.simpleName.asString() == "_binding" }
//            .let {
//                val resolveType = it.type.resolve()
//                logger.info("_binding type = ${resolveType.declaration.simpleName.asString()}")
//            }


//        resolver.getClassDeclarationByName<>()
//        resolver.getAllFiles()
//            .forEach {
////                it
//                logger.info("ActivityFilterProcessor file = $it")
////                it.containingFile
//            }
//        val symbols =
//            resolver.getSymbolsWithAnnotation(TestClassAnnotationWithArgument::class.qualifiedName!!)
////                .filterIsInstance<KSClassDeclaration>()
//        symbols.forEach {
//            logger.info("ActivityFilterProcessor symbol = $it")
//        }
//        logger.info("qualifiedName = ${TestClassAnnotationWithArgument::class.qualifiedName}")
//        val symbols =
//            resolver.getSymbolsWithAnnotation(TestClassAnnotationWithArgument::class.qualifiedName!!)
//                .filterIsInstance<KSClassDeclaration>()
//        symbols.forEach { classDeclaration ->
//            logger.info("class = $classDeclaration")
//            classDeclaration.superTypes.forEach {
//                logger.info("classDeclaration's super types = $it")
//            }
//        }
//
//        if (!symbols.iterator().hasNext()) return emptyList()
//
//
//        val outputStream: OutputStream = codeGenerator.createNewFile(
//            dependencies = Dependencies(false),
//            packageName = "com.aids61517",
//            fileName = "GeneratedFunctions"
//        )
//        val sink = outputStream.sink()
//            .buffer()
////            .use {
////                it.writeString("package com.aids61517", Charset.defaultCharset())
////                it.writeUtf8("\r\n")
//////                it.writeString("package com.aids61517", Charset.defaultCharset())
////            }
//        sink.run {
//            writeString("package com.aids61517", Charset.defaultCharset())
//            writeUtf8("\r\n")
//        }
//
//        symbols.forEach { it.accept(Visitor(sink), Unit) }
//
//        sink.close()

        return emptyList()
    }

    inner class Visitor(private val sink: BufferedSink) : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            logger.info("visitClassDeclaration $classDeclaration")
            logger.info("visitClassDeclaration classKind = ${classDeclaration.classKind}")
            if (classDeclaration.classKind != ClassKind.INTERFACE) return

            val annotation = classDeclaration.annotations.first {
                it.shortName.asString() == "TestClassAnnotationWithArgument"
            }

            val nameArgument = annotation.arguments.first {
                it.name?.asString() == "name"
            }

            logger.info("value of argument = ${nameArgument.value}")

            val properties: Sequence<KSPropertyDeclaration> = classDeclaration.getAllProperties()
                .filter { it.validate() }
            sink.writeUtf8("fun functionName(\n")
            properties.forEach {
                logger.info("property before filter = $it")
                visitPropertyDeclaration(it, data)
            }
            sink.writeUtf8(") {\n")
            sink.writeUtf8("}\n")
        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            logger.info("visitPropertyDeclaration $property")
            val propertyName = property.simpleName.asString()
            val propertyResolveType = property.type.resolve()
            val type = propertyResolveType.declaration.qualifiedName?.asString()
            sink.writeUtf8("\t$propertyName: $type")
            val genericArguments = property.type.element?.typeArguments ?: emptyList()
            visitTypeArguments(genericArguments)

            sink.writeUtf8(",\n")
        }

        override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit) {
            logger.info("visitTypeArgument $typeArgument")
            when (typeArgument.variance) {
                Variance.STAR -> sink.writeUtf8("*")
                Variance.COVARIANT, Variance.CONTRAVARIANT -> sink.writeUtf8(typeArgument.variance.label)
                Variance.INVARIANT -> {
                }
            }

            val resolveType = typeArgument.type?.resolve()
            resolveType?.declaration?.qualifiedName?.asString()?.let {
                sink.writeUtf8(it)
            }

            val genericArguments = typeArgument.type?.element?.typeArguments ?: emptyList()
            visitTypeArguments(genericArguments)

            if (resolveType?.nullability == Nullability.NULLABLE) {
                sink.writeUtf8("?")
            }
        }

        private fun visitTypeArguments(typeArguments: List<KSTypeArgument>) {
            if (typeArguments.isNotEmpty()) {
                sink.writeUtf8("<")
                typeArguments.forEachIndexed { i, arg ->
                    visitTypeArgument(arg, data = Unit)
                    if (i < typeArguments.lastIndex) {
                        sink.writeUtf8(", ")
                    }
                }
                sink.writeUtf8(">")
            }
        }
    }
}