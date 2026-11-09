package com.derivandi.api.test;

import com.derivandi.api.processor.ProcessingCallback;
import com.derivandi.api.processor.SimpleContext;
import com.derivandi.internal.test.ProcessorTestImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.processing.Processor;
import java.io.File;
import java.nio.file.Path;

/**
 * Runs annotation processors. Generated files will not be written to disk.
 * May get moved it a separate artifact in the future
 */
@ApiStatus.Experimental
public interface ProcessorTest
{
   @Contract(value = " -> new", pure = true)
   static ProcessorTest processorTest()
   {
      return new ProcessorTestImpl();
   }

   /**
    * adds an existing file to compile
    *
    * @return a new {@link ProcessorTest}
    */
   @Contract(value = "_ -> new", pure = true)
   ProcessorTest withCodeToCompile(Path fileToCompile);

   /**
    * adds an existing file to compile
    *
    * @return a new {@link ProcessorTest}
    */
   @Contract(value = "_ -> new", pure = true)
   ProcessorTest withCodeToCompile(File fileToCompile);

   /**
    * adds a new file to compile
    *
    * @param fileName MyClass.java
    * @param code the sourceCode of the file
    *
    * @return a new {@link ProcessorTest}
    */
   @Contract(value = "_,_ -> new", pure = true)
   ProcessorTest withCodeToCompile(String fileName, String code);

   /**
    * This already compiled class will also be processed
    *
    * @param qualifiedName java.lang.Object
    *
    * @return a new {@link ProcessorTest}
    */
   @Contract(value = "_ -> new", pure = true)
   ProcessorTest withCompiledClass(String qualifiedName);

   /**
    * This already compiled class will also be processed
    *
    * @return a new {@link ProcessorTest}
    */
   @Contract(value = "_ -> new", pure = true)
   ProcessorTest withCompiledClass(Class<?> aClass);

   /**
    * Compiler options
    *
    * @return a new {@link ProcessorTest}
    */
   @Contract(value = "_ -> new", pure = true)
   ProcessorTest withOption(String option);

   @Contract(value = "_ -> new", pure = true)
   ProcessorTest process(ProcessingCallback<SimpleContext> processingCallback);

   @Contract(value = "_ -> new", pure = true)
   ProcessorTest process(Processor... processors);

   @Contract(value = "_ -> new", pure = true)
   ProcessorTest withJavaFileCallback(JavaFileCallback javaFileCallback);

   @Contract(value = "_ -> new", pure = true)
   ProcessorTest withJavaSourceFileCallback(JavaSourceFileCallback javaSourceFileCallback);

   @Contract(value = "_ -> new", pure = true)
   ProcessorTest withJavaClassFileCallback(JavaClassFileCallback javaClassFileCallback);

   @Contract(value = "_ -> new", pure = true)
   ProcessorTest withOtherFileCallback(OtherFileCallback otherFileCallback);

   @Contract(mutates = "this")
   void run();
}
