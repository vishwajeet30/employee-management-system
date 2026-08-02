package com.project.ems.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles direct browser requests for React routes.
 *
 * React Router normally changes pages inside the browser.
 * However, directly opening or refreshing /dashboard,
 * /employees or /leaves sends the request to Spring Boot.
 *
 * This controller forwards those requests to React's
 * compiled index.html file.
 */
@Controller
public class SpaController {

    /**
     * Forwards supported frontend routes to index.html.
     *
     * The FORWARD dispatcher type is permitted inside
     * SecurityConfig.
     *
     * @return internal forward to React index.html
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