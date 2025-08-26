package org.arkadipta.journalappbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/api")
public class Public {
    @GetMapping("/health")
    public String healthCheck(){
        return "I am healthy !";
    }
}
