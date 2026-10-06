package com.derivandi.builder;

import com.derivandi.api.D;
import com.derivandi.api.dsl.JavaDsl;
import com.derivandi.api.dsl.class_.ClassBodyStep;
import com.derivandi.api.processor.Processor;
import com.derivandi.api.processor.ProcessorBuilder;
import com.derivandi.api.processor.ProcessorConfiguration;
import com.derivandi.api.processor.SimpleContext;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

/// Builds a companion Builder class for each annotated class
public class DerivandiBuilderProcessor
      extends Processor
{
   @Override
   public ProcessorConfiguration buildProcessor(ProcessorBuilder processorBuilder)
   {
      return processorBuilder.process(DerivandiBuilderProcessor::process);
   }

   private static void process(SimpleContext context)
   {
      // iterate over every class annotated with the BuilderPattern annotation
      for (D.Class toBuild : context.getClassesAnnotatedWith("com.derivandi.builder.BuilderPattern"))
      {
         // create the Builder Class
         ClassBodyStep step = JavaDsl.class_()
                                     .package_(toBuild.getPackage().getQualifiedName())
                                     .name(toBuild.getName() + "DerivandiBuilder");

         String toBuildVariableName = uncapitalize(toBuild.getName());
         List<String> setterInvocations = new ArrayList<>();

         // loop over all needed properties
         for (D.Property property : toBuild.getProperties()
                                           .stream()
                                           .filter(D.Property::isMutable)
                                           .toList())
         {
            String propertyName = property.getName();

            // render the existing field if possible, otherwise create a new one
            step = property.getField().map(step::field)
                           .orElse(step.field(JavaDsl.field().private_().type(property.getType()).name(propertyName)));

            // render the wither
            step = step.method(JavaDsl.method()
                                      .public_()
                                      .resultType(step)//use the builder type
                                      .name("with" + capitalize(propertyName))
                                      .parameter(JavaDsl.parameter(property.getType(), propertyName))
                                      .body("""
                                            this.%1$s = %1$s;
                                            return this;""".formatted(propertyName)));

            // collect all setter invocations for the object being build
            setterInvocations.add(toBuildVariableName + "." +
                                  property.getSetterOrThrow().getName() +
                                  "(" + propertyName + ");");
         }

         // render the build method
         step = step.method(JavaDsl.method().public_().resultType(toBuild).name("build")
                                   .body("""
                                         %1$s %2$s = new %1$s();
                                         %3$s
                                         return %2$s;
                                         """.formatted(toBuild.getName(),
                                                       toBuildVariableName,
                                                       join("\n\n", setterInvocations))));

         //writes the builder
         context.writeAndCompileSourceFile(step);
      }
   }
}