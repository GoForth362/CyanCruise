## ADDED Requirements

### Requirement: 文件上传预览作为调试能力保留
文件上传预览能力 SHALL 继续保留 `/cc001/files/*` 底层契约、route map 和验证入口，但独立文件上传预览页面 SHALL NOT 作为普通用户默认主导航入口。真实用户 SHALL 通过简历页等业务页面间接使用文件能力。

#### Scenario: 普通用户导航
- **WHEN** 普通用户打开 CyanCruise 默认导航
- **THEN** `file-upload-preview` SHALL NOT 作为普通业务入口展示

#### Scenario: 简历页使用文件能力
- **WHEN** 用户在简历页上传、预览或删除简历关联文件
- **THEN** 页面 SHALL 通过 `/cc001/files/upload`、`/cc001/files/download`、`/cc001/files/delete` 或等价文件服务契约完成操作

#### Scenario: 调试模式访问文件页
- **WHEN** 开发者使用 `?ccDebug=1#file-upload-preview` 或 hash 直达方式打开文件上传预览页面
- **THEN** 页面 SHALL 保留文件上传、预览、下载、删除和文本抽取契约信息，用于排查 BOS 文件服务接入
