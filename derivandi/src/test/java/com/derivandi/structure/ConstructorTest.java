package com.derivandi.structure;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

class ConstructorTest
{
   @Test
   void getEmptyParameters()
   {
      processorTest().withCodeToCompile("DefaultConstructorExample.java", "public class DefaultConstructorExample{}")
                     .process(context ->
                              {
                                 D.Class example = context.getClassOrThrow("DefaultConstructorExample");
                                 D.Constructor constructor = example.getConstructors().get(0);

                                 assertEquals(0, constructor.getParameters().size());
                              });
   }

   @Test
   void getParameters()
   {
      processorTest().withCodeToCompile("ConstructorExample.java", "public class ConstructorExample {public ConstructorExample(Long id) {}}")
                     .process(context ->
                              {
                                 D.Class example = context.getClassOrThrow("ConstructorExample");
                                 D.Class aLong = context.getClassOrThrow("java.lang.Long");
                                 D.Constructor constructor = example.getConstructors().get(0);
                                 List<D.Parameter> parameters = constructor.getParameters();

                                 assertEquals(1, parameters.size());
                                 assertEquals(aLong, parameters.get(0).getType());
                              });
   }

   @Test
   void getParameterTypes()
   {
      processorTest().withCodeToCompile("ConstructorExample.java", "public class ConstructorExample {public ConstructorExample(Long id) {}}")
                     .process(context ->
                              {
                                 D.Class aLong = context.getClassOrThrow("java.lang.Long");
                                 D.Class example = context.getClassOrThrow("ConstructorExample");
                                 D.Constructor constructor = example.getConstructors().get(0);

                                 assertEquals(List.of(aLong), constructor.getParameterTypes());
                              });
   }

   @Test
   void getThrows()
   {
      processorTest().withCodeToCompile("ConstructorExample.java", """
                                                                   import java.io.IOException;
                                                                   
                                                                   public class ConstructorExample {
                                                                      public ConstructorExample(Long id) {}
                                                                      public ConstructorExample(String name) throws IOException {}
                                                                   }
                                                                   """)
                     .process(context ->
                              {
                                 D.Class ioException = context.getClassOrThrow("java.io.IOException");
                                 D.Class example = context.getClassOrThrow("ConstructorExample");
                                 List<D.Constructor> constructors = example.getConstructors();

                                 assertEquals(emptyList(), constructors.get(0).getThrows());
                                 assertEquals(List.of(ioException), constructors.get(1).getThrows());
                              });
   }

   @Test
   void isVarArgs()
   {
      processorTest().withCodeToCompile("ConstructorExample.java", """
                                                                   import java.io.IOException;
                                                                   
                                                                   public class ConstructorExample {
                                                                      public ConstructorExample(Long id) {}
                                                                      public ConstructorExample(String... names) {}
                                                                   }
                                                                   """)
                     .process(context ->
                              {
                                 D.Class example = context.getClassOrThrow("ConstructorExample");
                                 List<D.Constructor> constructors = example.getConstructors();
                                 assertFalse(constructors.get(0).isVarArgs());
                                 assertTrue(constructors.get(1).isVarArgs());
                              });
   }

   @Test
   void getSurrounding()
   {
      processorTest().withCodeToCompile("ConstructorExample.java", """
                                                                   import java.io.IOException;
                                                                   
                                                                   public class ConstructorExample {
                                                                      public ConstructorExample(Long id) {}
                                                                      public ConstructorExample(String name) throws IOException {}
                                                                      public ConstructorExample(String... names) {}
                                                                   }
                                                                   """)
                     .process(context ->
                              {
                                 D.Class example = context.getClassOrThrow("ConstructorExample");
                                 D.Constructor constructor = example.getConstructors().get(0);
                                 assertEquals(example, constructor.getSurrounding());
                              });
   }

   @Test
   void getReceiverType()
   {
      processorTest().withCodeToCompile("DefaultConstructorExample.java", "public class DefaultConstructorExample{}")
                     .withCodeToCompile("ReceiverExample.java", """
                                                                public class ReceiverExample {
                                                                   public class Inner {
                                                                      public Inner(ReceiverExample ReceiverExample.this) {}
                                                                   }
                                                                }
                                                                """)
                     .process(context ->
                              {
                                 D.Class defaultConstructorExample = context.getClassOrThrow("DefaultConstructorExample");
                                 D.Constructor defaultConstructor = defaultConstructorExample.getConstructors().get(0);
                                 assertTrue(defaultConstructor.getReceiverType().isEmpty());

                                 D.Class example = context.getClassOrThrow("ReceiverExample");
                                 D.Class inner = context.getClassOrThrow("ReceiverExample.Inner");
                                 D.Constructor constructor = inner.getConstructors().get(0);
                                 assertEquals(example, constructor.getReceiverType().orElseThrow());
                              });
   }
}
