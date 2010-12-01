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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.jboss.seam.mvc.MVC;
import org.jboss.seam.mvc.spi.NavigationProvider;
import org.jboss.seam.mvc.template.ActionContext;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.template.CompiledView;
import org.jboss.weld.extensions.el.Expressions;
import org.jboss.weld.extensions.util.service.ServiceLoader;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ExecuteActionsPhase implements Phase
{
   @Inject
   @MVC
   private TemplateCompiler compiler;

   @Inject
   private ActionContext actions;

   @Inject
   private Expressions expressions;

   public void perform(final CompiledView view, final Map<String, String[]> parameterMap)
   {
      Map<Object, Object> map = new HashMap<Object, Object>();
      map.putAll(parameterMap);

      view.render(map);
      for (Entry<String, String> entry : actions.entrySet())
      {
         String param = entry.getKey();
         String method = entry.getValue();

         // TODO validation and conversion

         if (method != null)
         {
            method = expressions.toExpression(method);
            Object result = expressions.evaluateMethodExpression(method);
            if (result != null)
            {
               ServiceLoader<NavigationProvider> loader = ServiceLoader.load(NavigationProvider.class);
               for (NavigationProvider provider : loader)
               {
                  if (provider.navigate(result))
                  {
                     break;
                  }
               }
            }
         }
      }
   }
}
