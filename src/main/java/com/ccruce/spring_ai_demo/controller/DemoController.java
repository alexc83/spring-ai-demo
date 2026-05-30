package com.ccruce.spring_ai_demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class DemoController {

    //private final OpenAiChatModel chatModel;
	private ChatClient chatClient;
	private final ChatMemory chatMemory = 
		MessageWindowChatMemory.builder().build();
	@Autowired
	@Qualifier("openAiEmbeddingModel")
	private EmbeddingModel embeddingModel;
	private VectorStore vectorStore;

	/* this is for no memory */
	public DemoController(OpenAiChatModel chatModel, VectorStore vectorStore) {

		// #1 
		//this.openAiChatModel = openAiChatModel;
		this.chatClient = ChatClient.create(chatModel);
		this.vectorStore = vectorStore;

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
	public ResponseEntity<List<Document>> getProducts(@RequestParam String query) {

		List<Document> results = new ArrayList<>();
		// results = vectorStore.similaritySearch(query);
		results = vectorStore.similaritySearch(SearchRequest
				.builder()
				.query(query)
				.topK(2)
				.build());

		return ResponseEntity.ok(results);

		}

		@GetMapping("/api/answer")
		public ResponseEntity<String> getAnswerRag(@RequestParam String query) {

		String response =  chatClient
					.prompt(query)
					.advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
					.call()
					.content();
		return ResponseEntity.ok(response);
		}
	}
	
	



