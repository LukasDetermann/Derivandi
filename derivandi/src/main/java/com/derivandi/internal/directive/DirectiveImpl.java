package com.derivandi.internal.directive;

import com.derivandi.api.Origin;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.ModuleElement;

import static com.derivandi.api.adapter.Adapters.adapt;

public abstract class DirectiveImpl
{
   private final SimpleContext context;
   private final ModuleElement declaringModule;

   protected DirectiveImpl(SimpleContext context, ModuleElement declaringModule)
   {
      this.context = context;
      this.declaringModule = declaringModule;
   }

   public SimpleContext getApi()
   {
      return context;
   }

   public Origin getOrigin()
   {
      return adapt(adapt(getApi()).toElements().getOrigin(declaringModule, getMirror()));
   }

   public abstract ModuleElement.Directive getMirror();

   public ModuleElement getDeclaringModule()
   {
      return declaringModule;
   }
}
