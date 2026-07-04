package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.CareerResourceCardDto;
import v620.cc001.cloud01.app01.mservice.storage.AdminGovernanceStorage;
import v620.cc001.cloud01.app01.mservice.storage.CareerResourceStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Adapts administrator-managed content into the user-facing resource feed.
 */
public class AdminContentCareerResourceStorage implements CareerResourceStorage {

    private final AdminGovernanceStorage adminStorage;
    private final CareerResourceStorage fallbackStorage;

    public AdminContentCareerResourceStorage(AdminGovernanceStorage adminStorage,
                                             CareerResourceStorage fallbackStorage) {
        this.adminStorage = adminStorage;
        this.fallbackStorage = fallbackStorage;
    }

    public List<CareerResourceCardDto> listCards() {
        List<CareerResourceCardDto> out = new ArrayList<CareerResourceCardDto>();
        Set<String> ids = new LinkedHashSet<String>();

        List<AdminContentItemDto> content = adminStorage == null
                ? Collections.<AdminContentItemDto>emptyList()
                : adminStorage.listContent(null);
        Collections.sort(content, new Comparator<AdminContentItemDto>() {
            public int compare(AdminContentItemDto left, AdminContentItemDto right) {
                int pinned = bool(right == null ? null : right.getPinned())
                        - bool(left == null ? null : left.getPinned());
                if (pinned != 0) {
                    return pinned;
                }
                LocalDateTime rightTime = right == null ? null : right.getPublishedAt();
                LocalDateTime leftTime = left == null ? null : left.getPublishedAt();
                if (rightTime == null && leftTime == null) {
                    return 0;
                }
                if (rightTime == null) {
                    return 1;
                }
                if (leftTime == null) {
                    return -1;
                }
                return rightTime.compareTo(leftTime);
            }
        });
        for (AdminContentItemDto item : content) {
            String contentId = item == null ? null : item.getContentId();
            boolean reserved = hasText(contentId) && ids.add(contentId);
            CareerResourceCardDto card = toCard(item);
            if (card != null && (reserved || ids.add(card.getId()))) {
                out.add(card);
            }
        }

        if (fallbackStorage != null) {
            List<CareerResourceCardDto> fallback = fallbackStorage.listCards();
            for (CareerResourceCardDto card : fallback) {
                if (card != null && ids.add(card.getId())) {
                    out.add(card);
                }
            }
        }
        return out;
    }

    private CareerResourceCardDto toCard(AdminContentItemDto item) {
        if (item == null || Boolean.TRUE.equals(item.getHidden()) || !hasText(item.getTitle())) {
            return null;
        }
        CareerResourceCardDto card = new CareerResourceCardDto();
        card.setId(firstText(item.getContentId(), "admin-content-" + Math.abs(item.getTitle().hashCode())));
        card.setType(resourceType(item.getType()));
        card.setTitle(item.getTitle());
        card.setSummary(item.getSummary());
        card.setBody(item.getSummary());
        card.setCategory(item.getCategory());
        card.setKeyword(item.getCategory());
        card.setSourceUrl(item.getSourceUrl());
        card.setImageUrl(item.getImageUrl());
        card.setAuthor("CyanCruise");
        card.setPublishedAt(item.getPublishedAt());
        return card;
    }

    private String resourceType(String type) {
        String normalized = type == null ? "" : type.trim().toUpperCase();
        if ("VIDEO".equals(normalized)) {
            return "video";
        }
        if ("ARTICLE".equals(normalized)) {
            return "article";
        }
        if ("CAREER_PATH".equals(normalized) || "CAREER-PATH".equals(normalized)) {
            return "career-path";
        }
        return "consultation";
    }

    private static int bool(Boolean value) {
        return Boolean.TRUE.equals(value) ? 1 : 0;
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first.trim() : second;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
