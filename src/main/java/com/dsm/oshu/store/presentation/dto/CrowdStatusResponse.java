package com.dsm.oshu.store.presentation.dto;

public record CrowdStatusResponse(String level, String label, Integer estimatedWaitingMinutes) {
}
