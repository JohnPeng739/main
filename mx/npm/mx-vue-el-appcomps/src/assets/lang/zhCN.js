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
        online: '在线',
        offline: '离线',
        closed: '已关闭'
      },
      title: {
        add: '创建一个新{module}',
        edit: '修改{module}信息',
        detail: '显示{module}信息'
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
    }
  }
}
