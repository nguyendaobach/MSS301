package com.mss301.aiservice.service;

import com.mss301.aiservice.dto.request.ExpandNodeRequest;
import com.mss301.aiservice.dto.request.MindMapRequest;
import com.mss301.aiservice.dto.request.RegenerateNodeRequest;
import com.mss301.aiservice.dto.response.MindMapResponse;
import com.mss301.aiservice.entity.MindMapHistory;

import java.util.List;

public interface MindMapService {
    MindMapResponse generateMindMap(MindMapRequest request);
    MindMapResponse.MindMapStructure.MindMapNode regenerateNode(RegenerateNodeRequest request);
    MindMapResponse expandNode(ExpandNodeRequest request);
    List<MindMapHistory> getUserHistory(String userId);
}
