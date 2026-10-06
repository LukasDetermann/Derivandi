package com.derivandi.internal.type;

import com.derivandi.api.D;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.type.ArrayType;
import java.util.List;
import java.util.Objects;

import static com.derivandi.api.adapter.Adapters.adapt;

public final class ArrayImpl extends TypeImpl<ArrayType> implements D.Array
{

   public ArrayImpl(SimpleContext context, ArrayType arrayType)
   {
      super(context, arrayType);
   }

   @Override
   public boolean isSubtypeOf(D.Type type)
   {
      return adapt(getApi()).toTypes().isSubtype(adapt(type).toTypeMirror(), getMirror());
   }

   @Override
   public D.Type getComponentType()
   {
      return adapt(getApi(), getMirror().getComponentType());
   }

   @Override
   public List<D.Type> getDirectSuperTypes()
   {
      return adapt(getApi()).toTypes()
                       .directSupertypes(getMirror())
                       .stream()
                       .map(typeMirror1 -> adapt(getApi(), typeMirror1))
                       .toList();
   }

   @Override
   public D.Array asArray()
   {
      return adapt(getApi(), adapt(getApi()).toTypes().getArrayType(getMirror()));
   }

   @Override
   public D.Wildcard asExtendsWildcard()
   {
      return adapt(getApi(), adapt(getApi()).toTypes().getWildcardType(getMirror(), null));
   }

   @Override
   public D.Wildcard asSuperWildcard()
   {
      return adapt(getApi(), adapt(getApi()).toTypes().getWildcardType(null, getMirror()));
   }

   @Override
   public boolean isSameType(D.Type type)
   {
      return equals(type);
   }

   @Override
   public D.Array erasure()
   {
      return adapt(getApi(), ((ArrayType) adapt(getApi()).toTypes().erasure(getMirror())));
   }

   @Override
   public String renderType(RenderingContext renderingContext)
   {
      return getComponentType().renderName(renderingContext) + "[]";
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return renderType(renderingContext);
   }

   @Override
   public boolean equals(Object other)
   {
      return other instanceof D.Array array &&
             Objects.equals(getComponentType(), array.getComponentType());
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getComponentType());
   }

   @Override
   public String toString()
   {
      return "Array{" +
             "componentType=" + getComponentType() +
             '}';
   }
}
