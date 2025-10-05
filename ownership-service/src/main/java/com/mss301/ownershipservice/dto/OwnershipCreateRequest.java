package com.mss301.ownershipservice.dto;

public record OwnershipCreateRequest(
        String ownerId,
        String assetType,
        String assetId
) {
}
