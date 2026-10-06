package com.derivandi.article.intro.validation;

//@formatter:off
//tag::content[]
import com.derivandi.api.D;
import com.derivandi.api.processor.Processor;
import com.derivandi.api.processor.ProcessorBuilder;
import com.derivandi.api.processor.ProcessorConfiguration;
import com.derivandi.api.processor.SimpleContext;

import java.util.List;
import java.util.function.Predicate;

public class ValidationProcessor extends Processor {

   @Override
   public ProcessorConfiguration buildProcessor(ProcessorBuilder processorBuilder) {
      return processorBuilder.process(ValidationProcessor::process);
   }

   private static void process(SimpleContext context) {

      D.Annotation staticInterface = context.getAnnotationOrThrow(StaticInterface.class.getName());

      for (D.Declared annotated : context.getDeclaredAnnotatedWith(staticInterface)) {

         D.AnnotationUsage staticInterfaceUsage = annotated.getUsageOfOrThrow(staticInterface);
         D.AnnotationValue value = staticInterfaceUsage.getValueOrThrow("value");
         D.Type usedType = ((D.AnnotationValue.TypeValue) value).getValue();

         if (!(usedType instanceof D.Interface interfaceToImplement)) {
            context.logAndRaiseErrorAt(annotated, "Not an Interface");
            continue;
         }

         for (D.Method mandatoryMethod : interfaceToImplement.getMethods()) {
            if (mandatoryMethod.isStatic()) {
               continue;
            }
            if (doesNotOverwrite(annotated.getMethods(), mandatoryMethod)) {

               String msg = "Does not staticly overwrite: " + mandatoryMethod.getName();
               context.logAndRaiseErrorAt(annotated, msg);
            }
         }
      }
   }

   private static boolean doesNotOverwrite(List<D.Method> methods, D.Method mandatory) {
      return methods.stream()
                    .filter(D.StaticModifiable::isStatic)
                    .filter(Predicate.not(D.AccessModifiable::isPrivate))
                    .filter(method -> method.getName().equals(mandatory.getName()))
                    .noneMatch(method -> method.getParameterTypes().equals(mandatory.getParameterTypes()));
   }
}
//end::content[]
//@formatter:on