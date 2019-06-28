<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
    </div>
    <div>
      <div class="operator">
        <a-button type="primary" ghost @click="add" v-hasPermission="'oss:add'">新增</a-button>
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
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
        <!--<template slot="operation" slot-scope="text, record">-->
          <!--<a-icon v-hasPermission="'oss:edit'" type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修改文件"></a-icon>-->
          <!--&nbsp;-->
          <!--&lt;!&ndash;<a-icon v-hasPermission="'user:view'" type="eye" theme="twoTone" twoToneColor="#42b983" @click="view(record)" title="查看"></a-icon>&ndash;&gt;-->
          <!--<a-badge v-hasNoPermission="'oss:update','oss:view'" status="warning" text="无权限"></a-badge>-->
        <!--</template>-->
        <template slot="action" slot-scope="text, record">
          <!--<a href="/#/oss/ossDetail">编辑</a>-->
          <a href="javascript:void(0)" @click="goToSDetails($event,text.fileName,text.fastPath,text.versionNumber,text.id)">查看</a>
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
    <oss-add
      @close="handleUserAddClose"
      @success="handleUserAddSuccess"
      :userAddVisiable="userAdd.visiable">
    </oss-add>
    <!-- 修改用户 -->
    <oss-edit
      ref="userEdit"
      @close="handleUserEditClose"
      @success="handleUserEditSuccess"
      :userEditVisiable="userEdit.visiable">
    </oss-edit>
  </a-card>
</template>

<script>
  // import DocInfo from './DocInfo'
  // import DeptInputTree from '../system/dept/DeptInputTree'
  // import RangeDate from '@/components/datetime/RangeDate'
  import OssAdd from './OssAdd'
  import OssEdit from './OssEdit'

  export default {
    name: 'Oss',
    components: {OssAdd, OssEdit},
    data () {
      return {
        fastPath:'',
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
          title: '编号',
          dataIndex: 'id'
        },{
          title: '文件名称',
          dataIndex: 'fileName'
        },{
          title: '当前是否在用',
          dataIndex: 'status'
        },{
          title: '当前版本',
          dataIndex: 'versionNumber'
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
          key:'action',
          scopedSlots: { customRender: 'action' }
        }]
      }
    },
    mounted () {
      this.fetch()
    },
    methods: {
      goToSDetails:function (e,fileName,fileUrl,versionNumber,id) {

        this.$router.push({
          path:'/oss/ossDetail',
          query:{
            fileName:fileName,
            fileUrl:fileUrl,
            versionNumber:versionNumber,
            id:id
          }
        })
      },
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
        this.$message.success('修改文件成功')
        this.search()
      },
      handleUserInfoClose () {
        this.userInfo.visiable = false
      },
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
        params.userId = JSON.parse(localStorage.getItem('USER')).userId;
        this.$get('/oss/page', {
          ...params
        }).then((r) => {
          console.log(r);
          let data = r.data
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
            if((/(^[1-9]\d*$)/.test(data.rows[i].versionNumber))){
              data.rows[i].versionNumber = data.rows[i].versionNumber + '.0'
            }
          }
          const pagination = { ...this.pagination }
          pagination.total = data.total
          for(let i = 0;i < data.rows.length;i++){
            data.rows[i].index = i;
          }
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
  @import "../../../../static/less/Common";
</style>
