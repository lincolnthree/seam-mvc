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
package org.jboss.seam.mvc.template;

import java.util.Set;

import javax.servlet.ServletContext;

import org.jboss.seam.render.spi.TemplateResolver;
import org.jboss.seam.render.spi.TemplateResource;
import org.jboss.seam.render.util.Assert;
import org.jboss.seam.render.util.Paths;

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
   public TemplateResource<ServletContext> resolve(final String path)
   {
      Assert.notNull(path, "Resource path must not be null.");
      if (validResource(path))
      {
         return new ServletContextTemplateResource(this, context, path);
      }
      return null;
   }

   @Override
   public TemplateResource<ServletContext> resolveRelative(final TemplateResource<ServletContext> origin,
            final String relativePath)
   {
      Assert.notNull(origin, "Origin resource must not be null.");
      Assert.notNull(relativePath, "Relative resource path must not be null.");
      String path = origin.getPath();
      path = Paths.calculateRelativePath(path, relativePath);

      if (validResource(path))
      {
         return new ServletContextTemplateResource(this, context, path);
      }
      return null;
   }

   private boolean validResource(final String target)
   {
      Set<String> paths = context.getResourcePaths("/");
      if (paths.contains(target))
      {
         return true;
      }
      return false;
   }
}
