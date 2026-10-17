package com.derivandi.api.processor;

import com.derivandi.internal.processor.ContextImpl;
import com.derivandi.internal.processor.ProcessorBuilderImpl;
import com.derivandi.internal.processor.ProcessorConfigurationImpl;
import com.derivandi.internal.processor.ProxyPrintStream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

import static java.lang.System.out;

public abstract class DerivandiProcessor
      extends AbstractProcessor
{
   private ProcessorConfigurationImpl<Object> configuration;

   private int processingRound = 0;
   private ContextImpl context;

   @Override
   protected final synchronized boolean isInitialized()
   {
      return super.isInitialized();
   }

   @Override
   public final synchronized void init(ProcessingEnvironment processingEnv)
   {
      super.init(processingEnv);
      //noinspection
      ProcessorConfiguration processorConfiguration = buildProcessor(new ProcessorBuilderImpl());
      //noinspection unchecked
      configuration = (ProcessorConfigurationImpl<Object>) Objects.requireNonNull(processorConfiguration);

      proxySystemOut();
   }

   public abstract ProcessorConfiguration buildProcessor(ProcessorBuilder processorBuilder);

   @Override
   public final Set<String> getSupportedAnnotationTypes()
   {
      return Set.of("*");
   }

   @Override
   public final Set<String> getSupportedOptions()
   {
      return configuration.getSupportedOptions();
   }

   /**
    * Needs to be at least the version of the compiled code. otherwise the processor will be ignored
    */
   @Override
   public final SourceVersion getSupportedSourceVersion()
   {
      return configuration.getSourceVersion();
   }

   /**
    * Intercepts the process call and initializes the {@link SimpleContext}.
    */
   @Override
   public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
   {
      Instant start = Instant.now();
      context = new ContextImpl(processingEnv, roundEnv, processingRound, configuration.getOptionsMapper().orElse(null));
      try
      {
         configuration.getProcessingCallable().process(context);
      }
      //the compiler can crash when an uncaught exception is thrown. so they are handled here. the default handler Logs and raises an error
      catch (Exception t)
      {
         configuration.getExceptionHandler().accept(context, t);
      }
      String processorName = getClass().isAnonymousClass() ? "anonymousProcessor" : getClass().getSimpleName();
      configuration.getDiagnosticHandler()
                   .accept(context, new DiagnosticContext(context, processorName, processingRound, start, Instant.now()));

      processingRound++;

      //claiming annotations is kinda useless
      return false;
   }

   private void proxySystemOut()
   {
      if (out instanceof ProxyPrintStream)
      {
         return;
      }
      System.setOut(new ProxyPrintStream(out,
                                         out.charset(),
                                         configuration.getSystemOutHandler(),
                                         () -> context));
   }
}
