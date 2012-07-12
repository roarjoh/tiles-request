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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.tiles.request.ApplicationContext;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.0.0
 */

public class FileRequest extends AbstractMapRequest {

    private File file;
    private OutputStream outputStream;
    private OutputStreamWriter writer;
    private PrintWriter printWriter;
    private String encoding;

    public FileRequest(ApplicationContext context, File file, String encoding) {
        super(context);
        this.file = file;
        this.encoding = encoding;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if(outputStream == null) {
            outputStream = new FileOutputStream(file);
        }
        return outputStream;
    }

    @Override
    public Writer getWriter() throws IOException {
        if(writer == null) {
            writer = new OutputStreamWriter(getOutputStream(), encoding);
        }
        return writer;
    }

    @Override
    public PrintWriter getPrintWriter() throws IOException {
        if(printWriter == null) {
            printWriter = new PrintWriter(getWriter());
        }
        return printWriter;
    }

    @Override
    public boolean isResponseCommitted() {
        return outputStream != null;
    }

}
