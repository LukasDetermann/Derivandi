package com.derivandi.api.dsl;

import com.derivandi.api.dsl.import_.ImportRenderable;
import com.derivandi.internal.dsl.RenderingContextImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.List;
import java.util.function.Function;

import static com.derivandi.api.dsl.RenderingConfiguration.DEFAULT_CONFIGURATION;

/// Mutable Context that contains all information needed for rendering.
/// Each call to any `#render()` method should get a new instance of this context
///
/// @see #createRenderingContext()
/// @see #createRenderingContext(RenderingConfiguration)
/// @see #createRenderingContext(RenderingContext)
public interface RenderingContext
{
   /// creates a [RenderingContext] with [RenderingConfiguration#DEFAULT_CONFIGURATION]
   static RenderingContext createRenderingContext()
   {
      return createRenderingContext(DEFAULT_CONFIGURATION);
   }

   /// creates a [RenderingContext] using the [RenderingConfiguration]
   static RenderingContext createRenderingContext(RenderingConfiguration configuration)
   {
      return new RenderingContextImpl(configuration);
   }

   /// creates a copy of the supplied [RenderingContext]
   static RenderingContext createRenderingContext(RenderingContext renderingContext)
   {
      return new RenderingContextImpl(renderingContext);
   }

   /// Returns the objects surrounding the one being currently rendered
   Deque<Object> getSurrounding();

   <T> T withSurrounding(Object surrounding, Function<RenderingContext, T> withSurrounding);

   /// [#getLineIndentation()] = " ".repeat([#getIndentationLevel()] * [RenderingConfiguration#getIndentation()])
   ///
   /// @see RenderingConfiguration#getIndentation()
   /// @see #getLineIndentation()
   int getIndentationLevel();

   /// [#getLineIndentation()] = " ".repeat([#getIndentationLevel()] * [RenderingConfiguration#getIndentation()])
   ///
   /// @see RenderingConfiguration#getIndentation()
   /// @see #getIndentationLevel()
   String getLineIndentation();

   /// Should be called by the element owning the code block. e.g. class, method but not field
   ///
   /// @see RenderingContext#getIndentationLevel()
   void incrementIndentationLevel();

   List<ImportRenderable> getImports();

   /// @see RenderingConfigurationBuilder#withoutAutomaticImports()
   /// @see #getImports()
   /// @see #renderName(String)
   String renderName(@Nullable String packageName, String typeName);

   /// @see RenderingConfigurationBuilder#withoutAutomaticImports()
   /// @see #getImports()
   /// @see #renderName(String, String)
   String renderName(String qualifiedName);

   /// sets the current package name. for types of this package no imports will be rendered
   void setCurrentPackageName(String currentPackageName);
}