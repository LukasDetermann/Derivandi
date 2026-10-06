package com.derivandi.internal.directive;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.ModuleElement;
import java.util.List;
import java.util.Objects;

import static com.derivandi.api.dsl.JavaDsl.provides;

public class ProvidesImpl extends DirectiveImpl implements D.Provides
{
   private final ModuleElement.ProvidesDirective providesDirective;

   public ProvidesImpl(SimpleContext context, ModuleElement declaringModule, ModuleElement.ProvidesDirective providesDirective)
   {
      super(context, declaringModule);
      this.providesDirective = providesDirective;
   }

   @Override
   public D.Declared getService()
   {
      return Adapters.adapt(getApi(), providesDirective.getService());
   }

   @Override
   public ModuleElement.ProvidesDirective getMirror()
   {
      return providesDirective;
   }

   @Override
   public List<D.Declared> getImplementations()
   {
      return providesDirective.getImplementations()
                              .stream()
                              .map(typeElement -> Adapters.<D.Declared>adapt(getApi(), typeElement))
                              .toList();
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      return provides().service(getService())
                       .with(getImplementations())
                       .renderDeclaration(renderingContext);
   }

   @Override
   public boolean equals(Object other)
   {
      return other instanceof D.Provides provides &&
             Objects.equals(getImplementations(), provides.getImplementations()) &&
             Objects.equals(getService(), provides.getService());
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getImplementations(), getService());
   }

   @Override
   public String toString()
   {
      return "Provides{" +
             "service=" + getService() +
             ", implementations=" + getImplementations() +
             '}';
   }
}
