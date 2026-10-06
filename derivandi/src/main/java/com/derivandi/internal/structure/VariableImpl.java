package com.derivandi.internal.structure;

import com.derivandi.api.D;
import com.derivandi.api.Modifier;
import com.derivandi.api.Origin;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.processor.SimpleContext;
import com.derivandi.internal.processor.ContextImpl;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.derivandi.api.adapter.Adapters.adapt;
import static java.util.Optional.ofNullable;

public abstract class VariableImpl
{
   private final VariableElement variableElement;
   private final SimpleContext context;

   protected VariableImpl(SimpleContext context, VariableElement variableElement)
   {
      this.context = context;
      this.variableElement = variableElement;
   }

   public Set<Modifier> getModifiers()
   {
      return ContextImpl.getModifiers(getElement());
   }

   public boolean isSubtypeOf(D.Type type)
   {
      return adapt(getApi()).toTypes().isSubtype(adapt((D.Declared) type).toDeclaredType(), getMirror());
   }

   public boolean isAssignableFrom(D.Type type)
   {
      return adapt(getApi()).toTypes().isAssignable(adapt((D.Declared) type).toDeclaredType(), getMirror());
   }

   public boolean isDeprecated()
   {
      return adapt(getApi()).toElements().isDeprecated(getElement());
   }

   public Origin getOrigin()
   {
      return adapt(adapt(getApi()).toElements().getOrigin(getElement()));
   }

   public D.VariableType getType()
   {
      return (D.VariableType) adapt(getApi(), getElement().asType());
   }

   public VariableElement getElement()
   {
      return variableElement;
   }

   public D.Module getModule()
   {
      return Adapters.adapt(getApi(), adapt(getApi()).toElements().getModuleOf(getElement()));
   }

   public String getName()
   {
      return getElement().getSimpleName().toString();
   }

   public Optional<String> getJavaDoc()
   {
      return ofNullable(adapt(getApi()).toElements().getDocComment(getElement()));
   }

   public List<D.AnnotationUsage> getAnnotationUsages()
   {
      return Adapters.adapt(getApi(), getElement(), adapt(getApi()).toElements().getAllAnnotationMirrors(getElement()));
   }

   public List<D.AnnotationUsage> getDirectAnnotationUsages()
   {
      return adapt(getApi(), getElement(), getElement().getAnnotationMirrors());
   }

   public SimpleContext getApi()
   {
      return context;
   }

   public TypeMirror getMirror()
   {
      return variableElement.asType();
   }

   @Override
   public String toString()
   {
      return getElement().toString();
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getName(),
                          getType(),
                          getModifiers());
   }

   @Override
   public boolean equals(Object other)
   {
      if (other == this)
      {
         return true;
      }
      if (!(other instanceof D.Variable otherVariable))
      {
         return false;
      }
      return Objects.equals(getName(), otherVariable.getName()) &&
             Objects.equals(getType(), otherVariable.getType()) &&
             Objects.equals(getModifiers(), otherVariable.getModifiers());
   }
}
