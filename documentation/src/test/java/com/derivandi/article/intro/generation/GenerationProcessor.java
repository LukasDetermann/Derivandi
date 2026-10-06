package com.derivandi.article.intro.generation;

//@formatter:off
//tag::content[]
import com.derivandi.api.D;
import com.derivandi.api.dsl.JavaDsl;
import com.derivandi.api.dsl.class_.ClassBodyStep;
import com.derivandi.api.dsl.constructor.ConstructorRenderable;
import com.derivandi.api.dsl.method.MethodRenderable;
import com.derivandi.api.processor.Processor;
import com.derivandi.api.processor.ProcessorBuilder;
import com.derivandi.api.processor.ProcessorConfiguration;
import com.derivandi.api.processor.SimpleContext;

import java.util.List;
import java.util.stream.Collectors;

import static com.derivandi.api.dsl.JavaDsl.*;
import static java.util.function.Predicate.not;

public class GenerationProcessor extends Processor {

   @Override
   public ProcessorConfiguration buildProcessor(ProcessorBuilder processorBuilder) {
      return processorBuilder.process(GenerationProcessor::process);
   }

   private static void process(SimpleContext context) {
      D.Annotation proxyAnnotation = context.getAnnotationOrThrow(Proxy.class.getName());
      D.Annotation delegateAnnotation = context.getAnnotationOrThrow(Delegate.class.getName());

      for (D.Class annotated : context.getClassesAnnotatedWith(proxyAnnotation)) {
         D.Declared delegate = getValid(context, annotated, delegateAnnotation);
         if (delegate == null) {
            continue;
         }
         context.writeAndCompileSourceFile(renderProxyImpl(context, annotated, delegate));
      }
   }

   private static D.Declared getValid(SimpleContext context,
                                      D.Class annotated,
                                      D.Annotation delegateAnnotation) {

      List<D.Field> delegates = annotated.getFields()
                                         .stream()
                                         .filter(field -> field.isAnnotatedWith(delegateAnnotation))
                                         .toList();

      if (!annotated.isAbstract()) {
         context.logAndRaiseErrorAt(annotated, "Not abstract");
         return null;
      }
      if (annotated.getDirectInterfaces().isEmpty()) {
         context.logAndRaiseErrorAt(annotated, "Does not implement any Interface");
         return null;
      }
      if (delegates.size() != 1) {
         context.logAndRaiseErrorAt(annotated, "A Proxy has to have one Delegate");
         return null;
      }
      D.Field delegate = delegates.getFirst();
      if (!delegate.isProtected() && !delegate.isPublic()) {
         context.logAndRaiseErrorAt(delegate, "Delegate needs to be accessible to child classes");
         return null;
      }
      if (!(delegate.getType() instanceof D.Declared declaredDelegate)) {
         context.logAndRaiseErrorAt(delegate, "Delegate must be a Declared Type");
         return null;
      }
      if (annotated.getConstructors()
                   .stream()
                   .filter(constructor -> constructor.getParameterTypes().size() == 1)
                   .noneMatch(constructor -> constructor.getParameterTypes()
                                                        .stream()
                                                        .findFirst()
                                                        .orElseThrow()
                                                        .equals(declaredDelegate))) {
         context.logAndRaiseErrorAt(annotated, "Needs to have a Constructor with with Type of the Delegate");
         return null;
      }
      return declaredDelegate;
   }

   private static ClassBodyStep renderProxyImpl(SimpleContext context,
                                                D.Class annotated,
                                                D.Declared delegate) {
      return class_()
            .package_(annotated.getPackage())
            .public_().name(annotated.getName() + "Impl")
            .genericDeclaration(annotated.getGenericDeclarations())
            .extends_(annotated)
            .constructor(renderConstructors(delegate))
            .method(renderProxyMethods(context, annotated, delegate));
   }

   private static ConstructorRenderable renderConstructors(D.Declared delegate)
   {
      return constructor().surroundingType()
                          .parameter(JavaDsl.parameter(delegate, "delegate"))
                          .body("super(delegate);");
   }

   private static List<MethodRenderable> renderProxyMethods(SimpleContext context,
                                                              D.Class annotated,
                                                              D.Declared delegate) {
      return delegate.getMethods()
                     .stream()
                     .filter(not(D.StaticModifiable::isStatic))
                     .filter(method -> annotated.getMethods()
                                                .stream()
                                                .noneMatch(method1 -> method1.overrides(method)))
                     .map(method -> renderProxyMethod(context, method))
                     .toList();
   }

   private static MethodRenderable renderProxyMethod(SimpleContext context,
                                                     D.Method method) {
      return method()
            .annotate(annotationUsage().type(annotation().package_(
                  "java.lang").name("Override")))
            .public_()
            .resultType(method.getResultType())
            .name(method.getName())
            .parameter(method.getParameters())
            .throws_(method.getThrows())
            .body(renderBody(context, method));
   }

   private static String renderBody(SimpleContext context,
                                    D.Method method) {

      String parameters = method.getParameters()
                             .stream()
                             .map(D.Nameable::getName)
                             .collect(Collectors.joining(", "));

      if (method.getResultType().equals(context.getConstants().getVoid())) {
         return "delegate." + method.getName() + "(" + parameters + ");";
      }
      return "return delegate." + method.getName() + "(" + parameters + ");";
   }
}
//end::content[]
//@formatter:on