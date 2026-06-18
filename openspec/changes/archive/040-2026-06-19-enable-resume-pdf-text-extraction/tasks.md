## 1. OpenSpec 与依赖边界

- [x] 1.1 运行 `openspec validate enable-resume-pdf-text-extraction --strict`，确认中文 proposal、design、delta specs 和任务可实施。
- [x] 1.2 在目标 Gradle 模块固定引入 PDFBox `2.0.31`，检查依赖树并记录 JDK 8/Cosmic 模板兼容性。

## 2. PDF 正文提取

- [x] 2.1 实现 `FileTextExtractor` PDFBox 适配器或组合提取器，保留 `.txt/.md` 行为并支持 `.pdf`。
- [x] 2.2 对正文执行 20,000 字限长，区分成功、无正文、不支持、加密/损坏和技术失败状态。
- [x] 2.3 增加 focused tests，覆盖文本型 PDF、空/图片 PDF、损坏 PDF、纯文本兼容和限长。

## 3. 简历与诊断联动

- [x] 3.1 复用 owner-only 简历更新边界，将成功提取的正文保存到 `parsedContent`，不得用空结果覆盖已有正文。
- [x] 3.2 扩展按 `resumeId` 诊断流程：正文缺失且存在 `fileKey` 时提取、回写并继续诊断；已有正文不重复提取。
- [x] 3.3 补齐必要的 Custom WebAPI 映射和 focused tests，覆盖跨用户拒绝、提取成功、扫描版降级和失败不回写。

## 4. CyanCruise 页面

- [x] 4.1 完善简历上传页面状态，准确区分 PDF 上传与正文提取，并把成功正文提交到简历记录。
- [x] 4.2 在简历诊断页面提供已有 PDF 重新读取正文入口，成功后刷新简历并继续诊断。
- [x] 4.3 更新 `cyancruise-routes.json` 和静态资源版本号，用户可见文案不使用专业缩写或误导性承诺。

## 5. 验证

- [x] 5.1 运行 PDF/file/resume/diagnosis focused Java tests。
- [x] 5.2 运行 `node --check webapp\isv\v620\cyancruise\assets\app.js` 和 `node webapp\isv\v620\cyancruise\validate-routes.js`。
- [x] 5.3 设置 JDK 8 后使用仓库 `gradlew.bat` 运行相关构建，必要时运行 `clean build`。
- [x] 5.4 更新迁移说明，记录 IPD 来源、数据映射、暂不迁移 OCR、依赖必要性、验证结果和部署说明。
