package com.derivandi.internal.structure;

import com.derivandi.api.D;
import com.derivandi.api.Origin;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.NoType;
import java.util.*;
import java.util.function.Supplier;

import static com.derivandi.api.adapter.Adapters.adapt;
import static com.derivandi.api.dsl.JavaDsl.moduleInfo;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collector.of;

public class ModuleImpl implements D.Module
{
   private final ModuleElement moduleElement;
   private final NoType noType;
   private final SimpleContext context;


   public ModuleImpl(SimpleContext context, ModuleElement moduleElement)
   {
      this.context = context;
      noType = (NoType) moduleElement.asType();
      this.moduleElement = moduleElement;
   }

   public ModuleElement getElement()
   {
      return moduleElement;
   }

   @Override
   public List<D.Package> getPackages()
   {
      return getElement().getEnclosedElements()
                         .stream()
                         .map(PackageElement.class::cast)
                         .map(element -> adapt(getApi(), element))
                         .toList();
   }

   @Override
   public String getQualifiedName()
   {
      return getElement().getQualifiedName().toString();
   }

   @Override
   public List<D.Declared> getDeclared()
   {
      return getPackages().stream().flatMap(aPackage -> aPackage.getDeclared().stream()).toList();
   }

   @Override
   public Optional<D.Declared> getDeclared(String qualifiedName)
   {
      return ofNullable(adapt(getApi()).toElements().getTypeElement(getElement(), qualifiedName))
            .map(typeElement -> Adapters.adapt(getApi(), typeElement));
   }

   @Override
   public boolean isOpen()
   {
      return getElement().isOpen();
   }

   @Override
   public boolean isUnnamed()
   {
      return getElement().isUnnamed();
   }

   @Override
   public boolean isAutomatic()
   {
      return adapt(getApi()).toElements().isAutomaticModule(getElement());
   }

   @Override
   public boolean isDeprecated()
   {
      return adapt(getApi()).toElements().isDeprecated(getElement());
   }

   @Override
   public Origin getOrigin()
   {
      return adapt(adapt(getApi()).toElements().getOrigin(getElement()));
   }

   @Override
   public List<D.Directive> getDirectives()
   {
      return getElement().getDirectives()
                         .stream()
                         .map(directive -> adapt(context, getElement(), directive))
                         .collect(of((Supplier<List<D.Directive>>) ArrayList::new,
                                     (directives, directive) ->
                                     {
                                        if (!(directive instanceof D.Provides provides))
                                        {
                                           directives.add(directive);
                                           return;
                                        }

                                        Optional<D.Provides> existing =
                                              directives.stream()
                                                        .filter(D.Provides.class::isInstance)
                                                        .map(D.Provides.class::cast)
                                                        .filter(collected -> collected.getService().isSameType(provides.getService()))
                                                        .findAny();

                                        if (existing.isEmpty())
                                        {
                                           directives.add(directive);
                                           return;
                                        }
                                        if (existing.get().getImplementations().size() > provides.getImplementations().size())
                                        {
                                           return;
                                        }
                                        int index = directives.indexOf(existing.get());
                                        directives.remove(index);
                                        directives.add(index, directive);
                                     },
                                     (directives, directives2) ->
                                     {
                                        directives.addAll(directives2);
                                        return directives;
                                     },
                                     Collections::unmodifiableList,
                                     IDENTITY_FINISH));
   }

   @Override
   public String getName()
   {
      return getElement().getSimpleName().toString();
   }

   @Override
   public Optional<String> getJavaDoc()
   {
      return ofNullable(adapt(getApi()).toElements().getDocComment(getElement()));
   }

   @Override
   public List<D.AnnotationUsage> getAnnotationUsages()
   {
      return Adapters.adapt(getApi(), getElement(), adapt(getApi()).toElements().getAllAnnotationMirrors(getElement()));
   }

   @Override
   public List<D.AnnotationUsage> getDirectAnnotationUsages()
   {
      return adapt(getApi(), getElement(), getElement().getAnnotationMirrors());
   }

   public NoType getMirror()
   {
      return noType;
   }

   public SimpleContext getApi()
   {
      return context;
   }

   @Override
   public String renderModuleInfo(RenderingContext renderingContext)
   {
      List<? extends D.Directive> directives = getDirectives();

      return moduleInfo().annotate(getAnnotationUsages())
                         .name(getName())
                         .requires(directives.stream().filter(D.Requires.class::isInstance).map(D.Requires.class::cast).toList())
                         .exports(directives.stream().filter(D.Exports.class::isInstance).map(D.Exports.class::cast).toList())
                         .opens(directives.stream().filter(D.Opens.class::isInstance).map(D.Opens.class::cast).toList())
                         .uses(directives.stream().filter(D.Uses.class::isInstance).map(D.Uses.class::cast).toList())
                         .provides(directives.stream().filter(D.Provides.class::isInstance).map(D.Provides.class::cast).toList())
                         .renderModuleInfo(renderingContext);
   }

   @Override
   public String renderQualifiedName(RenderingContext renderingContext)
   {
      return getQualifiedName();
   }

   @Override
   public String toString()
   {
      return getElement().toString();
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getQualifiedName());
   }

   @Override
   public boolean equals(Object other)
   {
      if (other == this)
      {
         return true;
      }
      if (!(other instanceof D.Module otherModule))
      {
         return false;
      }
      return Objects.equals(getQualifiedName(), otherModule.getQualifiedName());
   }
}
