# Shadow Api


Java SE and the Java ecosystem offer a multitude of Metaprogramming
Apis. They serve the same purpose just in different contexts. Annotation
processing to analyse classes in the during compilation process, or
Reflection to do the same at runtime. Each having unique concepts and
quirks. This makes Metaprogramming harder to understand and creates a
maintenance overhead. It’s very hard to switch from one Api to another.
Once something is written with java.lang.reflect it’s hard to change it
to an Annotation Processor. Some JDK Apis are not up to the normal
standard.


This project is an Abstraction for Metaprogramming


![Overview](https://www.shadow.determann.io/Shadow-Api/_images/Overview.svg)


It has two Types of Apis



## General Purpose Api


This Api works with any kind of Metaprogramming, but the featureset is
limited to common operations

<details>
<summary>Example</summary>


The Api is request based. As a caller you can request for example a
field of a class. Accessing fields may or may not be supported.


``` highlightjs
@Test
void request()
{
   //adapter for the reflection api
   C_Class systemClass = R_Adapter.generalize(System.class);
   //request the field "out" for the class java.lang.System
   Response<C_Field> out = Provider.request(systemClass,
                                            Operations.DECLARED_GET_FIELD,
                                            "out");

   switch (out)
   {
      //the implementation may not support this operation
      //e.g. it's impossible to access fields with reflection
      case Response.Unsupported<C_Field> unsupported -> Assertions.fail();
      //the implementation may support this operation, but there is no
      //parts for this instance
      //e.g. the class java.lang.System does not have a field called "out"
      case Response.Empty<C_Field> empty -> Assertions.fail();
      //accessing fields via reflection is possible and java.lang.System
      //does have a field called "out" therefore a parts is expected
      case Response.Result<C_Field> parts -> assertNotNull(parts.value());
   }
}
```

Or use a convenience method if `Optional.empty()` or throwing an
Exception is a fitting default behavior.

``` highlightjs
@Test
void requestOrEmpty()
{
   //adapter for the reflection api
   C_Class systemClass = R_Adapter.generalize(System.class);
   //request the field "out" for the class java.lang.System.
   //If its unsupported an Empty Optional is returned
   Optional<C_Field> out = Provider.requestOrEmpty(systemClass,
                                                   Operations.DECLARED_GET_FIELD,
                                                   "out");

   assertTrue(out.isPresent());
}
```
``` highlightjs
@Test
void requestOrThrow()
{
   //adapter for the reflection api
   C_Class systemClass = R_Adapter.generalize(System.class);
   //request the field "out" for the class java.lang.System.
   //If its unsupported an Exception is thrown
   C_Field out = Provider.requestOrThrow(systemClass,
                                         Operations.DECLARED_GET_FIELD,
                                         "out");

   assertNotNull(out);
}
```
</details>



## Specific Apis

There is one Api for each kind of Metaprogramming. One for Annotation
processing and another very similar one for reflection. Each being able
to support the complete featureset.

<details>
<summary>Example</summary>


This Annotation Processor generates Builder


``` highlightjs
package com.derivandi.shadow.builder;

import annotation_processing.api.com.derivandi.Ap;
import com.derivandi.shadow.api.dsl.Dsl;
import com.derivandi.shadow.api.dsl.class_.ClassBodyStep;

import java.util.ArrayList;
import java.util.List;

import static com.derivandi.shadow.api.dsl.RenderingContext.createRenderingContext;
import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

/// Builds a companion Builder class for each annotated class
public class ShadowBuilderProcessor
      extends Ap.Processor
{
   @Override
   public void process(final Ap.Context context)
   {
      // iterate over every class annotated with the BuilderPattern annotation
      for (Ap.Class toBuild : context
            .getClassesAnnotatedWith("builder.com.derivandi.BuilderPattern"))
      {
         // create the Builder Class
         ClassBodyStep step = Dsl.class_()
                                 .package_(toBuild.getPackage().getQualifiedName())
                                 .name(toBuild.getName() + "ShadowBuilder");

         String toBuildVariableName = uncapitalize(toBuild.getName());
         List<String> setterInvocations = new ArrayList<>();

         // create a record holding the code needed to render a property in the builder
         for (Ap.Property property : toBuild.getProperties()
                                            .stream()
                                            .filter(Ap.Property::isMutable)
                                            .toList())
         {
            String propertyName = property.getName();

            // render the existing field if possible, otherwise create a new one
            step = property.getField().map(step::field)
                           .orElse(step.field(Dsl.field(property.getType(), propertyName)));

            // render the wither
            step = step.method(Dsl.method()
                                  .public_()
                                  .resultType(step)//use the builder type
                                  .name("with" + capitalize(propertyName))
                                  .parameter(Dsl.parameter(property.getType(), propertyName))
                                  .body("""
                                        this.%1$s = %1$s;
                                        return this;""".formatted(propertyName)));

            // collect all setter invocations for the object being build
            setterInvocations.add(toBuildVariableName + "." +
                                  property.getSetterOrThrow().getName() +
                                  "(" + propertyName + ");");
         }

         // render the build method
         step = step.method(Dsl.method().public_().resultType(toBuild).name("build")
                               .body("""
                                     %1$s %2$s = new %1$s();
                                     %3$s
                                     return %2$s;
                                     """.formatted(toBuild.getName(),
                                                   toBuildVariableName,
                                                   join("\n\n", setterInvocations))));

         //writes the builder
         context.writeAndCompileSourceFile(toBuild.getQualifiedName() + "ShadowBuilder",
                                           step.renderDeclaration(createRenderingContext()));
      }
   }
}
```
</details>



## Supported

- [Annotation Processing](https://www.shadow.determann.io/Shadow-Api/Annotation%20Processing.html)
- [reflection](https://www.shadow.determann.io/Shadow-Api/Reflection.html) (experimental)



## Next Goals

- Improve Annotation Processing Support
  - Add support for specific Annotations
- Improve Support for other Metaprogramming Apis
  - Move more of the implementation to the `shadow-implementation-support`
    module for better consistency and easier support of multiple
    implementations
  - Support the complete Lexical Structure
  - Create a maven archetype to reduce the effort of supporting new
    Metaprogramming Implementations