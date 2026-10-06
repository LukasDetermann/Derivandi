package com.derivandi.builder;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BuilderTest
{
   private static final Path DIR = Paths.get("src",
                                             "test",
                                             "java",
                                             "com",
                                             "derivandi",
                                             "builder");

   @Test
   void derivandi()
   {
      assertDoesNotThrow(() -> processorTest().withCodeToCompile(DIR.resolve("BuilderPattern.java"))
                                              .withCodeToCompile(DIR.resolve("Customer.java"))
                                              .process(new DerivandiBuilderProcessor()));
   }

   @Test
   void jdk()
   {
      assertDoesNotThrow(() -> processorTest().withCodeToCompile(DIR.resolve("BuilderPattern.java"))
                                              .withCodeToCompile(DIR.resolve("Customer.java"))
                                              .process(new JdkBuilderProcessor()));
   }
}