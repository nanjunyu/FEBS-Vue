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
        <a-input v-model="fileName"/>
      </a-form-item>
      <a-form-item label='文件访问地址' v-bind="formItemLayout">
        <a-upload name="file" :fileList="fileList" :beforeUpload="beforeUpload">
          <a-button>
            <a-icon type="upload" /> 上传
          </a-button>
        </a-upload>
      </a-form-item>
      <a-form-item label="是否最新版本" v-bind="formItemLayout">
        <a-checkbox @change="onChange"></a-checkbox>
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
    name: 'DocAdd',
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
        status:''
      }
    },
    methods: {
      onChange(e){
        let checked = '${e.target.checked}'
        if(checked){
          this.status = 1
        }else{
          this.status = 0
        }
      },
      reset () {
        this.validateStatus = ''
        this.help = ''
        this.fileName = ''
        this.loading = false
        this.form.resetFields()
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
        let formData = new FormData()
        formData.append('fileName',this.fileName);
        formData.append('file',fileList[0]);
        formData.append('status',this.status);
        formData.append('userId',JSON.parse(localStorage.getItem('USER')).userId);
        formData.append('confirm','1');
        this.$upload('/oss/upload', formData).then((r) => {
          this.reset()
          this.$emit('success')
        }).catch((r) => {
          console.error(r)
          this.uploading = false
          this.fileList = []
        })
      }
    },
    watch: {

    }
  }
</script>
