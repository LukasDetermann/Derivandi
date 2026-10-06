package com.derivandi.structure;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordComponentTest
{
   @Test
   void isSubtype()
   {
      processorTest().withCodeToCompile("RecordComponentExample.java", "public record RecordComponentExample(Long id){}")
                     .process(context ->
                              {
                                 D.Class number = context.getClassOrThrow("java.lang.Number");
                                 D.Record example = context.getRecordOrThrow("RecordComponentExample");
                                 D.RecordComponent id = example.getRecordComponentOrThrow("id");
                                 assertTrue(id.isSubtypeOf(number));
                              });
   }

   @Test
   void isAssignableFrom()
   {
      processorTest().withCodeToCompile("RecordComponentExample.java", "public record RecordComponentExample(Long id){}")
                     .process(context ->
                              {
                                 D.Record example = context.getRecordOrThrow("RecordComponentExample");
                                 D.RecordComponent id = example.getRecordComponentOrThrow("id");
                                 assertTrue(id.isAssignableFrom(context.getConstants().getPrimitiveLong()));
                              });
   }

   @Test
   void getRecord()
   {
      processorTest().withCodeToCompile("RecordComponentExample.java", "public record RecordComponentExample(Long id){}")
                     .process(context ->
                              {
                                 D.Record example = context.getRecordOrThrow("RecordComponentExample");
                                 D.RecordComponent id = example.getRecordComponentOrThrow("id");
                                 assertEquals(example, id.getRecord());
                              });
   }

   @Test
   void getType()
   {
      processorTest().withCodeToCompile("RecordComponentExample.java", "public record RecordComponentExample(Long id){}")
                     .process(context ->
                              {
                                 D.Class cLong = context.getClassOrThrow("java.lang.Long");

                                 D.Record example = context.getRecordOrThrow("RecordComponentExample");
                                 D.RecordComponent id = example.getRecordComponentOrThrow("id");
                                 assertEquals(cLong, id.getType());
                              });
   }
}
