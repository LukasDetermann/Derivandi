package com.derivandi.internal.dsl;


import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.annotation_usage.AnnotationUsageRenderable;
import com.derivandi.api.dsl.class_.ClassRenderable;
import com.derivandi.api.dsl.constructor.ConstructorRenderable;
import com.derivandi.api.dsl.declared.DeclaredRenderable;
import com.derivandi.api.dsl.method.MethodRenderable;
import com.derivandi.api.dsl.receiver.ReceiverAnnotateStep;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static com.derivandi.internal.dsl.DslSupport.addArrayRenderer;
import static com.derivandi.internal.dsl.DslSupport.renderElement;

public class ReceiverDsl
      implements ReceiverAnnotateStep
{
   private final List<Renderable> annotations = new ArrayList<>();

   public ReceiverDsl()
   {
   }

   private ReceiverDsl(ReceiverDsl other)
   {
      this.annotations.addAll(other.annotations);
   }

   @Override
   public ReceiverAnnotateStep annotate(String... annotation)
   {
      return addArrayRenderer(new ReceiverDsl(this),
                              annotation,
                              (renderingContext, string) -> '@' + string,
                              receiverDsl -> receiverDsl.annotations::add);
   }

   @Override
   public ReceiverAnnotateStep annotate(List<? extends AnnotationUsageRenderable> annotation)
   {
      return addArrayRenderer(new ReceiverDsl(this),
                              annotation,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              receiverDsl -> receiverDsl.annotations::add);
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      Deque<Object> surrounding = renderingContext.getSurrounding();
      Object first = surrounding.pollLast();
      Object second = surrounding.pollLast();
      Object third = surrounding.pollLast();

      boolean isMethodReceiver = first instanceof MethodRenderable && second instanceof DeclaredRenderable;
      boolean isConstructorReceiver = first instanceof ConstructorRenderable &&
                                      second instanceof ClassRenderable &&
                                      third instanceof DeclaredRenderable;

      if (!isMethodReceiver && !isConstructorReceiver)
      {
         throw new IllegalStateException("A Receiver must be contained in a method or inner class constructor");
      }

      StringBuilder sb = new StringBuilder();

      renderElement(sb, annotations, " ", renderingContext, " ");

      DeclaredRenderable renderable;
      if (isMethodReceiver)
      {
         renderable = (DeclaredRenderable) second;
      }
      else
      {
         renderable = (DeclaredRenderable) third;
      }

      sb.append(renderable.renderType(renderingContext));
      sb.append(' ');
      sb.append(renderable.renderName(renderingContext));
      sb.append(".this");
      return sb.toString();
   }
}
