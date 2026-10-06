package com.derivandi.internal.dsl;

import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.VariableTypeRenderable;
import com.derivandi.api.dsl.annotation_usage.AnnotationUsageRenderable;
import com.derivandi.api.dsl.parameter.*;

import java.util.ArrayList;
import java.util.List;

import static com.derivandi.internal.dsl.DslSupport.*;

public class ParameterDsl
      implements ParameterAnnotateStep,
                 ParameterNameStep,
                 ParameterVarargsStep
{
   private final List<Renderable> annotations = new ArrayList<>();
   private boolean isFinal = false;
   private Renderable type;
   private String name;
   private boolean isVarArgs = false;

   public ParameterDsl() {}

   private ParameterDsl(ParameterDsl other)
   {
      this.annotations.addAll(other.annotations);
      this.isFinal = other.isFinal;
      this.type = other.type;
      this.name = other.name;
      this.isVarArgs = other.isVarArgs;
   }

   @Override
   public ParameterAnnotateStep annotate(String... annotation)
   {
      return addArrayRenderer(new ParameterDsl(this),
                              annotation,
                              (renderingContext, string) -> '@' + string,
                              parameterDsl -> parameterDsl.annotations::add);
   }

   @Override
   public ParameterAnnotateStep annotate(List<? extends AnnotationUsageRenderable> annotation)
   {
      return addArrayRenderer(new ParameterDsl(this),
                              annotation,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              parameterDsl -> parameterDsl.annotations::add);
   }

   @Override
   public ParameterTypeStep final_()
   {
      return setType(new ParameterDsl(this), true, (parameterDsl, isFinal) -> parameterDsl.isFinal = isFinal);
   }

   @Override
   public ParameterNameStep type(String type)
   {
      return setType(new ParameterDsl(this),
                     type,
                     (parameterDsl, string) -> parameterDsl.type = context -> context.renderName(string));
   }

   @Override
   public ParameterNameStep type(VariableTypeRenderable variableType)
   {
      return setTypeRenderer(new ParameterDsl(this),
                             variableType,
                             (renderingContext, renderable) -> renderable.renderType(renderingContext),
                             (parameterDsl, renderer) -> parameterDsl.type = renderer);
   }

   @Override
   public ParameterVarargsStep name(String name)
   {
      return setType(new ParameterDsl(this), name, (parameterDsl, s) -> parameterDsl.name = s);
   }

   @Override
   public ParameterRenderable varArgs()
   {
      return setType(new ParameterDsl(this), true, (parameterDsl, isVarargs) -> parameterDsl.isVarArgs = isVarargs);
   }

   @Override
   public String renderDeclaration(RenderingContext context1)
   {
      return context1.withSurrounding(this, context ->
      {
         StringBuilder sb = new StringBuilder();

         renderElement(sb, annotations, " ", context, " ");

         if (isFinal)
         {
            sb.append("final ");
         }

         sb.append(type.render(context));

         if (isVarArgs)
         {
            sb.append("...");
         }
         sb.append(" ");
         sb.append(name);

         return sb.toString();
      });
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return name;
   }
}
