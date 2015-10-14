package org.marsik.elshelves.backend.app.servlet;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Convert POST requests with _method field to the appropriate
 * _method requests. This allows us to make PUT and DELETE requests
 * from web browser.
 *
 * Source: www.isostech.com/lo/web/thomas/blog/-/blogs/put-and-delete-requests-with-yui3-and-spring-mvc
 */
public class WebFormSupportFilter extends OncePerRequestFilter {
    private static final String METHOD_OVERRIDE_FIELD_NAME = "_method";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String headerValue = request.getParameter(METHOD_OVERRIDE_FIELD_NAME);

        if ("POST".equals(request.getMethod()) && StringUtils.hasLength(headerValue)) {
            String method = headerValue.toUpperCase(Locale.ENGLISH);
            HttpServletRequest wrapper = new HttpMethodRequestWrapper(request, method);
            filterChain.doFilter(wrapper, response);
        }
        else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Simple {@link HttpServletRequest} wrapper that returns the supplied method for
     * {@link HttpServletRequest#getMethod()}.
     */
    private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {

        private final String method;

        public HttpMethodRequestWrapper(HttpServletRequest request, String method) {
            super(request);
            this.method = method;
        }

        @Override
        public String getMethod() {
            return this.method;
        }
    }
}
