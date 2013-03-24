/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.servlet.config;

import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * An {@link Operation} responsible for sending status codes via {@link HttpServletResponse#setStatus(int)} and
 * {@link HttpServletResponse#flushBuffer()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class SendStatus extends HttpOperation
{
   private final int code;

   private SendStatus(final int code)
   {
      this.code = code;
   }

   @Override
   public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
         ((HttpInboundServletRewrite) event).sendStatusCode(code);
   }

   protected int getCode()
   {
      return code;
   }

   /**
    * Create an {@link Operation} that will send an HTTP status code to the browser, then call
    * {@link HttpInboundServletRewrite#abort()}
    */
   public static SendStatus code(final int code)
   {
      return new SendStatus(code);
   }

   /**
    * Create an {@link Operation} that will send an HTTP error code to the browser, then call
    * {@link HttpInboundServletRewrite#abort()}
    */
   public static SendStatus error(final int code)
   {
      return new SendError(code);
   }

   private static class SendError extends SendStatus
   {
      public SendError(final int code)
      {
         super(code);
      }

      @Override
      public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
      {
         if (event instanceof HttpInboundServletRewrite)
            ((HttpInboundServletRewrite) event).sendErrorCode(this.getCode());
      }
   }

}
