package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.derivandi.api.NestingKind.INNER;
import static com.derivandi.api.NestingKind.OUTER;
import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DeclaredTest
{
   @Test
   void getFormalGenerics()
   {
      processorTest().process(context ->
                              {
                                 D.Interface comparable = context.getInterfaceOrThrow("java.lang.Comparable");
                                 D.Generic generics = comparable.getGenericDeclarations().get(0);
                                 assertEquals("T", generics.getName());
                              });
   }

   @Test
   void getNesting()
   {
      processorTest().withCodeToCompile("NestingExample.java", "public class NestingExample {private class Inner{}}")
                     .process(context ->
                              {
                                 D.Class outer = context.getClassOrThrow("NestingExample");
                                 assertEquals(OUTER, outer.getNesting());

                                 D.Class inner = context.getClassOrThrow("NestingExample.Inner");
                                 assertEquals(INNER, inner.getNesting());
                              });
   }

   @Test
   void getFieldOrThrow()
   {
      processorTest().process(context ->
                              {
                                 D.Class math = context.getClassOrThrow("java.lang.Math");
                                 D.Field e = math.getFieldOrThrow("E");
                                 assertEquals(2.7182818284590452354D, e.getConstantValueOrThrow());
                              });
   }

   @Test
   void getFields()
   {
      processorTest().withCodeToCompile("MyClass.java", "class MyClass{int a,b; private static final long C = 5;}")
                     .process(context ->
                              {
                                 D.Class myClass = context.getClassOrThrow("MyClass");
                                 List<String> fields = myClass.getFields()
                                                              .stream()
                                                              .map(D.Nameable::getName)
                                                              .toList();
                                 assertEquals(Arrays.asList("a", "b", "C"), fields);
                              });
   }

   @Test
   void getMethods()
   {
      processorTest().withCodeToCompile("MyClass.java",
                                        "class MyClass{int fooMethod(){return 0;}@Override public String toString() {return \"\";}}")
                     .process(context ->
                              {
                                 D.Class myClass = context.getClassOrThrow("MyClass");
                                 String methods = myClass.getMethods()
                                                         .stream()
                                                         .map(D.Nameable::getName)
                                                         .collect(Collectors.joining(", "));
                                 assertEquals("fooMethod, toString", methods);
                              });
   }

   @Test
   void getConstructors()
   {
      processorTest().withCodeToCompile("Test.java", "class Test{}")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Test");
                                 assertEquals(1, cClass.getConstructors().size());
                              });
   }

   @Test
   void getPackage()
   {
      processorTest().process(context ->
                              {
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 D.Package cPackage = object.getPackage();
                                 assertEquals("java.lang", cPackage.getQualifiedName());
                              });
   }

   @Test
   void getBinaryName()
   {
      processorTest().process(context ->
                              {
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 assertEquals("java.lang.Object", object.getBinaryName());

                                 D.Class randomNumberGeneratorHolder = context.getClassOrThrow("java.lang.Math.RandomNumberGeneratorHolder");
                                 assertEquals("java.lang.Math$RandomNumberGeneratorHolder", randomNumberGeneratorHolder.getBinaryName());
                              });
   }
}
