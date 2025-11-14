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

    @Value("${spring.ai.openai.api-key}")
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
        log.info("Starting mindmap generation for user: {}", request.getUserId());

        List<RetrievedChunkDto> relevantChunks = Collections.emptyList();
        String context;

        // Check if user wants to use RAG (with specific documents)
        if (request.getDocumentIds() != null && !request.getDocumentIds().isEmpty()) {
            log.info("Using RAG with specific documents: {}", request.getDocumentIds());

            // Step 1: RAG - Retrieve relevant context from user's documents
            relevantChunks = retrieveContext(
                    request.getPrompt(),
                    request.getUserId(),
                    request.getDocumentIds()
            );

            log.info("Retrieved {} relevant chunks", relevantChunks.size());

            // Step 2: Build context from retrieved chunks
            context = buildContextFromChunks(relevantChunks);
        } else {
            log.info("Using pure LLM mode (no RAG) - no documents specified");
            context = "Không có thông tin từ tài liệu. Hãy tạo mindmap dựa trên kiến thức chung.";
        }

        try {
            // Step 3: Build prompts for LLM
            String systemPrompt = buildSystemPrompt();
            String userPrompt = buildUserPrompt(request.getPrompt(), context, request);

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

    private List<RetrievedChunkDto> retrieveContext(String query, String userId, List<String> documentIds) {
        try {
            ApiResponse<List<RetrievedChunkDto>> response = vectorServiceClient.retrieveChunks(
                    RetrievalRequest.builder()
                            .query(query)
                            .userId(userId)
                            .documentIds(documentIds)
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
            và tạo cấu trúc mindmap logic, rõ ràng, dễ hiểu với nội dung chi tiết.
            
            NGUYÊN TẮC QUAN TRỌNG:
            1. CHỈ sử dụng thông tin từ context được cung cấp (nếu có context)
            2. Nếu không có context, sử dụng kiến thức chung để tạo mindmap chất lượng
            3. Tổ chức thông tin theo cấu trúc phân cấp rõ ràng
            4. Mỗi node label phải ngắn gọn, súc tích (tối đa 5-7 từ)
            5. Tạo tối thiểu 3 cấp độ (level) cho mindmap
            6. **QUAN TRỌNG**: Các node lá (node cuối cùng) phải có description CHI TIẾT, cung cấp nội dung cụ thể
            
            QUY TẮC VỀ NỘI DUNG CHI TIẾT:
            - Node gốc (level 0): Label ngắn gọn, description tổng quan
            - Node trung gian (level 1-2): Label ngắn gọn, description giải thích khái niệm
            - **Node lá (level cuối)**: Label ngắn gọn, description CỰC KỲ CHI TIẾT:
              * Giải thích đầy đủ khái niệm
              * Đưa ra ví dụ cụ thể (nếu có trong context)
              * Liệt kê các điểm quan trọng
              * Công thức toán học (nếu có) - viết dưới dạng KaTeX
              * Độ dài description cho node lá: 100-300 từ
            
            QUY TẮC VỀ CÔNG THỨC TOÁN HỌC (KaTeX):
            - Công thức inline: Bọc trong $...$
              Ví dụ: "Công thức $E = mc^2$ cho năng lượng"
            - Công thức block: Bọc trong $$...$$
              Ví dụ: "Phương trình: $$\\frac{-b \\pm \\sqrt{b^2-4ac}}{2a}$$"
            - Sử dụng ký hiệu LaTeX chuẩn:
              * Phân số: \\frac{tử}{mẫu}
              * Căn bậc hai: \\sqrt{x}
              * Mũ: x^2, x^{n+1}
              * Chỉ số dưới: x_i, x_{n-1}
              * Tích phân: \\int, \\sum, \\prod
              * Ký hiệu Hy Lạp: \\alpha, \\beta, \\gamma, \\Delta, \\pi
              * Ma trận: \\begin{bmatrix} a & b \\\\ c & d \\end{bmatrix}
            
            FORMAT OUTPUT - QUAN TRỌNG:
            Trả về ĐÚNG format JSON sau, không thêm text nào khác:
            
            {
              "title": "Tiêu đề chính của mindmap",
              "nodes": [
                {
                  "id": "node_1",
                  "label": "Tên node ngắn gọn",
                  "level": 0,
                  "parent": null,
                  "children": ["node_2", "node_3"],
                  "description": "Mô tả tổng quan về chủ đề chính"
                },
                {
                  "id": "node_2",
                  "label": "Topic chính",
                  "level": 1,
                  "parent": "node_1",
                  "children": ["node_4", "node_5"],
                  "description": "Giải thích về topic này, vai trò trong chủ đề chính"
                },
                {
                  "id": "node_4",
                  "label": "Chi tiết cụ thể",
                  "level": 2,
                  "parent": "node_2",
                  "children": [],
                  "description": "NỘI DUNG CỰC KỲ CHI TIẾT: \\n\\n**Định nghĩa**: [Giải thích đầy đủ khái niệm]\\n\\n**Công thức**: $$công_thức_KaTeX$$\\n\\n**Ví dụ**: [Ví dụ cụ thể]\\n\\n**Ứng dụng**: [Liệt kê các ứng dụng thực tế]\\n\\n**Lưu ý**: [Các điểm quan trọng cần nhớ]"
                }
              ]
            }
            
            LƯU Ý:
            - Level 0: Root node (1 node)
            - Level 1: Main topics (3-5 nodes)
            - Level 2+: Sub-topics với description CHI TIẾT
            - Mỗi node phải có id duy nhất
            - Parent phải tham chiếu đến id của node cha
            - Children là mảng các id của node con
            - Node lá (children rỗng) PHẢI có description dài và chi tiết
            - Sử dụng KaTeX cho TẤT CẢ công thức toán học
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
        if (request.getMaxDepth() != null) {
            prompt.append("Độ sâu tối đa: ").append(request.getMaxDepth()).append(" cấp\n");
        }

        if (request.getMinNodes() != null) {
            prompt.append("Số lượng node tối thiểu: ").append(request.getMinNodes()).append("\n");
        }

        if (request.getFocusAreas() != null && !request.getFocusAreas().isEmpty()) {
            prompt.append("Các khía cạnh cần tập trung: ")
                    .append(String.join(", ", request.getFocusAreas()))
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

    private MindMapResponse.MindMapStructure.MindMapNode parseNodeJson(String json) {
        try {
            return objectMapper.readValue(json, MindMapResponse.MindMapStructure.MindMapNode.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing mindmap node JSON: {}", json, e);
            throw new RuntimeException("Failed to parse mindmap node JSON", e);
        }
    }

    private void saveMindmapHistory(
            MindMapRequest request,
            MindMapResponse.MindMapStructure structure,
            List<RetrievedChunkDto> chunks) {

        try {
            MindMapHistory history = MindMapHistory.builder()
                    .historyId(UUID.randomUUID().toString())
                    .userId(request.getUserId())
                    .prompt(request.getPrompt())
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
        log.info("Regenerating node: {} for user: {}", request.nodeId(), request.userId());

        // Build query: prefer additionalContext, otherwise fallback to nodeId as a hint
        String query = (request.additionalContext() != null && !request.additionalContext().isBlank())
                ? request.additionalContext()
                : "Regenerate content for node: " + request.nodeId();

        String context;

        // Check if user wants to use RAG
        if (request.documentIds() != null && !request.documentIds().isEmpty()) {
            log.info("Using RAG with specific documents for regenerate");
            List<RetrievedChunkDto> relevantChunks = retrieveContext(query, request.userId(), request.documentIds());
            context = buildContextFromChunks(relevantChunks);
        } else {
            log.info("Using pure LLM mode for regenerate (no documents)");
            context = "Không có thông tin từ tài liệu. Hãy tái tạo node dựa trên kiến thức chung.";
        }

        // Build a focused prompt for regenerating a single node
        StringBuilder userPrompt = new StringBuilder();
        userPrompt.append(context).append("\n\n");
        userPrompt.append("Yêu cầu: Tái tạo nội dung cho node có id '")
                .append(request.nodeId())
                .append("'. Hãy trả về một đối tượng JSON duy nhất mô tả node theo định dạng: {\"id\":..., \"label\":..., \"level\":..., \"parent\":..., \"children\":[...], \"description\":... }\\n");
        if (request.additionalContext() != null && !request.additionalContext().isBlank()) {
            userPrompt.append("Thông tin bổ sung: ").append(request.additionalContext()).append("\n");
        }
        userPrompt.append("Chỉ sử dụng thông tin từ context nếu có. Trả về ĐÚNG JSON, không thêm bình luận.");

        String systemPrompt = buildSystemPrompt();
        String llmResponse = callLLM(systemPrompt, userPrompt.toString());

        // Try to parse a single node
        MindMapResponse.MindMapStructure.MindMapNode node = parseNodeJson(llmResponse);

        return node;
    }

    public MindMapResponse expandNode(ExpandNodeRequest request) {
        log.info("Expanding node: {} for user: {}", request.nodeId(), request.userId());

        String query = "Expand node: " + request.nodeId();
        String context;
        List<RetrievedChunkDto> relevantChunks = Collections.emptyList();

        // Check if user wants to use RAG
        if (request.documentIds() != null && !request.documentIds().isEmpty()) {
            log.info("Using RAG with specific documents for expand");
            relevantChunks = retrieveContext(query, request.userId(), request.documentIds());
            context = buildContextFromChunks(relevantChunks);
        } else {
            log.info("Using pure LLM mode for expand (no documents)");
            context = "Không có thông tin từ tài liệu. Hãy mở rộng node dựa trên kiến thức chung.";
        }

        int numChildren = (request.numberOfChildren() != null && request.numberOfChildren() > 0)
                ? request.numberOfChildren()
                : 3;

        StringBuilder userPrompt = new StringBuilder();
        userPrompt.append(context).append("\n\n");
        userPrompt.append("Yêu cầu: Mở rộng node có id '")
                .append(request.nodeId())
                .append("' bằng cách tạo ")
                .append(numChildren)
                .append(" node con phù hợp. Trả về một cấu trúc mindmap JSON (theo định dạng đã quy định) chỉ chứa các node mới và node cha nếu cần.\n");
        userPrompt.append("Chỉ sử dụng thông tin từ context nếu có. Trả về ĐÚNG JSON, không thêm bình luận.");

        String systemPrompt = buildSystemPrompt();
        String llmResponse = callLLM(systemPrompt, userPrompt.toString());

        // Parse the returned structure (may contain multiple nodes)
        MindMapResponse.MindMapStructure structure = parseMindmapJson(llmResponse);
        // Return a MindMapResponse with generated structure and source chunks
        return MindMapResponse.builder()
                .mindMap(structure)
                .sourceChunks(relevantChunks)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    public List<MindMapHistory> getUserHistory(String userId) {
        return historyRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}