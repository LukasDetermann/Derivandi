package com.derivandi.dsl;

import com.derivandi.api.Modifier;
import com.derivandi.api.dsl.JavaDsl;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.annotation.AnnotationImportStep;
import org.junit.jupiter.api.Test;

import static com.derivandi.api.dsl.JavaDsl.annotation;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnnotationDslTest
{

   public static final AnnotationImportStep ANNOTATION = JavaDsl.annotation().package_("org.example");

   @Test
   void javadoc()
   {
      assertEquals("""
                   package org.example;
                   
                   /// some java doc
                   @interface MyInterface {
                   }""",
                   ANNOTATION.javadoc("/// some java doc")
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
                   @interface MyInterface {
                   }""",
                   ANNOTATION.annotate("MyAnnotation")
                             .annotate(JavaDsl.annotationUsage().type("MyAnnotation"))
                             .name("MyInterface")
                             .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void modifier()
   {
      assertEquals("""
                   package org.example;
                   
                   public dings abstract public protected private sealed non-sealed strictfp @interface MyInterface {
                   }""",
                   ANNOTATION.modifier(Modifier.PUBLIC)
                             .modifier("dings")
                             .abstract_()
                             .public_()
                             .protected_()
                             .private_()
                             .sealed()
                             .nonSealed()
                             .strictfp_()
                             .name("MyInterface")
                             .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void body()
   {
      assertEquals("""
                   package org.example;
                   
                   @interface MyInterface {
                      // some content
                   }""",
                   ANNOTATION.name("MyInterface")
                             .body("// some content")
                             .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void field()
   {
      assertEquals("""
                   package org.example;
                   
                   @interface MyInterface {
                      String s;
                      
                      private int i;
                   }""",
                   ANNOTATION.name("MyInterface")
                             .field("String s;")
                             .field(JavaDsl.field().private_().type("int").name("i"))
                             .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void method()
   {
      assertEquals("""
                   package org.example;
                   
                   @interface MyInterface {
                      String foo();
                      
                      String myMethod();
                   
                   }""",
                   ANNOTATION.name("MyInterface")
                             .method("String foo();")
                             .method(JavaDsl.method().result("String").name("myMethod"))
                             .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void inner()
   {
      assertEquals("""
                   package org.example;
                   
                   @interface MyInterface {
                      @interface Inner {}
                      
                      @interface Inner2 {
                      }
                   
                   }""",
                   ANNOTATION.name("MyInterface")
                             .inner("@interface Inner {}")
                             .inner(JavaDsl.innerAnnotation().outer("org.example.MyInterface").name("Inner2"))
                             .renderDeclaration(RenderingContext.createRenderingContext()));
   }


   @Test
   void copyright()
   {
      assertEquals("""
                   // some copyright
                   package org.example;
                   
                   /// some javadoc
                   @interface MyInterface {
                   }""",
                   annotation()
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
                   
                   @interface MyInterface {
                   }""",
                   annotation()
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
                   
                   @interface MyInterface {
                   }""",
                   annotation()
                      .package_("org.example")
                      .import_("some.thing")
                      .import_(JavaDsl.import_("Dings"))
                      .import_(JavaDsl.import_().package_(JavaDsl.packageInfo().name("other.package")))
                      .import_(JavaDsl.import_().static_().declared("foo.package.MyType"))
                      .import_(JavaDsl.import_().static_().declared(ANNOTATION.name("MyInterface2")))
                      .name("MyInterface")
                      .renderDeclaration(RenderingContext.createRenderingContext()));
   }

   @Test
   void renderQualifiedName()
   {
      assertEquals("org.example.MyAnnotation",
                   annotation()
                      .package_("org.example")
                      .name("MyAnnotation")
                      .renderQualifiedName(RenderingContext.createRenderingContext()));
   }

   @Test
   void renderType()
   {
      assertEquals("org.example.MyAnnotation",
                   annotation()
                      .package_("org.example")
                      .name("MyAnnotation")
                      .renderType(RenderingContext.createRenderingContext()));
   }

   @Test
   void renderName()
   {
      assertEquals("MyAnnotation",
                   annotation()
                      .package_("org.example")
                      .name("MyAnnotation")
                      .renderName(RenderingContext.createRenderingContext()));
   }

   @Test
   void renderMethod()
   {
      assertEquals("""
                   package org.example;
                   
                   @interface MyAnnotation {
                      String foo() default "";
                   
                   }""",
                   ANNOTATION.name("MyAnnotation")
                             .method(JavaDsl.method().result("String").name("foo"),
                                     JavaDsl.annotationValue(""))
                             .renderDeclaration(RenderingContext.createRenderingContext()));
   }
}
