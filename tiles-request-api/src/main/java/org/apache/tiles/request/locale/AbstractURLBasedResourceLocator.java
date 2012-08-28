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
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.ApplicationResourceLocator;

/**
 *
 * @version $Rev$ $Date$
 * @since 1.1.0
 */

public abstract class AbstractURLBasedResourceLocator implements ApplicationResourceLocator {

    public abstract URLApplicationResource getResource(String localePath);

    @Override
    public final URLApplicationResource getResource(ApplicationResource base, Locale locale) {
        if (base instanceof URLApplicationResource && ((URLApplicationResource) base).getSource() == this) {
            // use the URL directly if it is safe
            URL url;
            try {
                url = new URL(base.getLocalePath(locale));
                // check that the resource exists
                InputStream is = url.openConnection().getInputStream();
                is.close();
            } catch (IOException e) {
                return null;
            }
            return createResource(url);
        } else {
            return null;
        }

    }

    protected URLApplicationResource createResource(URL url) {
        return new URLApplicationResource(url.toExternalForm(), url, this);
    }
}
