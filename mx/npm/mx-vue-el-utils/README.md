# mx-vue-el-utils
====
<h5>版本： V1.0.1</h5>
一个非常简单使用的面向HTML5的WEB开发的框架模块，使用了VUE 2和Element-UI。目前封装了:

1. 工具类：<br/>
  1.1 ajax 封装了默认错误处理的AJAX请求工具类，基于mx-app-utils中的ajax<br/>
  2.2 notify 封装了常规的info、warn、error、formValidateWarn等提示框工具<br/>
  2.3 formValidateRules 封装了常规的：必填校验、范围校验、电子邮件校验、自定义规则校验等表单校验工具<br/>
2. 组件：
  2.1 form组件：tagNormal和tagCouple两个常规的标签组件；<br/>
  2.2 icon组件：支持所有material design图标；<br/>
  2.3 PaginatePane组件：支持常规新增、修改、删除、详情、刷新和分页显示的面板组件。<br/>
3. 布局：<br/>
  3.1 normal布局组件：常规的顶栏+左边栏导航布局。<br/>

## 安装
    npm i mx-vue-el-utils --save

## 用法
    import {ajax, notify, formValidateRules, TagNormal, TagCouple, Icon, LayoutNormal} from ‘mx-vue-el-utils'
    import 'mx-vue-el-utils/mx-vue-el-utils.min.css'

## 依赖模块
- mx-app-utils
- vue
- Element-UI


## 修改历史
**1.0.1**<br>
1. 调整了输出的css文件的路径。

**1.0.0**<br>
1. 初始版本。
