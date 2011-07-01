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
package org.jboss.seam.mvc.test.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.seam.mvc.MVC;
import org.jboss.seam.mvc.lifecycle.RenderPhase;
import org.jboss.seam.mvc.template.BindingContext;
import org.jboss.seam.mvc.test.MVCTest;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.template.CompiledTemplateResource;
import org.jboss.seam.render.template.resolver.ClassLoaderTemplateResolver;
import org.jboss.seam.render.template.resolver.TemplateResolverFactory;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RenderPhaseTest extends MVCTest
{
   @Inject
   private RenderPhase render;

   @Inject
   private BindingContext bindings;

   @Inject
   @MVC
   private TemplateCompiler compiler;

   @Inject
   protected void init(final TemplateResolverFactory factory)
   {
      compiler.getTemplateResolverFactory().addResolver(
               new ClassLoaderTemplateResolver(this.getClass().getClassLoader()));
   }

   @Test
   public void testRenderTemplate() throws Exception
   {
      Map<Object, Object> context = new HashMap<Object, Object>();
      context.put("name", "lincoln");

      CompiledTemplateResource view = compiler.compile("org/jboss/seam/mvc/views/hello.xhtml");
      String output = render.perform(view, context);

      System.out.println(output);
      assertEquals("exampleBean.name", bindings.get("name"));
      assertTrue(output.contains("Hi lincoln,"));
      assertTrue(output.contains("value=\"Lincoln\""));
      assertTrue(output.contains("name=\"name\""));
      assertTrue(output.contains("selected=\"selected\""));
   }

}
