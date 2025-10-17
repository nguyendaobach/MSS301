package com.mss301.aiservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mss301.aiservice.dto.RetrievedChunkDto;
import com.mss301.aiservice.dto.request.ExpandNodeRequest;
import com.mss301.aiservice.dto.request.MindMapRequest;
import com.mss301.aiservice.dto.request.RegenerateNodeRequest;
import com.mss301.aiservice.dto.request.RetrievalRequest;
import com.mss301.aiservice.dto.response.ApiResponse;
import com.mss301.aiservice.dto.response.MindMapResponse;
import com.mss301.aiservice.entity.MindMapHistory;
import com.mss301.aiservice.repository.MindMapHistoryRepository;
import com.mss301.aiservice.service.MindMapService;
import com.mss301.aiservice.service.VectorServiceClient;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MindMapServiceImpl implements MindMapService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${mindmap.rag.top-k:5}")
    private int ragTopK;

    @Value("${mindmap.llm.model:gpt-4}")
    private String llmModel;

    @Value("${openai.timeout.seconds:120}")
    private int timeoutSeconds;

    private final VectorServiceClient vectorServiceClient;
    private final MindMapHistoryRepository historyRepository;
    private OpenAiService openAiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        // ✅ FIX: Configure OkHttpClient với timeout dài hơn
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(timeoutSeconds))  // 120 seconds for GPT-4
                .callTimeout(Duration.ofSeconds(timeoutSeconds + 10))
                .build();

        openAiService = new OpenAiService(openaiApiKey, Duration.ofSeconds(timeoutSeconds));

        log.info("Mindmap Generation Service initialized with model: {} (timeout: {}s)",
                llmModel, timeoutSeconds);
    }


    public MindMapResponse generateMindMap(MindMapRequest request) {
        log.info("Starting mindmap generation for user: {}", request.userId());

        try {
            // Step 1: RAG - Retrieve relevant context from user's documents
            List<RetrievedChunkDto> relevantChunks = retrieveContext(
                    request.prompt(),
                    request.userId()
            );

            log.info("Retrieved {} relevant chunks", relevantChunks.size());

            // Step 2: Build context from retrieved chunks
            String context = buildContextFromChunks(relevantChunks);

            // Step 3: Build prompts for LLM
            String systemPrompt = buildSystemPrompt();
            String userPrompt = buildUserPrompt(request.prompt(), context, request);

            // Step 4: Call LLM to generate mindmap
            String mindMapJson = callLLM(systemPrompt, userPrompt);
            log.debug("LLM response: {}", mindMapJson);

            // Step 5: Parse JSON response
            MindMapResponse.MindMapStructure structure = parseMindmapJson(mindMapJson);

            // Step 6: Save to history
            saveMindmapHistory(request, structure, relevantChunks);

            // Step 7: Build response
            return MindMapResponse.builder()
                    .mindMap(structure)
                    .sourceChunks(relevantChunks)
                    .generatedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error generating mindmap", e);
            throw new RuntimeException("Failed to generate mindmap", e);
        }
    }

    private List<RetrievedChunkDto> retrieveContext(String query, String userId) {
        try {
            ApiResponse<List<RetrievedChunkDto>> response = vectorServiceClient.retrieveChunks(
                    RetrievalRequest.builder()
                            .query(query)
                            .userId(userId)
                            .topK(ragTopK)
                            .build()
            );
            return response.getData() != null ? response.getData() : List.of();
        } catch (Exception e) {
            log.error("Error retrieving context from vector service", e);
            return Collections.emptyList();
        }
    }

    private String buildContextFromChunks(List<RetrievedChunkDto> chunks) {
        if (chunks.isEmpty()) {
            return "Không có thông tin từ tài liệu của người dùng.";
        }

        StringBuilder context = new StringBuilder();
        context.append("=== THÔNG TIN TỪ TÀI LIỆU CỦA NGƯỜI DÙNG ===\n\n");

        for (int i = 0; i < chunks.size(); i++) {
            RetrievedChunkDto chunk = chunks.get(i);
            context.append(String.format(
                    "[Đoạn %d - Độ liên quan: %.2f - Từ file: %s]\n%s\n\n",
                    i + 1,
                    chunk.score(),
                    chunk.fileName(),
                    chunk.text()
            ));
        }

        return context.toString();
    }

    private String buildSystemPrompt() {
        return """
            Bạn là chuyên gia tạo mindmap chuyên nghiệp. Nhiệm vụ của bạn là phân tích thông tin 
            và tạo cấu trúc mindmap logic, rõ ràng, dễ hiểu.
            
            NGUYÊN TẮC QUAN TRỌNG:
            1. CHỈ sử dụng thông tin từ context được cung cấp
            2. KHÔNG thêm bất kỳ thông tin nào từ kiến thức chung của bạn
            3. Nếu context không đủ thông tin, hãy tạo mindmap dựa trên những gì có
            4. Tổ chức thông tin theo cấu trúc phân cấp rõ ràng
            5. Mỗi node phải ngắn gọn, súc tích (tối đa 5-7 từ)
            6. Tạo tối thiểu 3 cấp độ (level) cho mindmap
            
            FORMAT OUTPUT - QUAN TRỌNG:
            Trả về ĐÚNG format JSON sau, không thêm text nào khác:
            
            {
              "title": "Tiêu đề chính của mindmap",
              "nodes": [
                {
                  "id": "node_1",
                  "label": "Tên node",
                  "level": 0,
                  "parent": null,
                  "children": ["node_2", "node_3"],
                  "description": "Mô tả chi tiết node này (optional)"
                },
                {
                  "id": "node_2",
                  "label": "Node con cấp 1",
                  "level": 1,
                  "parent": "node_1",
                  "children": ["node_4", "node_5"],
                  "description": "Mô tả"
                }
              ]
            }
            
            LƯU Ý:
            - Level 0: Root node (1 node)
            - Level 1: Main topics (3-5 nodes)
            - Level 2+: Sub-topics
            - Mỗi node phải có id duy nhất
            - Parent phải tham chiếu đến id của node cha
            - Children là mảng các id của node con
            """;
    }

    private String buildUserPrompt(
            String userQuery,
            String context,
            MindMapRequest request) {

        StringBuilder prompt = new StringBuilder();

        // Add context
        prompt.append(context).append("\n\n");

        // Add user requirements
        prompt.append("=== YÊU CẦU CỦA NGƯỜI DÙNG ===\n");
        prompt.append("Chủ đề: ").append(userQuery).append("\n\n");

        // Add optional parameters
        if (request.maxDepth() != null) {
            prompt.append("Độ sâu tối đa: ").append(request.maxDepth()).append(" cấp\n");
        }

        if (request.minNodes() != null) {
            prompt.append("Số lượng node tối thiểu: ").append(request.minNodes()).append("\n");
        }

        if (request.focusAreas() != null && !request.focusAreas().isEmpty()) {
            prompt.append("Các khía cạnh cần tập trung: ")
                    .append(String.join(", ", request.focusAreas()))
                    .append("\n");
        }

        prompt.append("\n");
        prompt.append("Hãy tạo mindmap dựa trên thông tin từ tài liệu trên, ");
        prompt.append("tập trung vào yêu cầu của người dùng. ");
        prompt.append("Trả về ĐÚNG format JSON như đã hướng dẫn.");

        return prompt.toString();
    }

    private String callLLM(String systemPrompt, String userPrompt) {
        try {
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(llmModel)
                    .messages(Arrays.asList(
                            new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt),
                            new ChatMessage(ChatMessageRole.USER.value(), userPrompt)
                    ))
                    .temperature(0.7)
                    .maxTokens(2000)
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(request);
            String response = result.getChoices().get(0).getMessage().getContent();

            // Clean response (remove markdown code blocks if present)
            response = response.replaceAll("```json\\n?", "")
                    .replaceAll("```\\n?", "")
                    .trim();

            return response;

        } catch (Exception e) {
            log.error("Error calling LLM", e);
            throw new RuntimeException("Failed to call LLM", e);
        }
    }

    private MindMapResponse.MindMapStructure parseMindmapJson(String json) {
        try {
            return objectMapper.readValue(json, MindMapResponse.MindMapStructure.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing mindmap JSON: {}", json, e);
            throw new RuntimeException("Failed to parse mindmap JSON", e);
        }
    }

    private void saveMindmapHistory(
            MindMapRequest request,
            MindMapResponse.MindMapStructure structure,
            List<RetrievedChunkDto> chunks) {

        try {
            MindMapHistory history = MindMapHistory.builder()
                    .historyId(UUID.randomUUID().toString())
                    .userId(request.userId())
                    .prompt(request.prompt())
                    .mindMapJson(objectMapper.writeValueAsString(structure))
                    .sourceDocuments(chunks.stream()
                            .map(RetrievedChunkDto::documentId)
                            .distinct()
                            .collect(Collectors.joining(",")))
                    .createdAt(LocalDateTime.now())
                    .build();

            historyRepository.save(history);
            log.info("Mindmap history saved: {}", history.getHistoryId());

        } catch (Exception e) {
            log.error("Error saving mindmap history", e);
            // Don't throw - history is not critical
        }
    }

    public MindMapResponse.MindMapStructure.MindMapNode regenerateNode(RegenerateNodeRequest request) {
        // Implementation for regenerating a specific node
        // This would call LLM with focus on that specific node
        log.info("Regenerating node: {}", request.nodeId());
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public MindMapResponse expandNode(ExpandNodeRequest request) {
        // Implementation for expanding a node with more children
        log.info("Expanding node: {}", request.nodeId());
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<MindMapHistory> getUserHistory(String userId) {
        return historyRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}