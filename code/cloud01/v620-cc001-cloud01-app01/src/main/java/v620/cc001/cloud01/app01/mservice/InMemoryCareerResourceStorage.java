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
        cards.add(withAuthor(card("service-ncss-001", "consultation", "国家大学生就业服务平台",
                "查看职位信息、就业指导、专场招聘、重点领域就业和高校毕业生服务。",
                "公共服务", "高校毕业生", "https://www.ncss.cn/", now.minusHours(6)),
                "教育部学生服务与素质发展中心"));
        cards.add(withAuthor(card("service-12333-001", "consultation", "全国就业公共服务平台",
                "查询岗位推荐、招聘会信息、职业指导、职业测评和高校毕业生就业服务。",
                "公共服务", "岗位推荐", "https://www.12333.gov.cn/job/", now.minusHours(12)),
                "人力资源社会保障部"));
        cards.add(withAuthor(card("service-mohrss-job-001", "consultation", "中国公共招聘网",
                "查看招聘信息、招聘会信息、事业单位公开招聘和市场资讯。",
                "公共招聘", "招聘会", "https://job.mohrss.gov.cn/", now.minusDays(1)),
                "人力资源社会保障部"));
        cards.add(withAuthor(card("service-jobonline-001", "consultation", "就业在线",
                "全国招聘求职服务平台入口，聚合各地公共就业人才服务和招聘求职资源。",
                "公共招聘", "就业在线", "https://www.jobonline.cn/", now.minusDays(2)),
                "人力资源社会保障部"));
        cards.add(withAuthor(card("article-people-202606-001", "article", "三“新”看就业共赴好前程",
                "关注高校毕业生就业新趋势、数智就业服务和 AI 模拟面试等公共就业服务实践。",
                "就业观察", "数智就业", "https://cpc.people.com.cn/n1/2026/0614/c64387-40739823.html", now.minusDays(1)),
                "人民日报"));
        cards.add(withAuthor(card("article-ncss-202404-001", "article", "专家支招大学生求职：明确目标、提升自我",
                "围绕高校毕业生求职困惑，提供目标定位、能力提升和就业指导建议。",
                "求职指导", "就业指导", "https://www.ncss.cn/ncss/jydt/jy/202404/20240403/2293279892.html", now.minusDays(2)),
                "国家大学生就业服务平台"));
        cards.add(withAuthor(card("article-ncss-202208-001", "article", "秋招拉开帷幕，毕业生该如何准备？",
                "覆盖简历、宣讲会、海投和视频面试等秋招准备问题。",
                "求职指导", "秋招", "https://www.ncss.cn/ncss/zd/ws/202208/20220809/2209339200.html", now.minusDays(3)),
                "国家大学生就业服务平台"));
        cards.add(withAuthor(card("video-bilibili-resume-001", "video", "求职简历怎么写？看这 1 个视频就够了",
                "B 站求职简历讲解视频，可直接跳转播放，适合完善简历前观看。",
                "简历", "在线视频", "https://www.bilibili.com/video/BV1RN411f7LU/", now.minusDays(1)),
                "哔哩哔哩"));
        cards.get(cards.size() - 1).setDurationSec(Integer.valueOf(600));
        cards.add(withAuthor(card("video-cctv-employment-001", "video", "大学生就业季：求职路观察",
                "央视网就业季视频页面，可直接跳转观看，适合作为求职环境和就业选择参考。",
                "就业观察", "在线视频", "https://news.cctv.com/2013/05/31/VIDE1369989879849147.shtml", now.minusDays(2)),
                "央视网"));
        cards.get(cards.size() - 1).setDurationSec(Integer.valueOf(300));
        cards.add(withAuthor(card("video-bilibili-interview-001", "video", "大学生找工作避雷：求职技巧与面试误区",
                "围绕校招、简历、面试和实习选择的避坑视频，可直接跳转播放。",
                "面试", "在线视频", "https://www.bilibili.com/video/BV1Zg4y1A7sU/", now.minusDays(3)),
                "哔哩哔哩"));
        cards.get(cards.size() - 1).setDurationSec(Integer.valueOf(600));
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

    private CareerResourceCardDto withAuthor(CareerResourceCardDto card, String author) {
        card.setAuthor(author);
        return card;
    }
}
