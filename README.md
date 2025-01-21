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

3. Course类: teacherId -> 删除
> 课程不需要和教师绑定,同一个课程可能有多个授课教师,教师和对应教学班级绑定即可

影响传参处: 无(暂未编写对应crud方法)

4. `新增一个学生到指定班级`方法 : -> 传参更改
```java
    @Operation(summary = "新增一个学生到指定班级")
@PutMapping("add/class/{classId}/{studentNumber}")
public Result addSingleStudentToClass(
        @PathVariable Long classId, 
        @PathVariable String studentNumber) : 将原来的Student实体类改为studentNumber(学号)
```
> 解耦,原来方法是教师可以直接添加一个学生实体到数据库,现在更改为教师只能添加已注册的学生到班级中

01.15fix:

1. 根据教师id查询教学班级: 由于新增根据学生id查询教学班级,所以需要修改接口路径
```java
    // 查询教师所有教学班级
    @Operation(summary = "查询该教师所有教学班级")
    @GetMapping("/list/teacher/{teacherId}")  : 原为`/list/{teacherId}

```

2. 创建教学班级和更新教学班级 : 简化入参,故入参有更改请注意


01.18

1. 查重功能设计思路
已在服务器接入查重脚本,支持两两文件比较,支持输出重复率和相似部分对比
每当一份作业提交,则进行一次遍历比较,比较**同作业**,**每个学生的最新一次**的提交
当重复率超过某个阈值(如70%,依据工具情况动态调整),则返回**相似部分对比和查重警告**
否则返回**查重通过**

问题1:查重比较耗时,故**作为异步任务执行**.应该额外增加存储查重结果的区域(新增表)
- 新增表记录查重信息(仅记录未通过的)
  - id
  - 作业id
  - 提交id
  - 重复的另一份提交id
  - 重复率
  - 查重报告(两份代码的相似之处)

问题2:作业存储路径需要结构化,便于遍历
存储路径:
- codehub-submit
    - 教学班id
      - 作业id
        - 提交id: (文件名).代码后缀 // 非代码无需查重

查重时只需遍历对应班级对应作业即可
        