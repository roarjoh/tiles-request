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

package org.apache.tiles.request.spring;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.locale.AbstractURLBasedResourceLocator;
import org.apache.tiles.request.locale.URLApplicationResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 *
 * @version $Rev$ $Date$
 * @since 1.1.0
 */

public class SpringResourceLocator extends AbstractURLBasedResourceLocator {

    private ResourcePatternResolver resourceLoader;

    /**
     * @param resourceLoader
     */
    public SpringResourceLocator(ResourcePatternResolver resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /** {@inheritDoc} */
    @Override
    public URLApplicationResource getResource(String localePath) {
        try {
            Resource resource = resourceLoader.getResource(localePath);
            URL url = resource.getURL();
            if (url != null) {
                return createResource(url);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Collection<ApplicationResource> getResources(String localePath) {
        ArrayList<ApplicationResource> resources = new ArrayList<ApplicationResource>();
        Resource[] foundResources;
        try {
            foundResources = resourceLoader.getResources(localePath);
        } catch (IOException e) {
            return Collections.<ApplicationResource> emptyList();
        }
        for (Resource resource : foundResources) {
            try {
                URL url = resource.getURL();
                if (url != null) {
                    resources.add(createResource(url));
                }
            } catch (IOException e) {
                continue;
            }
        }
        return resources;
    }

}
