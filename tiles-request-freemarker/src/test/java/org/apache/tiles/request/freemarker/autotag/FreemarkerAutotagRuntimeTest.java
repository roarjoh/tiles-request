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
package org.apache.tiles.request.freemarker.autotag;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.freemarker.FreemarkerRequest;
import org.apache.tiles.request.freemarker.autotag.FreemarkerAutotagRuntime;
import org.apache.tiles.request.freemarker.autotag.FreemarkerModelBody;
import org.junit.Test;
import freemarker.core.Environment;
import freemarker.core.Macro;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;

/**
 * Tests {@link FreemarkerAutotagRuntime}.
 *
 * @version $Rev$ $Date$
 */
public class FreemarkerAutotagRuntimeTest {

    @Test
    public void testLookupRequest() throws IOException, TemplateModelException {
        Map<String, TemplateModel> params = Collections.<String, TemplateModel> emptyMap();
        Template template = createMock(Template.class);
        expect(template.getMacros()).andReturn(Collections.<String, Macro> emptyMap());
        TemplateHashModel rootDataModel = createMock(TemplateHashModel.class);
        Request parentRequest = createMock(Request.class);
        expect(rootDataModel.get(Request.class.getName())).andReturn(ObjectWrapper.DEFAULT_WRAPPER.wrap(parentRequest));
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        expect(parentRequest.getApplicationContext()).andReturn(applicationContext);
        expect(parentRequest.getAvailableScopes()).andReturn(Arrays.asList("parent"));
        PrintWriter out = createMock(PrintWriter.class);
        TemplateDirectiveBody body = createMock(TemplateDirectiveBody.class);
        replay(template, rootDataModel, out, body, parentRequest, applicationContext);
        Environment env = new Environment(template, rootDataModel, out);
        FreemarkerAutotagRuntime runtime = new FreemarkerAutotagRuntime();
        runtime.execute(env, params, new TemplateModel[0], body);
        Request request = runtime.createRequest();
        assertTrue(request instanceof FreemarkerRequest);
        assertEquals(env, ((FreemarkerRequest)request).getEnvironment());
        verify(template, rootDataModel, out, body, parentRequest, applicationContext);
    }

    @Test
    public void testCreateRequest() throws IOException, TemplateModelException {
        Map<String, TemplateModel> params = Collections.<String, TemplateModel> emptyMap();
        Template template = createMock(Template.class);
        expect(template.getMacros()).andReturn(Collections.<String, Macro> emptyMap());
        TemplateHashModel rootDataModel = createMock(TemplateHashModel.class);
        expect(rootDataModel.get(Request.class.getName())).andReturn(null);
        Configuration config = createMock(Configuration.class);
        expect(template.getConfiguration()).andReturn(config).anyTimes();
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        expect(config.getSharedVariable(Request.class.getName())).andReturn(null);
        expect(config.getSharedVariable(ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE)).andReturn(
                ObjectWrapper.DEFAULT_WRAPPER.wrap(applicationContext));
        PrintWriter out = createMock(PrintWriter.class);
        TemplateDirectiveBody body = createMock(TemplateDirectiveBody.class);
        
        replay(template, rootDataModel, out, body, applicationContext, config);
        Environment env = new Environment(template, rootDataModel, out);
        FreemarkerAutotagRuntime runtime = new FreemarkerAutotagRuntime();
        runtime.execute(env, params, new TemplateModel[0], body);
        Request request = runtime.createRequest();
        assertTrue(request instanceof FreemarkerRequest);
        assertEquals(env, ((FreemarkerRequest)request).getEnvironment());
        verify(template, rootDataModel, out, body, applicationContext, config);
    }

    @Test
    public void testCreateModelBody() {
        Template template = createMock(Template.class);
        TemplateHashModel rootDataModel = createMock(TemplateHashModel.class);
        Writer out = createMock(Writer.class);
        expect(template.getMacros()).andReturn(new HashMap<String, Macro>());
        replay(template, rootDataModel, out);
        Environment env = new Environment(template, rootDataModel, out);
        @SuppressWarnings("unchecked")
        Map<String, TemplateModel> params = createMock(Map.class);
        TemplateDirectiveBody body = createMock(TemplateDirectiveBody.class);
        replay(params, body);
        FreemarkerAutotagRuntime runtime = new FreemarkerAutotagRuntime();
        runtime.execute(env, params, new TemplateModel[0], body);
        ModelBody modelBody = runtime.createModelBody();
        assertTrue(modelBody instanceof FreemarkerModelBody);
        verify(template, rootDataModel, out, params, body);
    }

    @Test
    public void testGetParameter() throws TemplateModelException {
        Template template = createMock(Template.class);
        TemplateHashModel rootDataModel = createMock(TemplateHashModel.class);
        Writer out = createMock(Writer.class);
        expect(template.getMacros()).andReturn(new HashMap<String, Macro>());
        replay(template, rootDataModel, out);
        Environment env = new Environment(template, rootDataModel, out);
        TemplateNumberModel model = createMock(TemplateNumberModel.class);
        expect(model.getAsNumber()).andReturn(new Integer(42)).anyTimes();
        @SuppressWarnings("unchecked")
        Map<String, TemplateModel> params = createMock(Map.class);
        TemplateDirectiveBody body = createMock(TemplateDirectiveBody.class);
        expect(params.get(eq("notnullParam"))).andReturn(model).anyTimes();
        expect(params.get(eq("nullParam"))).andReturn(null).anyTimes();
        replay(model, params, body);
        FreemarkerAutotagRuntime runtime = new FreemarkerAutotagRuntime();
        runtime.execute(env, params, new TemplateModel[0], body);
        Object notnullParam = runtime.getParameter("notnullParam", Object.class, null);
        Object nullParam = runtime.getParameter("nullParam", Object.class, null);
        int notnullParamDefault = runtime.getParameter("notnullParam", Integer.class, new Integer(24));
        int nullParamDefault = runtime.getParameter("nullParam", Integer.class, new Integer(24));
        assertEquals(42, notnullParam);
        assertEquals(null, nullParam);
        assertEquals(42, notnullParamDefault);
        assertEquals(24, nullParamDefault);
        verify(template, rootDataModel, out, model, params, body);
    }
}
