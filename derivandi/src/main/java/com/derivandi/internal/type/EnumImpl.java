package com.derivandi.internal.type;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.util.List;

import static com.derivandi.api.dsl.JavaDsl.enum_;
import static com.derivandi.api.dsl.JavaDsl.innerEnum;

public class EnumImpl
      extends DeclaredImpl
      implements D.Enum
{
   public EnumImpl(SimpleContext context, DeclaredType declaredTypeMirror)
   {
      super(context, declaredTypeMirror);
   }

   public EnumImpl(SimpleContext context, TypeElement typeElement)
   {
      super(context, typeElement);
   }

   @Override
   public List<D.EnumConstant> getEnumConstants()
   {
      return getElement().getEnclosedElements()
                         .stream()
                         .filter(element -> ElementKind.ENUM_CONSTANT.equals(element.getKind()))
                         .map(VariableElement.class::cast)
                         .map(variableElement -> Adapters.adapt(getApi(), variableElement))
                         .map(D.EnumConstant.class::cast)
                         .toList();
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      return (switch (getNesting())
              {
                 case OUTER -> enum_().package_(getPackage());
                 case INNER -> innerEnum().outer(getSurrounding().orElseThrow());
              }).annotate(getDirectAnnotationUsages())
                .modifier(getModifiers())
                .name(getName())
                .implements_(getDirectInterfaces())
                .enumConstant(getEnumConstants())
                .field(getFields())
                .method(getMethods())
                .constructor(getConstructors())
                .renderDeclaration(renderingContext);
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
   public String renderType(RenderingContext renderingContext)
   {
      return (switch (getNesting())
              {
                 case OUTER -> enum_().package_(getPackage());
                 case INNER -> innerEnum().outer(getSurrounding().orElseThrow());
              }).name(getName())
                .renderType(renderingContext);
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return renderingContext.renderName(renderQualifiedName(renderingContext));
   }

   @Override
   public boolean equals(Object other)
   {
      return equals(D.Enum.class, other);
   }

   @Override
   public String toString()
   {
      return toString("Enum");
   }
}
