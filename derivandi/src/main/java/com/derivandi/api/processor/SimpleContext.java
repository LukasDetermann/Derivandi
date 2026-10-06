package com.derivandi.api.processor;

import com.derivandi.api.D;
import com.derivandi.api.dsl.declared.DeclaredRenderable;
import com.derivandi.api.dsl.module.ModuleRenderable;
import com.derivandi.api.dsl.package_.PackageRenderable;

import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.derivandi.api.dsl.RenderingContext.createRenderingContext;

/**
 * This is the core class for a lightweight wrapper around the java annotationProcessor api. The {@link SimpleContext} is transient. Meaning you can
 * transition between using the derivandi and the java annotation processor api from line to line if you so wish.
 * <br><br>
 *
 * <h2>Usage:</h2>
 *
 * <ul>
 *    <li>get anything that is annotated {@link #getAnnotatedWith(String)}</li>
 *    <li>get already compiled sources {@link #getDeclaredOrThrow(String)} for example</li>
 *    <li>get constants {@link #getConstants()}</li>
 *    <li>log using {@link #logAndRaiseError(String)} or log at {@link #logAndRaiseErrorAt(D.Annotationable, String)}</li>
 * </ul>
 *
 * @see Processor
 * @see D.Type
 */
public interface SimpleContext
{
   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Annotationable> getAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Annotationable> getAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Declared> getDeclaredAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Declared> getDeclaredAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Class> getClassesAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Class> getClassesAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Enum> getEnumsAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Enum> getEnumsAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Interface> getInterfacesAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Interface> getInterfacesAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Record> getRecordsAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Record> getRecordsAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Field> getFieldsAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Field> getFieldsAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Parameter> getParametersAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Parameter> getParametersAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Method> getMethodsAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Method> getMethodsAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Constructor> getConstructorsAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Constructor> getConstructorsAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Annotation> getAnnotationsAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Annotation> getAnnotationsAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Package> getPackagesAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Package> gePackagesAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Generic> getGenericDeclarationsAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Generic> geGenericsAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Module> getModulesAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.Module> geModulesAnnotatedWith(D.Annotation annotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.RecordComponent> getRecordComponentsAnnotatedWith(String qualifiedAnnotation);

   /**
    * Looks up annotated elements in currently compiled code. <br>
    * Annotation processing happens in rounds.
    * If a processor generates sources the next round will contain only these.
    *
    * @see #writeAndCompileSourceFile(String, String)
    * @see #writeClassFile(String, String)
    */
   Set<D.RecordComponent> geRecordComponentsAnnotatedWith(D.Annotation annotation);

   /**
    * the created file will be registered for the next annotation processor round. writes .java files
    */
   void writeAndCompileSourceFile(String qualifiedName, String content);

   /**
    * the created file will be registered for the next annotation processor round. writes .java files
    */
   default void writeAndCompileSourceFile(DeclaredRenderable declaredRenderable)
   {
      writeAndCompileSourceFile(declaredRenderable.renderQualifiedName(createRenderingContext()),
                                declaredRenderable.renderDeclaration(createRenderingContext()));
   }

   /**
    * the created file will be registered for the next annotation processor round. writes .java files
    */
   default void writeAndCompileSourceFile(ModuleRenderable moduleRenderable)
   {
      writeAndCompileSourceFile(moduleRenderable.renderQualifiedName(createRenderingContext()),
                                moduleRenderable.renderModuleInfo(createRenderingContext()));
   }

   /**
    * the created file will be registered for the next annotation processor round. writes .java files
    */
   default void writeAndCompileSourceFile(PackageRenderable packageRenderable)
   {
      writeAndCompileSourceFile(packageRenderable.renderQualifiedName(createRenderingContext()),
                                packageRenderable.renderPackageInfo(createRenderingContext()));
   }

   /**
    * the created file will be registered for the next annotation processor round. writes .class files
    */
   void writeClassFile(String qualifiedName, String content);

   /**
    * the created file will NOT be registered for the next annotation processor round. writes anything
    */
   void writeResource(StandardLocation location, String moduleAndPkg, String relativePath, String content);

   FileObject readResource(StandardLocation location, String moduleAndPkg, String relativePath) throws IOException;

   /**
    * Last round of annotation processing
    */
   boolean isProcessingOver();

   /**
    * First round of annotation processing
    */
   boolean isFirstRound();

   /**
    * starts at 0
    */
   int getProcessingRound();

   void logAndRaiseError(String msg);

   void logInfo(String msg);

   void logWarning(String msg);

   void logAndRaiseErrorAt(D.Annotationable annotationable, String msg);

   void logInfoAt(D.Annotationable annotationable, String msg);

   void logWarningAt(D.Annotationable annotationable, String msg);

   List<D.Declared> getDeclared();

   Optional<D.Declared> getDeclared(String qualifiedName);

   default D.Declared getDeclaredOrThrow(String qualifiedName)
   {
      return getDeclared(qualifiedName).orElseThrow();
   }

   default List<D.Annotation> getAnnotations()
   {
      return getDeclared().stream()
                          .filter(D.Annotation.class::isInstance)
                          .map(D.Annotation.class::cast)
                          .toList();
   }

   default Optional<D.Annotation> getAnnotation(String qualifiedName)
   {
      return getDeclared(qualifiedName).map(D.Annotation.class::cast);
   }

   default D.Annotation getAnnotationOrThrow(String qualifiedName)
   {
      return ((D.Annotation) getDeclaredOrThrow(qualifiedName));
   }

   default List<D.Class> getClasses()
   {
      return getDeclared().stream()
                          .filter(D.Class.class::isInstance)
                          .map(D.Class.class::cast)
                          .toList();
   }

   default Optional<D.Class> getClass(String qualifiedName)
   {
      return getDeclared(qualifiedName).map(D.Class.class::cast);
   }

   default D.Class getClassOrThrow(String qualifiedName)
   {
      return ((D.Class) getDeclaredOrThrow(qualifiedName));
   }

   default List<D.Enum> getEnums()
   {
      return getDeclared().stream()
                          .filter(D.Enum.class::isInstance)
                          .map(D.Enum.class::cast)
                          .toList();
   }

   default Optional<D.Enum> getEnum(String qualifiedName)
   {
      return getDeclared(qualifiedName).map(D.Enum.class::cast);
   }

   default D.Enum getEnumOrThrow(String qualifiedName)
   {
      return ((D.Enum) getDeclaredOrThrow(qualifiedName));
   }

   default List<D.Interface> getInterfaces()
   {
      return getDeclared().stream()
                          .filter(D.Interface.class::isInstance)
                          .map(D.Interface.class::cast)
                          .toList();
   }

   default Optional<D.Interface> getInterface(String qualifiedName)
   {
      return getDeclared(qualifiedName).map(D.Interface.class::cast);
   }

   default D.Interface getInterfaceOrThrow(String qualifiedName)
   {
      return ((D.Interface) getDeclaredOrThrow(qualifiedName));
   }

   default List<D.Record> getRecords()
   {
      return getDeclared().stream()
                          .filter(D.Record.class::isInstance)
                          .map(D.Record.class::cast)
                          .toList();
   }

   default Optional<D.Record> getRecord(String qualifiedName)
   {
      return getDeclared(qualifiedName).map(D.Record.class::cast);
   }

   default D.Record getRecordOrThrow(String qualifiedName)
   {
      return ((D.Record) getDeclaredOrThrow(qualifiedName));
   }

   List<D.Module> getModules();

   Optional<D.Module> getModule(String name);

   D.Module getModuleOrThrow(String name);

   /**
    * a package is unique per module. With multiple modules there can be multiple packages with the same name
    */
   List<D.Package> getPackage(String qualifiedName);

   List<D.Package> getPackages();

   Optional<D.Package> getPackage(String qualifiedModuleName, String qualifiedPackageName);

   D.Package getPackageOrThrow(String qualifiedModuleName, String qualifiedPackageName);

   Optional<D.Package> getPackage(D.Module module, String qualifiedPackageName);

   D.Package getPackageOrThrow(D.Module module, String qualifiedPackageName);

   Constants getConstants();
}
