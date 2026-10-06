package com.derivandi.internal.type;

import com.derivandi.api.D;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.type.ErrorType;

public class UnresolvedImpl
      extends DeclaredImpl
      implements D.Unresolved
{
   public UnresolvedImpl(SimpleContext context, ErrorType declaredTypeMirror)
   {
      super(context, declaredTypeMirror);
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      throw new IllegalStateException("Can not render the declaration of an Unresolved Type");
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
   public String renderName(RenderingContext renderingContext)
   {
      return renderingContext.renderName(renderQualifiedName(renderingContext));
   }

   @Override
   public String renderType(RenderingContext renderingContext)
   {
      return renderName(renderingContext);
   }

   @Override
   public boolean equals(Object other)
   {
      return equals(D.Unresolved.class, other);
   }

   @Override
   public String toString()
   {
      return toString("Unresolved");
   }
}