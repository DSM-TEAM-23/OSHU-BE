package com.dsm.oshu.common.service;

import com.dsm.oshu.common.presentation.dto.PageResponse;
import java.util.List;

public final class PageUtils {
    private PageUtils() {
    }

    public static <T> PageResponse<T> page(List<T> source, int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        int from = Math.min(normalizedPage * normalizedSize, source.size());
        int to = Math.min(from + normalizedSize, source.size());
        return new PageResponse<>(source.subList(from, to), normalizedPage, normalizedSize, source.size());
    }
}
