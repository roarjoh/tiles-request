package org.apache.tiles.request.spring;

import java.util.Locale;

import org.apache.tiles.request.DefaultRequestWrapper;
import org.apache.tiles.request.Request;

public class SpringRequest extends DefaultRequestWrapper {

    private Locale locale;

    protected SpringRequest(Request context, Locale locale) {
        super(context);
        this.locale = locale;
    }

    /** {@inheritDoc} */
    @Override
    public Locale getRequestLocale() {
        return locale;
    }
}