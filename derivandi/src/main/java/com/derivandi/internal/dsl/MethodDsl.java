package com.derivandi.internal.dsl;


import com.derivandi.api.Modifier;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.TypeRenderable;
import com.derivandi.api.dsl.annotation.AnnotationRenderable;
import com.derivandi.api.dsl.annotation_usage.AnnotationUsageRenderable;
import com.derivandi.api.dsl.class_.ClassRenderable;
import com.derivandi.api.dsl.generic.GenericRenderable;
import com.derivandi.api.dsl.method.*;
import com.derivandi.api.dsl.parameter.ParameterRenderable;
import com.derivandi.api.dsl.receiver.ReceiverRenderable;
import com.derivandi.api.dsl.result.ResultRenderable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.derivandi.api.dsl.RenderingContext.createRenderingContext;
import static com.derivandi.internal.dsl.DslSupport.*;

public class MethodDsl
      implements MethodJavaDocStep,
                 MethodNameStep,
                 MethodReceiverStep
{
   private Renderable javadoc;
   private final List<Renderable> annotations = new ArrayList<>();
   private final List<Renderable> modifiers = new ArrayList<>();
   private final List<Renderable> generics = new ArrayList<>();
   private Renderable result;
   private String name;
   private Renderable receiver;
   private final List<Renderable> parameters = new ArrayList<>();
   private final List<Renderable> exceptions = new ArrayList<>();
   private Renderable body;

   public MethodDsl() {}

   private MethodDsl(MethodDsl other)
   {
      this.javadoc = other.javadoc;
      this.annotations.addAll(other.annotations);
      this.modifiers.addAll(other.modifiers);
      this.generics.addAll(other.generics);
      this.result = other.result;
      this.name = other.name;
      this.receiver = other.receiver;
      this.parameters.addAll(other.parameters);
      this.exceptions.addAll(other.exceptions);
      this.body = other.body;
   }

   @Override
   public MethodAnnotateStep javadoc(String javadoc)
   {
      return setTypeRenderer(new MethodDsl(this), javadoc, DslSupport::indent, (methodDsl, function) -> methodDsl.javadoc = function);
   }

   @Override
   public MethodReceiverStep name(String name)
   {
      return setType(new MethodDsl(this), name, (methodDsl, s) -> methodDsl.name = s);
   }

   @Override
   public MethodParameterStep receiver(String receiver)
   {
      return setType(new MethodDsl(this), receiver, (methodDsl, string) -> methodDsl.receiver = renderingContext -> string);
   }

   @Override
   public MethodParameterStep receiver(ReceiverRenderable receiver)
   {
      return setTypeRenderer(new MethodDsl(this),
                             receiver,
                             (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                             (methodDsl, receiverRenderable) -> methodDsl.receiver = receiverRenderable);
   }

   @Override
   public MethodParameterStep parameter(String... parameter)
   {
      return addArray2(new MethodDsl(this), parameter, (methodDsl, string) -> methodDsl.parameters.add(renderingContext -> string));
   }

   @Override
   public MethodParameterStep parameter(List<? extends ParameterRenderable> parameter)
   {
      return addArrayRenderer(new MethodDsl(this),
                              parameter,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              methodDsl -> methodDsl.parameters::add);
   }

   @Override
   public MethodThrowsStep throws_(String... exception)
   {
      return addArray2(new MethodDsl(this),
                       exception,
                       (methodDsl, string) -> methodDsl.exceptions.add(context -> context.renderName(string)));
   }

   @Override
   public MethodThrowsStep throws_(List<? extends ClassRenderable> exception)
   {
      return addArrayRenderer(new MethodDsl(this),
                              exception,
                              (renderingContext, classRenderable) -> classRenderable.renderName(renderingContext),
                              methodDsl -> methodDsl.exceptions::add);
   }

   @Override
   public MethodRenderable body(String body)
   {
      return setType(new MethodDsl(this), body, (methodDsl, s) -> methodDsl.body = context -> indent(context, s));
   }

   @Override
   public MethodAnnotateStep annotate(String... annotation)
   {
      return addArrayRenderer(new MethodDsl(this),
                              annotation,
                              (context, string) -> indent(context, '@' + context.renderName(string)),
                              methodDsl -> methodDsl.annotations::add);
   }

   @Override
   public MethodAnnotateStep annotate(List<? extends AnnotationUsageRenderable> annotation)
   {
      return addArrayRenderer(new MethodDsl(this),
                              annotation,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              methodDsl -> methodDsl.annotations::add);
   }

   @Override
   public MethodModifierStep modifier(String... modifiers)
   {
      return addArrayRenderer(new MethodDsl(this), modifiers, methodDsl -> methodDsl.modifiers::add);
   }

   @Override
   public MethodModifierStep modifier(Set<Modifier> modifiers)
   {
      return addArray2(new MethodDsl(this),
                       modifiers,
                       (methodDsl, modifier) -> methodDsl.modifiers.add(modifier::render));
   }

   @Override
   public MethodModifierStep abstract_()
   {
      return setType(new MethodDsl(this),
                     Modifier.ABSTRACT,
                     (methodDsl, modifier) -> methodDsl.modifiers.add(modifier::render));
   }

   @Override
   public MethodModifierStep public_()
   {
      return setType(new MethodDsl(this),
                     Modifier.PUBLIC,
                     (methodDsl, modifier) -> methodDsl.modifiers.add(modifier::render));
   }

   @Override
   public MethodModifierStep protected_()
   {
      return setType(new MethodDsl(this),
                     Modifier.PROTECTED,
                     (methodDsl, modifier) -> methodDsl.modifiers.add(modifier::render));
   }

   @Override
   public MethodModifierStep private_()
   {
      return setType(new MethodDsl(this),
                     Modifier.PRIVATE,
                     (methodDsl, modifier) -> methodDsl.modifiers.add(modifier::render));
   }

   @Override
   public MethodModifierStep default_()
   {
      return setType(new MethodDsl(this),
                     Modifier.DEFAULT,
                     (methodDsl, modifier) -> methodDsl.modifiers.add(modifier::render));
   }

   @Override
   public MethodModifierStep final_()
   {
      return setType(new MethodDsl(this),
                     Modifier.FINAL,
                     (methodDsl, modifier) -> methodDsl.modifiers.add(modifier::render));
   }

   @Override
   public MethodModifierStep native_()
   {
      return setType(new MethodDsl(this),
                     Modifier.NATIVE,
                     (methodDsl, modifier) -> methodDsl.modifiers.add(modifier::render));
   }

   @Override
   public MethodModifierStep static_()
   {
      return setType(new MethodDsl(this),
                     Modifier.STATIC,
                     (methodDsl, modifier) -> methodDsl.modifiers.add(modifier::render));
   }

   @Override
   public MethodModifierStep strictfp_()
   {
      return setType(new MethodDsl(this),
                     Modifier.STRICTFP,
                     (methodDsl, modifier) -> methodDsl.modifiers.add(modifier::render));
   }

   @Override
   public MethodGenericStep genericDeclaration(String... genericDeclarations)
   {
      return addArray2(new MethodDsl(this), genericDeclarations, (methodDsl, string) -> methodDsl.generics.add(renderingContext -> string));
   }

   @Override
   public MethodGenericStep genericDeclaration(List<? extends GenericRenderable> genericDeclarations)
   {
      return addArrayRenderer(new MethodDsl(this),
                              genericDeclarations,
                              (renderingContext, genericRenderable) -> genericRenderable.renderDeclaration(renderingContext),
                              methodDsl -> methodDsl.generics::add);
   }

   @Override
   public MethodNameStep result(String result)
   {
      return setType(new MethodDsl(this), result, (methodDsl, string) -> methodDsl.result = renderingContext -> result);
   }

   @Override
   public MethodNameStep result(ResultRenderable result)
   {
      return setTypeRenderer(new MethodDsl(this),
                             result,
                             (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                             (methodDsl, resultRenderable) -> methodDsl.result = resultRenderable);
   }

   @Override
   public MethodNameStep resultType(String resultType)
   {
      return setType(new MethodDsl(this),
                     resultType,
                     (methodDsl, string) -> methodDsl.result = renderingContext -> renderingContext.renderName(string));
   }

   @Override
   public MethodNameStep resultType(TypeRenderable resultType)
   {
      return setType(new MethodDsl(this),
                     resultType,
                     (methodDsl, typeNameRenderable) -> methodDsl.result = typeNameRenderable::renderType);
   }

   @Override
   public MethodNameStep surroundingResultType()
   {
      return setType(new MethodDsl(this),
                     (Renderable) renderingContext -> renderingContext
                           .getSurrounding()
                           .stream()
                           .filter(TypeRenderable.class::isInstance)
                           .map(TypeRenderable.class::cast)
                           .map(typeRenderable -> typeRenderable.renderType(renderingContext))
                           .findFirst()
                           .orElseThrow(() -> new IllegalStateException("Result needs to be contained in a TypeRenderable")),
                     (resultDsl, renderable) -> resultDsl.result = renderable);
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      RenderingContext context1 = createRenderingContext(renderingContext);
      return context1.withSurrounding(this, context ->
      {
         StringBuilder sb = new StringBuilder();
         if (javadoc != null)
         {
            sb.append(javadoc.render(context));
            sb.append("\n");
         }

         renderElement(sb, annotations, context, "\n", new Padding(null, context.getLineIndentation(), null, "\n"));
         sb.append(context.getLineIndentation());
         renderElement(sb, modifiers, " ", context, " ");
         renderElement(sb, "<", generics, "> ", context, ", ");

         sb.append(result.render(context))
           .append(' ')
           .append(name)
           .append('(');

         if (receiver != null)
         {
            sb.append(receiver.render(context));
            if (!parameters.isEmpty())
            {
               sb.append(", ");
            }
         }

         renderElement(sb, parameters, context, ", ");
         sb.append(')');

         renderElement(sb, " throws ", exceptions, context, ", ");

         if (!(renderingContext.getSurrounding().peekFirst() instanceof AnnotationRenderable))
         {
            sb.append(" {");
            if (body != null)
            {
               RenderingContext indented = createRenderingContext(context);
               indented.incrementIndentationLevel();

               sb.append('\n')
                 .append(body.render(indented))
                 .append('\n')
                 .append(context.getLineIndentation());
            }
            sb.append("}");
         }
         return sb.toString();
      });
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return name;
   }
}
