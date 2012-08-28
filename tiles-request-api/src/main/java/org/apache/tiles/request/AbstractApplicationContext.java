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

package org.apache.tiles.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.0.0
 */

public abstract class AbstractApplicationContext implements ApplicationContext {
    private List<ApplicationResourceLocator> locators = new ArrayList<ApplicationResourceLocator>();

    public void register(ApplicationResourceLocator locator) {
        this.locators.add(locator);
    }

    public void setLocators(List<ApplicationResourceLocator> locators) {
        this.locators = locators;
    }

    @Override
    public ApplicationResource getResource(String localePath) {
        for (ApplicationResourceLocator locator : locators) {
            ApplicationResource resource = locator.getResource(localePath);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

    @Override
    public ApplicationResource getResource(ApplicationResource base, Locale locale) {
        // use the same locator if known
        if (base instanceof LocatedResource && ((LocatedResource) base).getSource() != null) {
            return ((LocatedResource) base).getSource().getResource(base, locale);
        }
        // otherwise lookup for the right one
        for (ApplicationResourceLocator locator : locators) {
            ApplicationResource resource = locator.getResource(base, locale);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

    @Override
    public Collection<ApplicationResource> getResources(String path) {
        Collection<ApplicationResource> retValue = new ArrayList<ApplicationResource>();
        for (ApplicationResourceLocator locator : locators) {
            Collection<ApplicationResource> resources = locator.getResources(path);
            if (resources != null) {
                retValue.addAll(resources);
            }
        }
        return retValue;
    }

}
