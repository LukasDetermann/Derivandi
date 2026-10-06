package com.derivandi.api.generate;

import com.derivandi.api.D;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// A Typesafe [D.AnnotationUsage] will be generated for listed Annotations.
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface GenerateTypesafeUsage
{
   /// Annotations a Typesafe [D.AnnotationUsage] should be generated for
   /// @see GenerateTypesafeUsage
   Class<?>[] value();
}