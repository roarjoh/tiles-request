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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tiles.request.Request;
import org.apache.tiles.request.RequestWrapper;
import org.apache.tiles.request.attribute.Addable;

import freemarker.core.Environment;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.1.0
 */

public class FreemarkerRequestWrapper extends FreemarkerRequest implements RequestWrapper {

    private List<String> scopes;
    private Request request;

    public FreemarkerRequestWrapper(Request request, Environment env) {
        super(request.getApplicationContext(), env);
        this.request = request;
        List<String> scopes = new ArrayList<String>();
        scopes.addAll(request.getAvailableScopes());
        scopes.add(SCOPE_PAGE);
        this.scopes = Collections.unmodifiableList(scopes);
    }

    @Override
    public Request getWrappedRequest() {
        return request;
    }

    public Map<String, String> getHeader() {
        return request.getHeader();
    }

    public Map<String, String[]> getHeaderValues() {
        return request.getHeaderValues();
    }

    public Addable<String> getResponseHeaders() {
        return request.getResponseHeaders();
    }

    public Map<String, Object> getContext(String scope) {
        if (SCOPE_PAGE.equals(scope)) {
            return super.getContext(scope);
        } else {
            return request.getContext(scope);
        }
    }

    @Override
    public List<String> getAvailableScopes() {
        return scopes;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return request.getOutputStream();
    }

    @Override
    public boolean isResponseCommitted() {
        return request.isResponseCommitted();
    }

    @Override
    public Map<String, String> getParam() {
        return request.getParam();
    }

    @Override
    public Map<String, String[]> getParamValues() {
        return request.getParamValues();
    }

    @Override
    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    @Override
    public Locale getRequestLocale() {
        return request.getRequestLocale();
    }

}
