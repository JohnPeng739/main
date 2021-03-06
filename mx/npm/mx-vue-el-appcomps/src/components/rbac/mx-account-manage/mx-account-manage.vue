<template>
  <div>
    <mx-paginate-table ref="paginatePane" v-on:buttonHandle="handleButtonClick" :buttons-layout="buttonsLayout">
      <el-table :data="tableData" class="table" :max-height="tableMaxHeight" @current-change="handleCurrentChange"
                highlight-current-row header-row-class-name="table-header">
        <el-table-column prop="code" :label="$t('common.code')" :width="80"></el-table-column>
        <el-table-column prop="name" :label="$t('common.name')" :width="100">
          <template slot-scope="scope">
            {{scope.row.name}}<br/>
            <span class="reference">{{$t('common.from')}} {{getOwnerName(scope.row.owner)}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="roles" :label="$t('pages.account.fields.roles')" :width="120">
          <template slot-scope="scope">
            {{getRoles(scope.row.roles)}}
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" :label="$t('common.createdTime')" :width="100">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.createdTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="desc" :label="$t('common.desc')"></el-table-column>
      </el-table>
    </mx-paginate-table>
    <mx-dialog ref="dialogPane" :title="title()" v-on:reset="handleReset" v-on:submit="handleSubmit"
               class="layout-dialog">
      <el-form ref="formAccount" slot="form" :model="formAccount" :rules="rulesAccount" label-width="130px"
               class="dialog-form">
        <el-row type="flex">
          <el-col :span="8">
            <el-form-item prop="code" :label="$t('common.code')">
              <el-input v-model="formAccount.code" :readonly="readonly || operate !== 'allocate'"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item v-if="operate === 'allocate'" prop="owner" :label="$t('pages.account.fields.owner')">
              <mx-choose-user-input v-model="formAccount.owner" :disabled="readonly"></mx-choose-user-input>
            </el-form-item>
            <el-form-item v-else prop="name" :label="$t('common.name')">
              <el-input v-model="formAccount.name" :readonly="readonly || operate !== 'allocate'"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="roles" :label="$t('pages.account.fields.roles')">
              <mx-choose-dict-tag v-model="formAccount.roles" :multiple="true" restUrl="/rest/roles" displayFormat="{code} - {name}"
                                  :disabled="readonly"></mx-choose-dict-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="desc" :label="$t('common.desc')">
              <el-input type="textarea" v-model="formAccount.desc" :rows="4" :readonly="readonly"></el-input>
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
  import MxChooseUserInput from '../../rbac/mx-choose-user-input'
  import MxChooseDictTag from '../../mx-choose-dict-tag'

  export default {
    name: 'mx-account-manage',
    components: {MxChooseUserInput, MxChooseDictTag},
    data () {
      return {
        buttonsLayout: [{
          code: 'allocate',
          name: this.$t('pages.account.fields.allocate'),
          icon: 'assignment_ind'
        }, 'edit', 'delete', 'details', 'refresh'],
        tableMaxHeight: 540,
        tableData: [],
        operate: 'details',
        selected: null,
        formAccount: this.newAccount(),
        rulesAccount: {
          code: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('common.code')])})],
          name: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('common.name')])})],
          owner: [MxFormValidateRules.requiredRule({
            msg: this.$t('message.validate.required', [this.$t('pages.account.fields.owner')]),
            trigger: 'change'
          })],
          roles: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('pages.account.fields.roles')])})]
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
        let module = this.$t('pages.account.name')
        switch (this.operate) {
          case 'allocate':
            return this.$t('pages.account.message.allocate')
          case 'edit':
            return this.$t('message.dialog.title.edit', [module])
          case 'details':
          default:
            return this.$t('message.dialog.title.details', [module])
        }
      },
      getRoles (roles) {
        let list = []
        if (roles && roles.length > 0) {
          roles.forEach(role => list.push(role.name))
        }
        return formatter.formatArgs('[%s]', list.join(', '))
      },
      getOwnerName (user) {
        if (user && user.fullName) {
          return user.fullName
        } else {
          return this.$t('common.NA')
        }
      },
      newAccount () {
        return {id: undefined, code: '', name: '', owner: undefined, roles: [], desc: ''}
      },
      parseDatetime (longDate) {
        if (longDate) {
          return formatter.formatDatetime(longDate)
        } else {
          return this.$t('common.NA')
        }
      },
      refreshData (pagination) {
        let fnSuccess = (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            MxNotify.info(this.$t('message.list.refreshSuccess', [this.$t('pages.account.name')]))
          }
        }
        MxAjax.post({url: '/rest/accounts', data: pagination, fnSuccess})
      },
      showData (data, operate) {
        if (!data) {
          logger.error('not set the required data.')
          return
        }
        let {id, code, name, desc, owner, roles} = data
        if (!owner) {
          owner = {}
        }
        if (!roles) {
          roles = []
        }
        this.formAccount = {id, code, name, owner, roles, desc}
        this.operate = operate
        this.$refs['dialogPane'].show(operate, '90%')
        logger.debug('show dialog, operate: %s, data: %j.', operate, data)
      },
      handleSubmit () {
        this.$refs['formAccount'].validate(valid => {
          if (valid) {
            let {id, code, desc, owner, roles} = this.formAccount
            let roleIds = []
            if (roles && roles.length > 0) {
              roles.forEach(role => roleIds.push(role.id))
            }
            if (this.operate === 'allocate') {
              let url = '/rest/users/' + owner.id + '/allocate'
              logger.debug('send POST "%s".', url)
              let fnSuccess = (data) => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  MxNotify.info(this.$t('message.list.addSuccess', [this.$t('pages.account.name')]))
                }
              }
              MxAjax.post({url, data: {code, desc, ownerId: owner.id, roleIds}, fnSuccess})
            } else if (this.operate === 'edit') {
              let url = '/rest/accounts/' + id
              logger.debug('send PUT "%s".', url)
              let fnSuccess = (data) => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  MxNotify.info(this.$t('message.list.editSuccess', [this.$t('pages.account.name')]))
                }
              }
              MxAjax.put({url, data: {id, code, desc, ownerId: owner.id, roleIds}, fnSuccess})
            }
          } else {
            MxNotify.formValidateWarn()
          }
        })
      },
      handleReset () {
        this.$refs['formAccount'].resetFields()
      },
      handleButtonClick (operate, pagination) {
        switch (operate) {
          case 'refresh':
            this.refreshData(pagination)
            break
          case 'allocate':
            this.showData(this.newAccount(), operate)
            break
          case 'edit':
          case 'delete':
          case 'details':
            if (!this.selected) {
              MxNotify.info(this.$t('message.list.needChoose', [this.$t('pages.account.name')]))
              break
            }
            if (operate === 'delete') {
              let {id} = this.selected
              let url = '/rest/accounts/' + id
              logger.debug('send DELETE "%s".', url)
              let fnSuccess = (data) => {
                if (data) {
                  this.refreshData(pagination)
                  MxNotify.info(this.$t('message.list.deleteSuccess', [this.$t('pages.account.name')]))
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
