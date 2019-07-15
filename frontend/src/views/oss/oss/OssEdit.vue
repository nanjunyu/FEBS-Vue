<template>
  <a-drawer
    title="修改文件"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="userEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='文件名称'
                   v-bind="formItemLayout">
        <a-input v-model="fileName"/>
      </a-form-item>
      <a-form-item label='版本号'
                   v-bind="formItemLayout">
        <span v-text="versionNumber"></span>
      </a-form-item>
      <a-form-item label='文件访问地址' v-bind="formItemLayout">
        <a-upload name="file" :fileList="fileList" :remove="handleRemove" :disabled="fileList.length === 1" :beforeUpload="beforeUpload">
          <a-button>
            <a-icon type="upload" /> 上传
          </a-button>
        </a-upload>
      </a-form-item>
      <a-form-item label="是否最新版本" v-bind="formItemLayout">
        <a-checkbox @change="onChange" :checked="checked"></a-checkbox>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
  import {mapState, mapMutations} from 'vuex'

  const formItemLayout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 16 }
  }
  export default {
    name: 'DocEdit',
    props: {
      userEditVisiable: {
        default: false
      }
    },
    data () {
      return {
        formItemLayout,
        form: this.$form.createForm(this),
        deptTreeData: [],
        roleData: [],
        userDept: [],
        userId: '',
        loading: false,
        oldFileName:'',
        fileName:'',
        versionNumber:'',
        oldStatus:'',
        parentId:'',
        id:'',
        uid:'',
        checked:false,
        fileList:[]
      }
    },
    computed: {
      ...mapState({
        currentUser: state => state.account.user
      })
    },
    methods: {
      ...mapMutations({
        setUser: 'account/setUser'
      }),
      onChange(e){
        let checked = e.target.checked
        if(checked){
          this.status = 0
          this.checked = true
        }else{
          this.status = 1
          this.checked = false
        }
      },
      onClose () {
        this.loading = false
        this.form.resetFields()
        this.$emit('close')
      },
      handleRemove (file) {
        if (this.uploading) {
          this.$message.warning('文件导入中，请勿删除')
          return
        }
        const index = this.fileList.indexOf(file)
        const newFileList = this.fileList.slice()
        newFileList.splice(index, 1)
        this.fileList = newFileList
      },
      beforeUpload (file) {
        this.fileList = [...this.fileList, file]
        return false
      },
      setFormValues ({...user}) {
        console.log(user);
        this.id = user.id;
        this.fileName = user.fileName;
        this.oldFileName = user.fileName;
        this.versionNumber = user.versionNumber;
        this.parentId = user.parentId
        if(user.status == '使用中'){
          this.oldStatus = 0
          this.status = 0
          this.checked = true
        }else{
          this.oldStatus = 1
          this.status = 1
          this.checked = false
        }
        let HOST = process.env.HOST;
        if(HOST == 'test'){
          user.fastPath = 'http://10.137.35.134/'+ user.fastPath
        }else if(HOST == 'prod'){
          // user.fastPath = 'http://www.invs.com/'+ user.fastPath
        }else if(HOST == 'dev'){
          // user.fastPath = 'http://127.0.0.1/'+ user.fastPath
          user.fastPath = 'http://10.137.35.134/'+ user.fastPath
        }
        this.fileList = [{
          name:user.fastPath,
          size:user.fileSize,
          uid:user.fileId
        }]
        this.uid = user.fileId
      },
      handleSubmit () {
        if(this.fileName == this.oldFileName && this.status == this.oldStatus && this.uid != this.fileList[0].id){
          this.$emit('close')
        }else{
          let that = this
          let params = {
            id:this.id
          };
          this.$get('/oss/getById', {
            ...params
          }).then((r) => {
            let formData = new FormData();
            formData.append('fileName',this.fileName);
            formData.append('status',this.status);
            if(this.fileName == this.oldFileName && this.status != this.oldStatus && this.uid != this.fileList[0].id){
              formData.append('versionNumber',r.data.maxVersion);
            }else{
              formData.append('versionNumber',(r.data.maxVersion*10 + 1)/10);
            }
            formData.append('id',this.id);
            if(this.parentId){
              formData.append('parentId',this.parentId);    // 如果有父id，则传父id
            }
            if(this.uid != this.fileList[0].id){
              formData.append('file',this.fileList[0]);
            }
            this.$upload2('/oss/updateFile', formData).then((r) => {
              that.$emit('success');
            }).catch((r) => {
              console.error(r)
              this.uploading = false
              this.fileList = []
            })
          })
        }
      }
    },
    watch: {
      // userEditVisiable () {
      //   if (this.userEditVisiable) {
      //     this.$get('role').then((r) => {
      //       this.roleData = r.data.rows
      //     })
      //     this.$get('dept').then((r) => {
      //       this.deptTreeData = r.data.rows.children
      //     })
      //   }
      // }
    }
  }
</script>
