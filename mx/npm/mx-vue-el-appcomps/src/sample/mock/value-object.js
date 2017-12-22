let dict = {
  'id': '@id',
  'code': '@word',
  'name': '@cname',
  'desc': '@cparagraph'
}

let user = {
  'id': '@id',
  'firstName': '@cfirst',
  'middleName': '@cword',
  'lastName': '@clast',
  'fullName': '@cname',
  'desc': '@cparagraph',
  'station': '@ctitle',
  'sex|1': ['FEMALE', 'MALE', 'NA'],
  'birthday': '@integer(1413763339001, 1513763339001)',
  'department': {
    ...dict
  }
}

let role = {
  ...dict,
  'accounts|1-3': [dict],
  'privileges|1-10': [dict]
}

let account = {
  ...dict,
  'owner': dict,
  'roles|1-5': [dict]
}

let privilege = {
  ...dict,
  'roles|1-3': [dict]
}

let department = {
  ...dict,
  'manager': dict,
  'employees': [dict]
}

let accredit = {
  'id': '@id',
  'desc': '@cparagraph',
  'src': dict,
  'tar': dict,
  'roles|1-3': [dict],
  'startTime': '@integer(1413763339001, 1513763339001)',
  'endTime|1': ['@integer(1413763339001, 1513763339001)', undefined],
  'closed|1': false
}

let loginHistory = {
  'id': '@id',
  'account': dict,
  'loginTime': '@integer(1413763339001, 1513763339001)',
  'logoutTime|1': ['@integer(1413763339001, 1513763339001)', undefined],
  'online|1': true
}

export {user, account, role, privilege, department, accredit, loginHistory}
