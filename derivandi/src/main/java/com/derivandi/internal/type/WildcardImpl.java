package com.derivandi.internal.type;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import java.util.Objects;
import java.util.Optional;

import static com.derivandi.api.adapter.Adapters.adapt;

public class WildcardImpl extends TypeImpl<WildcardType> implements D.Wildcard
{
   public WildcardImpl(SimpleContext context, WildcardType wildcardTypeMirror)
   {
      super(context, wildcardTypeMirror);
   }

   @Override
   public Optional<D.Type> getExtends()
   {
      TypeMirror extendsBound = getMirror().getExtendsBound();
      if (extendsBound == null)
      {
         return Optional.empty();
      }
      return Optional.of(adapt(getApi(), extendsBound));
   }

   @Override
   public Optional<D.Type> getSuper()
   {
      TypeMirror superBound = getMirror().getSuperBound();
      if (superBound == null)
      {
         return Optional.empty();
      }
      return Optional.of(adapt(getApi(), superBound));
   }

   @Override
   public boolean contains(D.ReferenceType referenceType)
   {
      TypeMirror typemirror = switch (referenceType)
      {
         case D.Array array -> adapt(array).toArrayType();
         case D.Generic generic -> adapt(generic).toTypeVariable();
         case D.Declared declared -> adapt(declared).toDeclaredType();
      };

      return adapt(getApi()).toTypes().contains(getMirror(), typemirror);
   }

   @Override
   public D.Wildcard erasure()
   {
      return (D.Wildcard) Adapters.adapt(getApi(), adapt(getApi()).toTypes().erasure(getMirror()));
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      Optional<D.Type> superType = getSuper();
      if (superType.isPresent())
      {
         return "? super " + superType.get().renderName(renderingContext);
      }
      Optional<D.Type> extendsType = getExtends();
      if (extendsType.isPresent())
      {
         return "? extends " + extendsType.get().renderType(renderingContext);
      }
      return "?";
   }

   @Override
   public String renderType(RenderingContext renderingContext)
   {
      return renderName(renderingContext);
   }

   @Override
   public boolean equals(Object other)
   {
      return other instanceof D.Wildcard wildcard &&
             Objects.equals(getExtends(), wildcard.getExtends()) &&
             Objects.equals(getSuper(), wildcard.getSuper());
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getExtends(), getSuper());
   }

   @Override
   public String toString()
   {
      return "Wildcard{" +
             "extends=" + getExtends() +
             ", super=" + getSuper() +
             '}';
   }
}
