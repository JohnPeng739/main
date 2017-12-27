# mx-vue-el-utils
====
<h5>版本： V1.2.3</h5>
一个非常简单使用的面向HTML5的WEB开发的框架模块，使用了VUE 2和Element-UI。目前封装了:

1. 工具类：<br/>
  1.1 ajax 封装了默认错误处理的AJAX请求工具类，基于mx-app-utils中的ajax<br/>
  2.2 notify 封装了常规的info、warn、error、formValidateWarn等提示框工具<br/>
  2.3 formValidateRules 封装了常规的：必填校验、范围校验、电子邮件校验、自定义规则校验等表单校验工具<br/>
2. 组件：
  2.1 form组件：tagNormal和tagCouple两个常规的标签组件；<br/>
  2.2 Icon组件：支持所有material design图标；<br/>
  2.3 PaginatePane组件：支持常规新增、修改、删除、详情、刷新和分页显示的面板组件。<br/>
  2.4 添加了一个基于Input控件的选择组件（ChooseInput），支持定义显示内容，数据为JSON对象。<br/>
  2.5 添加了一个基于tag控件的选择组件（ChooseTag），支持定义显示内容，数据为JSON对象。<br/>
3. 布局：<br/>
  3.1 normal布局组件(LayoutNormal)：常规的顶栏+左边栏导航布局。<br/>
4. 对话框（DialogPane）<br/>
5. 国际化（locale）

## 安装
    npm i mx-vue-el-utils --save

## 用法
    import {locale, ajax, notify, formValidateRules, ChooseInput, ChooseTag, TagNormal, TagCouple, Icon, DialogPane, LayoutNormal} from ‘mx-vue-el-utils'
    import 'mx-vue-el-utils/mx-vue-el-utils.min.css'

    locale.setLanguage('zhCN')
    Vue.use(ElementUI, {locale: locale.elLocale})
    new Vue({locale.i18n, ...})

## 依赖模块
- mx-app-utils
- vue
- vue-i18n
- Element-UI


## 修改历史
**1.2.3**<br>
1. 为ChooseInput控件添加清除功能（clear）。

**1.2.2**<br>
1. 为PaginatePane组件添加了自定义按钮操作功能。
2. 调整了相关组件中按钮的样式定义。

**1.2.1**<br>
1. 重构了ChooseInput和ChooseTag中确认数据的方法，提供了done函数。
2. 修改了DialogPane中遮罩插入方式。
3. 增加了ChooseInput和ChooseTag中的语言翻译（en和zhCN）。

**1.2.0**<br>
1. 添加了ChooseInput和ChooseTag控件。
2. 修改了图标和按钮控件的样式控制。

**1.1.1**<br>
1. 提供了多语种资源文件的合并功能，在setLanguage方法中添加了...messages参数。

**1.1.0**<br>
1. 增加了国际化支持，默认支持en和zhCN语种。

**1.0.9**<br>
1. 增加了对话框的支持。

**1.0.8**<br>
1. 修改了分页面板中按钮多次触发的问题。

**1.0.4**<br>
1. 修改了ajax不能访问和分页post的数据的问题。

**1.0.1**<br>
1. 调整了输出的css文件的路径。

**1.0.0**<br>
1. 初始版本。
