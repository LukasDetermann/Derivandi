package com.derivandi.internal;

import com.derivandi.api.D;
import com.derivandi.api.dsl.JavaDsl;
import com.derivandi.api.dsl.VariableTypeRenderable;
import com.derivandi.api.dsl.annotation_usage.AnnotationUsageRenderable;
import com.derivandi.api.dsl.class_.ClassRenderable;
import com.derivandi.api.dsl.declared.DeclaredRenderable;
import com.derivandi.api.dsl.interface_.InterfaceGenericStep;
import com.derivandi.api.dsl.interface_.InterfaceRenderable;
import com.derivandi.api.dsl.method.MethodRenderable;
import com.derivandi.api.dsl.package_.PackageRenderable;
import com.derivandi.api.processor.Processor;
import com.derivandi.api.processor.ProcessorBuilder;
import com.derivandi.api.processor.ProcessorConfiguration;
import com.derivandi.api.processor.SimpleContext;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.derivandi.api.dsl.JavaDsl.*;
import static com.derivandi.api.dsl.RenderingContext.createRenderingContext;
import static java.lang.Character.toLowerCase;

@NotNullByDefault
public class TypesafeUsageGenerator
      extends Processor
{
   private static final String GENERATE_TYPESAFE_USAGE_NAME = "com.derivandi.api.generate.GenerateTypesafeUsage";

   private static final PackageRenderable DERIVANDI_PACKAGE = packageInfo().name("com.derivandi.api");
   private static final PackageRenderable JAVA_LANG_PACKAGE = packageInfo().name("java.lang");
   private static final InterfaceRenderable ANNOTATION_USAGE = interface_().package_(DERIVANDI_PACKAGE).name("D.AnnotationUsage");
   private static final AnnotationUsageRenderable OVERRIDE_USAGE = annotationUsage().type(annotation().package_(JAVA_LANG_PACKAGE)
                                                                                                      .name("Override"));
   private static final ClassRenderable STRING = class_().package_(JAVA_LANG_PACKAGE).name("String");

   @Override
   public ProcessorConfiguration buildProcessor(ProcessorBuilder processorBuilder)
   {
      return processorBuilder.process(TypesafeUsageGenerator::process);
   }

   private static void process(SimpleContext context)
   {
      for (D.Annotation annotated : getAnnotated(context))
      {
         context.writeAndCompileSourceFile(createTypeSafeUsage(annotated));
      }
   }

   private static Set<D.Annotation> getAnnotated(SimpleContext context)
   {
      return context.getAnnotation(GENERATE_TYPESAFE_USAGE_NAME)
                    .map(annotation ->
                         {
                            //noinspection unchecked
                            return context.getAnnotatedWith(annotation)
                                          .stream()
                                          .map(annotation1 -> annotation1.getUsageOfOrThrow(annotation))
                                          .map(annotationUsage -> annotationUsage.getValueOrThrow("value"))
                                          .map(annotationValue -> ((D.AnnotationValue.Values<D.AnnotationValue.TypeValue>) annotationValue))
                                          .map(D.AnnotationValue.Values::getValue)
                                          .flatMap(Collection::stream)
                                          .map(D.AnnotationValue.TypeValue::getValue)
                                          .filter(D.Annotation.class::isInstance)
                                          .map(D.Annotation.class::cast)
                                          .collect(Collectors.toSet());
                         }).orElse(Collections.emptySet());
   }

   public static ClassRenderable createTypeSafeUsage(D.Annotation annotation)
   {
      String fieldName = getFieldName(annotation);

      return class_().package_(annotation.getPackage())
                     .import_(JavaDsl.import_("java.util.Objects"))
                     .import_(JavaDsl.import_("com.derivandi.api.dsl.RenderingContext"))
                     .annotate(JavaDsl.generated(TypesafeUsageGenerator.class.getName()))
                     .public_().name(annotation.getName() + "TypesafeUsage")
                     .implements_(ANNOTATION_USAGE)
                     .field(field().private_()
                                   .static_()
                                   .final_()
                                   .type(STRING)
                                   .name("QUALIFIED_ANNOTATION_NAME")
                                   .initializer("\"" + annotation.getQualifiedName() + "\""))
                     .field(field().private_().final_().type(ANNOTATION_USAGE).name(fieldName))
                     .constructor(constructor().public_()
                                               .surroundingType()
                                               .parameter(parameter(ANNOTATION_USAGE, "usage"))
                                               .body("""
                                                     Objects.requireNonNull(usage);
                                                     
                                                     String qualifiedName = usage.getAnnotation().getQualifiedName();
                                                     
                                                     if (!QUALIFIED_ANNOTATION_NAME.equals(qualifiedName)) {
                                                     
                                                        throw new IllegalArgumentException("This Meta-Model represents a usage of \\"" +
                                                                                           QUALIFIED_ANNOTATION_NAME + "\\" not \\"" + qualifiedName + "\\"");
                                                     }
                                                     this.%s = usage;
                                                     """.formatted(fieldName)))
                     .method(createAccessors(annotation, fieldName))
                     .method(method().private_()
                                     .static_()
                                     .resultType(class_().package_(JAVA_LANG_PACKAGE).name("Class").genericUsage("?"))
                                     .name("resolveEnumName")
                                     .parameter(parameter(interface_().package_(DERIVANDI_PACKAGE).name("D.EnumConstant"), "enumConstant"))
                                     .body("""
                                           try {
                                              return Class.forName(enumConstant.getSurrounding().getBinaryName());
                                           }
                                           catch (ClassNotFoundException e) {
                                              throw new RuntimeException(e);
                                           }
                                           """))
                     .method(method().annotate(OVERRIDE_USAGE)
                                     .public_()
                                     .resultType(interface_().package_("java.util").name("Map").genericUsage("D.Method", "D.AnnotationValue"))
                                     .name("getValues")
                                     .body("return %s.getValues();".formatted(fieldName)))
                     .method(method().annotate(OVERRIDE_USAGE)
                                     .public_().resultType(interface_().package_(DERIVANDI_PACKAGE).name("D.Annotation")).name("getAnnotation")
                                     .body("return %s.getAnnotation();".formatted(fieldName)))
                     .method(method().annotate(OVERRIDE_USAGE)
                                     .public_()
                                     .resultType(interface_().package_(DERIVANDI_PACKAGE).name("Origin"))
                                     .name("getOrigin")
                                     .body("return %s.getOrigin();".formatted(fieldName)))
                     .method(method().annotate(OVERRIDE_USAGE)
                                     .public_().resultType(STRING).name("renderDeclaration").parameter("RenderingContext renderingContext")
                                     .body("return %s.renderDeclaration(renderingContext);".formatted(fieldName)));
   }

   private static String getFieldName(D.Annotation annotation)
   {
      String typeName = annotation.getName();
      return toLowerCase(typeName.charAt(0)) + typeName.substring(1);
   }

   private static List<MethodRenderable> createAccessors(D.Annotation annotation, String fieldName)
   {
      return annotation.getMethods().stream().flatMap(method -> Stream.of(accessorMethod(fieldName, method),
                                                                          valueMethod(fieldName, method))).toList();
   }

   private static MethodRenderable accessorMethod(String fieldName, D.Method method)
   {
      VariableTypeRenderable accessorType = createAccessorType(method.getResultType());
      if (method.getResultType() instanceof D.Array array)
      {
         String start = "return ((D.AnnotationValue.Values<?>) %s.getValueOrThrow(\"%s\")).getValue().stream()".formatted(fieldName, method.getName());
         return method().public_().resultType(accessorType)
                        .name(method.getName())
                        .body(start + listType(array.getComponentType()) + ".toList();");
      }
      if (method.getResultType() instanceof D.EnumConstant enumConstant)
      {
         return method().public_().resultType(accessorType)
                        .name(method.getName())
                        .body("""
                              D.EnumConstant enumConstant = (D.EnumConstant) %s.getValueOrThrow("%s").getValue();
                              return Enum.valueOf((Class<%s>) resolveEnumName(enumConstant), enumConstant.getName());
                              """.formatted(fieldName, method.getName(), enumConstant.getSurrounding().getName()));
      }
      return method().public_().resultType(accessorType)
                     .name(method.getName())
                     .body("return (%s) %s.getValueOrThrow(\"%s\").getValue();"
                                 .formatted(accessorType.renderType(createRenderingContext()), fieldName, method.getName()));
   }

   private static String listType(D.Type type)
   {
      return switch (type)
      {
         //@formatter:off
         case D.Class aClass when "java.lang.String".equals(aClass.getQualifiedName()) -> ".map(D.NestedAnnotationValue.StringValue.class::cast).map(D.NestedAnnotationValue.StringValue::getValue)";
         case D.Class aClass when "java.lang.Class".equals(aClass.getQualifiedName()) -> ".map(D.NestedAnnotationValue.TypeValue.class::cast).map(D.NestedAnnotationValue.TypeValue::getValue)";
         case D.boolean_ aBoolean -> ".map(D.NestedAnnotationValue.BooleanValue.class::cast).map(D.NestedAnnotationValue.BooleanValue::getValue)";
         case D.byte_ aByte -> ".map(D.NestedAnnotationValue.ByteValue.class::cast).map(D.NestedAnnotationValue.ByteValue::getValue)";
         case D.short_ aShort -> ".map(D.NestedAnnotationValue.ShortValue.class::cast).map(D.NestedAnnotationValue.ShortValue::getValue)";
         case D.int_ anInt -> ".map(D.NestedAnnotationValue.IntegerValue.class::cast).map(D.NestedAnnotationValue.IntegerValue::getValue)";
         case D.long_ aLong -> ".map(D.NestedAnnotationValue.LongValue.class::cast).map(D.NestedAnnotationValue.LongValue::getValue)";
         case D.char_ aChar -> ".map(D.NestedAnnotationValue.CharacterValue.class::cast).map(D.NestedAnnotationValue.CharacterValue::getValue)";
         case D.float_ aFloat -> ".map(D.NestedAnnotationValue.FloatValue.class::cast).map(D.NestedAnnotationValue.FloatValue::getValue)";
         case D.double_ aDouble -> ".map(D.NestedAnnotationValue.DoubleValue.class::cast).map(D.NestedAnnotationValue.DoubleValue::getValue)";
         case D.Enum anEnum ->".map(D.NestedAnnotationValue.EnumValue.class::cast).map(D.NestedAnnotationValue.EnumValue::getValue).map(enumConstant -> Enum.valueOf((Class<%s>) resolveEnumName(enumConstant), enumConstant.getName()))".formatted(anEnum.getName());
         case D.Annotation annotation ->".map(D.NestedAnnotationValue.AnnotationUsageValue.class::cast).map(D.NestedAnnotationValue.AnnotationUsageValue::getValue)";
         case D.Array array ->".map(o -> (D.AnnotationValue.Values<?>) o).flatMap(values -> values.getValue().stream())" + listType(array.getComponentType());
         default -> throw new IllegalStateException();
         //@formatter:on
      };
   }

   private static VariableTypeRenderable createAccessorType(D.Type returnType)
   {
      return switch (returnType)
      {
         //@formatter:off
         case D.Class aClass when "java.lang.String".equals(aClass.getQualifiedName()) -> class_().package_(JAVA_LANG_PACKAGE).name("String");
         case D.Class aClass when "java.lang.Class".equals(aClass.getQualifiedName()) -> interface_().package_(DERIVANDI_PACKAGE).name("D.AnnotationValue.TypeValue");
         case D.boolean_ aBoolean -> class_().noPackage().name("boolean");
         case D.byte_ aByte -> class_().noPackage().name("byte");
         case D.short_ aShort -> class_().noPackage().name("short");
         case D.int_ anInt -> class_().noPackage().name("int");
         case D.long_ aLong -> class_().noPackage().name("long");
         case D.char_ aChar -> class_().noPackage().name("char");
         case D.float_ aFloat -> class_().noPackage().name("float");
         case D.double_ aDouble -> class_().noPackage().name("double");
         case D.Enum anEnum -> anEnum;
         case D.Annotation annotation -> annotation;
         case D.Array array -> interface_().package_("java.util").name("List").genericUsage(createAccessorType(array.getComponentType()));
         default -> throw new IllegalStateException();
         //@formatter:on
      };
   }

   private static MethodRenderable valueMethod(String fieldName, D.Method method)
   {
      DeclaredRenderable accessorType = createValueAccessorType(method.getResultType());

      return method().public_().resultType(accessorType)
                     .name(method.getName() + "Value")
                     .body("return (%s) %s.getValueOrThrow(\"%s\");"
                                 .formatted(accessorType.renderSimpleName(createRenderingContext()), fieldName, method.getName()));
   }

   private static DeclaredRenderable createValueAccessorType(D.Type returnType)
   {
      InterfaceGenericStep genericStep =
            interface_().package_(DERIVANDI_PACKAGE)
                        .name(switch (returnType)
                              {
                                 case D.Class aClass when "java.lang.String".equals(aClass.getQualifiedName()) ->
                                       "D.AnnotationValue.StringValue";
                                 case D.Class aClass when "java.lang.Class".equals(aClass.getQualifiedName()) -> "D.AnnotationValue.TypeValue";
                                 case D.boolean_ aBoolean -> "D.AnnotationValue.BooleanValue";
                                 case D.byte_ aByte -> "D.AnnotationValue.ByteValue";
                                 case D.short_ aShort -> "D.AnnotationValue.ShortValue";
                                 case D.int_ anInt -> "D.AnnotationValue.IntegerValue";
                                 case D.long_ aLong -> "D.AnnotationValue.LongValue";
                                 case D.char_ aChar -> "D.AnnotationValue.CharacterValue";
                                 case D.float_ aFloat -> "D.AnnotationValue.FloatValue";
                                 case D.double_ aDouble -> "D.AnnotationValue.DoubleValue";
                                 case D.Enum anEnum -> "D.AnnotationValue.EnumValue";
                                 case D.Annotation annotation -> "D.AnnotationValue.AnnotationUsageValue";
                                 case D.Array array -> "D.AnnotationValue.Values";
                                 default -> throw new IllegalStateException();
                              });
      if (!(returnType instanceof D.Array array))
      {
         return genericStep;
      }
      return genericStep.genericUsage(createValueAccessorType(array.getComponentType()));
   }
}