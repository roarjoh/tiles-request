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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.CannotRenderException;
import org.apache.tiles.request.render.Renderer;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * FreeMarker renderer for rendering FreeMarker templates as Tiles attributes.
 *
 * @version $Rev$ $Date$
 */
public class FreemarkerRenderer implements Renderer {

    private Configuration configuration;
    private ObjectWrapper wrapper;

    public FreemarkerRenderer() {
    }
    
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        this.wrapper = configuration.getObjectWrapper();
        if (this.wrapper == null) {
            this.wrapper = ObjectWrapper.DEFAULT_WRAPPER;
        }
    }

    @Override
    public void render(String path, Request request) throws IOException {
        try {
            RequestHashModel model = new RequestHashModel(wrapper, request);
            Template template = configuration.getTemplate(path, request.getRequestLocale());
            template.process(model, request.getWriter());
        } catch (FileNotFoundException e) {
            throw new CannotRenderException("Couln't locate Freemarker template " + path, e);
        } catch (TemplateException e) {
            throw new CannotRenderException("Couln't process Freemarker template " + path, e);
        }
    }

    @Override
    public boolean isRenderable(String path, Request request) {
        return path != null && path.startsWith("/") && path.endsWith(".ftl");
    }
}
