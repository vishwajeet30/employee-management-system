package com.project.ems.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Forwards React client-side routes to index.html.
 *
 * React Router handles these routes inside the browser.
 * Without this controller, refreshing /employees or /leaves
 * after deployment would produce a Spring Boot 404 response.
 */
@Controller
public class SpaController {

    /**
     * Forwards every known frontend page to React.
     *
     * API routes are not included because they begin with
     * /api/v1 and are handled by REST controllers.
     *
     * @return internal forward to React's index.html
     */
    @GetMapping({
            "/",
            "/login",
            "/dashboard",
            "/employees",
            "/leaves"
    })
    public String forwardReactRoutes() {
        return "forward:/index.html";
    }
}