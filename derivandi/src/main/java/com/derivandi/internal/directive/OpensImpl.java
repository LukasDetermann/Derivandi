package com.derivandi.internal.directive;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.ModuleElement;
import java.util.List;
import java.util.Objects;

import static com.derivandi.api.dsl.JavaDsl.opens;

public class OpensImpl extends DirectiveImpl implements D.Opens
{
   private final ModuleElement.OpensDirective opensDirective;

   public OpensImpl(SimpleContext langModelContext, ModuleElement declaringModule, ModuleElement.OpensDirective opensDirective)
   {
      super(langModelContext, declaringModule);
      this.opensDirective = opensDirective;
   }

   @Override
   public D.Package getPackage()
   {
      return Adapters.adapt(getApi(), opensDirective.getPackage());
   }

   @Override
   public List<D.Module> getTargetModules()
   {
      return opensDirective.getTargetModules()
                           .stream()
                           .map(moduleElement -> Adapters.adapt(getApi(), moduleElement))
                           .toList();
   }

   @Override
   public boolean toAll()
   {
      return getTargetModules().isEmpty();
   }

   @Override
   public ModuleElement.OpensDirective getMirror()
   {
      return opensDirective;
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      return opens().package_(getPackage())
                    .to(getTargetModules())
                    .renderDeclaration(renderingContext);
   }

   @Override
   public final boolean equals(Object o)
   {
      return o instanceof D.Opens opens &&
             Objects.equals(getPackage(), opens.getPackage()) &&
             Objects.equals(getTargetModules(), opens.getTargetModules());
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getPackage(), getTargetModules());
   }

   @Override
   public String toString()
   {
      return "Opens{" +
             "package=" + getPackage() +
             ", targetModules=" + getTargetModules() +
             '}';
   }
}
