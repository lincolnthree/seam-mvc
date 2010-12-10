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

import javax.inject.Inject;

import org.jboss.seam.mvc.util.Crypto;
import org.jboss.seam.render.template.nodes.ContextualNode;
import org.mvel2.CompileException;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.Node;
import org.mvel2.templates.util.TemplateOutputStream;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ActionNode extends ContextualNode
{
   private static final long serialVersionUID = -8274035715437363235L;
   private static final String DELIM = ":";

   @Inject
   private ActionContext actions;

   private CompiledTemplate action;

   @Override
   public void setContents(final char[] contents)
   {
      super.setContents(contents);

      String str = new String(contents).trim();

      if (str.isEmpty())
      {
         throw new CompileException("@" + getName()
                  + "{ param " + DELIM + " bean.field } requires two parameters, instead received @" + getName() + "{"
                  + action + "}");
      }

      try
      {
         action = TemplateCompiler.compileTemplate(contents);
      }
      catch (Exception e)
      {
         throw new CompileException("Could not compile action [" + str + "]", e);
      }

   }

   @Override
   public Object eval(final TemplateRuntime runtime, final TemplateOutputStream appender, final Object ctx,
            final VariableResolverFactory factory)
   {
      try
      {
         String a = (String) TemplateRuntime.execute(action, ctx, factory);
         String hash = "a_" + Crypto.hash(a);
         actions.put(hash, a);
         appender.append(hash);
      }
      catch (Exception e)
      {
         throw new CompileException("Could not bind action [" + new String(contents) + "]", e);
      }
      return next != null ? next.eval(runtime, appender, ctx, factory) : null;
   }

   @Override
   public boolean demarcate(final Node terminatingNode, final char[] template)
   {
      return false;
   }

}
