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

package org.apache.tiles.request.locale;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.apache.tiles.request.ApplicationResource;

/**
 *
 * @version $Rev$ $Date$
 * @since 1.1.0
 */

public class ClasspathResourceLocator extends AbstractURLBasedResourceLocator {

    private ClassLoader classLoader;

    /**
     * 
     */
    public ClasspathResourceLocator() {
        this(null);
    }
    
    /**
     * @param classLoader
     */
    public ClasspathResourceLocator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    /** {@inheritDoc} */
    @Override
    public URLApplicationResource getResource(String localePath) {
        ClassLoader loader = classLoader;
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        String path = localePath;
        if(path.startsWith("/")) {
            path = path.substring(1);
        }
        URL url = loader.getResource(path);
        if(url != null) {
            return createResource(url);
        }
        else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Collection<ApplicationResource> getResources(String localePath) {
        ArrayList<ApplicationResource> result = new ArrayList<ApplicationResource>();
        ClassLoader loader = classLoader;
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        Enumeration<URL> urls;
        try {
            String path = localePath;
            if(path.startsWith("/")) {
                path = path.substring(1);
            }
            urls = loader.getResources(path);
        } catch (IOException e) {
            return result;
        }
        while(urls.hasMoreElements()) {
            URL url = urls.nextElement();
            result.add(createResource(url));
        }
        return result;
    }

}
