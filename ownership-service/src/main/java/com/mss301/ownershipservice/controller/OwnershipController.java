package com.mss301.ownershipservice.controller;

import com.mss301.ownershipservice.entity.Ownership;
import com.mss301.ownershipservice.service.OwnershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ownerships")
@RequiredArgsConstructor
public class OwnershipController {
    private final OwnershipService ownershipService;

    @GetMapping
    public ResponseEntity<List<Ownership>> getOwnerships(
            @RequestParam String ownerId,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String assetId) {
        return ResponseEntity.ok(ownershipService.getOwnership(ownerId, assetType, assetId));
    }

    @PostMapping
    public ResponseEntity<Void> createOwnership(@RequestBody com.mss301.ownershipservice.dto.OwnershipCreateRequest request) {
        ownershipService.createOwnership(request);
        return ResponseEntity.created(null).build();
    }

}
