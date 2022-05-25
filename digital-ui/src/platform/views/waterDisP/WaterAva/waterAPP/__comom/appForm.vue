<template>
  <div>
    <div class="__header__">
      <SelectYear @change-year="changeYear"></SelectYear>
      <el-button type="primary" style="margin-left: 10px" @click="handlAdd"
        >新增</el-button
      >
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
        <div class="__card__">
          <!-- <el-card class="box-card" v-for="(item, i) in 12" :key="i"> -->
          <div class="box-card" v-for="(item, i) in tableData" :key="i">
            <!-- <img src="../../../../../assets/img/file.png" class="file_class" /> -->
            <div class="c_div">
              <div class="_title">{{ item.applyCompany }}取水申请表</div>
              <div class="_title">
                合计： <span class="_num">{{ item.total }}万</span>(m³)
              </div>
            </div>
            <div class="_n _title theme-text-color-primary-light">
              {{ `${stringSb(item.intakeStart)}至${stringSb(item.intakeEnd)}` }}
            </div>
            <div class="__btn__">
              <img
                src="../../../../../assets/img/waterDisP/print.png"
                @click="handlePrint(item)"
                alt=""
              />
              <img
                src="../../../../../assets/img/waterDisP/edit.png"
                @click="handleEdit(item)"
                alt=""
                v-hasPermi="['intake:apply:edit']"
              />
              <img
                src="../../../../../assets/img/waterDisP/upload.png"
                alt=""
                @click="upLoadFile(item)"
              />
              <img
                src="../../../../../assets/img/waterDisP/delete.png"
                @click="handleDelete(item)"
                v-hasPermi="['intake:apply:remove']"
                alt=""
              />
            </div>
          </div>
        </div>
        <el-pagination
          background
          :current-page="page"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="pageSizeChangeHandle"
          @current-change="pageCurrentChangeHandle"
        ></el-pagination>
      </div>
    </div>
    <appFormDialog
      ref="addOrUpdate"
      v-if="appFormDialog.show"
      v-bind="appFormDialog"
      @close="appFormDialog.close"
    ></appFormDialog>
    <div class="print_content">
      <div id="print">
        <div class="title">{{ printJSON.applyCompanyName }}取水申请表</div>
        <div class="dw">单位：万立方米</div>
        <table class="theme-table-form">
          <colgroup>
            <col />
            <col />
            <col />
            <col />
            <col />
          </colgroup>
          <tr>
            <td colspan="2">申请单位</td>
            <td colspan="3">
              <span>{{ printJSON.applyCompanyName }}</span>
            </td>
          </tr>
          <tr>
            <td colspan="2">取水口</td>
            <td colspan="3">
              <span>{{ printJSON.waterIntake }}</span>
            </td>
          </tr>
          <tr>
            <td>许可水量</td>
            <td>
              <span>{{ printJSON.allowWater }}</span>
            </td>
            <td>年计划批复水量</td>
            <td colspan="2">
              <span>{{ printJSON.yearReplyWater }}</span>
            </td>
          </tr>
          <tr class="_center">
            <td colspan="5">
              <span>本次申请取水量</span>
            </td>
          </tr>
          <tr class="_center">
            <td>城市生活</td>
            <td>农业灌溉</td>
            <td>工业</td>
            <td>生态环境</td>
            <td>合计</td>
          </tr>
          <tr class="_center">
            <td>{{ printJSON.cityLife }}</td>
            <td>{{ printJSON.agriculture }}</td>
            <td>{{ printJSON.industry }}</td>
            <td>{{ printJSON.ecology }}</td>
            <td>{{ printJSON.total }}</td>
          </tr>
          <tr class="_center">
            <td>取水流量</td>
            <td>{{ printJSON.intakeFlow }}</td>
            <td>取水时段</td>
            <td colspan="2">
              {{ printJSON.intakeStart + "至" + printJSON.intakeEnd }}
            </td>
          </tr>
          <tr>
            <td colspan="5" class="content">
              <div class="_t">申请单位意见：</div>
              <div class="_c">
                {{ printJSON.applyOpinion }}
              </div>
              <div class="_z">申请单位： （盖章）</div>
              <div class="_time">日期：{{ printJSON.writeDate }}</div>
            </td>
          </tr>
          <tr>
            <td colspan="5" class="content" style="height: 130px">
              <div class="_t">县（市、区）局主管部门意见：</div>
              <div class="_c"></div>
              <div class="_z">申请单位： （盖章）</div>
              <div class="_time">日期：</div>
            </td>
          </tr>
          <tr>
            <td colspan="5" class="content" style="height: 130px">
              <div class="_t">市局主管部门意见：</div>
              <div class="_c"></div>
              <div class="_z">申请单位： （盖章）</div>
              <div class="_time">日期：</div>
            </td>
          </tr>
          <tr>
            <td colspan="5" class="content" style="height: 130px">
              <div class="_t">省局主管部门意见：</div>
              <div class="_c"></div>
              <div class="_z">申请单位： （盖章）</div>
              <div class="_time">日期：</div>
            </td>
          </tr>
        </table>
      </div>
    </div>

    <AutoDialog title="上传" :show.sync="open" width="400px" @close="upLoadClose">
      <Upload v-model="fileList" showType="picture-card"></Upload>
    </AutoDialog>
  </div>
</template>

<script>
import SelectYear from "../../../__comom/SelectYear";
import appFormDialog from "./__appFormDialog";
import {
  LIST,
  getDetail,
  ADD,
  UPDATE,
  DEL,
  saveFileKey,
} from "@/platform/api/waterDisP/waterApp";
import print from "print-js";
import Upload from "@/platform/views/waterDisP/__comom/upload";
import {
  uploadAddFile,
  getGroupFile,
  uploadEditFile,
} from "@/platform/api/common/common.js";
export default {
  data() {
    return {
      open: false,
      fileList: [],
      // 分页器属性
      page: 1,
      pageSize: 10,
      total: 2,
      tableData: [],
      year: new Date().getFullYear(),
      month: "",
      appFormDialog: {
        show: false,
        title: "",
        mode: "",
        model: null,
        chartsSetOption: {},
        close: () => {
          this.appFormDialog.show = false;
        },
      },
      printJSON: {},
      activeId: "",
    };
  },
  components: {
    SelectYear,
    appFormDialog,
    Upload,
  },
  created() {
    this.$nextTick(() => {
      this.fetchData();
    });
  },
  methods: {
    stringSb(str) {
      if (!str) return "";
      str = str.toString().split("-");
      return `${str[1]}月${str[2]}日`;
    },
    fetchData() {
      let query = {
        pageNum: this.page,
        pageSize: this.pageSize,
        year: this.year,
        month: this.month,
      };
      LIST(query).then((res) => {
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        this.tableData = res.rows;
        this.total = res.total;
      });
    },
    changeYear(val) {
      this.year = val;
      this.month = "";
      this.fetchData();
    },
    changeMonth(v) {
      this.month = v.toString().length === 1 ? "0" + v : v;
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
    handlAdd() {
      getDetail(null, this.year, this.month ? this.month : null).then((res) => {
        if (!res.data) return this.$message.warning("暂无当前时间取水计划");
        this.appFormDialog = {
          show: true,
          title: "新增",
          mode: "add",
          model: res.data,
          chartsSetOption: {},
          close: this.handleAddForm,
        };
      });
    },
    handleAddForm(save, model, parms) {
      if (!save) {
        this.appFormDialog.show = false;
        return;
      }
      ADD(model).then((res) => {
        this.appFormDialog.show = false;
        this.fetchData();
      });
    }, //修改
    handleEdit(row) {
      getDetail(row.id).then((res) => {
        res.data.month = row.month;
        res.data.year = row.year;
        this.appFormDialog = {
          show: true,
          title: "编辑",
          mode: "update",
          model: res.data,
          close: this.handleEditSave,
        };
      });
    },
    //上传文件
    upLoadFile(row) {

      if (row.fileKey) {
        getGroupFile(row.fileKey).then((res) => {
          if (res.code !== 200) {
            return this.$message.error(res.msg);
          }
          this.fileList = res.data;
        });
      } else {
        this.fileList = [];
      }
      this.activeId = row.id;
      this.open = true;
    },
    upLoadClose(key) {
      if (key === "ok") {
        getDetail(this.activeId).then((res) => {
          let ids = [];
          this.fileList.forEach((n) => {
            ids.push(n.id);
          });
          if (ids.length == 0) {
            if (res.data.fileKey) {
              uploadEditFile({
                groupId: res.data.fileKey,
                ids: ids,
              });
            }
            return;
          }
          if (res.data.fileKey) {
            uploadEditFile({
              groupId: res.data.fileKey,
              ids: ids,
            });
          } else {
            uploadAddFile(ids).then((r) => {
              saveFileKey({ fileKey: r.data, id: this.activeId }).then(this.fetchData());
            });
          }
        });
      }
    },
    handleEditSave(save, model, parms) {
      if (!save) {
        this.appFormDialog.show = false;
        return;
      }
      UPDATE(model).then((res) => {
        this.appFormDialog.show = false;
        this.fetchData(false);
      });
    },
    handleDelete(row) {
      this.$confirm("确定要删除该记录吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          DEL(row.id).then((res) => {
            this.fetchData();
          });
        })
        .catch(() => {});
    },
    handlePrint(row) {
      getDetail(row.id)
        .then((res) => {
          this.printJSON = res.data || {};
        })
        .then(() => {
          this.$nextTick(() => {
            printJS({
              printable: "print",
              type: "html",
              targetStyles: ["*"],
              ignoreElements: ["no-print", "bc", "gb"],
            });
          });
        });
    },
  },
};
</script>
<style lang="scss" scoped>
.__header__ {
  display: flex;
}
.__content__ {
  display: flex;
  padding: 10px 0;
  width: 100%;
  .__left__ {
    width: 100px;
    .__item {
      margin: 5px;
      border-width: 1px;
      border-style: solid;
      cursor: pointer;
      height: 30px;
      line-height: 30px;
      text-align: center;
      font-weight: bold;
      font-size: 12px;
    }
  }
  .__right__ {
    width: calc(100% - 160px);

    ::v-deep .el-card__body {
      padding: 15px 10px 20px 10px;
    }
    .__card__ {
      display: flex;
      flex-wrap: wrap;
      justify-content: flex-start;

      .box-card {
        width: 247px;
        margin: 5px;
        height: 155px;
        background-color: #f2f6fb;
        position: relative;
        .c_div {
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
        ._num {
          color: #029adc;
        }
        ._n {
          // position: relative;
          // top: 110px;
          // left: 45px;
          padding: 0 0 0 55px;
          font-size: 13px;
        }
        .__btn__ {
          width: 100%;
          background-color: #e1e8f0;
          position: absolute;
          bottom: 0px;
          padding-left: 90px;
          img {
            cursor: pointer;
            margin: 5px;
            font-size: 15px;
          }
        }
      }
      .file_class {
        height: 160px;
        width: 115px;
        position: absolute;
        left: 15px;
      }
    }
    .add__btn {
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
}
.print_content {
  position: absolute;
  top: 99999;
  z-index: -9999;
  #print {
    width: 800px;
    margin-top: 20px;
    .title {
      text-align: center;
      font-weight: bold;
      font-size: 38px;
      line-height: 50px;
    }
    .dw {
      font-size: 18px;
      text-align: right;
      line-height: 60px;
      margin-right: 100px;
    }
    table {
      font-size: 16px;
      td {
        height: 60px;
        border-color: #000000;
      }
      ._center {
        td {
          text-align: center;
        }
      }
      .content {
        height: 200px;
        position: relative;
        ._t {
          position: absolute;
          left: 10px;
          top: 10px;
        }
        ._c {
          text-indent: 2em;
          position: absolute;
          bottom: 10px;
          width: 100%;
          height: 140px;
        }
        ._z {
          position: absolute;
          right: 130px;
          bottom: 45px;
        }
        ._time {
          position: absolute;
          right: 120px;
          bottom: 10px;
        }
      }
    }
  }
}
</style>
