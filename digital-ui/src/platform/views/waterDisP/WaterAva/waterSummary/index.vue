<template>
  <div class="app-containter">
    <div class="__header__">
      <SelectYear @change-year="changeYear"></SelectYear>
      <el-button type="primary" style="margin-left: 10px">保存</el-button>
    </div>
    <div class="__content__">
      <!-- <div class="__left__">
        <div
          v-for="i in 12"
          :key="i"
          @click="changeMonth(i)"
          :class="[
            '__item',
            'theme-border-color3',
            month == i
              ? 'theme-bg-dark-light theme-text-color-custom-dark'
              : 'theme-text-color-primary-light',
          ]"
        >
          {{ i }}月
        </div>
      </div> -->
      <div class="__right__">
        <div class="__top__ theme-border-color3">
          <div class="title theme-text-color-primary-dark">
            本年批复用水量:
            <span class="_color">{{ summaryData.replyWater || "-" }}万m³</span
            >。
          </div>
          <div class="title theme-text-color-primary-dark">
            已签字用水量:
            <span class="_color">{{ summaryData.signWater || "-" }}万m³</span>。
          </div>
          <!-- <div class="title theme-text-color-primary-dark">
                        监测用水量:
                        <span class="_color">{{
                            summaryData.monitorWater || "-"
                        }}</span
                        >。
                    </div> -->
        </div>
        <div class="__right__list__">
          <div class="__list__" v-for="(item, i) in tableData" :key="i">
            <div class="file-card">
              <div class="c_header">{{ item.title }}取水申请</div>
              <div class="c_div" v-if="item.title">
                <div class="_title theme-text-color-primary-dark">
                  <span style="color:#000">时段：</span>{{ item.intakeDate }}
                </div>
                <!-- <div class="_title theme-text-color-primary-dark">
                  <span>监测水量:</span>
                  <span>{{ item.monitorWater || "-" }}</span>
                </div> -->
                <div class="_title theme-text-color-primary-dark">
                  <span style="color:#000">批复水量:</span>
                  <span
                    >{{ item.replyWater || "-" }}
                    <span style="color:#000;font-size:12px">万m³</span></span
                  >
                </div>
                <div class="_title theme-text-color-primary-dark">
                  <span style="color:#000">签字水量:</span>
                  <span
                    >{{ item.signWater || "-"
                    }}<span style="color:#000;font-size:12px">万m³</span></span
                  >
                </div>
              </div>
              <div class="c_div" v-else>暂无数据</div>
              <div class="_n _title theme-text-color-primary-light">
                {{ item.intakeDate }}
              </div>
              <div class="__btn__">
                <div
                  class="btn-item"
                  @click="handleAdd(item)"
                  v-hasPermi="['water:summary:add']"
                >
                  <!-- <img src="../../../../assets/img/waterDisP/edit.png" alt="" />
                  <span>新增</span> -->
                  <el-button type="primary">新增签字用水</el-button>
                </div>
                <el-button @click="upLoadFile(item)" type="primary"
                  >上传</el-button
                >
                <!-- <img @click="upLoadFile(item)" src="../../../../assets/img/waterDisP/upload.png" alt="" /> -->

                <!-- <img src="../../../../assets/img/waterDisP/delete.png" alt="" /> -->
                <!-- <img src="../../../../assets/img/waterDisP/upload.png" alt="" /> -->
              </div>
            </div>
            <el-table
              :stripe="true"
              :data="item.companylist"
              border
              height="100%"
              style="width: 100%"
            >
              <el-table-column
                prop="companyName"
                label="用水单位名称"
                min-width="100"
                align="center"
              ></el-table-column>
              <el-table-column
                prop="water"
                label="用水量(万m³)"
                width="100"
                align="right"
              ></el-table-column>
              <el-table-column
                prop="waterType"
                label="用水类型"
                width="100"
                align="center"
              ></el-table-column>
              <el-table-column label="操作" width="180" align="center">
                <template slot-scope="scope">
                  <el-button
                    type="primary"
                    @click="handleEdit(scope.row)"
                    size="small"
                    v-hasPermi="['water:summary:edit']"
                    >编辑</el-button
                  >
                  <el-button
                    type="danger"
                    @click="handleDelete(scope.row)"
                    size="small"
                    v-hasPermi="['water:summary:edit']"
                    >删除</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </div>
    <waterSummaryDialog
      ref="addOrUpdate"
      v-if="waterSummaryDialog.show"
      v-bind="waterSummaryDialog"
      @close="waterSummaryDialog.close"
    ></waterSummaryDialog>
    <AutoDialog
      title="上传"
      :show.sync="open"
      width="400px"
      @close="upLoadClose"
    >
      <Upload v-model="fileList" showType="picture-card"></Upload>
    </AutoDialog>
  </div>
</template>

<script>
import SelectYear from "../../__comom/SelectYear";
import waterSummaryDialog from "./__waterSummaryDialog";
import {
  LIST,
  summary,
  ADD,
  UPDATE,
  DEL
} from "@/platform/api/waterDisP/waterSummary";
import Upload from "@/platform/views/waterDisP/__comom/upload";
import { saveFileKey } from "@/platform/api/waterDisP/waterApp";
import {
  uploadAddFile,
  getGroupFile,
  uploadEditFile
} from "@/platform/api/common/common.js";
export default {
  data() {
    return {
      open: false,
      fileList: [],
      activeRow: undefined,
      // 分页器属性
      page: 1,
      pageSize: 10,
      total: 2,
      tableData: [],
      year: new Date().getFullYear(),
      month: "",
      summaryData: {},
      waterSummaryDialog: {
        show: false,
        title: "",
        mode: "",
        model: null,
        chartsSetOption: {},
        close: () => {
          this.waterSummaryDialog.show = false;
        }
      }
    };
  },
  components: {
    SelectYear,
    waterSummaryDialog,
    Upload
  },
  created() {
    this.$nextTick(() => {
      this.fetchData();
    });
  },
  methods: {
    //上传文件
    upLoadFile(row) {
      console.log(row);
      if (!row.id) return this.$message.warning("请先上传汇总数据");

      if (row.fileKeySummary) {
        getGroupFile(row.fileKeySummary).then(res => {
          if (res.code !== 200) {
            return this.$message.error(res.msg);
          }
          this.fileList = res.data;
          console.log(this.fileList);
        });
      } else {
        this.fileList = [];
      }
      this.activeRow = row;
      this.open = true;
    },
    upLoadClose(key) {
      if (key === "ok") {
        let ids = [];
        this.fileList.forEach(n => {
          ids.push(n.id);
        });
        if (ids.length == 0) {
          if (this.activeRow.fileKeySummary) {
            uploadEditFile({
              groupId: this.activeRow.fileKeySummary,
              ids: ids
            });
          }
          return;
        }
        if (this.activeRow.fileKeySummary) {
          uploadEditFile({
            groupId: this.activeRow.fileKeySummary,
            ids: ids
          });
        } else {
          uploadAddFile(ids).then(r => {
            saveFileKey({ fileKeySummary: r.data, id: this.activeRow.id });
          });
        }
      }
    },

    fetchData() {
      let query = {
        pageNum: this.page,
        pageSize: this.pageSize,
        year: this.year,
        month: this.month
      };
      LIST(query).then(res => {
        console.log(res);
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        this.tableData = res.data;
      });
      summary(query).then(res => {
        console.log(res);
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        this.summaryData = res.data || {};
      });
    },
    changeYear(val) {
      this.year = val;
      this.month = "";
      this.fetchData();
    },
    changeMonth(v) {
      this.month = v;
      this.fetchData();
    },
    //分页器事件
    pageSizeChangeHandle(pageSize) {
      this.pageSize = pageSize;
      this.page = 1;
      this.fetchData();
    },
    pageCurrentChangeHandle(page) {
      this.page = page;
      this.fetchData();
    },
    handleAdd(row) {
      console.log(row);
      this.waterSummaryDialog = {
        show: true,
        title: "新增",
        mode: "add",
        model: row,
        close: this.handleAddForm
      };
    },
    handleAddForm(save, model, parms) {
      if (!save) {
        this.waterSummaryDialog.show = false;
        return;
      }
      ADD(model).then(res => {
        this.waterSummaryDialog.show = false;
        this.fetchData();
      });
    }, //修改
    handleEdit(row) {
      console.log(row);
      let row2 = JSON.parse(JSON.stringify(row));
      this.waterSummaryDialog = {
        show: true,
        title: "编辑",
        mode: "update",
        model: row2,
        close: this.handleEditSave
      };
    },
    handleEditSave(save, model, parms) {
      if (!save) {
        this.waterSummaryDialog.show = false;
        return;
      }
      UPDATE(model).then(res => {
        this.waterSummaryDialog.show = false;
        this.fetchData(false);
      });
    },
    handleDelete(row) {
      this.$confirm("确定要删除该记录吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          DEL({ id: row.id }).then(res => {
            this.fetchData();
          });
        })
        .catch(() => {});
    }
  }
};
</script>

<style lang="scss" scoped>
.app-containter {
  padding: 10px;
  display: flex;
  flex-direction: column;
  .__header__ {
    display: flex;
  }
  .__content__ {
    display: flex;
    padding: 10px 0;
    width: 100%;
    .__left__ {
      width: 160px;
      .__item {
        margin: 5px;
        border-width: 1px;
        border-style: solid;
        cursor: pointer;
        height: 35px;
        line-height: 35px;
        text-align: center;
        font-weight: bold;
        font-size: 16px;
      }
    }
    .__top__ {
      margin-top: 5px;
      display: flex;
      padding: 10px;
      border-style: solid;
      border-width: 1px;
      .title {
        font-size: 16px;
        margin: 0 10px;
        ._color {
          font-weight: bold;
          padding: 0 10px;
        }
      }
    }
    .__right__ {
      width: calc(100% - 0px);
      .__right__list__ {
        display: flex;
        width: 100%;
        flex-direction: column;
        .__list__ {
          display: flex;
          width: 100%;
          margin: 5px;
          height: 215px;
          position: relative;
          .file-card {
            width: 247px;
            margin: 5px;
            height: 100%;
            background-color: #f2f6fb;
            position: relative;
            .c_header {
              height: 30px;
              font-size: 16px;
              line-height: 30px;
              padding: 0 0 0 5px;
              background-color: #e1e8f0;
            }
            .c_div {
              font-size: 13px;
              width: 100%;
              height: 110px;
              position: static;
              padding: 20px 0;
            }
            ._title {
              padding-left: 15px;
              width: 100%;
              text-align: left;
              font-size: 15px;
              margin-bottom: 15px;
            }
            ._n {
              position: absolute;
              bottom: 0;
              font-size: 15px;
            }
            .__btn__ {
              width: 100%;
              height: 30px;
              background-color: #e1e8f0;
              position: absolute;
              bottom: 0px;
              padding-left: 50px;
              text-align: right;
              display: flex;
              .btn-item {
                margin-right: 5px;
                span {
                  display: inline-block;
                  line-height: 30px;
                }
              }

              img {
                cursor: pointer;
                margin: 5px;
                font-size: 15px;
                vertical-align: middle;
              }
            }
          }
        }
        .file_class {
          height: 170px;
          width: 125px;
          position: absolute;
        }
        .c_div {
          text-align: center;
          height: 130px;
          width: 125px;
          position: absolute;
          top: 30px;
        }
      }
      .add__btn {
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
  }
}
</style>
