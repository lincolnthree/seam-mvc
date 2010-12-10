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

import java.util.Queue;

import javax.inject.Inject;

import org.jboss.seam.render.template.nodes.ContextualNode;
import org.jboss.seam.render.util.Tokenizer;
import org.jboss.weld.extensions.el.Expressions;
import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.Node;
import org.mvel2.templates.util.TemplateOutputStream;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class BindingNode extends ContextualNode
{
   private static final long serialVersionUID = -8274035715437363235L;
   private static final String DELIM = ":";

   @Inject
   private Expressions expressions;

   @Inject
   private BindingContext bindings;

   @Override
   public void setContents(final char[] contents)
   {
      super.setContents(contents);
      setup();
   }

   private void setup()
   {

   }

   @Override
   public Object eval(final TemplateRuntime runtime, final TemplateOutputStream appender, final Object ctx,
            final VariableResolverFactory factory)
   {
      /*
       * TODO this needs to be a compile time step that stores bindings, possibly with a Tree-visitor (See:
       * template.getRoot())
       */
      String line = new String(contents);

      Queue<String> tokens = Tokenizer.tokenize(DELIM, line);

      if (tokens.size() != 2)
      {
         throw new CompileException("@" + getName()
                  + "{ param " + DELIM + " bean.field } requires two parameters, instead received @bind{"
                  + line + "}");
      }

      String name = tokens.remove().trim();
      String el = tokens.remove().trim();
      name = MVEL.eval(name, ctx, factory, String.class);
      el = MVEL.eval(el, ctx, factory, String.class);

      Object result = expressions.evaluateValueExpression(expressions.toExpression(el));
      bindings.put(name, el);
      if (result != null)
      {
         // appender.append(result.toString());
      }

      return next != null ? next.eval(runtime, appender, ctx, factory) : null;
   }

   @Override
   public boolean demarcate(final Node terminatingNode, final char[] template)
   {
      return false;
   }

}
