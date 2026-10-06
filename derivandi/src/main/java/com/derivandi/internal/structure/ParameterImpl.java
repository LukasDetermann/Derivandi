package com.derivandi.internal.structure;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.parameter.ParameterAnnotateStep;
import com.derivandi.api.dsl.parameter.ParameterTypeStep;
import com.derivandi.api.dsl.parameter.ParameterVarargsStep;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Objects;

import static com.derivandi.api.dsl.JavaDsl.parameter;

public class ParameterImpl extends VariableImpl implements D.Parameter
{
   public ParameterImpl(SimpleContext context, VariableElement variableElement)
   {
      super(context, variableElement);
   }

   @Override
   public boolean isVarArgs()
   {
      List<D.Parameter> parameters = getSurrounding().getParameters();
      return getSurrounding().isVarArgs() &&
             parameters.get(parameters.size() - 1).equals(this);
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      ParameterAnnotateStep annotateStep = parameter().annotate(getDirectAnnotationUsages());

      ParameterTypeStep parameterTypeStep = annotateStep;
      if (isFinal())
      {
         parameterTypeStep = annotateStep.final_();
      }

      ParameterVarargsStep varargsStep = parameterTypeStep.type(getType())
                                                          .name(getName());

      if (!isVarArgs())
      {
         return varargsStep.renderDeclaration(renderingContext);
      }
      return varargsStep.varArgs().renderDeclaration(renderingContext);
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return getName();
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getName(),
                          getSurrounding());
   }

   @Override
   public D.Executable getSurrounding()
   {
      return Adapters.adapt(getApi(), ((ExecutableElement) getElement().getEnclosingElement()));
   }

   @Override
   public boolean equals(Object other)
   {
      if (other == this)
      {
         return true;
      }
      if (!(other instanceof D.Parameter otherVariable))
      {
         return false;
      }
      return Objects.equals(getName(), otherVariable.getName()) &&
             Objects.equals(getType(), otherVariable.getType()) &&
             Objects.equals(getModifiers(), otherVariable.getModifiers());
   }
}
