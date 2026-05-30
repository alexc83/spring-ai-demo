package com.ccruce.spring_ai_demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    private ChatClient chatClient;

    private OpenAiImageModel imageModel;

    public ImageController(ChatClient.Builder builder , OpenAiImageModel imageModel) {
        this.chatClient = builder.build();
        this.imageModel = imageModel;
    }

    @GetMapping("/api/image/{prompt}")
    public ImageResponse generateImage(@PathVariable  String prompt) {
         ImagePrompt promptImg = new ImagePrompt(prompt);
         ImageResponse imgResponse =  imageModel.call(promptImg);
         return imgResponse;
    }
}

