<template>
  <a-drawer
    title="新增文件"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="userAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='文件名称'
                   v-bind="formItemLayout"
                   :validateStatus="validateStatus"
                   :help="help">
        <a-input v-model="fileName" @blur="checkFile"/>
      </a-form-item>
      <a-form-item label='文件上传' v-bind="formItemLayout">
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
  const formItemLayout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 16 }
  }
  export default {
    name: 'OssAdd',
    props: {
      userAddVisiable: {
        default: false
      }
    },
    data () {
      return {
        fileName:'',
        loading: false,
        formItemLayout,
        defaultPassword: '1234qwer',
        form: this.$form.createForm(this),
        validateStatus: '',
        help: '',
        fileList:[],
        status:1,
        checked:false
      }
    },
    methods: {
      checkFile(){
        let that = this
        if(this.fileName){
          let params = {};
          params.fileName = this.fileName;
          this.$get('/oss/check', params).then((r) => {
            if(r.data){
              this.$confirm({
                title: '此文件名已存在，请重新输入',
                centered: true,
                onOk () {
                  that.$emit('succuss')
                  that.fileName = ''
                }
              })
            }
          }).catch((r) => {

          })
        }else{
          this.$confirm({
            title:'文件不能为空',
            centered: true,
            onOk () {
              that.$emit('succuss')
            }
          })
        }
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
      onChange(e){
        if(this.checked){
          this.status = 1
          this.checked = false
        }else{
          this.status = 0
          this.checked = true
        }
      },
      reset () {
        this.fileName = ''
        this.status = 1
        this.checked = false
        this.fileList = []

      },
      onClose () {
        this.reset()
        this.$emit('close')
      },
      beforeUpload (file) {
        this.fileList = [...this.fileList, file]
        return false
      },
      handleSubmit () {
        const { fileList } = this
        let that = this
        if(this.fileName == ''){
          alert('请输入文件名称');
          return false;
        }
        if(fileList.length == 0){
          alert('请上传文件');
          return false;
        }
        let formData = new FormData()
        formData.append('fileName',this.fileName);
        formData.append('file',fileList[0]);
        formData.append('status',this.status);
        // formData.append('userId',JSON.parse(localStorage.getItem('USER')).userId);
        formData.append('versionNumber','1.0');
        this.$upload('/oss/upload', formData).then((r) => {
          that.reset()
          that.$emit('success')
        }).catch((r) => {
          this.uploading = false
          this.fileList = []
        })
      }
    },
    watch: {

    }
  }
</script>
