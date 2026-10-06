package com.derivandi.internal.dsl;


import com.derivandi.api.Modifier;
import com.derivandi.api.dsl.JavaDsl;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.annotation.*;
import com.derivandi.api.dsl.annotation_usage.AnnotationUsageRenderable;
import com.derivandi.api.dsl.annotation_value.AnnotationValueRenderable;
import com.derivandi.api.dsl.declared.DeclaredRenderable;
import com.derivandi.api.dsl.field.FieldRenderable;
import com.derivandi.api.dsl.import_.ImportRenderable;
import com.derivandi.api.dsl.method.MethodRenderable;
import com.derivandi.api.dsl.package_.PackageRenderable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.derivandi.api.dsl.RenderingContext.createRenderingContext;
import static com.derivandi.internal.dsl.DslSupport.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

public class AnnotationDsl
      implements
      AnnotationCopyrightHeaderStep,
      AnnotationImportStep,
      AnnotationBodyStep,
      AnnotationOuterStep
{
   /// optional only for inner
   private Renderable outerType;

   /// optional and only for files
   private String copyright;
   private PackageRenderable package_;
   private final List<Renderable> imports = new ArrayList<>();

   /// for all
   private Renderable javadoc;
   private final List<Renderable> annotations = new ArrayList<>();
   private final List<Renderable> modifiers = new ArrayList<>();
   private String name;
   private final List<Renderable> fields = new ArrayList<>();
   private final List<Renderable> methods = new ArrayList<>();
   private final List<Renderable> inner = new ArrayList<>();
   private Renderable body;

   public AnnotationDsl()
   {
   }

   private AnnotationDsl(AnnotationDsl other)
   {
      this.outerType = other.outerType;

      this.copyright = other.copyright;
      this.package_ = other.package_;
      this.imports.addAll(other.imports);

      this.javadoc = other.javadoc;
      this.annotations.addAll(other.annotations);
      this.modifiers.addAll(other.modifiers);
      this.name = other.name;
      this.fields.addAll(other.fields);
      this.methods.addAll(other.methods);
      this.inner.addAll(other.inner);
      this.body = other.body;
   }

   @Override
   public AnnotationJavaDocStep outer(String qualifiedOuterType)
   {
      return setTypeRenderer(new AnnotationDsl(this), qualifiedOuterType, (classDsl, s) -> classDsl.outerType = s);
   }

   @Override
   public AnnotationJavaDocStep outer(DeclaredRenderable outerType)
   {
      return setTypeRenderer(new AnnotationDsl(this),
                             outerType,
                             (renderingContext, declaredRenderable) -> declaredRenderable.renderQualifiedName(renderingContext),
                             (classDsl, s) -> classDsl.outerType = s);
   }

   @Override
   public AnnotationAnnotateStep javadoc(String javadoc)
   {
      return setTypeRenderer(new AnnotationDsl(this),
                             javadoc,
                             DslSupport::indent,
                             (annotationDsl, renderable) -> annotationDsl.javadoc = renderable);
   }

   @Override
   public AnnotationAnnotateStep annotate(String... annotation)
   {
      return addArray2(new AnnotationDsl(this),
                       annotation,
                       (annotationDsl, string) -> annotationDsl.annotations.add(renderingContext -> indent(renderingContext,
                                                                                                           '@' +
                                                                                                           renderingContext.renderName(string))));
   }


   @Override
   public AnnotationAnnotateStep annotate(List<? extends AnnotationUsageRenderable> annotation)
   {
      return addArrayRenderer(new AnnotationDsl(this),
                              annotation,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              annotationDsl -> annotationDsl.annotations::add);
   }

   @Override
   public AnnotationRenderable body(String body)
   {
      return setType(new AnnotationDsl(this),
                     body, (annotationDsl, s) -> annotationDsl.body = context -> indent(context, s));
   }

   @Override
   public AnnotationBodyStep field(String... fields)
   {
      return addArray2(new AnnotationDsl(this),
                       fields,
                       (annotationDsl, s) -> annotationDsl.fields.add(renderingContext -> indent(renderingContext, s)));
   }

   @Override
   public AnnotationBodyStep field(List<? extends FieldRenderable> fields)
   {
      return addArrayRenderer(new AnnotationDsl(this),
                              fields,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              annotationDsl -> annotationDsl.fields::add);
   }

   @Override
   public AnnotationBodyStep method(String... methods)
   {
      return addArray2(new AnnotationDsl(this),
                       methods,
                       (annotationDsl, string) -> annotationDsl.methods.add(renderingContext -> indent(renderingContext, string)));
   }

   @Override
   public AnnotationBodyStep method(List<? extends MethodRenderable> methods)
   {
      return addArrayRenderer(new AnnotationDsl(this),
                              methods,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext) + ';',
                              annotationDsl -> annotationDsl.methods::add);
   }

   @Override
   public AnnotationBodyStep method(MethodRenderable method, AnnotationValueRenderable defaultValue)
   {
      record Pair(MethodRenderable method,
                  AnnotationValueRenderable defaultValue) {}

      return setType(new AnnotationDsl(this),
                     new Pair(method, defaultValue),
                     (annotationDsl, pair) ->
                           annotationDsl.methods.add(renderingContext -> pair.method().renderDeclaration(renderingContext)
                                                                         + " default " +
                                                                         pair.defaultValue().render(renderingContext) + ';'));
   }

   @Override
   public AnnotationBodyStep inner(String... inner)
   {
      return addArray2(new AnnotationDsl(this),
                       inner,
                       (annotationDsl, string) -> annotationDsl.inner.add(renderingContext -> indent(renderingContext, string)));
   }

   @Override
   public AnnotationBodyStep inner(List<? extends DeclaredRenderable> inner)
   {
      return addArrayRenderer(new AnnotationDsl(this),
                              inner,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              annotationDsl -> annotationDsl.inner::add);
   }

   @Override
   public AnnotationModifierStep modifier(String... modifiers)
   {
      return addArrayRenderer(new AnnotationDsl(this), modifiers, annotationDsl -> annotationDsl.modifiers::add);
   }

   @Override
   public AnnotationModifierStep modifier(Set<Modifier> modifiers)
   {
      return addArray2(new AnnotationDsl(this),
                       modifiers,
                       (annotationDsl, modifier) -> annotationDsl.modifiers.add(modifier::render));
   }

   @Override
   public AnnotationModifierStep public_()
   {
      return setType(new AnnotationDsl(this),
                     Modifier.PUBLIC,
                     (annotationDsl, modifier) -> annotationDsl.modifiers.add(modifier::render));
   }

   @Override
   public AnnotationModifierStep protected_()
   {
      return setType(new AnnotationDsl(this),
                     Modifier.PROTECTED,
                     (annotationDsl, modifier) -> annotationDsl.modifiers.add(modifier::render));
   }

   @Override
   public AnnotationModifierStep private_()
   {
      return setType(new AnnotationDsl(this),
                     Modifier.PRIVATE,
                     (annotationDsl, modifier) -> annotationDsl.modifiers.add(modifier::render));
   }

   @Override
   public AnnotationModifierStep abstract_()
   {
      return setType(new AnnotationDsl(this),
                     Modifier.ABSTRACT,
                     (annotationDsl, modifier) -> annotationDsl.modifiers.add(modifier::render));
   }

   @Override
   public AnnotationModifierStep sealed()
   {
      return setType(new AnnotationDsl(this),
                     Modifier.SEALED,
                     (annotationDsl, modifier) -> annotationDsl.modifiers.add(modifier::render));
   }

   @Override
   public AnnotationModifierStep nonSealed()
   {
      return setType(new AnnotationDsl(this),
                     Modifier.NON_SEALED,
                     (annotationDsl, modifier) -> annotationDsl.modifiers.add(modifier::render));
   }

   @Override
   public AnnotationModifierStep strictfp_()
   {
      return setType(new AnnotationDsl(this),
                     Modifier.STRICTFP,
                     (annotationDsl, modifier) -> annotationDsl.modifiers.add(modifier::render));
   }

   @Override
   public AnnotationBodyStep name(String name)
   {
      return setType(new AnnotationDsl(this), name, (annotationDsl, s) -> annotationDsl.name = s);
   }

   @Override
   public AnnotationPackageStep copyright(String copyrightHeader)
   {
      return setType(new AnnotationDsl(this),
                     copyrightHeader,
                     (annotationDsl, string) -> annotationDsl.copyright = string);
   }

   @Override
   public AnnotationImportStep package_(String packageName)
   {
      return setType(new AnnotationDsl(this),
                     packageName,
                     (annotationDsl, string) -> annotationDsl.package_ = new PackageRenderable()
                     {
                        @Override
                        public String renderQualifiedName(RenderingContext renderingContext)
                        {
                           return string;
                        }

                        @Override
                        public String renderPackageInfo(RenderingContext renderingContext)
                        {
                           throw new IllegalStateException();
                        }
                     });
   }

   @Override
   public AnnotationImportStep package_(PackageRenderable aPackage)
   {
      return setType(new AnnotationDsl(this),
                     aPackage,
                     (annotationDsl, packageRenderable) -> annotationDsl.package_ = packageRenderable);
   }

   @Override
   public AnnotationImportStep noPackage()
   {
      return new AnnotationDsl(this);
   }

   @Override
   public AnnotationImportStep import_(String... name)
   {
      return addArray2(new AnnotationDsl(this),
                       name,
                       (annotationDsl, string) -> annotationDsl.imports.add(renderingContext -> JavaDsl.import_(string)
                                                                                                       .renderDeclaration(renderingContext)));
   }

   @Override
   public AnnotationImportStep import_(List<? extends ImportRenderable> declared)
   {
      return addArrayRenderer(new AnnotationDsl(this),
                              declared,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              annotationDsl -> annotationDsl.imports::add);
   }

   @Override
   public String renderDeclaration(RenderingContext context1)
   {
      return context1.withSurrounding(this,
            context ->
            {
               if (package_ != null)
               {
                  renderPackageName(package_, context).ifPresent(context::setCurrentPackageName);
               }

               StringBuilder sb = new StringBuilder();
               if (javadoc != null)
               {
                  sb.append(javadoc.render(context))
                    .append("\n");
               }

               renderElement(sb, annotations, context, "\n", new Padding(null, context.getLineIndentation(), null, "\n"));
               sb.append(context.getLineIndentation());
               renderElement(sb, modifiers, context, " ", new Padding(null, null, null, " "));

               sb.append("@interface ")
                 .append(name)
                 .append(' ');

               sb.append("{\n");
               RenderingContext indented = createRenderingContext(context);
               indented.incrementIndentationLevel();
               if (body != null)
               {
                  sb.append(body.render(indented))
                    .append('\n');
               }
               else
               {
                  renderElement(sb, fields, indented, "\n\n", new Padding(null, null, null, "\n"));
                  renderElement(sb, methods, indented, "\n\n", new Padding(null, null, null, "\n\n"));
                  renderElement(sb, inner, indented, "\n\n", new Padding(null, null, null, "\n\n"));
               }
               sb.append(context.getLineIndentation())
                 .append('}');

               //render Header

               if (!imports.isEmpty() || !context.getImports().isEmpty())
               {
                  sb.insert(0, "\n\n");
               }
               sb.insert(0, concat(imports.stream().map(renderable -> renderable.render(context)),
                                   context.getImports()
                                          .stream()
                                          .map(renderable -> renderable.renderDeclaration(context))).collect(joining("\n")));

               if (package_ != null)
               {
                  renderPackageDeclaration(package_, context)
                        .ifPresent(s -> sb.insert(0, "\n\n")
                                          .insert(0, s));
               }

               if (copyright != null)
               {
                  sb.insert(0, '\n')
                    .insert(0, copyright);
               }
               return sb.toString();
            });
   }

   @Override
   public String renderQualifiedName(RenderingContext context1)
   {
      return context1.withSurrounding(this, context ->
      {
         StringBuilder sb = new StringBuilder();
         if (package_ != null)
         {
            renderPackageName(package_, context).ifPresent(s -> sb.append(s).append('.'));
         }
         if (outerType != null)
         {
            sb.append(outerType.render(context))
              .append('.');
         }
         sb.append(name);
         return sb.toString();
      });
   }

   @Override
   public String renderType(RenderingContext renderingContext)
   {
      return renderQualifiedName(renderingContext);
   }

   @Override
   public String renderSimpleName(RenderingContext renderingContext)
   {
      return name;
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      String name = this.name;

      if (outerType != null)
      {
         name = outerType.render(renderingContext) + '.' + name;
      }
      String finalName = name;
      return renderPackageName(package_, renderingContext).map(s -> renderingContext.renderName(s, finalName))
                                                          .orElseGet(() -> renderingContext.renderName(finalName));
   }
}
