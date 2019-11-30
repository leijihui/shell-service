# shell-service

springboot学习入门，后端远程登录到主机启动shell脚本并返回信息给前端，实现部署功能

## 更新日志
---
1. 搭建了自己的centos虚拟机用于测试（https://blog.csdn.net/qq_28866471/article/details/102984161）
---
- 2019.11.09
1. 完成服务器连接工作
2. 可以上传脚本并执行，获取返回值和脚本输出
3. 完成邮件发送功能（在内网部署好像是个没用的功能，考虑websocket）
4. 上传的脚本存储在jar包内部，运行完成后最好从远程删除脚本文件
---
- 2019.11.10
1. 安装MySQL并建表（mysql文件夹）
1. 使用mybatis连接数据库，使用generator插件（mapper），解决关键字报错问题
2. 添加全局异常捕获机制，添加自定义异常
3. 加工从数据库获取的数据（PropertyService），减轻一部分工作量
4. **添加返回json数据的封装工具类（ResponseEntity和ResponseBuilder）**
5. 常量封装（Constant）
6. 完成前端一个接口的开发，编写可用脚本，获取服务器指定文件夹下的备份目录列表
---
- 2019.11.11
1. 完善运行脚本工具类，封装两种运行方式（持续性的和非持续性的）
2. 修改脚本运行的工具类，同一时刻只能运行一个长持续周期的脚本
3. 运行脚本支持回调函数，需要实现Callback接口或lambda
4. 完成几个前端脚本的编写
5. 学会了正确在配置文件中修改日志级别的方法，将一些地方的级别调整为debug
6. [ ] 内网连不通邮箱，学习feign（？），使用测试环境已有的sms接口（TODO）
---
- 2019.11.12
1. 不在类内部使用线程跑持续脚本，改为从外部设置线程，内部只存储是否为持续性脚本，交给外部处理逻辑
2. 添加登录验证，使用jwt生成token
3. 完善从Git部署前端接口，未测试
---
- 2019.11.13
1. 添加仓库用表，存储使用的git仓库信息
2. 使用脚本检索git仓库的可用分支，前端给出可用脚本(npm run build:xxx)
3. 上传文件和文件夹
4. 连续查看脚本输出信息是相同信息，已修改为resultMsg.clone()
5. 走通从Git部署前端流程（邮件看情况调整为使用短信）

---
- 2019.11.27
1. 部署前端跑通流程
2. 在.bash_profile中export npm_config_loglevel=error(打包信息不输出WARN级别信息)