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

import java.util.Map;

import javax.inject.Inject;

import org.jboss.seam.mvc.spi.resolver.TemplateResource;
import org.jboss.seam.mvc.template.CompiledView;
import org.jboss.seam.mvc.template.CompositionContext;
import org.jboss.seam.mvc.template.ViewCompiler;
import org.jboss.seam.mvc.template.util.NullTemplateOutputStream;
import org.mvel2.CompileException;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.Node;
import org.mvel2.templates.res.TerminalNode;
import org.mvel2.templates.util.TemplateOutputStream;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ComposeNode extends ContextualNode
{
   private static final long serialVersionUID = -9214745288595708748L;

   @Inject
   private ViewCompiler compiler;

   private Node inside;

   public ComposeNode()
   {
      super();
      terminus = new TerminalNode();
   }

   @Override
   @SuppressWarnings("unchecked")
   public Object eval(final TemplateRuntime runtime, final TemplateOutputStream appender, final Object ctx,
            final VariableResolverFactory factory)
   {
      String requestedTemplate = new String(contents);
      if ((requestedTemplate == null) || requestedTemplate.isEmpty())
      {
         throw new CompileException("@" + getName()
                  + "{ ...template... } requires 1 parameter, instead received @" + getName() + "{"
                  + requestedTemplate + "}");
      }

      this.inside = next;
      next = terminus;

      // TODO consider evaluating this for dynamic template names
      String requested = requestedTemplate.trim();

      if (requested != null)
      {
         // Detect bindings
         TemplateOutputStream stream = new NullTemplateOutputStream();
         inside.eval(runtime, stream, ctx, factory);

         // Clone context
         Map<Object, Object> clonedContext = cloneContext((Map<Object, Object>) ctx);

         CompositionContext compositionContext = (CompositionContext) ((Map<Object, Object>) ctx)
                  .get(CompiledView.CONTEXT_KEY);

         // TemplateResource templateResource = resourceStack.pop();
         TemplateResource<?> templateResource = compositionContext.getTemplateResource();

         // compiler.compileRelative(templateResource, requested);
         CompiledView composite = compiler.compileRelative(templateResource, requested);

         // Execute new CompiledView.render for requested template on cloned context
         String render = composite.render(compositionContext, clonedContext);

         appender.append(render);
      }
      return next != null ? next.eval(runtime, appender, ctx, factory) : null;
   }

   private Map<Object, Object> cloneContext(final Map<Object, Object> ctx)
   {
      // TODO clone context
      return ctx;
   }

   @Override
   public boolean demarcate(final Node terminatingNode, final char[] template)
   {
      return true;
   }

   @Override
   public boolean isOpenNode()
   {
      return true;
   }

}
