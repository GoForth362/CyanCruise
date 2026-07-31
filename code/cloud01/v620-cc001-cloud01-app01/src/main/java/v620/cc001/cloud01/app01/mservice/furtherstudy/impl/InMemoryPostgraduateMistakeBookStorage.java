package v620.cc001.cloud01.app01.mservice.furtherstudy.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeBookEntryDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeBookPageDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeBookQueryRequest;
import v620.cc001.cloud01.app01.mservice.furtherstudy.PostgraduateMistakeBookStorage;

public class InMemoryPostgraduateMistakeBookStorage implements PostgraduateMistakeBookStorage {
    private final Map<String, PostgraduateMistakeBookEntryDto> entries = new ConcurrentHashMap<String, PostgraduateMistakeBookEntryDto>();

    public PostgraduateMistakeBookEntryDto save(String userId, PostgraduateMistakeBookEntryDto entry) {
        if (entry == null) throw new IllegalArgumentException("错题记录不能为空。");
        String safeUserId = require(userId, "userId");
        LocalDateTime now = LocalDateTime.now();
        if (!text(entry.getMistakeId())) { entry.setMistakeId("pmb-" + UUID.randomUUID().toString().replace("-", "")); entry.setCreatedAt(now); }
        else if (entry.getCreatedAt() == null && entries.containsKey(entry.getMistakeId())) entry.setCreatedAt(entries.get(entry.getMistakeId()).getCreatedAt());
        if (entry.getCreatedAt() == null) entry.setCreatedAt(now);
        entry.setUserId(safeUserId); entry.setUpdatedAt(now); entries.put(entry.getMistakeId(), entry); return entry;
    }

    public PostgraduateMistakeBookPageDto list(String userId, PostgraduateMistakeBookQueryRequest request) {
        String safeUserId = require(userId, "userId"); List<PostgraduateMistakeBookEntryDto> all = new ArrayList<PostgraduateMistakeBookEntryDto>();
        for (PostgraduateMistakeBookEntryDto entry : entries.values()) if (safeUserId.equals(entry.getUserId())) all.add(summary(entry));
        Collections.sort(all, new Comparator<PostgraduateMistakeBookEntryDto>() { public int compare(PostgraduateMistakeBookEntryDto a, PostgraduateMistakeBookEntryDto b) { return b.getUpdatedAt().compareTo(a.getUpdatedAt()); } });
        int offset = request == null || request.getOffset() == null ? 0 : Math.max(0, request.getOffset().intValue());
        int limit = request == null || request.getLimit() == null ? 20 : Math.min(100, Math.max(1, request.getLimit().intValue()));
        int from = Math.min(offset, all.size()), to = Math.min(all.size(), from + limit); PostgraduateMistakeBookPageDto page = new PostgraduateMistakeBookPageDto();
        page.setItems(new ArrayList<PostgraduateMistakeBookEntryDto>(all.subList(from, to))); page.setNextOffset(to); page.setHasMore(to < all.size()); return page;
    }

    public PostgraduateMistakeBookEntryDto load(String userId, String mistakeId) { PostgraduateMistakeBookEntryDto entry = entries.get(require(mistakeId, "mistakeId")); return entry != null && require(userId, "userId").equals(entry.getUserId()) ? entry : null; }
    public boolean delete(String userId, String mistakeId) { String safeUserId = require(userId, "userId"); String safeMistakeId = require(mistakeId, "mistakeId"); PostgraduateMistakeBookEntryDto entry = entries.get(safeMistakeId); return entry != null && safeUserId.equals(entry.getUserId()) && entries.remove(safeMistakeId, entry); }
    private PostgraduateMistakeBookEntryDto summary(PostgraduateMistakeBookEntryDto source) { PostgraduateMistakeBookEntryDto copy = new PostgraduateMistakeBookEntryDto(); copy.setMistakeId(source.getMistakeId()); copy.setUserId(source.getUserId()); copy.setSubject(source.getSubject()); copy.setQuestionText(source.getQuestionText()); copy.setCreatedAt(source.getCreatedAt()); copy.setUpdatedAt(source.getUpdatedAt()); return copy; }
    private String require(String value, String name) { if (!text(value)) throw new IllegalArgumentException(name + "不能为空。"); return value.trim(); }
    private boolean text(String value) { return value != null && value.trim().length() > 0; }
}
