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

package org.apache.tiles.request.velocity.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.tiles.request.Request;
import org.apache.velocity.context.Context;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.0.0
 */

public class VelocityRequestContext implements Context {

    private Request request;
    private Context context;
    private List<String> scopes;

    public VelocityRequestContext(Request request, Context context) {
        this.request = request;
        this.context = context;
        scopes = new ArrayList<String>();
        boolean requestScopeFound = false;
        for (String scope : request.getAvailableScopes()) {
            if (Request.REQUEST_SCOPE.equals(scope)) {
                requestScopeFound = true;
            }
            if (requestScopeFound) {
                scopes.add(scope);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object put(String key, Object value) {
        return context.put(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public Object get(String key) {
        if(Request.class.getName().equals(key)) {
            return request;
        }
        Object result = context.get(key);
        for (String scope : scopes) {
            if (result != null) {
                break;
            } else {
                result = request.getContext(scope).get(key);
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsKey(Object key) {
        boolean result = context.containsKey(key);
        for (String scope : scopes) {
            if (!result) {
                break;
            } else {
                result = request.getContext(scope).containsKey(key);
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public Object[] getKeys() {
        HashSet<Object> result = new HashSet<Object>();
        result.addAll(Arrays.asList(context.getKeys()));
        for (String scope : scopes) {
            result.addAll(request.getContext(scope).keySet());
        }
        return result.toArray();
    }

    /** {@inheritDoc} */
    @Override
    public Object remove(Object key) {
        return context.remove(key);
    }

}
