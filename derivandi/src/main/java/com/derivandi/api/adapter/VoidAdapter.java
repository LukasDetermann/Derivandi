package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.type.VoidImpl;

import javax.lang.model.type.NoType;

public class VoidAdapter
{
   private final D.Void aVoid;

   VoidAdapter(D.Void aVoid)
   {
      this.aVoid = aVoid;
   }

   public NoType toNoType()
   {
      //noinspection OverlyStrongTypeCast
      return ((VoidImpl) aVoid).getMirror();
   }
}
