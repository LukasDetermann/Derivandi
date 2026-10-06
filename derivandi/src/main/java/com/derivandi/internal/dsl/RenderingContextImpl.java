package com.derivandi.internal.dsl;

import com.derivandi.api.dsl.RenderingConfiguration;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.import_.ImportRenderable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

public class RenderingContextImpl
      implements RenderingContext
{
   private final RenderingConfiguration configuration;
   private ImportRenderingContextImpl importContext;
   private final Deque<Object> surrounding;
   private String lineIndentation;
   private int indentationLevel;

   public RenderingContextImpl(RenderingContext renderingContext)
   {
      RenderingContextImpl other = (RenderingContextImpl) renderingContext;

      this.configuration = other.configuration;
      this.importContext = other.importContext;
      this.surrounding = new ArrayDeque<>(other.surrounding);
      this.lineIndentation = other.lineIndentation;
      this.indentationLevel = other.indentationLevel;
   }

   public RenderingContextImpl(RenderingConfiguration configuration)
   {
      this.configuration = configuration;
      this.importContext = new ImportRenderingContextImpl(configuration);
      this.surrounding = new ArrayDeque<>();
      this.lineIndentation = "";
      this.indentationLevel = 0;
   }

   @Override
   public Deque<Object> getSurrounding()
   {
      return new ArrayDeque<>(surrounding);
   }

   @Override
   public <T> T withSurrounding(Object surrounding, Function<RenderingContext, T> withSurrounding)
   {
      this.surrounding.add(surrounding);
      T result = withSurrounding.apply(this);
      this.surrounding.removeLast();
      return result;
   }

   @Override
   public int getIndentationLevel()
   {
      return indentationLevel;
   }

   @Override
   public String getLineIndentation()
   {
      return lineIndentation;
   }

   @Override
   public void incrementIndentationLevel()
   {
      indentationLevel++;
      lineIndentation = lineIndentation + " ".repeat(configuration.getIndentation());
   }

   public void setCurrentPackageName(String currentPackageName)
   {
      importContext.setCurrentPackageName(currentPackageName);
   }

   @Override
   public List<ImportRenderable> getImports()
   {
      return importContext.getImports();
   }

   @Override
   public String renderName(@Nullable String packageName, String typeName)
   {
      return importContext.renderName(packageName, typeName);
   }

   @Override
   public String renderName(String qualifiedName)
   {
      QualifiedName splitName = splitName(qualifiedName);
      return renderName(splitName.packageName(), splitName.typeName());
   }

   public static @NotNull QualifiedName splitName(String qualifiedName)
   {
      requireNonNull(qualifiedName);

      if (qualifiedName.isBlank())
      {
         throw new IllegalArgumentException("Blank name");
      }

      String[] byDot = qualifiedName.split("\\.");

      if (byDot.length == 0)
      {
         throw new IllegalArgumentException('\"' + qualifiedName + '\"' + "is not a valid type name");
      }

      //no package and no outer class
      if (byDot.length == 1)
      {
         return new QualifiedName(null, qualifiedName);
      }
      for (int i = 0; i < byDot.length; i++)
      {
         boolean isTypeName = !byDot[i].isEmpty() && Character.isUpperCase(byDot[i].charAt(0));
         if (isTypeName)
         {
            if (i == 0)
            {
               //Outer + Inner without package
               return new QualifiedName(null, qualifiedName);
            }
            // normal or inner type
            return new QualifiedName(stream(byDot)
                                           .limit(i)
                                           .collect(Collectors.joining(".")),
                                     stream(byDot)
                                           .skip(i)
                                           .collect(Collectors.joining(".")));
         }
      }

      //lowercase className, can not handle an inner class with a lowercase outer class
      return new QualifiedName(stream(byDot)
                                     .limit(byDot.length - 1L)
                                     .collect(Collectors.joining(".")),
                               stream(byDot)
                                     .skip(byDot.length - 1L)
                                     .collect(Collectors.joining(".")));
   }

   ImportRenderingContextImpl getImportContext()
   {
      return importContext;
   }
}