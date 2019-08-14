<template>
  <div>
    <a-card :bordered="false" class="card-area">
      <div :class="advanced ? 'search' : null">
        <!-- 搜索区域 -->
        <a-form layout="horizontal">
          <div :class="advanced ? null: 'fold'">
            <a-row>
              <a-col :md="8" :sm="24">
                <a-form-item
                  label="文件名称"
                  :labelCol="{span: 5}"
                  :wrapperCol="{span: 18, offset: 1}">
                  <a-input v-model="queryParams.fileName"/>
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item
                  label="时间"
                  :labelCol="{span: 5}"
                  :wrapperCol="{span: 18, offset: 1}">
                  <range-date @change="handleDateChange" ref="createTime"></range-date>
                </a-form-item>
              </a-col>
              <span style="float: right;">
          <a-button type="primary" @click="lineChart">查询</a-button>
        </span>
            </a-row>
          </div>
        </a-form>
      </div>
    </a-card>
    <div id="echart_line"></div>
  </div>
</template>

<script>
    import RangeDate from '@/components/datetime/RangeDate'
    import echarts from 'echarts'
    export default {
        name: "AccessStatistics",
      components: {RangeDate},
      data(){
          return {
            advanced:false,
            queryParams: {
              startTime: '',
              endTime: '',
              fileName:''
            },
          }
      },
      methods:{
        lineChart(){
          // var params = this.queryParams;
          // this.$get('/dashBoard/getFilePvInfo', {
          //   ...params
          // }).then((r) => {
          //   console.log(r);
          // })
            let myChart = echarts.init(document.getElementById("echart_line"));
            let option = {
              tooltip : {      // 设置悬浮出来的数据及结构
                trigger: 'axis',
                show: true,
                formatter: function (val) {   //val.data  是 data这个数组  里面当前悬浮的数字作为下标 对应到data 的一个对象
                  return val[0].name + '</br>'+ val[0].value+'%';    //return 是鼠标悬浮着显示的数据及结构
                }
              },
              calculable : true,
              xAxis : [
                {
                  type : 'category',
                  boundaryGap : false,
                  data : ['2018-12-09', '2018-12-10','2018-12-11'],  //  横坐标的值
                  splitLine: {
                    show: false
                  }
                }
              ],
              yAxis : [
                {
                  type : 'value',
                  axisLabel: {
                    formatter: '{value}%'
                  },
                  splitLine: {
                    show: false
                  }
                }
              ],
              grid:{
                left: '60',
              },
              series : [
                {
                  name:'入驻率',
                  type:'line',
                  smooth:true,
                  itemStyle: {
                    normal: {
                      areaStyle: {
                        type: 'default',
                        color: '#D9EFFC',
                      },
                      lineStyle: {
                        color: '#88CEFE',
                      }
                    }
                  },
                  data: [20,30,40]
                },
              ]
            }
            myChart.setOption(option);
          },
        handleDateChange (value) {
          if (value) {
            this.queryParams.startTime = value[0]
            this.queryParams.endTime = value[1]
          }
        },
      },
      mounted(){
        this.lineChart();
      }
    }
</script>

<style scoped>
  #echart_line{
    width: 1000px;
    height: 500px;
  }
</style>
