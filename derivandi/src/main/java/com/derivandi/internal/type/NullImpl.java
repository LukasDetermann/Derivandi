package com.derivandi.internal.type;

import com.derivandi.api.D;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.type.NullType;
import java.util.Objects;

public class NullImpl extends TypeImpl<NullType> implements D.Null
{
   public NullImpl(SimpleContext context, NullType nullType)
   {
      super(context, nullType);
   }

   @Override
   public boolean isSameType(D.Type type)
   {
      return type instanceof D.Null;
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return "null";
   }

   @Override
   public String renderType(RenderingContext renderingContext)
   {
      return renderName(renderingContext);
   }

   @Override
   public boolean equals(Object other)
   {
      return other instanceof D.Null;
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(D.Null.class);
   }

   @Override
   public String toString()
   {
      return "Null";
   }
}
