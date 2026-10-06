package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.structure.ModuleImpl;

import javax.lang.model.element.ModuleElement;
import javax.lang.model.type.NoType;

public class ModuleAdapter
{
   private final D.Module module;

   ModuleAdapter(D.Module module)
   {
      this.module = module;
   }

   public NoType toNoType()
   {
      return ((ModuleImpl) module).getMirror();
   }

   public ModuleElement toModuleElement()
   {
      return ((ModuleImpl) module).getElement();
   }
}
