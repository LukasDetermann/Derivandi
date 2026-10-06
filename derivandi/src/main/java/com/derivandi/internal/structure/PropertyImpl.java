package com.derivandi.internal.structure;

import com.derivandi.api.D;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class PropertyImpl
      implements D.Property
{
   private final String name;
   private final D.VariableType type;
   private final D.Field field;
   private final D.Method getter;
   private final D.Method setter;

   PropertyImpl(String name,
                D.VariableType type,
                D.Field field,
                D.Method getter,
                D.Method setter)
   {
      this.name = name;
      this.type = type;
      this.field = field;
      this.getter = getter;
      this.setter = setter;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public D.VariableType getType()
   {
      return type;
   }

   @Override
   public Optional<D.Field> getField()
   {
      return ofNullable(field);
   }

   @Override
   public D.Field getFieldOrThrow()
   {
      return getField().orElseThrow();
   }

   @Override
   public D.Method getGetter()
   {
      return getter;
   }

   @Override
   public Optional<D.Method> getSetter()
   {
      return ofNullable(setter);
   }

   @Override
   public D.Method getSetterOrThrow()
   {
      return getSetter().orElseThrow();
   }

   @Override
   public boolean isMutable()
   {
      return getSetter().isPresent();
   }

   @Override
   public boolean equals(Object other)
   {
      return other instanceof D.Property property &&
             Objects.equals(getName(), property.getName()) &&
             Objects.equals(getType(), property.getType());
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getName(), getType());
   }

   @Override
   public String toString()
   {
      return "Property{" +
             "name='" + getName() + '\'' +
             ", type=" + getType() +
             '}';
   }
}
