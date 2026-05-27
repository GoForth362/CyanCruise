## 1. Shared DTOs

- [x] 1.1 新增 JDK 8 兼容 `ResumeRecordDto`。
- [x] 1.2 新增 `ResumeCreateRequest` 和 `ResumeUpdateRequest`。
- [x] 1.3 确认 DTO 不依赖 Spring、JPA、Lombok、Jackson 特定注解或 Java 9+ API。

## 2. Profile Resume Block

- [x] 2.1 在 `CareerProfileSnapshotMergeService` 中新增 resume block 合并能力。
- [x] 2.2 支持清理 resume block 或用用户剩余最新简历重建 resume block。
- [x] 2.3 增加 helper 测试，验证保存简历不会覆盖 assessment、onboarding 或 preferences。

## 3. Resume Storage And Application Service

- [x] 3.1 新增 `ResumeStorage` 边界，支持 save、load、listByUser、delete。
- [x] 3.2 新增文件型 `ResumeStorage` 默认适配器。
- [x] 3.3 新增内存型 `ResumeStorage` 测试适配器。
- [x] 3.4 新增 `ResumeApplicationService`，实现创建、详情、列表、更新、删除和所有权校验。
- [x] 3.5 创建或更新简历后同步职业画像 resume block 并刷新统一画像。
- [x] 3.6 删除当前画像引用的简历后清理或切换到剩余最新简历。

## 4. WebAPI Boundary

- [x] 4.1 新增 Cosmic 风格 `ResumeWebApi`，使用 `/cc001/resume` 路径。
- [x] 4.2 暴露创建、详情、用户列表、更新和删除入口。
- [x] 4.3 WebAPI 只调用 `ResumeApplicationService`，不依赖 Spring、JPA、OSS SDK、PDF 解析或简历诊断。

## 5. Test Coverage

- [x] 5.1 测试创建简历记录并按用户列表读回。
- [x] 5.2 测试跨用户读取、更新或删除被拒绝。
- [x] 5.3 测试文件型存储通过新实例读回简历记录。
- [x] 5.4 测试创建或更新简历后画像 `hasResume=true`，且 resume 缺失信号消失。
- [x] 5.5 测试删除当前画像引用的简历后 resume block 被清理或切换到剩余最新简历。

## 6. Migration Documents

- [x] 6.1 更新 `docs/ipd-to-cyancruise-migration-map.md` 中简历基础状态。
- [x] 6.2 在文档中保留 Cosmic datamodel、文件上传/预览、简历诊断和简历生成仍待后续迁移的说明。

## 7. Validation

- [x] 7.1 运行 `openspec validate migrate-resume-core --strict`。
- [x] 7.2 运行 `openspec validate --all --strict`。
- [x] 7.3 设置 JDK 8 后运行相关 Gradle 测试或构建。
