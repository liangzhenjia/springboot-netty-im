# springboot-netty-im

+ 简单快捷的IM方案，快速打造在线IM，可用于公司内网、外网通讯，客服系统等，实现了socket,websocket，能和安卓、IOS应用结合使用。
+ Java后端和js消息采用Google Protobuf传输，如需修改protobuf文件请参考当前文档
+ 项目可以直接生成后台代码、页面及js文件，大大节省开发时间
+ 目前实现了单聊、群聊、机器人回复功能，项目已经结合Mysql数据库，聊天的信息会保存到数据库表中。

##运行环境

jdk8 + mysql8 + maven