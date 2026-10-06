package com.derivandi.internal.structure;

import com.derivandi.api.D;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.constructor.ConstructorGenericStep;
import com.derivandi.api.dsl.constructor.ConstructorParameterStep;
import com.derivandi.api.dsl.constructor.ConstructorReceiverStep;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.ExecutableElement;

import static com.derivandi.api.adapter.Adapters.adapt;
import static com.derivandi.api.dsl.JavaDsl.constructor;

public class ConstructorImpl
      extends ExecutableImpl
      implements D.Constructor
{
   public ConstructorImpl(SimpleContext context, ExecutableElement executableElement)
   {
      super(context, executableElement);
   }

   @Override
   public boolean isCompact()
   {
      return adapt(getApi()).toElements().isCompactConstructor(getElement());
   }

   @Override
   public boolean isCanonical()
   {
      return adapt(getApi()).toElements().isCanonicalConstructor(getElement());
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      ConstructorGenericStep genericStep = constructor()
            .annotate(getDirectAnnotationUsages())
            .modifier(getModifiers()).
            genericDeclaration(getGenericDeclarations());

      D.Declared type = getSurrounding();
      ConstructorReceiverStep receiverStep = switch (type)
      {
         case D.Class cClass -> genericStep.type(cClass);
         case D.Enum cEnum -> genericStep.type(cEnum);
         case D.Record cRecord -> genericStep.type(cRecord);
         default -> throw new IllegalStateException("Unexpected value: " + type);
      };

      ConstructorParameterStep parameterStep = getReceiver()
            .map(receiverStep::receiver)
            .orElse(receiverStep);

      return parameterStep.parameter(getParameters())
                          .throws_(getThrows())
                          .renderDeclaration(renderingContext);
   }
}
