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
package org.apache.tiles.request.velocity.autotag;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.autotag.core.runtime.AutotagRuntime;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.velocity.VelocityRequest;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.ASTMap;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * A Runtime for implementing Velocity Directives.
 */
public class VelocityAutotagRuntime extends Directive implements AutotagRuntime<Request> {

    private static Collection<VelocityRequestFactory> requestFactories;
    static {
        requestFactories = new ArrayList<VelocityRequestFactory>();
        String[] requestFactoryNames = { //
        "org.apache.tiles.request.velocity.autotag.VelocityViewRequestFactory", //
                "org.apache.tiles.request.velocity.autotag.VelocityWrappedRequestFactory", //
        };
        for (String className : requestFactoryNames) {
            try {
                requestFactories.add((VelocityRequestFactory) Class.forName(className).newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private InternalContextAdapter context;
    private Writer writer;
    private Node node;
    private Map<String, Object> params;

    /** {@inheritDoc} */
    @Override
    public Request createRequest() {
        for (VelocityRequestFactory factory : requestFactories) {
            VelocityRequest result = factory.createRequest(context, writer);
            if (result != null) {
                return result;
            }
        }
        throw new IllegalArgumentException("Cannot create a Request from the velocity context");
    }

    /** {@inheritDoc} */
    @Override
    public ModelBody createModelBody() {
        ASTBlock block = (ASTBlock) node.jjtGetChild(1);
        return new VelocityModelBody(context, block, writer);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String name, Class<T> type, T defaultValue) {
        if (params == null) {
            ASTMap astMap = (ASTMap) node.jjtGetChild(0);
            params = (Map<String, Object>) astMap.value(context);
        }
        T result = (T) params.get(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int getType() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) {
        this.context = context;
        this.writer = writer;
        this.node = node;
        return false;
    }

}
