package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.directive.RequiresImpl;

import javax.lang.model.element.ModuleElement;

public class RequiresAdapter
{
   private final D.Requires requires;

   RequiresAdapter(D.Requires requires)
   {
      this.requires = requires;
   }

   public ModuleElement.RequiresDirective toRequiresDirective()
   {
      return ((RequiresImpl) requires).getMirror();
   }
}
