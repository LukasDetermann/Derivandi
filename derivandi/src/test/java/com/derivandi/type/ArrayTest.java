package com.derivandi.type;

import com.derivandi.api.D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.*;

class ArrayTest
{
   @Test
   void isSubtypeOfTest()
   {
      processorTest().process(context ->
                              {
                                 D.Class string = context.getClassOrThrow("java.lang.String");
                                 D.Array stringArray1 = string.asArray();
                                 D.Array stringArray2 = string.asArray();

                                 assertTrue(stringArray1.isSubtypeOf(stringArray2));

                                 D.Interface collection = context.getInterfaceOrThrow("java.util.Collection");
                                 D.Array collectionArray = collection.asArray();
                                 D.Interface iterable = context.getInterfaceOrThrow("java.lang.Iterable");
                                 D.Array iterableArray = iterable.asArray();
                                 assertFalse(collectionArray.isSubtypeOf(iterableArray));
                              });
   }

   @Test
   void getComponentTypeTest()
   {
      processorTest().process(context ->
                              {
                                 D.Class string = context.getClassOrThrow("java.lang.String");
                                 D.Array stringArray = string.asArray();
                                 assertEquals(string, stringArray.getComponentType());
                              });
   }

   @Test
   void getDirectSuperTypesTest()
   {
      processorTest().process(context ->
                              {
                                 D.Class string = context.getClassOrThrow("java.lang.String");
                                 D.Class object = context.getClassOrThrow("java.lang.Object");
                                 D.Array stringArray = string.asArray();
                                 D.Array objectArray = object.asArray();

                                 List<D.Type> superTypes = stringArray.getDirectSuperTypes();
                                 assertEquals(1, superTypes.size());
                                 assertEquals(objectArray, superTypes.get(0));

                                 D.Primitive cint = context.getConstants().getPrimitiveInt();

                                 D.Array intArray = cint.asArray();
                                 D.Interface serializable = context.getInterfaceOrThrow("java.io.Serializable");
                                 D.Interface cloneable = context.getInterfaceOrThrow("java.lang.Cloneable");

                                 List<D.Type> intArraySuperTypes = intArray.getDirectSuperTypes();
                                 assertEquals(1, intArraySuperTypes.size());
                                 List<D.Type> bounds = ((D.Generic) intArraySuperTypes.get(0)).getBounds();
                                 assertEquals(List.of(serializable, cloneable), bounds);
                              });
   }
}
