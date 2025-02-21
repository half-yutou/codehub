接口文档地址(本地):http://localhost:8848/doc.html
接口文档地址(云端):http://47.122.113.178:8848/doc.html

admin账号密码:
```text
{
  "role": "admin",
  "username": "root",
  "password": "xyt"
}
```

统一MVC风格:
1. controller: 参数校验,调用方法,统一返回Result对象 / 异常处理-->全局异常处理器 / 简单逻辑可放宽,使用mp直接查询数据
2. Service: 参数校验,调用mapper层,统一返回实体类 / 简单逻辑可放宽,使用mp直接查询数据
3. Mapper: 调用Mybatis

        