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

package org.apache.tiles.request.basic;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.attribute.Addable;
import org.apache.tiles.request.collection.ReadOnlyEnumerationMap;

/**
 *
 * @version $Rev$ $Date$
 * @since 1.1.0
 */

public abstract class AbstractMapRequest implements Request {

    private static final List<String> SCOPES = Arrays.asList(Request.REQUEST_SCOPE, Request.APPLICATION_SCOPE);

    private ApplicationContext context;

    private Map<String, String[]> headerValues;
    private ArrayMapExtractor headerValuesExtractor;
    private Map<String, String[]> responseHeaders;
    private ArrayMapExtractor responseHeadersExtractor;
    private Map<String, String[]> params;
    private ArrayMapExtractor paramsExtractor;
    private Map<String, Object> requestScope;
    private Locale locale;
    private Set<String> roles;

    public AbstractMapRequest(ApplicationContext context) {
        this.context = context;
        this.headerValues = new HashMap<String, String[]>();
        this.headerValuesExtractor = new ArrayMapExtractor(headerValues);
        this.responseHeaders = new HashMap<String, String[]>();
        this.responseHeadersExtractor = new ArrayMapExtractor(responseHeaders);
        this.params = new HashMap<String, String[]>();
        this.paramsExtractor = new ArrayMapExtractor(params);
        this.requestScope = new HashMap<String, Object>();
        this.locale = Locale.ROOT;
        this.roles = new HashSet<String>();
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getHeader() {
        return new ReadOnlyEnumerationMap<String>(headerValuesExtractor);
    }

    public void addHeader(String name, String value) {
        headerValuesExtractor.setValue(name, value);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String[]> getHeaderValues() {
        return Collections.unmodifiableMap(headerValues);
    }

    /** {@inheritDoc} */
    @Override
    public Addable<String> getResponseHeaders() {
        return responseHeadersExtractor;
    }

    public String getResponseHeader(String key) {
        return responseHeadersExtractor.getValue(key);
    }
    
    /** {@inheritDoc} */
    @Override
    public Map<String, Object> getContext(String scope) {
        if("request".equals(scope)) {
            return requestScope;
        }
        else if ("application".equals(scope)) {
            return context.getApplicationScope();
        }
        else {
            throw new IllegalArgumentException("Unknown scope "+scope);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getAvailableScopes() {
        return SCOPES;
    }

    /** {@inheritDoc} */
    @Override
    public ApplicationContext getApplicationContext() {
        return context;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getParam() {
        return new ReadOnlyEnumerationMap<String>(paramsExtractor);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String[]> getParamValues() {
        return Collections.unmodifiableMap(params);
    }
    
    public void setParam(String name, String value) {
        paramsExtractor.setValue(name, value);
    }

    /** {@inheritDoc} */
    @Override
    public Locale getRequestLocale() {
        return locale;
    }

    public void setRequestLocale(Locale locale) {
        this.locale = locale;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isUserInRole(String role) {
        return roles.contains(role);
    }

    public void addRole(String role) {
        roles.add(role);
    }
}
