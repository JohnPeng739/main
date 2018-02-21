let dict = {
  'id': '@id',
  'code': '@word',
  'name': '@cname',
  'desc': '@cparagraph',
  'createdTime': '@integer(1413763339001, 1513763339001)',
  'updatedTime': '@integer(1413763339001, 1513763339001)',
  'operator': '@word'
}

let role = {
  ...dict,
  'accounts|1-3': [dict],
  'privileges|1-10': [dict]
}

let account = {
  ...dict,
  'owner': dict,
  'favoriteTools': ['/manage/user', '/manage/account', '/personal/mySetting'],
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

let loginHistory = {
  'id': '@id',
  'account': account,
  'loginTime': '@integer(1413763339001, 1513763339001)',
  'logoutTime|1': ['@integer(1413763339001, 1513763339001)', undefined],
  'token': '@title',
  'online|1': true
}

export {account, role, loginHistory}
