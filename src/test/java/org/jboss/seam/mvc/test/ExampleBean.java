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
package org.jboss.seam.mvc.test;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Named
@RequestScoped
public class ExampleBean
{
   private String name = "Lincoln";
   private final Set<ExampleData> collection;

   public ExampleBean()
   {
      collection = new HashSet<ExampleData>();
      collection.add(new ExampleData(1, "Eddie", "Boy"));
      collection.add(new ExampleData(2, "Emily", "Girl"));
      collection.add(new ExampleData(3, "Don", "Boy"));
      collection.add(new ExampleData(4, "Susan", "Girl"));
      collection.add(new ExampleData(5, "Lincolns", "Boy"));
   }

   public String getName()
   {
      return name;
   }

   public void setName(final String firstName)
   {
      this.name = firstName;
   }

   public String submit()
   {
      return null;
   }

   public Set<ExampleData> getCollection()
   {
      return collection;
   }
}
