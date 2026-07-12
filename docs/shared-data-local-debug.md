# 共享数据本地调试

无论用户从局域网主机地址还是本机 `127.0.0.1` 进入 CyanCruise，业务数据、通知与订阅额度、用户权限和管理后台数据都以平台身份及 PostgreSQL 为准，不以电脑为准。

## 主机共享服务

主机运行 `DebugApplication`，其他电脑只使用浏览器访问：

```text
http://10.0.0.8:8881/ierp/login.html
```

主机的 `debug-local.properties` 应使用：

```properties
cosmic.web.bind.ip=0.0.0.0
cosmic.web.public.url=http://10.0.0.8:8881/ierp
cosmic.shared.runtime.enabled=true
```

## 标准星瀚服务入口

主机通过 `http://10.0.0.8:8080/ierp` 进入时，使用的是 `F:\kingdee\ENV\start-cosmic.bat` 启动的标准星瀚服务，不是 `DebugApplication`。它也必须启用相同的 PostgreSQL 和共享运行参数：

```text
-Dcc001.shared.runtime.enabled=true
-Dcc001.storage.backend=postgresql
-Dcc001.storage.postgresql.url=jdbc:postgresql://10.0.0.8:5432/cyancruise
-Dcc001.storage.postgresql.username=<数据库账号>
-Dcc001.storage.postgresql.password=<数据库密码或环境变量>
-Dcc001.storage.postgresql.schema=public
```

修改 `start-cosmic.bat` 后必须重启标准星瀚服务。`8080` 和 `8881` 只要加载同一版本代码、同一 PostgreSQL 数据库及相同 schema，同一平台账号看到的数据和权限就应一致；网址本身不参与数据分片。

## 另一台电脑的本机调试

另一台电脑需要同步同一版本的 CyanCruise 代码并运行自己的 `DebugApplication`。其 `debug-local.properties` 应使用本机页面地址，但 PostgreSQL 配置必须与主机服务一致：

```properties
cosmic.server.host=10.0.0.8
cosmic.web.bind.ip=127.0.0.1
cosmic.web.public.url=http://127.0.0.1:8881/ierp
cosmic.shared.runtime.enabled=true

cc001.profile.storage.adapter=postgresql
cc001.profile.postgresql.url=jdbc:postgresql://10.0.0.8:5432/cyancruise
cc001.profile.postgresql.username=<数据库账号>
cc001.profile.postgresql.password=<数据库密码>
cc001.profile.postgresql.schema=public
cc001.profile.postgresql.initialize=false
```

然后访问：

```text
http://127.0.0.1:8881/ierp/login.html
```

`cosmic.shared.runtime.enabled=true` 是关键配置。它会要求完整 PostgreSQL 连接并禁止业务状态、订阅额度和管理员治理状态退回到本机内存。若本地调试需要隔离测试数据，应使用独立数据库或 schema；不要关闭共享模式后再期待与主机数据一致。
