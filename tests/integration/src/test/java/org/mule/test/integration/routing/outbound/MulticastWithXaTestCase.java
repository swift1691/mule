/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.routing.outbound;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Make sure to run an external amq broker, otherwise the test isn't possible.
 */
public class MulticastWithXaTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/routing/outbound/multicasting-router-xa-config.xml";
    }

    @Test
    public void testName() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage msg = new DefaultMuleMessage("Hi", client.getMuleContext());
        client.dispatch("jms://Myflow.input?connector=simpleJmsConnector", msg);
        MuleMessage result = client.request("jms://Myflow.finishedOriginal?connector=simpleJmsConnector", 10000);
        assertNotNull(result);
    }
}
