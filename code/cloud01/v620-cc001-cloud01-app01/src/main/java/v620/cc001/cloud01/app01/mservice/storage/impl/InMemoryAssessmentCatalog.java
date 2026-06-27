package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.AssessmentOptionDto;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Server-side assessment catalog migrated from IPD assessment semantics.
 */
public class InMemoryAssessmentCatalog implements AssessmentCatalog {

    private final List<AssessmentScaleDto> scales;

    public InMemoryAssessmentCatalog() {
        this.scales = Arrays.asList(
                mbtiScale(),
                riasecScale(),
                bigFiveScale(),
                careerValuesScale(),
                stressCopingScale());
    }

    public List<AssessmentScaleDto> listScales() {
        List<AssessmentScaleDto> out = new ArrayList<AssessmentScaleDto>();
        for (AssessmentScaleDto scale : scales) {
            AssessmentScaleDto summary = copyScale(scale, false);
            summary.setQuestionCount(Integer.valueOf(scale.getQuestions() == null ? 0 : scale.getQuestions().size()));
            out.add(summary);
        }
        return out;
    }

    public AssessmentScaleDto loadScale(Long scaleId) {
        if (scaleId == null) {
            return null;
        }
        for (AssessmentScaleDto scale : scales) {
            if (scaleId.equals(scale.getScaleId())) {
                return copyScale(scale, true);
            }
        }
        return null;
    }

    private AssessmentScaleDto mbtiScale() {
        long scaleId = 1001L;
        AssessmentScaleDto scale = scale(scaleId,
                "性格倾向测评(MBTI)",
                "探索你在外向/内向、直觉/实感、思考/情感、判断/知觉四个维度上的偏好，帮助理解思维与决策风格。",
                "ipd-mbti-v1");
        scale.setQuestions(Arrays.asList(
                q(scaleId, 1, "在社交场合中，你通常会：", "EI",
                        o(100101, "A", "主动认识新朋友，并从群体互动中获得能量", "E", 0),
                        o(100102, "B", "更愿意和少数熟人交流，长时间热闹会消耗精力", "I", 1)),
                q(scaleId, 2, "你最容易通过哪种方式恢复状态？", "EI",
                        o(100201, "A", "和他人待在一起、交流想法", "E", 0),
                        o(100202, "B", "留出安静独处的时间", "I", 1)),
                q(scaleId, 3, "面对一个新问题时，你更倾向于：", "EI",
                        o(100301, "A", "和别人边讨论边打开思路", "E", 0),
                        o(100302, "B", "先自己想清楚再表达", "I", 1)),
                q(scaleId, 4, "在沟通中你通常：", "EI",
                        o(100401, "A", "先说出来，再在交流中修正", "E", 0),
                        o(100402, "B", "组织完整后再开口", "I", 1)),
                q(scaleId, 5, "你更喜欢的信息类型是：", "SN",
                        o(100501, "A", "具体、可验证、来自真实经验", "S", 0),
                        o(100502, "B", "抽象、理论化、能探索多种可能", "N", 1)),
                q(scaleId, 6, "学习新内容时，你更喜欢：", "SN",
                        o(100601, "A", "按步骤跟着做，先掌握细节", "S", 0),
                        o(100602, "B", "先抓住整体图景，再补细节", "N", 1)),
                q(scaleId, 7, "做判断时，你更信任：", "SN",
                        o(100701, "A", "直接经验和已经验证的方法", "S", 0),
                        o(100702, "B", "模式、灵感和趋势判断", "N", 1)),
                q(scaleId, 8, "你更享受哪类内容？", "SN",
                        o(100801, "A", "贴近现实、有明确情境的故事", "S", 0),
                        o(100802, "B", "有想象力、带有假设或未来感的故事", "N", 1)),
                q(scaleId, 9, "做决定时，你更看重：", "TF",
                        o(100901, "A", "逻辑、一致性和客观事实", "T", 0),
                        o(100902, "B", "人的感受和关系影响", "F", 1)),
                q(scaleId, 10, "你更希望别人认为你：", "TF",
                        o(101001, "A", "专业、理性、判断清晰", "T", 0),
                        o(101002, "B", "体贴、可靠、能照顾他人感受", "F", 1)),
                q(scaleId, 11, "遇到冲突时，你会优先关注：", "TF",
                        o(101101, "A", "问题背后的事实与原则", "T", 0),
                        o(101102, "B", "团队氛围和关系修复", "F", 1)),
                q(scaleId, 12, "你更容易被什么说服？", "TF",
                        o(101201, "A", "数据和分析推理", "T", 0),
                        o(101202, "B", "价值观、故事和具体人的处境", "F", 1)),
                q(scaleId, 13, "你更喜欢一天的安排：", "JP",
                        o(101301, "A", "有计划、有结构、边界清楚", "J", 0),
                        o(101302, "B", "灵活、可调整、保留余地", "P", 1)),
                q(scaleId, 14, "面对截止日期，你通常：", "JP",
                        o(101401, "A", "尽量提前完成", "J", 0),
                        o(101402, "B", "临近截止时效率更高", "P", 1)),
                q(scaleId, 15, "你的工作空间通常：", "JP",
                        o(101501, "A", "整洁、有序、物品位置固定", "J", 0),
                        o(101502, "B", "灵感式摆放，不一定规整但你知道在哪", "P", 1)),
                q(scaleId, 16, "做选择时，你更倾向于：", "JP",
                        o(101601, "A", "尽快决定并推进", "J", 0),
                        o(101602, "B", "尽量保持选择开放", "P", 1))
        ));
        return withQuestionCount(scale);
    }

    private AssessmentScaleDto riasecScale() {
        long scaleId = 1002L;
        AssessmentScaleDto scale = scale(scaleId, "RIASEC职业兴趣",
                "Holland职业兴趣测评，探索你的实际型、研究型、艺术型、社会型、企业型、常规型倾向。",
                "riasec-v1");
        scale.setQuestions(Arrays.asList(
                paired(scaleId, 1, "更吸引你的任务是：", "R", "修理设备、搭建实物或处理工具", "I", "分析问题、研究规律或验证假设"),
                paired(scaleId, 2, "你更愿意参与：", "A", "创意表达、设计内容或写作展示", "S", "帮助他人、沟通协调或教学辅导"),
                paired(scaleId, 3, "在团队中你更自然地承担：", "E", "推动决策、争取资源或组织行动", "C", "整理资料、制定流程或检查细节"),
                paired(scaleId, 4, "你更有成就感的是：", "I", "找到复杂问题的关键原因", "R", "把一个具体东西做出来"),
                paired(scaleId, 5, "你更喜欢的工作环境是：", "S", "需要持续服务和沟通的场景", "A", "允许自由表达和审美判断的场景"),
                paired(scaleId, 6, "面对任务时你更看重：", "C", "规则清楚、步骤明确、结果稳定", "E", "目标清晰、影响更大、能带动别人"),
                paired(scaleId, 7, "你更愿意学习：", "R", "硬件、工程、工具或操作技能", "A", "视觉、表达、内容或创意技能"),
                paired(scaleId, 8, "你更容易沉浸在：", "I", "数据、模型、实验或推理中", "S", "关系、反馈、用户需求或辅导中"),
                paired(scaleId, 9, "你更擅长处理：", "E", "机会判断、说服沟通和结果推进", "R", "现场问题、设备材料和实际约束"),
                paired(scaleId, 10, "你更偏好的产出是：", "C", "准确报表、规范文档或流程清单", "I", "分析结论、研究报告或解决方案"),
                paired(scaleId, 11, "你更喜欢别人评价你：", "A", "有想法、有表达、有审美", "E", "有魄力、有影响、有推动力"),
                paired(scaleId, 12, "你更能接受长期投入：", "S", "与人协作并看到他人成长", "C", "把系统和资料维护得稳定可靠")
        ));
        return withQuestionCount(scale);
    }

    private AssessmentScaleDto bigFiveScale() {
        long scaleId = 1003L;
        AssessmentScaleDto scale = scale(scaleId, "大五人格(BIG5)",
                "测量开放性、尽责性、外向性、宜人性、神经质五大人格维度。",
                "big5-v1");
        scale.setQuestions(Arrays.asList(
                paired(scaleId, 1, "你更像是：", "O", "喜欢新想法、新体验和跨界探索", "C", "重视计划、秩序和完成质量"),
                paired(scaleId, 2, "别人更常说你：", "E", "外向、活跃、容易带动气氛", "A", "温和、配合、愿意照顾别人"),
                paired(scaleId, 3, "压力来临时你通常：", "N", "情绪波动明显，需要更多恢复时间", "C", "先拆任务，再逐项推进"),
                paired(scaleId, 4, "你更喜欢：", "O", "探索不确定的新方向", "A", "建立稳定互信的合作关系"),
                paired(scaleId, 5, "你处理承诺时通常：", "C", "尽量按时兑现，提前准备", "E", "边互动边推进，更依赖现场状态"),
                paired(scaleId, 6, "你在陌生环境中：", "E", "较快打开话题并认识新人", "N", "会先观察风险和不确定性"),
                paired(scaleId, 7, "你更在意：", "A", "合作体验和彼此感受", "O", "想法是否新颖、有启发"),
                paired(scaleId, 8, "你面对反馈时：", "N", "容易反复思考其中的负面部分", "C", "更关注可执行的改进清单"),
                paired(scaleId, 9, "你更享受：", "O", "学习陌生领域和提出假设", "E", "公开表达和即时交流"),
                paired(scaleId, 10, "你更容易投入：", "A", "服务他人和团队协作", "C", "长期目标和自我管理")
        ));
        return withQuestionCount(scale);
    }

    private AssessmentScaleDto careerValuesScale() {
        long scaleId = 1004L;
        AssessmentScaleDto scale = scale(scaleId, "职业价值观",
                "了解你最看重的职业价值维度：成就感、安全感、自主性、社会服务、地位声望、多样挑战。",
                "values-v1");
        scale.setQuestions(Arrays.asList(
                paired(scaleId, 1, "你更看重一份工作能带来：", "ACH", "明确成果和成长成就", "SEC", "稳定收入和安全边界"),
                paired(scaleId, 2, "你更希望工作中拥有：", "AUT", "自主安排和决策空间", "SOC", "帮助他人和产生社会价值"),
                paired(scaleId, 3, "更吸引你的是：", "STA", "更高平台、头衔或行业认可", "VAR", "不同任务和持续挑战"),
                paired(scaleId, 4, "长期选择岗位时你优先考虑：", "SEC", "行业稳定、制度清楚、风险可控", "ACH", "能不断突破和拿到结果"),
                paired(scaleId, 5, "你更不能接受：", "AUT", "事事被限制、没有决策余地", "SEC", "节奏混乱、保障不足"),
                paired(scaleId, 6, "你更愿意投入：", "SOC", "对人有帮助的产品或服务", "STA", "能提升个人影响力的项目"),
                paired(scaleId, 7, "理想工作更应该：", "VAR", "保持新鲜感，持续遇到新问题", "AUT", "给你独立负责的空间"),
                paired(scaleId, 8, "你更看重领导认可你：", "ACH", "把难题做成了", "SOC", "让团队或用户变好了"),
                paired(scaleId, 9, "你更偏好：", "STA", "进入有名气的平台", "SEC", "进入稳定可靠的平台"),
                paired(scaleId, 10, "你希望每天工作：", "VAR", "变化丰富，有探索感", "ACH", "目标明确，有进展感"),
                paired(scaleId, 11, "你愿意为哪件事牺牲部分收入：", "SOC", "工作更有意义", "AUT", "工作更自由"),
                paired(scaleId, 12, "你更想积累：", "STA", "行业声誉和人脉资源", "VAR", "跨场景解决问题的能力")
        ));
        return withQuestionCount(scale);
    }

    private AssessmentScaleDto stressCopingScale() {
        long scaleId = 1005L;
        AssessmentScaleDto scale = scale(scaleId, "压力应对测评",
                "评估你在压力情境中的应对风格和情绪调节模式。",
                "stress-v1");
        scale.setQuestions(Arrays.asList(
                paired(scaleId, 1, "压力出现时，你通常先：", "PROBLEM", "拆解问题并找下一步行动", "EMOTION", "先处理情绪和感受"),
                paired(scaleId, 2, "遇到不确定任务，你更倾向于：", "PLAN", "列计划、设截止点、控制节奏", "SUPPORT", "找人讨论，获得反馈和支持"),
                paired(scaleId, 3, "当结果不理想时，你更常：", "REFRAME", "复盘并重新解释问题", "AVOID", "暂时回避，等状态恢复"),
                paired(scaleId, 4, "压力很大的一天结束后，你会：", "EMOTION", "休息、运动或做让自己平静的事", "PROBLEM", "继续补齐任务缺口"),
                paired(scaleId, 5, "你更相信：", "SUPPORT", "合适的支持能显著降低压力", "PLAN", "清晰计划能显著降低压力"),
                paired(scaleId, 6, "面对批评，你更容易：", "REFRAME", "把它转成可改进的信息", "EMOTION", "先被情绪影响一段时间"),
                paired(scaleId, 7, "压力积累时，你更常：", "AVOID", "拖延或切换到轻松任务", "SUPPORT", "找可信的人说清楚现状"),
                paired(scaleId, 8, "你更擅长：", "PLAN", "安排优先级和节奏", "REFRAME", "调整看法和降低内耗"),
                paired(scaleId, 9, "面对多任务，你会：", "PROBLEM", "先处理最关键瓶颈", "AVOID", "容易被任务量压住而迟迟不开头"),
                paired(scaleId, 10, "你最需要补强的是：", "SUPPORT", "主动求助和同步风险", "PLAN", "拆解任务和稳定执行")
        ));
        return withQuestionCount(scale);
    }

    private AssessmentScaleDto scale(long scaleId, String title, String description, String version) {
        AssessmentScaleDto scale = new AssessmentScaleDto();
        scale.setScaleId(Long.valueOf(scaleId));
        scale.setTitle(title);
        scale.setDescription(description);
        scale.setVersion(version);
        return scale;
    }

    private AssessmentScaleDto withQuestionCount(AssessmentScaleDto scale) {
        scale.setQuestionCount(Integer.valueOf(scale.getQuestions() == null ? 0 : scale.getQuestions().size()));
        return scale;
    }

    private AssessmentQuestionDto paired(long scaleId, long sortOrder, String text,
                                         String leftDimension, String leftText,
                                         String rightDimension, String rightText) {
        long base = scaleId * 1000L + sortOrder * 10L;
        return q(scaleId, sortOrder, text, leftDimension + "/" + rightDimension,
                o(base + 1L, "A", leftText, leftDimension, 0),
                o(base + 2L, "B", rightText, rightDimension, 1));
    }

    private AssessmentQuestionDto q(long scaleId, long sortOrder, String text, String dimension,
                                    AssessmentOptionDto left, AssessmentOptionDto right) {
        AssessmentQuestionDto question = new AssessmentQuestionDto();
        question.setQuestionId(Long.valueOf(scaleId * 100L + sortOrder));
        question.setScaleId(Long.valueOf(scaleId));
        question.setQuestionText(text);
        question.setQuestionType("SINGLE");
        question.setDimensionCode(dimension);
        question.setSortOrder(Integer.valueOf((int) sortOrder));
        left.setQuestionId(question.getQuestionId());
        right.setQuestionId(question.getQuestionId());
        question.setOptions(Arrays.asList(left, right));
        return question;
    }

    private AssessmentOptionDto o(long id, String label, String text, String dimension, int sortOrder) {
        AssessmentOptionDto option = new AssessmentOptionDto();
        option.setOptionId(Long.valueOf(id));
        option.setOptionLabel(label);
        option.setOptionText(text);
        option.setDimensionCode(dimension);
        option.setScoreValue(BigDecimal.ONE);
        option.setSortOrder(Integer.valueOf(sortOrder));
        return option;
    }

    private AssessmentScaleDto copyScale(AssessmentScaleDto source, boolean includeQuestions) {
        AssessmentScaleDto copy = new AssessmentScaleDto();
        copy.setScaleId(source.getScaleId());
        copy.setTitle(source.getTitle());
        copy.setDescription(source.getDescription());
        copy.setVersion(source.getVersion());
        copy.setQuestionCount(source.getQuestionCount());
        if (includeQuestions) {
            List<AssessmentQuestionDto> questions = new ArrayList<AssessmentQuestionDto>();
            for (AssessmentQuestionDto question : source.getQuestions()) {
                questions.add(copyQuestion(question));
            }
            copy.setQuestions(questions);
        } else {
            copy.setQuestions(new ArrayList<AssessmentQuestionDto>());
        }
        return copy;
    }

    private AssessmentQuestionDto copyQuestion(AssessmentQuestionDto source) {
        AssessmentQuestionDto copy = new AssessmentQuestionDto();
        copy.setQuestionId(source.getQuestionId());
        copy.setScaleId(source.getScaleId());
        copy.setQuestionText(source.getQuestionText());
        copy.setQuestionType(source.getQuestionType());
        copy.setDimensionCode(source.getDimensionCode());
        copy.setSortOrder(source.getSortOrder());
        List<AssessmentOptionDto> options = new ArrayList<AssessmentOptionDto>();
        for (AssessmentOptionDto option : source.getOptions()) {
            options.add(copyOption(option));
        }
        copy.setOptions(options);
        return copy;
    }

    private AssessmentOptionDto copyOption(AssessmentOptionDto source) {
        AssessmentOptionDto copy = new AssessmentOptionDto();
        copy.setOptionId(source.getOptionId());
        copy.setQuestionId(source.getQuestionId());
        copy.setOptionLabel(source.getOptionLabel());
        copy.setOptionText(source.getOptionText());
        copy.setScoreValue(source.getScoreValue());
        copy.setDimensionCode(source.getDimensionCode());
        copy.setSortOrder(source.getSortOrder());
        return copy;
    }
}
