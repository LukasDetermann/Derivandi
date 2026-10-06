package com.derivandi.internal.structure;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.VariableElement;

import static com.derivandi.api.dsl.JavaDsl.enumConstant;

public class EnumConstantImpl extends VariableImpl implements D.EnumConstant
{
   public EnumConstantImpl(SimpleContext context, VariableElement variableElement)
   {
      super(context, variableElement);
   }

   @Override
   public D.Enum getSurrounding()
   {
      return (D.Enum) Adapters.adapt(getApi(), getElement().getEnclosingElement());
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      return enumConstant()
            .annotate(getDirectAnnotationUsages())
            .name(getName()).renderDeclaration(renderingContext);
   }
}
