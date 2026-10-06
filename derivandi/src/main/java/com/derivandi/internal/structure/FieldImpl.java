package com.derivandi.internal.structure;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.field.FieldInitializationStep;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import java.util.Optional;

import static com.derivandi.api.dsl.JavaDsl.field;

public class FieldImpl extends VariableImpl implements D.Field
{
   public FieldImpl(SimpleContext context, VariableElement variableElement)
   {
      super(context, variableElement);
   }

   @Override
   public D.Declared getSurrounding()
   {
      return Adapters.adapt(getApi(), ((TypeElement) getElement().getEnclosingElement()));
   }

   @Override
   public boolean isConstant()
   {
      return getElement().getConstantValue() != null;
   }

   @Override
   public Optional<Object> getConstantValue()
   {
      return Optional.ofNullable(getElement().getConstantValue());
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      FieldInitializationStep initializationStep = field()
            .annotate(getDirectAnnotationUsages())
            .modifier(getModifiers())
            .type(getType())
            .name(getName());

      if (!isConstant())
      {
         return initializationStep.renderDeclaration(renderingContext);
      }
      return initializationStep.initializer(getConstantValueOrThrow().toString())
                               .renderDeclaration(renderingContext);
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return getName();
   }
}
