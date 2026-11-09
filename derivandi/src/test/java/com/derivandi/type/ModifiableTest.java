package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.derivandi.api.Modifier.*;
import static com.derivandi.api.test.ProcessorTest.processorTest;

class ModifiableTest
{
   @Test
   void _public()
   {
      processorTest().withCodeToCompile("Test.java", """
                                                     class Test{
                                                        public Test(){};
                                                     }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Test");
                                 D.Constructor constructor = cClass.getConstructors().get(0);
                                 Assertions.assertTrue(constructor.hasModifier(PUBLIC));
                              })
                     .run();
   }

   @Test
   void _protected()
   {
      processorTest().withCodeToCompile("Test.java", """
                                                     class Test{
                                                        protected Test(){};
                                                     }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Test");
                                 D.Constructor constructor = cClass.getConstructors().get(0);
                                 Assertions.assertTrue(constructor.hasModifier(PROTECTED));
                              })
                     .run();
   }

   @Test
   void _private()
   {
      processorTest().withCodeToCompile("Test.java", """
                                                     class Test{
                                                        private Test(){};
                                                     }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Test");
                                 D.Constructor constructor = cClass.getConstructors().get(0);
                                 Assertions.assertTrue(constructor.hasModifier(PRIVATE));
                              })
                     .run();
   }

   @Test
   void _abstract()
   {
      processorTest().withCodeToCompile("Test.java", "abstract class Test{}")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Test");
                                 Assertions.assertTrue(cClass.hasModifier(ABSTRACT));
                              })
                     .run();
   }

   @Test
   void packagePrivate()
   {
      processorTest().withCodeToCompile("Test.java", "interface Test{}")
                     .process(context ->
                              {
                                 D.Interface cInterface = context.getInterfaceOrThrow("Test");
                                 Assertions.assertTrue(cInterface.hasModifier(PACKAGE_PRIVATE));
                              })
                     .run();
   }

   @Test
   void _default()
   {
      processorTest().withCodeToCompile("Test.java", """
                                                     interface Test{
                                                        default void test(){};
                                                     }""")
                     .process(context ->
                              {
                                 D.Interface cInterface = context.getInterfaceOrThrow("Test");
                                 D.Method method = cInterface.getMethods("test").get(0);
                                 Assertions.assertTrue(method.hasModifier(DEFAULT));
                              })
                     .run();
   }

   @Test
   void _static()
   {
      processorTest().withCodeToCompile("Test.java", """
                                                     class Test{
                                                        static void test(){};
                                                     }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Test");
                                 D.Method method = cClass.getMethods("test").get(0);
                                 Assertions.assertTrue(method.hasModifier(STATIC));
                              })
                     .run();
   }

   @Test
   void sealed()
   {
      processorTest().withCodeToCompile("Parent.java", "sealed interface Parent permits Child {}")
                     .withCodeToCompile("Child.java", "non-sealed interface Child extends Parent {}")
                     .process(context ->
                              {
                                 D.Interface cInterface = context.getInterfaceOrThrow("Parent");
                                 Assertions.assertTrue(cInterface.hasModifier(SEALED));
                              })
                     .run();
   }

   @Test
   void nonSealed()
   {
      processorTest().withCodeToCompile("Parent.java", "sealed interface Parent permits Child {}")
                     .withCodeToCompile("Child.java", "non-sealed interface Child extends Parent {}")
                     .process(context ->
                              {
                                 D.Interface cInterface = context.getInterfaceOrThrow("Child");
                                 Assertions.assertTrue(cInterface.hasModifier(NON_SEALED));
                              })
                     .run();
   }

   @Test
   void _final()
   {
      processorTest().withCodeToCompile("Test.java", """
                                                     class Test{
                                                        final void test(){};
                                                     }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Test");
                                 D.Method method = cClass.getMethods("test").get(0);
                                 Assertions.assertTrue(method.hasModifier(FINAL));
                              })
                     .run();
   }

   @Test
   void _transient()
   {
      processorTest().withCodeToCompile("Test.java", """
                                                     class Test{
                                                        transient int foo;
                                                     }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Test");
                                 D.Field field = cClass.getFieldOrThrow("foo");
                                 Assertions.assertTrue(field.hasModifier(TRANSIENT));
                              })
                     .run();
   }

   @Test
   void _volatile()
   {
      processorTest().withCodeToCompile("Test.java", """
                                                     class Test{
                                                        volatile int foo;
                                                     }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Test");
                                 D.Field field = cClass.getFieldOrThrow("foo");
                                 Assertions.assertTrue(field.hasModifier(VOLATILE));
                              })
                     .run();
   }

   @Test
   void _synchronized()
   {
      processorTest().withCodeToCompile("Test.java", """
                                                     class Test{
                                                        synchronized void test(){};
                                                     }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Test");
                                 D.Method method = cClass.getMethods("test").get(0);
                                 Assertions.assertTrue(method.hasModifier(SYNCHRONIZED));
                              })
                     .run();
   }

   @Test
   void _native()
   {
      processorTest().withCodeToCompile("Test.java", """
                                                     class Test{
                                                        native void test();
                                                     }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Test");
                                 D.Method method = cClass.getMethods("test").get(0);
                                 Assertions.assertTrue(method.hasModifier(NATIVE));
                              })
                     .run();
   }

   @Test
   void _strictfp()
   {
      processorTest().withCodeToCompile("Test.java", """
                                                     class Test{
                                                        strictfp void test(){};
                                                     }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Test");
                                 D.Method method = cClass.getMethods("test").get(0);
                                 Assertions.assertTrue(method.hasModifier(STRICTFP));
                              })
                     .run();
   }
}
