package v620.cc001.cloud01.app01.mservice.furtherstudy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeBookEntryDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeBookPageDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeBookQueryRequest;
import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryPostgraduateMistakeBookStorage;

class PostgraduateMistakeBookStorageTest {
    @Test
    void listsAllPagesAndKeepsUsersIsolated() {
        InMemoryPostgraduateMistakeBookStorage storage = new InMemoryPostgraduateMistakeBookStorage();
        save(storage, "user-a", "数学", "题目一"); save(storage, "user-a", "英语", "题目二"); save(storage, "user-a", "政治", "题目三"); save(storage, "user-b", "408", "他人的题目");
        PostgraduateMistakeBookQueryRequest first = new PostgraduateMistakeBookQueryRequest(); first.setLimit(Integer.valueOf(2)); first.setOffset(Integer.valueOf(0));
        PostgraduateMistakeBookPageDto firstPage = storage.list("user-a", first);
        assertEquals(2, firstPage.getItems().size()); assertTrue(firstPage.isHasMore());
        PostgraduateMistakeBookQueryRequest second = new PostgraduateMistakeBookQueryRequest(); second.setLimit(Integer.valueOf(2)); second.setOffset(Integer.valueOf(firstPage.getNextOffset()));
        PostgraduateMistakeBookPageDto secondPage = storage.list("user-a", second);
        assertEquals(1, secondPage.getItems().size()); assertFalse(secondPage.isHasMore());
        assertNull(storage.load("user-b", firstPage.getItems().get(0).getMistakeId()));
    }

    @Test
    void deletesOnlyTheCurrentUsersMistake() {
        InMemoryPostgraduateMistakeBookStorage storage = new InMemoryPostgraduateMistakeBookStorage();
        save(storage, "user-a", "408", "自己的错题"); save(storage, "user-b", "英语", "他人的错题");
        PostgraduateMistakeBookQueryRequest request = new PostgraduateMistakeBookQueryRequest(); request.setLimit(Integer.valueOf(20)); request.setOffset(Integer.valueOf(0));
        String ownId = storage.list("user-a", request).getItems().get(0).getMistakeId();
        String otherId = storage.list("user-b", request).getItems().get(0).getMistakeId();
        assertFalse(storage.delete("user-a", otherId));
        assertTrue(storage.delete("user-a", ownId));
        assertNull(storage.load("user-a", ownId));
        assertTrue(storage.load("user-b", otherId) != null);
    }
    private void save(InMemoryPostgraduateMistakeBookStorage storage, String userId, String subject, String question) {
        PostgraduateMistakeBookEntryDto entry = new PostgraduateMistakeBookEntryDto(); entry.setSubject(subject); entry.setQuestionText(question); entry.setWrongAnswer("错误答案"); entry.setResultJson("{}"); storage.save(userId, entry);
    }
}
