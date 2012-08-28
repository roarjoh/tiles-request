package org.apache.tiles.request.basic;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.tiles.request.ApplicationContext;

public class StringRequest extends AbstractMapRequest {

    public StringRequest(ApplicationContext context) {
        super(context);
        writer = new StringWriter();
        printWriter = new PrintWriter(writer);
    }

    private StringWriter writer;
    private PrintWriter printWriter;

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Cannot render binary data into a string");
    }

    @Override
    public Writer getWriter() throws IOException {
        return writer;
    }

    @Override
    public PrintWriter getPrintWriter() throws IOException {
        return printWriter;
    }

    @Override
    public boolean isResponseCommitted() {
        return writer.getBuffer().length() > 0;
    }

    public String getOutput() {
        return writer.toString();
    }
}
