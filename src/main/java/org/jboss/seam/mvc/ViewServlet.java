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
package org.jboss.seam.mvc;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.mvc.lifecycle.ApplyValuesPhase;
import org.jboss.seam.mvc.lifecycle.RenderPhase;
import org.jboss.seam.mvc.request.Params;
import org.jboss.seam.mvc.template.BindingNode;
import org.jboss.seam.mvc.template.ServletContextTemplateResolver;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.spi.TemplateResource;
import org.jboss.seam.render.template.CompiledTemplateResource;
import org.jboss.seam.render.template.resolver.TemplateResolverFactory;
import org.mvel2.templates.res.Node;

import com.ocpsoft.pretty.PrettyContext;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@WebServlet(name = "Seam MVC", urlPatterns = { "/mvc/*" }, loadOnStartup = 1, asyncSupported = true)
public class ViewServlet extends HttpServlet
{
   private static final long serialVersionUID = 8641290779641399526L;

   @Inject
   @MVC
   private TemplateCompiler compiler;

   @Inject
   private ApplyValuesPhase applyValuesPhase;

   @Inject
   private RenderPhase renderPhase;

   @Inject
   private TemplateResolverFactory factory;

   @Inject
   private Params params;

   private ServletConfig config;

   private final Map<String, CompiledTemplateResource> views = Collections
            .synchronizedMap(new HashMap<String, CompiledTemplateResource>());

   private final Map<String, Long> viewTimestamps = Collections
            .synchronizedMap(new HashMap<String, Long>());

   @Override
   public void init(final ServletConfig config) throws ServletException
   {
      System.out.println("Starting Seam MVC");
      this.config = config;
      factory.addResolver(new ServletContextTemplateResolver(config.getServletContext()));
   }

   @Override
   protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException
   {
      CompiledTemplateResource input = getTemplate(req);
      if (input != null)
      {
         // OutputStream output = resp.getOutputStream();
         Map<String, String[]> parameterMap = req.getParameterMap();
         params.putAll(parameterMap);

         Map<Object, Object> map = new HashMap<Object, Object>();
         String written = renderPhase.perform(input, map);
         resp.getWriter().write(written);
         // System.out.println(written);
         // output.flush();
         // output.close();
      }
      else
      {
         PrettyContext.getCurrentInstance().sendError(404);
      }
   }

   @Override
   protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException
   {
      CompiledTemplateResource input = getTemplate(req);
      if (input != null)
      {
         Map<String, String[]> parameterMap = req.getParameterMap();
         params.putAll(parameterMap);

         applyValuesPhase.perform(input, parameterMap);
         Map<Object, Object> map = new HashMap<Object, Object>();

         String written = renderPhase.perform(input, map);
         resp.getWriter().write(written);
      }
      else
      {
         PrettyContext.getCurrentInstance().sendError(404);
      }
   }

   private CompiledTemplateResource getTemplate(final HttpServletRequest req)
   {
      String requestURI = req.getRequestURI();
      requestURI = PrettyContext.getCurrentInstance(req).stripContextPath(requestURI);

      Collection<String> mappings = config.getServletContext().getServletRegistration(config.getServletName())
               .getMappings();
      for (String m : mappings)
      {
         m = m.replaceAll("\\*", "");
         if (requestURI.startsWith(m))
         {
            requestURI = "/" + requestURI.substring(m.length());
         }
      }

      CompiledTemplateResource view = null;
      synchronized (views)
      {
         TemplateResource<?> resource = factory.resolve(requestURI);
         long lastModified = resource.getLastModified();
         if (views.containsKey(requestURI) && !(lastModified > viewTimestamps.get(requestURI)) && (lastModified != 0))
         {
            view = views.get(requestURI);
         }
         else
         {
            view = compiler.compile(requestURI, getNodes());
            views.put(requestURI, view);
            viewTimestamps.put(requestURI, resource.getLastModified());
         }
      }
      return view;
   }

   private Map<String, Class<? extends Node>> getNodes()
   {
      Map<String, Class<? extends Node>> nodes = new HashMap<String, Class<? extends Node>>();

      nodes.put("bind", BindingNode.class);
      return nodes;
   }
}
