<template>
    <div>
      <form id="form">
        <div class="input_item">
          <label for="fileName">文件名称：</label>
          <input type="text" id="fileName" name="fileName">
        </div>
        <div class="input_item">
          <label for="fileName">上传文件：</label>
          <input type="file" name="file" id="file" @change="getFile($event)">
        </div>
        <div class="input_item">
          <label for="fileName">是否最新版本：</label>
          <input type="checkbox" id="status" name="status" @click="checkSatus" :value="checked">
        </div>
        <input type="hidden" name="userId" value="14">
        <input type="hidden" name="confirm" value="1">
        <button  type="button" @click="submit">提交</button>
      </form>
    </div>
</template>

<script>
    export default {
        name: "Doc",
        data(){
          return {
            checked:'1'
          }
        },
        methods:{getFile(event) {
            this.file = event.target.files[0];
            console.log(this.file);
          },
          checkSatus(){
            if(this.checked == '1'){
              this.checked = '0'
            }else{
              this.checked = '1'
            }
          },
          submit(){
            // let file = document.getElementById('form')
            // let formData = new FormData(file)
            // this.$upload('/oss/upload', formData).then((r) => {
            //   // let data = r.data.data
            //   // if (data.data.length) {
            //   //   this.fetch()
            //   // }
            //   // this.importData = data.data
            //   // this.errors = data.error
            //   // this.times = data.time / 1000
            //   // this.uploading = false
            //   // this.fileList = []
            //   // this.importResultVisible = true
            // }).catch((r) => {
            //   console.error(r)
            //   this.uploading = false
            //   this.fileList = []
            // })
            this.$get('/oss/page').then((r) => {
              console.log(r);
              // let data = r.data.data
              // if (data.data.length) {
              //   this.fetch()
              // }
              // this.importData = data.data
              // this.errors = data.error
              // this.times = data.time / 1000
              // this.uploading = false
              // this.fileList = []
              // this.importResultVisible = true
            }).catch((r) => {
              console.error(r)
              this.uploading = false
              this.fileList = []
            })
          }
        },
        created(){

        }
    }
</script>

<style scoped>
  .input_item{
    margin-bottom: 20px;
  }
</style>
