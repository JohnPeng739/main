<template>
  <div>
    <mx-paginate-table ref="paginatePane" v-on:buttonHandle="handleButtonClick">
      <el-table :data="tableData" class="table" :max-height="tableMaxHeight" @current-change="handleCurrentChange"
                highlight-current-row header-row-class-name="table-header">
        <el-table-column prop="fullName" :label="$t('common.name')" :width="100"></el-table-column>
        <el-table-column prop="sex" :label="$t('common.gender')" :width="80">
          <template slot-scope="scope">
            <span v-if="scope.row.sex === 'FEMALE'">{{$t('common.FEMALE')}}</span>
            <span v-else-if="scope.row.sex === 'MALE'">{{$t('common.MALE')}}</span>
            <span v-else>{{$t('common.NA')}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="birthday" :label="$t('common.birthday')" :width="100">
          <template slot-scope="scope">
            {{parseDate(scope.row.birthday)}}
          </template>
        </el-table-column>
        <el-table-column prop="department" :label="$t('pages.user.fields.department')" :width="100">
          <template slot-scope="scope">
            {{getDepartmentName(scope.row.department)}}
          </template>
        </el-table-column>
        <el-table-column prop="station" :label="$t('pages.user.fields.station')" :width="120"></el-table-column>
        <el-table-column prop="desc" :label="$t('common.desc')"></el-table-column>
      </el-table>
    </mx-paginate-table>
    <mx-dialog ref="dialogPane" :title="title()" v-on:reset="handleReset" v-on:submit="handleSubmit"
               class="layout-dialog">
      <el-form ref="formUser" slot="form" :model="formUser" :rules="rulesUser" label-width="130px" class="dialog-form">
        <el-row type="flex">
          <el-col :span="8">
            <el-form-item prop="firstName" :label="$t('pages.user.fields.firstName')">
              <el-input v-model="formUser.firstName" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="middleName" :label="$t('pages.user.fields.middleName')">
              <el-input v-model="formUser.middleName" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="lastName" :label="$t('pages.user.fields.lastName')">
              <el-input v-model="formUser.lastName" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="desc" :label="$t('common.desc')">
              <el-input type="textarea" v-model="formUser.desc" :rows="4" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="sex" :label="$t('common.gender')">
              <el-select v-model="formUser.sex" :disabled="readonly" style="width: 100%;">
                <el-option value="FEMALE" :label="$t('common.FEMALE')"></el-option>
                <el-option value="MALE" :label="$t('common.MALE')"></el-option>
                <el-option value="NA" :label="$t('common.NA')"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="birthday" :label="$t('common.birthday')">
              <el-date-picker v-model="formUser.birthday" type="date" :readonly="readonly"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="department" :label="$t('pages.user.fields.department')">
              <mx-choose-dict-input v-model="formUser.department" restUrl="/rest/departments"
                                    displayFormat="{code} - {name}" :disabled="readonly"></mx-choose-dict-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="station" :label="$t('pages.user.fields.station')">
              <el-select v-model="formUser.station" filterable allow-create :disabled="readonly">
                <el-option v-for="item in stations" :key="item" :label="item" :value="item"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </mx-dialog>
  </div>
</template>

<script>
  import { logger, formatter } from 'mx-app-utils'
  import { MxFormValidateRules, MxAjax, MxNotify } from 'mx-vue-el-utils'
  import MxChooseDictInput from '../../mx-choose-dict-input'

  export default {
    name: 'mx-user-manage',
    components: {MxChooseDictInput},
    props: {
      stations: {
        type: Array,
        default: function () {
          return []
        }
      }
    },
    data () {
      return {
        tableMaxHeight: 540,
        tableData: [],
        operate: 'details',
        selected: null,
        formUser: this.newUser(),
        rulesUser: {
          firstName: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('pages.user.fields.firstName')])})],
          lastName: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('pages.user.fields.lastName')])})]
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
        let module = this.$t('pages.user.name')
        switch (this.operate) {
          case 'add':
            return this.$t('message.dialog.title.add', [module])
          case 'edit':
            return this.$t('message.dialog.title.edit', [module])
          case 'details':
          default:
            return this.$t('message.dialog.title.details', [module])
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
          return this.$t('common.NA')
        }
      },
      getDepartmentName (department) {
        if (department && department.name) {
          return department.name
        } else {
          return this.$t('NA')
        }
      },
      refreshData (pagination) {
        let fnSuccess = (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            MxNotify.info(this.$t('message.list.refreshSuccess', [this.$t('pages.user.name')]))
          }
        }
        MxAjax.post({url: '/rest/users', data: pagination, fnSuccess})
      },
      showData (data, operate) {
        if (!data) {
          logger.error('not set the required data.')
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
            let departId
            if (department && department.id) {
              departId = department.id
            }
            if (this.operate === 'add') {
              let url = '/rest/users/new'
              logger.debug('send POST "%s".', url)
              let fnSuccess = (data) => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  MxNotify.info(this.$t('message.list.addSuccess', [this.$t('pages.user.name')]))
                }
              }
              MxAjax.post({
                url,
                data: {
                  id,
                  firstName,
                  middleName,
                  lastName,
                  desc,
                  station,
                  sex,
                  birthday,
                  departId
                },
                fnSuccess
              })
            } else if (this.operate === 'edit') {
              let url = '/rest/users/' + id
              logger.debug('send PUT "%s".', url)
              let fnSuccess = (data) => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  MxNotify.info(this.$t('message.list.editSuccess', [this.$t('pages.user.name')]))
                }
              }
              MxAjax.put({
                url,
                data: {
                  id,
                  firstName,
                  middleName,
                  lastName,
                  desc,
                  station,
                  sex,
                  birthday,
                  departId
                },
                fnSuccess
              })
            }
          } else {
            MxNotify.formValidateWarn()
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
              MxNotify.info(this.$t('message.list.needChoose', [this.$t('pages.user.name')]))
              break
            }
            if (operate === 'delete') {
              let {id} = this.selected
              let url = '/rest/users/' + id
              logger.debug('send DELETE "%s".', url)
              let fnSuccess = (data) => {
                if (data) {
                  this.refreshData(pagination)
                  MxNotify.info(this.$t('message.list.deleteSuccess', [this.$t('pages.user.name')]))
                }
              }
              MxAjax.del({url, fnSuccess})
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
      if (!this.$isServer) {
        if (this.$el) {
          this.tableMaxHeight = this.$el.clientHeight - 110
        }
      }
      this.refreshData(null)
    }
  }
</script>
