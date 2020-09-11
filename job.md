#定时任务说明文档
* ### 数据库表
> 表名 taskinfo   注释： 任务信息表    
> 约束 p表示主键，n表示非空，u表示唯一   ,下划线转换成java里为驼峰，如 create_user 为 createUser      
>
|  序号   | 字段  | 数据类型 | 注释 | 约束|
|  :----:  | :----:  | :---: | :---: |  :---: |
| 1  | id  |  bigint | 任务id| p |
| 2  | name | varchar(255)| 任务名称| u |
| 3  | desc | varchar(500)| 任务描述|   |
| 4  | cron_expression | varchar(255)| cron表达式| n |
| 5  | targeturl | varchar(255)| 执行接口| n |
| 6  | status | int | 任务状态| n |
| 7  | create_user | varchar(255)| 创建人| n |
| 8  | create_time | varchar(255)| 创建时间| n |
| 9  | update_user | varchar(255)| 更新人| n |
| 10  | update_time | varchar(255)| 更新时间| n |

* ### [参考示例]("https://www.cnblogs.com/laoyeye/p/9352002.html")