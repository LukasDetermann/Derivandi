package com.derivandi.api;

import com.derivandi.api.dsl.ReferenceTypeRenderable;
import com.derivandi.api.dsl.TypeRenderable;
import com.derivandi.api.dsl.VariableTypeRenderable;
import com.derivandi.api.dsl.annotation.AnnotationRenderable;
import com.derivandi.api.dsl.annotation_usage.AnnotationUsageRenderable;
import com.derivandi.api.dsl.annotation_value.AnnotationValueRenderable;
import com.derivandi.api.dsl.array.ArrayRenderable;
import com.derivandi.api.dsl.class_.ClassRenderable;
import com.derivandi.api.dsl.constructor.ConstructorRenderable;
import com.derivandi.api.dsl.declared.DeclaredRenderable;
import com.derivandi.api.dsl.enum_.EnumRenderable;
import com.derivandi.api.dsl.enum_constant.EnumConstantRenderable;
import com.derivandi.api.dsl.exports.ExportsRenderable;
import com.derivandi.api.dsl.field.FieldRenderable;
import com.derivandi.api.dsl.generic.GenericRenderable;
import com.derivandi.api.dsl.interface_.InterfaceRenderable;
import com.derivandi.api.dsl.method.MethodRenderable;
import com.derivandi.api.dsl.module.ModuleRenderable;
import com.derivandi.api.dsl.opens.OpensRenderable;
import com.derivandi.api.dsl.package_.PackageRenderable;
import com.derivandi.api.dsl.parameter.ParameterRenderable;
import com.derivandi.api.dsl.provides.ProvidesRenderable;
import com.derivandi.api.dsl.receiver.ReceiverRenderable;
import com.derivandi.api.dsl.record.RecordRenderable;
import com.derivandi.api.dsl.record_component.RecordComponentRenderable;
import com.derivandi.api.dsl.requires.RequiresRenderable;
import com.derivandi.api.dsl.result.ResultRenderable;
import com.derivandi.api.dsl.uses.UsesRenderable;
import com.derivandi.api.processor.SimpleContext;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.util.*;

import static com.derivandi.api.Modifier.*;

public interface D
{
   /// anything that can be annotated
   interface Annotationable
   {
      /// returns all annotations. Annotations on parentClasses are included when they are annotated with {@link java.lang.annotation.Inherited}
      List<AnnotationUsage> getAnnotationUsages();

      default List<AnnotationUsage> getUsagesOf(Annotation annotation)
      {
         return getAnnotationUsages().stream()
                                     .filter(usage -> usage.getAnnotation().equals(annotation))
                                     .toList();
      }

      default List<AnnotationUsage> getUsagesOf(String annotation)
      {
         return getAnnotationUsages().stream()
                                     .filter(usage -> usage.getAnnotation().getQualifiedName().equals(annotation))
                                     .toList();
      }

      default Optional<AnnotationUsage> getUsageOf(Annotation annotation)
      {
         List<AnnotationUsage> usages = getUsagesOf(annotation);

         if (usages.isEmpty())
         {
            return Optional.empty();
         }
         if (usages.size() == 1)
         {
            return Optional.of(usages.get(0));
         }
         throw new IllegalArgumentException();
      }

      default Optional<AnnotationUsage> getUsageOf(String annotation)
      {
         List<AnnotationUsage> usages = getUsagesOf(annotation);

         if (usages.isEmpty())
         {
            return Optional.empty();
         }
         if (usages.size() == 1)
         {
            return Optional.of(usages.get(0));
         }
         throw new IllegalArgumentException();
      }

      default AnnotationUsage getUsageOfOrThrow(Annotation annotation)
      {
         return getUsageOf(annotation).orElseThrow(IllegalArgumentException::new);
      }

      default AnnotationUsage getUsageOfOrThrow(String annotation)
      {
         return getUsageOf(annotation).orElseThrow(IllegalArgumentException::new);
      }

      default boolean isAnnotatedWith(Annotation annotation)
      {
         return getAnnotationUsages().stream()
                                     .map(AnnotationUsage::getAnnotation)
                                     .anyMatch(annotation1 -> annotation1.equals(annotation));
      }

      default boolean isAnnotatedWith(String annotation)
      {
         return getAnnotationUsages().stream()
                                     .map(AnnotationUsage::getAnnotation)
                                     .anyMatch(annotation1 -> annotation1.getQualifiedName().equals(annotation));
      }

      /// returns all direkt annotations
      ///
      /// @see #getAnnotationUsages()
      List<AnnotationUsage> getDirectAnnotationUsages();

      default List<AnnotationUsage> getDirectUsagesOf(Annotation annotation)
      {
         return getDirectAnnotationUsages().stream()
                                           .filter(usage -> usage.getAnnotation().equals(annotation))
                                           .toList();
      }

      default List<AnnotationUsage> getDirectUsagesOf(String annotation)
      {
         return getDirectAnnotationUsages().stream()
                                           .filter(usage -> usage.getAnnotation().getQualifiedName().equals(annotation))
                                           .toList();
      }

      default Optional<AnnotationUsage> getDirectUsageOf(Annotation annotation)
      {
         return getDirectAnnotationUsages().stream()
                                           .filter(usage -> usage.getAnnotation().equals(annotation))
                                           .findAny();
      }

      default Optional<AnnotationUsage> getDirectUsageOf(String annotation)
      {
         return getDirectAnnotationUsages().stream()
                                           .filter(usage -> usage.getAnnotation().getQualifiedName().equals(annotation))
                                           .findAny();
      }

      default AnnotationUsage getDirectUsageOfOrThrow(Annotation annotation)
      {
         return getDirectUsageOf(annotation).orElseThrow();
      }

      default AnnotationUsage getDirectUsageOfOrThrow(String annotation)
      {
         return getDirectUsageOf(annotation).orElseThrow();
      }

      default boolean isDirectlyAnnotatedWith(Annotation annotation)
      {
         return getDirectAnnotationUsages().stream()
                                           .map(AnnotationUsage::getAnnotation)
                                           .anyMatch(annotation1 -> annotation1.equals(annotation));
      }

      default boolean isDirectlyAnnotatedWith(String annotation)
      {
         return getDirectAnnotationUsages().stream()
                                           .map(AnnotationUsage::getAnnotation)
                                           .anyMatch(annotation1 -> annotation1.getQualifiedName().equals(annotation));
      }
   }

   /// {@link Annotation} represents the java file for the java concept of an annotation. This on the other hand represents
   /// a usage of such an annotation. like <br>
   /// {@code @Documented("testValue) public class Test{ }}
   interface AnnotationUsage
         extends AnnotationUsageRenderable
   {
      Map<Method, AnnotationValue> getValues();

      default AnnotationValue getValueOrThrow(String methodName)
      {
         return getValue(methodName).orElseThrow();
      }

      default Optional<AnnotationValue> getValue(String methodName)
      {
         return getValues().entrySet()
                           .stream()
                           .filter(entry -> entry.getKey().getName().equals(methodName))
                           .map(Map.Entry::getValue)
                           .findAny();
      }

      Origin getOrigin();

      Annotation getAnnotation();
   }

   /// Lets say an Annotation has a List<String> as its value.
   /// That would be moddled as `D.AnnotationValue.Values<D.NestedAnnotationValue.StringValue>`
   /// The List has may have a default Value. The Strings do not.
   /// This Type represents the Strings.
   sealed interface NestedAnnotationValue
         extends AnnotationValueRenderable
   {
      Object getValue();

      non-sealed interface StringValue
            extends NestedAnnotationValue
      {
         @Override
         String getValue();
         String getDefaultValue();
      }

      non-sealed interface BooleanValue
            extends NestedAnnotationValue
      {
         @Override
         Boolean getValue();
      }

      non-sealed interface ByteValue
            extends NestedAnnotationValue
      {
         @Override
         Byte getValue();
      }

      non-sealed interface ShortValue
            extends NestedAnnotationValue
      {
         @Override
         Short getValue();
      }

      non-sealed interface IntegerValue
            extends NestedAnnotationValue
      {
         @Override
         Integer getValue();
      }

      non-sealed interface LongValue
            extends NestedAnnotationValue
      {
         @Override
         Long getValue();
      }

      non-sealed interface CharacterValue
            extends NestedAnnotationValue
      {
         @Override
         Character getValue();
      }

      non-sealed interface FloatValue
            extends NestedAnnotationValue
      {
         @Override
         Float getValue();
      }

      non-sealed interface DoubleValue
            extends NestedAnnotationValue
      {
         @Override
         Double getValue();
      }

      non-sealed interface TypeValue
            extends NestedAnnotationValue
      {
         @Override
         Type getValue();
      }

      non-sealed interface EnumValue
            extends NestedAnnotationValue
      {
         @Override
         EnumConstant getValue();
      }

      non-sealed interface AnnotationUsageValue
            extends NestedAnnotationValue
      {
         @Override
         AnnotationUsage getValue();
      }

      non-sealed interface Values<T extends NestedAnnotationValue>
            extends NestedAnnotationValue
      {
         @Override
         List<T> getValue();
      }
   }

   sealed interface AnnotationValue
         extends AnnotationValueRenderable
   {
      Object getValue();

      /// is this the default value specified in the annotation?
      boolean isDefault();

      Object getDefaultValue();

      non-sealed interface StringValue
            extends AnnotationValue,
                    NestedAnnotationValue.StringValue
      {
         @Override
         String getValue();

         @Override
         String getDefaultValue();
      }

      non-sealed interface BooleanValue
            extends AnnotationValue,
                    NestedAnnotationValue.BooleanValue
      {
         @Override
         Boolean getValue();

         @Override
         Boolean getDefaultValue();
      }

      non-sealed interface ByteValue
            extends AnnotationValue,
                    NestedAnnotationValue.ByteValue
      {
         @Override
         Byte getValue();

         @Override
         Byte getDefaultValue();
      }

      non-sealed interface ShortValue
            extends AnnotationValue,
                    NestedAnnotationValue.ShortValue
      {
         @Override
         Short getValue();

         @Override
         Short getDefaultValue();
      }

      non-sealed interface IntegerValue
            extends AnnotationValue,
                    NestedAnnotationValue.IntegerValue
      {
         @Override
         Integer getValue();

         @Override
         Integer getDefaultValue();
      }

      non-sealed interface LongValue
            extends AnnotationValue,
                    NestedAnnotationValue.LongValue
      {
         @Override
         Long getValue();

         @Override
         Long getDefaultValue();
      }

      non-sealed interface CharacterValue
            extends AnnotationValue,
                    NestedAnnotationValue.CharacterValue
      {
         @Override
         Character getValue();

         @Override
         Character getDefaultValue();
      }

      non-sealed interface FloatValue
            extends AnnotationValue,
                    NestedAnnotationValue.FloatValue
      {
         @Override
         Float getValue();

         @Override
         Float getDefaultValue();
      }

      non-sealed interface DoubleValue
            extends AnnotationValue,
                    NestedAnnotationValue.DoubleValue
      {
         @Override
         Double getValue();

         @Override
         Double getDefaultValue();
      }

      non-sealed interface TypeValue
            extends AnnotationValue,
                    NestedAnnotationValue.TypeValue
      {
         @Override
         Type getValue();

         @Override
         Type getDefaultValue();
      }

      non-sealed interface EnumValue
            extends AnnotationValue,
                    NestedAnnotationValue.EnumValue
      {
         @Override
         EnumConstant getValue();

         @Override
         EnumConstant getDefaultValue();
      }

      non-sealed interface AnnotationUsageValue
            extends AnnotationValue,
                    NestedAnnotationValue.AnnotationUsageValue
      {
         @Override
         AnnotationUsage getValue();

         @Override
         AnnotationUsage getDefaultValue();
      }

      non-sealed interface Values<T extends NestedAnnotationValue>
            extends AnnotationValue,
                    NestedAnnotationValue.Values<T>
      {
         @Override
         List<T> getValue();

         @Override
         List<T> getDefaultValue();
      }
   }

   interface Documented
   {
      Optional<String> getJavaDoc();
   }

   interface Erasable
   {
      /// Information regarding generics is lost after the compilation. For Example {@code List<String>} becomes {@code List}. This method Does the same.
      /// This can be useful if you want to check if a derivandi implements for example {@link java.util.Collection}
      /// {@code typeToTest.erasure().isSubtypeOf(context.getDeclaredOrThrow("java.util.Collection").erasure())}
      /// <p>
      /// for {@link Class}s this means for example {@code class MyClass<T>{}} -&gt; {@code class MyClass{}}
      Erasable erasure();
   }

   interface ModuleEnclosed
   {
      Module getModule();
   }

   interface Nameable
   {
      String getName();
   }

   interface QualifiedNameable
   {
      /**
       * a Qualified name is {@code javax.lang.model.element.QualifiedNameable}
       */
      String getQualifiedName();
   }

   non-sealed interface Annotation
         extends Declared,
                 StaticModifiable,
                 AnnotationRenderable {}

   non-sealed interface Array
         extends Erasable,
                 ReferenceType,
                 ArrayRenderable
   {
      /**
       * returns true if this can be cast to that.
       * This can be useful if you want to check if a type implements for example a
       * {@link java.util.Collection} {@snippet class = "com.derivandi.javadoc.GenericUsageTest" region = "GenericUsage.isSubtypeOf"}
       */
      boolean isSubtypeOf(Type type);

      /**
       * {@snippet :
       *  String[]//@highlight substring="String"
       *}
       */
      Type getComponentType();

      /**
       * returns Object[] for declared Arrays and an {@link Generic} with bounds of {@code java.io.Serializable&java.lang.Cloneable}
       * for primitive Arrays
       */
      List<Type> getDirectSuperTypes();

      /**
       * String[] -&gt; String[][]
       */
      Array asArray();

      Wildcard asExtendsWildcard();

      Wildcard asSuperWildcard();

      /**
       * Information regarding generics is lost after the compilation. For Example {@code List<String>} becomes {@code List}. This method Does the same.
       * This can be useful if you want to check if a derivandi implements for example {@link java.util.Collection}
       * {@code typeToTest.erasure().isSubtypeOf(context.getDeclaredOrThrow("java.util.Collection").erasure())}
       * <p>
       * for {@link Array}s this means for example {@code T[]} -&gt; {@code java.lang.Object[]}
       */
      @Override
      Array erasure();
   }

   non-sealed interface Class
         extends Declared,
                 AbstractModifiable,
                 StaticModifiable,
                 Sealable,
                 FinalModifiable,
                 Erasable,
                 ClassRenderable
   {
      /**
       * reruns the super class of this class. calling {@code getSuperClass())} on {@link Integer} will return {@link Number}.
       * For {@link Object} null will be returned
       */
      @Nullable Class getSuperClass();

      List<Class> getPermittedSubClasses();

      List<Property> getProperties();

      /**
       * Equivalent to {@link Declared#isSubtypeOf(ReferenceType)} except for primitives.
       * if one is a primitive and the other is not it tries to convert them
       */
      boolean isAssignableFrom(Type type);

      List<Constructor> getConstructors();

      /**
       * {@snippet class = "com.derivandi.javadoc.GenericUsageTest" region = "GenericUsage.getGenericTypes"}
       */
      List<Type> getGenericUsages();

      /**
       * {@snippet class = "com.derivandi.javadoc.GenericUsageTest" region = "GenericUsage.getGenerics"}
       */
      List<Generic> getGenericDeclarations();

      /**
       * {@code context.getDeclaredOrThrow("java.util.List")} represents {@code List}
       * {@code context.getDeclaredOrThrow("java.util.List").withGenerics(context.getDeclaredOrThrow("java.lang.String"))} represents {@code List<String>}
       */
      Class withGenerics(Type... generics);

      /**
       * like {@link #withGenerics(Type...)} but resolves the names using {@link SimpleContext#getDeclaredOrThrow(String)}
       */
      Class withGenerics(String... qualifiedGenerics);

      /**
       * Used when constructing types to compare to at compile time that contain multiple, on each other depended, generics.
       * <p>
       * it answers the question: given {@snippet class = "com.derivandi.javadoc.InterpolateGenericsTest" region = "InterpolateGenerics.interpolateGenerics.code"}
       * and A being {@code String} what can B be by returning the "simplest" possible answer. in this case String
       * <p>
       * The code for the example
       * {@snippet class = "com.derivandi.javadoc.InterpolateGenericsTest" region = "InterpolateGenerics.interpolateGenerics"}
       */
      Class capture();

      /**
       * Integer -&gt; int<br>
       * Long -&gt; long<br>
       * etc...
       */
      Primitive asUnboxed();

      /**
       * Information regarding generics is lost after the compilation. For Example {@code List<String>} becomes {@code List}. This method Does the same.
       * This can be useful if you want to check if a derivandi implements for example {@link java.util.Collection}
       * {@code typeToTest.erasure().isSubtypeOf(context.getDeclaredOrThrow("java.util.Collection").erasure())}
       * <p>
       * for {@link Class}s this means for example {@code class MyClass<T>{}} -&gt; {@code class MyClass{}}
       */
      Class erasure();
   }

   /**
    * Anything that can be a file.
    */
   sealed interface Declared
         extends ReferenceType,
                 Annotationable,
                 AccessModifiable,
                 StrictfpModifiable,
                 Nameable,
                 QualifiedNameable,
                 ModuleEnclosed,
                 Documented,
                 DeclaredRenderable
         permits Annotation,
                 Class,
                 Enum,
                 Interface,
                 Record,
                 Unresolved
   {
      /**
       * returns true if this can be cast to that.
       * This can be useful if you want to check if a type implements for example a {@link java.util.Collection}
       * {@snippet class = "com.derivandi.javadoc.GenericUsageTest" region = "GenericUsage.isSubtypeOf"}
       */
      boolean isSubtypeOf(ReferenceType referenceType);

      /**
       * is it an outer or inner class? etc.
       */
      NestingKind getNesting();


      default Optional<Field> getField(String simpleName)
      {
         return getFields().stream().filter(field -> field.getName().equals(simpleName)).findAny();
      }

      default Field getFieldOrThrow(String simpleName)
      {
         return getField(simpleName).orElseThrow();
      }

      List<Field> getFields();

      default List<Method> getMethods(String simpleName)
      {
         return getMethods().stream().filter(field -> field.getName().equals(simpleName)).toList();
      }

      List<Method> getMethods();

      /**
       * returns the parentClass including interfaces
       */
      List<Declared> getDirectSuperTypes();

      /**
       * returns all distinct supertypes including interfaces
       */
      Set<Declared> getSuperTypes();

      List<Interface> getInterfaces();

      default Interface getInterfaceOrThrow(String qualifiedName)
      {
         return getInterfaces().stream()
                               .filter(anInterface -> anInterface.getQualifiedName().equals(qualifiedName))
                               .findAny()
                               .orElseThrow();
      }

      List<Interface> getDirectInterfaces();


      default Interface getDirectInterfaceOrThrow(String qualifiedName)
      {
         return getDirectInterfaces().stream()
                                     .filter(anInterface -> anInterface.getQualifiedName().equals(qualifiedName))
                                     .findAny()
                                     .orElseThrow();
      }

      Package getPackage();

      /**
       * part of the java language specification:
       * <p>
       * The binary name of a top level type is its canonical name. (qualified name)
       * The binary name of a member type consists of the binary name of its immediately enclosing type, followed by $, followed by the simple name of the member.
       */
      String getBinaryName();

      /**
       * String -&gt; String[]
       */
      Array asArray();

      /**
       * returns the {@link Declared} that surrounds this {@link Declared}
       */
      Optional<Declared> getSurrounding();

      Optional<Declared> getOutermostSurrounding();

      Origin getOrigin();

      Wildcard asExtendsWildcard();

      Wildcard asSuperWildcard();
   }

   non-sealed interface Enum
         extends Declared,
                 StaticModifiable,
                 EnumRenderable
   {
      List<Constructor> getConstructors();

      List<EnumConstant> getEnumConstants();

      default EnumConstant getEnumConstantOrThrow(String simpleName)
      {
         return getEnumConstants().stream().filter(field -> field.getName().equals(simpleName)).findAny().orElseThrow();
      }
   }

   /**
    * Represents a Type that can not be resolved in this processing Round
    */
   non-sealed interface Unresolved
         extends Declared {}

   /**
    * represents the generic parameter at a class, method, constructor etc.
    */
   non-sealed interface Generic
         extends ReferenceType,
                 Annotationable,
                 Nameable,
                 Erasable,
                 GenericRenderable
   {
      Type getBound();

      /**
       * {@snippet :
       * T extends Collection & Serializable//@highlight substring="Collection & Serializable"
       *}
       */
      List<Type> getBounds();

      List<Interface> getAdditionalBounds();

      /// Can not be declared in java normal java code. some results of [Class#capture()] can create a [Generic] with a super type
      Optional<Type> getSuper();

      /**
       * returns the class, method constructor etc. this is the generic for
       */
      Object getEnclosing();

      Origin getOrigin();

      /**
       * Information regarding generics is lost after the compilation. For Example {@code List<String>} becomes {@code List}. This method Does the same.
       * This can be useful if you want to check if a derivandi implements for example {@link java.util.Collection}
       * {@code typeToTest.erasure().isSubtypeOf(context.getDeclaredOrThrow("java.util.Collection").erasure())}
       * <p>
       * for {@link Generic}s this means for example {@code T extends Number} -&gt; {@code Number}
       */
      @Override
      Generic erasure();
   }

   non-sealed interface Interface
         extends Declared,
                 AbstractModifiable,
                 StaticModifiable,
                 Sealable,
                 Erasable,
                 InterfaceRenderable
   {
      boolean isFunctional();

      List<Declared> getPermittedSubTypes();

      /**
       * {@snippet class = "com.derivandi.javadoc.GenericUsageTest" region = "GenericUsage.getGenericTypes"}
       */
      List<Type> getGenericUsages();

      /**
       * {@snippet class = "com.derivandi.javadoc.GenericUsageTest" region = "GenericUsage.getGenerics"}
       */
      List<Generic> getGenericDeclarations();

      /**
       * {@code context.getDeclaredOrThrow("java.util.List")} represents {@code List}
       * {@code context.getDeclaredOrThrow("java.util.List").withGenerics(context.getDeclaredOrThrow("java.lang.String"))} represents {@code List<String>}
       */
      Interface withGenerics(Type... generics);

      /**
       * like {@link #withGenerics(Type...)} but resolves the names using {@link SimpleContext#getDeclaredOrThrow(String)}
       */
      Interface withGenerics(String... qualifiedGenerics);

      /**
       * Used when constructing types to compare to at compile time that contain multiple, on each other depended, generics.
       * <p>
       * it answers the question: given {@snippet class = "com.derivandi.javadoc.InterpolateGenericsTest" region = "InterpolateGenerics.interpolateGenerics.code"}
       * and A being {@code String} what can B be by returning the "simplest" possible answer. in this case String
       * <p>
       * The code for the example
       * {@snippet class = "com.derivandi.javadoc.InterpolateGenericsTest" region = "InterpolateGenerics.interpolateGenerics"}
       */
      Interface capture();

      /**
       * Information regarding generics is lost after the compilation. For Example {@code List<String>} becomes {@code List}. This method Does the same.
       * This can be useful if you want to check if a derivandi implements for example {@link java.util.Collection}
       * {@code typeToTest.erasure().isSubtypeOf(context.getDeclaredOrThrow("java.util.Collection").erasure())}
       * <p>
       * for {@link Interface}s this means for example {@code interface MyInterface<T>{}} -&gt; {@code interface MyInterface{}}
       */
      @Override
      Interface erasure();
   }

   non-sealed interface Null
         extends Type {}

   non-sealed interface Record
         extends Declared,
                 StaticModifiable,
                 FinalModifiable,
                 Erasable,
                 RecordRenderable
   {
      default RecordComponent getRecordComponentOrThrow(String simpleName)
      {
         return getRecordComponents().stream()
                                     .filter(field -> field.getName().equals(simpleName))
                                     .findAny()
                                     .orElseThrow();
      }

      List<RecordComponent> getRecordComponents();

      List<Constructor> getConstructors();

      /**
       * {@snippet class = "com.derivandi.javadoc.GenericUsageTest" region = "GenericUsage.getGenericTypes"}
       */
      List<Type> getGenericUsages();

      /**
       * {@snippet class = "com.derivandi.javadoc.GenericUsageTest" region = "GenericUsage.getGenerics"}
       */
      List<Generic> getGenericDeclarations();

      /**
       * {@code context.getRecordOrThrow("org.example.MyRecord")} represents {@code MyRecord}
       * {@code context.getRecordOrThrow("org.example.MyRecord").withGenerics(context.getDeclaredOrThrow("java.lang.String"))} represents {@code MyRecord<String>}
       */
      Record withGenerics(Type... generics);

      /**
       * like {@link #withGenerics(Type...)} but resolves the names using {@link SimpleContext#getDeclaredOrThrow(String)}
       */
      Record withGenerics(String... qualifiedGenerics);

      /**
       * Used when constructing types to compare to at compile time that contain multiple, on each other depended, generics.
       * <p>
       * it answers the question: given {@snippet class = "com.derivandi.javadoc.InterpolateGenericsTest" region = "InterpolateGenerics.interpolateGenerics.code"}
       * and A being {@code String} what can B be by returning the "simplest" possible answer. in this case String
       * <p>
       * The code for the example
       * {@snippet class = "com.derivandi.javadoc.InterpolateGenericsTest" region = "InterpolateGenerics.interpolateGenerics"}
       */
      Record capture();

      /**
       * Information regarding generics is lost after the compilation. For Example {@code List<String>} becomes {@code List}. This method Does the same.
       * This can be useful if you want to check if a derivandi implements for example {@link Collection}
       * {@code typeToTest.erasure().isSubtypeOf(context.getDeclaredOrThrow("java.util.Collection").erasure())}
       * <p>
       * for {@link Interface}s this means for example {@code interface MyInterface<T>{}} -&gt; {@code interface MyInterface{}}
       */
      @Override
      Record erasure();
   }

   sealed interface ReferenceType
         extends VariableType,
                 ReferenceTypeRenderable
         permits Array,
                 Declared,
                 Generic {}

   sealed interface Type
         extends TypeRenderable
         permits Null,
                 VariableType,
                 Void,
                 Wildcard
   {
      /**
       * type equals from the compiler perspective. for example ? does not equal ? for the compiler
       */
      boolean isSameType(Type type);
   }

   /// a type that can be used as method/ constructor parameter or field
   sealed interface VariableType
         extends Type,
                 VariableTypeRenderable
         permits ReferenceType,
                 Primitive {}

   non-sealed interface Void
         extends Type {}

   /**
    * {@snippet id = "test":
    *  List<? extends Number>//@highlight substring="? extends Number"
    *}
    * or
    * {@snippet :
    *  List<? super Number>//@highlight substring="? super Number"
    *}
    */
   non-sealed interface Wildcard
         extends Type,
                 Erasable
   {
      /**
       * {@snippet :
       *  List<? extends Number>//@highlight substring="? extends Number"
       *}
       */
      Optional<Type> getExtends();

      /**
       * {@snippet :
       *  List<? super Number>//@highlight substring="? super Number"
       *}
       */
      Optional<Type> getSuper();

      /**
       * {@snippet class = "com.derivandi.javadoc.WildcardTest" region = "Wildcard.contains"}
       */
      boolean contains(ReferenceType referenceType);

      /**
       * Information regarding generics is lost after the compilation. For Example {@code List<String>} becomes {@code List}. This method Does the same.
       * This can be useful if you want to check if a derivandi implements for example {@link Collection}
       * {@code typeToTest.erasure().isSubtypeOf(context.getDeclaredOrThrow("java.util.Collection").erasure())}
       * <p>
       * for {@link Wildcard}s this means for example {@code ? extends java.lang.Number} -&gt; {@code java.lang.Number}
       */
      @Override
      Wildcard erasure();
   }

   non-sealed interface boolean_
         extends Primitive {}

   non-sealed interface byte_
         extends Primitive {}

   non-sealed interface char_
         extends Primitive {}

   non-sealed interface double_
         extends Primitive {}

   non-sealed interface float_
         extends Primitive {}

   non-sealed interface int_
         extends Primitive {}

   non-sealed interface long_
         extends Primitive {}

   non-sealed interface short_
         extends Primitive {}

   /**
    * represents primitive types, but not there wrapper classes. for example int, long, short
    */
   sealed interface Primitive
         extends Nameable,
                 VariableType
         permits boolean_,
                 byte_,
                 char_,
                 double_,
                 float_,
                 int_,
                 long_,
                 short_
   {
      /**
       * returns true if this can be cast to that.
       * This can be useful if you want to check if a type implements for example a
       * {@link Collection} {@snippet class = "com.derivandi.javadoc.GenericUsageTest" region = "GenericUsage.isSubtypeOf"}
       */
      boolean isSubtypeOf(Type type);

      /**
       * Equivalent to {@link #isSubtypeOf(Type)} except for primitives.
       * if one is a primitive and the other is not it tries to convert them
       */
      boolean isAssignableFrom(Type type);

      /**
       * int -&gt; Integer<br>
       * long -&gt; Long<br>
       * etc...
       */
      Class asBoxed();

      /**
       * int -&gt; int[]
       */
      Array asArray();
   }


   non-sealed interface Constructor
         extends Executable,
                 AccessModifiable,
                 ConstructorRenderable {

      boolean isCompact();

      boolean isCanonical();
   }

   non-sealed interface EnumConstant
         extends Variable,
                 EnumConstantRenderable
   {
      @Override
      Enum getSurrounding();
   }

   sealed interface Executable
         extends Annotationable,
                 Nameable,
                 Modifiable,
                 ModuleEnclosed,
                 Documented
         permits Constructor,
                 Method
   {
      /**
       * {@snippet :
       *  public MyObject(String param){}//@highlight substring="String param"
       *}
       * Returns the formal parameters, meaning everything but the Receiver.
       * <p>
       * there is a bug in {@link java.lang.reflect.Executable#getParameters()} for {@link java.lang.reflect.Constructor}s. For
       * {@link Constructor}s with more than one {@link Parameter} of the {@link #getReceiverType()} a Receiver will be returned.
       */
      List<Parameter> getParameters();

      default Optional<Parameter> getParameter(String name)
      {
         return getParameters().stream().filter(parameter -> parameter.getName().equals(name)).findAny();
      }

      default Parameter getParameterOrThrow(String name)
      {
         return getParameter(name).orElseThrow();
      }

      List<Type> getParameterTypes();

      List<Class> getThrows();

      /**
       * {@link List#of(Object[])}
       */
      boolean isVarArgs();

      boolean isDeprecated();

      boolean isSubsignatureOf(Executable method);

      boolean isSubsignatureFor(Executable method);

      Origin getOrigin();

      /**
       * returns the {@link Declared} that surrounds this {@link Executable}
       */
      Declared getSurrounding();

      /**
       * {@snippet class = "com.derivandi.javadoc.GenericUsageTest" region = "GenericUsage.getGenerics"}
       */
      List<Generic> getGenericDeclarations();

      /**
       * The receiver represents the instance the method is called on. This language feature is barely used, it makes it possible to annotate "this".
       * {@snippet class = "com.derivandi.javadoc.ReceiverUsageTest" region = "ReceiverUsageTest.method"}
       */
      Optional<Declared> getReceiverType();

      /**
       * The receiver represents the instance the method is called on. This language feature is barely used, it makes it possible to annotate "this".
       * {@snippet class = "com.derivandi.javadoc.ReceiverUsageTest" region = "ReceiverUsageTest.method"}
       */
      Optional<Receiver> getReceiver();

      /**
       * The receiver represents the instance the method is called on. This language feature is barely used, it makes it possible to annotate "this".
       * {@snippet class = "com.derivandi.javadoc.ReceiverUsageTest" region = "ReceiverUsageTest.method"}
       */
      default Receiver getReceiverOrThrow()
      {
         return getReceiver().orElseThrow();
      }
   }

   non-sealed interface Field
         extends Variable,
                 AccessModifiable,
                 FinalModifiable,
                 StaticModifiable,
                 FieldRenderable

   {
      boolean isConstant();

      /// String or primitive value of final fields no enums
      Optional<Object> getConstantValue();

      /// String or primitive value of final fields no enums
      default Object getConstantValueOrThrow()
      {
         return getConstantValue().orElseThrow();
      }

      @Override
      Declared getSurrounding();
   }

   non-sealed interface Method
         extends Executable,
                 StaticModifiable,
                 DefaultModifiable,
                 AccessModifiable,
                 AbstractModifiable,
                 FinalModifiable,
                 StrictfpModifiable,
                 NativeModifiable,
                 MethodRenderable
   {
      Type getResultType();

      boolean overrides(Method method);

      boolean overwrittenBy(Method method);

      /**
       * <pre>{@code
       * Do both methods have the same parameter types in the same order?
       * a() && b() -> true
       * a(String name) && b() -> false
       * a(String name, Long id) && b(Long id, String name) -> false
       * a(String name) && b(String name2) -> true
       * a(List list) && b(List<String> strings) -> true
       * a(List<String> strings) b(List list) -> false
       * }</pre>
       */
      boolean sameParameterTypes(Method method);

      /**
       * The java language and the java virtual machine have different specification. Bridge Methods are created to bridge that gap
       */
      boolean isBridge();

      /**
       * Can be annotated using annotations with {@link ElementType#TYPE_USE}
       */
      Result getResult();
   }

   interface Module
         extends Annotationable,
                 Nameable,
                 QualifiedNameable,
                 Documented,
                 ModuleRenderable
   {
      List<Package> getPackages();

      /**
       * can everybody use reflection on this module?
       */
      boolean isOpen();

      boolean isUnnamed();

      boolean isAutomatic();

      boolean isDeprecated();

      Origin getOrigin();

      /**
       * Relations between modules
       */
      List<Directive> getDirectives();

      List<Declared> getDeclared();

      Optional<Declared> getDeclared(String qualifiedName);

      default Declared getDeclaredOrThrow(String qualifiedName)
      {
         return getDeclared(qualifiedName).orElseThrow();
      }

      default List<Annotationable> getAnnotations()
      {
         return getDeclared().stream()
                             .map(Annotationable.class::cast)
                             .toList();
      }

      default Optional<Annotation> getAnnotation(String qualifiedName)
      {
         return getDeclared(qualifiedName).map(Annotation.class::cast);
      }

      default Annotation getAnnotationOrThrow(String qualifiedName)
      {
         return ((Annotation) getDeclaredOrThrow(qualifiedName));
      }

      default List<Class> getClasses()
      {
         return getDeclared().stream()
                             .filter(Class.class::isInstance)
                             .map(Class.class::cast)
                             .toList();
      }

      default Optional<Class> getClass(String qualifiedName)
      {
         return getDeclared(qualifiedName).map(Class.class::cast);
      }

      default Class getClassOrThrow(String qualifiedName)
      {
         return ((Class) getDeclaredOrThrow(qualifiedName));
      }

      default List<Enum> getEnums()
      {
         return getDeclared().stream()
                             .filter(Enum.class::isInstance)
                             .map(Enum.class::cast)
                             .toList();
      }

      default Optional<Enum> getEnum(String qualifiedName)
      {
         return getDeclared(qualifiedName).map(Enum.class::cast);
      }

      default Enum getEnumOrThrow(String qualifiedName)
      {
         return ((Enum) getDeclaredOrThrow(qualifiedName));
      }

      default List<Interface> getInterfaces()
      {
         return getDeclared().stream()
                             .filter(Interface.class::isInstance)
                             .map(Interface.class::cast)
                             .toList();
      }

      default Optional<Interface> getInterface(String qualifiedName)
      {
         return getDeclared(qualifiedName).map(Interface.class::cast);
      }

      default Interface getInterfaceOrThrow(String qualifiedName)
      {
         return ((Interface) getDeclaredOrThrow(qualifiedName));
      }

      default List<Record> getRecords()
      {
         return getDeclared().stream()
                             .filter(Record.class::isInstance)
                             .map(Record.class::cast)
                             .toList();
      }

      default Optional<Record> getRecord(String qualifiedName)
      {
         return getDeclared(qualifiedName).map(Record.class::cast);
      }

      default Record getRecordOrThrow(String qualifiedName)
      {
         return ((Record) getDeclaredOrThrow(qualifiedName));
      }
   }

   interface Package
         extends Annotationable,
                 Nameable,
                 QualifiedNameable,
                 ModuleEnclosed,
                 Documented,
                 PackageRenderable
   {
      /**
       * Unnamed packages are intend for small snips of code like jShell and not seen in regular projects
       */
      boolean isUnnamed();

      boolean isDeprecated();

      Origin getOrigin();

      List<Declared> getDeclared();

      Optional<Declared> getDeclared(String qualifiedName);

      default Declared getDeclaredOrThrow(String qualifiedName)
      {
         return getDeclared(qualifiedName).orElseThrow();
      }

      default List<Annotation> getAnnotations()
      {
         return getDeclared().stream()
                             .filter(Annotation.class::isInstance)
                             .map(Annotation.class::cast)
                             .toList();
      }

      default Optional<Annotation> getAnnotation(String qualifiedName)
      {
         return getDeclared(qualifiedName).map(Annotation.class::cast);
      }

      default Annotation getAnnotationOrThrow(String qualifiedName)
      {
         return ((Annotation) getDeclaredOrThrow(qualifiedName));
      }

      default List<Class> getClasses()
      {
         return getDeclared().stream()
                             .filter(Class.class::isInstance)
                             .map(Class.class::cast)
                             .toList();
      }

      default Optional<Class> getClass(String qualifiedName)
      {
         return getDeclared(qualifiedName).map(Class.class::cast);
      }

      default Class getClassOrThrow(String qualifiedName)
      {
         return ((Class) getDeclaredOrThrow(qualifiedName));
      }

      default List<Enum> getEnums()
      {
         return getDeclared().stream()
                             .filter(Enum.class::isInstance)
                             .map(Enum.class::cast)
                             .toList();
      }

      default Optional<Enum> getEnum(String qualifiedName)
      {
         return getDeclared(qualifiedName).map(Enum.class::cast);
      }

      default Enum getEnumOrThrow(String qualifiedName)
      {
         return ((Enum) getDeclaredOrThrow(qualifiedName));
      }

      default List<Interface> getInterfaces()
      {
         return getDeclared().stream()
                             .filter(Interface.class::isInstance)
                             .map(Interface.class::cast)
                             .toList();
      }

      default Optional<Interface> getInterface(String qualifiedName)
      {
         return getDeclared(qualifiedName).map(Interface.class::cast);
      }

      default Interface getInterfaceOrThrow(String qualifiedName)
      {
         return ((Interface) getDeclaredOrThrow(qualifiedName));
      }

      default List<Record> getRecords()
      {
         return getDeclared().stream()
                             .filter(Record.class::isInstance)
                             .map(Record.class::cast)
                             .toList();
      }

      default Optional<Record> getRecord(String qualifiedName)
      {
         return getDeclared(qualifiedName).map(Record.class::cast);
      }

      default Record getRecordOrThrow(String qualifiedName)
      {
         return ((Record) getDeclaredOrThrow(qualifiedName));
      }
   }

   /// Parameter of a method or constructor
   ///
   /// @see Executable#getParameters()
   non-sealed interface Parameter
         extends Variable,
                 FinalModifiable,
                 ParameterRenderable
   {
      /**
       * {@link List#of(Object[])}
       */
      boolean isVarArgs();

      @Override
      Executable getSurrounding();
   }

   /// This represents a java beans Property. Only a [Getter][Property#getGetter()]  is mandatory
   ///
   /// [Getter][Property#getGetter()]
   /// - not static
   /// - the name starts with "get" and is longer then 3 or
   /// the name starts with "is" and has the return type [Boolean] or [Boolean#TYPE] and is longer then 2
   ///   - if a "is" and a "get" setter are present "is" is preferred
   ///
   /// [Setter][Property#getSetter()]
   /// - not static
   /// - the names has to match the getter name
   /// - the name starts with "set" and is longer then 3
   /// - has one parameter
   /// - the parameter has the same type as the Getter
   ///
   /// [Field][Property#getField()]
   /// - not static
   /// - has the same type as the Getter
   /// - if the name of the Getter without prefix is longer then 1 and has an uppercase Character
   /// at index 0 or 1 the field name has to match exactly
   /// or the name matches with the first Character converted to lowercase
   ///    - [Java Beans 8.8](https://www.oracle.com/java/technologies/javase/javabeans-spec.html)
   interface Property
         extends Nameable
   {
      /**
       * based on the name of the getter without the prefix. if one of the first 2 chars is uppercase the
       * property name is not changed. otherwise the first char is converted to lowercase
       *
       * @see #getGetter()
       */
      String getName();

      /**
       * return type of getter
       *
       * @see #getGetter()
       */
      VariableType getType();

      /**
       * a {@link Field} with the name and tye of this property
       *
       * @see #getName()
       * @see #getType()
       */
      Optional<Field> getField();

      /**
       * @see #getField()
       */
      Field getFieldOrThrow();

      /**
       * 2 possible types of getters
       * <ul>
       * <li>return type boolean, name prefix "is" and no parameters</li>
       * <li>name prefix is "get" and no parameters</li>
       * </ul>
       * when both are present "is" is preferred over get
       */
      Method getGetter();

      /**
       * a method with the same name as the getter but "set" instead of "is" or "get", return type void and one
       * parameter being the type of the return of the getter
       *
       * @see #getGetter()
       */
      Optional<Method> getSetter();

      /**
       * @see #getSetter()
       */
      Method getSetterOrThrow();

      /**
       * has a {@link #getSetter()}
       */
      boolean isMutable();
   }

   interface Receiver
         extends Annotationable,
                 ReceiverRenderable
   {
      Type getType();
   }

   interface RecordComponent
         extends Annotationable,
                 Nameable,
                 ModuleEnclosed,
                 RecordComponentRenderable
   {
      /**
       * returns true if this can be cast to that.
       * This can be useful if you want to check if a type implements for example a
       * {@link Collection} {@snippet class = "com.derivandi.javadoc.GenericUsageTest" region = "GenericUsage.isSubtypeOf"}
       */
      boolean isSubtypeOf(Type type);

      /**
       * Equivalent to {@link #isSubtypeOf(Type)} except for primitives.
       * if one is a primitive and the other is not it tries to convert them
       */
      boolean isAssignableFrom(Type type);

      /**
       * returns the record this is a port of
       */
      Record getRecord();

      Type getType();

      Method getGetter();

      Origin getOrigin();
   }

   interface Result
         extends Annotationable,
                 ResultRenderable
   {
      Type getType();
   }

   sealed interface Variable
         extends Annotationable,
                 Documented,
                 Nameable,
                 ModuleEnclosed,
                 Modifiable
         permits EnumConstant,
                 Field,
                 Parameter
   {
      /**
       * returns true if this can be cast to that.
       * This can be useful if you want to check if a type implements for example a
       * {@link Collection} {@code typeToTest.erasure().isSubtypeOf(context.getDeclaredOrThrow("java.util.Collection").erasure())}
       */
      boolean isSubtypeOf(Type type);

      /**
       * Equivalent to {@link #isSubtypeOf(Type)} except for primitives.
       * if one is a primitive and the other is not it tries to convert them
       */
      boolean isAssignableFrom(Type type);

      VariableType getType();

      boolean isDeprecated();

      Origin getOrigin();

      /**
       * The {@link Object} surrounding this {@link Variable}
       */
      Object getSurrounding();
   }

   interface AbstractModifiable
         extends Modifiable
   {
      default boolean isAbstract()
      {
         return hasModifier(Modifier.ABSTRACT);
      }
   }

   interface AccessModifiable
         extends Modifiable
   {
      default boolean isPublic()
      {
         return hasModifier(PUBLIC);
      }

      default boolean isPackagePrivate()
      {
         return hasModifier(PACKAGE_PRIVATE);
      }

      default boolean isProtected()
      {
         return hasModifier(PROTECTED);
      }

      default boolean isPrivate()
      {
         return hasModifier(PRIVATE);
      }
   }

   interface DefaultModifiable
         extends Modifiable
   {
      default boolean isDefault()
      {
         return hasModifier(DEFAULT);
      }
   }

   interface FinalModifiable
         extends Modifiable
   {
      default boolean isFinal()
      {
         return hasModifier(FINAL);
      }
   }

   interface Modifiable
   {
      Set<Modifier> getModifiers();

      default boolean hasModifier(Modifier modifier)
      {
         return getModifiers().contains(modifier);
      }
   }

   interface NativeModifiable
         extends Modifiable
   {
      default boolean isNative()
      {
         return hasModifier(NATIVE);
      }
   }

   interface Sealable
         extends Modifiable
   {
      default boolean isSealed()
      {
         return hasModifier(SEALED);
      }

      default boolean isNonSealed()
      {
         return hasModifier(NON_SEALED);
      }
   }

   interface StaticModifiable
         extends Modifiable
   {
      default boolean isStatic()
      {
         return hasModifier(STATIC);
      }
   }

   interface StrictfpModifiable
         extends Modifiable
   {
      default boolean isStrictfp()
      {
         return hasModifier(STRICTFP);
      }
   }

   /// Relation between modules
   sealed interface Directive
         permits Exports,
                 Opens,
                 Provides,
                 Requires,
                 Uses
   {
      Origin getOrigin();
   }

   /**
    * Exports a {@link #getPackage()} to {@link #getTargetModules()} or {@link #toAll()}
    */
   non-sealed interface Exports
         extends Directive,
                 ExportsRenderable
   {
      /**
       * packages to export to every module in {@link #getTargetModules()} or all if the list is empty
       */
      Package getPackage();

      /**
       * The list of modules the package is exported to. or all if the list is empty
       */
      List<Module> getTargetModules();

      boolean toAll();
   }

   /**
    * Allows reflection access to {@link #getPackage()} for {@link #getTargetModules()} or {@link #toAll()}
    */
   non-sealed interface Opens
         extends Directive,
                 OpensRenderable
   {
      /**
       * the package to be accessed by reflection
       */
      Package getPackage();

      /**
       * Modules allowed to access {@link #getPackage()} or {@link #toAll()} if the list is empty
       */
      List<Module> getTargetModules();

      boolean toAll();
   }

   /**
    * Provides a {@link #getService()} to other modules internally using {@link #getImplementations()}
    *
    * @see Uses
    */
   non-sealed interface Provides
         extends Directive,
                 ProvidesRenderable
   {
      /**
       * a service to provide to other modules
       */
      Declared getService();

      /**
       * Implementations of the provided service
       */
      List<Declared> getImplementations();
   }

   /**
    * Dependency on another Module
    */
   non-sealed interface Requires
         extends Directive,
                 RequiresRenderable
   {
      /**
       * The dependent module is required at compile time and optional at runtime
       */
      boolean isStatic();

      /**
       * Marks transitive Dependencies.
       * Let's say A depends on B
       * and B depends transitive on C.
       * In this case A also depends on C. This is needed when B exposes C
       */
      boolean isTransitive();

      Module getDependency();
   }

   /**
    * Uses a Service of another module
    *
    * @see Provides
    */
   non-sealed interface Uses
         extends Directive,
                 UsesRenderable
   {
      Declared getService();
   }
}
