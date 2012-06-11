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
package org.apache.tiles.request.freemarker.render;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.freemarker.RequestTemplateLoader;
import org.apache.tiles.request.locale.URLApplicationResource;
import org.apache.tiles.request.render.CannotRenderException;
import org.junit.Before;
import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateExceptionHandler;

/**
 * Tests {@link FreemarkerRenderer}.
 *
 * @version $Rev$ $Date$
 */
public class FreemarkerRendererTest {

    /**
     * The application context.
     */
    private ApplicationContext applicationContext;

    /**
     * The request.
     */
    private Request request;

    /**
     * The FreemarkerRenderer to test.
     */
    private FreemarkerRenderer renderer;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        applicationContext = createMock(ApplicationContext.class);
        URLApplicationResource resource = new URLApplicationResource("/hello.ftl", getClass().getClassLoader().getResource("test.ftl"));
        expect(applicationContext.getResource("/hello.ftl")).andReturn(resource).anyTimes();
        expect(applicationContext.getResource(anyObject(String.class))).andReturn(null).anyTimes();
        
        request = createMock(Request.class);
        expect(request.getAvailableScopes()).andReturn(Arrays.asList("application", "request")).anyTimes();
        Map<String, Object> applicationScope = Collections.<String, Object> emptyMap();
        Map<String, Object> requestScope = Collections.<String, Object> emptyMap();
        Map<String, String> requestParams = Collections.<String, String> emptyMap();
        expect(request.getContext("application")).andReturn(applicationScope).anyTimes();
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();
        expect(request.getParam()).andReturn(requestParams).anyTimes();
        expect(request.getRequestLocale()).andReturn(Locale.ROOT).anyTimes();

        Configuration config = new Configuration();
        config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        config.setObjectWrapper(ObjectWrapper.DEFAULT_WRAPPER);
        config.setTemplateLoader(new RequestTemplateLoader(applicationContext));
        config.setDefaultEncoding("ISO-8859-1");
        config.setTemplateUpdateDelay(0);
        renderer = new FreemarkerRenderer();
        renderer.setConfiguration(config);
    }

    /**
     * Tests {@link FreemarkerRenderer#render(String, org.apache.tiles.request.Request)}.
     * @throws IOException If something goes wrong.
     * @throws ServletException If something goes wrong.
     */
    @Test
    public void testWrite() throws IOException, ServletException {
        
        StringWriter stringWriter = new StringWriter();
        expect(request.getWriter()).andReturn(stringWriter);
        
        replay(applicationContext, request);
        
        renderer.render("/hello.ftl", request);
        stringWriter.close();
        assertTrue(stringWriter.toString().startsWith("Hello!"));
        verify(applicationContext, request);
    }

    /**
     * Tests {@link FreemarkerRenderer#render(String, org.apache.tiles.request.Request)}.
     * @throws IOException If something goes wrong.
     * @throws ServletException If something goes wrong.
     */
    @Test(expected = CannotRenderException.class)
    public void testRenderException1() throws IOException, ServletException {
        replay(applicationContext, request);

        try {
            renderer.render("/doesnotexist.ftl", request);
        } finally {
            verify(applicationContext, request);
        }
    }

    /**
     * Test method for
     * {@link FreemarkerRenderer
     * #isRenderable(Object, org.apache.tiles.Attribute, org.apache.tiles.context.TilesRequestContext)}
     * .
     */
    @Test
    public void testIsRenderable() {
        replay(applicationContext, request);
        assertFalse(renderer.isRenderable(null, request));
        assertTrue(renderer.isRenderable("/my/template.ftl", request));
        assertFalse(renderer.isRenderable("my/template.ftl", request));
        assertFalse(renderer.isRenderable("/my/template.jsp", request));
        verify(applicationContext, request);
    }

}
