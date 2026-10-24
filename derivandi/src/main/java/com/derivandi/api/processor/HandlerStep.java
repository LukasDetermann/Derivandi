package com.derivandi.api.processor;

import org.jetbrains.annotations.Contract;

import java.util.function.BiConsumer;

public interface HandlerStep extends ProcessorConfiguration
{
   @Contract(value = "_ -> new", pure = true)
   HandlerStep withExceptionHandler(BiConsumer<SimpleContext, Throwable> exceptionHandler);

   /**
    * Executed at the end of each round.
    * When the processing is over each Processor gets called one more time with {@link Context#isProcessingOver()} = true.
    */
   @Contract(value = "_ -> new", pure = true)
   HandlerStep withDiagnosticHandler(BiConsumer<SimpleContext, DiagnosticContext> diagnosticHandler);
}