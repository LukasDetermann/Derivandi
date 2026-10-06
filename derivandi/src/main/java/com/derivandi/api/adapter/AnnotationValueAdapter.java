package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.annotationvalue.AnnotationValueImpl;

import javax.lang.model.element.AnnotationValue;
import java.util.Optional;

public class AnnotationValueAdapter
{
   private final D.AnnotationValue annotationValue;

   AnnotationValueAdapter(D.AnnotationValue annotationValue)
   {
      this.annotationValue = annotationValue;
   }

   public Optional<AnnotationValue> toAnnotationValue()
   {
      return ((AnnotationValueImpl<?>) annotationValue).getAnnotationValue();
   }
   public AnnotationValue toDefaultAnnotationValue()
   {
      return ((AnnotationValueImpl<?>) annotationValue).getDefaultAnnotationValue();
   }
}