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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.attribute.Addable;
import org.junit.Before;
import org.junit.Test;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Tests {@link FreemarkerRequest}.
 *
 * @version $Rev$ $Date$
 */
public class FreemarkerRequestWrapperTest {

    /**
     * The reuqest context to test.
     */
    private FreemarkerRequest request;

    /**
     * A Request.
     */
    private Request enclosedRequest;
    
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
    private TemplateHashModel model;
    
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
        model = createMock(TemplateHashModel.class);
        writer = new PrintWriter(new StringWriter());
        expect(template.getMacros()).andReturn(Collections.<String, TemplateModel> emptyMap());
        enclosedRequest = createMock(Request.class);
        expect(enclosedRequest.getApplicationContext()).andReturn(applicationContext).anyTimes();
        expect(enclosedRequest.getAvailableScopes()).andReturn(Arrays.asList("parent")).anyTimes();
        replay(template, model, applicationContext);
        env = new Environment(template, model, writer);
        locale = new Locale("te", "ST");
        env.setLocale(locale);
        env.setGlobalVariable("testPage",ObjectWrapper.DEFAULT_WRAPPER.wrap("testValue"));
    }
    
    @Test
    public void testAvailableScopes() {
        replay(enclosedRequest);
        request = new FreemarkerRequestWrapper(enclosedRequest, env);
        assertEquals(Arrays.asList("parent", "page"), request.getAvailableScopes());
        assertNotNull(request.getContext("page").get("testPage"));
        verify(template, model, applicationContext, enclosedRequest);
    }

    @Test
    public void testDelegates() throws IOException {
        expect(enclosedRequest.getOutputStream()).andReturn(new ByteArrayOutputStream()).times(2);
        expect(enclosedRequest.getWriter()).andReturn(writer);
        expect(enclosedRequest.getPrintWriter()).andReturn(writer);
        expect(enclosedRequest.isResponseCommitted()).andReturn(false);
        expect(enclosedRequest.getHeader()).andReturn(new HashMap<String, String>()).times(2);
        expect(enclosedRequest.getHeaderValues()).andReturn(new HashMap<String, String[]>()).times(2);
        expect(enclosedRequest.getResponseHeaders()).andReturn(new Addable<String>() {
            
            @Override
            public void setValue(String key, String value) {
                
            }
        }).times(2);
        expect(enclosedRequest.getParam()).andReturn(new HashMap<String, String>()).times(2);
        expect(enclosedRequest.getParamValues()).andReturn(new HashMap<String, String[]>()).times(2);
        expect(enclosedRequest.isUserInRole("test")).andReturn(true);
        expect(enclosedRequest.getRequestLocale()).andReturn(locale);
        replay(enclosedRequest);
        request = new FreemarkerRequestWrapper(enclosedRequest, env);
        assertSame(applicationContext, request.getApplicationContext());
        assertSame(enclosedRequest.getOutputStream(), request.getOutputStream());
        assertSame(enclosedRequest.getWriter(), request.getWriter());
        assertSame(enclosedRequest.getPrintWriter(), request.getPrintWriter());
        assertEquals(false, request.isResponseCommitted());
        assertSame(enclosedRequest.getHeader(), request.getHeader());
        assertSame(enclosedRequest.getHeaderValues(), request.getHeaderValues());
        assertSame(enclosedRequest.getResponseHeaders(), request.getResponseHeaders());
        assertSame(enclosedRequest.getParam(), request.getParam());
        assertSame(enclosedRequest.getParamValues(), request.getParamValues());
        assertTrue(request.isUserInRole("test"));
        assertEquals(locale, request.getRequestLocale());
        verify(template, model, applicationContext, enclosedRequest);
    }
}
