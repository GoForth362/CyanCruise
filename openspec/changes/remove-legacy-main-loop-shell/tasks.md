## 1. 首屏历史壳清理

- [x] 1.1 让入口头部、页面内导航和状态区在静态首屏默认隐藏，同时保留调试模式所需 DOM 标识
- [x] 1.2 删除“求职主循环”“主循环状态”等历史用户可见文案，并替换为当前 CyanCruise 普通中文文案
- [x] 1.3 更新受影响静态资源缓存版本号

## 2. 回归门禁

- [x] 2.1 扩展静态验证，检查首屏默认隐藏、历史文案清理和关键挂载点保留
- [x] 2.2 执行路由校验、JavaScript 语法校验和历史文案扫描
- [x] 2.3 执行 `openspec validate remove-legacy-main-loop-shell --strict` 与 `openspec validate --all --strict`
- [x] 2.4 使用 JDK 8 执行仓库构建验证
