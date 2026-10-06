package com.derivandi.structure;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PackageTest
{
   @Test
   void testGetContent()
   {
      processorTest().withCodeToCompile("AnyClass.java", """
                                                         package com.derivandi.example.processed.test.packagee.not_empty;
                                                         
                                                         public class AnyClass {}""")
                     .process(context ->
                              {
                                 assertTrue(context.getPackage("asdkfh").isEmpty());

                                 D.Class anyClass = context.getClassOrThrow(
                                       "com.derivandi.example.processed.test.packagee.not_empty.AnyClass");

                                 D.Package cPackage = context.getPackage("com.derivandi.example.processed.test.packagee.not_empty")
                                                             .get(0);

                                 assertEquals(List.of(anyClass), cPackage.getDeclared());
                              });
   }
}
