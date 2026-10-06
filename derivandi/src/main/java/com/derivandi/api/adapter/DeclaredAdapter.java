package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.type.DeclaredImpl;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

public class DeclaredAdapter
{
   private final D.Declared declared;

   DeclaredAdapter(D.Declared declared)
   {
      this.declared = declared;
   }

   public DeclaredType toDeclaredType()
   {
      //noinspection OverlyStrongTypeCast
      return ((DeclaredImpl) declared).getMirror();
   }

   public TypeElement toTypeElement()
   {
      return ((DeclaredImpl) declared).getElement();
   }
}
