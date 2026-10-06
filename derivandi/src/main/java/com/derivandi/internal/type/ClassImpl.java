package com.derivandi.internal.type;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.adapter.TypeAdapter;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.class_.ClassGenericStep;
import com.derivandi.api.processor.SimpleContext;
import com.derivandi.internal.structure.PropertyFactory;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;

import static com.derivandi.api.adapter.Adapters.adapt;
import static com.derivandi.api.dsl.JavaDsl.class_;
import static com.derivandi.api.dsl.JavaDsl.innerClass;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

public class ClassImpl
      extends DeclaredImpl
      implements D.Class
{
   public ClassImpl(SimpleContext context, DeclaredType declaredTypeMirror)
   {
      super(context, declaredTypeMirror);
   }

   public ClassImpl(SimpleContext context, TypeElement typeElement)
   {
      super(context, typeElement);
   }

   @Override
   public boolean isAssignableFrom(D.Type type)
   {
      return adapt(getApi()).toTypes().isAssignable(getMirror(), adapt(type).toTypeMirror());
   }

   @Nullable
   @Override
   public D.Class getSuperClass()
   {
      TypeMirror superclass = getElement().getSuperclass();
      if (TypeKind.NONE.equals(superclass.getKind()))
      {
         return null;
      }
      return (D.Class) adapt(getApi(), ((DeclaredType) superclass));
   }

   @Override
   public List<D.Class> getPermittedSubClasses()
   {
      return getElement().getPermittedSubclasses()
                         .stream()
                         .map(typeMirror -> adapt(getApi(), ((DeclaredType) typeMirror)))
                         .map(D.Class.class::cast)
                         .toList();
   }

   @Override
   public List<D.Property> getProperties()
   {
      return PropertyFactory.of(this);
   }

   @Override
   public List<D.Type> getGenericUsages()
   {
      return getMirror().getTypeArguments()
                        .stream()
                        .map(typeMirror -> Adapters.<D.Type>adapt(getApi(), typeMirror))
                        .toList();
   }

   @Override
   public List<D.Generic> getGenericDeclarations()
   {
      return getElement().getTypeParameters()
                         .stream()
                         .map(element -> adapt(getApi(), element))
                         .toList();
   }

   @Override
   public D.Class withGenerics(D.Type... generics)
   {
      if (generics.length == 0 || getGenericDeclarations().size() != generics.length)
      {
         throw new IllegalArgumentException(getQualifiedName() +
                                            " has " +
                                            getGenericDeclarations().size() +
                                            " generics. " +
                                            generics.length +
                                            " are provided");
      }
      Optional<D.Declared> outerType = getSurrounding();
      if (!isStatic() && outerType.isPresent() &&
          (outerType.get() instanceof D.Interface anInterface &&
           !anInterface.getGenericDeclarations().isEmpty() ||
           outerType.get() instanceof D.Class aClass1 && !aClass1.getGenericDeclarations().isEmpty()))
      {
         throw new IllegalArgumentException("cant add generics to " +
                                            getQualifiedName() +
                                            " when the class is not static and the outer class has generics");
      }
      TypeMirror[] typeMirrors = stream(generics)
            .map(Adapters::adapt)
            .map(TypeAdapter::toTypeMirror)
            .toArray(TypeMirror[]::new);

      return (D.Class) adapt(getApi(), adapt(getApi()).toTypes().getDeclaredType(getElement(), typeMirrors));
   }

   @Override
   public D.Class withGenerics(String... qualifiedGenerics)
   {
      return withGenerics(stream(qualifiedGenerics)
                                .map(name -> getApi().getDeclaredOrThrow(name))
                                .toArray(D.Type[]::new));
   }

   @Override
   public D.Class capture()
   {
      return (D.Class) adapt(getApi(), ((DeclaredType) adapt(getApi()).toTypes().capture(getMirror())));
   }

   @Override
   public D.Primitive asUnboxed()
   {
      return adapt(getApi(), adapt(getApi()).toTypes().unboxedType(getMirror()));
   }

   @Override
   public D.Class erasure()
   {
      return (D.Class) adapt(getApi(), ((DeclaredType) adapt(getApi()).toTypes().erasure(getMirror())));
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      ClassGenericStep extendsStep = (switch (getNesting())
                                      {
                                         case OUTER -> class_().package_(getPackage());
                                         case INNER -> innerClass().outer(getSurrounding().orElseThrow());
                                      }).annotate(getDirectAnnotationUsages())
                                        .modifier(getModifiers())
                                        .name(getName())
                                        .genericDeclaration(getGenericDeclarations())
                                        .genericUsage(getGenericUsages());

      return ofNullable(getSuperClass())
            .map(extendsStep::extends_)
            .orElse(extendsStep)
            .implements_(getDirectInterfaces())
            .permits(getPermittedSubClasses())
            .field(getFields())
            .method(getMethods())
            .constructor(getConstructors())
            .renderDeclaration(renderingContext);
   }

   @Override
   public String renderQualifiedName(RenderingContext renderingContext)
   {
      return getQualifiedName();
   }

   @Override
   public String renderSimpleName(RenderingContext renderingContext)
   {
      return getName();
   }

   @Override
   public String renderType(RenderingContext renderingContext)
   {
      return (switch (getNesting())
              {
                 case OUTER -> class_().package_(getPackage());
                 case INNER -> innerClass().outer(getSurrounding().orElseThrow());
              }).name(getName())
                .genericDeclaration(getGenericDeclarations())
                .genericUsage(getGenericUsages())
                .renderType(renderingContext);
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return renderingContext.renderName(renderQualifiedName(renderingContext));
   }

   @Override
   public boolean equals(Object other)
   {
      return equals(D.Class.class, other);
   }

   @Override
   public String toString()
   {
      return toString("Class");
   }
}
