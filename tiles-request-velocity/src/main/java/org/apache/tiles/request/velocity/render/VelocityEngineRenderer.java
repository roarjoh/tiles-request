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

import java.io.IOException;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.CannotRenderException;
import org.apache.tiles.request.render.Renderer;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.config.ConfigurationUtils;
import org.apache.velocity.tools.config.FactoryConfiguration;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.0.0
 */

public class VelocityEngineRenderer implements Renderer {

    /**
     * The ToolManager object to use.
     */
    private ToolManager toolManager;
    
    /**
     * Constructor.
     *
     * @param velocityView The Velocity view manager.
     */
    public VelocityEngineRenderer(ApplicationContext applicationContext, VelocityEngine velocityEngine) {
        this.toolManager = new ToolManager();
        // TODO configuration
        FactoryConfiguration config = ConfigurationUtils.getGenericTools();
        toolManager.setVelocityEngine(velocityEngine);
        toolManager.configure(config);
    }

    /** {@inheritDoc} */
    @Override
    public void render(String path, Request request) throws IOException {
        if (path == null) {
            throw new CannotRenderException("Cannot dispatch a null path");
        }

        Context context = new VelocityRequestContext(request, toolManager.createContext());

        // get the template
        Template template;
        try {
            template = toolManager.getVelocityEngine().getTemplate(path);
        } catch (Exception e) {
            throw new CannotRenderException(e);
        }

        // merge the template and context into the writer
        template.merge(context, request.getWriter());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRenderable(String path, Request request) {
        return path != null && path.startsWith("/") && path.endsWith(".vm") && toolManager.getVelocityEngine().resourceExists(path);
    }

}
