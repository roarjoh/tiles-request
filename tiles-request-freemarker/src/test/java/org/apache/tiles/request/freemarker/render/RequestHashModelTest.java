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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import org.apache.tiles.request.Request;
import org.junit.Before;
import org.junit.Test;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModelException;

/**
 * Tests {@link FreemarkerRenderer}.
 *
 * @version $Rev$ $Date$
 */
public class RequestHashModelTest {
    private Request request;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        request = createMock(Request.class);
        expect(request.getAvailableScopes()).andReturn(Arrays.asList("application", "request")).anyTimes();
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put("applicationAttribute", 1);
        Map<String, Object> requestScope = new HashMap<String, Object>();
        applicationScope.put("requestAttr", true);
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("param", "value");
        expect(request.getContext("application")).andReturn(applicationScope).anyTimes();
        expect(request.getContext("request")).andReturn(requestScope).anyTimes();
        expect(request.getParam()).andReturn(requestParams).anyTimes();
        expect(request.getRequestLocale()).andReturn(Locale.ROOT).anyTimes();
    }

    @Test
    public void testConstructor() throws TemplateModelException {
        replay(request);
        new RequestHashModel(ObjectWrapper.DEFAULT_WRAPPER, request);
        verify(request);
    }
    
    @Test
    public void testGet() throws TemplateModelException {
        replay(request);
        RequestHashModel target = new RequestHashModel(ObjectWrapper.DEFAULT_WRAPPER, request);
        assertNotNull("the Request object does not exist", target.get("Request"));
        assertNotNull("the RequestParameters object does not exist", target.get("RequestParameters"));
        assertNotNull("the Application object does not exist", target.get("Application"));
        assertNotNull("applicationAttribute not found", target.get("applicationAttribute"));
        assertNotNull("requestAttr not found", target.get("requestAttr"));
        assertNull("params should not be directly available in the hash", target.get("param"));
        assertNull("found an attribute that does not exist!", target.get("doesnotexist"));
        verify(request);
    }
    
}
