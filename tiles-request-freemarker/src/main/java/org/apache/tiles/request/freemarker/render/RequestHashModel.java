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
package org.apache.tiles.request.freemarker.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tiles.request.Request;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class RequestHashModel extends SimpleHash {
    private static final long serialVersionUID = -3374812539993708686L;
    private final ObjectWrapper wrapper;
    private final Request request;
    private final Map<String, Object> unlistedModels = new HashMap<String, Object>();

    public static final String KEY_REQUEST_PARAMETERS = "RequestParameters";

    /**
     * Creates a new instance of RequestHashModel for handling a single Request.
     * 
     * @param wrapper the object wrapper to use
     * @param request the Request being processed
     * @throws TemplateModelException if the model cannot be created
     */
    public RequestHashModel(ObjectWrapper wrapper, Request request) throws TemplateModelException {
        this.wrapper = wrapper;
        this.request = request;
        for (String scope : request.getAvailableScopes()) {
            String key = scope.substring(0, 1).toUpperCase() + scope.substring(1);
            unlistedModels.put(key, request.getContext(scope));
        }
        unlistedModels.put(KEY_REQUEST_PARAMETERS, request.getParam());
        unlistedModels.put(Request.class.getName(), request);
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        // Lookup in page scope
        TemplateModel model = super.get(key);
        if (model != null) {
            return model;
        }

        // Look in unlisted models
        Object target = unlistedModels.get(key);
        if (target != null) {
            return wrapper.wrap(target);
        }

        // Lookup in request scopes in reverse order
        List<String> scopes = request.getAvailableScopes();
        for (int i = scopes.size() - 1; i >= 0; --i) {
            String scope = scopes.get(i);
            target = request.getContext(scope).get(key);
            if (target != null) {
                return wrapper.wrap(target);
            }
        }

        // return wrapper's null object (probably null).
        return wrapper.wrap(null);
    }
}
