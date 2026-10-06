package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.directive.OpensImpl;

import javax.lang.model.element.ModuleElement;

public class OpensAdapter
{
   private final D.Opens opens;

   OpensAdapter(D.Opens opens)
   {
      this.opens = opens;
   }

   public ModuleElement.OpensDirective toOpensDirective()
   {
      return ((OpensImpl) opens).getMirror();
   }
}
