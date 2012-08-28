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

package org.apache.tiles.request.velocity;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.attribute.Addable;
import org.apache.velocity.context.Context;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.0.0
 */

public class RootVelocityRequest implements Request {

    private ApplicationContext applicationContext;
    private Context ctx;
    private Writer writer;
    private PrintWriter printWriter;
    private Map<String, Object> requestScope;
    private boolean committed;

    public RootVelocityRequest(ApplicationContext applicationContext, Context ctx, Writer writer) {
        this.applicationContext = applicationContext;
        this.ctx = ctx;
        this.writer = writer;
        if(writer instanceof PrintWriter) {
            this.printWriter = (PrintWriter)writer;
        }
        else {
            this.printWriter = new PrintWriter(writer);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getHeader() {
        return Collections.<String, String> emptyMap();
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String[]> getHeaderValues() {
        return Collections.<String, String[]> emptyMap();
    }

    /** {@inheritDoc} */
    @Override
    public Addable<String> getResponseHeaders() {
        return new Addable<String>() {

            @Override
            public void setValue(String key, String value) {
            }

        };
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> getContext(String scope) {
        if ("application".equals(scope)) {
            return applicationContext.getApplicationScope();
        } else if ("request".equals(scope)) {
            if (requestScope == null) {
                requestScope = new VelocityScopeMap(ctx);
            }
            return requestScope;
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getAvailableScopes() {
        return Arrays.asList("request", "application");
    }

    /** {@inheritDoc} */
    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /** {@inheritDoc} */
    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Velocity doesn't support binary output");
    }

    /** {@inheritDoc} */
    @Override
    public Writer getWriter() throws IOException {
        committed = true;
        return writer;
    }

    /** {@inheritDoc} */
    @Override
    public PrintWriter getPrintWriter() throws IOException {
        committed = true;
        return printWriter;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isResponseCommitted() {
        return committed;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getParam() {
        return Collections.<String, String> emptyMap();
   }

    /** {@inheritDoc} */
    @Override
    public Map<String, String[]> getParamValues() {
        return Collections.<String, String[]> emptyMap();
    }

    /** {@inheritDoc} */
    @Override
    public Locale getRequestLocale() {
        return Locale.ROOT;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

}
