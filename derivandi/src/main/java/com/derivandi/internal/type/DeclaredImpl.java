package com.derivandi.internal.type;

import com.derivandi.api.D;
import com.derivandi.api.Modifier;
import com.derivandi.api.NestingKind;
import com.derivandi.api.Origin;
import com.derivandi.api.processor.SimpleContext;
import com.derivandi.internal.processor.ContextImpl;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.*;

import static com.derivandi.api.adapter.Adapters.adapt;
import static java.util.Optional.ofNullable;

public class DeclaredImpl extends TypeImpl<DeclaredType>
{
   private final TypeElement typeElement;

   DeclaredImpl(SimpleContext context, DeclaredType declaredTypeMirror)
   {
      super(context, declaredTypeMirror);
      this.typeElement = (TypeElement) declaredTypeMirror.asElement();
   }

   DeclaredImpl(SimpleContext context, TypeElement typeElement)
   {
      super(context, (DeclaredType) typeElement.asType());
      this.typeElement = typeElement;
   }

   public Set<Modifier> getModifiers()
   {
      return ContextImpl.getModifiers(getElement());
   }

   public boolean isSubtypeOf(D.ReferenceType referenceType)
   {
      TypeMirror typemirror = switch (referenceType)
      {
         case D.Array array -> adapt(array).toArrayType();
         case D.Generic generic -> adapt(generic).toTypeVariable();
         case D.Declared declared -> adapt(declared).toDeclaredType();
      };

      return adapt(getApi()).toTypes().isSubtype(getMirror(), typemirror);
   }

   public TypeElement getElement()
   {
      return typeElement;
   }

   public NestingKind getNesting()
   {
      return switch (getElement().getNestingKind())
      {
         case MEMBER -> NestingKind.INNER;
         case TOP_LEVEL -> NestingKind.OUTER;
         default -> throw new IllegalStateException();
      };
   }

   public List<D.Field> getFields()
   {
      return getElement().getEnclosedElements()
                         .stream()
                         .filter(element -> element.getKind().equals(ElementKind.FIELD))
                         .map(VariableElement.class::cast)
                         .map(variableElement -> adapt(getApi(), variableElement))
                         .map(D.Field.class::cast)
                         .toList();
   }

   public List<D.Method> getMethods()
   {
      return ElementFilter.methodsIn(getElement().getEnclosedElements())
                          .stream()
                          .map(element -> adapt(getApi(), element))
                          .map(D.Method.class::cast)
                          .toList();
   }

   public List<D.Constructor> getConstructors()
   {
      return ElementFilter.constructorsIn(getElement().getEnclosedElements())
                          .stream()
                          .map(element -> adapt(getApi(), element))
                          .map(D.Constructor.class::cast)
                          .toList();
   }

   public List<D.Declared> getDirectSuperTypes()
   {
      return adapt(getApi()).toTypes()
                        .directSupertypes(getMirror())
                        .stream()
                        .map(typeMirror1 -> adapt(getApi(), ((DeclaredType) typeMirror1)))
                        .toList();
   }

   public Set<D.Declared> getSuperTypes()
   {
      return findAllSupertypes(new HashSet<>(), ((D.Declared) this));
   }

   private Set<D.Declared> findAllSupertypes(Set<D.Declared> found, D.Declared declared)
   {
      List<D.Declared> directSupertypes = declared.getDirectSuperTypes();
      found.addAll(directSupertypes);
      for (D.Declared directSupertype : directSupertypes)
      {
         findAllSupertypes(found, directSupertype);
      }
      return found;
   }

   public String getQualifiedName()
   {
      return getElement().getQualifiedName().toString();
   }

   public String getBinaryName()
   {
      return adapt(getApi()).toElements().getBinaryName(getElement()).toString();
   }

   public D.Array asArray()
   {
      return adapt(getApi(), adapt(getApi()).toTypes().getArrayType(getMirror()));
   }

   public Optional<D.Declared> getSurrounding()
   {
      Element enclosingElement = typeElement.getEnclosingElement();
      if (!enclosingElement.getKind().isDeclaredType())
      {
         return Optional.empty();
      }
      return Optional.of((D.Declared) adapt(getApi(), enclosingElement));
   }

   public Optional<D.Declared> getOutermostSurrounding()
   {
      TypeElement outermost = adapt(getApi()).toElements().getOutermostTypeElement(getElement());
      if (outermost == null)
      {
         return Optional.empty();
      }
      return Optional.of(adapt(getApi(), outermost));
   }

   public Origin getOrigin()
   {
      return adapt(adapt(getApi()).toElements().getOrigin(getElement()));
   }

   public D.Wildcard asExtendsWildcard()
   {
      return adapt(getApi(), adapt(getApi()).toTypes().getWildcardType(getMirror(), null));
   }

   public D.Wildcard asSuperWildcard()
   {
      return adapt(getApi(), adapt(getApi()).toTypes().getWildcardType(null, getMirror()));
   }

   public List<D.Interface> getDirectInterfaces()
   {
      return getElement().getInterfaces()
                         .stream()
                         .map(typeMirror -> adapt(getApi(), ((DeclaredType) typeMirror)))
                         .map(D.Interface.class::cast)
                         .toList();
   }

   public List<D.Interface> getInterfaces()
   {
      return getSuperTypes().stream()
                            .filter(declared -> declared instanceof D.Interface)
                            .map(D.Interface.class::cast)
                            .toList();
   }

   public D.Package getPackage()
   {
      return adapt(getApi(), adapt(getApi()).toElements().getPackageOf(getElement()));
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

   @Override
   public boolean isSameType(D.Type type)
   {
      return type instanceof D.Declared declared &&
             adapt(getApi()).toTypes().isSameType(getMirror(), adapt(declared).toDeclaredType());
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getQualifiedName());
   }

   protected boolean equals(Class<? extends D.Declared> type, Object other)
   {
      if (other == this)
      {
         return true;
      }
      if (!type.isInstance(other))
      {
         return false;
      }
      D.Declared declared = (D.Declared) other;

      return Objects.equals(getQualifiedName(), declared.getQualifiedName());
   }

   protected String toString(String type)
   {
      return type + "{" +
             "qualifiedName='" + getQualifiedName()  + '\'' +
             '}';
   }
}
