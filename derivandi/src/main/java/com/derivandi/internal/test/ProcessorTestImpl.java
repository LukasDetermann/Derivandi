package com.derivandi.internal.test;

import com.derivandi.api.processor.*;
import com.derivandi.api.test.ProcessorTest;

import javax.annotation.processing.Processor;
import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ProcessorTestImpl implements ProcessorTest
{
   private List<? extends Processor> processors;
   private final List<JavaFileObject> toCompile;
   private final List<String> options;
   private final List<String> compiledClassNames;

   private ProcessorTestImpl(List<? extends Processor> processors,
                             List<JavaFileObject> toCompile,
                             List<String> options,
                             List<String> compiledClassNames)
   {
      this.processors = processors;
      this.toCompile = toCompile;
      this.options = options;
      this.compiledClassNames = compiledClassNames;
   }

   public ProcessorTestImpl()
   {
      toCompile = new ArrayList<>();
      options = new ArrayList<>();
      compiledClassNames = new ArrayList<>();
      compiledClassNames.add("java.lang.Object");
   }

   @Override
   public ProcessorTest withCodeToCompile(Path fileToCompile)
   {
      return withCodeToCompile(fileToCompile.toFile());
   }

   @Override
   public ProcessorTest withCodeToCompile(File fileToCompile)
   {
      if (!fileToCompile.getName().endsWith(".java"))
      {
         throw new IllegalArgumentException("can only compile .java files");
      }

      String sourceCode;
      try
      {
         sourceCode = String.join("\n", Files.readAllLines(fileToCompile.toPath()));
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }

      List<JavaFileObject> updated = new ArrayList<>(toCompile);
      updated.add(createJavaFileObject(fileToCompile.toURI(), sourceCode));
      return new ProcessorTestImpl(processors, updated, options, compiledClassNames);
   }

   @Override
   public ProcessorTest withCodeToCompile(String fileName, String code)
   {
      List<JavaFileObject> updated = new ArrayList<>(toCompile);
      updated.add(createJavaFileObject(createUri(fileName), code));
      return new ProcessorTestImpl(processors, updated, options, compiledClassNames);
   }

   @Override
   public ProcessorTest withOption(String option)
   {
      List<String> updated = new ArrayList<>(options);
      updated.add(option);
      return new ProcessorTestImpl(processors, toCompile, updated, compiledClassNames);
   }

   @Override
   public ProcessorTest withCompiledClass(String qualifiedName)
   {
      List<String> updated = new ArrayList<>(compiledClassNames);
      updated.add(qualifiedName);
      return new ProcessorTestImpl(processors, toCompile, options, updated);
   }

   @Override
   public ProcessorTest withCompiledClass(Class<?> aClass)
   {
      List<String> updated = new ArrayList<>(compiledClassNames);
      updated.add(aClass.getName());
      return new ProcessorTestImpl(processors, toCompile, options, updated);
   }

   @Override
   public void process(ProcessingCallback<SimpleContext> processingCallback)
   {
      this.processors = Collections.singletonList(createProcessor(processingCallback));
      compile();
   }

   @Override
   public void process(Processor... processors)
   {
      this.processors = Arrays.asList(processors);
      compile();
   }

   private void compile()
   {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      Objects.requireNonNull(compiler);
      JavaCompiler.CompilationTask compilerTask = compiler.getTask(null,
                                                                   new TestFileManager(compiler.getStandardFileManager(null, null, null)),
                                                                   diagnostic ->
                                                                   {
                                                                      if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR))
                                                                      {
                                                                         throw new IllegalStateException(diagnostic.toString());
                                                                      }
                                                                   },
                                                                   options,
                                                                   compiledClassNames,
                                                                   toCompile);

      compilerTask.setProcessors(processors);
      compilerTask.call();
   }

   private Processor createProcessor(ProcessingCallback<SimpleContext> processingCallback)
   {
      return new DerivandiProcessor()
      {
         @Override
         public ProcessorConfiguration buildProcessor(ProcessorBuilder processorBuilder)
         {
            return processorBuilder.process(processingCallback);
         }
      };
   }

   private SimpleJavaFileObject createJavaFileObject(URI uri, String sourceCode)
   {
      return new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE)
      {
         @Override
         public CharSequence getCharContent(boolean b)
         {
            return sourceCode;
         }
      };
   }

   private URI createUri(String fileName)
   {
      try
      {
         return getClass()
               .getProtectionDomain()
               .getCodeSource()
               .getLocation()
               .toURI()
               .resolve(fileName);
      }
      catch (SecurityException | URISyntaxException e)
      {
         return URI.create(fileName);
      }
   }
}