package com.derivandi.structure;

import com.derivandi.api.D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.RetentionPolicy;

import static com.derivandi.api.test.ProcessorTest.processorTest;

class EnumConstantTest
{
   @Test
   void getSurrounding()
   {
      processorTest().process(context ->
                              {
                                 D.Enum retentionPolicy = context.getEnumOrThrow("java.lang.annotation.RetentionPolicy");
                                 D.EnumConstant source = retentionPolicy.getEnumConstantOrThrow("SOURCE");
                                 Assertions.assertEquals(retentionPolicy, source.getSurrounding());
                              });
   }

   @Test
   void getConstantValue()
   {
      processorTest().process(context ->
                              {
                                 D.Annotation retention = context.getAnnotationOrThrow("java.lang.annotation.Retention");
                                 D.AnnotationUsage retentionUsage = retention.getUsageOfOrThrow(retention);
                                 RetentionPolicy value = Enum.valueOf(RetentionPolicy.class,
                                                                      ((D.NestedAnnotationValue.EnumValue) retentionUsage
                                                                            .getValueOrThrow("value")).getValue().getName());

                                 Assertions.assertEquals(RetentionPolicy.RUNTIME, value);
                              });
   }


   @Test
   void getConstantValueSameCompilationUnit()
   {
      processorTest().withCodeToCompile("MyEnum.java", """
                                                       public enum MyEnum {
                                                           A,
                                                           B;
                                                       }
                                                       """)
                     .withCodeToCompile("MyAnnotation.java", """
                                                             @MyAnnotation(MyEnum.A)
                                                             public @interface MyAnnotation {
                                                                 MyEnum value();
                                                             }""")
                     .process(context ->
                              {
                                 D.Annotation retention = context.getAnnotationOrThrow("MyAnnotation");
                                 D.AnnotationUsage retentionUsage = retention.getUsageOfOrThrow(retention);
                                 D.EnumConstant value = ((D.NestedAnnotationValue.EnumValue) retentionUsage.getValueOrThrow("value")).getValue();

                                 Assertions.assertEquals("A", value.getName());
                              });
   }
}
