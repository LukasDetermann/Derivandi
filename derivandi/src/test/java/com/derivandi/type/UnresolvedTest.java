package com.derivandi.type;

import com.derivandi.api.D;
import com.derivandi.api.dsl.JavaDsl;
import org.junit.jupiter.api.Test;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UnresolvedTest
{
   @Test
   void missingClass()
   {
      processorTest().withCodeToCompile("HasMissingClass.java", "public class HasMissingClass {Missing field;}")
                     .process(context ->
                              {
                                 D.Class hasMissingClass = context.getClassOrThrow("HasMissingClass");
                                 D.Field field = hasMissingClass.getFields().getFirst();
                                 D.VariableType fieldType = field.getType();

                                 if (context.isFirstRound())
                                 {
                                    assertInstanceOf(D.Unresolved.class, fieldType);
                                    D.Unresolved unresolved = (D.Unresolved) fieldType;
                                    assertEquals("Missing", unresolved.getName());

                                    context.writeAndCompileSourceFile(JavaDsl.class_().noPackage().name("Missing"));
                                 }
                                 else
                                 {
                                    assertInstanceOf(D.Class.class, fieldType);
                                 }
                              });
   }
}
