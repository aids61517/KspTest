package com.aids61517.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class ActivityFilterProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ActivityFilterProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
            options = environment.options,
        )
    }
}