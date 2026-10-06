package com.derivandi.internal.annotationvalue;

import com.derivandi.api.D;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.annotation_value.AnnotationValueRenderable;
import com.derivandi.api.processor.SimpleContext;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.AnnotationValue;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.derivandi.api.dsl.JavaDsl.annotationValue;

public abstract class AnnotationValueImpl<R>
{
   protected final SimpleContext context;
   private final AnnotationValue annotationValue;
   private final @Nullable R value;
   private final AnnotationValue defaultAnnotationValue;
   private final R valueOrDefault;

   public static class StringValueImpl
         extends AnnotationValueImpl<String>
         implements D.AnnotationValue.StringValue
   {
      public StringValueImpl(SimpleContext context,
                             AnnotationValue annotationValue,
                             @Nullable String value,
                             AnnotationValue defaultAnnotationValue,
                             String valueOrDefault)
      {
         super(context, annotationValue, value, defaultAnnotationValue, valueOrDefault);
      }
   }

   public static class BooleanValueImpl
         extends AnnotationValueImpl<Boolean>
         implements D.AnnotationValue.BooleanValue
   {
      public BooleanValueImpl(SimpleContext context,
                              AnnotationValue annotationValue,
                              @Nullable Boolean value,
                              AnnotationValue defaultAnnotationValue,
                              Boolean valueOrDefault)
      {
         super(context, annotationValue, value, defaultAnnotationValue, valueOrDefault);
      }
   }

   public static class ByteValueImpl
         extends AnnotationValueImpl<Byte>
         implements D.AnnotationValue.ByteValue
   {
      public ByteValueImpl(SimpleContext context,
                           AnnotationValue annotationValue,
                           @Nullable Byte value,
                           AnnotationValue defaultAnnotationValue,
                           Byte valueOrDefault)
      {
         super(context, annotationValue, value, defaultAnnotationValue, valueOrDefault);
      }
   }

   public static class ShortValueImpl
         extends AnnotationValueImpl<Short>
         implements D.AnnotationValue.ShortValue
   {
      public ShortValueImpl(SimpleContext context,
                            AnnotationValue annotationValue,
                            @Nullable Short value,
                            AnnotationValue defaultAnnotationValue,
                            Short valueOrDefault)
      {
         super(context, annotationValue, value, defaultAnnotationValue, valueOrDefault);
      }
   }

   public static class IntegerValueImpl
         extends AnnotationValueImpl<Integer>
         implements D.AnnotationValue.IntegerValue
   {
      public IntegerValueImpl(SimpleContext context,
                              AnnotationValue annotationValue,
                              @Nullable Integer value,
                              AnnotationValue defaultAnnotationValue,
                              Integer valueOrDefault)
      {
         super(context, annotationValue, value, defaultAnnotationValue, valueOrDefault);
      }
   }

   public static class LongValueImpl
         extends AnnotationValueImpl<Long>
         implements D.AnnotationValue.LongValue
   {
      public LongValueImpl(SimpleContext context,
                           AnnotationValue annotationValue,
                           @Nullable Long value,
                           AnnotationValue defaultAnnotationValue,
                           Long valueOrDefault)
      {
         super(context, annotationValue, value, defaultAnnotationValue, valueOrDefault);
      }
   }

   public static class CharacterValueImpl
         extends AnnotationValueImpl<Character>
         implements D.AnnotationValue.CharacterValue
   {
      public CharacterValueImpl(SimpleContext context,
                                AnnotationValue annotationValue,
                                @Nullable Character value,
                                AnnotationValue defaultAnnotationValue,
                                Character valueOrDefault)
      {
         super(context, annotationValue, value, defaultAnnotationValue, valueOrDefault);
      }
   }

   public static class FloatValueImpl
         extends AnnotationValueImpl<Float>
         implements D.AnnotationValue.FloatValue
   {
      public FloatValueImpl(SimpleContext context,
                            AnnotationValue annotationValue,
                            @Nullable Float value,
                            AnnotationValue defaultAnnotationValue,
                            Float valueOrDefault)
      {
         super(context, annotationValue, value, defaultAnnotationValue, valueOrDefault);
      }
   }

   public static class DoubleValueImpl
         extends AnnotationValueImpl<Double>
         implements D.AnnotationValue.DoubleValue
   {
      public DoubleValueImpl(SimpleContext context,
                             AnnotationValue annotationValue,
                             @Nullable Double value,
                             AnnotationValue defaultAnnotationValue,
                             Double valueOrDefault)
      {
         super(context, annotationValue, value, defaultAnnotationValue, valueOrDefault);
      }
   }

   public static class TypeValueImpl
         extends AnnotationValueImpl<D.Type>
         implements D.AnnotationValue.TypeValue
   {
      public TypeValueImpl(SimpleContext context,
                           AnnotationValue annotationValue,
                           @Nullable D.Type value,
                           AnnotationValue defaultAnnotationValue,
                           D.Type valueOrDefault)
      {
         super(context, annotationValue, value, defaultAnnotationValue, valueOrDefault);
      }
   }

   public static class EnumValueImpl
         extends AnnotationValueImpl<D.EnumConstant>
         implements D.AnnotationValue.EnumValue
   {
      public EnumValueImpl(SimpleContext context,
                           AnnotationValue annotationValue,
                           @Nullable D.EnumConstant value,
                           AnnotationValue defaultAnnotationValue,
                           D.EnumConstant valueOrDefault)
      {
         super(context,
               annotationValue,
               value,
               defaultAnnotationValue,
               valueOrDefault);
      }
   }

   public static class AnnotationUsageValueImpl
         extends AnnotationValueImpl<D.AnnotationUsage>
         implements D.AnnotationValue.AnnotationUsageValue
   {
      public AnnotationUsageValueImpl(SimpleContext context,
                                      AnnotationValue annotationValue,
                                      @Nullable D.AnnotationUsage value,
                                      AnnotationValue defaultAnnotationValue,
                                      D.AnnotationUsage valueOrDefault)
      {
         super(context, annotationValue, value, defaultAnnotationValue, valueOrDefault);
      }
   }

   public static class ValuesImpl<T extends D.NestedAnnotationValue>
         extends AnnotationValueImpl<List<T>>
         implements D.AnnotationValue.Values<T>
   {
      public ValuesImpl(SimpleContext context,
                        AnnotationValue annotationValue,
                        @Nullable List<T> value,
                        AnnotationValue defaultAnnotationValue,
                        List<T> valueOrDefault)
      {
         super(context, annotationValue, value, defaultAnnotationValue, valueOrDefault);
      }
   }

   private AnnotationValueImpl(SimpleContext context,
                               AnnotationValue annotationValue,
                               @Nullable R value,
                               AnnotationValue defaultAnnotationValue,
                               R valueOrDefault)
   {
      this.context = context;
      this.annotationValue = annotationValue;
      this.value = value;
      this.defaultAnnotationValue = defaultAnnotationValue;
      this.valueOrDefault = valueOrDefault;
   }

   public boolean isDefault()
   {
      return value == null;
   }

   public Optional<AnnotationValue> getAnnotationValue()
   {
      return Optional.ofNullable(annotationValue);
   }

   public AnnotationValue getDefaultAnnotationValue()
   {
      return defaultAnnotationValue;
   }

   public R getValue()
   {
      return value == null ? valueOrDefault : value;
   }

   public R getDefaultValue()
   {
      return valueOrDefault;
   }

   public String render(RenderingContext renderingContext)
   {
      Object object = getValue();

      return (switch (object)
              {
                 case Character c -> annotationValue(c);
                 case String s -> annotationValue(s);
                 case Boolean b -> annotationValue(b);
                 case Byte b -> annotationValue(b);
                 case Short s -> annotationValue(s);
                 case Integer i -> annotationValue(i);
                 case Long l -> annotationValue(l);
                 case Float f -> annotationValue(f);
                 case D.EnumConstant e -> annotationValue(e);
                 case D.Type t -> annotationValue(t);
                 case D.AnnotationUsage a -> annotationValue(a);
                 case List<?> l ->
                    //noinspection unchecked
                       annotationValue((List<AnnotationValueRenderable>) l);

                 default -> throw new IllegalStateException("Unexpected value: " + object);
              }).render(renderingContext);
   }

   @Override
   public boolean equals(Object other)
   {
      return other instanceof D.AnnotationValue annotationValue1 &&
             Objects.equals(isDefault(), annotationValue1.isDefault()) &&
             Objects.equals(getValue(), annotationValue1.getValue());
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(isDefault(), getValue());
   }

   @Override
   public String toString()
   {
      return "AnnotationValue{" +
             "default=" + isDefault() +
             ", value=" + getValue() +
             ", defaultValue=" + getDefaultValue() +
             '}';
   }
}
