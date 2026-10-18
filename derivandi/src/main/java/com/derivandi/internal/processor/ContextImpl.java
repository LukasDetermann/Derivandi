package com.derivandi.internal.processor;

import com.derivandi.api.D;
import com.derivandi.api.Modifier;
import com.derivandi.api.adapter.Adapters;
import com.derivandi.api.processor.Constants;
import com.derivandi.api.processor.Context;
import com.derivandi.api.processor.SimpleContext;
import com.derivandi.internal.ApConstantsImpl;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.derivandi.api.adapter.Adapters.adapt;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

public class ContextImpl
      implements Context<Object>
{
   private final ProcessingEnvironment processingEnv;
   private final RoundEnvironment roundEnv;
   @Nullable private final Function<Map<String, String>, Object> optionsMapper;
   private final int processingRound;
   private final Types types;
   private final Elements elements;
   private Object optionsCache;

   public ContextImpl(ProcessingEnvironment processingEnv,
                      RoundEnvironment roundEnv,
                      int processingRound,
                      @Nullable Function<Map<String, String>, Object> optionsMapper)
   {
      types = processingEnv.getTypeUtils();
      elements = processingEnv.getElementUtils();
      this.processingRound = processingRound;
      this.processingEnv = processingEnv;
      this.roundEnv = roundEnv;
      this.optionsMapper = optionsMapper;
   }

   private <TYPE> Set<TYPE> getAnnotated(String qualifiedAnnotation, java.lang.Class<TYPE> typeClass)
   {
      TypeElement annotation = getProcessingEnv().getElementUtils().getTypeElement(qualifiedAnnotation);
      if (annotation == null || !annotation.getKind().equals(ElementKind.ANNOTATION_TYPE))
      {
         throw new IllegalArgumentException("No annotation found with qualified name \"" + qualifiedAnnotation + "\"");
      }
      return getAnnotated(annotation, typeClass);
   }

   private <TYPE> Set<TYPE> getAnnotated(TypeElement annotation, java.lang.Class<TYPE> typeClass)
   {
      return getRoundEnv().getElementsAnnotatedWith(annotation).stream()
                          .map(element ->
                               {
                                  if (element.getKind().isExecutable())
                                  {
                                     return (D.Annotationable) Adapters.adapt(getApi(), ((ExecutableElement) element));
                                  }
                                  return ((D.Annotationable) Adapters.adapt(getApi(), element));
                               })
                          .filter(typeClass::isInstance)
                          .map(typeClass::cast)
                          .collect(toSet());
   }

   private <RESULT> Set<RESULT> getAnnotated(D.QualifiedNameable input, java.lang.Class<RESULT> resultClass)
   {
      if (input instanceof D.Annotation annotationLangModel)
      {
         return getAnnotated(adapt(annotationLangModel).toTypeElement(), resultClass);
      }
      return getAnnotated(input.getQualifiedName(), resultClass);
   }

   @Override
   public Set<D.Annotationable> getAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Annotationable.class);
   }

   @Override
   public Set<D.Annotationable> getAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Annotationable.class);
   }

   @Override
   public Set<D.Declared> getDeclaredAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Declared.class);
   }

   @Override
   public Set<D.Declared> getDeclaredAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Declared.class);
   }

   @Override
   public Set<D.Class> getClassesAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Class.class);
   }

   @Override
   public Set<D.Class> getClassesAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Class.class);
   }

   @Override
   public Set<D.Enum> getEnumsAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Enum.class);
   }

   @Override
   public Set<D.Enum> getEnumsAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Enum.class);
   }

   @Override
   public Set<D.Interface> getInterfacesAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Interface.class);
   }

   @Override
   public Set<D.Interface> getInterfacesAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Interface.class);
   }

   @Override
   public Set<D.Record> getRecordsAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Record.class);
   }

   @Override
   public Set<D.Record> getRecordsAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Record.class);
   }

   @Override
   public Set<D.Field> getFieldsAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Field.class);
   }

   @Override
   public Set<D.Field> getFieldsAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Field.class);
   }

   @Override
   public Set<D.Parameter> getParametersAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Parameter.class);
   }

   @Override
   public Set<D.Parameter> getParametersAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Parameter.class);
   }

   @Override
   public Set<D.Method> getMethodsAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Method.class);
   }

   @Override
   public Set<D.Method> getMethodsAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Method.class);
   }

   @Override
   public Set<D.Constructor> getConstructorsAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Constructor.class);
   }

   @Override
   public Set<D.Constructor> getConstructorsAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Constructor.class);
   }

   @Override
   public Set<D.Annotation> getAnnotationsAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Annotation.class);
   }

   @Override
   public Set<D.Annotation> getAnnotationsAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Annotation.class);
   }

   @Override
   public Set<D.Package> getPackagesAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Package.class);
   }

   @Override
   public Set<D.Package> getPackagesAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Package.class);
   }

   @Override
   public Set<D.Generic> getGenericDeclarationsAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Generic.class);
   }

   @Override
   public Set<D.Generic> getGenericsAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Generic.class);
   }

   @Override
   public Set<D.Module> getModulesAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.Module.class);
   }

   @Override
   public Set<D.Module> getModulesAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.Module.class);
   }

   @Override
   public Set<D.RecordComponent> getRecordComponentsAnnotatedWith(String qualifiedAnnotation)
   {
      return getAnnotated(qualifiedAnnotation, D.RecordComponent.class);
   }

   @Override
   public Set<D.RecordComponent> getRecordComponentsAnnotatedWith(D.Annotation annotation)
   {
      return getAnnotated(annotation, D.RecordComponent.class);
   }

   @Override
   public void writeAndCompileSourceFile(String qualifiedName, String content)
   {
      try (Writer writer = getProcessingEnv().getFiler().createSourceFile(qualifiedName).openWriter())
      {
         writer.write(content);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void writeClassFile(String qualifiedName, String content)
   {
      try (Writer writer = getProcessingEnv().getFiler().createClassFile(qualifiedName).openWriter())
      {
         writer.write(content);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void writeResource(StandardLocation location, String moduleAndPkg, String relativePath, String content)
   {
      try (Writer writer = getProcessingEnv().getFiler().createResource(location, moduleAndPkg, relativePath).openWriter())
      {
         writer.write(content);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public FileObject readResource(StandardLocation location, String moduleAndPkg, String relativePath) throws IOException
   {
      return getProcessingEnv().getFiler().getResource(location, moduleAndPkg, relativePath);
   }

   @Override
   public boolean isProcessingOver()
   {
      return getRoundEnv().processingOver();
   }

   @Override
   public boolean isFirstRound()
   {
      return processingRound == 0;
   }

   @Override
   public int getProcessingRound()
   {
      return processingRound;
   }

   @Override
   public void logAndRaiseError(String msg)
   {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg);
   }

   @Override
   public void logInfo(String msg)
   {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
   }

   @Override
   public void logWarning(String msg)
   {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, msg);
   }

   @Override
   public void logAndRaiseErrorAt(D.Annotationable annotationable, String msg)
   {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, adapt(annotationable).toElement());
   }

   @Override
   public void logInfoAt(D.Annotationable annotationable, String msg)
   {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg, adapt(annotationable).toElement());
   }

   @Override
   public void logWarningAt(D.Annotationable annotationable, String msg)
   {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, msg, adapt(annotationable).toElement());
   }

   @Override
   public List<D.Module> getModules()
   {
      return elements.getAllModuleElements()
                     .stream()
                     .map(moduleElement -> adapt(this, moduleElement))
                     .toList();
   }

   @Override
   public Optional<D.Module> getModule(String name)
   {
      return ofNullable(elements.getModuleElement(name))
            .map(moduleElement -> adapt(this, moduleElement));
   }

   @Override
   public D.Module getModuleOrThrow(String name)
   {
      return getModule(name).orElseThrow();
   }

   @Override
   public List<D.Package> getPackage(String qualifiedName)
   {
      return elements.getAllPackageElements(qualifiedName)
                     .stream()
                     .map(packageElement -> adapt(this, packageElement))
                     .toList();
   }

   @Override
   public List<D.Package> getPackages()
   {
      return elements.getAllModuleElements()
                     .stream()
                     .flatMap(moduleElement -> moduleElement.getEnclosedElements().stream())
                     .map(PackageElement.class::cast)
                     .map(packageElement -> adapt(this, packageElement))
                     .toList();
   }

   @Override
   public Optional<D.Package> getPackage(String qualifiedModuleName, String qualifiedPackageName)
   {
      return getPackage(getModuleOrThrow(qualifiedModuleName), qualifiedPackageName);
   }

   @Override
   public D.Package getPackageOrThrow(String qualifiedModuleName, String qualifiedPackageName)
   {
      return getPackage(qualifiedModuleName, qualifiedPackageName).orElseThrow();
   }

   @Override
   public Optional<D.Package> getPackage(D.Module module, String qualifiedPackageName)
   {
      return ofNullable(elements.getPackageElement(adapt(module).toModuleElement(), qualifiedPackageName))
            .map(packageElement -> adapt(this, packageElement));
   }

   @Override
   public D.Package getPackageOrThrow(D.Module module, String qualifiedPackageName)
   {
      return getPackage(module, qualifiedPackageName).orElseThrow();
   }

   @Override
   public Optional<D.Declared> getDeclared(String qualifiedName)
   {
      return ofNullable(elements.getTypeElement(qualifiedName))
            .map(typeElement -> adapt(this, typeElement));
   }

   @Override
   public List<D.Declared> getDeclared()
   {
      return getPackages()
            .stream()
            .flatMap(packageType -> packageType.getDeclared().stream())
            .toList();
   }

   @Override
   public Constants getConstants()
   {
      return new ApConstantsImpl(this);
   }

   public static Set<Modifier> getModifiers(Element element)
   {
      Set<Modifier> result = element.getModifiers().stream().map(ContextImpl::mapModifier).collect(Collectors.toSet());
      if ((element.getKind().isExecutable() || element.getKind().isDeclaredType() || element.getKind().isVariable()) &&
          !result.contains(Modifier.PUBLIC) &&
          !result.contains(Modifier.PROTECTED) &&
          !result.contains(Modifier.PRIVATE))
      {
         result.add(Modifier.PACKAGE_PRIVATE);
      }
      return Collections.unmodifiableSet(result);
   }

   private static Modifier mapModifier(javax.lang.model.element.Modifier modifier)
   {
      return switch (modifier)
      {
         case PUBLIC -> Modifier.PUBLIC;
         case PROTECTED -> Modifier.PROTECTED;
         case PRIVATE -> Modifier.PRIVATE;
         case ABSTRACT -> Modifier.ABSTRACT;
         case STATIC -> Modifier.STATIC;
         case SEALED -> Modifier.SEALED;
         case NON_SEALED -> Modifier.NON_SEALED;
         case FINAL -> Modifier.FINAL;
         case STRICTFP -> Modifier.STRICTFP;
         case DEFAULT -> Modifier.DEFAULT;
         case TRANSIENT -> Modifier.TRANSIENT;
         case VOLATILE -> Modifier.VOLATILE;
         case SYNCHRONIZED -> Modifier.SYNCHRONIZED;
         case NATIVE -> Modifier.NATIVE;
      };
   }

   @Override
   public Object getOptions()
   {
      if (optionsMapper == null)
      {
         throw new IllegalStateException();
      }
      if (optionsCache == null)
      {
         optionsCache = optionsMapper.apply(processingEnv.getOptions());
      }
      return optionsCache;
   }

   public Types getTypes()
   {
      return types;
   }

   public Elements getElements()
   {
      return elements;
   }

   public SimpleContext getApi()
   {
      return this;
   }

   public ProcessingEnvironment getProcessingEnv()
   {
      return processingEnv;
   }

   public RoundEnvironment getRoundEnv()
   {
      return roundEnv;
   }
}
