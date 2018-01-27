<template>
  <div>
    <mx-paginate-table ref="paginatePane" v-on:buttonHandle="handleButtonClick" :buttons-layout="buttonsLayout">
      <el-table :data="tableData" :max-height="tableMaxHeight" @current-change="handleCurrentChange"
                highlight-current-row>
        <el-table-column prop="code" :label="t('rbac.common.fields.code')" :width="80"></el-table-column>
        <el-table-column prop="name" :label="t('rbac.common.fields.name')" :width="100">
          <template slot-scope="scope">
            {{scope.row.name}}<br/>
            <span class="reference">{{t('rbac.common.fields.from')}} {{scope.row.owner.name}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="roles" :label="t('rbac.common.fields.roles')" :width="120">
          <template slot-scope="scope">
            {{getRoles(scope.row.roles)}}
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" :label="t('rbac.common.fields.createdTime')" :width="100">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.createdTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="desc" :label="t('rbac.common.fields.desc')"></el-table-column>
      </el-table>
    </mx-paginate-table>
    <mx-dialog ref="dialogPane" :title="title()" v-on:reset="handleReset" v-on:submit="handleSubmit"
               class="layout-dialog">
      <el-form ref="formAccount" slot="form" :model="formAccount" :rules="rulesAccount" label-width="130px"
               class="dialog-form">
        <el-row type="flex">
          <el-col :span="8">
            <el-form-item prop="code" :label="t('rbac.common.fields.code')">
              <el-input v-model="formAccount.code" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item v-if="operate === 'allocate'" prop="owner" :label="t('rbac.account.fields.owner')">
              <mx-choose-user-input v-model="formAccount.owner" :disabled="readonly"></mx-choose-user-input>
            </el-form-item>
            <el-form-item v-else prop="name" :label="t('rbac.common.fields.name')">
              <el-input v-model="formAccount.name" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="roles" :label="t('rbac.common.fields.roles')">
              <mx-choose-dict-tag v-model="formAccount.roles" restUrl="/rest/roles" displayFormat="{code} - {name}"
                                  :disabled="readonly"></mx-choose-dict-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="desc" :label="t('rbac.common.fields.desc')">
              <el-input type="textarea" v-model="formAccount.desc" :rows="4" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </mx-dialog>
    <dialog-pane ref="dialogPanePassword" :title="t('rbac.account.title.password')" v-on:reset="handleResetPassword"
                 v-on:submit="handleSubmitPassword" class="layout-dialog">
      <el-form ref="formPassword" slot="form" :model="formPassword" :rules="rulesPassword" label-width="130px"
               class="dialog-form">
        <el-row type="flex">
          <el-col :span="8">
            <el-form-item prop="code" :label="t('rbac.common.fields.code')">
              <el-input v-model="formAccount.code" :readonly="true"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="name" :label="t('rbac.common.fields.name')">
              <el-input v-model="formAccount.name" :readonly="true"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="oldPassword" :label="t('rbac.account.fields.oldPassword')">
              <el-input type="password" v-model="formPassword.oldPassword"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="password" :label="t('rbac.account.fields.password')">
              <el-input type="password" v-model="formPassword.password"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="confirm" :label="t('rbac.account.fields.confirm')">
              <el-input type="password" v-model="formPassword.confirm"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </dialog-pane>
  </div>
</template>

<script>
  import { logger, formatter } from 'mx-app-utils'
  import { MxFormValidateRules } from 'mx-vue-el-utils'
  import MxChooseUserInput from '@/components/rbac/mx-choose-user-input'
  import MxChooseDictTag from '@/components/mx-choose-dict-tag'
  import { t } from '@/locale'

  export default {
    name: 'mx-account-manage',
    components: {MxChooseUserInput, MxChooseDictTag},
    props: {
      tableMaxHeight: {
        type: Number,
        default: 540
      }
    },
    data () {
      let passwordMatch = (rule, value, callback) => {
        let {password, confirm} = this.formPassword
        if (password === confirm) {
          callback()
        } else {
          callback(new Error(t('rbac.account.validate.passwordMatch')))
        }
      }
      return {
        t: t,
        buttonsLayout: [{
          code: 'allocate',
          name: t('rbac.account.title.allocate'),
          icon: 'assignment_ind'
        }, 'edit', 'delete', 'details', {
          code: 'password',
          name: t('rbac.account.title.password'),
          icon: 'lock'
        }, 'refresh'],
        tableData: [],
        operate: 'details',
        selected: null,
        formAccount: this.newAccount(),
        rulesAccount: {
          code: [MxFormValidateRules.requiredRule({msg: t('rbac.common.validate.requiredCode')})],
          name: [MxFormValidateRules.requiredRule({msg: t('rbac.common.validate.requiredName')})],
          owner: [MxFormValidateRules.requiredRule({
            msg: t('rbac.account.validate.requiredOwner'),
            trigger: 'change'
          })],
          roles: [MxFormValidateRules.requiredRule({msg: t('rbac.account.validate.requiredRoles')})]
        },
        formPassword: {oldPassword: '', password: '', confirm: ''},
        rulesPassword: {
          oldPassword: [MxFormValidateRules.requiredRule({msg: t('rbac.account.validate.requiredPassword')})],
          password: [MxFormValidateRules.requiredRule({msg: t('rbac.account.validate.requiredPassword')})],
          confirm: [MxFormValidateRules.requiredRule({msg: t('rbac.account.validate.requiredConfirm')}),
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
        let module = t('rbac.account.module')
        switch (this.operate) {
          case 'allocate':
            return t('rbac.account.title.allocate', {module})
          case 'edit':
            return t('rbac.common.title.edit', {module})
          case 'detail':
          default:
            return t('rbac.common.title.details', {module})
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
          return t('rbac.common.fields.NA')
        }
      },
      refreshData (pagination) {
        this.$mxPost('/rest/accounts', pagination, (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            this.$mxInfo(t('rbac.common.message.refreshSuccess', {module: t('rbac.account.module')}))
          }
        })
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
              this.$mxPost(url, {code, desc, ownerId: owner.id, roleIds}, data => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  this.$mxInfo(t('rbac.common.message.addSuccess', {module: t('rbac.account.module')}))
                }
              })
            } else if (this.operate === 'edit') {
              let url = '/rest/accounts/' + id
              logger.debug('send PUT "%s".', url)
              this.$mxPut(url, {accountId: id, code, name, desc, ownerId: owner.id, roleIds}, data => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  this.$mxInfo(t('rbac.common.message.editSuccess', {module: t('rbac.account.module')}))
                }
              })
            }
          } else {
            this.$mxFormValidateWarn()
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
            this.$mxPost(url, {newPassword: password, oldPassword}, data => {
              if (data) {
                this.$mxInfo(t('rbac.account.message.changePasswordSuccess', {code, name}))
                done()
              }
            })
          } else {
            this.$mxFormValidateWarn()
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
              this.$mxInfo(t('rbac.common.message.needChoose', {module: t('rbac.account.module')}))
              break
            }
            if (operate === 'delete') {
              let {id} = this.selected
              let url = '/rest/accounts/' + id
              logger.debug('send DELETE "%s".', url)
              this.$mxDel(url, data => {
                if (data) {
                  this.refreshData(pagination)
                  this.$mxInfo(t('rbac.common.message.deleteSuccess', {module: t('rbac.account.module')}))
                }
              })
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
      this.refreshData(null)
    }
  }
</script>
