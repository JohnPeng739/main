<template>
  <div>
    <mx-paginate-table ref="paginatePane" v-on:buttonHandle="handleButtonClick" :buttons-layout="buttonsLayout">
      <el-table :data="tableData" class="table" :max-height="tableMaxHeight" @current-change="handleCurrentChange"
                highlight-current-row header-row-class-name="table-header">
        <el-table-column prop="code" :label="$t('rbac.common.fields.code')" :width="80"></el-table-column>
        <el-table-column prop="name" :label="$t('rbac.common.fields.name')" :width="100">
          <template slot-scope="scope">
            {{scope.row.name}}<br/>
            <span class="reference">{{$t('rbac.common.fields.from')}} {{scope.row.owner.name}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="roles" :label="$t('rbac.common.fields.roles')" :width="120">
          <template slot-scope="scope">
            {{getRoles(scope.row.roles)}}
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" :label="$t('rbac.common.fields.createdTime')" :width="100">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.createdTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="desc" :label="$t('rbac.common.fields.desc')"></el-table-column>
      </el-table>
    </mx-paginate-table>
    <mx-dialog ref="dialogPane" :title="title()" v-on:reset="handleReset" v-on:submit="handleSubmit"
               class="layout-dialog">
      <el-form ref="formAccount" slot="form" :model="formAccount" :rules="rulesAccount" label-width="130px"
               class="dialog-form">
        <el-row type="flex">
          <el-col :span="8">
            <el-form-item prop="code" :label="$t('rbac.common.fields.code')">
              <el-input v-model="formAccount.code" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item v-if="operate === 'allocate'" prop="owner" :label="$t('rbac.account.fields.owner')">
              <mx-choose-user-input v-model="formAccount.owner" :disabled="readonly"></mx-choose-user-input>
            </el-form-item>
            <el-form-item v-else prop="name" :label="$t('rbac.common.fields.name')">
              <el-input v-model="formAccount.name" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="roles" :label="$t('rbac.common.fields.roles')">
              <mx-choose-dict-tag v-model="formAccount.roles" restUrl="/rest/roles" displayFormat="{code} - {name}"
                                  :disabled="readonly"></mx-choose-dict-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="desc" :label="$t('rbac.common.fields.desc')">
              <el-input type="textarea" v-model="formAccount.desc" :rows="4" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </mx-dialog>
    <mx-dialog ref="dialogPanePassword" :title="$t('rbac.account.title.password')" v-on:reset="handleResetPassword"
                 v-on:submit="handleSubmitPassword" class="layout-dialog">
      <el-form ref="formPassword" slot="form" :model="formPassword" :rules="rulesPassword" label-width="130px"
               class="dialog-form">
        <el-row type="flex">
          <el-col :span="8">
            <el-form-item prop="code" :label="$t('rbac.common.fields.code')">
              <el-input v-model="formAccount.code" :readonly="true"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="name" :label="$t('rbac.common.fields.name')">
              <el-input v-model="formAccount.name" :readonly="true"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="oldPassword" :label="$t('rbac.account.fields.oldPassword')">
              <el-input type="password" v-model="formPassword.oldPassword"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="password" :label="$t('rbac.account.fields.password')">
              <el-input type="password" v-model="formPassword.password"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="confirm" :label="$t('rbac.account.fields.confirm')">
              <el-input type="password" v-model="formPassword.confirm"></el-input>
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
      let passwordMatch = (rule, value, callback) => {
        let {password, confirm} = this.formPassword
        if (password === confirm) {
          callback()
        } else {
          callback(new Error(this.$t('rbac.account.validate.passwordMatch')))
        }
      }
      return {
        buttonsLayout: [{
          code: 'allocate',
          name: this.$t('rbac.account.title.allocate'),
          icon: 'assignment_ind'
        }, 'edit', 'delete', 'details', {
          code: 'password',
          name: this.$t('rbac.account.title.password'),
          icon: 'lock'
        }, 'refresh'],
        tableMaxHeight: 540,
        tableData: [],
        operate: 'details',
        selected: null,
        formAccount: this.newAccount(),
        rulesAccount: {
          code: [MxFormValidateRules.requiredRule({msg: this.$t('rbac.common.validate.requiredCode')})],
          name: [MxFormValidateRules.requiredRule({msg: this.$t('rbac.common.validate.requiredName')})],
          owner: [MxFormValidateRules.requiredRule({
            msg: this.$t('rbac.account.validate.requiredOwner'),
            trigger: 'change'
          })],
          roles: [MxFormValidateRules.requiredRule({msg: this.$t('rbac.account.validate.requiredRoles')})]
        },
        formPassword: {oldPassword: '', password: '', confirm: ''},
        rulesPassword: {
          oldPassword: [MxFormValidateRules.requiredRule({msg: this.$t('rbac.account.validate.requiredPassword')})],
          password: [MxFormValidateRules.requiredRule({msg: this.$t('rbac.account.validate.requiredPassword')})],
          confirm: [MxFormValidateRules.requiredRule({msg: this.$t('rbac.account.validate.requiredConfirm')}),
            MxFormValidateRules.customRule({validator: passwordMatch})]
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
        let module = this.$t('rbac.account.module')
        switch (this.operate) {
          case 'allocate':
            return this.$t('rbac.account.title.allocate', {module})
          case 'edit':
            return this.$t('rbac.common.title.edit', {module})
          case 'details':
          default:
            return this.$t('rbac.common.title.details', {module})
        }
      },
      getRoles (roles) {
        let list = []
        if (roles && roles.length > 0) {
          roles.forEach(role => list.push(role.name))
        }
        return formatter.formatArgs('[%s]', list.join(', '))
      },
      newAccount () {
        return {id: undefined, code: '', name: '', owner: undefined, roles: [], desc: ''}
      },
      parseDatetime (longDate) {
        if (longDate) {
          return formatter.formatDatetime(longDate)
        } else {
          return this.$t('rbac.common.fields.NA')
        }
      },
      refreshData (pagination) {
        let fnSuccess = (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            MxNotify.info(this.$t('rbac.common.message.refreshSuccess', {module: this.$t('rbac.account.module')}))
          }
        }
        MxAjax.post({url: '/rest/accounts', data: pagination, fnSuccess})
      },
      showData (data, operate) {
        if (!data) {
          logger.error('No data.')
          return
        }
        let {id, code, name, desc, owner, roles} = data
        this.formAccount = {id, code, name, owner, roles, desc}
        this.operate = operate
        this.$refs['dialogPane'].show(operate, '90%')
        logger.debug('show dialog, operate: %s, data: %j.', operate, data)
      },
      handleSubmit () {
        this.$refs['formAccount'].validate(valid => {
          if (valid) {
            let {id, code, name, desc, owner, roles} = this.formAccount
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
                  MxNotify.info(this.$t('rbac.common.message.addSuccess', {module: this.$t('rbac.account.module')}))
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
                  MxNotify.info(this.$t('rbac.common.message.editSuccess', {module: this.$t('rbac.account.module')}))
                }
              }
              MxAjax.put({url, data: {accountId: id, code, name, desc, ownerId: owner.id, roleIds}, fnSuccess})
            }
          } else {
            MxNotify.formValidateWarn()
          }
        })
      },
      handleReset () {
        this.$refs['formAccount'].resetFields()
      },
      handleSubmitPassword (done) {
        this.$refs['formPassword'].validate(valid => {
          if (valid) {
            let {id, code, name} = this.formAccount
            let {oldPassword, password} = this.formPassword
            let url = '/rest/accounts/' + id + '/password/change'
            logger.debug('send POST "%s".', url)
            let fnSuccess = (data) => {
              if (data) {
                MxNotify.info(this.$t('rbac.account.message.changePasswordSuccess', {code, name}))
                done()
              }
            }
            MxAjax.post({url, data: {newPassword: password, oldPassword}, fnSuccess})
          } else {
            MxNotify.formValidateWarn()
          }
        })
      },
      handleResetPassword () {
        this.$refs['formPassword'].resetFields()
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
          case 'password':
            if (!this.selected) {
              MxNotify.info(this.$t('rbac.common.message.needChoose', {module: this.$t('rbac.account.module')}))
              break
            }
            if (operate === 'delete') {
              let {id} = this.selected
              let url = '/rest/accounts/' + id
              logger.debug('send DELETE "%s".', url)
              let fnSuccess = (data) => {
                if (data) {
                  this.refreshData(pagination)
                  MxNotify.info(this.$t('rbac.common.message.deleteSuccess', {module: this.$t('rbac.account.module')}))
                }
              }
              MxAjax.del({url, fnSuccess})
            } else if (operate === 'password') {
              let {id, code, name, password, desc, owner, roles} = this.selected
              this.formAccount = {id, code, name, password, desc, owner, roles}
              this.formPassword = {oldPassword: '', password: '', confirm: ''}
              this.$refs['dialogPanePassword'].show('edit', '90%')
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
