package com.ccruce.spring_ai_demo.utils;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.List;

@Component
public class DataInitializer {

    private final VectorStore vectorStore;


    public DataInitializer(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }


    @PostConstruct
    public void initData() {
        TextReader textReader = new TextReader(new ClassPathResource("product_data.txt"));

        TokenTextSplitter splitter = TokenTextSplitter
                .builder()
                .withChunkSize(500)
                .build();

        List<Document> splitDoc = splitter.split(textReader.get());

        vectorStore.add(splitDoc);
    }
    
}
