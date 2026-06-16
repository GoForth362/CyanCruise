package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CareerResourceCardDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeded resource cards for the first Cosmic migration slice.
 */
public class InMemoryCareerResourceStorage implements CareerResourceStorage {

    private final List<CareerResourceCardDto> cards = new ArrayList<CareerResourceCardDto>();

    public InMemoryCareerResourceStorage() {
        LocalDateTime now = LocalDateTime.now();
        cards.add(card("article-resume-001", "article", "Resume evidence checklist",
                "Use target-role evidence to improve resume bullets.", "resume", "resume",
                "/cyancruise/resources/resume-evidence", now.minusDays(1)));
        cards.add(card("video-interview-001", "video", "Mock interview practice loop",
                "Practice answer structure before a real interview.", "interview", "interview",
                "https://www.bilibili.com/video/BV1cyancruise", now.minusDays(2)));
        cards.get(cards.size() - 1).setBvid("BV1cyancruise");
        cards.get(cards.size() - 1).setDurationSec(Integer.valueOf(480));
        cards.add(card("tip-plan-001", "consultation", "This week career focus",
                "Pick one target role, one resume improvement and one interview drill.", "plan", "plan",
                "/cyancruise/resources/weekly-focus", now.minusDays(3)));
        CareerResourceCardDto path = card("path-software", "career_path", "Software Engineer path",
                "Core route for backend, web, data and AI application roles.", "career-path", "software",
                "index.html#career-plan", now.minusDays(4));
        path.setCareerPathId("software-engineer");
        cards.add(path);
    }

    public InMemoryCareerResourceStorage(List<CareerResourceCardDto> cards) {
        if (cards != null) {
            this.cards.addAll(cards);
        }
    }

    public List<CareerResourceCardDto> listCards() {
        return new ArrayList<CareerResourceCardDto>(cards);
    }

    private CareerResourceCardDto card(String id, String type, String title, String summary,
                                       String category, String keyword, String sourceUrl,
                                       LocalDateTime publishedAt) {
        CareerResourceCardDto card = new CareerResourceCardDto();
        card.setId(id);
        card.setType(type);
        card.setTitle(title);
        card.setSummary(summary);
        card.setBody(summary);
        card.setCategory(category);
        card.setKeyword(keyword);
        card.setSourceUrl(sourceUrl);
        card.setPublishedAt(publishedAt);
        return card;
    }
}
