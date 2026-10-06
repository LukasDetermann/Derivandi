package com.derivandi.structure;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultTest
{
   @Test
   void getType()
   {
      processorTest().withCodeToCompile("ReturnExample.java", """
                                                              abstract class ReturnExample {
                                                                 public abstract Integer method();
                                                              }
                                                              """)
                     .process(context ->
                              {
                                 D.Class integer = context.getClassOrThrow("java.lang.Integer");
                                 D.Class returnExample = context.getClassOrThrow("ReturnExample");
                                 D.Method method = returnExample.getMethods().get(0);
                                 D.Result cReturn = method.getResult();

                                 assertEquals(integer, cReturn.getType());
                              });
   }

   @Test
   void getAnnotationUsages()
   {
      processorTest().withCodeToCompile("MyAnnotation.java",
                                        "@java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE_USE) @interface MyAnnotation{int value();}")
                     .withCodeToCompile("ReturnExample.java", """
                                                              abstract class ReturnExample {
                                                                 public abstract @MyAnnotation(2) Integer method();
                                                              }
                                                              """)
                     .process(context ->
                              {
                                 D.Class returnExample = context.getClassOrThrow("ReturnExample");
                                 D.Method method = returnExample.getMethods().get(0);
                                 D.Result cReturn = method.getResult();

                                 List<D.AnnotationUsage> usages = cReturn.getAnnotationUsages();
                                 assertEquals(1, usages.size());
                                 D.AnnotationValue annotationValue = usages.get(0).getValueOrThrow("value");
                                 assertEquals(2, annotationValue.getValue());
                              });
   }
}