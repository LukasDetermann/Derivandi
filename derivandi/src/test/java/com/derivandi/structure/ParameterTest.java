package com.derivandi.structure;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.*;

class ParameterTest
{
   @Test
   void getSurrounding()
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
                                 D.Class example = context.getClassOrThrow("ParameterExample");
                                 D.Method method = example.getMethods("foo").get(0);
                                 D.Parameter foo = method.getParameterOrThrow("foo");
                                 assertEquals(method, foo.getSurrounding());

                                 D.Constructor constructor = example.getConstructors().get(0);
                                 D.Parameter name = constructor.getParameters().get(0);
                                 assertEquals(constructor, name.getSurrounding());
                              });
   }

   @Test
   void isVarArgs()
   {
      processorTest().withCodeToCompile("VarArgsExample.java",
                                        """
                                        class VarArgsExample{
                                            VarArgsExample(String s, String... s1) {}
                                        }
                                        """)
                     .process(context ->
                              {
                                 D.Class example = context.getClassOrThrow("VarArgsExample");
                                 D.Constructor constructor = example.getConstructors().get(0);
                                 List<D.Parameter> parameters = constructor.getParameters();
                                 assertFalse(parameters.get(0).isVarArgs());
                                 assertTrue(parameters.get(1).isVarArgs());
                              });
   }
}
