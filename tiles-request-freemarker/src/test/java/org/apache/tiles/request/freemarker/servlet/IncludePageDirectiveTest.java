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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.request.Request;
import org.apache.tiles.request.RequestWrapper;
import org.apache.tiles.request.servlet.ServletRequest;
import org.junit.Before;
import org.junit.Test;

import freemarker.core.Environment;
import freemarker.core.InvalidReferenceException;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 *
 * @version $Rev$ $Date$
 * @since 1.1.0
 */

public class IncludePageDirectiveTest {

    private Template template;
    private TemplateHashModel model;
    private PrintWriter out;
    private Map<String, TemplateModel> params;
    private TemplateModel[] loopVars = {};
    private TemplateDirectiveBody body;
    
    private IncludePageDirective directive;
    
    /**
     * Sets up the test.
     * @throws TemplateModelException 
     */
    @Before
    public void setUp() throws TemplateModelException {
        template = createMock(Template.class);
        expect(template.getMacros()).andReturn(Collections.emptyMap());
        model = createMock(TemplateHashModel.class);
        out = createMock(PrintWriter.class);
        body = createMock(TemplateDirectiveBody.class);
        directive = new IncludePageDirective();
        params = new HashMap<String, TemplateModel>();
        params.put("path", ObjectWrapper.DEFAULT_WRAPPER.wrap("/test.jsp"));
    }
    
    @Test
    public void testExecute() throws TemplateException, IOException, ServletException {
        ServletRequest request = createMock(ServletRequest.class);
        RequestWrapper requestWrapper = createMock(RequestWrapper.class);
        expect(requestWrapper.getWrappedRequest()).andReturn(request);
        expect(model.get(Request.class.getName())).andReturn(ObjectWrapper.DEFAULT_WRAPPER.wrap(requestWrapper));
        RequestDispatcher dispatcher = createMock(RequestDispatcher.class);
        HttpServletRequest httpRequest = createMock(HttpServletRequest.class);
        expect(request.getRequest()).andReturn(httpRequest);
        expect(httpRequest.getRequestDispatcher("/test.jsp")).andReturn(dispatcher);
        HttpServletResponse httpResponse = createMock(HttpServletResponse.class);
        expect(request.getResponse()).andReturn(httpResponse);
        expect(httpResponse.getWriter()).andReturn(out);
        dispatcher.include(httpRequest, httpResponse);
        replay(template, model, out, body, request, requestWrapper, httpRequest, httpResponse, dispatcher);
        Environment env = new Environment(template, model, out);
        directive.execute(env, params, loopVars, body);
        verify(template, model, out, body, request, requestWrapper, httpRequest, httpResponse, dispatcher);
    }
    
    @Test(expected = InvalidReferenceException.class)
    public void testExecuteNoServlet() throws TemplateException, IOException {
        Request request = createMock(Request.class);
        expect(model.get(Request.class.getName())).andReturn(ObjectWrapper.DEFAULT_WRAPPER.wrap(request));
        replay(template, model, out, body, request);
        Environment env = new Environment(template, model, out);
        directive.execute(env, params, loopVars, body);
        verify(template, model, out, body, request);
    }
}
