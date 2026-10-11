package com.derivandi.builder;

import java.util.Objects;

@BuilderPattern
public class Customer
{
   private String name;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @Override
   public boolean equals(Object o)
   {
      return this == o ||
             o instanceof Customer customer &&
             Objects.equals(getName(), customer.getName());
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(getName());
   }

   @Override
   public String toString()
   {
      return "Customer{" +
             "name='" + name + '\'' +
             '}';
   }
}
