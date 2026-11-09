package com.derivandi.javadoc;

import com.derivandi.api.D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.derivandi.api.test.ProcessorTest.processorTest;

public class InterpolateGenericsTest
{
   @Test
   void interpolateGenerics()
   {
      processorTest().withCodeToCompile("MyClass.java",
                                        //@start region="InterpolateGenerics.interpolateGenerics.code"
                                        "public class MyClass<A extends Comparable<B>, B extends Comparable<A>> {}"
                                        //@end
                                       )
                     .process(context ->
                              {
                                 //@start region="InterpolateGenerics.interpolateGenerics"
                                 D.Class myClass = context.getClassOrThrow("MyClass");
                                 D.Declared string = context.getDeclaredOrThrow("java.lang.String");

                                 D.Class withGenerics = myClass.withGenerics(string,
                                                                             //the unboundWildcard will be replaced with the result
                                                                             context.getConstants().getUnboundWildcard());

                                 D.Class capture = withGenerics.capture();

                                 D.Type stringRep = Optional.of(capture.getGenericUsages().get(1))
                                                            .map(D.Generic.class::cast)
                                                            .map(D.Generic::getBound)
                                                            .map(D.Interface.class::cast)
                                                            .map(D.Interface::getGenericUsages)
                                                            .map(types -> types.get(0))
                                                            .orElseThrow();

                                 Assertions.assertEquals(string, stringRep);
                                 //@end
                              })
                     .run();
   }
}
