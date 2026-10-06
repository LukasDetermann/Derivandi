package com.derivandi.internal.structure;

import com.derivandi.api.D;
import com.derivandi.api.Modifier;
import com.derivandi.api.Origin;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.processor.SimpleContext;
import com.derivandi.internal.processor.ContextImpl;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.derivandi.api.adapter.Adapters.adapt;
import static java.util.Optional.ofNullable;


public abstract class ExecutableImpl
{
   private final SimpleContext context;
   private final ExecutableElement executableElement;


   protected ExecutableImpl(SimpleContext context, ExecutableElement executableElement)
   {
      this.context = context;
      this.executableElement = executableElement;
   }

   public Set<Modifier> getModifiers()
   {
      return ContextImpl.getModifiers(getElement());
   }

   public D.Result getResult()
   {
      return new ResultImpl(getApi(), getMirror().getReturnType());
   }

   public D.Type getResultType()
   {
      return adapt(getApi(), getMirror().getReturnType());
   }

   public List<D.Type> getParameterTypes()
   {
      return getMirror().getParameterTypes()
                        .stream()
                        .map(typeMirror -> Adapters.<D.Type>adapt(getApi(), typeMirror))
                        .toList();
   }

   public Optional<D.Declared> getReceiverType()
   {
      TypeMirror receiverType = getMirror().getReceiverType();
      if (receiverType == null || receiverType.getKind().equals(TypeKind.NONE))
      {
         return Optional.empty();
      }
      return Optional.of(adapt(getApi(), ((DeclaredType) receiverType)));
   }

   public Optional<D.Receiver> getReceiver()
   {
      TypeMirror receiverType = getMirror().getReceiverType();
      if (receiverType == null || receiverType.getKind().equals(TypeKind.NONE))
      {
         return Optional.empty();
      }
      return Optional.of(new ReceiverImpl(getApi(), getMirror().getReceiverType()));
   }

   public List<D.Class> getThrows()
   {
      return getMirror().getThrownTypes()
                        .stream()
                        .map(typeMirror -> Adapters.<D.Class>adapt(getApi(), typeMirror))
                        .map(D.Class.class::cast)
                        .toList();
   }

   public boolean isBridge()
   {
      return adapt(getApi()).toElements().isBridge(getElement());
   }

   public boolean isDeprecated()
   {
      return adapt(getApi()).toElements().isDeprecated(getElement());
   }

   public Origin getOrigin()
   {
      return adapt(adapt(getApi()).toElements().getOrigin(getElement()));
   }

   public boolean isVarArgs()
   {
      return getElement().isVarArgs();
   }

   public D.Declared getSurrounding()
   {
      return adapt(getApi(), ((TypeElement) getElement().getEnclosingElement()));
   }

   public ExecutableElement getElement()
   {
      return executableElement;
   }

   public boolean overrides(D.Method method)
   {
      return adapt(getApi()).toElements().overrides(getElement(),
                                                    adapt(method).toExecutableElement(),
                                                    adapt(getSurrounding()).toTypeElement());
   }

   public boolean overwrittenBy(D.Method method)
   {
      return adapt(getApi()).toElements().overrides(adapt(method).toExecutableElement(),
                                                    getElement(),
                                                    adapt(method.getSurrounding()).toTypeElement());
   }

   public boolean isSubsignatureOf(D.Executable executable)
   {
      return adapt(getApi()).toTypes().isSubsignature(getMirror(), adapt(executable).toExecutableType());
   }

   public boolean isSubsignatureFor(D.Executable executable)
   {
      return adapt(getApi()).toTypes().isSubsignature(adapt(executable).toExecutableType(), getMirror());
   }

   public boolean sameParameterTypes(D.Method method)
   {
      return adapt(getApi()).toTypes().isSubsignature(getMirror(), adapt(method).toExecutableType());
   }

   public List<D.Parameter> getParameters()
   {
      return getElement().getParameters()
                         .stream()
                         .map(VariableElement.class::cast)
                         .map(variableElement -> adapt(getApi(), variableElement))
                         .map(D.Parameter.class::cast)
                         .toList();
   }

   public List<D.Generic> getGenericDeclarations()
   {
      return getElement().getTypeParameters()
                         .stream()
                         .map(element -> adapt(getApi(), element))
                         .toList();
   }

   public D.Module getModule()
   {
      return adapt(getApi(), adapt(getApi()).toElements().getModuleOf(getElement()));
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
      return adapt(getApi(), getElement(), adapt(getApi()).toElements().getAllAnnotationMirrors(getElement()));
   }

   public List<D.AnnotationUsage> getDirectAnnotationUsages()
   {
      return adapt(getApi(), getElement(), getElement().getAnnotationMirrors());
   }

   public SimpleContext getApi()
   {
      return context;
   }

   public ExecutableType getMirror()
   {
      return (ExecutableType) executableElement.asType();
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
                          getParameterTypes(),
                          getParameters(),
                          getModifiers());
   }

   @Override
   public boolean equals(Object other)
   {
      if (other == this)
      {
         return true;
      }
      if (!(other instanceof D.Executable otherExecutable))
      {
         return false;
      }
      return Objects.equals(getName(), otherExecutable.getName()) &&
             Objects.equals(getParameters(), otherExecutable.getParameters()) &&
             Objects.equals(getModifiers(), otherExecutable.getModifiers());
   }
}
