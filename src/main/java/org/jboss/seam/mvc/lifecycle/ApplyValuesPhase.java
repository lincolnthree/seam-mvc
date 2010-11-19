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

import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.inject.Inject;

import org.jboss.seam.mvc.template.Bindings;
import org.jboss.seam.mvc.template.CompiledView;
import org.jboss.seam.mvc.template.ViewCompiler;
import org.jboss.weld.extensions.el.Expressions;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ApplyValuesPhase implements Phase
{
   @Inject
   private ViewCompiler compiler;

   @Inject
   private Bindings bindings;

   @Inject
   private Expressions expressions;

   public void perform(final InputStream input, final Map<String, String[]> parameterMap)
   {
      CompiledView view = compiler.compile(input);
      view.render(parameterMap);
      for (Entry<String, String> entry : bindings.entrySet())
      {
         String param = entry.getKey();
         String el = entry.getValue();
         String[] values = parameterMap.get(param);
         String inject = null;
         if ((values != null) && (values.length > 0))
         {
            inject = values[0];
         }

         // TODO validation and conversion

         if (el != null)
         {
            el = expressions.toExpression(el);
            ValueExpression ve = expressions.getExpressionFactory().createValueExpression(el, Object.class);
            ELContext context = expressions.getELContext();
            Class<?> type = ve.getType(context);

            Object coercedValue = expressions.getExpressionFactory().coerceToType(inject, type);
            ve = expressions.getExpressionFactory().createValueExpression(context, el, type);
            ve.setValue(expressions.getELContext(), coercedValue);
         }
      }
   }
}
