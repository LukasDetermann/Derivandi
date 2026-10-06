package com.derivandi.structure;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class VariableTest
{
   @Test
   void isSubtypeOf()
   {
      processorTest().withCodeToCompile("ParameterExample.java", """
                                                                 public class ParameterExample
                                                                 {
                                                                    public ParameterExample(String name) {}
                                                                 
                                                                    public void foo(Long foo) { }
                                                                 }
                                                                 """)
                     .process(context ->
                              {
                                 D.Class string = context.getClassOrThrow("java.lang.String");
                                 D.Class example = context.getClassOrThrow("ParameterExample");
                                 D.Constructor constructor = example.getConstructors().get(0);
                                 D.Parameter parameter = constructor.getParameters().get(0);
                                 assertTrue(parameter.isSubtypeOf(string));
                              });
   }

   @Test
   void isAssignableFrom()
   {
      processorTest().withCodeToCompile("FieldExample.java", "public class FieldExample{public static final int ID = 2;}")
                     .process(context ->
                              {
                                 D.Class integer = context.getClassOrThrow("java.lang.Integer");
                                 D.Class example = context.getClassOrThrow("FieldExample");
                                 D.Field field = example.getFields().get(0);
                                 assertTrue(field.isAssignableFrom(integer));
                              });
   }
}
