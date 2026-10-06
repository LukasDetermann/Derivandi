package com.derivandi.article.intro.generation;

import java.util.Map;

@Proxy
public abstract class MapProxy<K, V>
      implements Map<K, V>
{
   @Delegate
   protected final Map<K, V> delegate;
   private int counter = 0;

   public MapProxy(Map<K, V> delegate)
   {
      this.delegate = delegate;
   }

   @Override
   public V put(K key, V value)
   {
      counter++;

      return delegate.put(key, value);
   }
}
