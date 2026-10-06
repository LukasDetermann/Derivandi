package com.derivandi.internal.directive;

import com.derivandi.api.D;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.requires.RequiresNameStep;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.ModuleElement;
import java.util.Objects;

import static com.derivandi.api.dsl.JavaDsl.requires;

public class RequiresImpl extends DirectiveImpl implements D.Requires
{
   private final ModuleElement.RequiresDirective requiresDirective;

   public RequiresImpl(SimpleContext context, ModuleElement declaringModule, ModuleElement.RequiresDirective requiresDirective)
   {
      super(context, declaringModule);
      this.requiresDirective = requiresDirective;
   }

   @Override
   public boolean isStatic()
   {
      return requiresDirective.isStatic();
   }

   @Override
   public boolean isTransitive()
   {
      return requiresDirective.isTransitive();
   }

   @Override
   public D.Module getDependency()
   {
      return Adapters.adapt(getApi(), requiresDirective.getDependency());
   }

   @Override
   public ModuleElement.RequiresDirective getMirror()
   {
      return requiresDirective;
   }

   @Override
   public String renderDeclaration(RenderingContext renderingContext)
   {
      RequiresNameStep nameStep;
      if (isStatic())
      {
         nameStep = requires().static_();
      }
      else if (isTransitive())
      {
         nameStep = requires().transitive();
      }
      else
      {
         nameStep = requires();
      }
      return nameStep.dependency(getDependency()).renderDeclaration(renderingContext);
   }

   @Override
   public boolean equals(Object other)
   {
      return other instanceof D.Requires requires &&
             Objects.equals(isStatic(), requires.isStatic()) &&
             Objects.equals(isTransitive(), requires.isTransitive()) &&
             Objects.equals(getDependency(), requires.getDependency());

   }

   @Override
   public int hashCode()
   {
      return Objects.hash(isStatic(), isTransitive(), getDependency());
   }

   @Override
   public String toString()
   {
      return "Requires{" +
             "static=" + isStatic() +
             ", transitive=" + isTransitive() +
             ", dependency=" + getDependency() +
             '}';

   }
}
