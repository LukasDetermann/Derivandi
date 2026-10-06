package com.derivandi.internal.type;

import com.derivandi.api.D;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import static com.derivandi.api.dsl.JavaDsl.annotation;
import static com.derivandi.api.dsl.JavaDsl.innerAnnotation;

public class AnnotationImpl extends DeclaredImpl implements D.Annotation
{
   public AnnotationImpl(SimpleContext context, DeclaredType declaredTypeMirror)
   {
      super(context, declaredTypeMirror);
   }

   public AnnotationImpl(SimpleContext context, TypeElement typeElement)
   {
      super(context, typeElement);
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      return (switch (getNesting())
              {
                 case OUTER -> annotation().package_(getPackage());
                 case INNER -> innerAnnotation().outer(getSurrounding().orElseThrow());
              }).annotate(getDirectAnnotationUsages())
                .modifier(getModifiers())
                .name(getName())
                .field(getFields())
                .method(getMethods())
                .renderDeclaration(renderingContext);
   }

   @Override
   public String renderType(RenderingContext renderingContext)
   {
      return (switch (getNesting())
              {
                 case OUTER -> annotation().package_(getPackage());
                 case INNER -> innerAnnotation().outer(getSurrounding().orElseThrow());
              }).name(getName())
                .renderType(renderingContext);
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return renderingContext.renderName(getQualifiedName());
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
   public boolean equals(Object other)
   {
      return equals(D.Annotation.class, other);
   }

   @Override
   public String toString()
   {
      return toString("Annotation");
   }
}
