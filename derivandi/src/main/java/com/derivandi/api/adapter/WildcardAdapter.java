package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.type.WildcardImpl;

import javax.lang.model.type.WildcardType;

public class WildcardAdapter
{
   private final D.Wildcard wildcard;

   WildcardAdapter(D.Wildcard wildcard)
   {
      this.wildcard = wildcard;
   }

   public WildcardType toWildcardType()
   {
      //noinspection OverlyStrongTypeCast
      return ((WildcardImpl) wildcard).getMirror();
   }
}
