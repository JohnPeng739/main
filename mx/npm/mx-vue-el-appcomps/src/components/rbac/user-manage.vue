<style rel="stylesheet/less" lang="less" scoped>
  .dialog-form {
    padding: 0;
    margin: 0;
  }
</style>

<template>
  <div>
    <paginate-pane ref="paginatePane" v-on:buttonHandle="handleButtonClick">
      <el-table :data="tableData" :max-height="tableMaxHeight" @current-change="handleCurrentChange"
                highlight-current-row>
        <el-table-column prop="fullName" label="姓名" :width="100"></el-table-column>
        <el-table-column prop="sex" label="性别" :width="80">
          <template slot-scope="scope">
            <span v-if="scope.row.sex === 'FEMALE'">女性</span>
            <span v-else-if="scope.row.sex === 'MALE'">男性</span>
            <span v-else>未知</span>
          </template>
        </el-table-column>
        <el-table-column prop="birthday" label="出生日期" :width="100">
          <template slot-scope="scope">
            {{parseDate(scope.row.birthday)}}
          </template>
        </el-table-column>
        <el-table-column prop="department" label="部门" :width="100">
          <template slot-scope="scope">
            {{getDepartmentName(scope.row.department)}}
          </template>
        </el-table-column>
        <el-table-column prop="station" label="岗位" :width="120"></el-table-column>
        <el-table-column prop="desc" label="描述"></el-table-column>
      </el-table>
    </paginate-pane>
    <dialog-pane ref="dialogPane" :title="title()" v-on:reset="handleReset" v-on:submit="handleSubmit">
      <el-form ref="formUser" slot="form" :model="formUser" :rules="rulesUser" label-width="80px" class="dialog-form">
        <el-row type="flex">
          <el-col :span="6">
            <el-form-item prop="firstName" label="姓">
              <el-input v-model="formUser.firstName" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item prop="middleName" label="辈">
              <el-input v-model="formUser.middleName" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item prop="lastName" label="名">
              <el-input v-model="formUser.lastName" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item prop="station" label="岗位">
              <el-input v-model="formUser.station" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="desc" label="">
              <el-input type="textarea" v-model="formUser.desc" :rows="4" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flext">
          <el-col :span="6">
            <el-form-item prop="sex" label="性别">
              <el-select v-model="formUser.sex">
                <el-option value="FEMALE" label="女性"></el-option>
                <el-option value="MALE" label="男性"></el-option>
                <el-option value="NA" label="未知"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item prop="birthday" label="出生日期">
              <el-date-picker v-model="formUser.birthday" type="date" :readonly="readonly"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </dialog-pane>
  </div>
</template>

<script>
  import { logger, formatter } from 'mx-app-utils'
  import { ajax, notify, formValidateRules, PaginatePane, DialogPane } from 'mx-vue-el-utils'

  export default {
    name: 'page-rbac-manage',
    components: {PaginatePane, DialogPane},
    props: {
      tableMaxHeight: {
        type: Number,
        default: 560
      }
    },
    data () {
      return {
        tableData: [],
        operate: 'detail',
        selected: null,
        formUser: this.newUser(),
        rulesUser: {
          firstName: [formValidateRules.requiredRule({msg: '必须输入名'})],
          lastName: [formValidateRules.requiredRule({msg: '必须输入姓'})]
        }
      }
    },
    computed: {
      readonly () {
        return this.operate === 'detail'
      }
    },
    methods: {
      title () {
        switch (this.operate) {
          case 'add':
            return '新增用户'
          case 'edit':
            return '修改用户信息'
          case 'detail':
          default:
            return '显示用户详情'
        }
      },
      newUser () {
        return {id: undefined, firstName: '', middleName: '', lastName: '', fullName: '', desc: '', station: '', sex: 'MALE', birthday: 0, department: {id: '', code: '', name: '', desc: ''}}
      },
      parseDate (longDate) {
        if (longDate) {
          return formatter.formatDate(longDate)
        } else {
          return 'NA'
        }
      },
      getDepartmentName (department) {
        if (department && department.name) {
          return department.name
        } else {
          return 'NA'
        }
      },
      refreshData (pagination) {
        ajax.post('/rest/users', pagination, (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            notify.info('刷新用户数据成功。')
          }
        })
      },
      showData (data, operate) {
        if (!data) {
          logger.error('未设置有效数据。')
          return
        }
        let {id, firstName, middleName, lastName, desc, station, sex, birthday, department} = data
        if (birthday && typeof birthday === 'number') {
          birthday = new Date(birthday)
        }
        this.formUser = {id, firstName, middleName, lastName, desc, station, sex, birthday, department}
        this.operate = operate
        this.$refs['dialogPane'].show(operate, '80%')
        logger.debug('show dialog, operate: %s, data: %j.', operate, data)
      },
      handleSubmit () {
        this.$refs['formUser'].validate(valid => {
          if (valid) {
            let {id, firstName, middleName, lastName, desc, station, sex, birthday, department} = this.formUser
            if (birthday && birthday instanceof Date) {
              birthday = birthday.getTime()
            }
            if (this.operate === 'add') {
              let url = '/rest/users/new'
              logger.debug('send POST "%s".', url)
              ajax.post(url, {id, firstName, middleName, lastName, desc, station, sex, birthday, department}, data => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  notify.info('添加用户数据成功。')
                }
              })
            } else if (this.operate === 'edit') {
              let url = '/rest/users/' + id
              logger.debug('send PUT "%s".', url)
              ajax.put(url, {id, firstName, middleName, lastName, desc, station, sex, birthday, department}, data => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  notify.info('修改用户数据成功。')
                }
              })
            }
          } else {
            notify.formValidateWarn()
          }
        })
      },
      handleReset () {
        this.$refs['formUser'].resetFields()
      },
      handleButtonClick (operate, pagination) {
        switch (operate) {
          case 'refresh':
            this.refreshData(pagination)
            break
          case 'add':
            this.showData(this.newUser(), operate)
            break
          case 'edit':
          case 'delete':
          case 'detail':
            if (!this.selected) {
              notify.info('请先选中一个用户进行操作。')
              break
            }
            if (operate === 'delete') {
              let {id} = this.selected
              let url = '/rest/users/' + id
              logger.debug('send DELETE "%s".', url)
              ajax.del(url, data => {
                if (data) {
                  this.refreshData(pagination)
                  notify.info('删除用户数据成功。')
                }
              })
            } else {
              this.showData(this.selected, operate)
            }
            break
        }
      },
      handleCurrentChange (currentRow, oldCurrentRow) {
        logger.debug('The current row changed, current: %j, old: %j.', currentRow, oldCurrentRow)
        this.selected = currentRow
      }
    },
    mounted () {
      this.refreshData(null)
    }
  }
</script>
