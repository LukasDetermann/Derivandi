package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.*;

class ClassTest
{
   @Test
   void withGenerics()
   {
      processorTest().withCodeToCompile("InterpolateGenericsExample.java", """
                                                                           public class InterpolateGenericsExample <A extends java.lang.Comparable<B>, B extends java.lang.Comparable<A>> {
                                                                                static class IndependentGeneric<C> {}
                                                                                static class DependentGeneric<D extends E, E> {}
                                                                             }
                                                                           """)
                     .withCodeToCompile("WithGenericsExample.java", """
                                                                    public class WithGenericsExample {
                                                                       class Inner<T> {}
                                                                    }
                                                                    """)
                     .process(context ->
                              {
                                 assertThrows(IllegalArgumentException.class,
                                              () -> context.getClassOrThrow("InterpolateGenericsExample.DependentGeneric")
                                                           .withGenerics("java.lang.String"));

                                 assertThrows(IllegalArgumentException.class,
                                              () -> context.getClassOrThrow("java.lang.String").withGenerics("java.lang.String"));

                                 assertEquals(context.getClassOrThrow("java.lang.String"),
                                              context.getClassOrThrow("WithGenericsExample.Inner").withGenerics("java.lang.String")
                                                     .getGenericUsages()
                                                     .get(0));

                                 assertEquals(List.of(context.getClassOrThrow("java.lang.String")),
                                              context.getClassOrThrow("InterpolateGenericsExample.IndependentGeneric")
                                                     .withGenerics("java.lang.String")
                                                     .getGenericUsages());

                                 assertEquals(List.of(context.getClassOrThrow("java.lang.String"),
                                                      context.getClassOrThrow("java.lang.Number")),
                                              context.getClassOrThrow("InterpolateGenericsExample.DependentGeneric")
                                                     .withGenerics("java.lang.String",
                                                                   "java.lang.Number")
                                                     .getGenericUsages());
                              });
   }

   @Test
   void capture()
   {
      processorTest().withCodeToCompile("InterpolateGenericsExample.java", """
                                                                           public class InterpolateGenericsExample <A extends Comparable<B>, B extends Comparable<A>> {
                                                                                static class IndependentGeneric<C> {}
                                                                                static class DependentGeneric<D extends E, E> {}
                                                                             }
                                                                           """)
                     .process(context ->
                              {
                                 D.Class declared = context.getClassOrThrow("InterpolateGenericsExample")
                                                           .withGenerics(context.getClassOrThrow("java.lang.String"),
                                                                          context.getConstants().getUnboundWildcard());

                                 D.Class capture = declared.capture();
                                 D.Type interpolated = Optional.of((D.Generic) capture.getGenericUsages().get(1))
                                                               .map(D.Generic::getBound)
                                                               .map(D.Interface.class::cast)
                                                               .map(D.Interface::getGenericUsages)
                                                               .map(types -> types.get(0))
                                                               .orElseThrow();
                                 assertEquals(context.getClassOrThrow("java.lang.String"), interpolated);

                                 D.Class independentExample = context.getClassOrThrow("InterpolateGenericsExample.IndependentGeneric")
                                                                     .withGenerics(context.getConstants().getUnboundWildcard());

                                 D.Class independentCapture = independentExample.capture();
                                 D.Type interpolatedIndependent = Optional.of(((D.Generic) independentCapture.getGenericUsages().get(0)))
                                                                          .map(D.Generic::getBound)
                                                                          .orElseThrow();
                                 assertEquals(context.getClassOrThrow("java.lang.Object"), interpolatedIndependent);

                                 D.Class dependentExample = context.getClassOrThrow("InterpolateGenericsExample.DependentGeneric")
                                                                   .withGenerics(context.getConstants().getUnboundWildcard(),
                                                                                  context.getClassOrThrow("java.lang.String"));

                                 D.Class dependentCapture = dependentExample.capture();
                                 D.Type interpolatedDependent = Optional.of(((D.Generic) dependentCapture.getGenericUsages().get(0)))
                                                                        .map(D.Generic::getBound)
                                                                        .orElseThrow();
                                 assertEquals(context.getClassOrThrow("java.lang.String"), interpolatedDependent);
                              });
   }

   @Test
   void getSuperClass()
   {
      processorTest().process(context ->
                              {
                                 D.Class integer = context.getClassOrThrow("java.lang.Integer");
                                 D.Class number = context.getClassOrThrow("java.lang.Number");
                                 assertEquals(integer.getSuperClass(), number);
                              });
   }

   @Test
   void getSuperClassOfObject()
   {
      processorTest().process(context ->
                              {
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 assertNull(object.getSuperClass());
                              });
   }

   @Test
   void getPermittedSubClasses()
   {
      processorTest().withCodeToCompile("PermittedSubClassesExample.java", """
                                                                           public sealed class PermittedSubClassesExample permits PermittedSubClassesExample.Child
                                                                           {
                                                                              public static final class Child extends PermittedSubClassesExample {}
                                                                           }
                                                                           """)
                     .process(context ->
                              {
                                 List<D.Class> expected = List.of(context.getClassOrThrow("PermittedSubClassesExample.Child"));

                                 D.Class permittedSubClassesExample = context.getClassOrThrow("PermittedSubClassesExample");
                                 assertEquals(expected, permittedSubClassesExample.getPermittedSubClasses());
                              });
   }

   @Test
   void getDirectInterfaces()
   {
      processorTest().withCodeToCompile("Direct.java", "public interface Direct{}")
                     .withCodeToCompile("InDirect.java", "public interface InDirect{}")
                     .withCodeToCompile("DirectInterfacesExample.java", """
                                                                        public class DirectInterfacesExample implements InDirect {
                                                                           public static class Child extends DirectInterfacesExample implements Direct {}
                                                                        }
                                                                        """)
                     .process(context ->
                              {
                                 D.Class child = context.getClassOrThrow("DirectInterfacesExample.Child");
                                 List<D.Interface> directInterfaces = child.getDirectInterfaces();

                                 assertEquals(1, directInterfaces.size());
                                 assertEquals(context.getDeclaredOrThrow("Direct"), directInterfaces.get(0));
                              });
   }

   @Test
   void isSubtypeOf()
   {
      processorTest().process(context ->
                              {
                                 D.Class declaredLong = context.getClassOrThrow("java.lang.Long");
                                 D.Class number = context.getClassOrThrow("java.lang.Number");

                                 assertTrue(declaredLong.isSubtypeOf(number));
                                 assertTrue(declaredLong.isSubtypeOf(declaredLong));
                                 assertFalse(number.isSubtypeOf(declaredLong));
                              });
   }

   @Test
   void isAssignableFrom()
   {
      processorTest().process(context ->
                              {
                                 D.Class declaredLong = context.getClassOrThrow("java.lang.Long");
                                 D.Class integer = context.getClassOrThrow("java.lang.Integer");
                                 D.Class number = context.getClassOrThrow("java.lang.Number");

                                 assertTrue(declaredLong.isAssignableFrom(number));
                                 assertTrue(declaredLong.isAssignableFrom(declaredLong));
                                 assertFalse(number.isAssignableFrom(declaredLong));

                                 assertTrue(integer.isAssignableFrom(context.getConstants().getPrimitiveInt()));
                              });
   }

   @Test
   void iGetDirectSuperTypes()
   {
      processorTest().withCodeToCompile("ClassParent.java", "abstract class ClassParent extends Number {}")
                     .withCodeToCompile("ClassMixedParent.java",
                                        "abstract class ClassMixedParent extends Number implements java.lang.Comparable<ClassMixedParent>, java.util.function.Consumer<ClassMixedParent> {}")
                     .process(context ->
                              {
                                 D.Class number = context.getClassOrThrow("java.lang.Number");
                                 D.Class classParent = context.getClassOrThrow("ClassParent");
                                 D.Interface comparable = context.getInterfaceOrThrow("java.lang.Comparable");
                                 D.Interface consumer = context.getInterfaceOrThrow("java.util.function.Consumer");
                                 D.Class classMixedParent = context.getClassOrThrow("ClassMixedParent");

                                 assertEquals(List.of(number), classParent.getDirectSuperTypes());

                                 assertEquals(List.of(number, comparable, consumer), classMixedParent.getDirectSuperTypes());
                              });
   }

   @Test
   void getSuperTypes()
   {
      processorTest().withCodeToCompile("ClassNoParent.java", "class ClassNoParent {}")
                     .withCodeToCompile("ClassParent.java", "abstract class ClassParent extends Number {}")
                     .withCodeToCompile("ClassMixedParent.java",
                                        "abstract class ClassMixedParent extends Number implements java.lang.Comparable<ClassMixedParent>, java.util.function.Consumer<ClassMixedParent> {}")
                     .process(context ->
                              {
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 D.Class number = context.getClassOrThrow("java.lang.Number");
                                 D.Interface serializable = context.getInterfaceOrThrow("java.io.Serializable");
                                 D.Interface comparable = context.getInterfaceOrThrow("java.lang.Comparable");
                                 D.Interface consumer = context.getInterfaceOrThrow("java.util.function.Consumer");

                                 assertEquals(Collections.emptySet(), object.getSuperTypes());

                                 D.Class mixedParent = context.getClassOrThrow("ClassMixedParent");
                                 assertEquals(Set.of(object, number, serializable, comparable, consumer), mixedParent.getSuperTypes());
                              });
   }

   @Test
   void getSurounding()
   {
      processorTest().withCodeToCompile("Outer.java", """
                                                      public class Outer {
                                                           static class Inner {}
                                                        }
                                                      """)
                     .process(context ->
                              {
                                 D.Declared inner = context.getDeclaredOrThrow("Outer.Inner");
                                 D.Declared outer = inner.getSurrounding().orElseThrow();
                                 assertEquals(context.getDeclaredOrThrow("Outer"), outer);
                              });
   }
}
