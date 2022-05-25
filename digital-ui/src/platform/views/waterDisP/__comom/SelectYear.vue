<template>
  <div class="__select__year__">
    
    <el-radio-group v-model="year" size="mini" v-if="yearData.length>0">
      <el-radio-button
        :label="i"
        v-for="i in yearData"
        :key="i"
      ></el-radio-button>
    </el-radio-group>
    <el-button  v-else :disabled="true">{{currentYear}}</el-button>
    <div class="__btn__">
      <el-button type="primary" @click="goYeear">上一年</el-button>
      <el-button type="primary" @click="backYear">下一年</el-button>
      <el-button type="primary" @click="curYear">今年</el-button>
    </div>
    <span class="icon">
     注：年度为水利供水年度
    </span>
  </div>
</template>
<script>
import request from '@/platform/utils/request'

let propYear = new Date().getFullYear();
export default {
  data() {
    return {
      maxYear: propYear,
      minYear: propYear - 4,
      // 当前年数
      year: propYear,
      currentYear: propYear,
      yearData:[]
    };
  },
  watch: {
    year(v) {
      this.$emit("change-year", v);
      // this.$nextTick(() => {
      //   if (this.year == this.minYear) {
      //     this.currentYear = this.year;
      //     this.minYear = this.currentYear - 4;
      //     this.maxYear = this.currentYear;
      //   } else if (this.currentYear == this.year && this.year < propYear) {
      //     this.maxYear = this.currentYear + 4;
      //     this.minYear = this.currentYear;
      //     this.currentYear = this.maxYear;
      //   }
      // });
    },
  },
  methods: {
    goYeear() {
      if (this.year == this.minYear) {
        return
        // this.currentYear = this.year;
        // this.minYear = this.currentYear - 4;
        // this.maxYear = this.currentYear;
      }
      this.year = this.year - 1;
    },
    backYear() {
      //&& this.year < propYear
      if (this.currentYear == this.year) {
        return
        // this.maxYear = this.currentYear + 4;
        // this.minYear = this.currentYear;
        // this.currentYear = this.maxYear;
      }
      this.year = this.year + 1;
      // if (this.year > propYear) {
      //     this.year = propYear;
      // }
    },
    curYear() {
      this.maxYear = propYear;
      this.minYear = propYear - 4;
      this.year = propYear;
      this.currentYear = propYear;
    },
    getYearData(){
       request.get('/planDispatch/irrWaterApplicationRules/getYear').then(res=>{
         this.yearData = res.data
         this.maxYear = res.data[0]
         this.minYear = res.data[res.data.length-1]
       })
    }
  },
  created(){
   this.getYearData()
  }
};
</script>
<style lang="scss" scoped>
.__select__year__ {
  .icon {
    display: inline-block;
    width: 150px;
    line-height: 28px;
    height: 28px;
    position: absolute;
    right: 20px;
    color: red;
    // i{
    //     vertical-align: middle;
    // }
  }
  display: flex;
  flex-direction: row;
  .__btn__ {
    margin-left: 20px;
  }
}
</style>
