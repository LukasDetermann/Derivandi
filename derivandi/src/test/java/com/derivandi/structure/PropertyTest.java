package com.derivandi.structure;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.*;

class PropertyTest
{
   @Test
   void id()
   {
      processorTest().withCodeToCompile("Pojo", """
                                                class Pojo {
                                                   private Long id;
                                                   public Long getId() {return id;}
                                                   public void setId(Long id) {this.id = id;}
                                                }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Pojo");
                                 List<D.Property> properties = cClass.getProperties();

                                 assertEquals(2, properties.size());
                                 assertEquals("class", properties.get(0).getName());

                                 assertEquals("id", properties.get(1).getName());
                                 assertTrue(properties.get(1).isMutable());
                                 D.Nameable type = ((D.Nameable) properties.get(1).getType());
                                 assertEquals("Long", type.getName());
                                 D.Field field = properties.get(1).getFieldOrThrow();
                                 assertEquals("id", field.getName());
                                 D.Method getter = properties.get(1).getGetter();
                                 assertEquals("getId", getter.getName());
                                 D.Method setter = properties.get(1).getSetterOrThrow();
                                 assertEquals("setId", setter.getName());
                              });
   }

   @Test
   void isAndSet()
   {
      processorTest().withCodeToCompile("Pojo", """
                                                class Pojo {
                                                   public boolean isValid() {return false;}
                                                   public boolean getValid() {return false;}
                                                }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Pojo");
                                 List<D.Property> properties = cClass.getProperties();

                                 assertEquals(2, properties.size());
                                 assertEquals("class", properties.get(0).getName());

                                 assertEquals("valid", properties.get(1).getName());
                                 assertFalse(properties.get(1).isMutable());
                                 D.Nameable type = ((D.Nameable) properties.get(1).getType());
                                 assertEquals("boolean", type.getName());
                                 assertTrue(properties.get(1).getField().isEmpty());
                                 D.Method getter = properties.get(1).getGetter();
                                 assertEquals("isValid", getter.getName());
                                 assertTrue(properties.get(1).getSetter().isEmpty());
                              });
   }

   @Test
   void generic()
   {
      processorTest().withCodeToCompile("Pojo", """
                                                class Pojo {
                                                   private java.util.List<String> values;
                                                   public java.util.List<String> getValues() {return values;}
                                                   public void setValues(java.util.List<String> values) {this.values = values;}
                                                }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Pojo");
                                 List<D.Property> properties = cClass.getProperties();

                                 assertEquals(2, properties.size());
                                 assertEquals("class", properties.get(0).getName());

                                 assertEquals("values", properties.get(1).getName());
                                 assertTrue(properties.get(1).isMutable());
                                 D.Nameable type = ((D.Nameable) properties.get(1).getType());
                                 assertEquals("List", type.getName());
                                 D.Field field = properties.get(1).getFieldOrThrow();
                                 assertEquals("values", field.getName());
                                 D.Method getter = properties.get(1).getGetter();
                                 assertEquals("getValues", getter.getName());
                                 D.Method setter = properties.get(1).getSetterOrThrow();
                                 assertEquals("setValues", setter.getName());
                              });
   }

   @Test
   void sTrangeCase()
   {
      processorTest().withCodeToCompile("Pojo", """
                                                class Pojo {
                                                   private String sTrangeCase;
                                                   public String getsTrangeCase() {return sTrangeCase;}
                                                   public void setsTrangeCase(String sTrangeCase) {this.sTrangeCase = sTrangeCase;}
                                                }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Pojo");
                                 List<D.Property> properties = cClass.getProperties();

                                 assertEquals(2, properties.size());
                                 assertEquals("class", properties.get(0).getName());

                                 assertEquals("sTrangeCase", properties.get(1).getName());
                                 assertTrue(properties.get(1).isMutable());
                                 D.Nameable type = ((D.Nameable) properties.get(1).getType());
                                 assertEquals("String", type.getName());
                                 D.Field field = properties.get(1).getFieldOrThrow();
                                 assertEquals("sTrangeCase", field.getName());
                                 D.Method getter = properties.get(1).getGetter();
                                 assertEquals("getsTrangeCase", getter.getName());
                                 D.Method setter = properties.get(1).getSetterOrThrow();
                                 assertEquals("setsTrangeCase", setter.getName());
                              });
   }

   @Test
   void SCREAMING_SNAKE_CASE()
   {
      processorTest().withCodeToCompile("Pojo", """
                                                class Pojo {
                                                   private String NOT_A_CONSTANT;
                                                   public String getNOT_A_CONSTANT() {return NOT_A_CONSTANT;}
                                                   public void setNOT_A_CONSTANT(String NOT_A_CONSTANT) {this.NOT_A_CONSTANT = NOT_A_CONSTANT;}
                                                }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Pojo");
                                 List<D.Property> properties = cClass.getProperties();

                                 assertEquals(2, properties.size());
                                 assertEquals("class", properties.get(0).getName());

                                 assertEquals("NOT_A_CONSTANT", properties.get(1).getName());
                                 assertTrue(properties.get(1).isMutable());
                                 D.Nameable type = ((D.Nameable) properties.get(1).getType());
                                 assertEquals("String", type.getName());
                                 D.Field field = properties.get(1).getFieldOrThrow();
                                 assertEquals("NOT_A_CONSTANT", field.getName());
                                 D.Method getter = properties.get(1).getGetter();
                                 assertEquals("getNOT_A_CONSTANT", getter.getName());
                                 D.Method setter = properties.get(1).getSetterOrThrow();
                                 assertEquals("setNOT_A_CONSTANT", setter.getName());
                              });
   }

   @Test
   void voidMethod()
   {
      processorTest().withCodeToCompile("Pojo", """
                                                class Pojo {
                                                   public void foo() {}
                                                }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Pojo");
                                 List<D.Property> properties = cClass.getProperties();

                                 assertEquals(1, properties.size());
                                 assertEquals("class", properties.get(0).getName());
                              });
   }

   @Test
   void overwrittenAccessor()
   {
      processorTest().withCodeToCompile("Parent", "abstract class Parent{public abstract Long getId();}")
                     .withCodeToCompile("Child", "abstract class Child extends Parent{public abstract Long getId();}")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Child");
                                 List<D.Property> properties = cClass.getProperties();

                                 assertEquals(2, properties.size());
                                 assertEquals("class", properties.get(0).getName());
                                 assertEquals("id", properties.get(1).getName());

                                 D.Method getter = properties.get(1).getGetter();
                                 D.Declared cDeclared = getter.getSurrounding();
                                 assertEquals("Child", cDeclared.getName());
                              });
   }

   @Test
   void wrappedBoolean()
   {
      processorTest().withCodeToCompile("Pojo", """
                                                class Pojo {
                                                   private Boolean flag;
                                                   public Boolean isFlag() {return flag;}
                                                   public void setFlag(Boolean id) {this.flag = flag;}
                                                }""")
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("Pojo");
                                 List<D.Property> properties = cClass.getProperties();

                                 assertEquals(2, properties.size());
                                 assertEquals("class", properties.get(0).getName());
                                 assertEquals("flag", properties.get(1).getName());
                                 D.Field field = properties.get(1).getFieldOrThrow();
                                 assertEquals("flag", field.getName());
                                 D.QualifiedNameable fieldType = (D.QualifiedNameable) field.getType();
                                 assertEquals("java.lang.Boolean", fieldType.getQualifiedName());
                              });
   }
}
