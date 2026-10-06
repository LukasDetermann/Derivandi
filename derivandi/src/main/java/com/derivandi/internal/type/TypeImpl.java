package com.derivandi.internal.type;

import com.derivandi.api.D;
import com.derivandi.api.processor.SimpleContext;
import com.derivandi.internal.ApiHolder;

import javax.lang.model.type.TypeMirror;

import static com.derivandi.api.adapter.Adapters.adapt;

public abstract class TypeImpl<MIRROR extends TypeMirror> implements ApiHolder
{
   private final SimpleContext context;

   private final MIRROR typeMirror;

   protected TypeImpl(SimpleContext context, MIRROR typeMirror)
   {
      this.context = context;
      this.typeMirror = typeMirror;
   }

   public boolean isSameType(D.Type type)
   {
      return adapt(getApi()).toTypes().isSameType(getMirror(), adapt(type).toTypeMirror());
   }

   public MIRROR getMirror()
   {
      return typeMirror;
   }

   @Override
   public SimpleContext getApi()
   {
      return context;
   }
}
