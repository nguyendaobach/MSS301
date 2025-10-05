package com.mss301.ownershipservice.service.impl;

import com.mss301.ownershipservice.dto.OwnershipCreateRequest;
import com.mss301.ownershipservice.entity.Ownership;
import com.mss301.ownershipservice.repository.OwnershipRepository;
import com.mss301.ownershipservice.service.OwnershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnershipServiceImpl implements OwnershipService {
    private final OwnershipRepository ownershipRepository;

    @Override
    public List<Ownership> getOwnership(String ownerId, String assetType, String assetId) {
        Specification<Ownership> spec = Specification.where(null);


        spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("ownerId"), ownerId));

        if (assetType != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("assetType"), Ownership.AssetType.valueOf(assetType)));
        }

        if (assetId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("assetId"), assetId));
        }

        return ownershipRepository.findAll(spec);
    }

    @Override
    public void createOwnership(OwnershipCreateRequest request) {
        if (ownershipRepository.existsByOwnerIdAndAssetTypeAndAssetId(request.ownerId(), request.assetType(), request.assetId())) {
            throw new IllegalArgumentException("Ownership already exists");
        }
        Ownership ownership = Ownership.builder()
                .ownerId(request.ownerId())
                .assetType(Ownership.AssetType.valueOf(request.assetType().toUpperCase()))
                .assetId(request.assetId())
                .createdAt(LocalDateTime.now())
                .build();

        ownershipRepository.save(ownership);
    }
}
