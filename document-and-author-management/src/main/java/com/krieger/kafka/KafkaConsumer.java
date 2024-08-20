package com.krieger.kafka;

import com.krieger.author.models.AuthorResponse;
import com.krieger.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final DocumentService documentService;

    @KafkaListener(topics = "${kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAuthorResponseFromKafka(AuthorResponse authorResponse) {
        // delete author and all the documents associated with author and update references.
        documentService.updateDocumentReferencesAndDeleteAuthor(authorResponse.getId());
    }

}
