package com.dsm.oshu.store.domain;

public enum CrowdLevel {
    RELAXED("여유"), NORMAL("보통"), BUSY("혼잡"), VERY_BUSY("매우 혼잡");

    private final String label;

    CrowdLevel(String label) { this.label = label; }
    public String getLabel() { return label; }
}
