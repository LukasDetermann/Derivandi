package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.*;

class AnnotationTest
{
   @Test
   void isSubtypeOf()
   {
      processorTest().process(context ->
                              {
                                 D.Annotation overwrite = context.getAnnotationOrThrow("java.lang.Override");
                                 D.Interface annotation = context.getInterfaceOrThrow("java.lang.annotation.Annotation");
                                 D.Class number = context.getClassOrThrow("java.lang.Number");

                                 assertTrue(overwrite.isSubtypeOf(annotation));
                                 assertTrue(overwrite.isSubtypeOf(overwrite));
                                 assertFalse(overwrite.isSubtypeOf(number));
                              });
   }

   @Test
   void getDirectSuperTypes()
   {
      processorTest().withCodeToCompile("DirektSuperTypeExample.java", """
                                                                       public class DirektSuperTypeExample {
                                                                          @interface AnnotationNoParent {}
                                                                       }
                                                                       """)
                     .process(context ->
                              {
                                 List<D.Declared> expected = List.of(context.getClassOrThrow("java.lang.Object"),
                                                                     context.getInterfaceOrThrow("java.lang.annotation.Annotation"));

                                 D.Annotation annotation = context.getAnnotationOrThrow("DirektSuperTypeExample.AnnotationNoParent");
                                 assertEquals(expected, annotation.getDirectSuperTypes());
                              });
   }

   @Test
   void getSuperTypes()
   {
      processorTest().withCodeToCompile("DirektSuperTypeExample.java", """
                                                                       public class DirektSuperTypeExample {
                                                                          @interface AnnotationNoParent {}
                                                                       }
                                                                       """)
                     .process(context ->
                              {
                                 Set<D.Declared> expected = Set.of(context.getClassOrThrow("java.lang.Object"),
                                                                   context.getInterfaceOrThrow("java.lang.annotation.Annotation"));

                                 D.Annotation annotation = context.getAnnotationOrThrow("DirektSuperTypeExample.AnnotationNoParent");
                                 assertEquals(expected, annotation.getSuperTypes());
                              });
   }

   @Test
   void getSurounding()
   {
      processorTest().withCodeToCompile("Outer.java", """
                                                      public @interface Outer {
                                                            @interface Inner {}
                                                        }
                                                      """)
                     .process(context ->
                              {
                                 D.Declared inner = context.getDeclaredOrThrow("Outer.Inner");
                                 D.Declared outer = inner.getSurrounding().orElseThrow();
                                 assertEquals(context.getDeclaredOrThrow("Outer"), outer);
                              });
   }
}
