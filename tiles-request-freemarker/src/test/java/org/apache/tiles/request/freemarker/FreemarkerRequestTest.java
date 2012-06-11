/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tiles.request.freemarker;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import org.apache.tiles.request.ApplicationContext;
import org.junit.Before;
import org.junit.Test;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Tests {@link FreemarkerRequest}.
 *
 * @version $Rev$ $Date$
 */
public class FreemarkerRequestTest {

    /**
     * The request context to test.
     */
    private FreemarkerRequest request;

    /**
     * An ApplicationContext.
     */
    private ApplicationContext applicationContext;

    /**
     * a Template.
     */
    private Template template;
    
    /**
     * a model.
     */
    private TemplateHashModelEx model;
    
    /**
     * A print writer.
     */
    private PrintWriter writer;

    /**
     * The FreeMarker environment.
     */
    private Environment env;

    /**
     * The locale object.
     */
    private Locale locale;

    /**
     * Sets up the test.
     * @throws TemplateModelException 
     */
    @Before
    public void setUp() throws TemplateModelException {
        applicationContext = createMock(ApplicationContext.class);
        expect(applicationContext.getApplicationScope()).andReturn(new HashMap<String, Object>()).anyTimes();
        template = createMock(Template.class);
        model = createMock(TemplateHashModelEx.class);
        expect(model.get("test")).andReturn(ObjectWrapper.DEFAULT_WRAPPER.wrap("testValue")).anyTimes();
        writer = new PrintWriter(new StringWriter());
        expect(template.getMacros()).andReturn(Collections.<String, TemplateModel> emptyMap());
        replay(template, model, applicationContext);
        env = new Environment(template, model, writer);
        locale = new Locale("te", "ST");
        env.setLocale(locale);
        request = new FreemarkerRequest(applicationContext, env);
        env.setGlobalVariable("testPage", ObjectWrapper.DEFAULT_WRAPPER.wrap("testValue"));
    }

    @Test
    public void testHeaders() {
        assertEquals("Header is not empty", 0, request.getHeader().size());
        try {
            request.getHeader().put("test", "value");
            assertTrue("Header is modifiable", false);
        } catch(UnsupportedOperationException e) {
            // ok, that's what we want
        }
        assertEquals("HeaderValues is not empty", 0, request.getHeaderValues().size());
        try {
            request.getHeaderValues().put("test", new String[]{"value"});
            assertTrue("HeaderValues is modifiable", false);
        } catch(UnsupportedOperationException e) {
            // ok, that's what we want
        }
        try {
            request.getResponseHeaders().setValue("test", "value");
            assertTrue("ResponseHeaders is modifiable", false);
        } catch(UnsupportedOperationException e) {
            // ok, that's what we want
        }
        assertEquals("Param is not empty", 0, request.getParam().size());
        try {
            request.getParam().put("test", "value");
            assertTrue("Param is modifiable", false);
        } catch(UnsupportedOperationException e) {
            // ok, that's what we want
        }
        assertEquals("ParamValues is not empty", 0, request.getParamValues().size());
        try {
            request.getParamValues().put("test", new String[]{"value"});
            assertTrue("ParamValues is modifiable", false);
        } catch(UnsupportedOperationException e) {
            // ok, that's what we want
        }
        verify(template, model, applicationContext);
    }

    @Test
    public void testAvailableScopes() {
        assertEquals(Arrays.asList("application", "request", "page"), request.getAvailableScopes());
        assertSame(request.getContext("application"), request.getApplicationContext().getApplicationScope());
        assertNotNull(request.getContext("request").get("test"));
        assertNotNull(request.getContext("page").get("testPage"));
        verify(template, model, applicationContext);
    }

    @Test
    public void testApplicationContext() {
        assertSame(applicationContext, request.getApplicationContext());
        verify(template, model, applicationContext);
    }

    @Test
    public void testOutput() {
        assertEquals(writer, request.getWriter());
        assertEquals(writer, request.getPrintWriter());
        verify(template, model, applicationContext);
    }

    @Test
    public void testNotImplemented() {
        assertTrue(request.isResponseCommitted());
        assertFalse(request.isUserInRole("test"));
        verify(template, model, applicationContext);
    }

    @Test
    public void testLocale() {
        assertEquals(locale, request.getRequestLocale());
        verify(template, model, applicationContext);
    }
}
