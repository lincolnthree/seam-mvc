/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.mvc.request;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Named
@RequestScoped
public class Params implements Map<String, Object>
{
   private static final long serialVersionUID = 4126272518954264139L;

   private final Map<String, Object> map = new HashMap<String, Object>();

   @Override
   public void clear()
   {
      map.clear();
   }

   @Override
   public boolean containsKey(final Object key)
   {
      return map.containsKey(key);
   }

   @Override
   public boolean containsValue(final Object key)
   {
      return map.containsValue(key);
   }

   @Override
   public Set<java.util.Map.Entry<String, Object>> entrySet()
   {
      return map.entrySet();
   }

   @Override
   public Object get(final Object key)
   {
      Object result = map.get(key);
      if (result == null)
      {
         result = "";
      }
      else if (result.getClass().isArray())
      {
         if (((Object[]) result).length > 0)
         {
            result = ((Object[]) result)[0];
         }
      }
      return result;
   }

   @Override
   public boolean isEmpty()
   {
      return map.isEmpty();
   }

   @Override
   public Set<String> keySet()
   {
      return map.keySet();
   }

   @Override
   public Object put(final String key, final Object value)
   {
      return map.put(key, value);
   }

   @Override
   public void putAll(final Map<? extends String, ? extends Object> values)
   {
      map.putAll(values);
   }

   @Override
   public Object remove(final Object key)
   {
      return map.remove(key);
   }

   @Override
   public int size()
   {
      return map.size();
   }

   @Override
   public Collection<Object> values()
   {
      return map.values();
   }
}
