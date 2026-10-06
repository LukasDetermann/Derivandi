package com.derivandi.internal.directive;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.ModuleElement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.derivandi.api.dsl.JavaDsl.exports;

public class ExportsImpl extends DirectiveImpl implements D.Exports
{

   private final ModuleElement.ExportsDirective exportsDirective;

   public ExportsImpl(SimpleContext context, ModuleElement declaringModule, ModuleElement.ExportsDirective exportsDirective)
   {
      super(context, declaringModule);
      this.exportsDirective = exportsDirective;
   }

   @Override
   public D.Package getPackage()
   {
      return Adapters.adapt(getApi(), exportsDirective.getPackage());
   }

   @Override
   public List<D.Module> getTargetModules()
   {
      return exportsDirective.getTargetModules() == null ?
             Collections.emptyList() :
             exportsDirective.getTargetModules()
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
   public ModuleElement.ExportsDirective getMirror()
   {
      return exportsDirective;
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      return exports().package_(getPackage())
                      .to(getTargetModules())
                      .renderDeclaration(renderingContext);
   }

   @Override
   public final boolean equals(Object o)
   {
      return o instanceof D.Exports exports &&
             Objects.equals(getPackage(), exports.getPackage()) &&
             Objects.equals(getTargetModules(), exports.getTargetModules());
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getPackage(), getTargetModules());
   }

   @Override
   public String toString()
   {
      return "Exports{" +
             "package=" + getPackage() +
             ", targetModules=" + getTargetModules() +
             '}';
   }
}
