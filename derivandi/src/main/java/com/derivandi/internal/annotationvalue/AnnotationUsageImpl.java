package com.derivandi.internal.annotationvalue;

import com.derivandi.api.D;
import com.derivandi.api.Origin;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.annotation_usage.AnnotationUsageNameStep;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.derivandi.api.adapter.Adapters.adapt;
import static com.derivandi.api.dsl.JavaDsl.annotationUsage;

public class AnnotationUsageImpl
      implements D.AnnotationUsage
{
   private final SimpleContext context;
   private final AnnotationMirror annotationMirror;
   private final AnnotatedConstruct annotated;

   private Map<D.Method, D.AnnotationValue> values;

   public AnnotationUsageImpl(SimpleContext context, AnnotatedConstruct annotated, AnnotationMirror annotationMirror)
   {
      this.context = Objects.requireNonNull(context);
      this.annotationMirror = Objects.requireNonNull(annotationMirror);
      this.annotated = annotated;
   }

   @Override
   public Map<D.Method, D.AnnotationValue> getValues()
   {
      if (values == null)
      {
         values = new LinkedHashMap<>();

         Map<? extends ExecutableElement, ? extends AnnotationValue> withoutDefaults = annotationMirror.getElementValues();

         Map<? extends ExecutableElement, ? extends AnnotationValue> withDefaults =
               adapt(getApi()).toElements().getElementValuesWithDefaults(annotationMirror);

         for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : withDefaults.entrySet())
         {
            values.put((D.Method) Adapters.adapt(context, entry.getKey()),
                       Adapters.adapt(context, withoutDefaults.get(entry.getKey()), withDefaults.get(entry.getKey())));
         }
      }
      return values;
   }

   @Override
   public Origin getOrigin()
   {
      if (annotated == null)
      {
         throw new IllegalStateException();
      }
      return adapt(adapt(getApi()).toElements().getOrigin(annotated, getAnnotationMirror()));
   }

   @Override
   public D.Annotation getAnnotation()
   {
      return (D.Annotation) adapt(getApi(), annotationMirror.getAnnotationType());
   }

   @Override
   public String toString()
   {
      return annotationMirror.toString();
   }

   public AnnotationMirror getAnnotationMirror()
   {
      return annotationMirror;
   }

   public SimpleContext getApi()
   {
      return context;
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      AnnotationUsageNameStep nameStep = annotationUsage().type(getAnnotation());

      for (Map.Entry<? extends D.Method, ? extends D.AnnotationValue> entry : getValues().entrySet())
      {
         nameStep = nameStep.name(entry.getKey().getName()).value(entry.getValue());
      }
      return nameStep.renderDeclaration(renderingContext);
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getAnnotation(),
                          getValues());
   }

   @Override
   public boolean equals(Object other)
   {
      if (other == this)
      {
         return true;
      }
      if (!(other instanceof D.AnnotationUsage otherAnnotationUsage))
      {
         return false;
      }
      return Objects.equals(getAnnotation(), otherAnnotationUsage.getAnnotation()) &&
             Objects.equals(getValues(), otherAnnotationUsage.getValues());
   }
}
