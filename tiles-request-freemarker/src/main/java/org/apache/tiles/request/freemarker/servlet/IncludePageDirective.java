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

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.request.Request;
import org.apache.tiles.request.RequestWrapper;
import org.apache.tiles.request.servlet.ServletRequest;

import freemarker.core.Environment;
import freemarker.core.InvalidReferenceException;
import freemarker.ext.servlet.IncludePage;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.DeepUnwrap;

public class IncludePageDirective implements TemplateDirectiveModel {

    @Override
    @SuppressWarnings("unchecked")
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        Request request = (Request) DeepUnwrap.unwrap(env.getVariable(Request.class.getName()));
        while (request instanceof RequestWrapper) {
            request = ((RequestWrapper) request).getWrappedRequest();
        }
        if (request instanceof ServletRequest) {
            HttpServletRequest httpRequest = ((ServletRequest) request).getRequest();
            HttpServletResponse httpResponse = ((ServletRequest) request).getResponse();
            IncludePage includePage = new IncludePage(httpRequest, httpResponse);
            includePage.execute(env, params, loopVars, body);
        } else {
            throw new InvalidReferenceException("No servlet environment", env);
        }
    }

}
