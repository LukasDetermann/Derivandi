package com.derivandi.internal.structure;

import com.derivandi.api.D;
import com.derivandi.api.Origin;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.processor.SimpleContext;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.NoType;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.derivandi.api.adapter.Adapters.adapt;
import static com.derivandi.api.dsl.JavaDsl.packageInfo;
import static java.util.Optional.ofNullable;

public class PackageImpl implements D.Package
{
   private final PackageElement packageElement;
   private final SimpleContext context;
   private final NoType noType;

   public PackageImpl(SimpleContext context, PackageElement packageElement)
   {
      this.context = context;
      this.packageElement = packageElement;
      this.noType = (NoType) packageElement.asType();
   }

   @Override
   public String getQualifiedName()
   {
      return getElement().getQualifiedName().toString();
   }

   @Override
   public boolean isUnnamed()
   {
      return getElement().isUnnamed();
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

   public PackageElement getElement()
   {
      return packageElement;
   }

   @Override
   public List<D.Declared> getDeclared()
   {
      return getElement().getEnclosedElements()
                         .stream()
                         .map(TypeElement.class::cast)
                         .map(typeElement -> Adapters.<D.Declared>adapt(getApi(), typeElement))
                         .toList();
   }

   @Override
   public Optional<D.Declared> getDeclared(String qualifiedName)
   {
      return getDeclared().stream()
                          .filter(declared -> declared.getQualifiedName().equals(qualifiedName))
                          .findFirst();
   }

   @Override
   public D.Module getModule()
   {
      return Adapters.adapt(getApi(), adapt(getApi()).toElements().getModuleOf(getElement()));
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
      return Adapters.adapt(getApi(), getMirror(), adapt(getApi()).toElements().getAllAnnotationMirrors(getElement()));
   }

   @Override
   public List<D.AnnotationUsage> getDirectAnnotationUsages()
   {
      return adapt(getApi(), getElement(), getElement().getAnnotationMirrors());
   }

   @Override
   public String renderPackageInfo(RenderingContext renderingContext)
   {
      return packageInfo().annotate(getDirectAnnotationUsages())
                          .name(getQualifiedName())
                          .renderPackageInfo(renderingContext);
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
      return Objects.hash(getQualifiedName(), getModule());
   }

   @Override
   public boolean equals(Object other)
   {
      if (other == this)
      {
         return true;
      }
      if (!(other instanceof D.Package otherPackage))
      {
         return false;
      }
      return Objects.equals(getQualifiedName(), otherPackage.getQualifiedName()) &&
             Objects.equals(getModule(), otherPackage.getModule());
   }

   private SimpleContext getApi()
   {
      return context;
   }

   public NoType getMirror()
   {
      return noType;
   }
}
