package com.mss301.aiservice.service;

import com.mss301.aiservice.dto.RetrievedChunkDto;
import com.mss301.aiservice.dto.request.RetrievalRequest;
import com.mss301.aiservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@FeignClient(name = "vector-service", url = "${vector.service.url}")
public interface VectorServiceClient {

    @PostMapping("/vectors/retrieve")
    ApiResponse<List<RetrievedChunkDto>> retrieveChunks(@RequestBody RetrievalRequest request);
}