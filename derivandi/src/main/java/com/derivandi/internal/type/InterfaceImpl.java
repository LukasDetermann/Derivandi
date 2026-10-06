package com.derivandi.internal.type;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.adapter.TypeAdapter;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static com.derivandi.api.adapter.Adapters.adapt;
import static com.derivandi.api.dsl.JavaDsl.innerInterface;
import static com.derivandi.api.dsl.JavaDsl.interface_;
import static java.util.Arrays.stream;

public class InterfaceImpl
      extends DeclaredImpl
      implements D.Interface
{
   public InterfaceImpl(SimpleContext context, DeclaredType declaredTypeMirror)
   {
      super(context, declaredTypeMirror);
   }

   public InterfaceImpl(SimpleContext context, TypeElement typeElement)
   {
      super(context, typeElement);
   }


   @Override
   public boolean isFunctional()
   {
      return adapt(getApi()).toElements().isFunctionalInterface(getElement());
   }

   @Override
   public List<D.Declared> getPermittedSubTypes()
   {
      return getElement().getPermittedSubclasses()
                         .stream()
                         .map(typeMirror -> adapt(getApi(), ((DeclaredType) typeMirror)))
                         .toList();
   }

   @Override
   public List<D.Type> getGenericUsages()
   {
      return getMirror().getTypeArguments()
                        .stream()
                        .map(typeMirror -> adapt(getApi(), typeMirror))
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
   public D.Interface withGenerics(D.Type... generics)
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
      TypeMirror[] typeMirrors = stream(generics)
            .map(Adapters::adapt)
            .map(TypeAdapter::toTypeMirror)
            .toArray(TypeMirror[]::new);

      return (D.Interface) adapt(getApi(), adapt(getApi()).toTypes().getDeclaredType(getElement(), typeMirrors));
   }

   @Override
   public D.Interface withGenerics(String... qualifiedGenerics)
   {
      return withGenerics(stream(qualifiedGenerics)
                                .map(name -> getApi().getDeclaredOrThrow(name))
                                .toArray(D.Type[]::new));
   }

   @Override
   public D.Interface capture()
   {
      return (D.Interface) adapt(getApi(), ((DeclaredType) adapt(getApi()).toTypes().capture(getMirror())));
   }

   @Override
   public D.Interface erasure()
   {
      return (D.Interface) adapt(getApi(), ((DeclaredType) adapt(getApi()).toTypes().erasure(getMirror())));
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      return (switch (getNesting())
              {
                 case OUTER -> interface_().package_(getPackage());
                 case INNER -> innerInterface().outer(getSurrounding().orElseThrow());
              }).annotate(getDirectAnnotationUsages())
                .modifier(getModifiers())
                .name(getName())
                .genericDeclaration(getGenericDeclarations())
                .genericUsage(getGenericUsages())
                .extends_(getDirectInterfaces())
                .permits(getPermittedSubTypes())
                .field(getFields())
                .method(getMethods())
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
                 case OUTER -> interface_().package_(getPackage());
                 case INNER -> innerInterface().outer(getSurrounding().orElseThrow());
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
      return equals(D.Interface.class, other);
   }

   @Override
   public String toString()
   {
      return toString("Interface");
   }
}
