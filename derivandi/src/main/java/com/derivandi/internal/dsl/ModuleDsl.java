package com.derivandi.internal.dsl;

import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.annotation_usage.AnnotationUsageRenderable;
import com.derivandi.api.dsl.exports.ExportsRenderable;
import com.derivandi.api.dsl.module.*;
import com.derivandi.api.dsl.opens.OpensRenderable;
import com.derivandi.api.dsl.provides.ProvidesRenderable;
import com.derivandi.api.dsl.requires.RequiresRenderable;
import com.derivandi.api.dsl.uses.UsesRenderable;

import java.util.ArrayList;
import java.util.List;

import static com.derivandi.internal.dsl.DslSupport.*;

public class ModuleDsl
      implements ModuleCopyrightHeaderStep,
                 ModuleRequiresStep
{
   private String copyright;
   private Renderable javadoc;
   private final List<Renderable> annotations = new ArrayList<>();
   private String name;
   private final List<Renderable> requires = new ArrayList<>();
   private final List<Renderable> exports = new ArrayList<>();
   private final List<Renderable> opens = new ArrayList<>();
   private final List<Renderable> uses = new ArrayList<>();
   private final List<Renderable> provides = new ArrayList<>();

   public ModuleDsl() {}

   private ModuleDsl(ModuleDsl other)
   {
      this.copyright = other.copyright;
      this.javadoc = other.javadoc;
      this.annotations.addAll(other.annotations);
      this.name = other.name;
      this.requires.addAll(other.requires);
      this.exports.addAll(other.exports);
      this.opens.addAll(other.opens);
      this.uses.addAll(other.uses);
      this.provides.addAll(other.provides);
   }

   @Override
   public ModuleJavaDocStep copyright(String copyrightHeader)
   {
      return setType(new ModuleDsl(this),
                     copyrightHeader,
                     (moduleDsl, string) -> moduleDsl.copyright = string);
   }

   @Override
   public ModuleAnnotateStep javadoc(String javadoc)
   {
      return setTypeRenderer(new ModuleDsl(this), javadoc, (moduleDsl, function) -> moduleDsl.javadoc = function);
   }

   @Override
   public ModuleAnnotateStep annotate(String... annotation)
   {
      return addArrayRenderer(new ModuleDsl(this),
                              annotation,
                              (renderingContext, string) -> '@' + string,
                              moduleDsl -> moduleDsl.annotations::add);
   }

   @Override
   public ModuleAnnotateStep annotate(List<? extends AnnotationUsageRenderable> annotation)
   {
      return addArrayRenderer(new ModuleDsl(this),
                              annotation,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              moduleDsl -> moduleDsl.annotations::add);
   }

   @Override
   public ModuleRequiresStep name(String name)
   {
      return setType(new ModuleDsl(this), name, (moduleDsl, s) -> moduleDsl.name = s);
   }

   @Override
   public ModuleRequiresStep requires(String... requires)
   {
      return addArray2(new ModuleDsl(this), requires, (moduleDsl, s) -> moduleDsl.requires.add(renderingContext -> "requires " + s + ';'));
   }

   @Override
   public ModuleRequiresStep requires(List<? extends RequiresRenderable> requires)
   {
      return addArrayRenderer(new ModuleDsl(this),
                              requires,
                              (renderingContext, requiresRenderable) -> requiresRenderable.renderDeclaration(renderingContext),
                              moduleDsl -> moduleDsl.requires::add);
   }

   @Override
   public ModuleExportsStep exports(String... exports)
   {
      return addArray2(new ModuleDsl(this), exports, (moduleDsl, s) -> moduleDsl.exports.add(renderingContext -> "exports " + s + ';'));
   }

   @Override
   public ModuleExportsStep exports(List<? extends ExportsRenderable> exports)
   {
      return addArrayRenderer(new ModuleDsl(this),
                              exports,
                              (renderingContext, exportsRenderable) -> exportsRenderable.renderDeclaration(renderingContext),
                              moduleDsl -> moduleDsl.exports::add);
   }

   @Override
   public ModuleOpensStep opens(String... opens)
   {
      return addArray2(new ModuleDsl(this), opens, (moduleDsl, s) -> moduleDsl.opens.add(renderingContext -> "opens " + s + ';'));
   }

   @Override
   public ModuleOpensStep opens(List<? extends OpensRenderable> opens)
   {
      return addArrayRenderer(new ModuleDsl(this),
                              opens,
                              (renderingContext, opensRenderable) -> opensRenderable.renderDeclaration(renderingContext),
                              moduleDsl -> moduleDsl.opens::add);
   }

   @Override
   public ModuleUsesStep uses(String... uses)
   {
      return addArray2(new ModuleDsl(this), uses, (moduleDsl, s) -> moduleDsl.uses.add(renderingContext -> "uses " + s + ';'));
   }

   @Override
   public ModuleUsesStep uses(List<? extends UsesRenderable> uses)
   {
      return addArrayRenderer(new ModuleDsl(this),
                              uses,
                              (renderingContext, usesRenderable) -> usesRenderable.renderDeclaration(renderingContext),
                              moduleDsl -> moduleDsl.uses::add);
   }

   @Override
   public ModuleProvidesStep provides(String... provides)
   {
      return addArray2(new ModuleDsl(this), provides, (moduleDsl, s) -> moduleDsl.provides.add(renderingContext -> "provies " + s + ';'));
   }

   @Override
   public ModuleProvidesStep provides(List<? extends ProvidesRenderable> provides)
   {
      return addArrayRenderer(new ModuleDsl(this),
                              provides,
                              (renderingContext, providesRenderable) -> providesRenderable.renderDeclaration(renderingContext),
                              moduleDsl -> moduleDsl.provides::add);
   }

   @Override
   public String renderModuleInfo(RenderingContext context1)
   {
      return context1.withSurrounding(this, context ->
      {
         StringBuilder sb = new StringBuilder();
         if (copyright != null)
         {
            sb.append(copyright)
              .append("\n\n");
         }
         if (javadoc != null)
         {
            sb.append(javadoc.render(context))
              .append("\n");
         }

         renderElement(sb, annotations, "\n", context, "\n");

         sb.append("module ")
           .append(name)
           .append(" {\n");

         renderElement(sb, "\n", requires, "\n", context, "\n");
         renderElement(sb, "\n", exports, "\n", context, "\n");
         renderElement(sb, "\n", opens, "\n", context, "\n");
         renderElement(sb, "\n", uses, "\n", context, "\n");
         renderElement(sb, "\n", provides, "\n", context, "\n");

         return sb.append('}')
                  .toString();
      });
   }

   @Override
   public String renderQualifiedName(RenderingContext renderingContext)
   {
      return name;
   }
}
