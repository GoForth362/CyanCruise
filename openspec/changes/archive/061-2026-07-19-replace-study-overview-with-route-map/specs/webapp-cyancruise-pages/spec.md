## ADDED Requirements

### Requirement: 深造页面使用专用升学路线图渲染
CyanCruise webapp SHALL 为 `further-study-home` 使用专用升学路线图渲染，不再使用仅输出“深造护航工具”卡片矩阵的通用总览渲染。页面 SHALL 复用就业路线图的视觉层级，但 SHALL 使用升学语义和升学阶段文案。

#### Scenario: 打开升学路线图页面
- **WHEN** 用户打开 `index.html?ccRoute=further-study-home`
- **THEN** 页面 SHALL 显示“升学路线图”标题、准备情况摘要和四个阶段
- **AND** 页面 SHALL NOT 显示“深造护航工具”作为首屏主内容

#### Scenario: 浏览升学中心底部
- **WHEN** 用户浏览完升学路线图、洞察和资讯
- **THEN** 页面 SHALL NOT 显示考研陪伴、保研陪伴和留学陪伴卡片区
