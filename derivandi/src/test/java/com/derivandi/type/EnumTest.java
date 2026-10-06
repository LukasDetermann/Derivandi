package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.*;

class EnumTest
{
   @Test
   void isSubtypeOf()
   {
      processorTest().process(context ->
                              {
                                 D.Enum retentionPolicy = context.getEnumOrThrow("java.lang.annotation.RetentionPolicy");
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 D.Class number = context.getClassOrThrow("java.lang.Number");

                                 assertTrue(retentionPolicy.isSubtypeOf(object));
                                 assertTrue(retentionPolicy.isSubtypeOf(retentionPolicy));
                                 assertFalse(retentionPolicy.isSubtypeOf(number));
                              });
   }

   @Test
   void getDirectSuperTypes()
   {
      processorTest().withCodeToCompile("EnumNoParent.java", "enum EnumNoParent{}")
                     .withCodeToCompile("EnumMultiParent.java", """
                                                                enum EnumMultiParent implements java.util.function.Consumer<EnumMultiParent>, java.util.function.Supplier<EnumMultiParent> {
                                                                      ;
                                                                      @Override
                                                                      public void accept(EnumMultiParent enumMultiParent) {}
                                                                
                                                                      @Override
                                                                      public EnumMultiParent get() {return null;}
                                                                   }
                                                                """)
                     .process(context ->
                              {
                                 D.Class cEnum = context.getClassOrThrow("java.lang.Enum");
                                 D.Interface consumer = context.getInterfaceOrThrow("java.util.function.Consumer");
                                 D.Interface supplier = context.getInterfaceOrThrow("java.util.function.Supplier");

                                 D.Enum noParent = context.getEnumOrThrow("EnumNoParent");
                                 assertEquals(List.of(cEnum), noParent.getDirectSuperTypes());

                                 D.Enum multiParent = context.getEnumOrThrow("EnumMultiParent");
                                 assertEquals(List.of(cEnum, consumer, supplier), multiParent.getDirectSuperTypes());
                              });
   }

   @Test
   void getSuperTypes()
   {
      processorTest().withCodeToCompile("EnumNoParent.java", "enum EnumNoParent{}")
                     .withCodeToCompile("EnumMultiParent.java", """
                                                                enum EnumMultiParent implements java.util.function.Consumer<EnumMultiParent>, java.util.function.Supplier<EnumMultiParent> {
                                                                      ;
                                                                      @Override
                                                                      public void accept(EnumMultiParent enumMultiParent) {}
                                                                
                                                                      @Override
                                                                      public EnumMultiParent get() {return null;}
                                                                   }
                                                                """)
                     .process(context ->
                              {
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 D.Interface constable = context.getInterfaceOrThrow("java.lang.constant.Constable");
                                 D.Interface comparable = context.getInterfaceOrThrow("java.lang.Comparable");
                                 D.Interface serializable = context.getInterfaceOrThrow("java.io.Serializable");
                                 D.Class cEnum = context.getClassOrThrow("java.lang.Enum");
                                 D.Interface consumer = context.getInterfaceOrThrow("java.util.function.Consumer");
                                 D.Interface supplier = context.getInterfaceOrThrow("java.util.function.Supplier");

                                 D.Enum noParent = context.getEnumOrThrow("EnumNoParent");
                                 assertEquals(Set.of(object, constable, comparable, serializable, cEnum), noParent.getSuperTypes());

                                 D.Enum multiParent = context.getEnumOrThrow("EnumMultiParent");
                                 assertEquals(Set.of(object, constable, comparable, serializable, cEnum, consumer, supplier),
                                              multiParent.getSuperTypes());
                              });
   }

   @Test
   void getSurounding()
   {
      processorTest().withCodeToCompile("Outer.java", """
                                                      public enum Outer {
                                                            UNUSED;
                                                            enum Inner {}
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
