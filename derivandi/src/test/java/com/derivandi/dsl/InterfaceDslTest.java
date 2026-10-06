package com.derivandi.dsl;

import com.derivandi.api.Modifier;
import com.derivandi.api.dsl.JavaDsl;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.interface_.InterfaceImportStep;
import org.junit.jupiter.api.Test;

import static com.derivandi.api.dsl.JavaDsl.interface_;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InterfaceDslTest
{

   public static final InterfaceImportStep INTERFACE = JavaDsl.interface_().package_("org.example");

   @Test
   void javadoc()
   {
      assertEquals("""
                   package org.example;
                   
                   /// some java doc
                   interface MyInterface {
                   }""",
                   INTERFACE.javadoc("/// some java doc")
                            .name("MyInterface")
                            .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void annotate()
   {
      assertEquals("""
                   package org.example;
                   
                   @MyAnnotation
                   @MyAnnotation
                   interface MyInterface {
                   }""",
                   INTERFACE.annotate("MyAnnotation")
                            .annotate(JavaDsl.annotationUsage().type("MyAnnotation"))
                            .name("MyInterface")
                            .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void modifier()
   {
      assertEquals("""
                   package org.example;
                   
                   public dings abstract public protected private sealed non-sealed static strictfp interface MyInterface {
                   }""",
                   INTERFACE.modifier(Modifier.PUBLIC)
                            .modifier("dings")
                            .abstract_()
                            .public_()
                            .protected_()
                            .private_()
                            .sealed()
                            .nonSealed()
                            .static_()
                            .strictfp_()
                            .name("MyInterface")
                            .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void generic()
   {
      assertEquals("""
                   package org.example;
                   
                   interface MyInterface <T, V> {
                   }""",
                   INTERFACE.name("MyInterface")
                            .genericDeclaration("T")
                            .genericDeclaration(JavaDsl.generic("V"))
                            .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void extends_()
   {
      assertEquals("""
                   package org.example;
                   
                   interface MyInterface extends SomeInterface, org.example.Another {
                   }""",
                   INTERFACE.name("MyInterface")
                            .extends_("SomeInterface")
                            .extends_(JavaDsl.interface_().package_("org.example").name("Another"))
                            .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void permits()
   {
      assertEquals("""
                   package org.example;
                   
                   interface MyInterface permits Some, org.example.Thing {
                   }""",
                   INTERFACE.name("MyInterface")
                            .permits("Some")
                            .permits(JavaDsl.interface_().package_("org.example").name("Thing"))
                            .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void body()
   {
      assertEquals("""
                   package org.example;
                   
                   interface MyInterface {
                      // some content
                   }""",
                   INTERFACE.name("MyInterface")
                            .body("// some content")
                            .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void field()
   {
      assertEquals("""
                   package org.example;
                   
                   interface MyInterface {
                      String s;
                      
                      private int i;
                   }""",
                   INTERFACE.name("MyInterface")
                            .field("String s;")
                            .field(JavaDsl.field().private_().type("int").name("i"))
                            .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void method()
   {
      assertEquals("""
                   package org.example;
                   
                   interface MyInterface {
                      abstract void foo() {}
                      
                      String myMethod() {}
                   
                   }""",
                   INTERFACE.name("MyInterface")
                            .method("abstract void foo() {}")
                            .method(JavaDsl.method().result("String").name("myMethod"))
                            .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void inner()
   {
      assertEquals("""
                   package org.example;
                   
                   interface MyInterface {
                      interface Inner {}
                      
                      interface Inner2 {
                      }
                   
                   }""",
                   INTERFACE.name("MyInterface")
                            .inner("interface Inner {}")
                            .inner(JavaDsl.innerInterface().outer("org.example.MyInterface").name("Inner2"))
                            .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void copyright()
   {
      assertEquals("""
                   // some copyright
                   package org.example;
                   
                   /// some javadoc
                   interface MyInterface {
                   }""",
                   interface_()
                      .copyright("// some copyright")
                      .package_(JavaDsl.packageInfo().name("org.example"))
                         .javadoc("/// some javadoc")
                      .name("MyInterface")
                      .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void package_()
   {
      assertEquals("""
                   package org.example;
                   
                   interface MyInterface {
                   }""",
                   interface_()
                      .package_("org.example")
                      .name("MyInterface")
                      .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void import_()
   {
      assertEquals("""
                   package org.example;
                   
                   import some.thing;
                   import Dings;
                   import other.package.*;
                   import static foo.package.MyType.*;
                   import static org.example.MyInterface2.*;
                   
                   interface MyInterface {
                   }""",
                   interface_()
                      .package_("org.example")
                      .import_("some.thing")
                      .import_(JavaDsl.import_("Dings"))
                      .import_(JavaDsl.import_().package_(JavaDsl.packageInfo().name("other.package")))
                      .import_(JavaDsl.import_().static_().declared("foo.package.MyType"))
                      .import_(JavaDsl.import_().static_().declared(JavaDsl.annotation().package_("org.example").name("MyInterface2")))
                      .name("MyInterface")
                      .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void renderName()
   {
      assertEquals("MyInterface", INTERFACE.name("MyInterface").renderName(RenderingContext.createRenderingContext()));
   }

   @Test
   void renderType()
   {
      assertEquals("MyInterface<T, S>",
                   INTERFACE.name("MyInterface")
                            .genericUsage("T")
                            .genericUsage("S")
                            .renderType(RenderingContext.createRenderingContext()));
   }

   @Test
   void renderQualifiedName()
   {
      assertEquals("org.example.MyInterface",
                   interface_()
                         .package_("org.example")
                         .name("MyInterface")
                         .renderQualifiedName(RenderingContext.createRenderingContext()));
   }
}
