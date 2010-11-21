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

import java.io.InputStream;

import org.jboss.seam.mvc.spi.resolver.TemplateResolver;
import org.jboss.seam.mvc.spi.resolver.TemplateResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ClassLoaderTemplateResource implements TemplateResource<ClassLoader>
{

   private final ClassLoaderTemplateResolver resolver;
   private final ClassLoader loader;
   private final String path;

   /**
    * @param classLoaderTemplateResolver
    * @param resource
    */
   public ClassLoaderTemplateResource(final ClassLoaderTemplateResolver resolver, final ClassLoader loader,
            final String path)
   {
      this.resolver = resolver;
      this.loader = loader;
      this.path = path;
   }

   @Override
   public String getPath()
   {
      return path;
   }

   @Override
   public InputStream getInputStream()
   {
      return loader.getResourceAsStream(path);
   }

   @Override
   public ClassLoader getUnderlyingResource()
   {
      return loader;
   }

   @Override
   public TemplateResolver<ClassLoader> getResolvedBy()
   {
      return resolver;
   }

}
