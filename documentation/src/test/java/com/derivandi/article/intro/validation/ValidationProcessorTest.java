package com.derivandi.article.intro.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.derivandi.api.test.ProcessorTest.processorTest;

class ValidationProcessorTest
{
   private static final Path DIR = Paths.get("src",
                                             "test",
                                             "java",
                                             "com",
                                             "derivandi",
                                             "article",
                                             "intro",
                                             "validation");

   @Test
   void test()
   {
      Assertions.assertDoesNotThrow(() -> processorTest().withCodeToCompile(DIR.resolve("MyInterface.java"))
                                                         .withCodeToCompile(DIR.resolve("StaticInterface.java"))
                                                         .withCodeToCompile(DIR.resolve("MyC1ass.java"))
                                                         .process(new ValidationProcessor()));
   }
}
