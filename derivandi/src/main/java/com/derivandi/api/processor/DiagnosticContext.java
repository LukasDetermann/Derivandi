package com.derivandi.api.processor;

import java.time.Instant;
import java.util.Objects;

public class DiagnosticContext
{
   private final SimpleContext context;
   private final String processorName;
   private final int processingRound;
   private final Instant start;
   private final Instant end;

   DiagnosticContext(SimpleContext context, String processorName, int processingRound, Instant start, Instant end)
   {
      this.context = context;
      this.processorName = processorName;
      this.processingRound = processingRound;
      this.start = start;
      this.end = end;
   }

   public SimpleContext getContext()
   {
      return context;
   }

   public String getProcessorName()
   {
      return processorName;
   }

   public int getProcessingRound()
   {
      return processingRound;
   }

   public Instant getStart()
   {
      return start;
   }

   public Instant getEnd()
   {
      return end;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (!(o instanceof DiagnosticContext context))
      {
         return false;
      }
      return getProcessingRound() == context.getProcessingRound() &&
             Objects.equals(getProcessorName(), context.getProcessorName()) &&
             Objects.equals(getStart(), context.getStart()) &&
             Objects.equals(getEnd(), context.getEnd());
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getProcessorName(), getProcessingRound(), getStart(), getEnd());
   }

   @Override
   public String toString()
   {
      return "DiagnosticContext{" +
             "api=" + context +
             ", processorName='" + processorName + '\'' +
             ", processingRound=" + processingRound +
             ", start=" + start +
             ", now=" + end +
             '}';
   }
}
