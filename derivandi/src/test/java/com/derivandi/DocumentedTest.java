package com.derivandi;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentedTest
{
   @Test
   void getJavaDoc()
   {
      processorTest().withCodeToCompile("JavaDocExample.java", """
                                                               /**
                                                                * Class level doc
                                                                */
                                                               public class JavaDocExample<T>
                                                               {
                                                                  /**
                                                                   * Field level doc
                                                                   */
                                                                  private Long id;
                                                               
                                                                  /**
                                                                   * Constructor level doc
                                                                   */
                                                                  public JavaDocExample() {}
                                                               
                                                                  /**
                                                                   * Method level doc
                                                                   */
                                                                  @Override
                                                                  public String toString()
                                                                  {
                                                                     return "JavaDocExample{}";
                                                                  }
                                                               }
                                                               """)
                     .process(context ->
                              {
                                 assertTrue(context.getInterfaceOrThrow("java.util.Collection").getJavaDoc().isEmpty());

                                 D.Class aClass = context.getClassOrThrow("JavaDocExample");
                                 assertEquals(" Class level doc\n", aClass.getJavaDoc().orElseThrow());
                                 assertEquals(" Method level doc\n", aClass.getMethods("toString").get(0).getJavaDoc().orElseThrow());
                                 assertEquals(" Constructor level doc\n", aClass.getConstructors().get(0).getJavaDoc().orElseThrow());
                              })
                     .run();
   }
}
