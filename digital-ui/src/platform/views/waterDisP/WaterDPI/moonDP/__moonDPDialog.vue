<template>
  <AutoDialog
    :show="show"
    class="moonDPDialog"
    :title="protitle"
    sizeMode="auto"
    width="1024px"
    :fullscreen.sync="fullscreen"
    :closeOnClickModal="false"
    @close="onClickClose"
  >
    <el-form :model="form" ref="form" :rules="rules">
      <table class="theme-table-form">
        <colgroup>
          <col width="160" />
          <col />
          <col width="160" />
          <col />
        </colgroup>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>用水单位
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="用水单位" prop="waterCompany">
              <el-select
                v-model="form.waterCompany"
                placeholder="请选择用水单位"
                style="width: 310px"
              >
                <el-option
                  v-for="item in useWaterOptions"
                  :key="item.value"
                  :disabled="mode == 'view'"
                  :label="item.companyKey"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            供水年度
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="供水年度" prop="year">
              <el-select
                v-model="waterYear"
                placeholder="请选择供水年度"
                style="width: 310px"
                :disabled="mode == 'view' || !form.waterCompany"
                :value-key="`idValue`"
                @change="changeYear"
              >
                <el-option
                  v-for="item in yearOption"
                  :key="item.idValue"
                  :label="item.yearKey"
                  :value="item"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            月份
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="月份" prop="waterMonth">
              <el-select
                v-model="form.waterMonth"
                placeholder="请选择月份"
                style="width: 310px"
                :disabled="mode == 'view' || !form.waterCompany"
                @change="changeMonth"
              >
                <el-option
                  v-for="item in monthOption"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">用水年月度</td>
          <td class="theme-table-form__input">
            {{ __showYearMonth__ }}
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>指标水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="指标水量" prop="indexWater">
              <el-input
                type="text"
                placeholder="请输入指标水量"
                :disabled="mode == 'view'"
                v-model="form.indexWater"
              >
                <template slot="append">万(m³)</template>
              </el-input>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">状态</td>
          <td class="theme-table-form__input">
            <span>{{ status[form.status] }}</span>
            <!-- <el-form-item label="状态" prop="status">
                            <el-select
                                v-model="form.status"
                                placeholder="请选择状态"
                                :disabled="mode == 'audit' || mode == 'view'"
                                style="width: 220px"
                            >
                                <el-option
                                    v-for="item in statusOption"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value"
                                >
                                </el-option>
                            </el-select>
                        </el-form-item> -->
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>灌溉面积
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="灌溉面积" prop="irrigationArea">
              <el-input
                type="text"
                placeholder="请输入灌溉面积"
                v-model="form.irrigationArea"
                :disabled="mode == 'view'"
              >
                <template slot="append">万(亩)</template>
              </el-input>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>需水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="需水量" prop="needWater">
              <el-input
                type="text"
                placeholder="请输入需水量"
                :disabled="mode == 'view'"
                v-model="form.needWater"
              >
                <template slot="append">万(m³)</template>
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>收益乡镇
          </td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="收益乡镇" prop="profitTown">
              <el-input
                type="text"
                placeholder="请输入收益乡镇"
                :disabled="mode == 'view'"
                v-model="form.profitTown"
              >
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>用途
          </td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="用途" prop="purpose">
              <el-input
                type="text"
                placeholder="请输入用途"
                v-model="form.purpose"
                :disabled="mode == 'view'"
              >
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr v-if="mode !== 'add'">
          <td class="theme-table-form__label">
            驳回原因
          </td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="驳回原因" prop="rejection">
              <el-input
                type="text"
                placeholder=""
                v-model="form.rejection"
                :disabled="mode !== 'audit'"
              >
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>旬计划列表
          </td>
          <td
            class="theme-table-form__input"
            colspan="3"
            style="overflow: auto"
          >
            <!-- <table class="theme-table-form xjh">
                            <colgroup>
                                <col width="60" />
                                <col width="240" />
                                <col width="90" />
                                <col width="110" />
                                <col width="110" />
                                <col width="240" />
                                <col width="90" />
                                <col width="120" />
                                <col width="130" />
                            </colgroup>
                            <tr>
                                <td class="theme-table-form__label">旬</td>
                                <td class="theme-table-form__label">
                                    农业开始日期-结束日期
                                </td>
                                <td class="theme-table-form__label">
                                    农业天数
                                </td>
                                <td class="theme-table-form__label">
                                    农业流量(m³/s)
                                </td>
                                <td class="theme-table-form__label">
                                    农业水量(万m³)
                                </td>
                                <td class="theme-table-form__label">
                                    非农业开始日期-结束日期
                                </td>
                                <td class="theme-table-form__label">
                                    非农业天数
                                </td>
                                <td class="theme-table-form__label">
                                    非农业流量(m³/s)
                                </td>
                                <td class="theme-table-form__label">
                                    非农业水量(万m³)
                                </td>
                            </tr>
                            <tr v-if="_show_">
                                <td class="theme-table-form__label">上旬</td>
                                <td class="theme-table-form__input">
                                    <el-form-item prop="agricultureStart">
                                        <el-date-picker
                                            v-model="earlyTime1"
                                            type="daterange"
                                            :disabled="mode == 'audit' || mode == 'view'"
                                            style="width: 230px"
                                            range-separator="至"
                                            :clearable="false"
                                            value-format="yyyy-MM-dd"
                                            :default-value="eTime"
                                            start-placeholder="开始日期"
                                            end-placeholder="结束日期"
                                            :picker-options="pickerOptions1"
                                            @change="changePickDay('e1')"
                                        >
                                        </el-date-picker>
                                    </el-form-item>
                                </td>
                                <td class="theme-table-form__input">
                                    {{ early.agricultureDays }}
                                </td>
                                <td class="theme-table-form__input">
                                    <el-form-item prop="agricultureFlow">
                                        <el-input
                                            style="width: 80px"
                                            v-model="early.agricultureFlow"
                                            :disabled="mode == 'audit' || mode == 'view'"
                                            placeholder="流量"
                                        ></el-input>
                                    </el-form-item>
                                </td>
                                <td class="theme-table-form__input">
                                    {{ early.agricultureWater }}
                                </td>
                                <td class="theme-table-form__input">
                                    <el-form-item prop="noAgricultureStart">
                                        <el-date-picker
                                            v-model="earlyTime2"
                                            type="daterange"
                                            style="width: 230px"
                                            :disabled="mode == 'audit' || mode == 'view'"
                                            range-separator="至"
                                            :clearable="false"
                                            value-format="yyyy-MM-dd"
                                            :default-value="eTime"
                                            start-placeholder="开始日期"
                                            end-placeholder="结束日期"
                                            :picker-options="pickerOptions1"
                                            @change="changePickDay('e2')"
                                        >
                                        </el-date-picker>
                                    </el-form-item>
                                </td>
                                <td class="theme-table-form__input">
                                    {{ early.noAgricultureDays }}
                                </td>
                                <td class="theme-table-form__input">
                                    <el-input
                                        style="width: 80px"
                                        v-model="early.noAgricultureFlow"
                                        placeholder="流量"
                                    ></el-input>
                                </td>
                                <td class="theme-table-form__input">
                                    {{ early.noAgricultureWater }}
                                </td>
                            </tr>
                            <tr v-if="_show_">
                                <td class="theme-table-form__label">中旬</td>
                                <td class="theme-table-form__input">
                                    <el-date-picker
                                        v-model="midTime1"
                                        type="daterange"
                                        style="width: 230px"
                                        :disabled="mode == 'audit' || mode == 'view'"
                                        range-separator="至"
                                        :clearable="false"
                                        value-format="yyyy-MM-dd"
                                        :default-value="mTime"
                                        start-placeholder="开始日期"
                                        end-placeholder="结束日期"
                                        :picker-options="pickerOptions2"
                                        @change="changePickDay('m1')"
                                    >
                                    </el-date-picker>
                                </td>
                                <td class="theme-table-form__input">
                                    {{ midmonth.agricultureDays }}
                                </td>
                                <td class="theme-table-form__input">
                                    <el-input
                                        style="width: 80px"
                                        :disabled="mode == 'audit' || mode == 'view'"
                                        v-model="midmonth.agricultureFlow"
                                        placeholder="流量"
                                    ></el-input>
                                </td>
                                <td class="theme-table-form__input">
                                    {{ midmonth.agricultureWater }}
                                </td>
                                <td class="theme-table-form__input">
                                    <el-date-picker
                                        v-model="midTime2"
                                        type="daterange"
                                        style="width: 230px"
                                        range-separator="至"
                                        :disabled="mode == 'audit' || mode == 'view'"
                                        :clearable="false"
                                        value-format="yyyy-MM-dd"
                                        :default-value="mTime"
                                        start-placeholder="开始日期"
                                        end-placeholder="结束日期"
                                        :picker-options="pickerOptions2"
                                        @change="changePickDay('m2')"
                                    >
                                    </el-date-picker>
                                </td>
                                <td class="theme-table-form__input">
                                    {{ midmonth.noAgricultureDays }}
                                </td>
                                <td class="theme-table-form__input">
                                    <el-input
                                        style="width: 80px"
                                        v-model="midmonth.noAgricultureFlow"
                                        :disabled="mode == 'audit' || mode == 'view'"
                                        placeholder="流量"
                                    ></el-input>
                                </td>
                                <td class="theme-table-form__input">
                                    {{ midmonth.noAgricultureWater }}
                                </td>
                            </tr>

                            <tr v-if="_show_">
                                <td class="theme-table-form__label">下旬</td>
                                <td class="theme-table-form__input">
                                    <el-date-picker
                                        v-model="lateTime1"
                                        type="daterange"
                                        style="width: 230px"
                                        range-separator="至"
                                        :clearable="false"
                                        :disabled="mode == 'audit' || mode == 'view'"
                                        value-format="yyyy-MM-dd"
                                        :default-value="lTime"
                                        start-placeholder="开始日期"
                                        end-placeholder="结束日期"
                                        :picker-options="pickerOptions3"
                                        @change="changePickDay('l1')"
                                    >
                                    </el-date-picker>
                                </td>
                                <td class="theme-table-form__input">
                                    {{ late.agricultureDays }}
                                </td>
                                <td class="theme-table-form__input">
                                    <el-input
                                        style="width: 80px"
                                        v-model="late.agricultureFlow"
                                        :disabled="mode == 'audit' || mode == 'view'"
                                        placeholder="流量"
                                    ></el-input>
                                </td>
                                <td class="theme-table-form__input">
                                    {{ late.agricultureWater }}
                                </td>
                                <td class="theme-table-form__input">
                                    <el-date-picker
                                        v-model="lateTime2"
                                        type="daterange"
                                        style="width: 230px"
                                        range-separator="至"
                                        :clearable="false"
                                        value-format="yyyy-MM-dd"
                                        :disabled="mode == 'audit' || mode == 'view'"
                                        :default-value="lTime"
                                        start-placeholder="开始日期"
                                        end-placeholder="结束日期"
                                        :picker-options="pickerOptions3"
                                        @change="changePickDay('l2')"
                                    >
                                    </el-date-picker>
                                </td>
                                <td class="theme-table-form__input">
                                    {{ late.noAgricultureDays }}
                                </td>
                                <td class="theme-table-form__input">
                                    <el-input
                                        style="width: 80px"
                                        :disabled="mode == 'audit' || mode == 'view'"
                                        v-model="late.noAgricultureFlow"
                                        placeholder="流量"
                                    ></el-input>
                                </td>
                                <td class="theme-table-form__input">
                                    {{ late.noAgricultureWater }}
                                </td>
                            </tr>
                        </table> -->

            <el-form-item prop="tenDaysList">
              <el-table
                :data="form.tenDaysList"
                height="100%"
                border
                style="min-height: 200px"
              >
                <el-table-column
                  v-for="(v, i) in columns"
                  :key="i"
                  :prop="v.field"
                  :label="v.title"
                  :width="
                    v.field == 'tenDays'
                      ? '70px'
                      : v.type == 'month'
                      ? '255px'
                      : '130px'
                  "
                  align="center"
                >
                  <template slot-scope="scope">
                    <el-form-item
                      :prop="`tenDaysList[${scope.$index}].${v.field}`"
                      :rules="rules.abc"
                      v-if="v.type == 'number'"
                    >
                      <el-input
                        size="mini"
                        :placeholder="`请输入${v.title}`"
                        :disabled="mode == 'view'"
                        type="number"
                        min="0"
                        v-model="scope.row[v.field]"
                        @input="changeInput(scope.row)"
                      ></el-input>
                    </el-form-item>
                    <el-form-item
                      :prop="`tenDaysList[${scope.$index}].${v.field}`"
                      v-else-if="v.type == 'month' && scope.row.tenDays == 1"
                      :rules="{
                        required: true,
                        message: '不能为空',
                        trigger: ['blur', 'change']
                      }"
                    >
                      <el-date-picker
                        v-model="scope.row[v.field]"
                        type="daterange"
                        style="width: 230px"
                        range-separator="至"
                        :disabled="mode == 'view'"
                        :clearable="false"
                        value-format="yyyy-MM-dd"
                        :default-value="Time1"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        :picker-options="pickerOptions1"
                        @change="changePickDay(scope.row)"
                      >
                      </el-date-picker>
                    </el-form-item>
                    <el-form-item
                      :prop="`tenDaysList[${scope.$index}].${v.field}`"
                      v-else-if="v.type == 'month' && scope.row.tenDays == 2"
                      :rules="{
                        required: true,
                        message: '不能为空',
                        trigger: ['blur', 'change']
                      }"
                    >
                      <el-date-picker
                        v-model="scope.row[v.field]"
                        type="daterange"
                        style="width: 230px"
                        range-separator="至"
                        :disabled="mode == 'view'"
                        :clearable="false"
                        value-format="yyyy-MM-dd"
                        :default-value="Time2"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        :picker-options="pickerOptions2"
                        @change="changePickDay(scope.row)"
                      >
                      </el-date-picker>
                    </el-form-item>
                    <el-form-item
                      :prop="`tenDaysList[${scope.$index}].${v.field}`"
                      v-else-if="v.type == 'month' && scope.row.tenDays == 3"
                      :rules="{
                        required: true,
                        message: '不能为空',
                        trigger: ['blur', 'change']
                      }"
                    >
                      <el-date-picker
                        v-model="scope.row[v.field]"
                        type="daterange"
                        style="width: 230px"
                        range-separator="至"
                        :disabled="mode == 'view'"
                        :clearable="false"
                        value-format="yyyy-MM-dd"
                        :default-value="Time3"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        :picker-options="pickerOptions3"
                        @change="changePickDay(scope.row)"
                      >
                      </el-date-picker>
                    </el-form-item>
                    <span v-else-if="v.type == 'readyonly'">
                      {{ scope.row[v.field] }}
                    </span>
                    <span v-else-if="v.type == 'tenDays'">
                      {{ tenDaysArr[scope.row.tenDays] }}
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">申请文件</td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="参数键值" prop="applicationFile">
              <Upload
                v-model="fileList"
                :disabled="!(mode == 'view')"
                :limit="1"
              ></Upload>
            </el-form-item>
          </td>
        </tr>
      </table>
    </el-form>
    <template slot="footer">
      <el-button
        type="primary"
        v-if="mode != 'audit' && mode != 'view'"
        @click="submitForm('form', 0)"
        >保存</el-button
      >
      <el-button
        type="primary"
        v-if="mode != 'audit' && mode != 'view'"
        @click="submitForm('form', 1)"
        >提交</el-button
      ><el-button
        type="primary"
        v-if="mode == 'audit' && model.status == '1' && mode != 'view'"
        @click="aduitForm('2')"
        >通过</el-button
      ><el-button
        type="primary"
        v-if="mode == 'audit' && model.status == '1' && mode != 'view'"
        @click="aduitForm('3')"
        >驳回</el-button
      >
      <el-button @click="onClickClose">取消</el-button>
    </template>
  </AutoDialog>
</template>
<script>
import ValidateUtil from "@/platform/utils/ValidateUtil.js";
import { queryYearPlanDictList } from "@/platform/api/waterDisP/annualDP";
import { queryCompentDicts } from "@/platform/api/waterR/waterInfo";
import {
  uploadAddFile,
  getGroupFile,
  uploadEditFile
} from "@/platform/api/common/common.js";
import Upload from "@/platform/views/waterDisP/__comom/upload";
//guid 生成工具 这个不用看 示例而已 模拟后台返回的guid
let generateGuId = {
  _count: 1,
  get() {
    return +new Date() + "_" + this._count++;
  }
};
export default {
  components: {
    Upload
  },
  props: {
    show: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: ""
    },
    mode: {
      type: String,
      default: "add"
    },
    model: {
      type: Object
    }
  },
  computed: {
    __showYearMonth__() {
      let year = "",
        str = "";
      if (this.form.waterMonth && this.form.year) {
        if (this.form.waterMonth < 7) {
          year = parseInt(this.form.year) + 1;
        } else {
          year = this.form.year;
        }
        str = `${year}年度${this.form.waterMonth}月份`;
      }
      return str;
    }
    // 当不选择 年度 月度 不允许填写数据
    // _show_() {
    //     this.form.tenDaysList = [];
    //     let t = false;
    //     console.log("this.form.year", this.form.year);
    //     if (this.form.waterMonth && this.form.year) {
    //         console.log("this.form.year", this.form.year);
    //         t = true;
    //         for (let i = 0; i < 3; i++) {
    //             let obj = {
    //                 agricultureDays: 0,
    //                 agricultureEnd: "",
    //                 agricultureFlow: 0,
    //                 agricultureStart: "",
    //                 agricultureWater: 0,
    //                 noAgricultureDays: 0,
    //                 noAgricultureEnd: "",
    //                 noAgricultureFlow: 0,
    //                 noAgricultureStart: "",
    //                 noAgricultureWater: 0,
    //                 tenDays: i + 1,
    //             };
    //             this.form.tenDaysList.push(obj);
    //         }
    //     } else {
    //         this.form.tenDaysList = [];
    //     }
    //     return t;
    // },
    // // 上旬默认时间
    // Time3() {
    //     let year = "",
    //         str = [new Date(), new Date()];
    //     if (this.form.waterMonth && this.form.year) {
    //         if (this.form.waterMonth < 7) {
    //             year = parseInt(this.form.year) + 1;
    //         } else {
    //             year = this.form.year;
    //         }
    //         str = [
    //             new Date(year, this.form.waterMonth - 1, 1),
    //             new Date(year, this.form.waterMonth - 1, 10),
    //         ];
    //     }
    //     return str;
    // },
    // // 中旬默认时间
    // Time2() {
    //     let year = "",
    //         str = [new Date(), new Date()];
    //     if (this.form.waterMonth && this.form.year) {
    //         if (this.form.waterMonth < 7) {
    //             year = parseInt(this.form.year) + 1;
    //         } else {
    //             year = this.form.year;
    //         }
    //         str = [
    //             new Date(year, this.form.waterMonth - 1, 11),
    //             new Date(year, this.form.waterMonth - 1, 20),
    //         ];
    //     }
    //     return str;
    // },
    // // 上旬默认时间
    // Time1() {
    //     let year = "",
    //         str = [new Date(), new Date()];
    //     if (this.form.waterMonth && this.form.year) {
    //         if (this.form.waterMonth < 7) {
    //             year = parseInt(this.form.year) + 1;
    //         } else {
    //             year = this.form.year;
    //         }
    //         str = [
    //             new Date(year, this.form.waterMonth - 1, 21),
    //             new Date(
    //                 year,
    //                 this.form.waterMonth - 1,
    //                 new Date(year, this.form.waterMonth - 1, 0).getDate()
    //             ),
    //         ];
    //     }
    //     return str;
    // },
  },
  data() {
    return {
      fullscreen: false,
      protitle: this.title,
      fileList: [],
      visible: false,
      waterYear: {
        idValue: "",
        yearKey: ""
      },
      pickerOptions1: {
        disabledDate(time) {
          let cur = new Date(
              new Date().getFullYear(),
              new Date().getMonth(),
              1
            ),
            up = new Date(new Date().getFullYear(), new Date().getMonth(), 10);
          return (
            time.getTime() < cur.getTime() || time.getTime() > up.getTime()
          ); //大于当前的禁止，小于7天前的禁止
        }
      },
      pickerOptions2: {
        disabledDate(time) {
          let cur = new Date(
              new Date().getFullYear(),
              new Date().getMonth(),
              11
            ),
            up = new Date(new Date().getFullYear(), new Date().getMonth(), 20);
          return (
            time.getTime() < cur.getTime() || time.getTime() > up.getTime()
          ); //大于当前的禁止，小于7天前的禁止
        }
      },
      pickerOptions3: {
        disabledDate(time) {
          let cur = new Date(
              new Date().getFullYear(),
              new Date().getMonth(),
              21
            ),
            up = new Date(
              new Date().getFullYear(),
              new Date().getMonth(),
              new Date(
                new Date().getFullYear(),
                new Date().getMonth(),
                0
              ).getDate()
            );
          return (
            time.getTime() < cur.getTime() || time.getTime() > up.getTime()
          ); //大于当前的禁止，小于7天前的禁止
        }
      },
      // 上旬 农业时间
      earlyTime1: null,
      // 非农业时间
      earlyTime2: null,
      // 中旬 农业时间
      midTime1: null,
      // 非农业时间
      midTime2: null,
      // 下旬 农业时间
      lateTime1: null,
      // 非农业时间
      lateTime2: null,
      form: {
        id: "",
        applicationFile: "",
        indexWater: 0,
        irrigationArea: 0,
        needWater: 0,
        profitTown: "",
        purpose: "",
        status: "0",
        tenDaysList: [],
        waterCompany: "",
        waterMonth: "",
        year: "",
        yearId: ""
      },
      Time1: [],
      Time2: [],
      Time3: [],
      columns: [
        { field: "tenDays", title: "旬", type: "tenDays" },
        { field: "time1", title: "农业开始结束日期", type: "month" },
        {
          field: "agricultureDays",
          title: "农业天数",
          type: "readyonly"
        },
        {
          field: "agricultureFlow",
          title: "农业流量(m³/s)",
          type: "number"
        },
        {
          field: "agricultureWater",
          title: "农业水量(万m³)",
          type: "readyonly"
        },
        { field: "time2", title: "非农业开始结束日期", type: "month" },
        {
          field: "noAgricultureDays",
          title: "非农业天数",
          type: "readyonly"
        },
        {
          field: "noAgricultureFlow",
          title: "非农业流量(m³/s)",
          type: "number"
        },
        {
          field: "noAgricultureWater",
          title: "非农业水量(万m³)",
          type: "readyonly"
        }
      ],
      tenDaysArr: ["", "上旬", "中旬", "下旬"],
      early: {
        agricultureDays: 0,
        agricultureEnd: "",
        agricultureFlow: 0,
        agricultureStart: "",
        agricultureWater: 0,
        noAgricultureDays: 0,
        noAgricultureEnd: "",
        noAgricultureFlow: 0,
        noAgricultureStart: "",
        noAgricultureWater: 0,
        tenDays: "1"
      },
      midmonth: {
        agricultureDays: 0,
        agricultureEnd: "",
        agricultureFlow: 0,
        agricultureStart: "",
        agricultureWater: 0,
        noAgricultureDays: 0,
        noAgricultureEnd: "",
        noAgricultureFlow: 0,
        noAgricultureStart: "",
        noAgricultureWater: 0,
        tenDays: "2"
      },
      late: {
        agricultureDays: 0,
        agricultureEnd: "",
        agricultureFlow: 0,
        agricultureStart: "",
        agricultureWater: 0,
        noAgricultureDays: 0,
        noAgricultureEnd: "",
        noAgricultureFlow: 0,
        noAgricultureStart: "",
        noAgricultureWater: 0,
        tenDays: "3"
      },
      rules: {
        waterCompany: ValidateUtil.required(),
        waterYear: ValidateUtil.required(),
        waterMonth: ValidateUtil.required(),
        status: ValidateUtil.required(),
        purpose: ValidateUtil.requiredLength(0, 50),
        profitTown: ValidateUtil.requiredLength(0, 50),
        needWater: ValidateUtil.requiredPercent(0, 999999),
        indexWater: ValidateUtil.requiredPercent(0, 999999),
        irrigationArea: ValidateUtil.requiredPercent(0, 999999),
        year: ValidateUtil.required(),
        abc: ValidateUtil.requiredPercent(0, 999999),
        tenDaysList: ValidateUtil.requiredArray()
      },
      status: ["草稿", "待审批", "已审批", "已驳回"],
      statusOption: [
        {
          value: "0",
          label: "草稿"
        },
        {
          value: "1",
          label: "待审批"
        },
        {
          value: "2",
          label: "已审批"
        },
        {
          value: "3",
          label: "已驳回"
        }
      ],
      curMonthOptions: [],
      yearOption: [],
      useWaterOptions: [],
      monthOption: [
        {
          value: "7",
          label: "7月"
        },
        {
          value: "8",
          label: "8月"
        },
        {
          value: "9",
          label: "9月"
        },
        {
          value: "10",
          label: "10月"
        },
        {
          value: "11",
          label: "11月"
        },
        {
          value: "12",
          label: "12月"
        },
        {
          value: "1",
          label: "1月"
        },
        {
          value: "2",
          label: "2月"
        },
        {
          value: "3",
          label: "3月"
        },
        {
          value: "4",
          label: "4月"
        },
        {
          value: "5",
          label: "5月"
        },
        {
          value: "6",
          label: "6月"
        }
      ],
      l: 86400
    };
  },
  created() {
    this.$nextTick(() => {
      this.curMonthOptions = this.monthOptions;
      queryCompentDicts()
        .then(res => {
          if (res.code !== 200) {
            return this.$message.error(res.msg);
          }
          this.useWaterOptions = res.data;
        })
        .then(() => {
          queryYearPlanDictList().then(res => {
            if (res.code !== 200) {
              return this.$message.error(res.msg);
            }
            this.yearOption = res.data;
            if (this.model) {
              console.log(this.model);
              this.form = JSON.parse(JSON.stringify(this.model));
              this.dynamicDateOpt(this.form.year, this.form.waterMonth);
              this.waterYear.idValue = this.model.yearId;
              this.initShow();
              if (this.form.applicationFile) {
                getGroupFile(this.form.applicationFile).then(res => {
                  console.log(res);
                  this.fileList = res.data;
                });
              }
            }
          });
        });
    });
  },
  watch: {
    "form.waterCompany": {
      handler: function(a, b) {
        queryYearPlanDictList({ companyId: a }).then(res => {
          if (res.code !== 200) {
            return this.$message.error(res.msg);
          }
          this.yearOption = res.data;
        });
      },
      deep: true
    },
    "early.agricultureDays"(v) {
      this.early.agricultureWater = v * this.early.agricultureFlow * this.l;
    },
    "early.agricultureFlow"(v) {
      this.early.agricultureWater = this.early.agricultureDays * v * this.l;
    },
    "early.noAgricultureDays"(v) {
      this.early.noAgricultureWater = v * this.early.noAgricultureFlow * this.l;
    },
    "early.noAgricultureFlow"(v) {
      this.early.noAgricultureWater = this.early.noAgricultureDays * v * this.l;
    },

    "midmonth.agricultureDays"(v) {
      this.midmonth.agricultureWater =
        v * this.midmonth.agricultureFlow * this.l;
    },
    "midmonth.agricultureFlow"(v) {
      this.midmonth.agricultureWater =
        this.midmonth.agricultureDays * v * this.l;
    },

    "midmonth.noAgricultureDays"(v) {
      this.midmonth.noAgricultureWater =
        v * this.midmonth.noAgricultureFlow * this.l;
    },
    "midmonth.noAgricultureFlow"(v) {
      this.midmonth.noAgricultureWater =
        this.midmonth.noAgricultureDays * v * this.l;
    },
    "late.agricultureDays"(v) {
      this.late.agricultureWater = v * this.late.agricultureFlow * this.l;
    },
    "late.agricultureFlow"(v) {
      this.late.agricultureWater = this.late.agricultureDays * v * this.l;
    },
    "late.noAgricultureDays"(v) {
      this.late.noAgricultureWater = v * this.late.noAgricultureFlow * this.l;
    },
    "late.noAgricultureFlow"(v) {
      this.late.noAgricultureWater = this.late.noAgricultureDays * v * this.l;
    }
  },
  methods: {
    // 编辑回显 显示
    initShow() {
      // let t = this.form.tenDaysList;
      // const ns = ["", "early", "midmonth", "late"];
      // t.forEach((n) => {
      //     this[ns[n.tenDays]] = n;
      // });
      // this.earlyTime1 = [
      //     this.early.agricultureStart,
      //     this.early.agricultureEnd,
      // ];
      // this.earlyTime2 = [
      //     this.early.noAgricultureStart,
      //     this.early.noAgricultureEnd,
      // ];
      // this.midTime1 = [
      //     this.midmonth.agricultureStart,
      //     this.midmonth.agricultureEnd,
      // ];
      // this.midTime2 = [
      //     this.midmonth.noAgricultureStart,
      //     this.midmonth.noAgricultureEnd,
      // ];
      // this.lateTime1 = [
      //     this.late.agricultureStart,
      //     this.late.agricultureEnd,
      // ];
      // this.lateTime2 = [
      //     this.late.noAgricultureStart,
      //     this.late.noAgricultureEnd,
      // ];

      console.log(this.form.tenDaysList);
      let t = this.form.tenDaysList;
      let arr = [];
      t.forEach(n => {
        this.$set(n, "time1", [n.agricultureStart, n.agricultureEnd]);
        this.$set(n, "time2", [n.noAgricultureStart, n.noAgricultureEnd]);
        arr.push(n);
      });
      console.log(arr);
      this.form.tenDaysList = arr;
    },
    //输入流量 计算水量
    changeInput(row) {
      row.agricultureWater = (
        (row.agricultureDays * row.agricultureFlow * this.l) /
        10000
      ).toFixed(2);
      row.noAgricultureWater = (
        (row.noAgricultureDays * row.noAgricultureFlow * this.l) /
        10000
      ).toFixed(2);
    },
    // 计算选着时间天数
    changePickDay(row) {
      if (row.time1) {
        row.agricultureDays =
          (new Date(row.time1[1]).getTime() -
            new Date(row.time1[0]).getTime()) /
            (1 * 24 * 60 * 60 * 1000) +
          1;
        row.agricultureStart = row.time1[0];
        row.agricultureEnd = row.time1[1];
        row.agricultureWater = (
          (row.agricultureDays * row.agricultureFlow * this.l) /
          10000
        ).toFixed(2);
      }
      if (row.time2) {
        row.noAgricultureDays =
          (new Date(row.time2[1]).getTime() -
            new Date(row.time2[0]).getTime()) /
            (1 * 24 * 60 * 60 * 1000) +
          1;
        row.noAgricultureStart = row.time2[0];
        row.noAgricultureEnd = row.time2[1];
        row.noAgricultureWater = (
          (row.noAgricultureDays * row.noAgricultureFlow * this.l) /
          10000
        ).toFixed(2);
      }
      // // 1 上旬 2 中旬 3 下旬
      // if (type == "e1") {
      //     this.early.agricultureDays =
      //         (new Date(this.earlyTime1[1]).getTime() -
      //             new Date(this.earlyTime1[0]).getTime()) /
      //             (1 * 24 * 60 * 60 * 1000) +
      //         1;
      //     this.early.agricultureStart = this.earlyTime1[0];
      //     this.early.agricultureEnd = this.earlyTime1[1];
      // } else if (type == "e2") {
      //     this.early.noAgricultureDays =
      //         (new Date(this.earlyTime2[1]).getTime() -
      //             new Date(this.earlyTime2[0]).getTime()) /
      //             (1 * 24 * 60 * 60 * 1000) +
      //         1;
      //     this.early.noAgricultureStart = this.earlyTime2[0];
      //     this.early.noAgricultureEnd = this.earlyTime2[1];
      // } else if (type == "m1") {
      //     this.midmonth.agricultureDays =
      //         (new Date(this.midTime1[1]).getTime() -
      //             new Date(this.midTime1[0]).getTime()) /
      //             (1 * 24 * 60 * 60 * 1000) +
      //         1;
      //     this.midmonth.agricultureStart = this.midTime1[0];
      //     this.midmonth.agricultureEnd = this.midTime1[1];
      // } else if (type == "m2") {
      //     this.midmonth.noAgricultureDays =
      //         (new Date(this.midTime2[1]).getTime() -
      //             new Date(this.midTime2[0]).getTime()) /
      //             (1 * 24 * 60 * 60 * 1000) +
      //         1;
      //     this.midmonth.noAgricultureStart = this.midTime2[0];
      //     this.midmonth.noAgricultureEnd = this.midTime2[1];
      // } else if (type == "l1") {
      //     this.late.agricultureDays =
      //         (new Date(this.lateTime1[1]).getTime() -
      //             new Date(this.lateTime1[0]).getTime()) /
      //             (1 * 24 * 60 * 60 * 1000) +
      //         1;
      //     this.late.agricultureStart = this.lateTime1[0];
      //     this.late.agricultureEnd = this.lateTime1[1];
      // } else if (type == "l2") {
      //     this.late.noAgricultureDays =
      //         (new Date(this.lateTime2[1]).getTime() -
      //             new Date(this.lateTime2[0]).getTime()) /
      //             (1 * 24 * 60 * 60 * 1000) +
      //         1;
      //     this.late.noAgricultureStart = this.lateTime2[0];
      //     this.late.noAgricultureEnd = this.lateTime2[1];
      // }
    },
    changeYear(row) {
      this.form.yearId = row.idValue;
      this.form.year = row.yearKey;
      this.dynamicDateOpt(row.yearKey, this.form.waterMonth);
      this.initTenDaysList(row.yearKey, this.form.waterMonth);
    },
    changeMonth(row) {
      this.dynamicDateOpt(this.form.year, row);
      this.initTenDaysList(this.form.year, row);
    },
    initTenDaysList(year, month) {
      this.form.tenDaysList = [];
      if (year && month) {
        for (let i = 0; i < 3; i++) {
          let obj = {
            agricultureDays: 0,
            agricultureEnd: "",
            agricultureFlow: 0,
            agricultureStart: "",
            agricultureWater: 0,
            noAgricultureDays: 0,
            noAgricultureEnd: "",
            noAgricultureFlow: 0,
            noAgricultureStart: "",
            noAgricultureWater: 0,
            tenDays: i + 1
          };
          this.form.tenDaysList.push(obj);
        }
      } else {
        this.form.tenDaysList = [];
      }
    },
    //动态改变上下旬日期选择
    dynamicDateOpt(year, month) {
      let y = year || new Date().getFullYear();
      let m = month || new Date().getMonth() + 1;

      if (m < 7) {
        y = parseInt(y) + 1;
      }
      console.log(1223123);
      this.pickerOptions1 = {
        disabledDate(time) {
          let cur = new Date(y, m - 1, 1),
            up = new Date(y, m - 1, 10);
          return (
            time.getTime() < cur.getTime() || time.getTime() > up.getTime()
          ); //大于当前的禁止，小于7天前的禁止
        }
      };
      this.pickerOptions2 = {
        disabledDate(time) {
          let cur = new Date(y, m - 1, 11),
            up = new Date(y, m - 1, 20);
          return (
            time.getTime() < cur.getTime() || time.getTime() > up.getTime()
          ); //大于当前的禁止，小于7天前的禁止
        }
      };
      this.pickerOptions3 = {
        disabledDate(time) {
          let cur = new Date(y, m - 1, 21),
            up = new Date(y, m - 1, new Date(y, m - 1, 0).getDate());
          return (
            time.getTime() < cur.getTime() || time.getTime() > up.getTime()
          ); //大于当前的禁止，小于7天前的禁止
        }
      };

      this.Time1 = [new Date(y, m - 1, 1), new Date(y, m - 1, 10)];
      this.Time2 = [new Date(y, m - 1, 11), new Date(y, m - 1, 20)];
      this.Time3 = [
        new Date(y, m - 1, 21),
        new Date(y, m - 1, new Date(y, m - 1, 0).getDate())
      ];

      // 当改变 月份或者年份的时候 绑定的值应当取消重新选择 一遍上报正确
      // 对应天数也变为0
      this.earlyTime1 = null;
      this.earlyTime2 = null;
      this.early.agricultureDays = 0;
      this.early.noAgricultureDays = 0;
      this.midTime1 = null;
      this.midTime2 = null;
      this.midmonth.agricultureDays = 0;
      this.midmonth.noAgricultureDays = 0;
      this.lateTime1 = null;
      this.lateTime2 = null;
      this.late.agricultureDays = 0;
      this.late.noAgricultureDays = 0;
    },
    onClickClose() {
      this.$emit("close", false);
    },

    submitForm(formName, status) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let form = JSON.parse(JSON.stringify(this.form));
          form.status = status;
          // form.tenDaysList = [this.early, this.midmonth, this.late];
          let ids = [];
          this.fileList.forEach(n => {
            ids.push(n.id);
          });
          if (ids.length === 0) {
            this.$emit("close", true, form);
            return;
          }
          if (form.applicationFile) {
            uploadEditFile({
              groupId: form.applicationFile,
              ids: ids
            }).then(res => {
              console.log(res);
              this.$emit("close", true, form);
            });
          } else {
            uploadAddFile(ids).then(res => {
              console.log(res);
              form.applicationFile = res.data;
              this.$emit("close", true, form);
            });
          }
        } else {
          return false;
        }
      });
    },
    aduitForm(status) {
      if (status == "3") {
        if (!this.form.rejection) {
          return this.$message.warning("请填写驳回原因");
        }
      }
      if (status == "2") {
        this.form.rejection = "";
      }
      let parms = {
        bak: "",
        planId: this.form.id,
        sign: "2",
        status: status
      };
      this.$emit("close", true, parms, JSON.parse(JSON.stringify(this.form)));
    },
    resetModel(model = {}) {
      this.form = this.$util.coverValue(this.form, model, true);
    }
  }
};
</script>
<style lang="scss">
.moonDPDialog {
  .xjh {
    .theme-table-form__input,
    .theme-table-form__label {
      text-align: center;
    }
  }
  .el-table .cell {
    overflow: inherit;
  }
}
</style>
