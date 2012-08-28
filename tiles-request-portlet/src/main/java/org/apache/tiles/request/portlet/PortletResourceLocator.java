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

package org.apache.tiles.request.portlet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.portlet.PortletContext;

import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.ApplicationResourceLocator;
import org.apache.tiles.request.locale.URLApplicationResource;

/**
 *
 * @version $Rev$ $Date$
 * @since 1.1.0
 */

public class PortletResourceLocator implements ApplicationResourceLocator {

    private PortletContext context;

    /**
     * @param context
     */
    protected PortletResourceLocator(PortletContext context) {
        this.context = context;
    }

    /** {@inheritDoc} */
    @Override
    public URLApplicationResource getResource(String localePath) {
        try {
            URL url = context.getResource(localePath);
            if (url != null) {
                return new URLApplicationResource(localePath, url, this);
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public URLApplicationResource getResource(ApplicationResource base, Locale locale) {
        if (base instanceof URLApplicationResource && ((URLApplicationResource) base).getSource() == this) {
            return getResource(base.getLocalePath(locale));
        } else {
            return null;
        }

    }

    /** {@inheritDoc} */
    @Override
    public Collection<ApplicationResource> getResources(String localePath) {
        ApplicationResource result = getResource(localePath);
        if (result != null) {
            return Arrays.<ApplicationResource> asList(result);
        } else {
            return Collections.<ApplicationResource> emptyList();
        }
    }

}
