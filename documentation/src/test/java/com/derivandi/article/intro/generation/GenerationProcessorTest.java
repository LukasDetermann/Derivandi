package com.derivandi.article.intro.generation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.derivandi.api.test.ProcessorTest.processorTest;

public class GenerationProcessorTest
{
   private static final Path DIR = Paths.get("src",
                                             "test",
                                             "java",
                                             "com",
                                             "derivandi",
                                             "article",
                                             "intro",
                                             "generation");

   @Test
   void test()
   {
      Assertions.assertDoesNotThrow(() -> processorTest().withCodeToCompile(DIR.resolve("Proxy.java"))
                                                         .withCodeToCompile(DIR.resolve("MapProxy.java"))
                                                         .withCodeToCompile(DIR.resolve("Delegate.java"))
                                                         .process(new GenerationProcessor()));
   }
}