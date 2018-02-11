# mx-vue-el-appcomps
====
<h5>版本： V1.1.1</h5>
一个非常简单使用的面向HTML5的WEB开发的框架模块，使用了VUE 2和Element-UI。目前封装了:

1. 组件：
  1.1 字典对象选择组件：ChooseDictInput、ChooseDictTag和ChooseUserInput；<br/>
  1.2 通用字典管理页面组件：DictManage，支持常规的增删改查和刷新数据；<br/>
  1.3 RBAC管理页面组件：包括用户管理、账户管理、部门管理、角色管理、特权管理、授权管理、操作日志、登录日志挂历等组件。<br/>
2. store：<br/>
  2.1 登录账户信息状态。<br/>
3. 国际化（locale）

## 安装
    npm i mx-vue-el-appcomps --save

## 用法
    mport MxVueElUtils from 'mx-vue-el-utils'
    import MxAppComps from '../../dist/mx-vue-el-appcomps.min'

    import App from './App'
    import router from './router'
    import store from './store'
    import { MyLanguage, setLanguage } from './lang'

    import mock from './mock'

    Vue.config.productionTip = false

    setLanguage('zh-CN')
    Vue.use(ElementUI, {locale: MyLanguage.elLang})
    Vue.use(MxVueElUtils, {locale: MyLanguage.mxLang})
    Vue.use(MxAppComps, {locale: MyLanguage.appLang})

    mock()

    console.log(MyLanguage.myI18n.t('test'))

    /* eslint-disable no-new */
    new Vue({
      i18n: MyLanguage.myI18n,
      el: '#app',
      router,
      store,
      template: '<App/>',
      components: {App}
    })


## 依赖模块
- mx-vue-el-utils
- mx-app-utils
- vue
- vue-i18n
- Element-UI


## 修改历史
**1.1.1**<br>
1. 添加了修改密码和修改个性化菜单的功能。
2. 修改了发现的bug。

**1.0.16**<br>
1. 修改了多语种文件的载入方式。
2. 修改了分页控件中表格控件最小高度的设置规则，采用自动计算。
3. 调整了分页控件的表格样式。
4. 修改了账户维护和用户维护界面组件中的bug。
5. 调整了弹出表格的基础样式。
6. 调整了Account Store的代码，将持久化部分剥离，以支持SSR。
7. 修改了登入和登出的信息提示错误。
8. 添加了nuxtServerInit支持。
9. 添加了token支持。
10. 修复了账户登录状态管理中角色组定义错误。
11. 根据MxAjax重构内容，修改了相关调用代码。

**1.0.2**<br>
1. 修改了生产环境配置文件，瘦身了生产代码。

**1.0.1**<br>
1. 添加了账户登录状态中常用工具栏的处理。

**1.0.0**<br>
1. 初始版本。
