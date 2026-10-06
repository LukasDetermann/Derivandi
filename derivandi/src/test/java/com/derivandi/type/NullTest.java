package com.derivandi.type;

import org.junit.jupiter.api.Test;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NullTest
{
   @Test
   void nonNull()
   {
      processorTest().process(context -> assertNotNull(context.getConstants().getNull()));
   }
}
