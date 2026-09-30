package com.asiscontrol.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageUtils {

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    private PageUtils() {
    }

    public static Pageable create(
            int page,
            int size,
            String sortBy,
            Sort.Direction direction,
            Set<String> allowedSortFields,
            String defaultSortField
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        String safeSort = allowedSortFields.contains(sortBy) ? sortBy : defaultSortField;
        return PageRequest.of(safePage, safeSize, Sort.by(direction, safeSort));
    }
}
