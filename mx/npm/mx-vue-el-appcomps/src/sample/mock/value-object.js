let log = {
  'id': '@id',
  'operator': '@word',
  'content': '@cparagraph',
  'createdTime': '@integer(1413763339001, 1513763339001)'
}

let dict = {
  'id': '@id',
  'code': '@word',
  'name': '@cname',
  'desc': '@cparagraph',
  'createdTime': '@integer(1413763339001, 1513763339001)',
  'updatedTime': '@integer(1413763339001, 1513763339001)',
  'operator': '@word'
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
  'favorityTools': ['/manage/user', '/manage/account', '/personal/mySetting'],
  'roles|1-5': [{
    'id': '@id',
    'code|1': ['admin', 'user', 'guest'],
    'name': '@cname',
    'desc': '@cparagraph',
    'createdTime': '@integer(1413763339001, 1513763339001)',
    'updatedTime': '@integer(1413763339001, 1513763339001)',
    'operator': '@word'
  }]
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
  'account': account,
  'loginTime': '@integer(1413763339001, 1513763339001)',
  'logoutTime|1': ['@integer(1413763339001, 1513763339001)', undefined],
  'token': '@title',
  'online|1': true
}

export {log, user, account, role, privilege, department, accredit, loginHistory}
