package com.derivandi.internal.dsl;


import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.annotation_usage.AnnotationUsageRenderable;
import com.derivandi.api.dsl.class_.ClassRenderable;
import com.derivandi.api.dsl.generic.GenericAndExtendsStep;
import com.derivandi.api.dsl.generic.GenericAnnotateStep;
import com.derivandi.api.dsl.generic.GenericExtendsStep;
import com.derivandi.api.dsl.generic.GenericRenderable;
import com.derivandi.api.dsl.interface_.InterfaceRenderable;
import com.derivandi.api.dsl.parameter.ParameterRenderable;

import java.util.ArrayList;
import java.util.List;

import static com.derivandi.internal.dsl.DslSupport.*;

public class GenericDsl
      implements GenericAnnotateStep,
                 GenericExtendsStep,
                 GenericAndExtendsStep
{
   private final List<Renderable> annotations = new ArrayList<>();
   private String name;
   private final List<Renderable> extends_ = new ArrayList<>();

   public GenericDsl() {}

   private GenericDsl(GenericDsl other)
   {
      this.annotations.addAll(other.annotations);
      this.name = other.name;
      this.extends_.addAll(other.extends_);
   }

   @Override
   public GenericAnnotateStep annotate(String... annotation)
   {
      return addArrayRenderer(new GenericDsl(this),
                              annotation,
                              (context, string) -> '@' + context.renderName(string),
                              genericDsl -> genericDsl.annotations::add);
   }

   @Override
   public GenericAnnotateStep annotate(List<? extends AnnotationUsageRenderable> annotation)
   {
      return addArrayRenderer(new GenericDsl(this),
                              annotation,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              genericDsl -> genericDsl.annotations::add);
   }

   @Override
   public GenericExtendsStep name(String name)
   {
      return setType(new GenericDsl(this),
                     name,
                     (genericDsl, string) -> genericDsl.name = string);
   }

   @Override
   public GenericAndExtendsStep extends_(String bound)
   {
      return addTypeRenderer(new GenericDsl(this),
                             bound,
                             genericDsl -> genericDsl.extends_::add);
   }

   @Override
   public GenericAndExtendsStep extends_(ClassRenderable bound)
   {
      return setType(new GenericDsl(this),
                     bound,
                     (genericDsl, classRenderable) -> genericDsl.extends_.add(classRenderable::renderQualifiedName));
   }

   @Override
   public GenericAndExtendsStep extends_(InterfaceRenderable bound)
   {
      return setType(new GenericDsl(this),
                     bound,
                     (genericDsl, interfaceRenderable) -> genericDsl.extends_.add(interfaceRenderable::renderQualifiedName));
   }

   @Override
   public GenericAndExtendsStep extends_(GenericRenderable bound)
   {
      return setType(new GenericDsl(this),
                     bound,
                     (genericDsl, genericRenderable) -> genericDsl.extends_.add(genericRenderable::renderName));
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      StringBuilder sb = new StringBuilder();

      renderElement(sb, annotations, " ", renderingContext, " ");

      sb.append(renderType(renderingContext));

      return sb.toString();
   }

   @Override
   public String renderType(RenderingContext context)
   {
      if (!context.getSurrounding().isEmpty() && context.getSurrounding().getLast() instanceof ParameterRenderable)
      {
         return renderName(context);
      }
      StringBuilder sb = new StringBuilder();
      sb.append(name);

      renderElement(sb, " extends ", extends_, context, " & ");
      return sb.toString();
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return name;
   }
}
