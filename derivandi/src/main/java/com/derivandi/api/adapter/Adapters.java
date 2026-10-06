package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.api.Origin;
import com.derivandi.api.processor.SimpleContext;
import com.derivandi.internal.annotationvalue.AnnotationUsageImpl;
import com.derivandi.internal.annotationvalue.AnnotationValueImpl;
import com.derivandi.internal.directive.*;
import com.derivandi.internal.structure.*;
import com.derivandi.internal.type.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.AbstractAnnotationValueVisitor14;
import javax.lang.model.util.Elements;
import java.util.List;

import static java.util.Optional.ofNullable;


/**
 * If supporting new features in the underlying JDK API requires additional information, this API might break.
 */
public interface Adapters
{
   //derivandi -> jdk

   static AnnotationUsageAdapter adapt(D.AnnotationUsage annotationUsage)
   {
      return new AnnotationUsageAdapter(annotationUsage);
   }

   static AnnotationValueAdapter adapt(D.AnnotationValue annotationValue)
   {
      return new AnnotationValueAdapter(annotationValue);
   }

   static DeclaredAdapter adapt(D.Declared declared)
   {
      return new DeclaredAdapter(declared);
   }

   static ArrayAdapter adapt(D.Array array)
   {
      return new ArrayAdapter(array);
   }

   static ExecutableAdapter adapt(D.Executable executable)
   {
      return new ExecutableAdapter(executable);
   }

   static TypeAdapter adapt(D.Type type)
   {
      return new TypeAdapter(type);
   }

   static GenericAdapter adapt(D.Generic generic)
   {
      return new GenericAdapter(generic);
   }

   static ModuleAdapter adapt(D.Module module)
   {
      return new ModuleAdapter(module);
   }

   static NullAdapter adapt(D.Null aNull)
   {
      return new NullAdapter(aNull);
   }

   static PackageAdapter adapt(D.Package aPackage)
   {
      return new PackageAdapter(aPackage);
   }

   static PrimitiveAdapter adapt(D.Primitive primitive)
   {
      return new PrimitiveAdapter(primitive);
   }

   static VoidAdapter adapt(D.Void aVoid)
   {
      return new VoidAdapter(aVoid);
   }

   static WildcardAdapter adapt(D.Wildcard wildcard)
   {
      return new WildcardAdapter(wildcard);
   }

   static AnnotationableAdapter adapt(D.Annotationable annotationable)
   {
      return new AnnotationableAdapter(annotationable);
   }

   static RecordComponentAdapter adapt(D.RecordComponent recordComponent)
   {
      return new RecordComponentAdapter(recordComponent);
   }

   static VariableAdapter adapt(D.Variable variable)
   {
      return new VariableAdapter(variable);
   }

   static ExportsAdapter adapt(D.Exports exports)
   {
      return new ExportsAdapter(exports);
   }

   static OpensAdapter adapt(D.Opens opens)
   {
      return new OpensAdapter(opens);
   }

   static ProvidesAdapter adapt(D.Provides provides)
   {
      return new ProvidesAdapter(provides);
   }

   static RequiresAdapter adapt(D.Requires requires)
   {
      return new RequiresAdapter(requires);
   }

   static UsesAdapter adapt(D.Uses uses)
   {
      return new UsesAdapter(uses);
   }

   //jdk -> derivandi

   static D.Executable adapt(SimpleContext context, ExecutableElement element)
   {
      return switch (element.getKind())
      {
         case METHOD -> new MethodImpl(context, element);
         case CONSTRUCTOR -> new ConstructorImpl(context, element);
         default -> throw new IllegalStateException(
               "javax.lang.model.element.ExecutableElement that is nether a METHOD, FIELD or CONSTRUCTOR\n" + element);
      };
   }

   static D.Variable adapt(SimpleContext context, VariableElement element)
   {
      return switch (element.getKind())
      {
         case ENUM_CONSTANT -> new EnumConstantImpl(context, element);
         case FIELD -> new FieldImpl(context, element);
         case PARAMETER -> new ParameterImpl(context, element);
         default -> throw new IllegalStateException(
               "javax.lang.model.element.VariableElement that is nether a ENUM_CONSTANT, FIELD or PARAMETER\n" + element);
      };
   }

   static D.RecordComponent adapt(SimpleContext context, RecordComponentElement recordComponentElement)
   {
      return new RecordComponentImpl(context, recordComponentElement);
   }

   static D.Module adapt(SimpleContext context, ModuleElement moduleElement)
   {
      return new ModuleImpl(context, moduleElement);
   }

   static D.Wildcard adapt(SimpleContext context, WildcardType wildcardType)
   {
      return new WildcardImpl(context, wildcardType);
   }

   static D.Declared adapt(SimpleContext context, TypeElement typeElement)
   {
      return switch (typeElement.getKind())
      {
         case ENUM -> new EnumImpl(context, typeElement);
         case CLASS -> new ClassImpl(context, typeElement);
         case ANNOTATION_TYPE -> new AnnotationImpl(context, typeElement);
         case INTERFACE -> new InterfaceImpl(context, typeElement);
         case RECORD -> new RecordImpl(context, typeElement);
         default -> throw new IllegalStateException(
               "javax.lang.model.element.TypeElement that is nether a ENUM, CLASS, ANNOTATION_TYPE, INTERFACE or RECORD\n" + typeElement);
      };
   }

   static D.Generic adapt(SimpleContext context, TypeParameterElement typeParameterElement)
   {
      return new GenericImpl(context, typeParameterElement);
   }

   /// This Method constructs [Object]s based on [Element]s. They represent a specific usage. `List<String>` for example.
   /// If you want to represent the abstract code like `List<T>` then use [TypeMirror]s.
   static Object adapt(SimpleContext context, Element element)
   {
      return switch (element.getKind())
      {
         case PACKAGE -> adapt(context, (PackageElement) element);
         case ENUM, RECORD, ANNOTATION_TYPE, CLASS, INTERFACE -> adapt(context, (TypeElement) element);
         case TYPE_PARAMETER -> adapt(context, (TypeParameterElement) element);
         case MODULE -> adapt(context, (ModuleElement) element);
         case RECORD_COMPONENT -> adapt(context, (RecordComponentElement) element);
         case METHOD, CONSTRUCTOR -> adapt(context, ((ExecutableElement) element));
         case PARAMETER, FIELD, ENUM_CONSTANT -> adapt(context, ((VariableElement) element));
         case OTHER, STATIC_INIT, INSTANCE_INIT, EXCEPTION_PARAMETER, RESOURCE_VARIABLE,
              BINDING_VARIABLE, LOCAL_VARIABLE ->
               throw new IllegalArgumentException(element.getKind() + " is not exposed through annotation processing and not supported");
      };
   }

   /// This Method constructs [D.Type]s based on [TypeMirror]s. They represent the abstract code. `List<T>` for example.
   /// If you want a specific usage like `List<String>` then you should use the specific [Element].
   ///
   /// [TypeKind#EXECUTABLE], [TypeKind#NONE], [TypeKind#PACKAGE], [TypeKind#MODULE] can not be used with this method as information would be
   /// missing, that is needed for there creation. Use [Element]s to create them.
   static D.Type adapt(SimpleContext context, TypeMirror typeMirror)
   {
      return switch (typeMirror.getKind())
      {
         case BOOLEAN, DOUBLE, BYTE, SHORT, INT, LONG, CHAR, FLOAT -> adapt(context, (PrimitiveType) typeMirror);
         case ARRAY -> adapt(context, (ArrayType) typeMirror);
         case DECLARED -> adapt(context, ((DeclaredType) typeMirror));
         case WILDCARD -> adapt(context, (WildcardType) typeMirror);
         case VOID -> adapt(context, ((NoType) typeMirror));
         case NULL -> adapt(context, (NullType) typeMirror);
         case TYPEVAR -> adapt(context, ((TypeVariable) typeMirror));
         case INTERSECTION -> adapt(context, ((IntersectionType) typeMirror));
         case ERROR -> adapt(context, ((ErrorType) typeMirror));
         case EXECUTABLE, NONE, PACKAGE, MODULE -> throw new IllegalArgumentException(
               "for " +
               typeMirror.getKind() +
               " use Ap.Adapters.adapt(LM_Context, javax.lang.model.element.Element) as they don't extend " + D.Type.class.getSimpleName());
         case OTHER, UNION ->
               throw new IllegalArgumentException(typeMirror.getKind() + " is not exposed through annotation processing and not supported");
      };
   }

   static D.Declared adapt(SimpleContext context, DeclaredType declaredType)
   {
      if (TypeKind.ERROR.equals(declaredType.getKind()))
      {
         return adapt(context, ((ErrorType) declaredType));
      }

      return switch (adapt(context).toTypes().asElement(declaredType).getKind())
      {
         case ENUM -> new EnumImpl(context, declaredType);
         case CLASS -> new ClassImpl(context, declaredType);
         case ANNOTATION_TYPE -> new AnnotationImpl(context, declaredType);
         case INTERFACE -> new InterfaceImpl(context, declaredType);
         case RECORD -> new RecordImpl(context, declaredType);
         default -> throw new IllegalStateException(
               "javax.lang.model.type.DeclaredType that is nether a ENUM, CLASS, ANNOTATION_TYPE, INTERFACE or RECORD\n" + declaredType);
      };
   }

   static D.Unresolved adapt(SimpleContext context, ErrorType errorType)
   {
      return new UnresolvedImpl(context, errorType);
   }

   /// used to create [D.Void].
   /// For [TypeKind#PACKAGE] use [#adapt(SimpleContext , PackageElement)] and for [TypeKind#MODULE] use [#adapt(SimpleContext , ModuleElement)].
   /// javax.lang.model has no specific type that represents [Void], this api can therefore not be more specific.
   static D.Void adapt(SimpleContext context, NoType noType)
   {
      if (!TypeKind.VOID.equals(noType.getKind()))
      {
         throw new IllegalArgumentException(
               "For javax.lang.model.type.NoType other then Void use LM_Adapters.adapt(LM_Context, Element/ModuleElement/PackageElement)");
      }
      return new VoidImpl(context, noType);
   }

   static D.Generic adapt(SimpleContext context, IntersectionType intersectionType)
   {
      return new IntersectionImpl(context, intersectionType);
   }

   static D.Generic adapt(SimpleContext context, TypeVariable typeVariable)
   {
      return new GenericImpl(context, typeVariable);
   }

   static D.Null adapt(SimpleContext context, NullType nullType)
   {
      return new NullImpl(context, nullType);
   }

   static D.Array adapt(SimpleContext context, ArrayType arrayType)
   {
      return new ArrayImpl(context, arrayType);
   }

   static D.Primitive adapt(SimpleContext context, PrimitiveType primitiveType)
   {
      return switch (primitiveType.getKind())
      {
         case BOOLEAN -> new PrimitiveImpl.LM_booleanImpl(context, primitiveType);
         case BYTE -> new PrimitiveImpl.LM_byteImpl(context, primitiveType);
         case SHORT -> new PrimitiveImpl.LM_shortImpl(context, primitiveType);
         case INT -> new PrimitiveImpl.LM_intImpl(context, primitiveType);
         case LONG -> new PrimitiveImpl.LM_longImpl(context, primitiveType);
         case CHAR -> new PrimitiveImpl.LM_charImpl(context, primitiveType);
         case FLOAT -> new PrimitiveImpl.LM_floatImpl(context, primitiveType);
         case DOUBLE -> new PrimitiveImpl.LM_doubleImpl(context, primitiveType);
         default -> throw new IllegalArgumentException(primitiveType.getClass().getName() +
                                                       " that is nether a BOOLEAN, BYTE, SHORT, INT, LONG, CHAR, FLOAT or DOUBLE\n" +
                                                       primitiveType);
      };
   }

   static D.Package adapt(SimpleContext context, PackageElement packageElement)
   {
      return new PackageImpl(context, packageElement);
   }

   static D.AnnotationUsage adapt(SimpleContext context, @Nullable AnnotatedConstruct annotated, AnnotationMirror annotationMirror)
   {
      return new AnnotationUsageImpl(context, annotated, annotationMirror);
   }

   static List<D.AnnotationUsage> adapt(SimpleContext context,
                                        @Nullable AnnotatedConstruct annotated,
                                        List<? extends AnnotationMirror> annotationMirrors)
   {
      return annotationMirrors.stream()
                              .map(annotationMirror -> new AnnotationUsageImpl(context, annotated, annotationMirror))
                              .map(D.AnnotationUsage.class::cast)
                              .toList();
   }

   static D.Directive adapt(SimpleContext context, ModuleElement declaringModule, ModuleElement.Directive directive)
   {
      return switch (directive.getKind())
      {
         case REQUIRES -> adapt(context, declaringModule, ((ModuleElement.RequiresDirective) directive));
         case EXPORTS -> adapt(context, declaringModule, ((ModuleElement.ExportsDirective) directive));
         case OPENS -> adapt(context, declaringModule, ((ModuleElement.OpensDirective) directive));
         case USES -> adapt(context, declaringModule, ((ModuleElement.UsesDirective) directive));
         case PROVIDES -> adapt(context, declaringModule, ((ModuleElement.ProvidesDirective) directive));
      };
   }

   static D.Exports adapt(SimpleContext context, ModuleElement declaringModule, ModuleElement.ExportsDirective exportsDirective)
   {
      return new ExportsImpl(context, declaringModule, exportsDirective);
   }

   static D.Opens adapt(SimpleContext context, ModuleElement declaringModule, ModuleElement.OpensDirective opensDirective)
   {
      return new OpensImpl(context, declaringModule, opensDirective);
   }

   static D.Provides adapt(SimpleContext context, ModuleElement declaringModule, ModuleElement.ProvidesDirective providesDirective)
   {
      return new ProvidesImpl(context, declaringModule, providesDirective);
   }

   static D.Requires adapt(SimpleContext context, ModuleElement declaringModule, ModuleElement.RequiresDirective requiresDirective)
   {
      return new RequiresImpl(context, declaringModule, requiresDirective);
   }

   static D.Uses adapt(SimpleContext context, ModuleElement declaringModule, ModuleElement.UsesDirective usesDirective)
   {
      return new UsesImpl(context, declaringModule, usesDirective);
   }

   ///
   /// @param value usualy from [javax.lang.model.element.AnnotationMirror#getElementValues]
   /// @param valueOrDefault usualy from [javax.lang.model.util.Elements#getElementValuesWithDefaults]
   static D.AnnotationValue adapt(SimpleContext context,
                                  @Nullable AnnotationValue value,
                                  @NotNull AnnotationValue valueOrDefault)
   {
      return valueOrDefault.accept(new AbstractAnnotationValueVisitor14<D.AnnotationValue, Void>()
      {
         @Override
         public D.AnnotationValue visitBoolean(boolean b, Void unused)
         {
            return new AnnotationValueImpl.BooleanValueImpl(context,
                                                            value,
                                                            ofNullable(value).map(javax.lang.model.element.AnnotationValue::getValue)
                                                                             .map(Boolean.class::cast)
                                                                             .orElse(null),
                                                            valueOrDefault,
                                                            b);
         }

         @Override
         public D.AnnotationValue visitByte(byte b, Void unused)
         {
            return new AnnotationValueImpl.ByteValueImpl(context,
                                                         value,
                                                         ofNullable(value).map(javax.lang.model.element.AnnotationValue::getValue)
                                                                          .map(Byte.class::cast)
                                                                          .orElse(null),
                                                         valueOrDefault,
                                                         b);
         }

         @Override
         public D.AnnotationValue visitChar(char c, Void unused)
         {
            return new AnnotationValueImpl.CharacterValueImpl(context,
                                                              value,
                                                              ofNullable(value).map(javax.lang.model.element.AnnotationValue::getValue)
                                                                               .map(Character.class::cast)
                                                                               .orElse(null),
                                                              valueOrDefault,
                                                              c);
         }

         @Override
         public D.AnnotationValue visitDouble(double d, Void unused)
         {
            return new AnnotationValueImpl.DoubleValueImpl(context,
                                                           value,
                                                           ofNullable(value).map(javax.lang.model.element.AnnotationValue::getValue)
                                                                            .map(Double.class::cast)
                                                                            .orElse(null),
                                                           valueOrDefault,
                                                           d);
         }

         @Override
         public D.AnnotationValue visitFloat(float f, Void unused)
         {
            return new AnnotationValueImpl.FloatValueImpl(context,
                                                          value,
                                                          ofNullable(value).map(javax.lang.model.element.AnnotationValue::getValue)
                                                                           .map(Float.class::cast)
                                                                           .orElse(null),
                                                          valueOrDefault,
                                                          f);
         }

         @Override
         public D.AnnotationValue visitInt(int i, Void unused)
         {
            return new AnnotationValueImpl.IntegerValueImpl(context,
                                                            value,
                                                            ofNullable(value).map(javax.lang.model.element.AnnotationValue::getValue)
                                                                             .map(Integer.class::cast)
                                                                             .orElse(null),
                                                            valueOrDefault,
                                                            i);
         }

         @Override
         public D.AnnotationValue visitLong(long i, Void unused)
         {
            return new AnnotationValueImpl.LongValueImpl(context,
                                                         value,
                                                         ofNullable(value).map(javax.lang.model.element.AnnotationValue::getValue)
                                                                          .map(Long.class::cast)
                                                                          .orElse(null),
                                                         valueOrDefault,
                                                         i);
         }

         @Override
         public D.AnnotationValue visitShort(short s, Void unused)
         {
            return new AnnotationValueImpl.ShortValueImpl(context,
                                                          value,
                                                          ofNullable(value).map(javax.lang.model.element.AnnotationValue::getValue)
                                                                           .map(Short.class::cast)
                                                                           .orElse(null),
                                                          valueOrDefault,
                                                          s);
         }

         @Override
         public D.AnnotationValue visitString(String s, Void unused)
         {
            return new AnnotationValueImpl.StringValueImpl(context,
                                                           value,
                                                           ofNullable(value).map(javax.lang.model.element.AnnotationValue::getValue)
                                                                            .map(String.class::cast)
                                                                            .orElse(null),
                                                           valueOrDefault,
                                                           s);
         }

         @Override
         public D.AnnotationValue visitType(TypeMirror t, Void unused)
         {
            return new AnnotationValueImpl.TypeValueImpl(context,
                                                         value,
                                                         ofNullable(value).map(javax.lang.model.element.AnnotationValue::getValue)
                                                                          .map(TypeMirror.class::cast)
                                                                          .map(typeMirror -> adapt(context, typeMirror))
                                                                          .orElse(null),
                                                         valueOrDefault,
                                                         adapt(context, t));
         }

         @Override
         public D.AnnotationValue visitEnumConstant(VariableElement c, Void unused)
         {
            return new AnnotationValueImpl.EnumValueImpl(context,
                                                         value,
                                                         ofNullable(value).map(AnnotationValue::getValue)
                                                                          .map(VariableElement.class::cast)
                                                                          .map(variableElement -> adapt(context, variableElement))
                                                                          .map(D.EnumConstant.class::cast)
                                                                          .orElse(null),
                                                         valueOrDefault,
                                                         ((D.EnumConstant) adapt(context, c)));
         }

         @Override
         public D.AnnotationValue visitAnnotation(AnnotationMirror a, Void unused)
         {
            return new AnnotationValueImpl.AnnotationUsageValueImpl(context,
                                                                    value,
                                                                    ofNullable(value).map(javax.lang.model.element.AnnotationValue::getValue)
                                                                                     .map(AnnotationMirror.class::cast)
                                                                                     .map(annotationMirror -> adapt(context,
                                                                                                                    null,
                                                                                                                    annotationMirror))
                                                                                     .orElse(null),
                                                                    valueOrDefault,
                                                                    adapt(context, null, a));
         }

         @Override
         public D.AnnotationValue visitArray(List<? extends AnnotationValue> vals, Void unused)
         {
            return new AnnotationValueImpl.ValuesImpl<>(context,
                                                        value,
                                                        ofNullable(value).map(AnnotationValue::getValue)
                                                                         .map(obj ->
                                                                              {
                                                                                 //noinspection unchecked
                                                                                 return ((List<AnnotationValue>) obj);
                                                                              })
                                                                         .map(annotationValues ->
                                                                                    annotationValues.stream()
                                                                                                    .map(annotationValue ->
                                                                                                               adapt(context,
                                                                                                                     null,
                                                                                                                     annotationValue))
                                                                                                    .map(D.NestedAnnotationValue.class::cast)
                                                                                                    .toList())
                                                                         .orElse(null),
                                                        valueOrDefault,
                                                        vals.stream()
                                                            .map(annotationValue ->
                                                                       adapt(context,
                                                                             null,
                                                                             annotationValue))
                                                            .map(D.NestedAnnotationValue.class::cast)
                                                            .toList());
         }
      }, null);
   }

   static Origin adapt(Elements.Origin origin)
   {
      return switch (origin)
      {
         case EXPLICIT -> Origin.SOURCE_DECLARED;
         case MANDATED -> Origin.LANGUAGE_REQUIRED;
         case SYNTHETIC -> Origin.COMPILER_GENERATED;
      };
   }

   static ContextAdapter adapt(SimpleContext context)
   {
      return new ContextAdapter(context);
   }
}
