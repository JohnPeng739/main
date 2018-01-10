# mx-app-utils
====
<h5>版本： V1.6.0</h5>
一个非常简单使用的面向HTML5的WEB开发的工具类模块。目前封装了:
1. logger：LOG（debug、info、warn、error）
2. ajax：使用axios的简单AJAX调用工具方法
3. formatter: 支持格式化字符串、日期格式化等方法
4. parser: 支持日期、json等解析和转换
5. round、mixin、clone和timestamp的工具方法
6. MyWebSocket: 一个通用的Js WebSocket客户端

## 安装
    npm i mx-app-utils --save

## 用法
### logger
    import {logger} from ‘mx-app-utils'
    
    logger.debug('debug message')
    logger.debug('this is a debug message, code: %s, age: %d.', 'john', 30)
### ajax
    import {ajax, logger} from ‘mx-app-utils'
    
    ajax.get('/rest/path', data => logger.info('%j', data), err => logger.error('%s', err))

## 依赖模块
- axios
- util


## 修改历史
**1.6.0**<br>
1. 添加了一个通用的Js WebSocket客户端MyWebSocket。

**1.5.5**<br>
1. 添加了针对对象属性的变量格式化方法(formatObj)，并重命名了针对参数列表的变量格式化方法(format, formatArgs)。

**1.5.4**<br>
1. 添加了mixin的方法。

**1.5.3**<br>
1. 修改了ajax中操作pagination数据对象的bug。

**1.5.1**<br>
1. 增加了round方法，支持指定小数位数的四舍五入。

**1.5.0**<br>
1. 增加了parser的支持，提供了字符串解析为日期对象、字符串和json对象互转，日期解析支持自定义正则表达式。

**1.4.0**<br>
1. 重新发布编译代码。

**1.2.6**<br>
1. 提炼并抽象了formatter工具类，提供：format、formatDate、formatDatetime、formatTimestamp、formatFixedLength方法。
2. 提供了针对formatter的单元测试代码。

**1.2.5**<br>
1. 添加了针对ajax格式化内容单元测试代码。

**1.2.4**<br>
1. 添加了针对logger格式化内容单元测试代码。
2. 针对发布后的代码进行了编译优化，减小了代码包大小。

**1.2.3**<br>
1. 添加了编译脚本，并将编译后代码发布到npm中，解决了以前必须在引用项目中添加编译项的做法。

**1.1.9**<br>
1. 为ajax添加了del方法。

**1.1.8**<br>
1. 重新定义了ajax中errorCode为整数值，并且该值为0表示没有错误发生，否则错误信息存储在errorMessage中。
2. 添加了对分页查询的返回值支持，加入了pagination{totle, size, page}属性，用以存储分页信息。

**1.1.7**<br>
1. 修改了ajax中处理errorCode为undefined时存在的bug。

**1.1.6**<br>
基本可用的第一个版本。
1. 提供了logger、ajax、util的基本工具类
2. 变更了ajax的实现，增强了异常捕捉能力，并提供了后台data跟mock模拟data的兼容性，可以支持以下两种模式：
(1)成功直接返回数据，错误直接返回异常
(2)WEB服务器异常时返回异常，其他返回response数据，其中包括：errorCode、errorMsg、data字段，errorCode=='ok'表示业务成功，data中包括了业务数据；否则表示业务失败，errorMsg中包含业务错误信息。
3. logger中自动加入了process.env.NODE_EN变量的使用，即：在开发环境下才启用debug和info级别的日志。
