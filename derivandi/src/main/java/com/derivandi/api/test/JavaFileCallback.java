package com.derivandi.api.test;

import java.util.List;
import java.util.function.Consumer;

@FunctionalInterface
public interface JavaFileCallback
      extends Consumer<List<JavaFile>> {}