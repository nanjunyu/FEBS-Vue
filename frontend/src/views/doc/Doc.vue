<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <!--<a-form layout="horizontal">-->
        <!--<a-row >-->
          <!--<div :class="advanced ? null: 'fold'">-->
            <!--<a-col :md="12" :sm="24" >-->
              <!--<a-form-item-->
                <!--label="文件名称"-->
                <!--:labelCol="{span: 4}"-->
                <!--:wrapperCol="{span: 18, offset: 2}">-->
                <!--<a-input v-model="queryParams.username"/>-->
              <!--</a-form-item>-->
            <!--</a-col>-->
            <!--<template v-if="advanced">-->
              <!--<a-col :md="12" :sm="24" >-->
                <!--<a-form-item-->
                  <!--label="创建时间"-->
                  <!--:labelCol="{span: 4}"-->
                  <!--:wrapperCol="{span: 18, offset: 2}">-->
                  <!--<range-date @change="handleDateChange" ref="createTime"></range-date>-->
                <!--</a-form-item>-->
              <!--</a-col>-->
            <!--</template>-->
          <!--</div>-->
          <!--<span style="float: right; margin-top: 3px;">-->
            <!--<a-button type="primary" @click="search">查询</a-button>-->
          <!--</span>-->
        <!--</a-row>-->
      <!--</a-form>-->
    </div>
    <div>
      <div class="operator">
        <a-button type="primary" ghost @click="add" v-hasPermission="'user:add'">新增</a-button>
        <!--<a-button @click="batchDelete" v-hasPermission="'user:delete'">删除</a-button>-->
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="email" slot-scope="text, record">
          <a-popover placement="topLeft">
            <template slot="content">
              <div>{{text}}</div>
            </template>
            <p style="width: 150px;margin-bottom: 0">{{text}}</p>
          </a-popover>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon v-hasPermission="'user:update'" type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修改用户"></a-icon>
          &nbsp;
          <!--<a-icon v-hasPermission="'user:view'" type="eye" theme="twoTone" twoToneColor="#42b983" @click="view(record)" title="查看"></a-icon>-->
          <a-badge v-hasNoPermission="'user:update','user:view'" status="warning" text="无权限"></a-badge>
        </template>
      </a-table>
    </div>
    <!-- 用户信息查看 -->
    <!--<doc-info-->
      <!--:userInfoData="userInfo.data"-->
      <!--:userInfoVisiable="userInfo.visiable"-->
      <!--@close="handleUserInfoClose">-->
    <!--</doc-info>-->
    <!-- 新增用户 -->
    <doc-add
      @close="handleUserAddClose"
      @success="handleUserAddSuccess"
      :userAddVisiable="userAdd.visiable">
    </doc-add>
    <!-- 修改用户 -->
    <doc-edit
      ref="userEdit"
      @close="handleUserEditClose"
      @success="handleUserEditSuccess"
      :userEditVisiable="userEdit.visiable">
    </doc-edit>
  </a-card>
</template>

<script>
  // import DocInfo from './DocInfo'
  // import DeptInputTree from '../system/dept/DeptInputTree'
  // import RangeDate from '@/components/datetime/RangeDate'
  import DocAdd from './DocAdd'
  import DocEdit from './DocEdit'

  export default {
    name: 'Doc',
    components: {DocAdd, DocEdit},
    data () {
      return {
        advanced: false,
        userInfo: {
          visiable: false,
          data: {}
        },
        userAdd: {
          visiable: false
        },
        userEdit: {
          visiable: false
        },
        queryParams: {},
        filteredInfo: null,
        sortedInfo: null,
        paginationInfo: null,
        dataSource: [],
        selectedRowKeys: [],
        loading: false,
        pagination: {
          pageSizeOptions: ['10', '20', '30', '40', '100'],
          defaultCurrent: 1,
          defaultPageSize: 10,
          showQuickJumper: true,
          showSizeChanger: true,
          showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
        }
      }
    },
    computed: {
      columns () {
        let { sortedInfo, filteredInfo } = this
        sortedInfo = sortedInfo || {}
        filteredInfo = filteredInfo || {}
        return [{
          title: '文件名称',
          dataIndex: 'fileName'
        },{
          title: '当前在用版本',
          dataIndex: 'status'
        },{
          title: '上传时间',
          dataIndex: 'updateTime'
        },{
          title: '上传人',
          dataIndex: 'author'
        }, {
          title: '访问量',
          dataIndex: 'pv'
        }, {
          title: '操作',
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operation' }
        }]
      }
    },
    mounted () {
      this.fetch()
    },
    methods: {
      onSelectChange (selectedRowKeys) {
        this.selectedRowKeys = selectedRowKeys
      },
      // view (record) {
      //   this.userInfo.data = record
      //   this.userInfo.visiable = true
      // },
      add () {
        this.userAdd.visiable = true
      },
      handleUserAddClose () {
        this.userAdd.visiable = false
      },
      handleUserAddSuccess () {
        this.userAdd.visiable = false
        this.$message.success('新增文件成功')
        this.search()
      },
      edit (record) {
        this.$refs.userEdit.setFormValues(record)
        this.userEdit.visiable = true
      },
      handleUserEditClose () {
        this.userEdit.visiable = false
      },
      handleUserEditSuccess () {
        this.userEdit.visiable = false
        this.$message.success('修改用户成功')
        this.search()
      },
      handleUserInfoClose () {
        this.userInfo.visiable = false
      },
      // batchDelete () {
      //   if (!this.selectedRowKeys.length) {
      //     this.$message.warning('请选择需要删除的记录')
      //     return
      //   }
      //   let that = this
      //   this.$confirm({
      //     title: '确定删除所选中的记录?',
      //     content: '当您点击确定按钮后，这些记录将会被彻底删除',
      //     centered: true,
      //     onOk () {
      //       let userIds = []
      //       for (let key of that.selectedRowKeys) {
      //         userIds.push(that.dataSource[key].userId)
      //       }
      //       that.$delete('user/' + userIds.join(',')).then(() => {
      //         that.$message.success('删除成功')
      //         that.selectedRowKeys = []
      //         that.search()
      //       })
      //     },
      //     onCancel () {
      //       that.selectedRowKeys = []
      //     }
      //   })
      // },
      search () {
        let {sortedInfo, filteredInfo} = this
        let sortField, sortOrder
        // 获取当前列的排序和列的过滤规则
        if (sortedInfo) {
          sortField = sortedInfo.field
          sortOrder = sortedInfo.order
        }
        this.fetch({
          sortField: sortField,
          sortOrder: sortOrder,
          ...this.queryParams,
          ...filteredInfo
        })
      },
      handleTableChange (pagination, filters, sorter) {
        // 将这三个参数赋值给Vue data，用于后续使用
        this.paginationInfo = pagination
        this.filteredInfo = filters
        this.sortedInfo = sorter

        this.userInfo.visiable = false
        this.fetch({
          sortField: sorter.field,
          sortOrder: sorter.order,
          ...this.queryParams,
          ...filters
        })
      },
      fetch (params = {}) {
        // 显示loading
        this.loading = true
        if (this.paginationInfo) {
          // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
          this.$refs.TableInfo.pagination.current = this.paginationInfo.current
          this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
          params.pageSize = this.paginationInfo.pageSize
          params.pageNum = this.paginationInfo.current
        } else {
          // 如果分页信息为空，则设置为默认值
          params.pageSize = this.pagination.defaultPageSize
          params.pageNum = this.pagination.defaultCurrent
        }
        this.$get('/oss/page', {
          ...params
        }).then((r) => {
          let data = r.data
          console.log(data);
          for(let i = 0;i < data.rows.length;i++){
            if(data.rows[i].status == 0){
              data.rows[i].status = '是'
            }else{
              data.rows[i].status = '否'
            }
            if(data.rows[i].updateTime){
              data.rows[i].updateTime = data.rows[i].updateTime.split('T')[0]+' '+data.rows[i].updateTime.split('T')[1]
            }else{
              data.rows[i].updateTime = data.rows[i].createTime.split('T')[0]+' '+data.rows[i].createTime.split('T')[1]
            }
          }
          const pagination = { ...this.pagination }
          pagination.total = data.total
          this.dataSource = data.rows
          this.pagination = pagination
          // 数据加载完毕，关闭loading
          this.loading = false
        })
      }
    }
  }
</script>
<style lang="less" scoped>
  @import "../../../static/less/Common";
</style>
