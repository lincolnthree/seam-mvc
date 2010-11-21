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
package org.jboss.seam.mvc.template.resolver;

import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.mvc.spi.resolver.TemplateResolver;
import org.jboss.seam.mvc.spi.resolver.TemplateResource;
import org.jboss.seam.mvc.util.Assert;
import org.jboss.weld.extensions.util.service.ServiceLoader;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@SuppressWarnings("rawtypes")
public class TemplateResolverFactory implements TemplateResolver<Object>
{
   ServiceLoader<TemplateResolver> resolvers = null;

   Set<TemplateResolver> addedResolvers = new HashSet<TemplateResolver>();

   public void addResolver(final TemplateResolver resolver)
   {
      addedResolvers.add(resolver);
   }

   @Override
   @SuppressWarnings("unchecked")
   public TemplateResource resolve(final String target)
   {
      Assert.notNull(target, "Target resource must not be null.");
      loadResolvers();
      TemplateResource<?> resource = null;
      for (TemplateResolver<?> resolver : addedResolvers)
      {
         resource = resolver.resolve(target);
      }
      if (resource == null)
      {
         for (TemplateResolver<?> resolver : resolvers)
         {
            resource = resolver.resolve(target);
         }
      }
      return resource;
   }

   @Override
   @SuppressWarnings("unchecked")
   public TemplateResource resolveRelative(final TemplateResource origin, final String relativePath)
   {
      Assert.notNull(origin, "Origin resource must not be null.");
      Assert.notNull(relativePath, "Relative resource path must not be null.");
      TemplateResolver resolver = origin.getResolvedBy();
      TemplateResource result = resolver.resolveRelative(origin, relativePath);
      return result;
   }

   private void loadResolvers()
   {
      if (resolvers == null)
      {
         resolvers = ServiceLoader.load(TemplateResolver.class);
      }
   }

   @Override
   public Class<Object> getType()
   {
      return Object.class;
   }

}