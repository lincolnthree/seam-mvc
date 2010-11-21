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

import org.jboss.seam.mvc.spi.resolver.TemplateResolver;
import org.jboss.seam.mvc.spi.resolver.TemplateResource;
import org.jboss.seam.mvc.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class FileTemplateResolver implements TemplateResolver<File>
{
   @Override
   public Class<File> getType()
   {
      return File.class;
   }

   @Override
   public TemplateResource<File> resolve(final String path)
   {
      Assert.notNull(path, "Target resource path must not be null.");
      File file = new File(path);
      if (validResource(file))
      {
         return new FileTemplateResource(file, this);
      }
      return null;
   }

   @Override
   public TemplateResource<File> resolveRelative(final TemplateResource<File> origin, String relativePath)
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
      File file = new File(path + relativePath);

      if (validResource(file))
      {
         return new FileTemplateResource(file, this);
      }
      return null;
   }

   private boolean validResource(final File file)
   {
      return file.exists() && file.isFile();
   }
}
