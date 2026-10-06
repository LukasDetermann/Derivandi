package com.derivandi.article.intro.validation;

//@formatter:off
//tag::content[]
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface StaticInterface {

   Class<?> value();
}
//end::content[]
//@formatter:on