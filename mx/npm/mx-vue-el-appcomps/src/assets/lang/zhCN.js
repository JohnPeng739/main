export default {
  rbac: {
    user: {
      fields: {
        name: '姓名',
        sex: '性别',
        FEMALE: '女性',
        MALE: '男性',
        NA: '未知',
        birthday: '出生日期',
        department: '部门',
        station: '岗位',
        desc: '描述',
        firstName: '姓',
        middleName: '辈',
        lastName: '名'
      },
      validate: {
        requiredFirstName: '你需要输入用户的姓。',
        requiredLastName: '你需要输入用户的名'
      },
      title: {
        add: '创建一个新用户',
        edit: '修改用户信息',
        detail: '显示用户信息'
      },
      message: {
        refreshSuccess: '刷新用户列表数据成功。',
        addUserSuccess: '创建一个新用户成功。',
        editUserSuccess: '修改用户信息成功。',
        deleteUserSuccess: '删除用户信息成功。',
        needChooseUser: '你需要在操作前先选中一个用户。'
      }
    }
  }
}
