package com.derivandi.internal.test;


import com.derivandi.api.test.JavaFile;
import com.derivandi.api.test.ProcessorTest;
import org.jetbrains.annotations.NotNull;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * The compiler wants to write java sources it compiles. This is not needed for {@link ProcessorTest}s.
 * Furthermore, tests should be isolated from each other. Removing some potential for coupling should help with that.
 */
class TestFileManager
      extends ForwardingJavaFileManager<JavaFileManager>
{
   private final boolean hasJavaFileCallbacks;
   private final List<InternalFile> javaFileObjects = new ArrayList<>();

   TestFileManager(StandardJavaFileManager delegate, boolean hasJavaFileCallbacks)
   {
      super(delegate);
      this.hasJavaFileCallbacks = hasJavaFileCallbacks;
   }

   @Override
   public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling)
   {
      JavaFileObject javaFileObject = createSimpleJavaFileObject(className, kind);
      if (hasJavaFileCallbacks)
      {
         javaFileObjects.add(new InternalFile(className, javaFileObject));
      }
      return javaFileObject;
   }

   private static @NotNull JavaFileObject createSimpleJavaFileObject(String className, JavaFileObject.Kind kind)
   {
      return new SimpleJavaFileObject(URI.create("test:///" + className.replace('.', '/') + kind.extension), kind)
      {
         private byte[] content;

         @Override
         public CharSequence getCharContent(boolean ignoreEncodingErrors)
         {
            return new String(content);
         }

         @Override
         public ByteArrayOutputStream openOutputStream()
         {
            return new ByteArrayOutputStream()
            {
               @Override
               public void close() throws IOException
               {
                  super.close();
                  content = toByteArray();
               }
            };
         }
      };
   }

   List<JavaFile> getJavaFileObjects()
   {
      return javaFileObjects.stream().map(TestFileManager::createJavaFile).toList();
   }

   private static JavaFile createJavaFile(InternalFile internalFile)
   {
      try
      {
         return switch (internalFile.javaFileObject().getKind())
         {
            case SOURCE -> new JavaFileImpl.Source(internalFile.name(), (String) internalFile.javaFileObject().getCharContent(false));
            case CLASS -> new JavaFileImpl.Class(internalFile.name(), (String) internalFile.javaFileObject().getCharContent(false));
            case HTML, OTHER -> new JavaFileImpl.Other(internalFile.name(), (String) internalFile.javaFileObject().getCharContent(false));
         };
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private record InternalFile(String name,
                               JavaFileObject javaFileObject) {}
}
