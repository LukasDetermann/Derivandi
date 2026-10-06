package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.*;

class InterfaceTest
{
   @Test
   void withGenerics()
   {
      processorTest().withCodeToCompile("InterpolateGenericsExample.java", """
                                                                           public interface InterpolateGenericsExample<A extends Comparable<B>, B extends Comparable<A>> {
                                                                              interface IndependentGeneric<C> {}
                                                                              interface DependentGeneric<D extends E, E> {}
                                                                           }
                                                                           """)
                     .process(context ->
                              {
                                 assertThrows(IllegalArgumentException.class,
                                              () -> context.getInterfaceOrThrow("InterpolateGenericsExample.DependentGeneric")
                                                           .withGenerics("java.lang.String"));

                                 assertThrows(IllegalArgumentException.class,
                                              () -> context.getInterfaceOrThrow("java.io.Serializable").withGenerics("java.io.Serializable"));

                                 assertEquals(List.of(context.getClassOrThrow("java.lang.String")),
                                              context.getInterfaceOrThrow("InterpolateGenericsExample.IndependentGeneric")
                                                     .withGenerics("java.lang.String")
                                                     .getGenericUsages());

                                 assertEquals(List.of(context.getClassOrThrow("java.lang.String"),
                                                      context.getClassOrThrow("java.lang.Number")),
                                              context.getInterfaceOrThrow("InterpolateGenericsExample.DependentGeneric")
                                                     .withGenerics("java.lang.String",
                                                                   "java.lang.Number")
                                                     .getGenericUsages());
                              });
   }

   @Test
   void capture()
   {
      processorTest().withCodeToCompile("InterpolateGenericsExample.java", """
                                                                           public interface InterpolateGenericsExample<A extends Comparable<B>, B extends Comparable<A>> {
                                                                              interface IndependentGeneric<C> {}
                                                                              interface DependentGeneric<D extends E, E> {}
                                                                           }
                                                                           """)
                     .process(context ->
                              {
                                 D.Interface declared = context.getInterfaceOrThrow("InterpolateGenericsExample")
                                                               .withGenerics(context.getClassOrThrow("java.lang.String"),
                                                                              context.getConstants().getUnboundWildcard());

                                 D.Interface capture = declared.capture();
                                 D.Type interpolated = Optional.of(((D.Generic) capture.getGenericUsages().get(1)))
                                                               .map(D.Generic::getBound)
                                                               .map(D.Interface.class::cast)
                                                               .map(D.Interface::getGenericUsages)
                                                               .map(types -> types.get(0))
                                                               .orElseThrow();
                                 assertEquals(context.getClassOrThrow("java.lang.String"), interpolated);

                                 D.Interface independentExample = context.getInterfaceOrThrow("InterpolateGenericsExample.IndependentGeneric")
                                                                         .withGenerics(context.getConstants().getUnboundWildcard());

                                 D.Interface independentCapture = independentExample.capture();
                                 D.Type interpolatedIndependent = Optional.of(((D.Generic) independentCapture.getGenericUsages().get(0)))
                                                                          .map(D.Generic::getBound)
                                                                          .orElseThrow();
                                 assertEquals(context.getClassOrThrow("java.lang.Object"), interpolatedIndependent);

                                 D.Interface dependentExample = context.getInterfaceOrThrow("InterpolateGenericsExample.DependentGeneric")
                                                                       .withGenerics(context.getConstants().getUnboundWildcard(),
                                                                                      context.getClassOrThrow("java.lang.String"));

                                 D.Interface dependentCapture = dependentExample.capture();
                                 D.Type interpolatedDependent = Optional.of((D.Generic) dependentCapture.getGenericUsages().get(0))
                                                                        .map(D.Generic::getBound)
                                                                        .orElseThrow();
                                 assertEquals(context.getClassOrThrow("java.lang.String"), interpolatedDependent);
                              });
   }

   @Test
   void getDirectInterfaces()
   {
      processorTest().process(context ->
                              {
                                 D.Interface function = context.getInterfaceOrThrow("java.util.function.Function");
                                 D.Interface unaryOperator = context.getInterfaceOrThrow("java.util.function.UnaryOperator");

                                 assertEquals(0, function.getDirectInterfaces().size());

                                 assertEquals(List.of(function), unaryOperator.getDirectInterfaces());
                              });
   }

   @Test
   void isFunctional()
   {
      processorTest().process(context ->
                              {
                                 Predicate<String> isFunctional = name ->
                                 {
                                    D.Interface cInterface = context.getInterfaceOrThrow(name);
                                    return cInterface.isFunctional();
                                 };

                                 assertTrue(isFunctional.test("java.util.function.Function"));
                                 assertTrue(isFunctional.test("java.lang.Comparable"));
                                 assertFalse(isFunctional.test("java.util.List"));
                              });
   }

   @Test
   void isSubtypeOf()
   {
      processorTest().process(context ->
                              {
                                 D.Interface function = context.getInterfaceOrThrow("java.util.function.Function");
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 D.Class number = context.getClassOrThrow("java.lang.Number");

                                 assertTrue(function.isSubtypeOf(object));
                                 assertTrue(function.isSubtypeOf(function));
                                 assertFalse(function.isSubtypeOf(number));
                              });
   }

   @Test
   void getDirectSuperTypes()
   {
      processorTest().withCodeToCompile("DirektSuperTypeExample.java", """
                                                                       import java.util.function.Consumer;
                                                                       import java.util.function.Supplier;
                                                                       
                                                                       public class DirektSuperTypeExample {
                                                                          interface InterfaceNoParent {}
                                                                       
                                                                          interface InterfaceParent extends Comparable<InterfaceParent>,
                                                                                                            Consumer<InterfaceParent> {}
                                                                       }
                                                                       """)
                     .process(context ->
                              {
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 D.Interface comparable = context.getInterfaceOrThrow("java.lang.Comparable");
                                 D.Interface consumer = context.getInterfaceOrThrow("java.util.function.Consumer");

                                 D.Interface noParent = context.getInterfaceOrThrow("DirektSuperTypeExample.InterfaceNoParent");
                                 assertEquals(List.of(object), noParent.getDirectSuperTypes());

                                 D.Interface parent = context.getInterfaceOrThrow("DirektSuperTypeExample.InterfaceParent");
                                 assertEquals(List.of(object, comparable, consumer), parent.getDirectSuperTypes());
                              });
   }

   @Test
   void getSuperTypes()
   {
      processorTest().withCodeToCompile("DirektSuperTypeExample.java", """
                                                                       import java.util.function.Consumer;
                                                                       import java.util.function.Supplier;
                                                                       
                                                                       public class DirektSuperTypeExample {
                                                                          interface InterfaceNoParent {}
                                                                       
                                                                          interface InterfaceParent extends Comparable<InterfaceParent>,
                                                                                                            Consumer<InterfaceParent> {}
                                                                       }
                                                                       """)
                     .process(context ->
                              {
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 D.Interface comparable = context.getInterfaceOrThrow("java.lang.Comparable");
                                 D.Interface consumer = context.getInterfaceOrThrow("java.util.function.Consumer");

                                 D.Interface noParent = context.getInterfaceOrThrow("DirektSuperTypeExample.InterfaceNoParent");
                                 assertEquals(Set.of(object), noParent.getSuperTypes());

                                 D.Interface parent = context.getInterfaceOrThrow("DirektSuperTypeExample.InterfaceParent");
                                 assertEquals(Set.of(object, comparable, consumer), parent.getSuperTypes());
                              });
   }

   @Test
   void getSurounding()
   {
      processorTest().withCodeToCompile("Outer.java", """
                                                      public interface Outer {
                                                            interface Inner {}
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
