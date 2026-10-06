package com.derivandi.structure;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.*;

class MethodTest
{
   @Test
   void isSubSignatureOf()
   {
      processorTest().withCodeToCompile("SubSignature.java", """
                                                             import java.util.List;
                                                             
                                                             public abstract class SubSignature {
                                                                public abstract void first();
                                                                public abstract void second();
                                                                public abstract void third(String name, Long id);
                                                                public abstract void four(Long id, String name);
                                                                public abstract void five(Long id, String name2);
                                                                public abstract void six(List list);
                                                                public abstract void seven(List<String> strings);
                                                             }
                                                             """)
                     .process(context ->
                              {
                                 D.Class example = context.getClassOrThrow("SubSignature");
                                 List<D.Method> methods = example.getMethods();
                                 D.Method first = methods.get(0);
                                 D.Method second = methods.get(1);
                                 D.Method third = methods.get(2);
                                 D.Method four = methods.get(3);
                                 D.Method five = methods.get(4);
                                 D.Method six = methods.get(5);
                                 D.Method seven = methods.get(6);

                                 assertTrue(first.sameParameterTypes(second));
                                 assertTrue(second.sameParameterTypes(first));
                                 assertFalse(third.sameParameterTypes(four));
                                 assertFalse(four.sameParameterTypes(third));
                                 assertTrue(four.sameParameterTypes(five));
                                 assertTrue(six.sameParameterTypes(seven));
                                 assertFalse(seven.sameParameterTypes(six));
                              });
   }

   @Test
   void overrides()
   {
      processorTest().withCodeToCompile("MethodExample.java", """
                                                              public class MethodExample {
                                                                 @Override
                                                                 public String toString()
                                                                 {
                                                                    return "MethodExample{}";
                                                                 }
                                                              }
                                                              """)
                     .process(context ->
                              {
                                 D.Class example = context.getClassOrThrow("MethodExample");
                                 D.Class superClass = example.getSuperClass();
                                 D.Method method = example.getMethods("toString").get(0);
                                 D.Method superMethod = superClass.getMethods("toString").get(0);
                                 D.Method otherMethod = superClass.getMethods("clone").get(0);

                                 assertTrue(method.overrides(superMethod));
                                 assertFalse(method.overrides(otherMethod));
                              });
   }

   @Test
   void getParameters()
   {
      processorTest().withCodeToCompile("MethodExample.java", "public class MethodExample {private void varArgsMethod(String... args) {}}")
                     .process(context ->
                              {
                                 D.Class example = context.getClassOrThrow("MethodExample");
                                 D.Method method = example.getMethods("varArgsMethod").get(0);
                                 String parameters = method.getParameters()
                                                           .stream()
                                                           .map(D.Nameable::getName)
                                                           .collect(joining());

                                 assertEquals("args", parameters);
                              });
   }

   @Test
   void getParameterTypes()
   {
      processorTest().withCodeToCompile("MethodExample.java", "public class MethodExample {private void varArgsMethod(String... args) {}}")
                     .process(context ->
                              {
                                 D.Class string = context.getClassOrThrow("java.lang.String");
                                 D.Array stringArray = string.asArray();
                                 D.Class example = context.getClassOrThrow("MethodExample");
                                 D.Method method = example.getMethods("varArgsMethod").get(0);
                                 assertEquals(List.of(stringArray), method.getParameterTypes());
                              });
   }

   @Test
   void getResultType()
   {
      processorTest().process(context ->
                              {
                                 D.Class string = context.getClassOrThrow("java.lang.String");
                                 D.Class aLong = context.getClassOrThrow("java.lang.Long");
                                 D.Method toString = string.getMethods("toString").get(0);
                                 D.Type returnType = toString.getResultType();
                                 assertEquals(string, returnType);
                                 assertNotEquals(aLong, returnType);
                              });
   }

   @Test
   void getThrows()
   {
      processorTest().process(context ->
                              {
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 D.Class interruptedException = context.getClassOrThrow("java.lang.InterruptedException");
                                 D.Method toString = object.getMethods("toString").get(0);
                                 D.Method wait = object.getMethods("wait").get(0);

                                 assertEquals(emptyList(), toString.getThrows());
                                 assertEquals(List.of(interruptedException), wait.getThrows());
                              });
   }

   @Test
   void isNotVarArgs()
   {
      processorTest().process(context ->
                              {
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 D.Method toString = object.getMethods("toString").get(0);
                                 assertFalse(toString.isVarArgs());
                              });
   }

   @Test
   void isVarArgs()
   {
      processorTest().withCodeToCompile("MethodExample.java", "public class MethodExample {private void varArgsMethod(String... args) {}}")
                     .process(context ->
                              {
                                 D.Class object = context.getClassOrThrow("MethodExample");
                                 D.Method method = object.getMethods("varArgsMethod").get(0);
                                 assertTrue(method.isVarArgs());
                              });
   }

   @Test
   void getSurrounding()
   {
      processorTest().withCodeToCompile("MethodExample.java", """
                                                              public class MethodExample {
                                                                 @Override
                                                                 public String toString()
                                                                 {
                                                                    return "MethodExample{}";
                                                                 }
                                                              }
                                                              """)
                     .process(context ->
                              {
                                 D.Class example = context.getClassOrThrow("MethodExample");
                                 D.Method toString = example.getMethods("toString").get(0);
                                 assertEquals(example, toString.getSurrounding());
                              });
   }

   @Test
   void getReceiverType()
   {
      processorTest().withCodeToCompile("MethodExample.java", """
                                                              public class MethodExample {
                                                                 private void varArgsMethod(String... args) {}
                                                              
                                                                 private void receiver(MethodExample MethodExample.this) {}
                                                              
                                                                 @Override
                                                                 public String toString()
                                                                 {
                                                                    return "MethodExample{}";
                                                                 }
                                                              }
                                                              """)
                     .process(context ->
                              {
                                 D.Class example = context.getClassOrThrow("MethodExample");
                                 D.Method toString = example.getMethods("toString").get(0);
                                 assertTrue(toString.getReceiverType().isEmpty());

                                 D.Method method = example.getMethods("receiver").get(0);
                                 assertEquals(example, method.getReceiverType().orElseThrow());
                              });
   }
}
