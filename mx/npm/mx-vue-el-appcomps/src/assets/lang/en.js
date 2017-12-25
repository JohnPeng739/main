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
        online: 'Online',
        offline: 'Offline',
        closed: 'Closed'
      },
      title: {
        add: 'Create a new {module}',
        edit: 'Edit the {module}\'s information',
        detail: 'Show the {module}\'s information'
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
    }
  }
}
