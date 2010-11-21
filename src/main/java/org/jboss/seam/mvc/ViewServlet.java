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

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.mvc.lifecycle.ApplyValuesPhase;
import org.jboss.seam.mvc.lifecycle.RenderPhase;
import org.jboss.seam.mvc.template.CompiledView;
import org.jboss.seam.mvc.template.ViewCompiler;

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
   private ViewCompiler compiler;
   @Inject
   private ApplyValuesPhase applyValuesPhase;
   @Inject
   private RenderPhase renderPhase;

   private ServletConfig config;

   @Override
   public void init(final ServletConfig config) throws ServletException
   {
      System.out.println("Starting Seam MVC");
      this.config = config;
   }

   @Override
   protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException
   {
      CompiledView input = getTemplate(req);
      if (input != null)
      {
         // OutputStream output = resp.getOutputStream();
         String written = renderPhase.perform(input, req.getParameterMap());
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
      CompiledView input = getTemplate(req);
      if (input != null)
      {
         applyValuesPhase.perform(input, req.getParameterMap());

         String written = renderPhase.perform(input, req.getParameterMap());
         resp.getWriter().write(written);
      }
      else
      {
         PrettyContext.getCurrentInstance().sendError(404);
      }
   }

   private CompiledView getTemplate(final HttpServletRequest req)
   {
      String requestURI = req.getRequestURI();
      requestURI = PrettyContext.getCurrentInstance(req).stripContextPath(requestURI);

      Collection<String> mappings = config.getServletContext().getServletRegistration(config.getServletName())
               .getMappings();
      for (String m : mappings)
      {
         m = m.replaceAll("*", "");
         if (requestURI.startsWith(m))
         {
            requestURI = requestURI.substring(m.length());
         }
      }

      CompiledView view = compiler.compile(requestURI);
      return view;
   }
}
