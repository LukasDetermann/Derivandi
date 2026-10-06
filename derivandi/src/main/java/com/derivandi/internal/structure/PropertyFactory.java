package com.derivandi.internal.structure;

import com.derivandi.api.D;
import com.derivandi.api.Modifier;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.derivandi.internal.structure.PropertyFactory.AccessorType.GETTER;
import static com.derivandi.internal.structure.PropertyFactory.AccessorType.SETTER;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.util.stream.Collectors.*;

public class PropertyFactory
{
   private record Accessor(D.Method method,
                           AccessorType type,
                           String prefix,
                           String name,
                           int position) {}

   enum AccessorType
   {
      GETTER,
      SETTER,
   }

   private static final String GET_PREFIX = "get";
   private static final String SET_PREFIX = "set";
   private static final String IS_PREFIX = "is";

   private PropertyFactory() {}

   public static List<D.Property> of(D.Declared declared)
   {
      Map<String, D.Field> nameField = declared.getFields().stream()
                                               .filter(field -> !field.hasModifier(Modifier.STATIC))
                                               .collect(toMap(D.Nameable::getName, Function.identity()));

      //we should keep the ordering
      AtomicInteger position = new AtomicInteger();
      Map<String, Map<AccessorType, List<Accessor>>> nameTypeAccessors =
            getMethods(declared).stream()
                                .filter(method -> !method.hasModifier(Modifier.STATIC))
                                .map(method1 -> toAccessor(method1, position.getAndIncrement()))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(groupingBy(Accessor::name,
                                                    groupingBy(Accessor::type)));

      return nameTypeAccessors.entrySet().stream()
                              .filter(entry -> entry.getValue().containsKey(GETTER))
                              .map(entry ->
                                   {
                                      Accessor getter = findGetter(entry.getValue());
                                      String name = entry.getKey();
                                      D.VariableType type = ((D.VariableType) getter.method().getResultType());

                                      D.Method setter = findSetter(entry.getValue(), type).orElse(null);
                                      D.Field field = findField(nameField, type, name).orElse(null);

                                      PropertyImpl template = new PropertyImpl(name, type, field, getter.method(), setter);

                                      return new AbstractMap.SimpleEntry<>(getter.position(), template);
                                   })
                              .sorted((Map.Entry.comparingByKey()))
                              .map(Map.Entry::getValue)
                              .map(D.Property.class::cast)
                              .toList();
   }

   private static List<? extends D.Method> getMethods(D.Declared declared)
   {
      if (!(declared instanceof D.Class aClass))
      {
         return declared.getMethods();
      }
      List<D.Class> superClasses = Stream.iterate((aClass),
                                                  Objects::nonNull,
                                                  D.Class::getSuperClass).collect(toList());

      Collections.reverse(superClasses);

      List<? extends D.Method> methods = superClasses.stream()
                                                     .map(D.Declared::getMethods)
                                                     .flatMap(Collection::stream)
                                                     .toList();

      return methods.stream()
                    .filter(method -> methods.stream().noneMatch(method::overwrittenBy))
                    .toList();
   }

   private static Optional<D.Field> findField(Map<String, D.Field> nameField, D.Type type, String name)
   {
      D.Field field = nameField.get(name);
      if (field == null || !field.getType().isSameType(type))
      {
         return Optional.empty();
      }
      return Optional.of(field);
   }

   private static Optional<D.Method> findSetter(Map<AccessorType, List<Accessor>> typeAccessors, D.Type type)
   {
      List<Accessor> setters = typeAccessors.get(SETTER);
      if (setters == null ||
          setters.size() != 1 ||
          !setters.get(0).method().getParameters().get(0).getType().isSameType(type))
      {
         return Optional.empty();
      }
      return Optional.of(setters.get(0).method());
   }

   private static Accessor findGetter(Map<AccessorType, List<Accessor>> typeAccessors)
   {
      List<Accessor> getters = typeAccessors.get(GETTER);

      if (getters == null || getters.size() > 2)
      {
         throw new IllegalStateException();
      }
      if (getters.size() == 1)
      {
         return getters.get(0);
      }
      if (getters.size() == 2)
      {
         for (Accessor accessor : getters)
         {
            //prefer is getter over get getter
            if (accessor.prefix().equals(IS_PREFIX))
            {
               return accessor;
            }
         }
      }
      throw new IllegalStateException();
   }

   private static Optional<Accessor> toAccessor(D.Method method, int position)
   {
      String name = method.getName();
      List<? extends D.Parameter> parameters = method.getParameters();
      D.Type returnType = method.getResultType();

      //getter
      if (!(returnType instanceof D.Void))
      {
         boolean hasGetPrefix = name.startsWith(GET_PREFIX) && name.length() > 3;
         boolean hasIsPrefix = (returnType instanceof D.boolean_ ||
                                returnType instanceof D.Declared declared &&
                                "java.lang.Boolean".equals(declared.getQualifiedName())) &&
                               name.startsWith(IS_PREFIX) &&
                               name.length() > 2;

         if (parameters.isEmpty())
         {
            if (hasGetPrefix)
            {
               return Optional.of(new Accessor(method, GETTER, GET_PREFIX, toPropertyName(method, GET_PREFIX), position));
            }
            if (hasIsPrefix)
            {
               return Optional.of(new Accessor(method, GETTER, IS_PREFIX, toPropertyName(method, IS_PREFIX), position));
            }
         }
         return Optional.empty();
      }
      //setter
      boolean couldBeSetter = name.startsWith(SET_PREFIX) && name.length() > 3;

      if (couldBeSetter && parameters.size() == 1)
      {
         return Optional.of(new Accessor(method, SETTER, SET_PREFIX, toPropertyName(method, SET_PREFIX), position));
      }
      return Optional.empty();
   }

   private static String toPropertyName(D.Method method, String prefix)
   {
      String name = method.getName().substring(prefix.length());

      //java beans 8.8
      if (name.length() > 1 && isUpperCase(name.charAt(0)) && isUpperCase(name.charAt(1)))
      {
         return name;
      }
      return toLowerCase(name.charAt(0)) + name.substring(1);
   }
}