/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.http.issues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.AbstractServiceAndFlowTestCase;
import org.mule.tck.functional.StringAppendTestTransformer;
import org.mule.tck.junit4.rule.DynamicPort;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

public class HttpTransformersMule1822TestCase extends AbstractServiceAndFlowTestCase
{
    public static final String OUTBOUND_MESSAGE = "Test message";

    @Rule
    public DynamicPort dynamicPort1 = new DynamicPort("port1");

    @Rule
    public DynamicPort dynamicPort2 = new DynamicPort("port2");

    @Rule
    public DynamicPort dynamicPort3 = new DynamicPort("port3");

    public HttpTransformersMule1822TestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "http-transformers-mule-1822-test-service.xml"},
            {ConfigVariant.FLOW, "http-transformers-mule-1822-test-flow.xml"}
        });
    }      

    private MuleMessage sendTo(String uri) throws MuleException
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage message = client.send(uri, OUTBOUND_MESSAGE, null);
        assertNotNull(message);
        return message;
    }

    /**
     * With no transformer we expect just the modification from the FTC
     */
    @Test
    public void testBase() throws Exception
    {
        assertEquals(OUTBOUND_MESSAGE  + " Received", sendTo("base").getPayloadAsString());
    }

    /**
     * But response transformers on the base model should be applied
     */
    @Test
    public void testResponse() throws Exception
    {
        assertEquals(
                StringAppendTestTransformer.append(" response",
                        StringAppendTestTransformer.append(" response 2",
                                        OUTBOUND_MESSAGE + " Received")),
                sendTo("response").getPayloadAsString());
    }

    /**
     * Should also work with inbound transformers
     */
    @Test
    public void testBoth() throws Exception
    {
        assertEquals(
            StringAppendTestTransformer.append(" response",
                StringAppendTestTransformer.append(" response 2",
                    StringAppendTestTransformer.append(" transformed 2",
                        StringAppendTestTransformer.appendDefault(OUTBOUND_MESSAGE)) + " Received")),
                sendTo("both").getPayloadAsString());
    }

}
