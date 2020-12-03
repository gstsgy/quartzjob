# 定时任务说明文档
* ### 说明：
  之前我们项目中使用定时服务都是与业务代码写在一起，
  随着业务代码编译完成定时服务的一些属性也随之定死，为了解决动态创建定时任务，
  实时修改它的一些属性，比如开启，关闭，更改执行周期等等，因此我们将业务与定
  时服务解藕，分成两个项目，定时任务具体执行的业务通过http接口暴露出来，
  然后通过可视化界面来创建更新删除定时服务。
* ### 相关概念： 
  -  job：job是定时服务中的最小单位，一个job有 任务名称，任务描述，cron表达式，业务接口，
  任务状态，创建时间，更新时间等属性组成，通过创建，修改，删除job来操作定时服务。
  - 业务接口：定时服务具体执行的业务所要暴露的http接口，此处规定所有接口不加权限，
  所有接口使用post请求，所有接口无参无返回
  - cron表达式：Cron表达式是一个字符串，字符串以5或6个空格隔开，分为6或7个域，每一个域代表一个含义
* ### 使用说明
  -  可独立使用，也可整合到其他项目，独立使用运行对应的war包或者jar包即可
  -  访问地址（以下针对独立使用）：war——> http://ip:tomcat端口号/项目名/index.html，
    jar-> http://ip:9999/index.html
  -  初始登录帐号 admin   密码 admin123.

* ### 整合接口文档
  - 用户登录  url：/job/login method:post  data: {userName:xxx,passWord:xxx}
  - 获取系统状态 url:/job/sysstate  method：get 返回值  0表示首次登录，提示用户更改密码，密码更改后该值变为1
  - 更改密码 url：/job/password method:put params ?oldpw=xxx&newpw=xxx
  - 获取所有job信息 url：/job/jobs method:get params ?name=xx
  - 新增job url:/job/job method:post data TaskInfo 结构参照数据库表
  - 修改job url:/job/job method:put data TaskInfo 
  - 修改job状态 url:/job/jobstatus method:put data TaskInfo 
  - 修改job cron表达式 url:/job/jobcron method:put data TaskInfo
  - 执行一次job url:/job/run method:put data TaskInfo
  - 删除job url:/job/jobs method:delete data List<TaskInfo>
  - job状态枚举  0 停止  1 运行
  - 接口调用时需要在请求头加入 token 字段，值为登录成功后返回的token
* ### 数据库表
> 表名 taskinfo   注释： 任务信息表    
> 约束 p表示主键，n表示非空，u表示唯一   ,下划线转换成java里为驼峰，如 create_user 为 createUser      
>
|  序号   | 字段  | 数据类型 | 注释 | 约束|
|  :----:  | :----:  | :---: | :---: |  :---: |
| 1  | id  |  bigint | 任务id| p |
| 2  | job_name | varchar(255)| 任务名称| u |
| 3  | job_desc | varchar(500)| 任务描述|   |
| 4  | cron_expression | varchar(255)| cron表达式| n |
| 5  | targeturl | varchar(255)| 执行接口| n |
| 6  | job_status | int | 任务状态| n |
| 8  | create_time | varchar(255)| 创建时间| n |
| 10  | update_time | varchar(255)| 更新时间| n |

* ### 安全性
 为了保证业务接口安全性，定时任务每个请求都在请求头中加了password字段，值初始为 admin123
 admin123采用RSA加密协议加密过，请使用下面私钥解密验证通过后在进行业务接口的处理。（也可不加）
 ```$xslt
privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJr3Joxm69EZaIn1gPitQQXf3nzwjz1oTofrbTyQ1Wbepo6ktkxTnFccovVSwoOqNqhXm8mdtqfca7aFamQc/8go8HczFSNMSws4qQK3/wbQkQS053cs6vdIajpvih9ZIAQV6B+x30X0ELf+r8CDlNjXy5MUFn2aikNgZl9AJmS7AgMBAAECgYBwNuJKL0k6LFz/8bBH4yW/vEHfVU9UV1DuqbN7dSGjET2o82sy0CTZC9qRLTG/qCVWN8KO987JtRqmm+vTvRAWXx+dZ/WYBonhKDyKPZEEkduoKK96axg1qLozEGmw6ULZNF6cOADu0HLCWOHpijxQf7ew0YAKVwEvkt1ArU/K8QJBANYdAc5l4KlKQz3awMW4jpGShgD8ch+Ci4AKfDMdWjcx8qd96DAnuLNUShRdJFkreGN/kUuWYJYOSRzsMp+MJRMCQQC5R/aJWDu0St/Sd+QxH5/MVJgbvsTulMZWjykfNdqPJpqU55bcBIkeZds+o/cmyDHfs/KsLkroNaoXpibPJz65AkAH0xmMzGZQaXlZmlPvJdZ39W3WPWOCSUgFztxJFeqFF9sxScWhdOIoE419fwXkCFWm9TN/Gqi+8xiy6OLUt89nAkBMm/7KR1tYbuUhB0WpMiRGvmuufTC8XXOs8sDUENZv/kxIaYGtM4rmsML4oZ9dv8UvdB5RJ0r9vMK5yI+fc+CxAkEAr+Q3h0i6svII0yOmOuMmX9bRaw2Wu6k9BVoeAlXd7hmDa6hDUVGKW3tX5oDhzOxxETi64Yp+7fZf380jVsTbaw==";
```
