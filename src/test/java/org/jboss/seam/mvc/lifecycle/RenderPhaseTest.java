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
package org.jboss.seam.mvc.lifecycle;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mvc.Root;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@RunWith(Arquillian.class)
public class RenderPhaseTest
{
   @Deployment
   public static JavaArchive createTestArchive()
   {
      JavaArchive deployment = ShrinkWrap.create(JavaArchive.class, "test.jar")
               .addPackages(true, Root.class.getPackage())
               .addManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"));
      return deployment;
   }

   @Inject
   private RenderPhase render;

   @Test
   public void testRenderTemplate() throws Exception
   {
      Map<String, Object> context = new HashMap<String, Object>();
      context.put("world", "lincoln");

      InputStream stream = Thread.currentThread().getContextClassLoader()
               .getResourceAsStream("org/jboss/seam/mvc/views/hello.xhtml");
      String output = render.perform(stream, context);

      assertEquals("\n\nHi, this is the world of lincoln.", output);
   }

   @Test
   public void testRenderSimpleViewForm() throws Exception
   {
      Map<String, Object> context = new HashMap<String, Object>();
      context.put("world", "lincoln");

      String output = render.perform(
               Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("org/jboss/seam/mvc/views/simple-form.view"), context);

      System.out.println(output);
   }
}
