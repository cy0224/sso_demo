package org.example.app.controller;

import org.example.common.entity.ResponseData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class AppController {
    @GetMapping
    public ResponseData<String> app() {
        return ResponseData.success("访问应用1");
    }
}
