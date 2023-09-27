package com.n3lx.minidrive.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/api/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }

}
