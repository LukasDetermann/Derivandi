package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.*;

class AnnotationableTest
{
   @Test
   void emptyAnnotations()
   {
      processorTest().withCodeToCompile("NotAnnotated.java", "class NotAnnotated{}")
                     .process(context ->
                              {
                                 D.Declared cDeclared = context.getDeclaredOrThrow("NotAnnotated");
                                 assertTrue(cDeclared.getAnnotationUsages().isEmpty());
                                 assertTrue(cDeclared.getDirectAnnotationUsages().isEmpty());
                              });
   }

   @Test
   void inheritance()
   {
      processorTest().withCodeToCompile("InheritedAnnotation.java", "@java.lang.annotation.Inherited @interface InheritedAnnotation{}")
                     .withCodeToCompile("StandaloneAnnotation.java", "@interface StandaloneAnnotation{}")
                     .withCodeToCompile("Parent.java", "@InheritedAnnotation @StandaloneAnnotation class Parent{}")
                     .withCodeToCompile("Child.java", "class Child extends Parent{}")
                     .process(context ->
                              {
                                 D.Declared cDeclared = context.getDeclaredOrThrow("Child");
                                 D.Annotation standaloneAnnotation = context.getAnnotationOrThrow("StandaloneAnnotation");
                                 D.Annotation inheritedAnnotation = context.getAnnotationOrThrow("InheritedAnnotation");

                                 //direct
                                 assertTrue(cDeclared.getDirectAnnotationUsages().isEmpty());
                                 assertFalse(cDeclared.isDirectlyAnnotatedWith(standaloneAnnotation));
                                 assertFalse(cDeclared.isDirectlyAnnotatedWith(inheritedAnnotation));

                                 assertTrue(cDeclared.getDirectUsageOf(standaloneAnnotation).isEmpty());
                                 assertTrue(cDeclared.getDirectUsagesOf(inheritedAnnotation).isEmpty());

                                 //inherited
                                 List<D.AnnotationUsage> usages = cDeclared.getAnnotationUsages();
                                 assertEquals(1, usages.size());
                                 D.Annotation cAnnotation = usages.get(0).getAnnotation();
                                 assertEquals("InheritedAnnotation", cAnnotation.getQualifiedName());
                                 assertFalse(cDeclared.isAnnotatedWith(standaloneAnnotation));
                                 assertTrue(cDeclared.isAnnotatedWith(inheritedAnnotation));

                                 assertTrue(cDeclared.getUsageOf(standaloneAnnotation).isEmpty());
                                 assertTrue(cDeclared.getUsageOf(inheritedAnnotation).isPresent());
                              });
   }
}
