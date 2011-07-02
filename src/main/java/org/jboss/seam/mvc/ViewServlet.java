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

import org.jboss.logging.Logger;
import org.jboss.seam.mvc.lifecycle.ApplyValuesPhase;
import org.jboss.seam.mvc.lifecycle.ExecuteActionsPhase;
import org.jboss.seam.mvc.lifecycle.RenderPhase;
import org.jboss.seam.mvc.request.Params;
import org.jboss.seam.mvc.template.ServletContextTemplateResolver;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.spi.TemplateResource;
import org.jboss.seam.render.template.CompiledTemplateResource;
import org.jboss.seam.render.template.resolver.TemplateResolverFactory;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@WebServlet(name = "Seam MVC", urlPatterns = { "*.mvc" }, loadOnStartup = 1, asyncSupported = true)
public class ViewServlet extends HttpServlet
{
   private static final long serialVersionUID = 8641290779641399526L;

   private final Logger log = Logger.getLogger(ViewServlet.class);

   @Inject
   @MVC
   private TemplateCompiler compiler;

   @Inject
   private ApplyValuesPhase applyValuesPhase;

   @Inject
   private ExecuteActionsPhase executeActionsPhase;

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
      log.info("Seam MVC is active, listening for requests on: "
               + config.getServletContext().getServletRegistrations().get(config.getServletName()).getMappings());
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
         resp.sendError(HttpServletResponse.SC_NOT_FOUND);
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
         executeActionsPhase.perform(req, resp, input, parameterMap);

         Map<Object, Object> map = new HashMap<Object, Object>();

         String written = renderPhase.perform(input, map);
         resp.getWriter().write(written);
      }
      else
      {
         resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
   }

   private CompiledTemplateResource getTemplate(final HttpServletRequest req)
   {
      String requestURI = req.getRequestURI();
      if (requestURI.startsWith(req.getContextPath()))
      {
         requestURI = requestURI.substring(req.getContextPath().length());
      }

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
      TemplateResource<?> resource = factory.resolve(requestURI);
      long lastModified = resource.getLastModified();
      if (views.containsKey(requestURI) && !(lastModified > viewTimestamps.get(requestURI)) && (lastModified != 0))
      {
         view = views.get(requestURI);
      }
      else
      {
         view = compiler.compile(resource);
         views.put(requestURI, view);
         viewTimestamps.put(requestURI, resource.getLastModified());
      }
      return view;
   }

}
