<template>
  <div>
    <a-form :form="form">
      <a-form-item
        :label-col="formItemLayout.labelCol"
        :wrapper-col="formItemLayout.wrapperCol"
        label="系统名称"
      >
        <a-input
          v-decorator="[
          'sysName',
          {rules: [{ required: true,message:'请输入系统名称'}]}
        ]"
        />
      </a-form-item>
      <a-form-item
        :label-col="formItemLayout.labelCol"
        :wrapper-col="formItemLayout.wrapperCol"
        label="系统域名"
      >
        <a-input
          v-decorator="[
          'sysDomain',
          {rules: [{ required: true,message:'请输入系统域名'}]}
        ]"
        />
      </a-form-item>
      <a-form-item
        :label-col="formItemLayout.labelCol"
        :wrapper-col="formItemLayout.wrapperCol"
        label="负责人"
      >
        <a-input
          v-decorator="[
          'sysLeader',
          {rules: [{ required: true,message:'请输入负责人'}]}
        ]"
        />
      </a-form-item>
      <a-form-item
        :label-col="formItemLayout.labelCol"
        :wrapper-col="formItemLayout.wrapperCol"
        label="联系电话"
      >
        <a-input
          v-decorator="[
          'mobile',
          {rules: [{ required: true,message:'请输入联系电话'}]}
        ]"
        />
      </a-form-item>
      <a-form-item
        :label-col="formTailLayout.labelCol"
        :wrapper-col="formTailLayout.wrapperCol"
      >
        <a-button
          type="primary"
          @click="check"
        >
          提交
        </a-button>
      </a-form-item>
    </a-form>
    <div style="margin-top: 100px">
      <div style="padding-left: 90px;"><span>文件总个数：</span><span v-text="totalFileNum"></span></div>
      <div style="padding-left: 90px;margin-top: 10px;"><span>文件总大小：</span><span v-text="totalFileSize"></span></div>
    </div>
  </div>
</template>

<script>
  const formItemLayout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 8 },
  };
  const formTailLayout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 8, offset: 4 },
  };
  export default {
    data () {
      return {
        checkNick: false,
        formItemLayout,
        formTailLayout,
        form: this.$form.createForm(this),
        userId:'',
        totalFileNum:'',
        totalFileSize:''
      };
    },
    methods: {
      check  () {
        this.form.validateFields(
          (err) => {
            if (!err) {
              this.loading = true
              let user = this.form.getFieldsValue()
              user.userId = this.userId
              this.$put('user/sysInfo', {
                ...user
              }).then((r) => {
                this.loading = false
                alert('修改成功！')
                location.reload();
              }).catch(() => {
                this.loading = false
              })
            }
          },
        );
      },
      handleChange  (e) {
        this.checkNick = e.target.checked;
        this.$nextTick(() => {
          this.form.validateFields(['nickname'], { force: true });
        });
      },
    },
    mounted(){
      let userName = JSON.parse(localStorage.getItem('USER')).username;
      this.$get('user/'+userName, {}).then((r) => {
        console.log(r);
        let data = r.data
        this.form.setFieldsValue(data);
        this.userId = data.userId;
        this.totalFileNum = data.totalFileNum;
        this.totalFileSize = data.totalFileSize;
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  };
</script>
