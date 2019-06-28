<template>
  <a-card :bordered="false" class="card-area">
    <div style="margin-bottom: 20px;">
      <div class="item">
        <span class="tit">文件名称：</span>
        <span class="con" v-text="fileName"></span>
      </div>
      <div class="item">
        <span class="tit">文件地址：</span>
        <span class="con" v-text="fileUrl"></span>
      </div>
      <div class="item">
        <span class="tit">当前在用版本：</span>
        <span class="con" v-text="versionNumber"></span>
      </div>
    </div>
    <div>
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
        <template slot="action" slot-scope="text, record">
          <a-icon v-hasPermission="'oss:setCurrent'" :class="text.status == '使用中'?'setIconHide':''" type="star" theme="twoTone" twoToneColor="#42b983" @click="setCurrentVersion(text.id,text.status)" title="设为当前版本"></a-icon>
          <a-badge v-hasNoPermission="'oss:setCurrent'" status="warning" text="无权限"></a-badge>
          <a-icon v-hasPermission="'oss:update'" type="setting" theme="twoTone" twoToneColor="#4a9ff5"
                  @click="edit(record)" title="修改文件"></a-icon>
          <a-badge v-hasNoPermission="'oss:update'" status="warning" text="无权限"></a-badge>
        </template>
      </a-table>
    </div>
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
  import OssEdit from '../oss/OssEdit'

  export default {
    name: 'OssDetail',
    components: {OssEdit},
    data () {
      return {
        id:'',
        fileName:'',
        fileUrl:'',
        versionNumber:'',
        status:'',
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
        }, {
          title: '文件版本号',
          dataIndex: 'versionNumber'
        }, {
          title: '状态',
          dataIndex: 'status'
        },{
          title: '上传时间',
          dataIndex: 'updateTime'
        },{
          title: '上传人',
          dataIndex: 'author'
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
    created:function(){
      this.getParams();
    },
    watch: {
      // 监测路由变化,只要变化了就调用获取路由参数方法将数据存储本组件即可
      '$route': 'getParams'
    },
    methods: {
      setCurrentVersion:function(id,status){
        if(status == '使用中'){
          alert('已经是当前版本了')
        }else{
          let that = this
          this.$post('/oss/setPresent', {id:id}).then((r) => {
            that.$message.success('设置成功')
            that.fetch()
          }).catch((r) => {
            console.error(r)
          })
        }
      },
      getParams:function(){
        // 取到路由带过来的参数
        var routerParams = this.$route.query
        let HOST = process.env.HOST;
        if(HOST == 'test'){
          routerParams.fileUrl = 'http://10.137.37.68/'+ routerParams.fileUrl
        }else if(HOST == 'prod'){
          // routerParams.fileUrl = 'http://www.invs.com/'+ routerParams.fileUrl
        }else if(HOST == 'dev'){
          routerParams.fileUrl = 'http://127.0.0.1/'+ routerParams.fileUrl
        }
        this.fileName = routerParams.fileName;
        this.fileUrl = routerParams.fileUrl;
        this.versionNumber = routerParams.versionNumber;
        this.id = routerParams.id;
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
        // this.loading = true
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
        // params.userId = JSON.parse(localStorage.getItem('USER')).userId;
        params.parentId = this.id
        this.$get('/oss/getInfoByParentId', {
          ...params
        }).then((r) => {
          console.log(r);
          let data = r.data
          for(let i = 0;i < data.rows.length;i++){
            if(data.rows[i].status == 0){
              data.rows[i].status = '使用中'
            }else{
              data.rows[i].status = '已废弃'
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
  .setIconHide{
    display: none;
  }
</style>
