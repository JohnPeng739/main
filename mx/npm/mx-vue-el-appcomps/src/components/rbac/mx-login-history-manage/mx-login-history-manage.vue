<template>
  <div>
    <mx-paginate-table ref="paginatePane" v-on:buttonHandle="handleButtonClick" :buttons-layout="['details', 'refresh']">
      <el-table :data="tableData" :max-height="tableMaxHeight" @current-change="handleCurrentChange"
                highlight-current-row>
        <el-table-column :label="t('rbac.common.fields.name')" :width="150">
          <template slot-scope="scope">
            {{scope.row.account && scope.row.account.name ? scope.row.account.name : 'NA'}}
          </template>
        </el-table-column>
        <el-table-column prop="loginTime" :label="t('rbac.loginHistory.fields.loginTime')">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.loginTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="logoutTime" :label="t('rbac.loginHistory.fields.logoutTime')">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.logoutTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="online" :label="t('rbac.loginHistory.fields.online')" :width="150">
          <template slot-scope="scope">
            <span v-if="scope.row.online" class="online">{{t('rbac.common.fields.online')}}</span>
            <span v-else class="offline">{{t('rbac.common.fields.offline')}}</span>
          </template>
        </el-table-column>
      </el-table>
    </mx-paginate-table>
    <mx-dialog ref="dialogPane" :title="title()" class="layout-dialog">
      <el-form ref="formLoginHistory" slot="form" :model="formLoginHistory" label-width="130px" class="dialog-form">
        <el-row type="flex">
          <el-col :span="8">
            <el-form-item :label="t('rbac.common.fields.code')">
              <el-input v-model="formLoginHistory.account.code" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="t('rbac.common.fields.name')">
              <el-input v-model="formLoginHistory.account.name" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="t('rbac.loginHistory.fields.online')">
              <el-switch v-model="formLoginHistory.online" :disabled="readonly"></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item :label="t('rbac.loginHistory.fields.loginTime')">
              <el-date-picker type="datetime" v-model="formLoginHistory.loginTime"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('rbac.loginHistory.fields.logoutTime')">
              <el-date-picker type="datetime" v-model="formLoginHistory.logoutTime"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </mx-dialog>
  </div>
</template>

<script>
  import { logger, formatter } from 'mx-app-utils'
  import {t} from '@/locale'

  export default {
    name: 'mx-login-history-manage',
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
        formLoginHistory: {id: undefined, account: {code: '', name: ''}, online: true, loginTime: undefined, logoutTime: undefined}
      }
    },
    computed: {
      readonly () {
        return this.operate === 'details'
      }
    },
    methods: {
      title () {
        let module = t('rbac.loginHistory.module')
        return t('rbac.common.title.details', {module})
      },
      parseDatetime (longDate) {
        if (longDate) {
          return formatter.formatDatetime(longDate)
        } else {
          return t('rbac.common.fields.NA')
        }
      },
      refreshData (pagination) {
        this.$mxPost('/rest/loginHistories', pagination, (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            this.$mxInfo(t('rbac.common.message.refreshSuccess', {module: t('rbac.loginHistory.module')}))
          }
        })
      },
      showData (data, operate) {
        if (!data) {
          logger.error('未设置有效数据。')
          return
        }
        let {id, account, online, loginTime, logoutTime} = data
        if (account == null || account === undefined) {
          account = {code: '', name: ''}
        }
        if (loginTime > 0) {
          loginTime = new Date(loginTime)
        } else {
          loginTime = undefined
        }
        if (logoutTime > 0) {
          logoutTime = new Date(logoutTime)
        } else {
          logoutTime = undefined
        }
        this.formLoginHistory = {id, account, online, loginTime, logoutTime}
        this.operate = operate
        this.$refs['dialogPane'].show(operate, '90%')
        logger.debug('show dialog, operate: %s, data: %j.', operate, data)
      },
      handleButtonClick (operate, pagination) {
        switch (operate) {
          case 'refresh':
            this.refreshData(pagination)
            break
          case 'details':
            if (!this.selected) {
              this.$mxInfo(t('rbac.common.message.needChoose', {module: t('rbac.loginHistory.module')}))
              break
            }
            this.showData(this.selected, operate)
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