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

import java.io.File;
import java.util.Set;

import javax.servlet.ServletContext;

import org.jboss.seam.mvc.spi.resolver.TemplateResolver;
import org.jboss.seam.mvc.spi.resolver.TemplateResource;
import org.jboss.seam.mvc.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ServletContextTemplateResolver implements TemplateResolver<ServletContext>
{
   private final ServletContext context;

   public ServletContextTemplateResolver(final ServletContext context)
   {
      this.context = context;
   }

   @Override
   public Class<ServletContext> getType()
   {
      return ServletContext.class;
   }

   @Override
   public TemplateResource<ServletContext> resolve(final String target)
   {
      if (validResource(target))
      {
         return new ServletContextTemplateResource(this, context, target);
      }
      return null;
   }

   @Override
   public TemplateResource<ServletContext> resolveRelative(final TemplateResource<ServletContext> origin,
            String relativePath)
   {
      Assert.notNull(origin, "Origin resource must not be null.");
      Assert.notNull(relativePath, "Relative resource path must not be null.");
      relativePath = relativePath.trim();

      while (relativePath.startsWith("."))
      {
         if (relativePath.startsWith(".." + File.separator))
         {
            relativePath.substring(3);
         }
         if (relativePath.startsWith("." + File.separator))
         {
            relativePath.substring(2);
         }
      }

      String path = origin.getPath();
      if (path.endsWith(File.separator) && relativePath.startsWith(File.separator))
      {
         relativePath = relativePath.substring(1);
      }
      path = path + relativePath;

      if (validResource(path))
      {
         return new ServletContextTemplateResource(this, context, path);
      }
      return null;
   }

   private boolean validResource(final String target)
   {
      Set<String> paths = context.getResourcePaths(target);
      if (paths.contains(target))
      {
         return true;
      }
      return false;
   }
}
