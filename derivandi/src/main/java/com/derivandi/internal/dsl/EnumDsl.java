package com.derivandi.internal.dsl;


import com.derivandi.api.Modifier;
import com.derivandi.api.dsl.JavaDsl;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.annotation_usage.AnnotationUsageRenderable;
import com.derivandi.api.dsl.constructor.ConstructorRenderable;
import com.derivandi.api.dsl.declared.DeclaredRenderable;
import com.derivandi.api.dsl.enum_.*;
import com.derivandi.api.dsl.enum_constant.EnumConstantRenderable;
import com.derivandi.api.dsl.field.FieldRenderable;
import com.derivandi.api.dsl.import_.ImportRenderable;
import com.derivandi.api.dsl.interface_.InterfaceRenderable;
import com.derivandi.api.dsl.method.MethodRenderable;
import com.derivandi.api.dsl.package_.PackageRenderable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.derivandi.api.dsl.RenderingContext.createRenderingContext;
import static com.derivandi.internal.dsl.DslSupport.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

public class EnumDsl
      implements EnumCopyrightHeaderStep,
                 EnumImportStep,
                 EnumImplementsStep,
                 EnumOuterStep
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
   private final List<Renderable> implements_ = new ArrayList<>();
   private final List<Renderable> enumConstants = new ArrayList<>();
   private final List<Renderable> fields = new ArrayList<>();
   private final List<Renderable> methods = new ArrayList<>();
   private final List<Renderable> inner = new ArrayList<>();
   private final List<Renderable> instanceInitializers = new ArrayList<>();
   private final List<Renderable> staticInitializers = new ArrayList<>();
   private final List<Renderable> constructors = new ArrayList<>();
   private Renderable body;

   public EnumDsl() {}

   private EnumDsl(EnumDsl other)
   {
      this.outerType = other.outerType;

      this.copyright = other.copyright;
      this.package_ = other.package_;
      this.imports.addAll(other.imports);

      this.javadoc = other.javadoc;
      this.annotations.addAll(other.annotations);
      this.modifiers.addAll(other.modifiers);
      this.name = other.name;
      this.implements_.addAll(other.implements_);
      this.enumConstants.addAll(other.enumConstants);
      this.fields.addAll(other.fields);
      this.methods.addAll(other.methods);
      this.inner.addAll(other.inner);
      this.instanceInitializers.addAll(other.instanceInitializers);
      this.staticInitializers.addAll(other.staticInitializers);
      this.constructors.addAll(other.constructors);
      this.body = other.body;
   }

   @Override
   public EnumJavaDocStep outer(String qualifiedOuterType)
   {
      return setTypeRenderer(new EnumDsl(this), qualifiedOuterType, (enumDsl, s) -> enumDsl.outerType = s);
   }

   @Override
   public EnumJavaDocStep outer(DeclaredRenderable outerType)
   {
      return setTypeRenderer(new EnumDsl(this),
                             outerType,
                             (renderingContext, declaredRenderable) -> declaredRenderable.renderQualifiedName(renderingContext),
                             (enumDsl, s) -> enumDsl.outerType = s);

   }

   @Override
   public EnumAnnotateStep javadoc(String javadoc)
   {
      return setTypeRenderer(new EnumDsl(this),
                             javadoc,
                             DslSupport::indent,
                             (enumDsl, function) -> enumDsl.javadoc = function);
   }

   @Override
   public EnumAnnotateStep annotate(String... annotation)
   {
      return addArray2(new EnumDsl(this),
                       annotation,
                       (enumDsl, string) -> enumDsl.annotations.add(context -> indent(context, '@' + context.renderName(string))));
   }

   @Override
   public EnumAnnotateStep annotate(List<? extends AnnotationUsageRenderable> annotation)
   {
      return addArrayRenderer(new EnumDsl(this),
                              annotation,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              enumDsl -> enumDsl.annotations::add);
   }

   @Override
   public EnumModifierStep modifier(String... modifiers)
   {
      return addArrayRenderer(new EnumDsl(this), modifiers, enumDsl -> enumDsl.modifiers::add);
   }

   @Override
   public EnumModifierStep modifier(Set<Modifier> modifiers)
   {
      return addArray2(new EnumDsl(this),
                       modifiers,
                       (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public EnumModifierStep public_()
   {
      return setType(new EnumDsl(this),
                     Modifier.PUBLIC,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public EnumModifierStep protected_()
   {
      return setType(new EnumDsl(this),
                     Modifier.PROTECTED,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public EnumModifierStep private_()
   {
      return setType(new EnumDsl(this),
                     Modifier.PRIVATE,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public EnumModifierStep static_()
   {
      return setType(new EnumDsl(this),
                     Modifier.STATIC,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public EnumModifierStep strictfp_()
   {
      return setType(new EnumDsl(this),
                     Modifier.STRICTFP,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public EnumImplementsStep name(String name)
   {
      return setType(new EnumDsl(this), name, (enumDsl, s) -> enumDsl.name = s);
   }

   @Override
   public EnumImplementsStep implements_(String... interfaces)
   {
      return addArray2(new EnumDsl(this),
                       interfaces,
                       (enumDsl, string) -> enumDsl.implements_.add(context -> context.renderName(string)));
   }

   @Override
   public EnumImplementsStep implements_(List<? extends InterfaceRenderable> interfaces)
   {
      return addArrayRenderer(new EnumDsl(this),
                              interfaces,
                              (renderingContext, renderable) -> renderable.renderType(renderingContext),
                              enumDsl -> enumDsl.implements_::add);
   }

   @Override
   public EnumRenderable body(String body)
   {
      return setType(new EnumDsl(this),
                     body, (enumDsl, s) -> enumDsl.body = context -> indent(context, s));
   }

   @Override
   public EnumBodyStep field(String... fields)
   {
      return addArray2(new EnumDsl(this), fields, (enumDsl, string) -> enumDsl.fields.add(context -> indent(context, string)));
   }

   @Override
   public EnumBodyStep field(List<? extends FieldRenderable> fields)
   {
      return addArrayRenderer(new EnumDsl(this),
                              fields,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              enumDsl -> enumDsl.fields::add);
   }

   @Override
   public EnumBodyStep method(String... methods)
   {
      return addArray2(new EnumDsl(this), methods, (enumDsl, string) -> enumDsl.methods.add(context -> indent(context, string)));
   }

   @Override
   public EnumBodyStep method(List<? extends MethodRenderable> methods)
   {
      return addArrayRenderer(new EnumDsl(this),
                              methods,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              enumDsl -> enumDsl.methods::add);
   }

   @Override
   public EnumBodyStep inner(String... inner)
   {
      return addArray2(new EnumDsl(this), inner, (enumDsl, string) -> enumDsl.inner.add(context -> indent(context, string)));
   }

   @Override
   public EnumBodyStep inner(List<? extends DeclaredRenderable> inner)
   {
      return addArrayRenderer(new EnumDsl(this),
                              inner,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              enumDsl -> enumDsl.inner::add);
   }

   @Override
   public EnumBodyStep instanceInitializer(String... instanceInitializers)
   {
      return addArrayRenderer(new EnumDsl(this), instanceInitializers, DslSupport::indent, enumDsl -> enumDsl.instanceInitializers::add);
   }

   @Override
   public EnumBodyStep staticInitializer(String... staticInitializer)
   {
      return addArrayRenderer(new EnumDsl(this), staticInitializer, DslSupport::indent, enumDsl -> enumDsl.staticInitializers::add);
   }

   @Override
   public EnumBodyStep constructor(String... constructors)
   {
      return addArray2(new EnumDsl(this), constructors, (enumDsl, string) -> enumDsl.constructors.add(context -> indent(context, string)));
   }

   @Override
   public EnumBodyStep constructor(List<? extends ConstructorRenderable> constructors)
   {
      return addArrayRenderer(new EnumDsl(this),
                              constructors,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              enumDsl -> enumDsl.constructors::add);
   }

   @Override
   public EnumPackageStep copyright(String copyrightHeader)
   {
      return setType(new EnumDsl(this),
                     copyrightHeader,
                     (enumDsl, string) -> enumDsl.copyright = string);
   }

   @Override
   public EnumImportStep package_(String packageName)
   {
      return setType(new EnumDsl(this),
                     packageName,
                     (enumDsl, string) -> enumDsl.package_ = new PackageRenderable()
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
   public EnumImportStep package_(PackageRenderable aPackage)
   {
      return setType(new EnumDsl(this),
                     aPackage,
                     (enumDsl, string) -> enumDsl.package_ = string);
   }

   @Override
   public EnumImportStep noPackage()
   {
      return new EnumDsl(this);
   }

   @Override
   public EnumImportStep import_(String... name)
   {
      return addArray2(new EnumDsl(this),
                       name,
                       (enumDsl, string) -> enumDsl.imports.add(renderingContext -> JavaDsl.import_(string).renderDeclaration(renderingContext)));
   }

   @Override
   public EnumImportStep import_(List<? extends ImportRenderable> declared)
   {
      return addArray2(new EnumDsl(this),
                       declared,
                       (enumDsl, importRenderable) -> enumDsl.imports.add(importRenderable::renderDeclaration));
   }

   @Override
   public EnumEnumConstantStep enumConstant(String... enumConstant)
   {
      return addArray2(new EnumDsl(this),
                       enumConstant,
                       (enumDsl, string) -> enumDsl.enumConstants.add(context -> indent(context, string)));
   }

   @Override
   public EnumEnumConstantStep enumConstant(List<? extends EnumConstantRenderable> enumConstant)
   {
      return addArrayRenderer(new EnumDsl(this),
                              enumConstant,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              enumDsl -> enumDsl.enumConstants::add);
   }

   @Override
   public String renderDeclaration(RenderingContext context1)
   {
      return context1.withSurrounding(this, context ->
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

         sb.append("enum ")
           .append(name)
           .append(' ');

         renderElement(sb, implements_, context, ", ", new Padding("implements ", null, null, " "));

         RenderingContext indented = createRenderingContext(context);
         indented.incrementIndentationLevel();
         sb.append("{\n");
         if (body != null)
         {
            sb.append(body.render(indented))
              .append('\n');
         }
         else
         {
            renderElement(sb, enumConstants, indented, ",\n\n", new Padding(null, null, null, ";\n"));
            renderElement(sb, fields, indented, "\n\n", new Padding(null, null, null, "\n"));
            renderElement(sb, staticInitializers, indented, "\n\n", new Padding(null, null, null, "\n\n"));
            renderElement(sb, constructors, indented, "\n\n", new Padding(null, null, null, "\n\n"));
            renderElement(sb, instanceInitializers, indented, "\n\n", new Padding(null, null, null, "\n\n"));
            renderElement(sb, methods, indented, "\n\n", new Padding(null, null, null, "\n\n"));
            renderElement(sb, inner, indented, "\n\n", new Padding(null, null, null, "\n\n"));
         }

         //render Header

         if (!imports.isEmpty() || !context.getImports().isEmpty())
         {
            sb.insert(0, "\n\n");
         }
         sb.insert(0, concat(imports.stream().map(renderable -> renderable.render(context)),
                             context.getImports().stream().map(renderable -> renderable.renderDeclaration(context))).collect(joining("\n")));

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

         sb.append(context.getLineIndentation())
           .append('}');

         return sb.toString();
      });
   }

   @Override
   public String renderSimpleName(RenderingContext renderingContext)
   {
      return name;
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
      return renderName(renderingContext);
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
