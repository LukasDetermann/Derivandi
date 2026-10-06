package com.derivandi;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import static com.derivandi.api.dsl.RenderingContext.createRenderingContext;
import static com.derivandi.api.test.ProcessorTest.processorTest;
import static com.derivandi.internal.TypesafeUsageGenerator.createTypeSafeUsage;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TypesafeUsageGeneratorTest
{
   @Test
   void allParams()
   {
      processorTest().withCodeToCompile("MyOtherAnnotation.java",
                                        """
                                        public @interface MyOtherAnnotation {}""")
                     .withCodeToCompile("MyAnnotation.java",
                                        """
                                        public @interface MyAnnotation{
                                           String string();
                                           boolean boolean_();
                                           byte byte_();
                                           short short_();
                                           int int_();
                                           long long_();
                                           char char_();
                                           float float_();
                                           double double_();
                                           Class<?> type();
                                           java.lang.annotation.RetentionPolicy enum_();
                                           MyOtherAnnotation annotation();
                                           int[] values();
                                        }""")
                     .process(context ->
                              {
                                 if (!context.isFirstRound())
                                 {
                                    return;
                                 }

                                 D.Annotation myAnnotation = context.getAnnotationOrThrow("MyAnnotation");

                                 String declaration = createTypeSafeUsage(myAnnotation).renderDeclaration(createRenderingContext());
                                 String withTimeVariance = declaration.replaceFirst(
                                       "date = \"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+\"\\)",
                                       "date = \"1970-01-01T00:00:00.0000000\")");

                                 assertEquals(EXPECTED, withTimeVariance);
                              });
   }

   private static final String EXPECTED = """
                                          import java.util.Objects;
                                          import com.derivandi.api.dsl.RenderingContext;
                                          import com.derivandi.api.Origin;
                                          import com.derivandi.api.D;
                                          import javax.annotation.processing.Generated;
                                          import java.lang.annotation.RetentionPolicy;
                                          import java.util.List;
                                          import java.util.Map;
                                          
                                          @Generated(value = "com.derivandi.internal.TypesafeUsageGenerator", date = "1970-01-01T00:00:00.0000000")
                                          public class MyAnnotationTypesafeUsage implements D.AnnotationUsage {
                                          
                                             private static final String QUALIFIED_ANNOTATION_NAME = "MyAnnotation";
                                             private final D.AnnotationUsage myAnnotation;
                                             public MyAnnotationTypesafeUsage(D.AnnotationUsage usage) {
                                                Objects.requireNonNull(usage);
                                          
                                                String qualifiedName = usage.getAnnotation().getQualifiedName();
                                          
                                                if (!QUALIFIED_ANNOTATION_NAME.equals(qualifiedName)) {
                                          
                                                   throw new IllegalArgumentException("This Meta-Model represents a usage of \\"" +
                                                                                      QUALIFIED_ANNOTATION_NAME + "\\" not \\"" + qualifiedName + "\\"");
                                                }
                                                this.myAnnotation = usage;
                                             }
                                          
                                             public String string() {
                                                return (String) myAnnotation.getValueOrThrow("string").getValue();
                                             }
                                          
                                             public D.AnnotationValue.StringValue stringValue() {
                                                return (D.AnnotationValue.StringValue) myAnnotation.getValueOrThrow("string");
                                             }
                                          
                                             public boolean boolean_() {
                                                return (boolean) myAnnotation.getValueOrThrow("boolean_").getValue();
                                             }
                                          
                                             public D.AnnotationValue.BooleanValue boolean_Value() {
                                                return (D.AnnotationValue.BooleanValue) myAnnotation.getValueOrThrow("boolean_");
                                             }
                                          
                                             public byte byte_() {
                                                return (byte) myAnnotation.getValueOrThrow("byte_").getValue();
                                             }
                                          
                                             public D.AnnotationValue.ByteValue byte_Value() {
                                                return (D.AnnotationValue.ByteValue) myAnnotation.getValueOrThrow("byte_");
                                             }
                                          
                                             public short short_() {
                                                return (short) myAnnotation.getValueOrThrow("short_").getValue();
                                             }
                                          
                                             public D.AnnotationValue.ShortValue short_Value() {
                                                return (D.AnnotationValue.ShortValue) myAnnotation.getValueOrThrow("short_");
                                             }
                                          
                                             public int int_() {
                                                return (int) myAnnotation.getValueOrThrow("int_").getValue();
                                             }
                                          
                                             public D.AnnotationValue.IntegerValue int_Value() {
                                                return (D.AnnotationValue.IntegerValue) myAnnotation.getValueOrThrow("int_");
                                             }
                                          
                                             public long long_() {
                                                return (long) myAnnotation.getValueOrThrow("long_").getValue();
                                             }
                                          
                                             public D.AnnotationValue.LongValue long_Value() {
                                                return (D.AnnotationValue.LongValue) myAnnotation.getValueOrThrow("long_");
                                             }
                                          
                                             public char char_() {
                                                return (char) myAnnotation.getValueOrThrow("char_").getValue();
                                             }
                                          
                                             public D.AnnotationValue.CharacterValue char_Value() {
                                                return (D.AnnotationValue.CharacterValue) myAnnotation.getValueOrThrow("char_");
                                             }
                                          
                                             public float float_() {
                                                return (float) myAnnotation.getValueOrThrow("float_").getValue();
                                             }
                                          
                                             public D.AnnotationValue.FloatValue float_Value() {
                                                return (D.AnnotationValue.FloatValue) myAnnotation.getValueOrThrow("float_");
                                             }
                                          
                                             public double double_() {
                                                return (double) myAnnotation.getValueOrThrow("double_").getValue();
                                             }
                                          
                                             public D.AnnotationValue.DoubleValue double_Value() {
                                                return (D.AnnotationValue.DoubleValue) myAnnotation.getValueOrThrow("double_");
                                             }
                                          
                                             public D.AnnotationValue.TypeValue type() {
                                                return (D.AnnotationValue.TypeValue) myAnnotation.getValueOrThrow("type").getValue();
                                             }
                                          
                                             public D.AnnotationValue.TypeValue typeValue() {
                                                return (D.AnnotationValue.TypeValue) myAnnotation.getValueOrThrow("type");
                                             }
                                          
                                             public RetentionPolicy enum_() {
                                                return (RetentionPolicy) myAnnotation.getValueOrThrow("enum_").getValue();
                                             }
                                          
                                             public D.AnnotationValue.EnumValue enum_Value() {
                                                return (D.AnnotationValue.EnumValue) myAnnotation.getValueOrThrow("enum_");
                                             }
                                          
                                             public MyOtherAnnotation annotation() {
                                                return (MyOtherAnnotation) myAnnotation.getValueOrThrow("annotation").getValue();
                                             }
                                          
                                             public D.AnnotationValue.AnnotationUsageValue annotationValue() {
                                                return (D.AnnotationValue.AnnotationUsageValue) myAnnotation.getValueOrThrow("annotation");
                                             }
                                          
                                             public List<int> values() {
                                                return ((D.AnnotationValue.Values<?>) myAnnotation.getValueOrThrow("values")).getValue().stream().map(D.NestedAnnotationValue.IntegerValue.class::cast).map(D.NestedAnnotationValue.IntegerValue::getValue).toList();
                                             }
                                          
                                             public D.AnnotationValue.Values<D.AnnotationValue.IntegerValue> valuesValue() {
                                                return (D.AnnotationValue.Values) myAnnotation.getValueOrThrow("values");
                                             }
                                          
                                             private static Class<?> resolveEnumName(D.EnumConstant enumConstant) {
                                                try {
                                                   return Class.forName(enumConstant.getSurrounding().getBinaryName());
                                                }
                                                catch (ClassNotFoundException e) {
                                                   throw new RuntimeException(e);
                                                }
                                             }
                                          
                                             @Override
                                             public Map<D.Method, D.AnnotationValue> getValues() {
                                                return myAnnotation.getValues();
                                             }
                                          
                                             @Override
                                             public D.Annotation getAnnotation() {
                                                return myAnnotation.getAnnotation();
                                             }
                                          
                                             @Override
                                             public Origin getOrigin() {
                                                return myAnnotation.getOrigin();
                                             }
                                          
                                             @Override
                                             public String renderDeclaration(RenderingContext renderingContext) {
                                                return myAnnotation.renderDeclaration(renderingContext);
                                             }
                                          
                                          }""";
}