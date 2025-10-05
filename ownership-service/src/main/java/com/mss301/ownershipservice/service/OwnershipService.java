package com.mss301.ownershipservice.service;

import com.mss301.ownershipservice.dto.OwnershipCreateRequest;
import com.mss301.ownershipservice.entity.Ownership;

import java.util.List;

public interface OwnershipService {

    List<Ownership> getOwnership(String ownerId, String assetType, String assetId);

    void createOwnership(OwnershipCreateRequest request);
}
