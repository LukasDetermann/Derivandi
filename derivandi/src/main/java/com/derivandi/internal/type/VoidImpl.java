package com.derivandi.internal.type;

import com.derivandi.api.D;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.type.NoType;
import java.util.Objects;

public class VoidImpl
      extends TypeImpl<NoType>
      implements D.Void
{
   public VoidImpl(SimpleContext context, NoType typeMirror)
   {
      super(context, typeMirror);
   }

   @Override
   public boolean equals(Object other)
   {
      return other instanceof D.Void;
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(D.Void.class);
   }

   @Override
   public String toString()
   {
      return "void";
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return "void";
   }

   @Override
   public String renderType(RenderingContext renderingContext)
   {
      return renderName(renderingContext);
   }
}