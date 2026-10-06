package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.type.PrimitiveImpl;

import javax.lang.model.type.PrimitiveType;

public class PrimitiveAdapter
{
   private final D.Primitive primitive;

   PrimitiveAdapter(D.Primitive primitive)
   {
      this.primitive = primitive;
   }

   public PrimitiveType toPrimitiveType()
   {
      //noinspection OverlyStrongTypeCast
      return ((PrimitiveImpl) primitive).getMirror();
   }
}
