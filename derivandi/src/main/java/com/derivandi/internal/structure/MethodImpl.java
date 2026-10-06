package com.derivandi.internal.structure;

import com.derivandi.api.D;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.method.MethodReceiverStep;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.ExecutableElement;

import static com.derivandi.api.dsl.JavaDsl.method;

public class MethodImpl
      extends ExecutableImpl
      implements D.Method
{
   public MethodImpl(SimpleContext context, ExecutableElement executableElement)
   {
      super(context, executableElement);
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      MethodReceiverStep receiverStep = method()
            .annotate(getDirectAnnotationUsages())
            .modifier(getModifiers())
            .genericDeclaration(getGenericDeclarations())
            .result(getResult())
            .name(getName());

      return getReceiver().map(receiverStep::receiver).orElse(receiverStep)
                          .parameter(getParameters())
                          .throws_(getThrows())
                          .renderDeclaration(renderingContext);
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return getName();
   }
}
