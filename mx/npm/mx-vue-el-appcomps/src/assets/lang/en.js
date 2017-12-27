export default {
  rbac: {
    common: {
      fields: {
        FEMALE: 'Female',
        MALE: 'Male',
        NA: 'NA',
        id: 'ID',
        code: 'Code',
        name: 'Name',
        desc: 'Description',
        status: 'Status',
        createdTime: 'Created time',
        updatedTime: 'Updated time',
        operator: 'Operator',
        roles: 'Roles',
        online: 'Online',
        offline: 'Offline',
        closed: 'Closed',
        from: 'From'
      },
      title: {
        add: 'Create a new {module}',
        edit: 'Edit the {module}\'s information',
        details: 'Show the {module}\'s information'
      },
      message: {
        refreshSuccess: 'Refresh the {module}\'s list successfully.',
        addSuccess: 'Create a new {module} successfully.',
        editSuccess: 'Update the {module}\'s information successfully.',
        deleteSuccess: 'Delete the {module}\'s information successfully.',
        needChoose: 'You need choose a {module} for operating before.'
      },
      validate: {
        requiredCode: 'You need enter the code.',
        requiredName: 'You need enter the name.'
      }
    },
    user: {
      module: 'user',
      fields: {
        name: 'Name',
        sex: 'Sex',
        birthday: 'Birthday',
        department: 'Department',
        station: 'Station',
        firstName: 'First name',
        middleName: 'Middle name',
        lastName: 'Last name'
      },
      validate: {
        requiredFirstName: 'You need enter the first name.',
        requiredLastName: 'You need enter the last name.'
      }
    },
    role: {
      module: 'role'
    },
    department: {
      module: 'department'
    },
    privilege: {
      module: 'privilege'
    },
    loginHistory: {
      module: 'login history',
      fields: {
        loginTime: 'Login time',
        logoutTime: 'Logout time',
        online: 'Is online'
      }
    },
    accredit: {
      module: 'accredit',
      fields: {
        src: 'Source account',
        tar: 'Target account',
        roles: 'Roles',
        startTime: 'Start time',
        endTime: 'End time',
        closed: 'Is closed'
      },
      validate: {
        src: 'You need choose the source account.',
        tar: 'You need choose the target account.',
        roles: 'You need choose the roles at least one.',
        startTime: 'You need enter the started time.'
      }
    },
    logs: {
      module: 'Operate logs',
      fields: {
        time: 'Time',
        content: 'Content'
      }
    },
    account: {
      module: 'account',
      fields: {
        owner: 'Owner',
        oldPassword: 'Old password',
        password: 'New password',
        confirm: 'Confirm'
      },
      title: {
        allocate: 'Assign',
        password: 'Change password'
      },
      validate: {
        requiredOwner: 'You need choose a user for the account.',
        requiredRoles: 'You need choose a role at least.',
        requiredPassword: 'You need enter the password.',
        requiredConfirm: 'You need enter the confirm password.',
        passwordMatch: 'The password does not match the confirm password.'
      },
      message: {
        changePasswordSuccess: 'Change the password successfully for the account[{code} - {name}].'
      }
    }
  }
}
