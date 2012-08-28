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

package org.apache.tiles.request.freemarker.servlet;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.freemarker.render.FreemarkerRenderer;
import org.apache.tiles.request.freemarker.render.RequestHashModel;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModelException;

/**
 *
 * @version $Rev$ $Date$
 * @since 1.1.0
 */

public class RequestFreemarkerServletTest {

    private ApplicationContext applicationContext;
    private ServletConfig config;
    private ServletContext servletContext;
    private RequestFreemarkerServlet servlet;
    private Capture<FreemarkerRenderer> renderer = new Capture<FreemarkerRenderer>();

    @Before
    public void setup() {
        applicationContext = createMock(ApplicationContext.class);
        servletContext = createMock(ServletContext.class);
        expect(servletContext.getAttribute(ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE)).andReturn(
                applicationContext).anyTimes();
        expect(servletContext.getAttribute(FreemarkerRenderer.class.getName())).andReturn(null);
        servletContext.setAttribute(eq(FreemarkerRenderer.class.getName()), capture(renderer));
        config = createMock(ServletConfig.class);
        expect(config.getServletContext()).andReturn(servletContext).anyTimes();
        expect(config.getInitParameter(anyObject(String.class))).andReturn(null).anyTimes();
        expect(config.getInitParameterNames()).andReturn(Collections.enumeration(Collections.<String> emptyList()))
                .anyTimes();
        servlet = new RequestFreemarkerServlet();
    }

    @Test
    public void testInit() throws ServletException {
        Request request = createMock(Request.class);
        replay(applicationContext, servletContext, config, request);
        servlet.init(config);
        assertTrue("renderer is not propertly configured", renderer.getValue().isRenderable("/test.ftl", request));
        verify(applicationContext, servletContext, config, request);
    }

    @Test
    public void testLocale() throws ServletException {
        Locale result = new Locale("te", "ST");
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getLocale()).andReturn(result);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        replay(applicationContext, servletContext, config, request, response);
        servlet.init(config);
        assertEquals("Wrong locale", result, servlet.deduceLocale("/anypath", request, response));
        verify(applicationContext, servletContext, config, request, response);
    }

    @Test
    public void testCreateModel() throws ServletException, TemplateModelException {
        ObjectWrapper wrapper = createMock(ObjectWrapper.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        expect(applicationContext.getApplicationScope()).andReturn(Collections.<String, Object> emptyMap());
        replay(applicationContext, servletContext, config, wrapper, request, response);
        servlet.init(config);
        assertTrue("wrong model",
                servlet.createModel(wrapper, servletContext, request, response) instanceof RequestHashModel);
        verify(applicationContext, servletContext, config, wrapper, request, response);
    }
}
