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
        fileName:'',
        checked:false
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
        let checked = '${e.target.checked}'
        if(checked){
          this.status = 1
        }else{
          this.status = 0
        }
      },
      onClose () {
        this.loading = false
        this.form.resetFields()
        this.$emit('close')
      },
      setFormValues ({...user}) {
        console.log(user);
        this.fileName = user.fileName;
        if(user.status == '是'){
          this.checked = true;
        }else{
          this.checked = false;
        }
        // this.userId = user.userId
        // let fields = ['username', 'email', 'status', 'ssex', 'mobile']
        // Object.keys(user).forEach((key) => {
        //   if (fields.indexOf(key) !== -1) {
        //     this.form.getFieldDecorator(key)
        //     let obj = {}
        //     obj[key] = user[key]
        //     this.form.setFieldsValue(obj)
        //   }
        // })
        // if (user.roleId) {
        //   this.form.getFieldDecorator('roleId')
        //   let roleArr = user.roleId.split(',')
        //   this.form.setFieldsValue({'roleId': roleArr})
        // }
        // if (user.deptId) {
        //   this.userDept = [user.deptId]
        // }
      },
      handleSubmit () {
        this.form.validateFields((err, values) => {
          if (!err) {
            this.loading = true
            let user = this.form.getFieldsValue()
            user.roleId = user.roleId.join(',')
            user.userId = this.userId
            user.deptId = this.userDept
            this.$put('user', {
              ...user
            }).then((r) => {
              this.loading = false
              this.$emit('success')
              // 如果修改用户就是当前登录用户的话，更新其state
              if (user.username === this.currentUser.username) {
                this.$get(`user/${user.username}`).then((r) => {
                  this.setUser(r.data)
                })
              }
            }).catch(() => {
              this.loading = false
            })
          }
        })
      }
    },
    watch: {
      userEditVisiable () {
        if (this.userEditVisiable) {
          this.$get('role').then((r) => {
            this.roleData = r.data.rows
          })
          this.$get('dept').then((r) => {
            this.deptTreeData = r.data.rows.children
          })
        }
      }
    }
  }
</script>
