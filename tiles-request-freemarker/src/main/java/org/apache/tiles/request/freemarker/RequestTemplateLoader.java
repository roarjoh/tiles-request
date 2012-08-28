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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import freemarker.cache.TemplateLoader;

public class RequestTemplateLoader implements TemplateLoader {
    private ApplicationContext applicationContext;

    public RequestTemplateLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        String id = name;
        if (!id.startsWith("/")) {
            id = "/" + id;
        }
        return applicationContext.getResource(id);
    }

    @Override
    public long getLastModified(Object templateSource) {
        ApplicationResource source = (ApplicationResource) templateSource;
        try {
            return source.getLastModified();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        ApplicationResource source = (ApplicationResource) templateSource;
        return new InputStreamReader(source.getInputStream(), encoding);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
    }
}
