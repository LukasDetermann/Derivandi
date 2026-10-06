package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.type.NullImpl;

import javax.lang.model.type.NullType;

public class NullAdapter
{
   private final D.Null aNull;

   NullAdapter(D.Null aNull)
   {
      this.aNull = aNull;
   }

   public NullType toNullType()
   {
      //noinspection OverlyStrongTypeCast
      return ((NullImpl) aNull).getMirror();
   }
}
