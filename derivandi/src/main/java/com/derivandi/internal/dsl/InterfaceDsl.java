package com.derivandi.internal.dsl;


import com.derivandi.api.Modifier;
import com.derivandi.api.dsl.JavaDsl;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.TypeRenderable;
import com.derivandi.api.dsl.annotation_usage.AnnotationUsageRenderable;
import com.derivandi.api.dsl.declared.DeclaredRenderable;
import com.derivandi.api.dsl.field.FieldRenderable;
import com.derivandi.api.dsl.generic.GenericRenderable;
import com.derivandi.api.dsl.import_.ImportRenderable;
import com.derivandi.api.dsl.interface_.*;
import com.derivandi.api.dsl.method.MethodRenderable;
import com.derivandi.api.dsl.package_.PackageRenderable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.derivandi.api.dsl.RenderingContext.createRenderingContext;
import static com.derivandi.internal.dsl.DslSupport.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

public class InterfaceDsl
      implements InterfaceCopyrightHeaderStep,
                 InterfaceImportStep,
                 InterfaceGenericStep,
                 InterfaceOuterStep
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
   private final List<Renderable> genericDeclarations = new ArrayList<>();
   private final List<Renderable> genericUsages = new ArrayList<>();
   private final List<Renderable> extends_ = new ArrayList<>();
   private final List<Renderable> permits = new ArrayList<>();
   private final List<Renderable> fields = new ArrayList<>();
   private final List<Renderable> methods = new ArrayList<>();
   private final List<Renderable> inner = new ArrayList<>();
   private Renderable body;

   public InterfaceDsl() {}

   private InterfaceDsl(InterfaceDsl other)
   {
      this.outerType = other.outerType;

      this.copyright = other.copyright;
      this.package_ = other.package_;
      this.imports.addAll(other.imports);

      this.javadoc = other.javadoc;
      this.annotations.addAll(other.annotations);
      this.modifiers.addAll(other.modifiers);
      this.name = other.name;
      this.genericDeclarations.addAll(other.genericDeclarations);
      this.genericUsages.addAll(other.genericUsages);
      this.extends_.addAll(other.extends_);
      this.permits.addAll(other.permits);
      this.fields.addAll(other.fields);
      this.methods.addAll(other.methods);
      this.inner.addAll(other.inner);
      this.body = other.body;
   }

   @Override
   public InterfaceJavaDocStep outer(String outerType)
   {
      return setTypeRenderer(new InterfaceDsl(this), outerType, (interfaceDsl, s) -> interfaceDsl.outerType = s);
   }

   @Override
   public InterfaceJavaDocStep outer(DeclaredRenderable qualifiedOuterType)
   {
      return setTypeRenderer(new InterfaceDsl(this),
                             qualifiedOuterType,
                             (renderingContext, declaredRenderable) -> declaredRenderable.renderQualifiedName(renderingContext),
                             (interfaceDsl, s) -> interfaceDsl.outerType = s);
   }

   @Override
   public InterfaceAnnotateStep javadoc(String javadoc)
   {
      return setTypeRenderer(new InterfaceDsl(this),
                             javadoc,
                             DslSupport::indent,
                             (interfaceDsl, function) -> interfaceDsl.javadoc = function);
   }

   @Override
   public InterfaceAnnotateStep annotate(String... annotation)
   {
      return addArray2(new InterfaceDsl(this),
                       annotation,
                       (interfaceDsl, string) -> interfaceDsl.annotations.add(context -> indent(context, '@' + context.renderName(string))));
   }

   @Override
   public InterfaceAnnotateStep annotate(List<? extends AnnotationUsageRenderable> annotation)
   {
      return addArrayRenderer(new InterfaceDsl(this),
                              annotation,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              interfaceDsl -> interfaceDsl.annotations::add);
   }

   @Override
   public InterfaceModifierStep modifier(String... modifiers)
   {
      return addArrayRenderer(new InterfaceDsl(this), modifiers, interfaceDsl -> interfaceDsl.modifiers::add);
   }

   @Override
   public InterfaceModifierStep modifier(Set<Modifier> modifiers)
   {
      return addArray2(new InterfaceDsl(this),
                       modifiers,
                       (interfaceDsl, modifier) -> interfaceDsl.modifiers.add(modifier::render));
   }

   @Override
   public InterfaceModifierStep abstract_()
   {
      return setType(new InterfaceDsl(this),
                     Modifier.ABSTRACT,
                     (interfaceDsl, modifier) -> interfaceDsl.modifiers.add(modifier::render));
   }

   @Override
   public InterfaceModifierStep public_()
   {
      return setType(new InterfaceDsl(this),
                     Modifier.PUBLIC,
                     (interfaceDsl, modifier) -> interfaceDsl.modifiers.add(modifier::render));
   }

   @Override
   public InterfaceModifierStep protected_()
   {
      return setType(new InterfaceDsl(this),
                     Modifier.PROTECTED,
                     (interfaceDsl, modifier) -> interfaceDsl.modifiers.add(modifier::render));
   }

   @Override
   public InterfaceModifierStep private_()
   {
      return setType(new InterfaceDsl(this),
                     Modifier.PRIVATE,
                     (interfaceDsl, modifier) -> interfaceDsl.modifiers.add(modifier::render));
   }

   @Override
   public InterfaceModifierStep sealed()
   {
      return setType(new InterfaceDsl(this),
                     Modifier.SEALED,
                     (interfaceDsl, modifier) -> interfaceDsl.modifiers.add(modifier::render));
   }

   @Override
   public InterfaceModifierStep nonSealed()
   {
      return setType(new InterfaceDsl(this),
                     Modifier.NON_SEALED,
                     (interfaceDsl, modifier) -> interfaceDsl.modifiers.add(modifier::render));
   }

   @Override
   public InterfaceModifierStep static_()
   {
      return setType(new InterfaceDsl(this),
                     Modifier.STATIC,
                     (interfaceDsl, modifier) -> interfaceDsl.modifiers.add(modifier::render));
   }

   @Override
   public InterfaceModifierStep strictfp_()
   {
      return setType(new InterfaceDsl(this),
                     Modifier.STRICTFP,
                     (interfaceDsl, modifier) -> interfaceDsl.modifiers.add(modifier::render));
   }

   @Override
   public InterfaceGenericStep name(String name)
   {
      return setType(new InterfaceDsl(this), name, (interfaceDsl, s) -> interfaceDsl.name = s);
   }

   @Override
   public InterfaceGenericStep genericDeclaration(String... genericDeclarations)
   {
      return addArray2(new InterfaceDsl(this),
                       genericDeclarations, (interfaceDsl, string) -> interfaceDsl.genericDeclarations.add(renderingContext -> string));
   }

   @Override
   public InterfaceGenericStep genericDeclaration(List<? extends GenericRenderable> genericDeclarations)
   {
      return addArrayRenderer(new InterfaceDsl(this),
                              genericDeclarations,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              interfaceDsl -> interfaceDsl.genericDeclarations::add);
   }

   @Override
   public InterfaceGenericStep genericUsage(String... genericUsages)
   {
      return addArray2(new InterfaceDsl(this),
                       genericUsages, (interfaceDsl, string) -> interfaceDsl.genericUsages.add(renderingContext -> string));
   }

   @Override
   public InterfaceGenericStep genericUsage(List<? extends TypeRenderable> genericUsages)
   {
      return addArrayRenderer(new InterfaceDsl(this),
                              genericUsages,
                              (renderingContext, renderable) -> renderable.renderType(renderingContext),
                              interfaceDsl -> interfaceDsl.genericUsages::add);
   }

   @Override
   public InterfacePermitsStep permits(String... declared)
   {
      return addArray2(new InterfaceDsl(this),
                       declared,
                       (interfaceDsl, string) -> interfaceDsl.permits.add(context -> context.renderName(string)));
   }

   @Override
   public InterfacePermitsStep permits(List<? extends DeclaredRenderable> declared)
   {
      return addArrayRenderer(new InterfaceDsl(this),
                              declared,
                              (renderingContext, renderable) -> renderable.renderQualifiedName(renderingContext),
                              interfaceDsl -> interfaceDsl.permits::add);
   }

   @Override
   public InterfaceRenderable body(String body)
   {
      return setType(new InterfaceDsl(this),
                     body,
                     (interfaceDsl, s) -> interfaceDsl.body = context -> indent(context, s));
   }

   @Override
   public InterfaceBodyStep field(String... fields)
   {
      return addArray2(new InterfaceDsl(this), fields, (interfaceDsl, string) -> interfaceDsl.fields.add(context -> indent(context, string)));
   }

   @Override
   public InterfaceBodyStep field(List<? extends FieldRenderable> fields)
   {
      return addArrayRenderer(new InterfaceDsl(this),
                              fields,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              interfaceDsl -> interfaceDsl.fields::add);
   }

   @Override
   public InterfaceBodyStep method(String... methods)
   {
      return addArray2(new InterfaceDsl(this), methods, (interfaceDsl, string) -> interfaceDsl.methods.add(context -> indent(context, string)));
   }

   @Override
   public InterfaceBodyStep method(List<? extends MethodRenderable> methods)
   {
      return addArrayRenderer(new InterfaceDsl(this),
                              methods,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              interfaceDsl -> interfaceDsl.methods::add);
   }

   @Override
   public InterfaceBodyStep inner(String... inner)
   {
      return addArray2(new InterfaceDsl(this), inner, (interfaceDsl, string) -> interfaceDsl.inner.add(context -> indent(context, string)));
   }

   @Override
   public InterfaceBodyStep inner(List<? extends DeclaredRenderable> inner)
   {
      return addArrayRenderer(new InterfaceDsl(this),
                              inner,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              interfaceDsl -> interfaceDsl.inner::add);
   }

   @Override
   public InterfacePackageStep copyright(String copyrightHeader)
   {
      return setType(new InterfaceDsl(this),
                     copyrightHeader,
                     (interfaceDsl, string) -> interfaceDsl.copyright = string);
   }

   @Override
   public InterfaceImportStep package_(String packageName)
   {
      return setType(new InterfaceDsl(this),
                     packageName,
                     (interfaceDsl, string) -> interfaceDsl.package_ = new PackageRenderable()
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
   public InterfaceImportStep package_(PackageRenderable aPackage)
   {
      return setType(new InterfaceDsl(this),
                     aPackage,
                     (interfaceDsl, string) -> interfaceDsl.package_ = string);
   }

   @Override
   public InterfaceImportStep noPackage()
   {
      return new InterfaceDsl(this);
   }

   @Override
   public InterfaceImportStep import_(String... name)
   {
      return addArray2(new InterfaceDsl(this),
                       name,
                       (interfaceDsl, string) -> interfaceDsl.imports.add(renderingContext -> JavaDsl.import_(string)
                                                                                                     .renderDeclaration(renderingContext)));
   }

   @Override
   public InterfaceImportStep import_(List<? extends ImportRenderable> declared)
   {
      return addArray2(new InterfaceDsl(this),
                       declared,
                       (interfaceDsl, importRenderable) -> interfaceDsl.imports.add(importRenderable::renderDeclaration));
   }

   @Override
   public InterfaceExtendsStep extends_(String... interfaces)
   {
      return addArray2(new InterfaceDsl(this),
                       interfaces,
                       (interfaceDsl, string) -> interfaceDsl.extends_.add(context -> context.renderName(string)));
   }

   @Override
   public InterfaceExtendsStep extends_(List<? extends InterfaceRenderable> interfaces)
   {
      return addArrayRenderer(new InterfaceDsl(this),
                              interfaces,
                              (renderingContext, interfaceRenderable) -> interfaceRenderable.renderQualifiedName(renderingContext),
                              interfaceDsl -> interfaceDsl.extends_::add);
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

         sb.append("interface ");
         sb.append(name);
         sb.append(' ');

         renderElement(sb, "<", genericDeclarations, "> ", context, ", ");

         renderElement(sb, "extends ", extends_, " ", context, ", ");
         renderElement(sb, "permits ", permits, " ", context, ", ");

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
   public String renderType(RenderingContext context)
   {
      return context.withSurrounding(this, context1 ->
      {
         String qualifiedName = renderName(context);
         if (genericUsages.isEmpty())
         {
            return qualifiedName;
         }
         return qualifiedName + '<' + genericUsages.stream().map(renderable -> renderable.render(context)).collect(joining(", ")) + '>';
      });
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
