## ADDED Requirements

### Requirement: 错题解析页使用独立错题本入口
错题解析页 SHALL 在提交操作区提供“错题记录”入口，并 SHALL NOT 在当前解析页底部嵌入过往错题列表。过往错题的浏览和详情查看 SHALL 由独立错题本页面完成。

#### Scenario: 打开错题解析页
- **WHEN** 用户进入错题解析页
- **THEN** 页面 SHALL 显示解析操作和“错题记录”入口
- **AND** 页面 SHALL NOT 在底部渲染过往错题列表
