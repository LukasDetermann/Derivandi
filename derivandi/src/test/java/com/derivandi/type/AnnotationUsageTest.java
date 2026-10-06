package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.*;

class AnnotationUsageTest
{
   @Test
   void stingValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             String stingValue() default "string Value";
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 assertEquals("string Value", defaultValues.getValueOrThrow("stingValue").getValue());
                              });
   }

   @Test
   void booleanValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             boolean booleanValue() default false;
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 assertEquals(false, defaultValues.getValueOrThrow("booleanValue").getValue());
                              });
   }

   @Test
   void byteValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             byte byteValue() default 1;;
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 assertEquals((byte) 1, defaultValues.getValueOrThrow("byteValue").getValue());
                              });
   }

   @Test
   void shortValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             short shortValue() default 2;
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 assertEquals((short) 2, defaultValues.getValueOrThrow("shortValue").getValue());
                              });
   }

   @Test
   void intValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             int intValue() default 3;
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 assertEquals(3, defaultValues.getValueOrThrow("intValue").getValue());
                              });
   }

   @Test
   void longValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             long longValue() default 4L;
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 assertEquals(4L, defaultValues.getValueOrThrow("longValue").getValue());
                              });
   }

   @Test
   void charValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             char charValue() default 'a';
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 assertEquals('a', defaultValues.getValueOrThrow("charValue").getValue());
                              });
   }

   @Test
   void floatValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             float floatValue() default 5f;
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 assertEquals(5f, defaultValues.getValueOrThrow("floatValue").getValue());
                              });
   }

   @Test
   void doubleValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             double doubleValue() default 6D;
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 assertEquals(6D, defaultValues.getValueOrThrow("doubleValue").getValue());
                              });
   }

   @Test
   void typeValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             Class<String> typeValue() default String.class;
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 assertEquals(context.getClassOrThrow("java.lang.String"),
                                              defaultValues.getValueOrThrow("typeValue").getValue());
                              });
   }

   @Test
   void enumValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          import java.lang.annotation.ElementType;
                                                                          
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             ElementType enumValue() default ElementType.ANNOTATION_TYPE;
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 D.Enum elementType = context.getEnumOrThrow("java.lang.annotation.ElementType");
                                 D.EnumConstant annotationType = elementType.getEnumConstantOrThrow("ANNOTATION_TYPE");
                                 assertEquals(annotationType, defaultValues.getValueOrThrow("enumValue").getValue());
                              });
   }

   @Test
   void annotationUsageValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          import java.lang.annotation.Retention;
                                                                          import java.lang.annotation.RetentionPolicy;
                                                                          
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             Retention annotationUsageValue() default @Retention(RetentionPolicy.CLASS);
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 D.Enum retentionPolicy = context.getEnumOrThrow("java.lang.annotation.RetentionPolicy");
                                 D.EnumConstant classPolicy = retentionPolicy.getEnumConstantOrThrow("CLASS");

                                 D.AnnotationUsage defaultUsage = (D.AnnotationUsage) defaultValues.getValueOrThrow("annotationUsageValue")
                                                                                                   .getValue();
                                 D.AnnotationValue defaultRetentionValue = defaultUsage.getValueOrThrow("value");
                                 assertEquals(classPolicy, defaultRetentionValue.getValue());
                              });
   }

   @Test
   void listOfValues()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             char[] asListOfValues() default {'b', 'c'};
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 //noinspection unchecked
                                 List<D.AnnotationValue> values = (List<D.AnnotationValue>) defaultValues.getValueOrThrow("asListOfValues")
                                                                                                         .getValue();
                                 List<Object> list = values.stream().map(D.AnnotationValue::getValue).toList();
                              });
   }

   @Test
   void isDefault()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          import java.lang.annotation.ElementType;
                                                                          import java.lang.annotation.Retention;
                                                                          import java.lang.annotation.RetentionPolicy;
                                                                          
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             String stingValue() default "string Value";
                                                                             boolean booleanValue() default false;
                                                                             byte byteValue() default 1;
                                                                             short shortValue() default 2;
                                                                             int intValue() default 3;
                                                                             long longValue() default 4L;
                                                                             char charValue() default 'a';
                                                                             float floatValue() default 5f;
                                                                             double doubleValue() default 6D;
                                                                             Class<String> typeValue() default String.class;
                                                                             ElementType enumValue() default ElementType.ANNOTATION_TYPE;
                                                                             Retention annotationUsageValue() default @Retention(RetentionPolicy.CLASS);
                                                                             char[] asListOfValues() default {'b', 'c'};
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       @AnnotationUsageAnnotation
                                                                       public class AnnotationUsageExample {}
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.AnnotationUsage defaultValues = cClass.getAnnotationUsages().get(0);

                                 assertTrue(defaultValues.getValues().values()
                                                         .stream()
                                                         .allMatch(D.AnnotationValue::isDefault));
                              });
   }

   @Test
   void overwriteValue()
   {
      processorTest().withCodeToCompile("AnnotationUsageAnnotation.java", """
                                                                          @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
                                                                          public @interface AnnotationUsageAnnotation {
                                                                             String stingValue() default "string Value";
                                                                          }
                                                                          """)
                     .withCodeToCompile("AnnotationUsageExample.java", """
                                                                       public class AnnotationUsageExample {
                                                                          @AnnotationUsageAnnotation(stingValue = "custom Value")
                                                                          private String testField;
                                                                       }
                                                                       """)
                     .process(context ->
                              {
                                 D.Class cClass = context.getClassOrThrow("AnnotationUsageExample");
                                 D.Field field = cClass.getFieldOrThrow("testField");
                                 D.AnnotationUsage overwrittenStringValue = field.getAnnotationUsages().get(0);
                                 D.AnnotationValue annotationValue = overwrittenStringValue.getValueOrThrow("stingValue");
                                 assertFalse(annotationValue.isDefault());
                                 assertEquals("custom Value", annotationValue.getValue());
                              });
   }
}