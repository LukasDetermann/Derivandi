package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericTest
{
   @Test
   void getExtends()
   {
      processorTest().withCodeToCompile("GenericsExample.java",
                                        "public class GenericsExample<T extends Number>{}")
                     .process(context ->
                              {
                                 D.Class number = context.getClassOrThrow("java.lang.Number");
                                 D.Class genericsExample = context.getClassOrThrow("GenericsExample");
                                 D.Generic generic = (D.Generic) genericsExample.getGenericUsages().get(0);
                                 assertEquals(number, generic.getBound());
                              });
   }

   @Test
   void getEnclosing()
   {
      processorTest().withCodeToCompile("GenericsExample.java",
                                        "public class GenericsExample<T extends Number>{}")
                     .process(context ->
                              {
                                 D.Class genericsExample = context.getClassOrThrow("GenericsExample");
                                 D.Generic generic = (D.Generic) genericsExample.getGenericUsages().get(0);
                                 assertEquals(genericsExample, generic.getEnclosing());
                              });
   }
}
