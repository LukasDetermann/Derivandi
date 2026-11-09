package com.derivandi.internal.test;

import com.derivandi.api.test.JavaClassFile;
import com.derivandi.api.test.JavaSourceFile;
import com.derivandi.api.test.OtherFile;

class JavaFileImpl
{
   private final String name;
   private final String content;

   JavaFileImpl(String name, String content)
   {
      this.name = name;
      this.content = content;
   }

   public String getName()
   {
      return name;
   }

   public String getContent()
   {
      return content;
   }

   static class Source
         extends JavaFileImpl
         implements JavaSourceFile
   {
      Source(String name, String content)
      {
         super(name, content);
      }
   }

   static class Class
         extends JavaFileImpl
         implements JavaClassFile
   {
      Class(String name, String content)
      {
         super(name, content);
      }
   }

   static class Other
         extends JavaFileImpl
         implements OtherFile
   {
      Other(String name, String content)
      {
         super(name, content);
      }
   }
}
