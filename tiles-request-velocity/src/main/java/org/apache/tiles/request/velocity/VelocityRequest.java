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

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.tiles.request.DefaultRequestWrapper;
import org.apache.tiles.request.Request;
import org.apache.velocity.context.Context;

/**
 * The implementation of the Tiles request context specific for Velocity.
 *
 * @version $Rev$ $Date$
 */
public class VelocityRequest extends DefaultRequestWrapper {

    /**
     * The native available scopes, in fact only "page".
     */
    private final List<String> scopes;

    /**
     * The Velocity current context.
     */
    private final Context ctx;

    /**
     * The writer to use to render the response. It may be null, if not necessary.
     */
    private Writer writer;

    /**
     * The map of the page scope.
     */
    private Map<String, Object> pageScope;

    /**
     * Constructor.
     *
     * @param enclosedRequest The request that exposes non-Velocity specific properties
     * @param ctx The Velocity current context.
     * @param writer The writer to use to render the response. It may be null, if not necessary.
     */
    public VelocityRequest(Request enclosedRequest, Context ctx, Writer writer) {
        super(enclosedRequest);
        List<String> scopes = new ArrayList<String>();
        scopes.addAll(enclosedRequest.getAvailableScopes());
        scopes.add("page");
        this.scopes = Collections.unmodifiableList(scopes);
        this.ctx = ctx;
        this.writer = writer;
    }

    @Override
    public List<String> getAvailableScopes() {
        return scopes;
    }

    /** {@inheritDoc} */
    @Override
    public PrintWriter getPrintWriter() {
        if (writer == null) {
            throw new IllegalStateException("A writer-less Tiles request has been created, cannot return a PrintWriter");
        }
        if (writer instanceof PrintWriter) {
            return (PrintWriter) writer;
        }
        return new PrintWriter(writer);
    }

    /** {@inheritDoc} */
    @Override
    public Writer getWriter() {
        if (writer == null) {
            throw new IllegalStateException("A writer-less Tiles request has been created, cannot return a PrintWriter");
        }
        return writer;
    }

    /**
     * Returns the page scope.
     *
     * @return The page scope.
     */
    public Map<String, Object> getPageScope() {
        if (pageScope == null) {
            pageScope = new VelocityScopeMap(ctx);
        }
        return pageScope;
    }

    @Override
    public Map<String, Object> getContext(String scope) {
        return "page".equals(scope) ? getPageScope() : super.getContext(scope);
    }

    public Context getVelocityContext() {
        return ctx;
    }
    
}
