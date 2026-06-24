## Context

CyanCruise 当前 CareerLoop 静态页面和 `/cc001/*` WebAPI 已覆盖职业画像、简历、面试、就业洞察等就业场景。考研能力需要复用现有模块边界：DTO 放在 `base-common`，纯业务规则放在 `base-helper`，Cosmic WebAPI 放在 `cloud01`，页面放在 `webapp/isv/v620/cyancruise`。项目必须兼容 JDK 1.8，不新增破坏 Cosmic/KDDT 模板约束的依赖。

## Goals / Non-Goals

**Goals:**

- 提供考研页面入口，覆盖择校、复习计划、错题解析与复试准备四个高频动作。
- 后端提供稳定可用的规则兜底结果，AI 不可用时仍可生成清晰中文建议。
- API 请求和响应使用可测试 DTO，前端仅消费 route metadata 中声明的 `/cc001/postgraduate/*` 契约。
- 用户可见文案使用普通中文，避免直接展示内部代码标识或行业黑话。

**Non-Goals:**

- 不接入真实院校历史报录比数据库、研招网实时数据或外部爬虫。
- 不实现拍照 OCR；本期接收题目文本，图片识别留作后续文件/视觉能力。
- 不新增持久化数据表；本期结果按请求即时生成。
- 不替代正式升学咨询，只提供学习规划和信息整理辅助。

## Decisions

1. **以新能力 `postgraduate-exam-companion` 建模**

   考研与就业主循环目标不同，但页面和 WebAPI 技术栈一致。新能力可以独立描述择校、计划、错题和复试，不把考研规则混入面试或就业洞察规格。

2. **规则兜底优先，AI 增强可选**

   后端 service 先根据本科层次、绩点、英语水平、地区、目标科目和日期生成确定性建议。AI 可用时后续可替换或润色局部文本，但本期不依赖 AI 返回才能完成流程，避免生产配置缺失导致页面不可用。

3. **四个 WebAPI 对应四个用户动作**

   `/cc001/postgraduate/school-recommend`、`/cc001/postgraduate/plan/generate`、`/cc001/postgraduate/mistake/analyze`、`/cc001/postgraduate/reexam/prepare` 分别对应用户页面上的四个操作，便于前端局部 loading、局部错误和后续能力扩展。

4. **前端作为 CareerLoop 静态路由新增 `postgraduate`**

   页面沿用现有 `cyancruise` 单页脚本，不引入构建链。首页/就业入口增加“考研陪伴”入口，路由元数据声明 API、身份要求和 fallback。

## Risks / Trade-offs

- [院校推荐不含真实实时数据] -> 在结果中展示“建议核对当年招生简章、国家线和报录比”的提醒，并用“稳、冲、保”作为规划分层而非录取承诺。
- [错题解析无法处理图片] -> 页面文案明确当前支持粘贴题目文字，拍照识别留作后续文件能力。
- [即时生成不持久化] -> 用户刷新后需要重新生成；本期先满足闭环体验，后续可接 PostgreSQL 存储学习档案。
- [新增页面影响静态缓存] -> 修改 `index.html` 资源版本，部署时提示需要重新发布静态资源。
