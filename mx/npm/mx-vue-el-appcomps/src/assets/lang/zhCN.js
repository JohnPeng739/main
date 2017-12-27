export default {
  rbac: {
    common: {
      fields: {
        FEMALE: '女性',
        MALE: '男性',
        NA: '未知',
        id: 'ID',
        code: '代码',
        name: '名称',
        desc: '描述',
        status: '状态',
        createdTime: '创建时间',
        updatedTime: '更新时间',
        operator: '操作人',
        roles: '角色',
        online: '在线',
        offline: '离线',
        closed: '已关闭',
        from: '来自'
      },
      title: {
        add: '创建一个新{module}',
        edit: '修改{module}信息',
        details: '显示{module}信息'
      },
      message: {
        refreshSuccess: '刷新{module}列表数据成功。',
        addSuccess: '创建一个新{module}成功。',
        editSuccess: '修改{module}信息成功。',
        deleteSuccess: '删除{module}信息成功。',
        needChoose: '你需要在操作前先选中一个{module}。'
      },
      validate: {
        requiredCode: '你需要输入代码。',
        requiredName: '你需要输入名称。'
      }
    },
    user: {
      module: '用户',
      fields: {
        name: '姓名',
        sex: '性别',
        birthday: '出生日期',
        department: '部门',
        station: '岗位',
        firstName: '姓',
        middleName: '辈',
        lastName: '名'
      },
      validate: {
        requiredFirstName: '你需要输入用户的姓。',
        requiredLastName: '你需要输入用户的名'
      }
    },
    role: {
      module: '角色'
    },
    department: {
      module: '部门'
    },
    privilege: {
      module: '特权'
    },
    loginHistory: {
      module: '登录历史',
      fields: {
        loginTime: '登入时间',
        logoutTime: '登出时间',
        online: '是否在线'
      }
    },
    accredit: {
      module: '授权',
      fields: {
        src: '源账户',
        tar: '目标账户',
        roles: '角色',
        startTime: '开始时间',
        endTime: '结束时间',
        closed: '是否关闭'
      },
      validate: {
        src: '你需要选择一个源账户。',
        tar: '你需要选择一个目标账户。',
        roles: '你至少要选择一个角色。',
        startTime: '你需要输入开始时间。'
      }
    },
    logs: {
      module: '操作日志',
      fields: {
        time: '时间',
        content: '内容'
      }
    },
    account: {
      module: '账户',
      fields: {
        owner: '所属人',
        oldPassword: '旧密码',
        password: '新密码',
        confirm: '确认密码'
      },
      title: {
        allocate: '分配',
        password: '修改密码'
      },
      validate: {
        requiredOwner: '你需要为本账户选择一个用户。',
        requiredRoles: '你至少需要选择一个角色。',
        requiredPassword: '你必须要输入密码。',
        requiredConfirm: '你必须输入确认密码。',
        passwordMatch: '输入的密码和确认密码不一致。'
      },
      message: {
        changePasswordSuccess: '修改账户[{code} - {name}]的密码成功。'
      }
    }
  }
}
