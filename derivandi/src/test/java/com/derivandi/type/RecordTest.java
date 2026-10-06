package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.*;

class RecordTest
{
   @Test
   void withGenerics()
   {
      processorTest().withCodeToCompile("InterpolateGenericsExample.java", """
                                                                           public record InterpolateGenericsExample<A extends Comparable<B>, B extends Comparable<A>> () {
                                                                              record IndependentGeneric<C> () {}
                                                                              record DependentGeneric<D extends E, E> () {}
                                                                           }
                                                                           """)
                     .withCodeToCompile("SimpleRecord.java", """
                                                             public record SimpleRecord () {}
                                                             """)
                     .process(context ->
                              {
                                 assertThrows(IllegalArgumentException.class,
                                              () -> context.getRecordOrThrow("InterpolateGenericsExample.DependentGeneric")
                                                           .withGenerics("java.lang.String"));

                                 assertThrows(IllegalArgumentException.class,
                                              () -> context.getRecordOrThrow("SimpleRecord")
                                                           .withGenerics("java.io.Serializable"));

                                 assertEquals(List.of(context.getClassOrThrow("java.lang.String")),
                                              context.getRecordOrThrow("InterpolateGenericsExample.IndependentGeneric")
                                                     .withGenerics("java.lang.String")
                                                     .getGenericUsages());

                                 assertEquals(List.of(context.getClassOrThrow("java.lang.String"),
                                                      context.getClassOrThrow("java.lang.Number")),
                                              context.getRecordOrThrow("InterpolateGenericsExample.DependentGeneric")
                                                     .withGenerics("java.lang.String",
                                                                   "java.lang.Number")
                                                     .getGenericUsages());
                              });
   }

   @Test
   void capture()
   {
      processorTest().withCodeToCompile("InterpolateGenericsExample.java", """
                                                                           public record InterpolateGenericsExample<A extends Comparable<B>, B extends Comparable<A>> () {
                                                                              record IndependentGeneric<C> () {}
                                                                              record DependentGeneric<D extends E, E> () {}
                                                                           }
                                                                           """)
                     .process(context ->
                              {
                                 D.Record declared = context.getRecordOrThrow("InterpolateGenericsExample")
                                                            .withGenerics(context.getClassOrThrow("java.lang.String"),
                                                                           context.getConstants().getUnboundWildcard());

                                 D.Record capture = declared.capture();
                                 D.Type interpolated = Optional.of(((D.Generic) capture.getGenericUsages().get(1)))
                                                               .map(D.Generic::getBound)
                                                               .map(D.Interface.class::cast)
                                                               .map(D.Interface::getGenericUsages)
                                                               .map(types -> types.get(0))
                                                               .orElseThrow();
                                 assertEquals(context.getClassOrThrow("java.lang.String"), interpolated);

                                 D.Record independentExample = context.getRecordOrThrow("InterpolateGenericsExample.IndependentGeneric")
                                                                      .withGenerics(context.getConstants().getUnboundWildcard());

                                 D.Record independentCapture = independentExample.capture();
                                 D.Type interpolatedIndependent = Optional.of(((D.Generic) independentCapture.getGenericUsages().get(0)))
                                                                          .map(D.Generic::getBound)
                                                                          .orElseThrow();
                                 assertEquals(context.getClassOrThrow("java.lang.Object"), interpolatedIndependent);

                                 D.Record dependentExample = context.getRecordOrThrow("InterpolateGenericsExample.DependentGeneric")
                                                                    .withGenerics(context.getConstants().getUnboundWildcard(),
                                                                                   context.getClassOrThrow("java.lang.String"));

                                 D.Record dependentCapture = dependentExample.capture();
                                 D.Type interpolatedDependent = Optional.of(((D.Generic) dependentCapture.getGenericUsages().get(0)))
                                                                        .map(D.Generic::getBound)
                                                                        .orElseThrow();
                                 assertEquals(context.getClassOrThrow("java.lang.String"), interpolatedDependent);
                              });
   }

   @Test
   void getRecordComponent()
   {
      processorTest().withCodeToCompile("RecordExample.java", "public record RecordExample(Long id) implements java.io.Serializable{}")
                     .process(context ->
                              {
                                 D.Record example = context.getRecordOrThrow("RecordExample");
                                 D.Class cLong = context.getClassOrThrow("java.lang.Long");

                                 D.RecordComponent id = example.getRecordComponentOrThrow("id");
                                 assertEquals("id", id.getName());
                                 assertEquals(cLong, id.getType());

                                 assertThrows(NoSuchElementException.class, () -> example.getRecordComponentOrThrow("asdf"));
                              });
   }

   @Test
   void getDirectInterfaces()
   {
      processorTest().withCodeToCompile("RecordExample.java", "public record RecordExample(Long id) implements java.io.Serializable{}")
                     .process(context ->
                              {
                                 D.Record example = context.getRecordOrThrow("RecordExample");
                                 D.Interface serializable = context.getInterfaceOrThrow("java.io.Serializable");

                                 assertEquals(List.of(serializable), example.getDirectInterfaces());
                              });
   }

   @Test
   void isSubtypeOf()
   {
      processorTest().withCodeToCompile("RecordExample.java", "public record RecordExample(Long id) implements java.io.Serializable{}")
                     .process(context ->
                              {
                                 D.Record example = context.getRecordOrThrow("RecordExample");
                                 D.Class cRecord = context.getClassOrThrow("java.lang.Record");
                                 D.Class number = context.getClassOrThrow("java.lang.Number");

                                 assertTrue(example.isSubtypeOf(cRecord));
                                 assertTrue(example.isSubtypeOf(example));
                                 assertFalse(example.isSubtypeOf(number));
                              });
   }

   @Test
   void getDirectSuperTypes()
   {
      processorTest().withCodeToCompile("RecordNoParent.java", "record RecordNoParent() {}")
                     .withCodeToCompile("RecordMultiParent.java", """
                                                                  record RecordMultiParent() implements java.util.function.Consumer<RecordMultiParent>, java.util.function.Supplier<RecordMultiParent> {
                                                                        @Override
                                                                        public void accept(RecordMultiParent recordMultiParent) {}
                                                                  
                                                                        @Override
                                                                        public RecordMultiParent get() {return null;}
                                                                     }""")
                     .process(context ->
                              {
                                 D.Class cRecord = context.getClassOrThrow("java.lang.Record");
                                 D.Interface consumer = context.getInterfaceOrThrow("java.util.function.Consumer");
                                 D.Interface supplier = context.getInterfaceOrThrow("java.util.function.Supplier");

                                 D.Record noParent = context.getRecordOrThrow("RecordNoParent");
                                 assertEquals(List.of(cRecord), noParent.getDirectSuperTypes());

                                 D.Record multiParent = context.getRecordOrThrow("RecordMultiParent");
                                 assertEquals(List.of(cRecord, consumer, supplier), multiParent.getDirectSuperTypes());
                              });
   }

   @Test
   void getSuperTypes()
   {
      processorTest().withCodeToCompile("RecordNoParent.java", "record RecordNoParent() {}")
                     .withCodeToCompile("RecordMultiParent.java", """
                                                                  record RecordMultiParent() implements java.util.function.Consumer<RecordMultiParent>, java.util.function.Supplier<RecordMultiParent> {
                                                                        @Override
                                                                        public void accept(RecordMultiParent recordMultiParent) {}
                                                                  
                                                                        @Override
                                                                        public RecordMultiParent get() {return null;}
                                                                     }""")
                     .process(context ->
                              {
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 D.Class cRecord = context.getClassOrThrow("java.lang.Record");
                                 D.Interface consumer = context.getInterfaceOrThrow("java.util.function.Consumer");
                                 D.Interface supplier = context.getInterfaceOrThrow("java.util.function.Supplier");

                                 D.Record noParent = context.getRecordOrThrow("RecordNoParent");

                                 assertEquals(Set.of(object, cRecord), noParent.getSuperTypes());

                                 D.Record multiParent = context.getRecordOrThrow("RecordMultiParent");
                                 assertEquals(Set.of(object, cRecord, consumer, supplier), multiParent.getSuperTypes());
                              });
   }

   @Test
   void getSurounding()
   {
      processorTest().withCodeToCompile("Outer.java", """
                                                      public record Outer() {
                                                            record Inner() {}
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
