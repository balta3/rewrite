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
package org.ocpsoft.rewrite.config;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.InboundRewrite;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.test.MockInboundRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class ConfigurationBuilderTest
{
   private boolean performed = false;
   private boolean performedOtherwise = false;
   private Operation operation;
   private Operation otherwise;
   private InboundRewrite rewrite;
   private EvaluationContext context;

   @Before
   public void before()
   {
      rewrite = new MockInboundRewrite();
      context = new MockEvaluationContext();
      operation = new Operation() {
         @Override
         public void perform(final Rewrite event, final EvaluationContext context)
         {
            performed = true;
         }
      };
      otherwise = new Operation() {
         @Override
         public void perform(final Rewrite event, final EvaluationContext context)
         {
            performedOtherwise = true;
         }
      };
   }

   private void execute(Rule rule)
   {
      if (rule.evaluate(rewrite, context))
         rule.perform(rewrite, context);
      else
         rule.otherwise(rewrite, context);
   }

   @Test
   public void testBuildConfigurationPerformOnly()
   {
      Assert.assertFalse(performed);
      Assert.assertFalse(performedOtherwise);

      Configuration config = ConfigurationBuilder.begin().addRule()
               .when(And.all(Direction.isInbound(), new True()))
               .perform(operation);

      Rule rule = config.getRules().get(0);
      execute(rule);

      Assert.assertTrue(performed);
      Assert.assertFalse(performedOtherwise);
   }

   @Test
   public void testBuildConfigurationPerformOnlyNegative()
   {
      Assert.assertFalse(performed);
      Assert.assertFalse(performedOtherwise);

      Configuration config = ConfigurationBuilder.begin().addRule()
               .when(And.all(new False()))
               .perform(operation);

      Rule rule = config.getRules().get(0);
      execute(rule);

      Assert.assertFalse(performed);
      Assert.assertFalse(performedOtherwise);
   }

   @Test
   public void testBuildConfigurationOtherwiseOnly()
   {
      Assert.assertFalse(performed);
      Assert.assertFalse(performedOtherwise);

      Configuration config = ConfigurationBuilder.begin().addRule()
               .when(new False())
               .otherwise(otherwise);

      Rule rule = config.getRules().get(0);
      execute(rule);

      Assert.assertFalse(performed);
      Assert.assertTrue(performedOtherwise);
   }

   @Test
   public void testBuildConfigurationOtherwiseNegative()
   {
      Assert.assertFalse(performed);
      Assert.assertFalse(performedOtherwise);

      Configuration config = ConfigurationBuilder.begin().addRule()
               .when(new True())
               .otherwise(otherwise);

      Rule rule = config.getRules().get(0);
      execute(rule);

      Assert.assertFalse(performed);
      Assert.assertFalse(performedOtherwise);
   }

   @Test
   public void testBuildConfigurationPerformAndOtherwise() throws Exception
   {
      Assert.assertFalse(performed);
      Assert.assertFalse(performedOtherwise);

      Configuration config = ConfigurationBuilder.begin()
               .addRule()
               .when(new True())
               .perform(operation)
               .otherwise(otherwise);

      Rule rule = config.getRules().get(0);
      execute(rule);

      Assert.assertTrue(performed);
      Assert.assertFalse(performedOtherwise);
   }

   @Test
   public void testBuildConfigurationPerformAndOtherwiseNegative() throws Exception
   {
      Assert.assertFalse(performed);
      Assert.assertFalse(performedOtherwise);

      Configuration config = ConfigurationBuilder.begin()
               .addRule()
               .when(new False())
               .perform(operation)
               .otherwise(otherwise);

      Rule rule = config.getRules().get(0);
      execute(rule);

      Assert.assertFalse(performed);
      Assert.assertTrue(performedOtherwise);
   }

}
