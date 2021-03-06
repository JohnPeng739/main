<template>
  <div>
    <mx-paginate-table ref="paginatePane" v-on:buttonHandle="handleButtonClick"
                       :buttonsLayout="['add', 'delete', 'details', 'refresh']">
      <el-table :data="tableData" class="table" :max-height="tableMaxHeight" @current-change="handleCurrentChange"
                highlight-current-row  header-row-class-name="table-header">
        <el-table-column prop="src" :label="$t('common.src')" :width="100">
          <template slot-scope="scope">
            {{getUserName(scope.row.src)}}
          </template>
        </el-table-column>
        <el-table-column prop="roles" :label="$t('pages.account.fields.roles')" :width="150">
          <template slot-scope="scope">
            {{getRoles(scope.row.roles)}}
          </template>
        </el-table-column>
        <el-table-column prop="tar" :label="$t('common.tar')" :width="100">
          <template slot-scope="scope">
            {{getUserName(scope.row.tar)}}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" :label="$t('common.startTime')" :width="100">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.startTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" :label="$t('common.endTime')" :width="100">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.endTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="closed" :label="$t('common.closed')" :width="100">
          <template slot-scope="scope">
            <span v-if="scope.row.closed" class="online">{{$t('common.closed')}}</span>
            <span v-else></span>
          </template>
        </el-table-column>
        <el-table-column prop="desc" :label="$t('common.desc')"></el-table-column>
      </el-table>
    </mx-paginate-table>
    <mx-dialog ref="dialogPane" :title="title()" v-on:reset="handleReset" v-on:submit="handleSubmit"
               class="layout-dialog">
      <el-form ref="formAccredit" slot="form" :model="formAccredit" :rules="rulesAccredit" label-width="130px"
               class="dialog-form">
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="src" :label="$t('common.src')">
              <mx-choose-dict-input v-model="formAccredit.src" restUrl="/rest/accounts" displayFormat="{code} - {name}"
                                 :disabled="readonly"></mx-choose-dict-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="tar" :label="$t('common.tar')">
              <mx-choose-dict-input v-model="formAccredit.tar" restUrl="/rest/accounts" displayFormat="{code} - {name}"
                                    :disabled="readonly"></mx-choose-dict-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="roles" :label="$t('pages.account.fields.roles')">
              <mx-choose-dict-tag v-model="formAccredit.roles" restUrl="/rest/roles" displayFormat="{code} - {name}"
                                  :disabled="readonly"></mx-choose-dict-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="startTime" :label="$t('common.startTime')">
              <el-date-picker type="datetime" v-model="formAccredit.startTime" :readonly="readonly"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="endTime" :label="$t('common.endTime')">
              <el-date-picker type="datetime" v-model="formAccredit.endTime" :readonly="readonly"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="desc" :label="$t('common.desc')">
              <el-input type="textarea" v-model="formAccredit.desc" :rows="4" :readonly="readonly"></el-input>
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
  import MxChooseDictTag from '../../mx-choose-dict-tag'

  export default {
    name: 'mx-accredit-manage',
    components: {MxChooseDictInput, MxChooseDictTag},
    data () {
      return {
        tableMaxHeight: 540,
        tableData: [],
        operate: 'details',
        selected: null,
        formAccredit: this.newAccredit(),
        rulesAccredit: {
          src: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('common.src')]), trigger: 'change'})],
          tar: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('common.tar')]), trigger: 'change'})],
          roles: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('pages.account.fields.roles')]), trigger: 'change'})],
          startTime: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('common.startTime')])})]
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
        let module = this.$t('pages.accredit.name')
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
      newAccredit () {
        return {
          id: undefined,
          src: undefined,
          tar: undefined,
          roles: [],
          startTime: new Date(),
          endTime: undefined,
          desc: ''
        }
      },
      parseDatetime (longDate) {
        if (longDate) {
          return formatter.formatDatetime(longDate)
        } else {
          return this.$t('common.NA')
        }
      },
      getUserName (user) {
        if (user && user.name) {
          return user.name
        } else {
          return this.$t('common.NA')
        }
      },
      getRoles (roles) {
        if (roles && roles instanceof Array && roles.length > 0) {
          let list = []
          roles.forEach(role => list.push(role.name))
          return formatter.format('[%s]', list.join(','))
        } else {
          return '[]'
        }
      },
      refreshData (pagination) {
        let fnSuccess = (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            MxNotify.info(this.$t('message.list.refreshSuccess', [this.$t('pages.accredit.name')]))
          }
        }
        MxAjax.post({url: '/rest/accredits', data: pagination, fnSuccess})
      },
      showData (data, operate) {
        if (!data) {
          logger.error('not set the required data.')
          return
        }
        let {id, src, tar, roles, startTime, endTime, desc} = data
        if (roles === null && roles === undefined) {
          roles = []
        }
        if (startTime && typeof startTime === 'number') {
          startTime = new Date(startTime)
        }
        if (endTime && typeof endTime === 'number') {
          endTime = new Date(endTime)
        }
        this.formAccredit = {id, src, tar, roles, startTime, endTime, desc}
        this.operate = operate
        this.$refs['dialogPane'].show(operate, '90%')
        logger.debug('show dialog, operate: %s, data: %j.', operate, data)
      },
      handleSubmit () {
        this.$refs['formAccredit'].validate(valid => {
          if (valid) {
            let {id, src, tar, roles, startTime, endTime, desc} = this.formAccredit
            if (startTime && startTime instanceof Date) {
              startTime = startTime.getTime()
            }
            if (endTime && endTime instanceof Date) {
              endTime = endTime.getTime()
            }
            if (this.operate === 'add') {
              let url = '/rest/accredits/new'
              logger.debug('send POST "%s".', url)
              let fnSuccess = (data) => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  MxNotify.info(this.$t('message.list.addSuccess', [this.$t('pages.accredit.name')]))
                }
              }
              MxAjax.post({url, data: {id, src, tar, roles, startTime, endTime, desc}, fnSuccess})
            }
            // 授权不支持修改，只支持删除
          } else {
            MxNotify.formValidateWarn()
          }
        })
      },
      handleReset () {
        this.$refs['formAccredit'].resetFields()
      },
      handleButtonClick (operate, pagination) {
        switch (operate) {
          case 'refresh':
            this.refreshData(pagination)
            break
          case 'add':
            this.showData(this.newAccredit(), operate)
            break
          case 'delete':
          case 'details':
            if (!this.selected) {
              MxNotify.info(this.$t('message.list.needChoose', [this.$t('pages.accredit.name')]))
              break
            }
            if (operate === 'delete') {
              let {id} = this.selected
              let url = '/rest/accredits/' + id
              logger.debug('send DELETE "%s".', url)
              let fnSuccess = (data) => {
                if (data) {
                  this.refreshData(pagination)
                  MxNotify.info(this.$t('message.list.deleteSuccess', [this.$t('pages.accredit.name')]))
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
