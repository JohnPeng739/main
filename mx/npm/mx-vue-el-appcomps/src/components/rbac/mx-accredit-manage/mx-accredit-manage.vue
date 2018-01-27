<template>
  <div>
    <mx-paginate-table ref="paginatePane" v-on:buttonHandle="handleButtonClick"
                       :buttonsLayout="['add', 'delete', 'details', 'refresh']">
      <el-table :data="tableData" :max-height="tableMaxHeight" @current-change="handleCurrentChange"
                highlight-current-row>
        <el-table-column prop="src" :label="t('rbac.accredit.fields.src')" :width="100">
          <template slot-scope="scope">
            {{getUserName(scope.row.src)}}
          </template>
        </el-table-column>
        <el-table-column prop="roles" :label="t('rbac.accredit.fields.roles')" :width="150">
          <template slot-scope="scope">
            {{getRoles(scope.row.roles)}}
          </template>
        </el-table-column>
        <el-table-column prop="tar" :label="t('rbac.accredit.fields.tar')" :width="100">
          <template slot-scope="scope">
            {{getUserName(scope.row.tar)}}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" :label="t('rbac.accredit.fields.startTime')" :width="100">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.startTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" :label="t('rbac.accredit.fields.endTime')" :width="100">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.endTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="closed" :label="t('rbac.accredit.fields.closed')" :width="100">
          <template slot-scope="scope">
            <span v-if="scope.row.closed" class="online">{{t('rbac.common.fields.closed')}}</span>
            <span v-else></span>
          </template>
        </el-table-column>
        <el-table-column prop="desc" :label="t('rbac.common.fields.desc')"></el-table-column>
      </el-table>
    </mx-paginate-table>
    <mx-dialog ref="dialogPane" :title="title()" v-on:reset="handleReset" v-on:submit="handleSubmit"
               class="layout-dialog">
      <el-form ref="formAccredit" slot="form" :model="formAccredit" :rules="rulesAccredit" label-width="130px"
               class="dialog-form">
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="src" :label="t('rbac.accredit.fields.src')">
              <choose-dict-input v-model="formAccredit.src" restUrl="/rest/accounts" displayFormat="{code} - {name}"
                                 :disabled="readonly"></choose-dict-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="tar" :label="t('rbac.accredit.fields.tar')">
              <mx-choose-dict-input v-model="formAccredit.tar" restUrl="/rest/accounts" displayFormat="{code} - {name}"
                                    :disabled="readonly"></mx-choose-dict-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="roles" :label="t('rbac.accredit.fields.roles')">
              <mx-choose-dict-tag v-model="formAccredit.roles" restUrl="/rest/roles" displayFormat="{code} - {name}"
                                  :disabled="readonly"></mx-choose-dict-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="startTime" :label="t('rbac.accredit.fields.startTime')">
              <el-date-picker type="datetime" v-model="formAccredit.startTime" :disabled="readonly"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="endTime" :label="t('rbac.accredit.fields.endTime')">
              <el-date-picker type="datetime" v-model="formAccredit.endTime" :disabled="readonly"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="desc" :label="t('rbac.common.fields.desc')">
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
  import { MxFormValidateRules } from 'mx-vue-el-utils'
  import MxChooseDictInput from '@/components/mx-choose-dict-input'
  import MxChooseDictTag from '@/components/mx-choose-dict-tag'
  import { t } from '@/locale'

  export default {
    name: 'mx-accredit-manage',
    components: {MxChooseDictInput, MxChooseDictTag},
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
        formAccredit: this.newAccredit(),
        rulesAccredit: {
          src: [MxFormValidateRules.requiredRule({msg: t('rbac.accredit.validate.src'), trigger: 'change'})],
          tar: [MxFormValidateRules.requiredRule({msg: t('rbac.accredit.validate.tar'), trigger: 'change'})],
          roles: [MxFormValidateRules.requiredRule({msg: t('rbac.accredit.validate.roles'), trigger: 'change'})],
          startTime: [MxFormValidateRules.requiredRule({msg: t('rbac.accredit.validate.startTime')})]
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
        let module = t('rbac.accredit.module')
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
          return t('rbac.common.fields.NA')
        }
      },
      getUserName (user) {
        if (user && user.name) {
          return user.name
        } else {
          return t('rbac.common.fields.NA')
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
        this.$mxPost('/rest/accredits', pagination, (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            this.$mxInfo(t('rbac.common.message.refreshSuccess', {module: t('rbac.accredit.module')}))
          }
        })
      },
      showData (data, operate) {
        if (!data) {
          logger.error('未设置有效数据。')
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
              this.$mxPost(url, {id, src, tar, roles, startTime, endTime, desc}, data => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  this.$mxInfo(t('rbac.common.message.addSuccess', {module: t('rbac.accredit.module')}))
                }
              })
            }
            // 授权不支持修改，只支持删除
          } else {
            this.$mxFormValidateWarn()
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
              this.$mxInfo(t('rbac.common.message.needChoose', {module: t('rbac.accredit.module')}))
              break
            }
            if (operate === 'delete') {
              let {id} = this.selected
              let url = '/rest/accredits/' + id
              logger.debug('send DELETE "%s".', url)
              this.$mxDel(url, data => {
                if (data) {
                  this.refreshData(pagination)
                  this.$mxInfo(t('rbac.common.message.deleteSuccess', {module: t('rbac.accredit.module')}))
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
