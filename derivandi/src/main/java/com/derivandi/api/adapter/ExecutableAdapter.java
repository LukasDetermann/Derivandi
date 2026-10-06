package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.structure.ExecutableImpl;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

public class ExecutableAdapter
{
   private final D.Executable executable;

   ExecutableAdapter(D.Executable executable)
   {
      this.executable = executable;
   }

   public ExecutableType toExecutableType()
   {
      return ((ExecutableImpl) executable).getMirror();
   }

   public ExecutableElement toExecutableElement()
   {
      return ((ExecutableImpl) executable).getElement();
   }
}
