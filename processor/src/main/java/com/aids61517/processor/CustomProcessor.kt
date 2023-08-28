package com.aids61517.processor

import com.aids61517.processor.annotation.TestClassAnnotationWithArgument
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
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

class CustomProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("qualifiedName = ${TestClassAnnotationWithArgument::class.qualifiedName}")
//        logger.error("123")
        val symbols =
            resolver.getSymbolsWithAnnotation(TestClassAnnotationWithArgument::class.qualifiedName!!)
                .filterIsInstance<KSClassDeclaration>()
        symbols.forEach { classDeclaration ->
            logger.info("class = $classDeclaration")
            classDeclaration.superTypes.forEach {
                logger.info("classDeclaration's super types = $it")
            }
        }

        if (!symbols.iterator().hasNext()) return emptyList()


        val outputStream: OutputStream = codeGenerator.createNewFile(
            dependencies = Dependencies(false),
            packageName = "com.aids61517",
            fileName = "GeneratedFunctions"
        )
        val sink = outputStream.sink()
            .buffer()
//            .use {
//                it.writeString("package com.aids61517", Charset.defaultCharset())
//                it.writeUtf8("\r\n")
////                it.writeString("package com.aids61517", Charset.defaultCharset())
//            }
        sink.run {
            writeString("package com.aids61517", Charset.defaultCharset())
            writeUtf8("\r\n")
        }

        symbols.forEach { it.accept(Visitor(sink), Unit) }

        sink.close()

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
            sink.writeUtf8("fun get$classDeclaration(\n")
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