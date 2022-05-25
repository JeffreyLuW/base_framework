<template>
    <div class="ThemeTest theme-bg-control">
        <div style="margin: 10px auto; line-height: 28px">
            <!--列举色彩配置-->
            <div class="theme-pannel">
                <div class="theme-pannel__title">
                    主题参数设置 (均不能为空，否则异常)
                </div>
                <div style="padding: 10px">
                    <el-row>
                        <el-col :span="4">控件主色调:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.primary"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">深背景|导航|菜单:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.bgDark"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">borderRadius:</el-col>
                        <el-col :span="2">
                            <el-input
                                v-model="themeVars.borderRadius"
                                placeholder="数字,0表示无圆弧"
                            ></el-input>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="4">控件|面板背景:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.bg1"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">标题背景:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.bgTitle"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">基础背景:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.bg2"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">表头背景:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.bgTh"
                            ></el-color-picker>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="4">控件边框颜色:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.border1"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">面板|下拉|禁用边框:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.border2"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">禁用面板|表边框|滑块:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.border3"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">最浅边框颜色:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.border4"
                            ></el-color-picker>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="4">Success颜色:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.success"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">Warning颜色:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.warning"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">Danger|Error:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.danger"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">Info背景:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.info"
                            ></el-color-picker>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="4">一级字体颜色:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.text1"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">常规|常用字体颜色:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.text2"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">次级字体颜色:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.text3"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">placeholder字体|hover边框:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.text4"
                            ></el-color-picker>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="4">深背景组件字体颜色:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.text5"
                            ></el-color-picker>
                        </el-col>
                        <el-col :span="4">深背景自定义字体颜色:</el-col>
                        <el-col :span="2">
                            <el-color-picker
                                v-model="themeVars.textDark"
                            ></el-color-picker>
                        </el-col>
                    </el-row>
                </div>
            </div>
            <div
                class="theme-pannel layout-margin-top20 layout-margin-bottom20"
            >
                <div class="theme-pannel__title">
                    以下为参数配置,调整一套样式后，可以用作一套主题。
                    <DataButton
                        style="float: right"
                        text="应用Json参数"
                        @click="applyJsonVars"
                    ></DataButton>
                </div>
                <el-input
                    type="textarea"
                    v-model="themeVarsJson"
                    :rows="8"
                ></el-input>
            </div>
            <div
                class="theme-pannel layout-margin-top20 layout-margin-bottom20"
            >
                <div class="theme-pannel__title">以下为各组件预览效果</div>
                <el-row class="layout-margin-top20" :gutter="10">
                    <el-col :span="6">
                        <el-input
                            placeholder="请输入日期"
                            suffix-icon="el-icon-date"
                            v-model="cValue"
                        ></el-input>
                    </el-col>
                    <el-col :span="6">
                        <el-input
                            placeholder="请输入内容"
                            prefix-icon="el-icon-search"
                            v-model="cValue"
                        ></el-input>
                    </el-col>
                    <el-col :span="6">
                        <el-select v-model="select.value" placeholder="请选择">
                            <el-option
                                v-for="item in select.options"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            >
                            </el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="6">
                        <el-select
                            v-model="select.value3"
                            multiple
                            placeholder="请选择"
                        >
                            <el-option
                                v-for="item in select.options"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            >
                            </el-option>
                        </el-select>
                    </el-col>
                </el-row>
                <el-row class="layout-margin-top20" :gutter="10">
                    <el-col :span="6">
                        <el-cascader
                            v-model="select.value2"
                            :options="select.options2"
                        ></el-cascader>
                    </el-col>
                    <el-col :span="6">
                        <el-date-picker
                            v-model="datePikcer.value"
                            type="date"
                            placeholder="选择日期"
                        ></el-date-picker>
                    </el-col>
                </el-row>
                <div class="layout-margin-top20 layout-margin-bottom20">
                    <el-button @click="changeTheme('dark')">主题dark</el-button>
                    <el-button @click="changeTheme('light')"
                        >主题light</el-button
                    >
                    <el-button icon="el-icon-plus">默认按钮</el-button>
                    <el-button icon="el-icon-refresh">默认按钮</el-button>
                    <el-button type="text">文字按钮</el-button>
                    <el-button type="primary">主要按钮</el-button>
                    <el-button type="success">成功按钮</el-button>
                    <el-button type="info">信息按钮</el-button>
                    <el-button type="warning">警告按钮</el-button>
                    <el-button type="danger">危险按钮</el-button>
                    <br /><br />
                    <el-button @click="showMsgBox">提示框 MessageBox</el-button>
                    <el-button @click="showDialog">对话框 Dialog</el-button>
                </div>
                <el-table
                    :data="table.data"
                    stripe
                    border
                    style="width: 100%; height: 250px"
                    height="150px;"
                >
                    <el-table-column type="index" align="center" width="50">
                    </el-table-column>
                    <el-table-column
                        prop="date"
                        label="日期"
                        width="180"
                        align="right"
                    ></el-table-column>
                    <el-table-column label="姓名" width="180">
                        <el-table-column
                            prop="date"
                            label="姓"
                            width="180"
                            align="center"
                        ></el-table-column>
                        <el-table-column
                            prop="date"
                            label="名"
                            width="180"
                            align="center"
                        ></el-table-column>
                    </el-table-column>
                    <el-table-column
                        prop="address"
                        label="地址"
                        align="right"
                    ></el-table-column>
                    <el-table-column
                        label="操作"
                        width="140"
                        align="center"
                        fixed="right"
                    >
                        <template>
                            <el-button
                                type="text"
                                size="small"
                                icon="el-icon-edit"
                                >编辑</el-button
                            >
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination
                    :page-size="20"
                    :pager-count="11"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="1000"
                >
                </el-pagination>
                <div class="layout-margin-top20">
                    <div class="theme-text-color-regular">普通tree</div>
                    <el-tree
                        :props="tree.defaultProps"
                        :data="tree.data"
                        show-checkbox
                    >
                    </el-tree>
                    <div class="theme-text-color-regular">自定义tree</div>
                    <el-tree
                        :props="tree.defaultProps"
                        :data="tree.data"
                        show-checkbox
                        node-key="id"
                        default-expand-all
                        :expand-on-click-node="false"
                    >
                        <span
                            class="custom-tree-node"
                            slot-scope="{ node }"
                        >
                            <span>{{ node.label }}</span>
                            <span>
                                <el-button type="text" size="mini"
                                    >Append</el-button
                                >
                                <el-button type="text" size="mini"
                                    >Delete</el-button
                                >
                            </span>
                        </span>
                    </el-tree>
                </div>
                <div class="layout-margin-top20">
                    <el-radio v-model="radio" label="1">备选项1</el-radio>
                    <el-radio v-model="radio" label="2">备选项2</el-radio>
                    <el-checkbox v-model="checked">备选项</el-checkbox>
                    <el-switch v-model="checked"> </el-switch>
                </div>
                <div class="layout-margin-top20">
                    <el-transfer
                        v-model="transfer.value"
                        :data="transfer.data"
                    ></el-transfer>
                </div>
                <div class="layout-margin-top20">
                    <el-badge :value="12" class="item">
                        <el-button size="small">评论</el-button> </el-badge
                    >&#12288;&#12288;
                    <el-badge :value="3" class="item">
                        <el-button size="small">回复</el-button> </el-badge
                    >&#12288;&#12288;
                    <el-badge :value="1" class="item" type="primary">
                        <el-button size="small">评论</el-button> </el-badge
                    >&#12288;&#12288;
                    <el-badge :value="2" class="item" type="warning">
                        <el-button size="small">回复</el-button>
                    </el-badge>
                    &#12288;&#12288;
                    <el-dropdown trigger="click">
                        <span class="el-dropdown-link">
                            点我查看<i
                                class="el-icon-caret-bottom el-icon--right"
                            ></i>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item class="clearfix">
                                评论
                                <el-badge class="mark" :value="12" />
                            </el-dropdown-item>
                            <el-dropdown-item class="clearfix">
                                回复
                                <el-badge class="mark" :value="3" />
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                    <el-calendar v-model="calendarValue"> </el-calendar>
                </div>
                <div
                    style="height: 100px"
                    class="theme-bg-base layout-margin-top20"
                ></div>
            </div>
        </div>

        <!--动态jsx组件-->
        <Jsx :fn="fn"></Jsx>
    </div>
</template>

<script lang="jsx">
  import formhelper from '../../../../../utils/formhelper.js';
  import ThemeTemplate from '../../../../../utils/ThemeTemplate.js';

  export default {
    name: "ThemeTest",
    props: {
      vars: {
        type: Object | String,
      },
    },
    data() {
      this.$theme = ThemeTemplate;
      this._rawVars = this.$theme.currColorVars || {};
      let themeVars = this._rawVars;
      let inputVars = this.vars;
      if (inputVars) {
        if (typeof inputVars === "string")
          inputVars = JSON.parse(inputVars);
        themeVars = inputVars;
      }
      return {
        themeVars: {
          borderRadius: "1",
          bg1: '#FFFFFF', // 控件默认背景色
          bg2: '#F5F7FA', // 通用背景色 background-color-base  +表格 穿梭框等少量控件背景 hover focus背景
          bgTitle: '#F5F7FA',
          bgTh: '#F5F7FA',
          bgDark: '#409EFF',
          primary: '#409EFF',
          success: '#67C23A',
          warning: '#E6A23C',
          danger: '#F56C6C', //也是error的颜色
          info: '#909399', // 和3号字体色差不多
          text1: '#303133', // 字体颜色 333
          text2: '#606266',  // 最常用字体颜色 各种label 分页 下拉 表格
          text3: '#909399', // plain info tag 字体颜色
          text4: '#C0C4CC',// place-holder的字体颜色
          text5: '#FFFFFF',// 深色背景下的字体颜色
          textDark: '#FFFFFF',// 深色自定义组件背景下的字体颜色
          border1: '#DCDFE6', // 主border色 input边框 tab边框 按钮边框  但active focus后会切到primary相关的颜色
          border2: '#E4E7ED', // 面板类的 下拉的边框 禁用状态下的边框
          border3: '#EBEEF5', //  cb禁用色 折叠面板  table-border
          border4: '#F2F6FC',// 最浅 禁用 边框 颜色，
          ...themeVars,
          // ...this.$util.localStorage("__test_theme")
        },
        themeVarsJson: "",
        fn: null,
        select: {
          options: [
            {
              value: '选项1',
              label: '黄金糕'
            }, {
              value: '选项2',
              label: '双皮奶'
            }, {
              value: '选项3',
              label: '蚵仔煎'
            }, {
              value: '选项4',
              label: '龙须面'
            }, {
              value: '选项5',
              label: '北京烤鸭'
            }],
          value: '',
          options2: [
            {
              value: 'zhinan',
              label: '指南',
              children: [{
                value: 'shejiyuanze',
                label: '设计原则',
                children: [{
                  value: 'yizhi',
                  label: '一致'
                }, {
                  value: 'fankui',
                  label: '反馈'
                }, {
                  value: 'xiaolv',
                  label: '效率'
                }, {
                  value: 'kekong',
                  label: '可控'
                }]
              }, {
                value: 'daohang',
                label: '导航',
                children: [{
                  value: 'cexiangdaohang',
                  label: '侧向导航'
                }, {
                  value: 'dingbudaohang',
                  label: '顶部导航'
                }]
              }]
            }, {
              value: 'zujian',
              label: '组件',
              children: [{
                value: 'basic',
                label: 'Basic',
                children: [{
                  value: 'layout',
                  label: 'Layout 布局'
                }, {
                  value: 'color',
                  label: 'Color 色彩'
                }, {
                  value: 'typography',
                  label: 'Typography 字体'
                }, {
                  value: 'icon',
                  label: 'Icon 图标'
                }, {
                  value: 'button',
                  label: 'Button 按钮'
                }]
              }, {
                value: 'form',
                label: 'Form',
                children: [{
                  value: 'radio',
                  label: 'Radio 单选框'
                }, {
                  value: 'checkbox',
                  label: 'Checkbox 多选框'
                }, {
                  value: 'input',
                  label: 'Input 输入框'
                }, {
                  value: 'input-number',
                  label: 'InputNumber 计数器'
                }, {
                  value: 'select',
                  label: 'Select 选择器'
                }, {
                  value: 'cascader',
                  label: 'Cascader 级联选择器'
                }, {
                  value: 'switch',
                  label: 'Switch 开关'
                }, {
                  value: 'slider',
                  label: 'Slider 滑块'
                }, {
                  value: 'time-picker',
                  label: 'TimePicker 时间选择器'
                }, {
                  value: 'date-picker',
                  label: 'DatePicker 日期选择器'
                }, {
                  value: 'datetime-picker',
                  label: 'DateTimePicker 日期时间选择器'
                }, {
                  value: 'upload',
                  label: 'Upload 上传'
                }, {
                  value: 'rate',
                  label: 'Rate 评分'
                }, {
                  value: 'form',
                  label: 'Form 表单'
                }]
              }, {
                value: 'data',
                label: 'Data',
                children: [{
                  value: 'table',
                  label: 'Table 表格'
                }, {
                  value: 'tag',
                  label: 'Tag 标签'
                }, {
                  value: 'progress',
                  label: 'Progress 进度条'
                }, {
                  value: 'tree',
                  label: 'Tree 树形控件'
                }, {
                  value: 'pagination',
                  label: 'Pagination 分页'
                }, {
                  value: 'badge',
                  label: 'Badge 标记'
                }]
              }, {
                value: 'notice',
                label: 'Notice',
                children: [{
                  value: 'alert',
                  label: 'Alert 警告'
                }, {
                  value: 'loading',
                  label: 'Loading 加载'
                }, {
                  value: 'message',
                  label: 'Message 消息提示'
                }, {
                  value: 'message-box',
                  label: 'MessageBox 弹框'
                }, {
                  value: 'notification',
                  label: 'Notification 通知'
                }]
              }, {
                value: 'navigation',
                label: 'Navigation',
                children: [{
                  value: 'menu',
                  label: 'NavMenu 导航菜单'
                }, {
                  value: 'tabs',
                  label: 'Tabs 标签页'
                }, {
                  value: 'breadcrumb',
                  label: 'Breadcrumb 面包屑'
                }, {
                  value: 'dropdown',
                  label: 'Dropdown 下拉菜单'
                }, {
                  value: 'steps',
                  label: 'Steps 步骤条'
                }]
              }, {
                value: 'others',
                label: 'Others',
                children: [{
                  value: 'dialog',
                  label: 'Dialog 对话框'
                }, {
                  value: 'tooltip',
                  label: 'Tooltip 文字提示'
                }, {
                  value: 'popover',
                  label: 'Popover 弹出框'
                }, {
                  value: 'card',
                  label: 'Card 卡片'
                }, {
                  value: 'carousel',
                  label: 'Carousel 走马灯'
                }, {
                  value: 'collapse',
                  label: 'Collapse 折叠面板'
                }]
              }]
            }, {
              value: 'ziyuan',
              label: '资源',
              children: [{
                value: 'axure',
                label: 'Axure Components'
              }, {
                value: 'sketch',
                label: 'Sketch Templates'
              }, {
                value: 'jiaohu',
                label: '组件交互文档'
              }]
            }],
          value2: [],
          value3: [],
        },
        datePikcer: {
          value: null,
        },
        table: {
          data: [{
            date: '2016-05-02',
            name: '王小虎',
            address: '上海市普陀区金沙江路 1518 弄'
          }, {
            date: '2016-05-04',
            name: '王小虎',
            address: '上海市普陀区金沙江路 1517 弄'
          }, {
            date: '2016-05-01',
            name: '王小虎',
            address: '上海市普陀区金沙江路 1519 弄'
          }, {
            date: '2016-05-03',
            name: '王小虎',
            address: '上海市普陀区金沙江路 1516 弄'
          }]
        },
        tree: {
          data: [
            {
              id: 1,
              label: '一级 1',
              children: [{
                id: 4,
                label: '二级 1-1',
                children: [{
                  id: 9,
                  label: '三级 1-1-1'
                }, {
                  id: 10,
                  label: '三级 1-1-2'
                }]
              }]
            }, {
              id: 2,
              label: '一级 2',
              children: [{
                id: 5,
                label: '二级 2-1'
              }, {
                id: 6,
                label: '二级 2-2'
              }]
            }, {
              id: 3,
              label: '一级 3',
              children: [{
                id: 7,
                label: '二级 3-1'
              }, {
                id: 8,
                label: '二级 3-2'
              }]
            }],
          defaultProps: {
            children: 'children',
            label: 'label'
          }
        },
        cValue: null,
        radio: '1',
        checked: true,
        themeTemplate: ThemeTemplate,
        transfer: {
          value: [1, 4],
          data: (() => {
            const data = [];
            for (let i = 1; i <= 15; i++) {
              data.push({
                key: i,
                label: `备选项 ${i}`,
                disabled: false
              });
            }
            return data;
          })()
        },
        calendarValue:new Date(),
        fileList: [{
          name: 'food.jpeg',
          url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100'
        }, {
          name: 'food2.jpeg',
          url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100'
        }],
      }
    },
    created() {

    },

    mounted() {

    },
    watch: {
      themeVars: {
        deep: true,
        immediate: true,
        handler(themeVars) {
          let copyValue = {...this.themeVars};
          Object.keys(copyValue).forEach(key => {
            copyValue[key] = ('' + copyValue[key]).toUpperCase();
          });
          this.themeVarsJson = JSON.stringify(copyValue, null, '\t');
          ThemeTemplate.genNewStyle(themeVars, this.$util.rootVue, false);
        },
      },
      //可以自由监听样式变化
      'themeTemplate.currColorVars'(v) {
        //console.log("curr vars changed:", v);
        // this.$util.localStorage("__test_theme", v);
      },
    },
    methods: {
      //应用json配置
      applyJsonVars() {
        try {
          this.themeVars = {
            ...this.themeVars,
            ...eval(`(${this.themeVarsJson})`)
          };
        } catch (e) {
          console.warn(e);
        }
      },
      /**
       * 显示提示对话框
       */
      showMsgBox() {
        this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
      showDialog() {
        //this.fn =(h, context, params, jsx) => {
        //    return (<AutoDialog show={true} onClose={this.emptyFn}/>);
        //};
        this.$dialog.show({
          bind: this,
          title: "测试动态表单",
          width: "400px",
          component: "DataEditForm",
          fullscreen: false,
          propsData: {
            edit: formhelper.create([
              formhelper.itemCreator.input('角色编号', 'roleId', null, this.$rules.required()),
              formhelper.itemCreator.textarea('角色名称', 'roleName', null, this.$rules.required()),
              formhelper.itemCreator.upload('上传附件', 'roleFiles', null, this.$rules.required(), null, {type_limit: 4}),
              formhelper.itemCreator.select('操作', 'op', '1', null, "sys_oper_type")
            ], false),
          },
          onAutoOk: (rs, dialog, next) => {
            console.log(rs, dialog, next);
          }
        })
      },
      emptyFn() {
        this.fn = null;
      },
      changeTheme(name) {
        this.$theme.genNewStyle(name, this.$util.rootVue, false);
      },
    },
    beforeDestroy() {
      //恢复原样式
      this.$theme.genNewStyle(this.$theme.getLastSavedVars(), this.$util.rootVue, true);
    }
  }
</script>

<style lang="scss">
.ThemeTest {
    .hexff00ff {
        color: teal;
        background: teal;
    }
}
</style>
