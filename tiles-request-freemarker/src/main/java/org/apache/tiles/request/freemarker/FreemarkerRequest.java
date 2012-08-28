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
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.NotAvailableFeatureException;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.attribute.Addable;
import org.apache.tiles.request.attribute.AttributeExtractor;
import org.apache.tiles.request.attribute.HasKeys;
import org.apache.tiles.request.collection.ReadOnlyEnumerationMap;
import org.apache.tiles.request.collection.ScopeMap;

import freemarker.core.Environment;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.utility.DeepUnwrap;

/**
 * The FreeMarker-specific request context.
 *
 * @version $Rev$ $Date$
 */
public class FreemarkerRequest implements Request {

    public static final String SCOPE_APPLICATION = "application";
    public static final String SCOPE_REQUEST = "request";
    public static final String SCOPE_PAGE = "page";

    /**
     * the ApplicationContext.
     */
    private ApplicationContext applicationContext;

    /**
     * The FreeMarker current environment.
     */
    private Environment env;

    /**
     * The request scope map.
     */
    private Map<String, Object> requestScope;

    /**
     * The page scope map.
     */
    private Map<String, Object> pageScope;

    /**
     * Constructor.
     *
     * @param enclosedRequest
     *            The request that exposes non-FreeMarker specific properties
     * @param env
     *            The FreeMarker environment.
     */
    public FreemarkerRequest(ApplicationContext applicationContext, Environment env) {
        this.applicationContext = applicationContext;
        this.env = env;
    }

    /**
     * Returns the environment object.
     *
     * @return The environment.
     */
    public Environment getEnvironment() {
        return env;
    }

    /** {@inheritDoc} */
    @Override
    public Locale getRequestLocale() {
        return env.getLocale();
    }

    /**
     * Returns the page scope.
     *
     * @return The page scope.
     */
    public Map<String, Object> getRequestScope() {
        if (requestScope == null) {
            TemplateHashModel envDataModel = env.getDataModel();
            if (envDataModel instanceof TemplateHashModelEx) {
                requestScope = new ReadOnlyEnumerationMap<Object>(new FreemarkerExtractor(
                        (TemplateHashModelEx) envDataModel));
            } else {
                return Collections.<String, Object> emptyMap();
            }
        }
        return requestScope;
    }

    /**
     * Returns the page scope.
     *
     * @return The page scope.
     */
    public Map<String, Object> getPageScope() {
        if (pageScope == null) {
            pageScope = new ScopeMap(new FreemarkerAttributeExtractor(env.getGlobalNamespace()));
        }
        return pageScope;
    }

    @Override
    public List<String> getAvailableScopes() {
        return Arrays.<String> asList(SCOPE_APPLICATION, SCOPE_REQUEST, SCOPE_PAGE);
    }

    /** {@inheritDoc} */
    @Override
    public PrintWriter getPrintWriter() {
        Writer writer = env.getOut();
        if (writer instanceof PrintWriter) {
            return (PrintWriter) writer;
        }
        return new PrintWriter(writer);
    }

    /** {@inheritDoc} */
    @Override
    public Writer getWriter() {
        return env.getOut();
    }

    @Override
    public Map<String, Object> getContext(String scope) {
        if (SCOPE_PAGE.equals(scope)) {
            return getPageScope();
        } else if (SCOPE_REQUEST.equals(scope)) {
            return getRequestScope();
        } else if (SCOPE_APPLICATION.equals(scope)) {
            return applicationContext.getApplicationScope();
        } else {
            throw new IllegalArgumentException(scope + " does not exist. Call getAvailableScopes() first to check.");
        }
    }

    @Override
    public Map<String, String> getHeader() {
        return Collections.<String, String> emptyMap();
    }

    @Override
    public Map<String, String[]> getHeaderValues() {
        return Collections.<String, String[]> emptyMap();
    }

    @Override
    public Addable<String> getResponseHeaders() {
        return new Addable<String>() {
            @Override
            public void setValue(String key, String value) {
                throw new UnsupportedOperationException("Freemarker does not support response headers");
            }
        };
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new NotAvailableFeatureException("Freemarker does not support binary output");
    }

    @Override
    public boolean isResponseCommitted() {
        // we can't test for it, so we take a conservative stance.
        return true;
    }

    @Override
    public Map<String, String> getParam() {
        return Collections.<String, String> emptyMap();
    }

    @Override
    public Map<String, String[]> getParamValues() {
        return Collections.<String, String[]> emptyMap();
    }

    @Override
    public boolean isUserInRole(String role) {
        return false; // there is no notion of role in freemarker
    }

    private static class FreemarkerExtractor implements HasKeys<Object> {
        private TemplateHashModelEx source;

        /**
         * @param source
         */
        public FreemarkerExtractor(TemplateHashModelEx source) {
            this.source = source;
        }

        @Override
        public Enumeration<String> getKeys() {
            try {
                ArrayList<String> keys = new ArrayList<String>(source.size());
                for (TemplateModelIterator it = source.keys().iterator(); it.hasNext();) {
                    keys.add((String) DeepUnwrap.unwrap(it.next()));
                }
                return Collections.enumeration(keys);
            } catch (TemplateModelException e) {
                throw new FreemarkerRequestException("Cannot access page scope", e);
            }
        }

        @Override
        public Object getValue(String key) {
            try {
                return DeepUnwrap.unwrap(source.get(key));
            } catch (TemplateModelException e) {
                throw new FreemarkerRequestException("cannot access the data model", e);
            }
        }
    }

    private static class FreemarkerAttributeExtractor extends FreemarkerExtractor implements AttributeExtractor {
        private SimpleHash source;

        /**
         * @param source
         */
        public FreemarkerAttributeExtractor(SimpleHash source) {
            super(source);
            this.source = source;
        }

        @Override
        public void removeValue(String key) {
            source.remove(key);
        }

        @Override
        public void setValue(String key, Object value) {
            source.put(key, value);
        }
    }

}
