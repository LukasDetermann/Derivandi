package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IntersectionTest
{
   @Test
   void testGetBounds()
   {
      processorTest().withCodeToCompile("IntersectionExample.java",
                                        "public class IntersectionExample<T extends java.util.Collection & java.io.Serializable>{}")
                     .process(context ->
                              {
                                 D.Interface collection = context.getInterfaceOrThrow("java.util.Collection");
                                 D.Interface serializable = context.getInterfaceOrThrow("java.io.Serializable");
                                 List<D.Interface> expected = List.of(collection, serializable);

                                 D.Class intersectionExample = context.getClassOrThrow("IntersectionExample");
                                 D.Generic generic = intersectionExample.getGenericDeclarations().get(0);

                                 assertEquals(expected, generic.getBounds());
                              });
   }
}
