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


01.14 fix:
字段更改:
1. Assignment类:courseId -> classId
> 作业与教学班级相关联,而不是和课程相关联,因为同一门课程可能由不同老师的不同班级教授

影响传参处:所有含Assignment入参方法处
```java
@Operation(summary = "创建作业")
@PostMapping("/assign/add")
public Result createAssignment(@RequestBody Assignment assignment): 实体类字段改为classId


@Operation(summary = "修改作业信息")
@PutMapping("/assign/update")
public Result updateAssignment(@RequestBody Assignment assignment): 实体类字段改为classId

@Operation(summary = "根据班级ID查询作业列表")
@GetMapping("/assign/list/class/{classId}") : 将原来的courseId改为classId
```

2. RegisterDTO类:classId -> adminClass
> 学生注册时提交的是行政班级的名称,而不是具体的教学班级编号

影响传参处: 注册学生用户时的RegisterDTO
```java
@Operation(summary = "用户注册")
@PostMapping("/register")
public Result register(@RequestBody RegisterDTO registerDTO) : 将该对象的ClassId改为adminClass
```