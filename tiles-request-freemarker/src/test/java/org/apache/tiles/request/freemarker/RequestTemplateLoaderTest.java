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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;


import org.apache.commons.io.IOUtils;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.locale.URLApplicationResource;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link RequestTemplateLoader}.
 *
 * @version $Rev$ $Date$
 */
public class RequestTemplateLoaderTest {
    
    /**
     * An ApplicationContext.
     */
    private ApplicationContext applicationContext;
    
    /**
     * An Freemarker template.
     */
    private ApplicationResource template = new URLApplicationResource("/test.ftl", getClass().getClassLoader().getResource("test.ftl"));
    
    @Before
    public void setup() {
        applicationContext = createMock(ApplicationContext.class);
        expect(applicationContext.getResource("/test.ftl")).andReturn(template).anyTimes();
        expect(applicationContext.getResource(anyObject(String.class))).andReturn(null).anyTimes();
    }

    @Test
    public void testFindTemplateSource() throws IOException {
        replay(applicationContext);
        RequestTemplateLoader loader = new RequestTemplateLoader(applicationContext);
        assertEquals(template, loader.findTemplateSource("/test.ftl"));
        assertNull(loader.findTemplateSource("/doesnotexist.ftl"));
        verify(applicationContext);
    }

    @Test
    public void testGetLastModified() throws IOException {
        replay(applicationContext);
        RequestTemplateLoader loader = new RequestTemplateLoader(applicationContext);
        assertEquals(template.getLastModified(), loader.getLastModified(template));
        verify(applicationContext);
    }

    @Test
    public void testGetReader() throws IOException {
        replay(applicationContext);
        RequestTemplateLoader loader = new RequestTemplateLoader(applicationContext);
        Reader reader = loader.getReader(template, "UTF-8");
        assertTrue(IOUtils.contentEquals(new InputStreamReader(template.getInputStream(), "UTF-8"), reader));
        verify(applicationContext);
    }

    @Test
    public void testCloseTemplateSource() throws IOException {
        ApplicationResource resource = createMock(ApplicationResource.class);
        replay(applicationContext);
        RequestTemplateLoader loader = new RequestTemplateLoader(applicationContext);
        loader.closeTemplateSource(resource);
        verify(applicationContext);
    }

}
