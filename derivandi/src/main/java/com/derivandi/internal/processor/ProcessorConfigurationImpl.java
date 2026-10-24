package com.derivandi.internal.processor;

import com.derivandi.api.processor.DiagnosticContext;
import com.derivandi.api.processor.ProcessingCallback;
import com.derivandi.api.processor.ProcessorConfiguration;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.SourceVersion;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface ProcessorConfigurationImpl<OPTIONS>
      extends ProcessorConfiguration
{
   Set<String> getSupportedOptions();

   Optional<Function<Map<String, String>, OPTIONS>> getOptionsMapper();

   SourceVersion getSourceVersion();

   ProcessingCallback<? super SimpleContext> getProcessingCallable();

   BiConsumer<SimpleContext, Throwable> getExceptionHandler();

   BiConsumer<SimpleContext, DiagnosticContext> getDiagnosticHandler();
}