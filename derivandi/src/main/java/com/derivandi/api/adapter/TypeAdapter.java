package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.type.TypeImpl;

import javax.lang.model.type.TypeMirror;

public class TypeAdapter
{
   private final D.Type type;

   TypeAdapter(D.Type type)
   {
      this.type = type;
   }

   public TypeMirror toTypeMirror()
   {
      //noinspection unchecked
      return ((TypeImpl<TypeMirror>) type).getMirror();
   }
}
