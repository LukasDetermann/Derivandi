package com.derivandi.internal.processor;

import com.derivandi.api.processor.DiagnosticContext;
import com.derivandi.api.processor.HandlerStep;
import com.derivandi.api.processor.ProcessingCallback;
import com.derivandi.api.processor.SimpleContext;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.SourceVersion;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class HandlerStepImpl<OPTIONS>
      implements HandlerStep,
                 ProcessorConfigurationImpl<OPTIONS>
{
   private final Set<String> supportedOptions;
   private final Function<Map<String, String>, OPTIONS> optionsMapper;
   private final SourceVersion sourceVersion;
   private final ProcessingCallback<SimpleContext> processingCallable;
   private BiConsumer<SimpleContext, Throwable> exceptionHandler = createDefaultExceptionHandler();
   private BiConsumer<SimpleContext, DiagnosticContext> diagnosticHandler = createDefaultDiagnosticHandler();

   HandlerStepImpl(Set<String> supportedOptions,
                   Function<Map<String, String>, OPTIONS> optionsMapper,
                   SourceVersion sourceVersion,
                   ProcessingCallback<? extends SimpleContext> processingCallable)
   {
      this.supportedOptions = supportedOptions;
      this.optionsMapper = optionsMapper;
      this.sourceVersion = sourceVersion;
      //noinspection unchecked
      this.processingCallable = (ProcessingCallback<SimpleContext>) processingCallable;
   }

   HandlerStepImpl(SourceVersion sourceVersion,
                   ProcessingCallback<? extends SimpleContext> processingCallable)
   {
      this.supportedOptions = Collections.emptySet();
      this.optionsMapper = null;
      this.sourceVersion = sourceVersion;
      //noinspection unchecked
      this.processingCallable = (ProcessingCallback<SimpleContext>) processingCallable;
   }

   private HandlerStepImpl(HandlerStepImpl<OPTIONS> other)
   {
      this.supportedOptions = other.supportedOptions;
      this.optionsMapper = other.optionsMapper;
      this.sourceVersion = other.sourceVersion;
      this.processingCallable = other.processingCallable;
      this.exceptionHandler = other.exceptionHandler;
      this.diagnosticHandler = other.diagnosticHandler;
   }

   @Override
   public HandlerStep withExceptionHandler(BiConsumer<SimpleContext, Throwable> exceptionHandler)
   {
      Objects.requireNonNull(exceptionHandler);
      HandlerStepImpl<OPTIONS> copy = new HandlerStepImpl<>(this);
      copy.exceptionHandler = exceptionHandler;
      return copy;
   }

   @Override
   public HandlerStep withDiagnosticHandler(BiConsumer<SimpleContext, DiagnosticContext> diagnosticHandler)
   {
      Objects.requireNonNull(diagnosticHandler);
      HandlerStepImpl<OPTIONS> copy = new HandlerStepImpl<>(this);
      copy.diagnosticHandler = diagnosticHandler;
      return copy;
   }

   private static @NotNull BiConsumer<SimpleContext, Throwable> createDefaultExceptionHandler()
   {
      return (context, throwable) ->
      {
         StringWriter stringWriter = new StringWriter();
         PrintWriter printWriter = new PrintWriter(stringWriter);
         throwable.printStackTrace(printWriter);
         context.logAndRaiseError(stringWriter.toString());
         throw new RuntimeException(throwable);
      };
   }

   private static @NotNull BiConsumer<SimpleContext, DiagnosticContext> createDefaultDiagnosticHandler()
   {
      return (context, diagnosticContext) ->
      {
         if (!context.isProcessingOver())
         {
            String duration = Duration.between(diagnosticContext.getStart(), diagnosticContext.getEnd()).toString()
                                      .substring(2)
                                      .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                                      .toLowerCase();

            context.logInfo(diagnosticContext.getProcessorName() +
                            " took " +
                            duration +
                            " in round " +
                            diagnosticContext.getProcessingRound() +
                            "\n");
         }
      };
   }

   @Override
   public Set<String> getSupportedOptions()
   {
      return supportedOptions;
   }

   @Override
   public Optional<Function<Map<String, String>, OPTIONS>> getOptionsMapper()
   {
      return Optional.ofNullable(optionsMapper);
   }

   @Override
   public SourceVersion getSourceVersion()
   {
      return sourceVersion;
   }

   @Override
   public ProcessingCallback<? super SimpleContext> getProcessingCallable()
   {
      return processingCallable;
   }

   @Override
   public BiConsumer<SimpleContext, Throwable> getExceptionHandler()
   {
      return exceptionHandler;
   }

   @Override
   public BiConsumer<SimpleContext, DiagnosticContext> getDiagnosticHandler()
   {
      return diagnosticHandler;
   }
}