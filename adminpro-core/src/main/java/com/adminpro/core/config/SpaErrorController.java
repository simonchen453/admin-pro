package com.adminpro.core.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

/**
 * SPA Error Controller
 * Handles 404 errors for Single Page Applications by forwarding to index.html
 * allowing the client-side router to handle the path.
 */
@Component
public class SpaErrorController extends BasicErrorController {

    public SpaErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
        super(errorAttributes, serverProperties.getError());
    }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = getStatus(request);
        if (status == HttpStatus.NOT_FOUND) {
            String path = (String) request.getAttribute("jakarta.servlet.error.request_uri");
            if (path == null) {
                path = request.getRequestURI();
            }

            // Forward to index.html for non-static resources
            // This allows React Router to handle paths like /login, /admin/user, etc.
            // We exclude common static asset extensions to ensure real 404s for missing
            // assets are still returned as 404
            if (!path.matches(".*\\.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot|map|json)$")) {
                return new ModelAndView("forward:/index.html");
            }
        }
        return super.errorHtml(request, response);
    }
}
