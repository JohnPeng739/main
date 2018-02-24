<template>
  <div>
    <mx-paginate-table ref="paginatePane" v-on:buttonHandle="handleButtonClick"
                       :buttons-layout="['details', 'refresh']">
      <el-table :data="tableData" class="table" :max-height="tableMaxHeight" @current-change="handleCurrentChange"
                highlight-current-row header-row-class-name="table-header">
        <el-table-column :label="$t('common.name')" :width="150">
          <template slot-scope="scope">
            {{scope.row.account && scope.row.account.name ? scope.row.account.name : 'NA'}}
          </template>
        </el-table-column>
        <el-table-column prop="loginTime" :label="$t('common.loginTime')">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.loginTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="logoutTime" :label="$t('common.logoutTime')">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.logoutTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="online" :label="$t('common.online')" :width="150">
          <template slot-scope="scope">
            <span v-if="scope.row.online" class="online">{{$t('common.online')}}</span>
            <span v-else class="offline">{{$t('common.offline')}}</span>
          </template>
        </el-table-column>
      </el-table>
    </mx-paginate-table>
    <mx-dialog ref="dialogPane" :title="title()" class="layout-dialog">
      <el-form ref="formLoginHistory" slot="form" :model="formLoginHistory" label-width="130px" class="dialog-form">
        <el-row type="flex">
          <el-col :span="8">
            <el-form-item :label="$t('common.code')">
              <el-input v-model="formLoginHistory.account.code" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('common.name')">
              <el-input v-model="formLoginHistory.account.name" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('common.online')">
              <el-switch v-model="formLoginHistory.online" :disabled="readonly"></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item :label="$t('common.loginTime')">
              <el-date-picker type="datetime" v-model="formLoginHistory.loginTime" :readonly="readonly"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('common.logoutTime')">
              <el-date-picker type="datetime" v-model="formLoginHistory.logoutTime" :readonly="readonly"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </mx-dialog>
  </div>
</template>

<script>
  import { logger, formatter } from 'mx-app-utils'
  import { MxAjax, MxNotify } from 'mx-vue-el-utils'

  export default {
    name: 'mx-login-history-manage',
    data () {
      return {
        tableMaxHeight: 540,
        tableData: [],
        operate: 'details',
        selected: null,
        formLoginHistory: {
          id: undefined,
          account: {code: '', name: ''},
          online: true,
          loginTime: undefined,
          logoutTime: undefined
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
        let module = this.$t('pages.loginHistory.name')
        return this.$t('message.dialog.title.details', [module])
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
            MxNotify.info(this.$t('message.list.refreshSuccess', [this.$t('pages.loginHistory.name')]))
          }
        }
        MxAjax.post({url: '/rest/loginHistories', data: pagination, fnSuccess})
      },
      showData (data, operate) {
        if (!data) {
          logger.error('not set the required data.')
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
              MxNotify.info(this.$t('message.list.needChoose', [this.$t('pages.loginHistory.name')]))
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
      if (!this.$isServer) {
        if (this.$el) {
          this.tableMaxHeight = this.$el.clientHeight - 110
        }
      }
      this.refreshData(null)
    }
  }
</script>
