<template>
  <div class="app-container">
    <el-row :gutter="20" class="main-row">
      <!--对象类别-->
      <el-col :span="5" :xs="24" class="theme-pannel">
        <div class="theme-pannel__title">对象类别</div>
        <div class="theme-pannel__body">
          <div class="head-container">
            <el-input
              v-model="deptName"
              placeholder="请输入巡检类型"
              clearable
              size="small"
              prefix-icon="el-icon-search"
              style="margin-bottom: 20px"
            />
          </div>
          <div class="head-container">
            <el-tree
              :data="treeOptions"
              :props="defaultProps"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              ref="tree"
              node-key="id"
              default-expand-all
              :highlight-current="true"
              @node-click="handleNodeClick"
            >
              <template slot="default" slot-scope="{ node, data }">
                <span class="custom-tree-node">
                  <span>
                    <i v-if="data.dir == '1'" class="el-icon-folder icon-tree-btn"></i>
                    <i
                      class="el-icon-folder icon-tree-btn"
                      v-else
                      style="visibility: hidden"
                    ></i>
                    <span>{{ node.label }}</span>
                    <!-- 这里需要判断是否是文件夹，否则不能添加。 -->
                    <i
                      class="el-icon-plus icon-tree-btn"
                      v-if="data.dir == '1'"
                      @click.stop.prevent="handleClickNodeAdd(data)"
                      v-hasPermi="['inspection:clazz:add']"
                    ></i>
                    <!-- 这里需要判断是否是根节点，否则不能删除、修改。 -->
                    <i
                      class="el-icon-edit icon-tree-btn"
                      v-if="data.id != ''"
                      @click.stop.prevent="handleClickNodeedit(data, node)"
                      v-hasPermi="['inspection:clazz:edit']"
                    ></i>
                    <i
                      class="el-icon-delete icon-tree-btn"
                      v-if="data.id != ''"
                      @click.stop.prevent="handleClickNodeDel(data)"
                      v-hasPermi="['inspection:clazz:remove']"
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
        <el-form
          :model="queryParams"
          ref="queryForm"
          class="query-form"
          :inline="true"
          label-width="70px"
        >
          <el-form-item label="属性名称" prop="attributeName">
            <el-input
              v-model="queryParams.attributeName"
              placeholder="请输入属性名称"
              clearable
              size="small"
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="属性编码" prop="attributeCode">
            <el-input
              v-model="queryParams.attributeCode"
              placeholder="请输入属性编码"
              clearable
              size="small"
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <!--          <el-form-item>-->
          <!--            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>-->
          <!--            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>-->
          <!--          </el-form-item>-->
          <div class="query-form-btns">
            <el-button
              type="primary"
              icon="el-icon-search"
              size="mini"
              @click="handleQuery"
              >搜索</el-button
            >
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
              >重置</el-button
            >
          </div>
        </el-form>
        <FormToolsRow :buttons="formTools" @click="onClickOps"></FormToolsRow>

        <el-table
          v-loading="loading"
          :data="ObjList"
          border
          @selection-change="handleSelectionChange"
          height="calc(100% - 160px)"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column
            label="属性名"
            align="center"
            prop="attributeName"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="属性编码"
            align="center"
            prop="attributeCode"
            :show-overflow-tooltip="true"
          />
          <el-table-column label="是否必须" align="center" prop="necessary" width="80">
            <template slot-scope="scope">
              {{ scope.row.necessary == 1 ? "是" : "否" }}
            </template>
          </el-table-column>
          <el-table-column label="是否只读" align="center" prop="readonly" width="80">
            <template slot-scope="scope">
              {{ scope.row.readonly == 1 ? "是" : "否" }}
            </template>
          </el-table-column>
          <el-table-column
            label="属性类型"
            align="center"
            prop="type"
            width="80"
            :formatter="formatType"
          />
          <el-table-column
            label="是否基本信息"
            align="center"
            prop="readonly"
            width="100"
          >
            <template slot-scope="scope">
              {{ scope.row.baseinfo == 1 ? "是" : "否" }}
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            align="center"
            width="180"
            class-name="small-padding fixed-width"
          >
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['inspection:clazzAttribute:edit']"
                >修改
              </el-button>
              <el-button
                v-if="scope.row.userId !== 1"
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['inspection:clazzAttribute:remove']"
                >删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="clazzList"
        />
      </el-col>
    </el-row>

    <!-- 属性表单 -->
    <AutoDialog :title="title" :show.sync="open" width="700px">
      <el-form ref="form" :model="form" :rules="rules">
        <table class="theme-table-form">
          <colgroup>
            <col width="140" />
            <col />
            <col width="140" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(rules.attributeCode)">属性编码</td>
            <td class="theme-table-form__input">
              <el-form-item label="属性编码" prop="attributeCode">
                <el-input v-model="form.attributeCode" placeholder="请输入属性编码" />
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(rules.attributeName)">属性名称</td>
            <td class="theme-table-form__input">
              <el-form-item label="属性名" prop="attributeName">
                <el-input v-model="form.attributeName" placeholder="请输入属性名" />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.baseinfo)">是否是基本信息</td>
            <td class="theme-table-form__input">
              <el-form-item label="是否是基本信息" prop="baseinfo">
                <el-radio v-model="form.baseinfo" :label="'1'">是</el-radio>
                <el-radio v-model="form.baseinfo" :label="'0'">否</el-radio>
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(rules.necessary)">是否必须</td>
            <td class="theme-table-form__input">
              <el-form-item label="是否必须" prop="necessary">
                <el-radio v-model="form.necessary" :label="'1'">是</el-radio>
                <el-radio v-model="form.necessary" :label="'0'">否</el-radio>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.readonly)">是否是只读</td>
            <td class="theme-table-form__input">
              <el-form-item label="是否是只读" prop="readonly">
                <el-radio v-model="form.readonly" :label="'1'">是</el-radio>
                <el-radio v-model="form.readonly" :label="'0'">否</el-radio>
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(rules.attributeSort)">属性排序</td>
            <td class="theme-table-form__input">
              <el-form-item label="排序" prop="attributeSort">
                <el-input
                  type="number"
                  v-model="form.attributeSort"
                  placeholder="请输入排序号"
                />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.type)">属性类型</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="属性类型" prop="type">
                <el-select
                  v-model="form.type"
                  placeholder="属性类型"
                  clearable
                  size="small"
                >
                  <el-option
                    v-for="dict in typeOptions"
                    :key="dict.v"
                    :label="dict.l"
                    :value="dict.v"
                  />
                </el-select>
              </el-form-item>
            </td>
          </tr>
          <!-- 数字 -->
          <tr></tr>
        </table>
      </el-form>
      <!-- 数字 -->
      <el-form
        ref="numberForm"
        :model="NumberType"
        :rules="NumberTypeRules"
        v-if="form.type == 'number'"
      >
        <table class="theme-table-form">
          <colgroup>
            <col width="140" />
            <col />
            <col width="140" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(NumberType.max)">最大值</td>
            <td class="theme-table-form__input">
              <el-form-item label="最大值" prop="max">
                <el-input-number
                  v-model="NumberType.max"
                  style="width: 175px"
                  controls-position="right"
                  placeholder="请输入最大值"
                />
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(NumberType.min)">最小值</td>
            <td class="theme-table-form__input">
              <el-form-item label="最小值" prop="min">
                <el-input-number
                  v-model="NumberType.min"
                  style="width: 175px"
                  controls-position="right"
                  placeholder="请输入最小值"
                />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(NumberType.unit)">单位</td>
            <td class="theme-table-form__input">
              <el-form-item label="单位" prop="unit">
                <el-input v-model="NumberType.unit" placeholder="请输入单位" />
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(NumberType.defaultValue)">默认值</td>
            <td class="theme-table-form__input">
              <el-form-item label="默认值" prop="defaultValue">
                <el-input v-model="NumberType.defaultValue" placeholder="请输入默认值" />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(NumberType.thresholdMax)">阈值最小值</td>
            <td class="theme-table-form__input">
              <el-form-item label="阈值最小值" prop="thresholdMax">
                <el-input-number
                  v-model="NumberType.thresholdMax"
                  style="width: 175px"
                  controls-position="right"
                  placeholder="请输入阈值最小值"
                />
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(NumberType.thresholdMin)">阈值最大值</td>
            <td class="theme-table-form__input">
              <el-form-item label="阈值最大值" prop="thresholdMin">
                <el-input-number
                  v-model="NumberType.thresholdMin"
                  style="width: 175px"
                  controls-position="right"
                  placeholder="请输入阈值最大值"
                />
              </el-form-item>
            </td>
          </tr>
        </table>
      </el-form>
      <!-- 文本 -->
      <el-form
        ref="textForm"
        :model="TextType"
        :rules="TextTypeRules"
        v-if="form.type == 'text'"
      >
        <table class="theme-table-form">
          <colgroup>
            <col width="140" />
            <col />
            <col width="140" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(TextType.maxLength)">最大长度</td>
            <td class="theme-table-form__input">
              <el-form-item label="最大值" prop="maxLength">
                <el-input-number
                  v-model="TextType.maxLength"
                  style="width: 175px"
                  controls-position="right"
                  placeholder="请输入最大值"
                />
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(TextType.minLength)">最小长度</td>
            <td class="theme-table-form__input">
              <el-form-item label="最小值" prop="minLength">
                <el-input-number
                  v-model="TextType.minLength"
                  style="width: 175px"
                  controls-position="right"
                  placeholder="请输入最小值"
                />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(TextType.defaultValue)">默认值</td>
            <td class="theme-table-form__input">
              <el-form-item label="默认值" prop="defaultValue">
                <el-input v-model="TextType.defaultValue" placeholder="请输入默认值" />
              </el-form-item>
            </td>
          </tr>
        </table>
      </el-form>
      <!-- 时间 -->
      <el-form
        ref="dateForm"
        :model="DateType"
        :rules="DateTypeRules"
        v-if="form.type == 'date'"
      >
        <table class="theme-table-form">
          <colgroup>
            <col width="140" />
            <col />
            <col width="140" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(DateType.format)">时间格式</td>
            <td class="theme-table-form__input">
              <el-form-item label="时间格式" prop="format">
                <el-select
                  v-model="DateType.format"
                  placeholder="请选择时间格式"
                  clearable
                  size="small"
                >
                  <el-option
                    v-for="dict in dateOptions"
                    :key="dict.v"
                    :label="dict.l"
                    :value="dict.v"
                  />
                </el-select>
              </el-form-item>
            </td>
          </tr>
        </table>
      </el-form>
      <!-- 下拉选项 -->
      <el-form
        ref="selectForm"
        :model="SelectType"
        :rules="SelectTypeRules"
        v-if="form.type == 'select'"
      >
        <table class="theme-table-form">
          <colgroup>
            <col width="140" />
            <col />
            <col width="140" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(SelectType.defaultValue)">默认值</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="默认值" prop="defaultValue">
                <el-input v-model="SelectType.defaultValue" placeholder="请输入默认值" />
              </el-form-item>
            </td>
            <!-- <td :class="themeTableLabelClass(SelectType.thresholdMax)">阈值(固定值)</td>
            <td class="theme-table-form__input">
              <el-form-item label="阈值(固定值)" prop="thresholdMax">
                <el-input
                  v-model="SelectType.thresholdMax"
                  placeholder="请输入阈值(固定值)"
                />
              </el-form-item>
            </td> -->
          </tr>
          <tr>
            <td :class="themeTableLabelClass(SelectType.thresholdMax)">备选项</td>

            <td class="theme-table-form__input" colspan="3">
              <el-form-item prop="options">
                <el-button @click="addSelectTypeColumn()">添加</el-button>
                <el-table
                  :data="SelectType.options"
                  height="100%"
                  border
                  style="min-height: 160px"
                >
                  <el-table-column label="操作" width="60">
                    <template slot-scope="scope">
                      <span
                        class="el-tag el-tag--danger el-tag--mini"
                        style="cursor: pointer"
                        @click="delSelectTypeChange(scope.row, scope.$index)"
                        >删除</span
                      >
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-for="(v, i) in SelectTypeColumns"
                    :key="i"
                    :prop="v.field"
                    :label="v.title"
                  >
                    <template slot-scope="scope">
                      <el-form-item
                        :prop="`options[${scope.$index}].${v.field}`"
                        :rules="{
                          required: true,
                          message: '不能为空',
                          trigger: ['blur', 'change'],
                        }"
                      >
                        <el-input
                          :placeholder="`请输入${v.title}`"
                          v-model="scope.row[v.field]"
                        ></el-input>
                      </el-form-item>
                    </template>
                  </el-table-column>
                </el-table>
              </el-form-item>
            </td>
          </tr>
        </table>
      </el-form>
      <!-- 图片 -->
      <el-form
        ref="imageForm"
        :model="ImageType"
        :rules="ImageTypeRules"
        v-if="form.type == 'image'"
      >
        <table class="theme-table-form">
          <colgroup>
            <col width="140" />
            <col />
            <col width="140" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(ImageType.maxNumber)">最大数量</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="最大数量" prop="maxNumber">
                <el-input-number
                  v-model="ImageType.maxNumber"
                  :min="0"
                  style="width: 175px"
                  controls-position="right"
                  placeholder="请输入最大数量"
                />
              </el-form-item>
            </td>
          </tr>
        </table>
      </el-form>
      <!-- 多选 -->
      <el-form
        ref="checkboxForm"
        :model="CheckboxType"
        :rules="CheckboxTypeRules"
        v-if="form.type == 'checkbox'"
      >
        <table class="theme-table-form">
          <colgroup>
            <col width="140" />
            <col />
            <col width="140" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(CheckboxType.thresholdMax)">备选项</td>

            <td class="theme-table-form__input" colspan="3">
              <el-form-item prop="options">
                <el-button @click="addCheckboxTypeColumn()">添加</el-button>
                <el-table
                  :data="CheckboxType.options"
                  height="100%"
                  border
                  style="min-height: 140px"
                >
                  <el-table-column label="操作" width="60">
                    <template slot-scope="scope">
                      <span
                        class="el-tag el-tag--danger el-tag--mini"
                        style="cursor: pointer"
                        @click="delCheckboxTypeChange(scope.row, scope.$index)"
                        >删除</span
                      >
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-for="(v, i) in CheckboxTypeColumns"
                    :key="i"
                    :prop="v.field"
                    :label="v.title"
                  >
                    <template slot-scope="scope">
                      <el-form-item
                        :prop="`options[${scope.$index}].${v.field}`"
                        :rules="{
                          required: true,
                          message: '不能为空',
                          trigger: ['blur', 'change'],
                        }"
                      >
                        <el-input
                          :placeholder="`请输入${v.title}`"
                          v-model="scope.row[v.field]"
                        ></el-input>
                      </el-form-item>
                    </template>
                  </el-table-column>
                </el-table>
              </el-form-item>
            </td>
          </tr>
        </table>
      </el-form>
      <!-- 视频 -->
      <el-form
        ref="videoForm"
        :model="VideoType"
        :rules="VideoTypeRules"
        v-if="form.type == 'video'"
      >
        <table class="theme-table-form">
          <colgroup>
            <col width="140" />
            <col />
            <col width="140" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(VideoType.maxSize)">视频大小(M)</td>
            <td class="theme-table-form__input">
              <el-form-item label="视频大小(M)" prop="maxSize">
                <el-input-number
                  v-model="VideoType.maxSize"
                  :min="0"
                  style="width: 175px"
                  controls-position="right"
                  placeholder="请输入视频大小(M)"
                />
              </el-form-item>
            </td>
          </tr>
        </table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </AutoDialog>

    <!-- 用户导入对话框 -->
    <AutoDialog :title="treeDialog.title" :show.sync="treeDialog.open" width="600px">
      <el-form ref="treeForm" :model="treeForm" :rules="treeRules">
        <table class="theme-table-form">
          <colgroup>
            <col width="100" />
            <col />
            <col width="100" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass()">上级类型</td>
            <td class="theme-table-form__input">
              <span>{{ treeForm.parentName }}</span>
            </td>
            <td :class="themeTableLabelClass(treeRules.clazzCode)">类型编码</td>
            <td class="theme-table-form__input">
              <el-form-item label="类型编码" prop="clazzCode">
                <el-input v-model="treeForm.clazzCode" placeholder="请输入类型编码" />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(treeRules.clazzName)">类型名称</td>
            <td class="theme-table-form__input">
              <el-form-item label="类型名称" prop="clazzName">
                <el-input v-model="treeForm.clazzName" placeholder="请输入类型名称" />
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(treeRules.orderNum)">排序字段</td>
            <td class="theme-table-form__input">
              <el-form-item label="排序字段" prop="orderNum">
                <el-input-number
                  v-model="treeForm.orderNum"
                  controls-position="right"
                  :min="0"
                />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(treeRules.dir)">对象类型</td>
            <td class="theme-table-form__input">
              <el-form-item label="排序字段" prop="orderNum">
                <el-radio v-model="treeForm.dir" :label="'1'">文件夹</el-radio>
                <el-radio v-model="treeForm.dir" :label="'0'">类型</el-radio>
              </el-form-item>
            </td>
          </tr>
        </table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitTreeForm">确 定</el-button>
        <el-button @click="treeDialog.open = false">取 消</el-button>
      </div>
    </AutoDialog>
  </div>
</template>

<script>
import {
  getTree,
  addTree,
  getTreeDetail,
  updateTree,
  delTree,
  clazzList,
  clazzDetail,
  clazzAdd,
  clazzUpdate,
  clazzDel,
  clazzDict,
} from "@/platform/api/engineeringIO/CheckSetting/inspectionCate";
import ValidateUtil from "@/platform/utils/ValidateUtil.js";

export default {
  name: "inspectionCate",
  data() {
    let permMap = this.$perm.checkArrayToPermMap([
      { type: "add", permi: ["inspection:clazzAttribute:add"] },
      { type: "update", permi: ["inspection:clazzAttribute:edit"] },
      { type: "delete", permi: ["inspection:clazzAttribute:remove"] },
    ]);
    let validateStart = (rule, value, callback) => {
      if (this.form.pubPortEnd) {
        this.$refs.form.validateField("pubPortEnd");
      }
      callback();
    };
    let validateEnd = (rule, value, callback) => {
      if (this.form.pubPortStart) {
        if (Number(this.form.pubPortStart) < Number(value)) {
          callback();
        } else {
          callback(new Error("无效的源结束端口"));
        }
      } else {
        callback();
      }
    };
    return {
      // 遮罩层
      loading: false,
      formTools: Object.keys(permMap).join(","),
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户表格数据
      ObjList: null,
      // 弹出层标题
      title: "",
      // 类型树选项
      treeOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 部门名称
      deptName: undefined,
      // 默认密码
      initPassword: undefined,
      // 日期范围
      dateRange: [],
      // 状态数据字典
      statusOptions: [],
      // 性别状态字典
      sexOptions: [],
      // 岗位选项
      postOptions: [],
      // 角色选项
      roleOptions: [],
      // 属性对应关系 为了方便上报
      CRB: {
        number: "NumberType",
        date: "DateType",
        text: "TextType",
        select: "SelectType",
        image: "ImageType",
        checkbox: "CheckboxType",
        video: "VideoType",
      },
      typeOptions: [
        {
          v: "number",
          l: "数字",
        },
        {
          v: "text",
          l: "文本",
        },
        {
          v: "date",
          l: "日期",
        },
        {
          v: "select",
          l: "下拉选择",
        },
        {
          v: "image",
          l: "图片",
        },
        {
          v: "checkbox",
          l: "多选",
        },
        {
          v: "video",
          l: "视频",
        },
      ],
      NumberType: {
        min: "",
        max: "",
        unit: "",
        defaultValue: "",
        thresholdMin: "",
        thresholdMax: "",
      },
      NumberTypeRules: {
        min: ValidateUtil.requiredInt(),
        max: ValidateUtil.norequiredInt(),
        unit: ValidateUtil.required(),
        thresholdMax: ValidateUtil.norequiredInt(),
        thresholdMin: ValidateUtil.norequiredInt(),
      },
      TextType: {
        minLength: "",
        maxLength: "",
        defaultValue: "",
      },
      TextTypeRules: {
        maxLength: ValidateUtil.requiredInt(),
        minLength: ValidateUtil.requiredInt(),
      },
      DateType: {
        format: "",
      },
      DateTypeRules: {
        format: ValidateUtil.required(),
      },
      dateOptions: [
        {
          l: "精确到秒",
          v: "yyyy-MM-dd HH:mm:ss",
        },
        {
          l: "精确到分钟",
          v: "yyyy-MM-dd HH:mm",
        },
        {
          l: "精确到小时",
          v: "yyyy-MM-dd HH",
        },
        {
          l: "精确到日",
          v: "yyyy-MM-dd",
        },
        {
          l: "精确到月",
          v: "yyyy-MM",
        },
        {
          l: "精确到年",
          v: "yyyy",
        },
      ],
      SelectType: {
        options: [],
        thresholdMax: "",
        defaultValue: "",
      },
      SelectTypeRules: {
        options: ValidateUtil.required(),
      },
      SelectTypeColumns: [{ field: "text", title: "值" }],
      ImageType: {
        maxNumber: "",
      },
      ImageTypeRules: { maxNumber: ValidateUtil.requiredInt() },
      CheckboxType: {
        options: [],
      },
      CheckboxTypeRules: { options: ValidateUtil.required() },
      CheckboxTypeColumns: [{ field: "text", title: "值" }],
      VideoType: {
        maxSize: "",
      },
      VideoTypeRules: { maxSize: ValidateUtil.requiredInt() },
      // 表单参数
      form: {},
      // tree form
      treeForm: {
        clazzCode: "",
        clazzName: "",
        orderNum: 0,
        parentId: 0,
      },
      defaultProps: {
        children: "childs",
        label: "clazzName",
      },
      // 对象类型
      treeDialog: {
        // 是否显示弹出层（对象类型）
        open: false,
        // 弹出层标题（对象类型）
        title: "",
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        attributeName: undefined,
        attributeCode: undefined,
        clazzid: undefined,
      },
      // 表单校验
      rules: {
        attributeCode: ValidateUtil.required(),
        attributeName: ValidateUtil.required(),
        attributeUnit: ValidateUtil.required(),
        type: ValidateUtil.required(),
        readonly: ValidateUtil.required(),
        attributeSort: ValidateUtil.norequiredInt(),
        baseinfo: ValidateUtil.required(),
        control: ValidateUtil.required(),
        map: ValidateUtil.required(),
        necessary: ValidateUtil.required(),
        picture: ValidateUtil.required(),
        video: ValidateUtil.required(),
        length: ValidateUtil.norequiredInt(),
      },
      // tree 表单验证
      treeRules: {
        clazzCode: [
          {
            required: true,
            message: "类型编码不能为空",
            trigger: ["blur", "change"],
          },
        ],
        clazzName: [
          {
            required: true,
            message: "类型名称不能为空",
            trigger: ["blur", "change"],
          },
        ],
        dir: [
          {
            required: true,
            message: "对象类型不能为空",
            trigger: ["blur", "change"],
          },
        ],
      },
    };
  },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val);
    },
  },
  created() {
    this.clazzList();
    this.getTreeselect();
  },
  methods: {
    //新增多选备选值
    addCheckboxTypeColumn() {
      let j = {
        text: "",
      };
      this.CheckboxType.options.push(j);
    },
    // 多选删除操作
    delCheckboxTypeChange(row, index) {
      this.CheckboxType.options.splice(index, 1);
    },
    //新增单选备选值
    addSelectTypeColumn() {
      let j = {
        text: "",
      };
      this.SelectType.options.push(j);
    },
    // 单选删除操作
    delSelectTypeChange(row, index) {
      this.SelectType.options.splice(index, 1);
    },
    /** 查询用户列表 */
    clazzList() {
      // if (!this.queryParams.clazzId) {
      //     this.msgInfo("请选择一个类别后再操作");
      //     return false;
      // }
      this.loading = true;
      clazzList(this.queryParams).then((res) => {
        this.ObjList = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    /** 查询类型树结构 */
    getTreeselect() {
      getTree(0).then((res) => {
        // 添加一个顶级目录
        let obj = [
          {
            childs: res.data,
            clazzCode: "",
            clazzName: "巡检类型",
            id: 0,
            dir: "1",
            orderNum: 0,
            parentId: "",
          },
        ];
        this.treeOptions = obj;
      });
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.clazzName.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      console.log(data);
      this.queryParams.clazzId = "";
      if (data.dir != "1" && data.id != "") {
        this.queryParams.clazzId = data.id;
        this.clazzList();
      } else {
        this.ObjList = [];
      }
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
      this.otherResetForm();
    },
    otherResetForm() {
      this.NumberType = {
        min: "",
        max: "",
        unit: "",
        defaultValue: "",
        thresholdMin: "",
        thresholdMax: "",
      };
      this.TextType = {
        minLength: "",
        maxLength: "",
        defaultValue: "",
      };
      this.DateType = {
        format: "",
      };
      this.SelectType = {
        options: [],
        thresholdMax: "",
        defaultValue: "",
      };
      this.ImageType = {
        maxNumber: "",
      };
      this.CheckboxType = {
        options: [],
      };
      this.VideoType = {
        maxSize: "",
      };
    },
    // 属性表单重置
    reset() {
      this.form = {
        attributeCode: "",
        attributeName: "",
        baseinfo: "1",
        clazzId: 0,
        necessary: "0",
        readonly: "",
        type: "",
        typeConfig: "",
        attributeSort: 0,
      };
      this.resetForm("form");
    },
    // 类型表单重置
    treeReset() {
      this.treeForm = {
        clazzCode: "",
        clazzName: "",
        dir: "1",
        orderNum: 0,
        parentId: 0,
      };
      this.resetForm("treeForm");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.clazzList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length != 1;
      this.multiple = !selection.length;
    },
    // tree 增删改查
    handleClickNodeAdd(row) {
      this.treeReset();
      getTreeDetail(row.id).then((res) => {
        console.log(res);
        this.treeDialog.open = true;
        this.treeDialog.type = "add";
        this.treeForm.parentId = row.id;
        this.treeForm.parentName = row.clazzName;
      });
    },
    handleClickNodeedit(row, node) {
      let parent = node.parent.data;
      this.treeReset();
      this.treeDialog.open = true;
      this.treeDialog.type = "update";
      this.treeForm = JSON.parse(JSON.stringify(row));

      this.treeForm.parentName = parent.clazzName;
    },
    handleClickNodeDel(row) {
      delTree(row.id).then((res) => {
        if (res.code === 200) {
          this.msgSuccess("删除成功");
          this.treeDialog.open = false;
          this.getTreeselect();
        }
      });
    },
    submitTreeForm() {
      this.$refs["treeForm"].validate((valid) => {
        if (valid) {
          if (this.treeDialog.type == "update") {
            updateTree(this.treeForm).then((res) => {
              if (res.code === 200) {
                this.msgSuccess("修改成功");
                this.treeDialog.open = false;
                this.getTreeselect();
              }
            });
          } else {
            addTree(this.treeForm).then((res) => {
              if (res.code === 200) {
                this.msgSuccess("新增成功");
                this.treeDialog.open = false;
                this.getTreeselect();
              }
            });
          }
        }
      });
    },
    onClickOps(type, e) {
      switch (type) {
        case "add":
          this.title = "新增";
          // 判断是否已经选择类别
          if (!this.queryParams.clazzId) {
            this.msgInfo("请选择一个类别后再操作");
            return false;
          } else {
            this.handleAdd();
          }
          break;
        case "update":
          this.title = "编辑";
          // 判断是否已经选择类别
          if (!this.queryParams.clazzId) {
            this.msgInfo("请选择一个类别后再操作");
            return false;
          }
          if (!this.ids || !this.ids.length) {
            this.msgInfo("请选择一行后再操作");
            return;
          }
          if (this.ids.length !== 1) {
            this.msgInfo("只能选择一行");
            return;
          }
          this.handleUpdate();
          break;
        case "delete":
          if (!this.queryParams.clazzId) {
            this.msgInfo("请选择一个类别后再操作");
            return false;
          }
          if (!this.ids || !this.ids.length) {
            this.msgInfo("请最少选择一行后再操作");
            return;
          }
          this.handleDelete();
          break;
        case "refresh":
          this.resetQuery();
          break;

        default:
          break;
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.form.clazzId = this.queryParams.clazzId;
      this.open = true;
    },
    /** 修改按钮操作 */
    handleUpdate(row = {}) {
      this.title = "编辑";
      this.reset();
      const id = row.id || this.ids;
      clazzDetail(id).then((res) => {
        console.log(res);
        this.form = res.data;
        this[this.CRB[this.form.type]] = JSON.parse(this.form.typeConfig);
        this.open = true;
      });
    },
    checkPwdByRules(value) {
      if (value === null || value === undefined || value === "") {
        return "密码不能为空";
      }
      //获取系统配置。根据
      let pwdRuleConfig = this.$store.state.settings.sysConfigs["pwd.security.rules"];
      let pwdRule = pwdRuleConfig && pwdRuleConfig.configValue;
      if (!pwdRule) {
        return;
      }
      let rs = PwdChecker.checkPwd(pwdRule, value);
      return rs.ok ? null : rs.msg;
    },
    /** 重置密码按钮操作 */
    handleResetPwd(row) {
      this.$dialog.show({
        title: "重置密码",
        width: "400px",
        component: "DataEditForm",
        propsData: {
          edit: formhelper.create(
            [formhelper.itemCreator.input("输入密码", "pwd", null, this.rules.password)],
            false
          ),
        },
        onCloseFn: (type, dialog, next) => {
          if (type !== "ok") {
            next();
            return;
          }
          dialog.$component.validate((model) => {
            dialog.okBtnLoading = true;
            resetUserPwd(row.userId, model.pwd)
              .then((res) => {
                if (res.code === 200) {
                  this.msgSuccess("修改成功");
                  next();
                }
              })
              .finally(() => {
                dialog.okBtnLoading = false;
              });
          });
        },
      });
    },
    /** 提交按钮 */
    submitForm: function () {
      console.log(this.form.type);
      this.$refs["form"].validate((valid) => {
        if (!valid) return this.$message.error("验证失败");
        // let ref = this.$refs["selectForm"] || this.$refs["checkboxForm"];
        let ref = this.$refs[`${this.form.type}Form`];
        console.log(ref);
        if (ref) {
          ref.validate((v) => {
            if (!v) return this.$message.error("验证失败");
            this.submit();
          });
        } else {
          this.submit();
        }
      });
    },
    submit() {
      console.log(this[this.CRB[this.form.type]]);
      this.form.typeConfig = JSON.stringify(this[this.CRB[this.form.type]]);
      if (this.form.id != undefined) {
        clazzUpdate(this.form).then((res) => {
          if (res.code === 200) {
            this.msgSuccess("修改成功");
            this.open = false;
            this.clazzList();
          }
        });
      } else {
        clazzAdd(this.form).then((res) => {
          if (res.code === 200) {
            this.msgSuccess("新增成功");
            this.open = false;
            this.clazzList();
          }
        });
      }
    },
    /** 删除按钮操作 */
    handleDelete(row = {}) {
      const id = row.id || this.ids;
      this.$confirm(
        `是否确认删除${this.ids.length > 1 ? "这些" : "这条"}数据项?`,
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          clazzDel(id).then((res) => {
            if (res.code == 200) {
              this.clazzList();
              this.msgSuccess("删除成功");
            } else {
              this.msgSuccess(res.msg);
            }
          });
        })
        .catch(function () {});
    },
    formatType(row, column, cellValue, index) {
      let str = "";
      switch (cellValue) {
        case "select":
          str = "下拉选择";
          break;
        case "text":
          str = "文本";
          break;
        case "number":
          str = "数字";
          break;
        case "date":
          str = "日期";
          break;
        case "image":
          str = "图片";
          break;
        case "video":
          str = "视频";
          break;
        case "checkbox":
          str = "多选";
          break;
        default:
          str = "";
      }
      return str;
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
