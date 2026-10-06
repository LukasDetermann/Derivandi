package com.derivandi.api.adapter;

import com.derivandi.api.D;
import com.derivandi.internal.structure.RecordComponentImpl;

import javax.lang.model.element.RecordComponentElement;

public class RecordComponentAdapter
{
   private final D.RecordComponent recordComponent;

   RecordComponentAdapter(D.RecordComponent recordComponent)
   {
      this.recordComponent = recordComponent;
   }

   public RecordComponentElement toRecordComponentElement()
   {
      return ((RecordComponentImpl) recordComponent).getElement();
   }
}
