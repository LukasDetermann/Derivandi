package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WildcardTest
{
   @Test
   void testGetExtends()
   {
      processorTest().withCodeToCompile("BoundsExample.java", """
                                                              public class BoundsExample {
                                                                 public static void extendsExample(java.util.List<? extends Number> numbers) {}
                                                              }
                                                              """)
                     .process(context ->
                              {
                                 D.Class boundsExample = context.getClassOrThrow("BoundsExample");
                                 D.Method extendsExample = boundsExample.getMethods("extendsExample").get(0);
                                 D.Parameter parameter = extendsExample.getParameterOrThrow("numbers");
                                 D.Interface parameterType = ((D.Interface) parameter.getType());
                                 D.Wildcard wildcard = (D.Wildcard) parameterType.getGenericUsages().get(0);

                                 assertEquals(context.getClassOrThrow("java.lang.Number"), wildcard.getExtends().orElseThrow());
                              });
   }

   @Test
   void testGetSupper()
   {
      processorTest().withCodeToCompile("BoundsExample.java", """
                                                              public class BoundsExample {
                                                                 public static void superExample(java.util.List<? super Number> numbers) {}
                                                              }
                                                              """)
                     .process(context ->
                              {
                                 D.Class boundsExample = context.getClassOrThrow("BoundsExample");
                                 D.Method extendsExample = boundsExample.getMethods("superExample").get(0);
                                 D.Parameter parameter = extendsExample.getParameterOrThrow("numbers");
                                 D.Interface parameterType = ((D.Interface) parameter.getType());
                                 D.Wildcard wildcard = (D.Wildcard) parameterType.getGenericUsages().get(0);

                                 assertEquals(context.getClassOrThrow("java.lang.Number"), wildcard.getSuper().orElseThrow());
                              });
   }
}
