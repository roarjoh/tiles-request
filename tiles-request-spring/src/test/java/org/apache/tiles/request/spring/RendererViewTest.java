package org.apache.tiles.request.spring;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.Renderer;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.RequestContext;

public class RendererViewTest {
    private RendererView testTarget;
    private ApplicationContext applicationContext;
    private WebApplicationContext springContext;
    private Renderer renderer;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;
    private Map<String, Object> model;
    private String path;
    private String contentType;
    private Locale locale;

    @Before
    public void setUp() {
        applicationContext = createMock(ApplicationContext.class);
        springContext = createMock(WebApplicationContext.class);
        renderer = createMock(Renderer.class);
        request = createMock(HttpServletRequest.class);
        response = createMock(HttpServletResponse.class);
        servletContext = createMock(ServletContext.class);
        model = new HashMap<String, Object>();
        model.put("modelAttribute", "modelValue");
        path = "/template.test";
        contentType = "application/test";
        locale = Locale.ITALY;
        expect(servletContext.getInitParameter(anyObject(String.class))).andReturn(null).anyTimes();
        expect(springContext.getServletContext()).andReturn(servletContext).anyTimes();
        expect(request.getAttribute(RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE)).andReturn(springContext)
                .anyTimes();
        expect(request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE)).andReturn(null).anyTimes();
        expect(request.getLocale()).andReturn(Locale.ROOT).anyTimes(); // specifically a different locale
        expect(response.getContentType()).andReturn(null);
        response.setContentType(contentType);
        testTarget = new RendererView(applicationContext, renderer, path, locale);
    }

    @Test
    public void testRender() throws Exception {
        request.setAttribute("modelAttribute", "modelValue");
        request.setAttribute(eq("springMacroRequestContext"), anyObject(RequestContext.class));
        Capture<Request> renderableRequest = new Capture<Request>();
        expect(renderer.isRenderable(eq(path), and(isA(Request.class), capture(renderableRequest)))).andReturn(true);
        Capture<Request> renderRequest = new Capture<Request>();
        renderer.render(eq(path), and(isA(Request.class), capture(renderRequest)));
        replay(applicationContext, springContext, renderer, request, response, servletContext);
        testTarget.setApplicationContext(springContext);
        testTarget.setContentType(contentType);
        testTarget.render(model, request, response);
        assertEquals(renderableRequest.getValue().getRequestLocale(), locale);
        assertSame("isRenderable and render received different requests", renderableRequest.getValue(),
                renderRequest.getValue());
        verify(applicationContext, springContext, renderer, request, response, servletContext);
    }

    @Test
    public void testRenderNoAttributes() throws Exception {
        Capture<Request> renderableRequest = new Capture<Request>();
        expect(renderer.isRenderable(eq(path), and(isA(Request.class), capture(renderableRequest)))).andReturn(true);
        Capture<Request> renderRequest = new Capture<Request>();
        renderer.render(eq(path), and(isA(Request.class), capture(renderRequest)));
        replay(applicationContext, springContext, renderer, request, response, servletContext);
        testTarget.setApplicationContext(springContext);
        testTarget.setContentType(contentType);
        testTarget.setExposeModelInRequest(false);
        testTarget.render(model, request, response);
        assertEquals(renderableRequest.getValue().getRequestLocale(), locale);
        assertSame("isRenderable and render received different requests", renderableRequest.getValue(),
                renderRequest.getValue());
        verify(applicationContext, springContext, renderer, request, response, servletContext);
    }

    @Test
    public void testNotRenderable() throws Exception {
        expect(renderer.isRenderable(eq(path), isA(Request.class))).andReturn(false);
        replay(applicationContext, springContext, renderer, request, response, servletContext);
        testTarget.setApplicationContext(springContext);
        testTarget.setContentType(contentType);
        testTarget.setExposeModelInRequest(false);
        testTarget.render(model, request, response);
        verify(applicationContext, springContext, renderer, request, response, servletContext);
    }
}
