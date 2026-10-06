package com.derivandi.internal.structure;

import com.derivandi.api.D;
import com.derivandi.api.Origin;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Objects;

import static com.derivandi.api.adapter.Adapters.adapt;
import static com.derivandi.api.dsl.JavaDsl.recordComponent;

public class RecordComponentImpl implements D.RecordComponent
{
   private final RecordComponentElement recordComponentElement;
   private final SimpleContext context;
   private final TypeMirror typeMirror;

   public RecordComponentImpl(SimpleContext context, RecordComponentElement recordComponentElement)
   {
      this.typeMirror = recordComponentElement.asType();
      this.context = context;
      this.recordComponentElement = recordComponentElement;
   }

   @Override
   public boolean isSubtypeOf(D.Type type)
   {
      return adapt(getApi()).toTypes().isSubtype(getMirror(), adapt(type).toTypeMirror());
   }

   @Override
   public boolean isAssignableFrom(D.Type type)
   {
      return adapt(getApi()).toTypes().isAssignable(getMirror(), adapt(type).toTypeMirror());
   }

   @Override
   public D.Record getRecord()
   {
      return (D.Record) adapt(getApi(), ((TypeElement) getElement().getEnclosingElement()));
   }

   @Override
   public D.Type getType()
   {
      return adapt(getApi(), getElement().asType());
   }

   @Override
   public D.Method getGetter()
   {
      return (D.Method) adapt(getApi(), getElement().getAccessor());
   }

   @Override
   public Origin getOrigin()
   {
      return adapt(adapt(getApi()).toElements().getOrigin(getElement()));
   }

   public RecordComponentElement getElement()
   {
      return recordComponentElement;
   }

   @Override
   public D.Module getModule()
   {
      return adapt(getApi(), adapt(getApi()).toElements().getModuleOf(getElement()));
   }

   @Override
   public String getName()
   {
      return getElement().getSimpleName().toString();
   }

   @Override
   public List<D.AnnotationUsage> getAnnotationUsages()
   {
      return adapt(getApi(), getElement(), adapt(getApi()).toElements().getAllAnnotationMirrors(getElement()));
   }

   @Override
   public List<D.AnnotationUsage> getDirectAnnotationUsages()
   {
      return adapt(getApi(), getElement(), getElement().getAnnotationMirrors());
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      return recordComponent().annotate(getDirectAnnotationUsages())
                              .type(getType())
                              .name(getName())
                              .renderDeclaration(renderingContext);
   }

   @Override
   public String toString()
   {
      return getElement().toString();
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getName(), getType());
   }

   @Override
   public boolean equals(Object other)
   {
      if (other == this)
      {
         return true;
      }
      if (!(other instanceof D.RecordComponent otherRecordComponent))
      {
         return false;
      }
      return Objects.equals(getName(), otherRecordComponent.getName()) &&
             Objects.equals(getType(), otherRecordComponent.getType());
   }

   public TypeMirror getMirror()
   {
      return typeMirror;
   }

   public SimpleContext getApi()
   {
      return context;
   }
}
