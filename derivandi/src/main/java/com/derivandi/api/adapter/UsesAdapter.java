package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.directive.UsesImpl;

import javax.lang.model.element.ModuleElement;

public class UsesAdapter
{
   private final D.Uses uses;

   UsesAdapter(D.Uses uses)
   {
      this.uses = uses;
   }

   public ModuleElement.UsesDirective toUsesDirective()
   {
      return ((UsesImpl) uses).getMirror();
   }
}
