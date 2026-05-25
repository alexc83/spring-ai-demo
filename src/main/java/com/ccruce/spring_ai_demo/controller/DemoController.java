package com.ccruce.spring_ai_demo.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private final ChatModel openAIChatModel;

	public DemoController(ChatModel openAIChatModel) {
		this.openAIChatModel = openAIChatModel;
	}

	@GetMapping("/api/{prompt}")
	public ResponseEntity<String> getAnswer(@PathVariable String prompt) {
		
		String response = this.openAIChatModel.call(prompt);
		return new ResponseEntity<>(response, HttpStatus.OK);
	} 




    
}
