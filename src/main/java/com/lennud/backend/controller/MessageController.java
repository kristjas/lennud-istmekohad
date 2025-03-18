package com.lennud.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
@GetMapping("/hello")
    public String hello(){
        return "Hello World this is the test message";
    }
}
