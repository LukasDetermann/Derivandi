package com.derivandi.internal.structure;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Objects;

import static com.derivandi.api.dsl.JavaDsl.result;

public class ResultImpl
      implements D.Result
{
   private final SimpleContext context;
   private final TypeMirror typeMirror;

   ResultImpl(SimpleContext context, TypeMirror typeMirror)
   {
      this.context = context;
      this.typeMirror = typeMirror;
   }

   @Override
   public List<D.AnnotationUsage> getAnnotationUsages()
   {
      return getDirectAnnotationUsages();
   }

   @Override
   public List<D.AnnotationUsage> getDirectAnnotationUsages()
   {
      return Adapters.adapt(context, getTypeMirror(), getTypeMirror().getAnnotationMirrors());
   }

   @Override
   public D.Type getType()
   {
      return Adapters.adapt(context, getTypeMirror());
   }

   public TypeMirror getTypeMirror()
   {
      return typeMirror;
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      return result().annotate(getDirectAnnotationUsages())
                     .type(getType())
                     .renderDeclaration(renderingContext);
   }

   @Override
   public int hashCode()
   {
      return Objects.hashCode(getType());
   }

   @Override
   public boolean equals(Object other)
   {
      if (other == this)
      {
         return true;
      }
      if (!(other instanceof D.Result otherReturn))
      {
         return false;
      }
      return Objects.equals(getType(), otherReturn.getType());
   }
}
