package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.directive.ExportsImpl;

import javax.lang.model.element.ModuleElement;

public class ExportsAdapter
{
   private final D.Exports exports;

   ExportsAdapter(D.Exports exports)
   {
      this.exports = exports;
   }

   public ModuleElement.ExportsDirective toExportsDirective()
   {
      return ((ExportsImpl) exports).getMirror();
   }
}
