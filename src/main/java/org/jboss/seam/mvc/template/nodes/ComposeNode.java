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
package org.jboss.seam.mvc.template.nodes;

import javax.inject.Inject;

import org.jboss.seam.mvc.template.CompositionContext;
import org.mvel2.CompileException;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.Node;
import org.mvel2.templates.util.TemplateOutputStream;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ComposeNode extends ContextualNode
{
   private static final long serialVersionUID = -9214745288595708748L;

   @Inject
   private CompositionContext composition;

   @Override
   public Object eval(final TemplateRuntime runtime, final TemplateOutputStream appender, final Object ctx,
            final VariableResolverFactory factory)
   {
      String line = new String(contents);
      if ((line == null) || line.isEmpty())
      {
         throw new CompileException("@" + getName()
                  + "{ ...template... } requires 1 parameter, instead received @" + getName() + "{"
                  + line + "}");
      }

      // TODO consider evaluating this for dynamic template names
      String requested = line.trim();

      if ((requested != null) && !composition.isRequested())
      {
         composition.setRequestedTemplate(requested);
      }
      else if (composition.isRequested())
      {
         throw new CompileException("Duplicate @" + getName()
                  + "{" + getName() + "} detected in template: <template name?>");
      }
      return next != null ? next.eval(runtime, appender, ctx, factory) : null;
   }

   @Override
   public boolean demarcate(final Node terminatingNode, final char[] template)
   {
      return false;
   }

}
