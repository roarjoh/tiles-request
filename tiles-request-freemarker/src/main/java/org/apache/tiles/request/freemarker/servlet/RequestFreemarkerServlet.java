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
package org.apache.tiles.request.freemarker.servlet;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.freemarker.RequestTemplateLoader;
import org.apache.tiles.request.freemarker.render.FreemarkerRenderer;
import org.apache.tiles.request.freemarker.render.RequestHashModel;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.servlet.ServletUtil;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class RequestFreemarkerServlet extends FreemarkerServlet {

    private static final String KEY_JSP_TAGLIBS = "JspTaglibs";
    private static final String KEY_INCLUDE_PAGE = "include_page";
    private ApplicationContext applicationContext;

    @Override
    public void init() throws ServletException {
        super.init();
        applicationContext = ServletUtil.getApplicationContext(getServletContext());
        Configuration config = getConfiguration();
        config.setTemplateLoader(new RequestTemplateLoader(applicationContext));
        config.setSharedVariable(KEY_JSP_TAGLIBS, new TaglibFactory(getServletContext()));
        config.setSharedVariable(KEY_INCLUDE_PAGE, new IncludePageDirective());
        FreemarkerRenderer renderer = (FreemarkerRenderer) getServletContext().getAttribute(
                FreemarkerRenderer.class.getName());
        if (renderer == null) {
            renderer = new FreemarkerRenderer();
            getServletContext().setAttribute(FreemarkerRenderer.class.getName(), renderer);
        }
        renderer.setConfiguration(config);
    }

    @Override
    protected Locale deduceLocale(String templatePath, HttpServletRequest request, HttpServletResponse response) {
        return request.getLocale();
    }

    @Override
    protected TemplateModel createModel(ObjectWrapper wrapper, ServletContext servletContext,
            HttpServletRequest request, HttpServletResponse response) throws TemplateModelException {
        RequestHashModel result = new RequestHashModel(wrapper, new ServletRequest(applicationContext, request,
                response));
        return result;
    }

}
