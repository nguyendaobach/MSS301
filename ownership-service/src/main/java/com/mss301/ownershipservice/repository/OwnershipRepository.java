package com.mss301.ownershipservice.repository;

import com.mss301.ownershipservice.entity.Ownership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnershipRepository extends JpaRepository<Ownership, Integer>,  JpaSpecificationExecutor<Ownership> {
    public boolean existsByOwnerIdAndAssetTypeAndAssetId(String ownerId, String assetType, String assetId);
}
