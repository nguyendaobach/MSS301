package com.mss301.documentservice.service;

import com.mss301.documentservice.dto.request.EmbedRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@FeignClient(name = "vector-service", url = "${vector.service.url}")
public interface VectorServiceClient {

    @PostMapping("/api/vectors/embed-and-store")
    void embedAndStore(@RequestBody EmbedRequest request);

    @DeleteMapping("/api/vectors/document/{documentId}")
    void deleteDocument(@PathVariable String documentId);
}
