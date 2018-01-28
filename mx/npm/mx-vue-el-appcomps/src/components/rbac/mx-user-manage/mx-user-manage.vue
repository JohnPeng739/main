<template>
  <div>
    <mx-paginate-table ref="paginatePane" v-on:buttonHandle="handleButtonClick">
      <el-table :data="tableData" :max-height="tableMaxHeight" @current-change="handleCurrentChange"
                highlight-current-row>
        <el-table-column prop="fullName" :label="t('rbac.user.fields.name')" :width="100"></el-table-column>
        <el-table-column prop="sex" :label="t('rbac.user.fields.sex')" :width="80">
          <template slot-scope="scope">
            <span v-if="scope.row.sex === 'FEMALE'">{{t('rbac.common.fields.FEMALE')}}</span>
            <span v-else-if="scope.row.sex === 'MALE'">{{t('rbac.common.fields.MALE')}}</span>
            <span v-else>{{t('rbac.common.fields.NA')}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="birthday" :label="t('rbac.user.fields.birthday')" :width="100">
          <template slot-scope="scope">
            {{parseDate(scope.row.birthday)}}
          </template>
        </el-table-column>
        <el-table-column prop="department" :label="t('rbac.user.fields.department')" :width="100">
          <template slot-scope="scope">
            {{getDepartmentName(scope.row.department)}}
          </template>
        </el-table-column>
        <el-table-column prop="station" :label="t('rbac.user.fields.station')" :width="120"></el-table-column>
        <el-table-column prop="desc" :label="t('rbac.common.fields.desc')"></el-table-column>
      </el-table>
    </mx-paginate-table>
    <mx-dialog ref="dialogPane" :title="title()" v-on:reset="handleReset" v-on:submit="handleSubmit"
               class="layout-dialog">
      <el-form ref="formUser" slot="form" :model="formUser" :rules="rulesUser" label-width="130px" class="dialog-form">
        <el-row type="flex">
          <el-col :span="8">
            <el-form-item prop="firstName" :label="t('rbac.user.fields.firstName')">
              <el-input v-model="formUser.firstName" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="middleName" :label="t('rbac.user.fields.middleName')">
              <el-input v-model="formUser.middleName" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="lastName" :label="t('rbac.user.fields.lastName')">
              <el-input v-model="formUser.lastName" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="desc" :label="t('rbac.common.fields.desc')">
              <el-input type="textarea" v-model="formUser.desc" :rows="4" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="sex" :label="t('rbac.user.fields.sex')">
              <el-select v-model="formUser.sex">
                <el-option value="FEMALE" :label="t('rbac.common.fields.FEMALE')"></el-option>
                <el-option value="MALE" :label="t('rbac.common.fields.MALE')"></el-option>
                <el-option value="NA" :label="t('rbac.common.fields.NA')"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="birthday" :label="t('rbac.user.fields.birthday')">
              <el-date-picker v-model="formUser.birthday" type="date" :readonly="readonly"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="department" :label="t('rbac.user.fields.department')">
              <mx-choose-dict-input v-model="formUser.department" restUrl="/rest/departments"
                                    displayFormat="{code} - {name}" :disabled="readonly"></mx-choose-dict-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="station" :label="t('rbac.user.fields.station')">
              <el-input v-model="formUser.station" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </mx-dialog>
  </div>
</template>

<script>
  import { logger, formatter } from 'mx-app-utils'
  import { MxFormValidateRules } from 'mx-vue-el-utils'
  import MxChooseDictInput from '@/components/mx-choose-dict-input'
  import {t} from '@/locale'

  export default {
    name: 'mx-user-manage',
    components: {MxChooseDictInput},
    props: {
      tableMaxHeight: {
        type: Number,
        default: 540
      }
    },
    data () {
      return {
        t: t,
        tableData: [],
        operate: 'details',
        selected: null,
        formUser: this.newUser(),
        rulesUser: {
          firstName: [MxFormValidateRules.requiredRule({msg: t('rbac.user.validate.requiredFirstName')})],
          lastName: [MxFormValidateRules.requiredRule({msg: t('rbac.user.validate.requiredLastName')})]
        }
      }
    },
    computed: {
      readonly () {
        return this.operate === 'details'
      }
    },
    methods: {
      title () {
        let module = t('rbac.user.module')
        switch (this.operate) {
          case 'add':
            return t('rbac.common.title.add', {module})
          case 'edit':
            return t('rbac.common.title.edit', {module})
          case 'detail':
          default:
            return t('rbac.common.title.details', {module})
        }
      },
      newUser () {
        return {
          id: undefined,
          firstName: '',
          middleName: '',
          lastName: '',
          fullName: '',
          desc: '',
          station: '',
          sex: 'MALE',
          birthday: 0,
          department: undefined
        }
      },
      parseDate (longDate) {
        if (longDate) {
          return formatter.formatDate(longDate)
        } else {
          return t('rbac.common.fields.NA')
        }
      },
      getDepartmentName (department) {
        if (department && department.name) {
          return department.name
        } else {
          return t('NA')
        }
      },
      refreshData (pagination) {
        this.$mxPost('/rest/users', pagination, (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            this.$mxInfo(t('rbac.common.message.refreshSuccess', {module: t('rbac.user.module')}))
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
        this.$refs['dialogPane'].show(operate, '90%')
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
              this.$mxPost(url, {
                id,
                firstName,
                middleName,
                lastName,
                desc,
                station,
                sex,
                birthday,
                department
              }, data => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  this.$mxInfo(t('rbac.common.message.addSuccess', {module: t('rbac.user.module')}))
                }
              })
            } else if (this.operate === 'edit') {
              let url = '/rest/users/' + id
              logger.debug('send PUT "%s".', url)
              this.$mxPut(url, {
                id,
                firstName,
                middleName,
                lastName,
                desc,
                station,
                sex,
                birthday,
                department
              }, data => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  this.$mxInfo(t('rbac.common.message.editSuccess', {module: t('rbac.user.module')}))
                }
              })
            }
          } else {
            this.$mxFormValidateWarn()
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
          case 'details':
            if (!this.selected) {
              this.$mxInfo(t('rbac.common.message.needChoose', {module: t('rbac.user.module')}))
              break
            }
            if (operate === 'delete') {
              let {id} = this.selected
              let url = '/rest/users/' + id
              logger.debug('send DELETE "%s".', url)
              this.$mxDel(url, data => {
                if (data) {
                  this.refreshData(pagination)
                  this.$mxInfo(t('rbac.common.message.deleteSuccess', {module: t('rbac.user.module')}))
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