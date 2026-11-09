package com.derivandi.internal.test;

import com.derivandi.api.processor.*;
import com.derivandi.api.test.*;

import javax.annotation.processing.Processor;
import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;

public class ProcessorTestImpl
      implements ProcessorTest
{
   private final List<JavaFileCallback> javaFileCallbacks;
   private final List<JavaSourceFileCallback> javaSourceFileCallbacks;
   private final List<JavaClassFileCallback> javaClassFileCallbacks;
   private final List<OtherFileCallback> otherFileCallbacks;
   private final List<? extends Processor> processors;
   private final List<JavaFileObject> toCompile;
   private final List<String> options;
   private final List<String> compiledClassNames;

   private ProcessorTestImpl(List<JavaFileCallback> javaFileCallbacks,
                             List<JavaSourceFileCallback> javaSourceFileCallbacks,
                             List<JavaClassFileCallback> javaClassFileCallbacks,
                             List<OtherFileCallback> otherFileCallbacks,
                             List<? extends Processor> processors,
                             List<JavaFileObject> toCompile,
                             List<String> options,
                             List<String> compiledClassNames)
   {
      this.javaFileCallbacks = javaFileCallbacks;
      this.javaSourceFileCallbacks = javaSourceFileCallbacks;
      this.javaClassFileCallbacks = javaClassFileCallbacks;
      this.otherFileCallbacks = otherFileCallbacks;
      this.processors = processors;
      this.toCompile = toCompile;
      this.options = options;
      this.compiledClassNames = compiledClassNames;
   }

   public ProcessorTestImpl()
   {
      javaFileCallbacks = new ArrayList<>();
      javaSourceFileCallbacks = new ArrayList<>();
      javaClassFileCallbacks = new ArrayList<>();
      otherFileCallbacks = new ArrayList<>();
      processors = new ArrayList<>();
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
      return new ProcessorTestImpl(javaFileCallbacks,
                                   javaSourceFileCallbacks,
                                   javaClassFileCallbacks,
                                   otherFileCallbacks,
                                   processors,
                                   updated,
                                   options,
                                   compiledClassNames);
   }

   @Override
   public ProcessorTest withCodeToCompile(String fileName, String code)
   {
      List<JavaFileObject> updated = new ArrayList<>(toCompile);
      updated.add(createJavaFileObject(createUri(fileName), code));
      return new ProcessorTestImpl(javaFileCallbacks,
                                   javaSourceFileCallbacks,
                                   javaClassFileCallbacks,
                                   otherFileCallbacks,
                                   processors,
                                   updated,
                                   options,
                                   compiledClassNames);
   }

   @Override
   public ProcessorTest withOption(String option)
   {
      List<String> updated = new ArrayList<>(options);
      updated.add(option);
      return new ProcessorTestImpl(javaFileCallbacks,
                                   javaSourceFileCallbacks,
                                   javaClassFileCallbacks,
                                   otherFileCallbacks,
                                   processors,
                                   toCompile,
                                   updated,
                                   compiledClassNames);
   }

   @Override
   public ProcessorTest withCompiledClass(String qualifiedName)
   {
      List<String> updated = new ArrayList<>(compiledClassNames);
      updated.add(qualifiedName);
      return new ProcessorTestImpl(javaFileCallbacks,
                                   javaSourceFileCallbacks,
                                   javaClassFileCallbacks,
                                   otherFileCallbacks,
                                   processors,
                                   toCompile,
                                   options,
                                   updated);
   }

   @Override
   public ProcessorTest withCompiledClass(Class<?> aClass)
   {
      List<String> updated = new ArrayList<>(compiledClassNames);
      updated.add(aClass.getName());
      return new ProcessorTestImpl(javaFileCallbacks,
                                   javaSourceFileCallbacks,
                                   javaClassFileCallbacks,
                                   otherFileCallbacks,
                                   processors,
                                   toCompile,
                                   options,
                                   updated);
   }

   @Override
   public ProcessorTest process(ProcessingCallback<SimpleContext> processingCallback)
   {
      List<Processor> updated = new ArrayList<>(processors);
      updated.add(createProcessor(processingCallback));
      return new ProcessorTestImpl(javaFileCallbacks,
                                   javaSourceFileCallbacks,
                                   javaClassFileCallbacks,
                                   otherFileCallbacks,
                                   updated,
                                   toCompile,
                                   options,
                                   compiledClassNames);
   }

   @Override
   public ProcessorTest process(Processor... processors)
   {
      List<Processor> updated = new ArrayList<>(this.processors);
      updated.addAll(Arrays.asList(processors));
      return new ProcessorTestImpl(javaFileCallbacks,
                                   javaSourceFileCallbacks,
                                   javaClassFileCallbacks,
                                   otherFileCallbacks,
                                   updated,
                                   toCompile,
                                   options,
                                   compiledClassNames);
   }

   @Override
   public ProcessorTest withJavaFileCallback(JavaFileCallback javaFileCallback)
   {
      List<JavaFileCallback> updated = new ArrayList<>(this.javaFileCallbacks);
      updated.add(javaFileCallback);
      return new ProcessorTestImpl(updated,
                                   javaSourceFileCallbacks,
                                   javaClassFileCallbacks,
                                   otherFileCallbacks,
                                   processors,
                                   toCompile,
                                   options,
                                   compiledClassNames);
   }

   @Override
   public ProcessorTest withJavaSourceFileCallback(JavaSourceFileCallback javaSourceFileCallback)
   {
      List<JavaSourceFileCallback> updated = new ArrayList<>(this.javaSourceFileCallbacks);
      updated.add(javaSourceFileCallback);
      return new ProcessorTestImpl(javaFileCallbacks,
                                   updated,
                                   javaClassFileCallbacks,
                                   otherFileCallbacks,
                                   processors,
                                   toCompile,
                                   options,
                                   compiledClassNames);
   }

   @Override
   public ProcessorTest withJavaClassFileCallback(JavaClassFileCallback javaClassFileCallback)
   {
      List<JavaClassFileCallback> updated = new ArrayList<>(this.javaClassFileCallbacks);
      updated.add(javaClassFileCallback);
      return new ProcessorTestImpl(javaFileCallbacks,
                                   javaSourceFileCallbacks,
                                   updated,
                                   otherFileCallbacks,
                                   processors,
                                   toCompile,
                                   options,
                                   compiledClassNames);
   }

   @Override
   public ProcessorTest withOtherFileCallback(OtherFileCallback otherFileCallback)
   {
      List<OtherFileCallback> updated = new ArrayList<>(this.otherFileCallbacks);
      updated.add(otherFileCallback);
      return new ProcessorTestImpl(javaFileCallbacks,
                                   javaSourceFileCallbacks,
                                   javaClassFileCallbacks,
                                   updated,
                                   processors,
                                   toCompile,
                                   options,
                                   compiledClassNames);
   }

   public void run()
   {
      compile();
   }

   private void compile()
   {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      Objects.requireNonNull(compiler);
      TestFileManager fileManager = new TestFileManager(compiler.getStandardFileManager(null, null, null), hasJavaFileCallbacks());
      JavaCompiler.CompilationTask compilerTask = compiler.getTask(null,
                                                                   fileManager,
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

      List<JavaFile> javaFileObjects = fileManager.getJavaFileObjects();
      javaFileCallbacks.forEach(javaFileCallback -> javaFileCallback.accept(unmodifiableList(javaFileObjects)));

      List<JavaClassFile> javaClassFiles = javaFileObjects
                                                      .stream()
                                                      .filter(JavaClassFile.class::isInstance)
                                                      .map(JavaClassFile.class::cast)
                                                      .toList();
      javaClassFileCallbacks.forEach(javaClassFileCallback -> javaClassFileCallback.accept(javaClassFiles));

      List<JavaSourceFile> javaSourceFiles = javaFileObjects
                                                        .stream()
                                                        .filter(JavaSourceFile.class::isInstance)
                                                        .map(JavaSourceFile.class::cast)
                                                        .toList();
      javaSourceFileCallbacks.forEach(javaSourceFileCallback -> javaSourceFileCallback.accept(javaSourceFiles));

      List<OtherFile> otherFiles = javaFileObjects
                                              .stream()
                                              .filter(OtherFile.class::isInstance)
                                              .map(OtherFile.class::cast)
                                              .toList();
      otherFileCallbacks.forEach(otherFileCallback -> otherFileCallback.accept(otherFiles));
   }

   private boolean hasJavaFileCallbacks()
   {
      return !javaFileCallbacks.isEmpty() ||
             !javaClassFileCallbacks.isEmpty() ||
             !javaSourceFileCallbacks.isEmpty() ||
             !otherFileCallbacks.isEmpty();
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