package com.derivandi.internal.dsl;


import com.derivandi.api.Modifier;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.VariableTypeRenderable;
import com.derivandi.api.dsl.annotation_usage.AnnotationUsageRenderable;
import com.derivandi.api.dsl.field.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.derivandi.internal.dsl.DslSupport.*;

public class FieldDsl
      implements FieldJavaDocStep,
                 FieldNameStep,
                 FieldInitializationStep
{
   private Renderable javadoc;
   private final List<Renderable> annotations = new ArrayList<>();
   private final List<Renderable> modifiers = new ArrayList<>();
   private Renderable type;
   private String name;
   private String initializer;

   public FieldDsl() {}

   private FieldDsl(FieldDsl other)
   {
      this.javadoc = other.javadoc;
      this.annotations.addAll(other.annotations);
      this.modifiers.addAll(other.modifiers);
      this.type = other.type;
      this.name = other.name;
      this.initializer = other.initializer;
   }

   @Override
   public FieldAnnotateStep javadoc(String javadoc)
   {
      return setTypeRenderer(new FieldDsl(this),
                             javadoc,
                             DslSupport::indent,
                             (fieldDsl, function) -> fieldDsl.javadoc = function);
   }

   @Override
   public FieldAnnotateStep annotate(String... annotation)
   {
      return addArrayRenderer(new FieldDsl(this),
                              annotation,
                              (context, string) -> indent(context, '@' + string),
                              fieldDsl -> fieldDsl.annotations::add);
   }

   @Override
   public FieldAnnotateStep annotate(List<? extends AnnotationUsageRenderable> annotation)
   {
      return addArrayRenderer(new FieldDsl(this),
                              annotation,
                              (renderingContext, renderable) -> renderable.renderDeclaration(renderingContext),
                              fieldDsl -> fieldDsl.annotations::add);
   }

   @Override
   public FieldAdditionalNameStep initializer(String initializer)
   {
      // in this special the initializer needs to be added to the base field dsl to mach the count of names and initializers
      return setType(new Additional(this), initializer, (additional, s) -> this.initializer = s);
   }

   @Override
   public FieldModifierStep modifier(String... modifiers)
   {
      return addArrayRenderer(new FieldDsl(this), modifiers, fieldDsl -> fieldDsl.modifiers::add);
   }

   @Override
   public FieldModifierStep modifier(Set<Modifier> modifiers)
   {
      return addArray2(new FieldDsl(this),
                       modifiers,
                       (fieldDsl, modifier) -> fieldDsl.modifiers.add(modifier::render));
   }

   @Override
   public FieldModifierStep public_()
   {
      return setType(new FieldDsl(this),
                     Modifier.PUBLIC,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public FieldModifierStep protected_()
   {
      return setType(new FieldDsl(this),
                     Modifier.PROTECTED,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public FieldModifierStep private_()
   {
      return setType(new FieldDsl(this),
                     Modifier.PRIVATE,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public FieldModifierStep final_()
   {
      return setType(new FieldDsl(this),
                     Modifier.FINAL,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public FieldModifierStep static_()
   {
      return setType(new FieldDsl(this),
                     Modifier.STATIC,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public FieldModifierStep strictfp_()
   {
      return setType(new FieldDsl(this),
                     Modifier.STRICTFP,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public FieldModifierStep transient_()
   {
      return setType(new FieldDsl(this),
                     Modifier.TRANSIENT,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public FieldModifierStep volatile_()
   {
      return setType(new FieldDsl(this),
                     Modifier.VOLATILE,
                     (enumDsl, modifier) -> enumDsl.modifiers.add(modifier::render));
   }

   @Override
   public FieldInitializationStep name(String name)
   {
      return setType(new FieldDsl(this), name, (fieldDsl, s) -> fieldDsl.name = s);
   }

   @Override
   public FieldNameStep type(String type)
   {
      return setType(new FieldDsl(this), type, (fieldDsl, string) -> fieldDsl.type = context -> context.renderName(string));
   }

   @Override
   public FieldNameStep type(VariableTypeRenderable type)
   {
      return setTypeRenderer(new FieldDsl(this),
                             type,
                             (renderingContext, variableTypeRenderable) -> variableTypeRenderable.renderType(renderingContext),
                             (fieldDsl, function) -> fieldDsl.type = function);
   }

   @Override
   public String renderDeclaration(RenderingContext context1)
   {
      return context1.withSurrounding(this, context -> partialRender(context) + ';');
   }

   private String partialRender(RenderingContext context)
   {
      StringBuilder sb = new StringBuilder();
      if (javadoc != null)
      {
         sb.append(javadoc.render(context))
           .append("\n");
      }
      renderElement(sb, annotations, context, "\n", new Padding(null, context.getLineIndentation(), null, "\n"));
      sb.append(context.getLineIndentation());
      if (!modifiers.isEmpty())
      {
         renderElement(sb, modifiers, context, " ", new Padding(null, null, null, " "));
      }

      sb.append(type.render(context))
        .append(' ');

      sb.append(name);

      if (initializer != null)
      {
         sb.append(" = ")
           .append(initializer);
      }
      return sb.toString();
   }

   @Override
   public String renderName(RenderingContext renderingContext)
   {
      return name;
   }

   private static class Additional
         implements FieldAdditionalNameStep,
                    FieldAdditionalInitializationStep
   {
      private final List<String> initializers = new ArrayList<>();
      private final List<String> names = new ArrayList<>();

      private final FieldDsl fieldDsl;

      private Additional(FieldDsl fieldDsl)
      {
         this.fieldDsl = fieldDsl;
      }

      private Additional(Additional other)
      {
         this.fieldDsl = other.fieldDsl;
         this.initializers.addAll(other.initializers);
         this.names.addAll(other.names);
      }

      @Override
      public FieldAdditionalNameStep initializer(String initializer)
      {
         return setType(new Additional(this), initializer, (additional, s) -> additional.initializers.add(s));
      }

      @Override
      public FieldAdditionalInitializationStep name(String name)
      {
         return setType(new Additional(this), name, (additional, s) -> additional.names.add(s));
      }

      @Override
      public String renderDeclaration(RenderingContext context)
      {
         return context.withSurrounding(this, context1 ->
         {
            StringBuilder sb = new StringBuilder();

            sb.append(fieldDsl.partialRender(context));

            for (int i = 0; i < initializers.size(); i++)
            {
               sb.append(", ")
                 .append(names.get(i))
                 .append(" = ")
                 .append(initializers.get(i));
            }

            if (names.size() == initializers.size() + 1)
            {
               sb.append(", ")
                 .append(names.getLast());
            }
            sb.append(';');

            return sb.toString();
         });
      }

      @Override
      public String renderName(RenderingContext renderingContext)
      {
         return fieldDsl.name;
      }
   }
}
