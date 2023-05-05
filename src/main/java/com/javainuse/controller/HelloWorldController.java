package com.javainuse.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin()
@Slf4j
public class HelloWorldController {

	@RequestMapping({ "/hello" })
	public String hello() {
		log.info("Hello World log");
		return "Hello World";
	}

}
