<template>
  <div class="app-container">
    <el-row :gutter="20" class="main-row">
      <!--周期类别-->
      <el-col :span="5" :xs="24" class="theme-pannel">
        <div class="theme-pannel__title">周期类别</div>
        <div class="theme-pannel__body">
          <div class="head-container">
            <el-input
              v-model="cycleName"
              placeholder="请输入周期类型"
              clearable
              size="small"
              prefix-icon="el-icon-search"
              style="margin-bottom: 20px"
            />
          </div>
          <div class="head-container">
            <el-tree
              :data="cycleOptions"
              :props="defaultProps"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              ref="timeType"
              node-key="id"
              default-expand-all
              :highlight-current="true"
              @node-click="handleNodeClick"
            >
              <template slot="default" slot-scope="{ node, data }">
                <span class="custom-tree-node">
                  <span>
                    <i v-if="data.id == '0'" class="el-icon-folder icon-tree-btn"></i>
                    <span>{{ node.label }}</span>
                    <!-- 这里需要判断是否是文件夹，否则不能添加。 -->
                    <i
                      class="el-icon-plus icon-tree-btn"
                      v-if="data.id == '0'"
                      @click.stop.prevent="handleClickNodeAdd(data)"
                      v-hasPermi="['inspection:cycle:add']"
                    ></i>
                    <!-- 这里需要判断是否是根节点，否则不能删除、修改。 -->
                    <i
                      class="el-icon-edit icon-tree-btn"
                      v-if="data.id != '0'"
                      @click.stop.prevent="handleClickNodeedit(data, node)"
                      v-hasPermi="['inspection:cycle:edit']"
                    ></i>
                    <i
                      class="el-icon-delete icon-tree-btn"
                      v-if="data.id != '0'"
                      @click.stop.prevent="handleClickNodeDel(data)"
                      v-hasPermi="['inspection:cycle:remove']"
                    ></i>
                  </span>
                </span>
              </template>
            </el-tree>
          </div>
        </div>
      </el-col>
      <!--用户数据-->
      <el-col :span="19" :xs="24" class="main-row-right">
        <el-form ref="form" :model="form" :disabled="disabled" :rules="rules">
          <table class="theme-table-form">
            <colgroup>
              <col width="140" />
              <col />
              <col width="140" />
              <col />
            </colgroup>
            <tr>
              <td :class="themeTableLabelClass(rules.cycleName)">周期名称</td>
              <td class="theme-table-form__input">
                <el-form-item label="周期名称" prop="cycleName">
                  <el-input v-model="form.cycleName" placeholder="请输入周期名称" />
                </el-form-item>
              </td>
              <td :class="themeTableLabelClass(rules.orderNum)">顺序号</td>
              <td class="theme-table-form__input">
                <el-form-item label="顺序号" prop="orderNum">
                  <el-input-number
                    v-model="form.orderNum"
                    controls-position="right"
                    :min="0"
                  />
                </el-form-item>
              </td>
            </tr>
            <tr>
              <td :class="themeTableLabelClass(rules.cycleType)">类型</td>
              <td class="theme-table-form__input">
                <el-form-item label="类型" prop="cycleType">
                  <el-select
                    v-model="form.cycleType"
                    placeholder="请选择"
                    @change="cycleTypeChange"
                  >
                    <el-option
                      v-for="item in typeOptions"
                      :key="item.cycleType"
                      :label="item.cycleTypeName"
                      :value="item.cycleType"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </td>
              <td :class="themeTableLabelClass(rules.timeTypeId)">时段</td>
              <td class="theme-table-form__input">
                <el-form-item label="时段" prop="timeTypeId">
                  <el-select v-model="form.timeTypeId" placeholder="请选择">
                    <el-option
                      v-for="item in timeOptions"
                      :key="item.id"
                      :label="item.typeName"
                      :value="item.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </td>
            </tr>
            <tr v-if="form.cycleType == '2' || form.cycleType == '3'">
              <td :class="themeTableLabelClass(rules.cycleTypeDetail)">不同数据</td>
              <td class="theme-table-form__input" colspan="3">
                <el-form-item label="不同数据" prop="cycleTypeDetail">
                  <el-checkbox-group v-model="form.cycleTypeDetail">
                    <el-checkbox
                      v-for="city in otherData"
                      :label="city.value"
                      :key="city.value"
                      >{{ city.name }}</el-checkbox
                    >
                  </el-checkbox-group>
                </el-form-item>
              </td>
            </tr>
            <tr>
              <td :class="themeTableLabelClass(rules.beginDate)">开始日期</td>
              <td class="theme-table-form__input">
                <el-form-item label="开始日期" prop="beginDate">
                  <el-date-picker
                    v-model="form.beginDate"
                    type="date"
                    value-format="yyyy-MM-dd"
                    placeholder="选择开始日期"
                    :picker-options="pickerOptionsStart"
                  >
                  </el-date-picker>
                </el-form-item>
              </td>
              <td :class="themeTableLabelClass()" v-if="form.cycleType == '4'">
                结束日期
              </td>
              <td class="theme-table-form__input" v-if="form.cycleType == '4'">
                <el-form-item label="结束日期">
                  <el-date-picker
                    v-model="form.endDate"
                    type="date"
                    value-format="yyyy-MM-dd"
                    placeholder="选择结束日期"
                    :picker-options="pickerOptionsEnd"
                  >
                  </el-date-picker>
                </el-form-item>
              </td>
            </tr>
          </table>
          <div v-if="!disabled" style="text-align: center; margin: 10px">
            <el-button type="primary" @click="submitForm()">保存</el-button>
          </div>
        </el-form>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {
  getCycleDict,
  getCycleTypeDict,
  getCycleDetail,
  addCycle,
  updateCycle,
  delCycle,
} from "@/platform/api/engineeringIO/CheckSetting/pollingPeriod";
import { getTimeTypeDict } from "@/platform/api/engineeringIO/CheckSetting/timeDefine";
import ValidateUtil from "@/platform/utils/ValidateUtil.js";

export default {
  name: "pollingPeriod",
  data() {
    return {
      disabled: true,
      // 类型树选项
      cycleOptions: undefined,
      typeOptions: undefined,
      timeOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 时段类别名称
      cycleName: undefined,
      weekData: [
        { value: "1", name: "周一" },
        { value: "2", name: "周二" },
        { value: "3", name: "周三" },
        { value: "4", name: "周四" },
        { value: "5", name: "周五" },
        { value: "6", name: "周六" },
        { value: "7", name: "周七" },
      ],
      monthData: [],
      otherData: [],
      // 表单参数
      form: {},
      defaultProps: {
        children: "childs",
        label: "cycleName",
      },
      // 表单校验
      rules: {
        beginDate: ValidateUtil.required(),
        cycleName: ValidateUtil.required(),
        cycleType: ValidateUtil.required(),
        cycleTypeDetail: ValidateUtil.required(),
        endDate: ValidateUtil.required(),
        orderNum: ValidateUtil.norequiredInt(),
        timeTypeId: ValidateUtil.required(),
      },
      pickerOptionsStart: {
        disabledDate: (time) => {
          let endDateVal = this.form.endDate;
          if (endDateVal) {
            return time.getTime() > new Date(endDateVal).getTime();
          }
        },
      },
      pickerOptionsEnd: {
        disabledDate: (time) => {
          let beginDateVal = this.form.beginDate;
          if (beginDateVal) {
            return time.getTime() < new Date(beginDateVal).getTime();
          }
        },
      },
    };
  },
  watch: {
    // 根据名称筛选
    cycleName(val) {
      this.$refs.timeType.filter(val);
    },
  },
  created() {
    this.getCycleDict();

    this.getSelectOption();
    this.initMonthData();
  },
  methods: {
    //改变不同周期类型显示不同内容
    cycleTypeChange(type) {
      this.form.cycleTypeDetail = [];
      switch (type) {
        case "1":
          break;
        case "2":
          this.otherData = this.weekData;
          break;
        case "3":
          this.otherData = this.monthData;
          break;
        default:
          break;
      }
    },
    // 初始化 月份数据
    initMonthData() {
      this.monthData = [];
      for (let i = 1; i < 32; i++) {
        let obj = {};
        obj.value = `${i}`;
        obj.name = `${i}日`;
        this.monthData.push(obj);
      }
    },
    //查询周期 时段 select
    getSelectOption() {
      getCycleTypeDict().then((res) => {
        this.typeOptions = res.data;
      });
      getTimeTypeDict().then((res) => {
        this.timeOptions = res.data;
      });
    },
    /** 查询类型树结构 */
    getCycleDict() {
      getCycleDict().then((res) => {
        // 添加一个顶级目录
        let obj = [
          {
            childs: res.data,
            cycleName: "周期类型",
            id: "0",
          },
        ];
        this.cycleOptions = obj;
      });
    },
    // 筛选节点
    filterNode(value, data) {
      console.log(data);
      if (!value) return true;
      return data.cycleName.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(row) {
      if (row.id != "0") {
        this.disabled = true;
        getCycleDetail(row.id).then((res) => {
          console.log(res);
          this.cycleTypeChange(res.data.cycleType);
          this.form = res.data;
          this.form.cycleTypeDetail = !res.data.cycleTypeDetail
            ? []
            : res.data.cycleTypeDetail.split(",");
        });
      } else {
        this.reset();
        this.disabled = true;
      }
    },
    // 属性表单重置
    reset() {
      this.form = {
        beginDate: "",
        cycleName: "",
        cycleType: "",
        cycleTypeDetail: "",
        endDate: "",
        orderNum: 0,
        timeTypeId: "",
      };
      this.disabled = false;
      this.resetForm("form");
    },
    // 添加
    handleClickNodeAdd(row) {
      this.reset();
      this.disabled = false;
    },
    // 编辑
    handleClickNodeedit(row, node) {
      this.reset();
      getCycleDetail(row.id).then((res) => {
        console.log(res);
        this.cycleTypeChange(res.data.cycleType);
        this.form = res.data;
        this.form.cycleTypeDetail = !res.data.cycleTypeDetail
          ? []
          : res.data.cycleTypeDetail.split(",");
      });
    },
    handleClickNodeDel(row) {
      this.$confirm('是否确认删除时段类型为"' + row.cycleName + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          delCycle(row.id).then((res) => {
            if (res.code === 200) {
              this.msgSuccess("删除成功");
              this.getCycleDict();
            }
          });
        })
        .catch(function () {});
    },
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          switch (this.form.cycleType) {
            case "1":
              this.form.cycleTypeDetail = "";
              delete this.form["endDate"];
              break;
            case "2":
            case "3":
              delete this.form["endDate"];
              this.form.cycleTypeDetail = this.form.cycleTypeDetail.join(",");
              break;
            case "4":
              this.form.cycleTypeDetail = "";
              break;
            default:
              break;
          }
          if (this.form.id != undefined) {
            console.log(123);
            updateCycle(this.form).then((res) => {
              if (res.code === 200) {
                this.msgSuccess("修改成功");
                this.getCycleDict();
                this.reset();
                this.disabled = true;
              }
            });
          } else {
            console.log(321);
            addCycle(this.form).then((res) => {
              if (res.code === 200) {
                this.msgSuccess("新增成功");
                this.getCycleDict();
                this.reset();
                this.disabled = true;
              }
            });
          }
        }
      });
    },
  },
};
</script>
<style scoped>
.icon-tree-btn {
  margin: 0 5px;
}
.main-row,
.main-row-right {
  height: 100%;
}

.main-row-right {
  overflow: auto;
}

.theme-pannel {
  padding: 0 !important;
  height: 100%;
}

.theme-pannel__body {
  padding: 10px;
  box-sizing: border-box;
  height: calc(100% - 30px);
  overflow: auto;
}

.app-container {
  padding-left: 20px;
}
</style>
