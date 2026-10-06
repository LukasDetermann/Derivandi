package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.structure.PackageImpl;

import javax.lang.model.element.PackageElement;
import javax.lang.model.type.NoType;

public class PackageAdapter
{
   private final D.Package aPackage;

   PackageAdapter(D.Package aPackage)
   {
      this.aPackage = aPackage;
   }

   public NoType toNoType()
   {
      return ((PackageImpl) aPackage).getMirror();
   }

   public PackageElement toPackageElement()
   {
      return ((PackageImpl) aPackage).getElement();
   }
}
