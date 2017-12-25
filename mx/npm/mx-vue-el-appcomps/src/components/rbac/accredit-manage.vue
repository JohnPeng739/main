<template>
  <div>
    <paginate-pane ref="paginatePane" v-on:buttonHandle="handleButtonClick" :show-edit="false">
      <el-table :data="tableData" :max-height="tableMaxHeight" @current-change="handleCurrentChange"
                highlight-current-row>
        <el-table-column prop="src" :label="$t('rbac.accredit.fields.src')" :width="100">
          <template slot-scope="scope">
            {{getUserName(scope.row.src)}}
          </template>
        </el-table-column>
        <el-table-column prop="roles" :label="$t('rbac.accredit.fields.roles')" :width="150">
          <template slot-scope="scope">
            {{getRoles(scope.row.roles)}}
          </template>
        </el-table-column>
        <el-table-column prop="tar" :label="$t('rbac.accredit.fields.tar')" :width="100">
          <template slot-scope="scope">
            {{getUserName(scope.row.tar)}}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" :label="$t('rbac.accredit.fields.startTime')" :width="100">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.startTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" :label="$t('rbac.accredit.fields.endTime')" :width="100">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.endTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="closed" :label="$t('rbac.accredit.fields.closed')" :width="100">
          <template slot-scope="scope">
            {{scope.row.closed ? $t('rbac.common.fields.closed') : ''}}
          </template>
        </el-table-column>
        <el-table-column prop="desc" :label="$t('rbac.common.fields.desc')"></el-table-column>
      </el-table>
    </paginate-pane>
    <dialog-pane ref="dialogPane" :title="title()" v-on:reset="handleReset" v-on:submit="handleSubmit">
      <el-form ref="formAccredit" slot="form" :model="formAccredit" :rules="rulesAccredit" label-width="100px" class="dialog-form">
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="src" :label="$t('rbac.accredit.fields.src')">
              <el-input v-model="formAccredit.src" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="tar" :label="$t('rbac.accredit.fields.tar')">
              <el-input v-model="formAccredit.tar" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="roles" :label="$t('rbac.accredit.fields.roles')">
              <tag-normal v-model="formAccredit.roles" :disabled="readonly"></tag-normal>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item prop="startTime" :label="$t('rbac.accredit.fields.startTime')">
              <el-date-picker type="datetime" v-model="formAccredit.startTime" :disabled="readonly"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="endTime" :label="$t('rbac.accredit.fields.endTime')">
              <el-date-picker type="datetime" v-model="formAccredit.endTime" :disabled="readonly"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item prop="desc" :label="$t('rbac.common.fields.desc')">
              <el-input type="textarea" v-model="formAccredit.desc" :rows="4" :readonly="readonly"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </dialog-pane>
  </div>
</template>

<script>
  import { logger, formatter } from 'mx-app-utils'
  import { ajax, notify, formValidateRules, TagNormal, PaginatePane, DialogPane } from 'mx-vue-el-utils'

  // TODO 需要使用新的标签控件选择角色

  export default {
    name: 'page-accredit-manage',
    components: {TagNormal, PaginatePane, DialogPane},
    props: {
      tableMaxHeight: {
        type: Number,
        default: 550
      }
    },
    data () {
      return {
        tableData: [],
        operate: 'detail',
        selected: null,
        formAccredit: this.newAccredit(),
        rulesAccredit: {
          src: [formValidateRules.requiredRule({msg: this.$t('rbac.accredit.validate.src')})],
          tar: [formValidateRules.requiredRule({msg: this.$t('rbac.accredit.validate.tar')})],
          roles: [formValidateRules.requiredRule({msg: this.$t('rbac.accredit.validate.roles')})],
          startTime: [formValidateRules.requiredRule({msg: this.$t('rbac.accredit.validate.startTime')})]
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
        let module = this.$t('rbac.accredit.module')
        switch (this.operate) {
          case 'add':
            return this.$t('rbac.common.title.add', {module})
          case 'edit':
            return this.$t('rbac.common.title.edit', {module})
          case 'detail':
          default:
            return this.$t('rbac.common.title.detail', {module})
        }
      },
      newAccredit () {
        return {
          id: undefined,
          src: {id: undefined, code: '', name: ''},
          tar: {id: undefined, code: '', name: ''},
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
          return this.$t('rbac.common.fields.NA')
        }
      },
      getUserName (user) {
        if (user && user.name) {
          return user.name
        } else {
          return this.$t('rbac.common.fields.NA')
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
        ajax.post('/rest/accredits', pagination, (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            notify.info(this.$t('rbac.common.message.refreshSuccess', {module: this.$t('rbac.accredit.module')}))
          }
        })
      },
      showData (data, operate) {
        if (!data) {
          logger.error('未设置有效数据。')
          return
        }
        let {id, src, tar, roles, startTime, endTime, desc} = data
        if (src === null || src === undefined) {
          src = {id: undefined, code: '', name: ''}
        }
        if (tar === null && tar === undefined) {
          tar = {id: undefined, code: '', name: ''}
        }
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
        this.$refs['dialogPane'].show(operate, '80%')
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
              ajax.post(url, {id, src, tar, roles, startTime, endTime, desc}, data => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  notify.info(this.$t('rbac.common.message.addSuccess', {module: this.$t('rbac.accredit.module')}))
                }
              })
            }
            // 授权不支持修改，只支持删除
          } else {
            notify.formValidateWarn()
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
          case 'detail':
            if (!this.selected) {
              notify.info(this.$t('rbac.common.message.needChoose', {module: this.$t('rbac.accredit.module')}))
              break
            }
            if (operate === 'delete') {
              let {id} = this.selected
              let url = '/rest/accredits/' + id
              logger.debug('send DELETE "%s".', url)
              ajax.del(url, data => {
                if (data) {
                  this.refreshData(pagination)
                  notify.info(this.$t('rbac.common.message.deleteSuccess', {module: this.$t('rbac.accredit.module')}))
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
