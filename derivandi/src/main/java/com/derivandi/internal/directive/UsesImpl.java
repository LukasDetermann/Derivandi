package com.derivandi.internal.directive;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.JavaDsl;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.ModuleElement;
import java.util.Objects;

public class UsesImpl extends DirectiveImpl implements D.Uses
{
   private final ModuleElement.UsesDirective usesDirective;

   public UsesImpl(SimpleContext context, ModuleElement declaringModule, ModuleElement.UsesDirective usesDirective)
   {
      super(context, declaringModule);
      this.usesDirective = usesDirective;
   }

   @Override
   public D.Declared getService()
   {
      return Adapters.adapt(getApi(), usesDirective.getService());
   }

   @Override
   public ModuleElement.UsesDirective getMirror()
   {
      return usesDirective;
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      return JavaDsl.uses(getService()).renderDeclaration(renderingContext);
   }

   @Override
   public boolean equals(Object other)
   {
      return other instanceof D.Uses exports &&
             Objects.equals(getService(), exports.getService());

   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getService());
   }

   @Override
   public String toString()
   {
      return "Uses{" +
             "service=" + getService() +
             '}';
   }
}
