package com.ccruce.spring_ai_demo.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class DemoController {

    //private final OpenAiChatModel chatModel;
	private final ChatClient chatClient;
	private final ChatMemory chatMemory = 
		MessageWindowChatMemory.builder().build();
	@Autowired
	@Qualifier("openAiEmbeddingModel")
	private EmbeddingModel embeddingModel;

	/* this is for no memory */
	public DemoController(OpenAiChatModel chatModel) {

		// #1 
		//this.openAiChatModel = openAiChatModel;
		this.chatClient = ChatClient.create(chatModel);

	} 

		/* add chat advisor */
	/* public DemoController(ChatClient.Builder builder) {
		//this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();
		this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();
	} */

	@GetMapping("/api/{message}")
	public ResponseEntity<ChatResponse> getAnswer(@PathVariable String message) {
	
		// #1
		// String response = this.openAiChatModel.call(message);

		// #2 - just answer as String
		/* String response = chatClient.prompt(message)
			.call()
			.content(); */
		
		// get chatresponse object
		ChatResponse chatResponse = chatClient.prompt()
			.user(message)
			//.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "ac-chat"))
			.call()
			.chatResponse();

		return new ResponseEntity<>(chatResponse, HttpStatus.OK);
	} 

	@PostMapping("/api/recommend")
	public ResponseEntity<String> recommend(@RequestParam String genre, 
											@RequestParam String year) {

		String template = """
				You are a movie recommendation system. Please provide a movie recommendation based 
				on the user's preferences. The movie genre is {genre}. The movie release year is {year}.

				Response format would be:
				1.) Movie Name
				2.) Basic plot (2-3 sentences only)
				3.) Main Cast 
				4.) Movie Length in minutes
				5.) IMDB rating
				6.) Rotten tomatoes critic score (with number of reviews)
				7.) Rotten tomatoes audience score (with number of reviews)
				""";
		
		PromptTemplate promptTemplate = new PromptTemplate(template);

		Prompt prompt = promptTemplate.create(Map.of(
			"genre", genre,
			"year", year
		));

		String response = chatClient.prompt(prompt)
			.call()
			.content();
		
		return ResponseEntity.ok(response);
	}

	@PostMapping("/api/embeddings")
	public float[] embeddings(@RequestParam String text) {		
		return embeddingModel.embed(text);
	}
	
	@PostMapping("/api/products")
	public String postMethodName(@RequestParam String query) {
		
		return query;
	}
	
	


    
}
