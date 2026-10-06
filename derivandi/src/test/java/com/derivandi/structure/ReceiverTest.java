package com.derivandi.structure;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReceiverTest
{
   @Test
   void getType()
   {
      processorTest().withCodeToCompile("ReceiverExample.java", """
                                                                public class ReceiverExample {
                                                                   public class Inner {
                                                                      public Inner(ReceiverExample ReceiverExample.this) {}
                                                                   }
                                                                }
                                                                """)
                     .process(context ->
                              {
                                 D.Class outer = context.getClassOrThrow("ReceiverExample");
                                 D.Class inner = context.getClassOrThrow("ReceiverExample.Inner");
                                 D.Constructor cConstructor = inner.getConstructors().get(0);
                                 D.Receiver cReceiver = cConstructor.getReceiverOrThrow();

                                 assertEquals(outer, cReceiver.getType());
                              });
   }

   @Test
   void getAnnotationUsages()
   {
      processorTest().withCodeToCompile("MyAnnotation.java",
                                        "@java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE_USE) @interface MyAnnotation{int value();}")
                     .withCodeToCompile("ReceiverExample.java", """
                                                                public class ReceiverExample {
                                                                   public class Inner {
                                                                      public Inner(@MyAnnotation(2) ReceiverExample ReceiverExample.this) {}
                                                                   }
                                                                }
                                                                """)
                     .process(context ->
                              {
                                 D.Class outer = context.getClassOrThrow("ReceiverExample");
                                 D.Class inner = context.getClassOrThrow("ReceiverExample.Inner");
                                 D.Constructor cConstructor = inner.getConstructors().get(0);
                                 D.Receiver cReceiver = cConstructor.getReceiverOrThrow();

                                 assertEquals(outer, cReceiver.getType());

                                 List<D.AnnotationUsage> usages = cReceiver.getAnnotationUsages();
                                 assertEquals(1, usages.size());
                                 D.AnnotationValue annotationValue = usages.get(0).getValueOrThrow("value");
                                 assertEquals(2, annotationValue.getValue());
                              });
   }
}