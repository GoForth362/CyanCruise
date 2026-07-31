## ADDED Requirements

### Requirement: 考研子功能页提供返回考研陪伴操作
CyanCruise SHALL 在择校择专业、复习计划、错题解析和复试准备子功能页的标题区提供“返回考研陪伴”按钮，按钮 SHALL 返回 `postgraduate` route。

#### Scenario: 从考研子功能返回
- **WHEN** 用户打开 `postgraduate-school`、`postgraduate-plan`、`postgraduate-mistake` 或 `postgraduate-reexam`
- **THEN** 页面标题区 SHALL 显示“返回考研陪伴”按钮
- **AND** 用户点击后 SHALL 返回 `postgraduate`

### Requirement: 保研子功能页提供返回保研陪伴操作
CyanCruise SHALL 在排名监控、背景提升、材料精修和导师联系子功能页的标题区提供“返回保研陪伴”按钮，按钮 SHALL 返回 `postgraduate-recommendation` route。

#### Scenario: 从保研子功能返回
- **WHEN** 用户打开 `recommendation-ranking`、`recommendation-background`、`recommendation-material` 或 `recommendation-tutor`
- **THEN** 页面标题区 SHALL 显示“返回保研陪伴”按钮
- **AND** 用户点击后 SHALL 返回 `postgraduate-recommendation`

### Requirement: 留学子功能页提供返回留学陪伴操作
CyanCruise SHALL 在国家地区、语言考试、选校定位、文书主线和签证网申子功能页的标题区提供“返回留学陪伴”按钮，按钮 SHALL 返回 `study-abroad` route。

#### Scenario: 从留学子功能返回
- **WHEN** 用户打开 `study-abroad-profile`、`study-abroad-language`、`study-abroad-school`、`study-abroad-statement` 或 `study-abroad-visa`
- **THEN** 页面标题区 SHALL 显示“返回留学陪伴”按钮
- **AND** 用户点击后 SHALL 返回 `study-abroad`

### Requirement: 陪伴主页保持独立平台入口
考研陪伴、保研陪伴和留学陪伴主页 SHALL NOT 因子功能返回关系而新增页面内返回升学中心按钮。

#### Scenario: 打开陪伴主页
- **WHEN** 用户打开 `postgraduate`、`postgraduate-recommendation` 或 `study-abroad`
- **THEN** 页面 SHALL 保持独立平台入口行为
- **AND** 页面标题区 SHALL NOT 显示返回其自身或返回升学中心的按钮
