package com.derivandi.api.test;

public sealed interface JavaFile
      permits JavaSourceFile,
              JavaClassFile,
              OtherFile
{
   String getName();

   String getContent();
}