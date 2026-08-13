package org.blinkapp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class AuthTestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content";
    }

    @GetMapping("/user")
    public String userAccess() {
        return "User Content";
    }

    @PreAuthorize("hasRole('DEVELOPER')")
    @GetMapping("/developer")
    public String developerAccess() {
        return "Hello Tom!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminOnly() {
        return "Hello Ellie!";
    }

}
