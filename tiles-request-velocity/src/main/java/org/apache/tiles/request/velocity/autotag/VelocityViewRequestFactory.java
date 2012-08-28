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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.velocity.VelocityRequest;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.tools.view.ViewContext;

/**
 *
 * @version $Rev$ $Date$
 * @since 1.1.0
 */

public class VelocityViewRequestFactory implements VelocityRequestFactory {

    /** {@inheritDoc} */
    @Override
    public VelocityRequest createRequest(InternalContextAdapter context, Writer writer) {
        Context ctx = context.getInternalUserContext();
        if (ctx instanceof ViewContext) {
            ViewContext viewContext = (ViewContext) ctx;
            HttpServletRequest request = viewContext.getRequest();
            HttpServletResponse response = viewContext.getResponse();
            ServletContext servletContext = viewContext.getServletContext();
            ApplicationContext applicationContext = (ApplicationContext) servletContext
                    .getAttribute(ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE);
            ServletRequest servletRequest = new ServletRequest(applicationContext, request, response);
            VelocityRequest velocityRequest = new VelocityRequest(servletRequest, context, writer);
            return velocityRequest;
        } else {
            return null;
        }
    }

}
