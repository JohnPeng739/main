export default {
  rbac: {
    user: {
      fields: {
        name: 'Name',
        sex: 'Sex',
        FEMALE: 'Female',
        MALE: 'Male',
        NA: 'NA',
        birthday: 'Birthday',
        department: 'Department',
        station: 'Station',
        desc: 'Description',
        firstName: 'First name',
        middleName: 'Middle name',
        lastName: 'Last name'
      },
      validate: {
        requiredFirstName: 'You need enter the first name.',
        requiredLastName: 'You need enter the last name.'
      },
      title: {
        add: 'Create a new user',
        edit: 'Edit the user\'s information',
        detail: 'Show the user\'s information'
      },
      message: {
        refreshSuccess: 'Refresh the user\'s list successfully.',
        addUserSuccess: 'Create a new user successfully.',
        editUserSuccess: 'Update the user\'s information successfully.',
        deleteUserSuccess: 'Delete the user\'s information successfully.',
        needChooseUser: 'You need choose a user for operating before.'
      }
    }
  }
}
