package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.type.ArrayImpl;

import javax.lang.model.type.ArrayType;

public class ArrayAdapter
{
   private final D.Array array;

   ArrayAdapter(D.Array array)
   {
      this.array = array;
   }

   public ArrayType toArrayType()
   {
      //noinspection OverlyStrongTypeCast
      return ((ArrayImpl) array).getMirror();
   }
}
