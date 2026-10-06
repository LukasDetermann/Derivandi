package com.derivandi.internal.type;

import com.derivandi.api.D;
import com.derivandi.api.Origin;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.generic.GenericAndExtendsStep;
import com.derivandi.api.dsl.generic.GenericExtendsStep;
import com.derivandi.api.dsl.interface_.InterfaceRenderable;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.type.IntersectionType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.derivandi.api.adapter.Adapters.adapt;
import static com.derivandi.api.dsl.JavaDsl.generic;

public class IntersectionImpl extends TypeImpl<IntersectionType> implements D.Generic
{
   public IntersectionImpl(SimpleContext context, IntersectionType intersectionType)
   {
      super(context, intersectionType);
   }

   @Override
   public D.Type getBound()
   {
      return getBounds().getFirst();
   }

   @Override
   public List<D.Type> getBounds()
   {
      return getMirror().getBounds().stream()
                        .map(typeMirror -> adapt(getApi(), typeMirror))
                        .toList();
   }

   @Override
   public List<D.Interface> getAdditionalBounds()
   {
      List<D.Type> bounds = getBounds();
      if (bounds.size() <= 1)
      {
         return Collections.emptyList();
      }
      return bounds.stream().skip(1)
                   .map(D.Interface.class::cast)
                   .toList();
   }

   @Override
   public Optional<D.Type> getSuper()
   {
      return Optional.empty();
   }

   @Override
   public Object getEnclosing()
   {
      throw new IllegalStateException("can not get the enclosing Object for a generic backed by an intersection type");
   }

   @Override
   public Origin getOrigin()
   {
      return Origin.SOURCE_DECLARED;
   }

   @Override
   public D.Generic erasure()
   {
      return (D.Generic) adapt(getApi(), adapt(getApi()).toTypes().erasure(getMirror()));
   }

   @Override
   public List<D.AnnotationUsage> getAnnotationUsages()
   {
      return Collections.emptyList();
   }

   @Override
   public List<D.AnnotationUsage> getDirectAnnotationUsages()
   {
      return Collections.emptyList();
   }

   @Override
   public String getName()
   {
      return "";
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      GenericExtendsStep extendsStep = generic().name(getName());

      List<D.Type> bounds = getBounds();
      if (bounds.isEmpty())
      {
         return extendsStep.renderDeclaration(renderingContext);
      }
      GenericAndExtendsStep andExtendsStep = switch (bounds.getFirst())
      {
         case D.Class cClass -> extendsStep.extends_(cClass);
         case D.Interface cInterface -> extendsStep.extends_(cInterface);
         case D.Generic generic -> extendsStep.extends_(generic);
         default -> throw new IllegalStateException();
      };
      if (bounds.size() == 1)
      {
         return andExtendsStep.renderDeclaration(renderingContext);
      }

      for (D.Type additionalBound : bounds.subList(1, bounds.size()))
      {
         andExtendsStep = andExtendsStep.extends_(((InterfaceRenderable) additionalBound));
      }
      return extendsStep.renderDeclaration(renderingContext);
   }

   @Override
   public String renderType(RenderingContext renderingContext)
   {
      return renderName(renderingContext);
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return getName();
   }

   @Override
   public boolean equals(Object other)
   {
      return other instanceof D.Generic generic &&
             Objects.equals(getName(), generic.getName()) &&
             Objects.equals(getBounds(), generic.getBounds());
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getName(), getBounds());
   }

   @Override
   public String toString()
   {
      return "Generic{" +
             "name='" + getName()  + '\'' +
             ", bounds=" + getBounds() +
             '}';
   }
}
