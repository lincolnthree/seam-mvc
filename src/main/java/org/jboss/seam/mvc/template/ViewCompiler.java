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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.seam.mvc.spi.resolver.TemplateResource;
import org.jboss.seam.mvc.template.nodes.BindingNode;
import org.jboss.seam.mvc.template.nodes.ComposeNode;
import org.jboss.seam.mvc.template.nodes.DefineNode;
import org.jboss.seam.mvc.template.resolver.TemplateResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.res.Node;
import org.mvel2.util.MethodStub;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ViewCompiler
{
   private final ELVariableResolverFactory factory;
   private TemplateRegistry registry;
   private final MapVariableResolverFactory functions;
   private final TemplateResolverFactory resolverFactory;

   @PostConstruct
   public void init()
   {
      try
      {
         // inject the method by wrapping it in a MethodStub -- this is a marking wrapper that tells
         // mvel internally to treat this is a function pointer.
         functions.createVariable("time", new MethodStub(System.class.getMethod("bind", this.getClass())));
      }
      catch (NoSuchMethodException e)
      {
         // handle exception here.
      }

      registry = new SimpleTemplateRegistry();
      registry.addNamedTemplate("forms",
                TemplateCompiler.compileTemplate("@code{def bind(loc) {System.out.println(loc);}}"));
   }

   @Inject
   public ViewCompiler(final ELVariableResolverFactory factory,
            final TemplateResolverFactory resolverFactory)
   {
      this.factory = factory;
      this.resolverFactory = resolverFactory;

      // create a map resolve to hold the functions we want to inject, and chain
      // the ELVariableResolverFactory to this factory.
      functions = new MapVariableResolverFactory(new HashMap<String, Object>(), factory);
   }

   public CompiledView compile(final String path)
   {
      Map<String, Class<? extends Node>> nodes = new HashMap<String, Class<? extends Node>>();

      nodes.put("bind", BindingNode.class);
      nodes.put("define", DefineNode.class);
      nodes.put("compose", ComposeNode.class);

      TemplateResource<?> resource = resolverFactory.resolve(path);

      CompiledView view = new CompiledView(factory, registry, resolverFactory, resource, nodes);
      return view;
   }
}
