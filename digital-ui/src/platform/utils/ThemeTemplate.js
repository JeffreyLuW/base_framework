/**
 * 本地存储的样式变量
 * @type {string}
 */
const UKey_color_vars = 'color_vars';
const UKey_color_vars_bgdark = 'color_vars_bgdark';

const saveKeyPrefix = '__ThemeTemplate__';

function autoSave(target, key, val) {
  if (val === undefined) {
    //get val
    let savedVal = target.getItem(saveKeyPrefix + key);
    if (savedVal) {
      return JSON.parse(savedVal).val;
    } else {
      return undefined;
    }
  } else {
    target.setItem(saveKeyPrefix + key, JSON.stringify({val}));
    return val;
  }
}

/**
 * 这里应该是某个样式标准模板。用于实现Element主题简单定制。
 * 加载顺序应该在ElementUI样式之后： Vue.use(ThemeTemplate);
 * 组件中注入$theme
 * 可以根据参数生成样式;可以获取当前各种颜色，用于动态生成。可以计算颜色深浅。
 */
export default {
  __targetStyle: null,
  currColorVars: null,//可以watch当前对象,实现跟随当然主题色变化。初始化时不可用。 this.$theme.currColorVars
  /**
   * 可以放置预置的主题
   */
  preparedTheme: {
    'light': {
      key: "light",
      name: '蓝色经典',
      vars: {
        borderRadius: '1',
        bg1: '#FFFFFF',
        bg2: '#F4F9FD',
        bgTitle: '#E4EFFF',
        bgTh: '#E4EFFF',
        bgDark: '#2563BB',
        textDark: '#FFFFFF',
        primary: '#2563BB',
        success: '#67C23A',
        warning: '#E6A23C',
        danger: '#DF1D1D',
        info: '#909399',
        text1: '#333333',
        text2: '#333333',
        text3: '#000000',
        text4: '#C4C4C4',
        text5: '#ffffff',
        border1: '#C4C4C4',
        border2: '#C4C4C4',
        border3: '#BAD4F1',
        border4: '#F2F6FC'
      },
    },
  },
  /**
   *  auto表示首次应用默认变量，以后自动应用上次主题；
   *  defaultTheme 指定的样式变量|preparedTheme的键名
   * @param Vue
   * @param options  {auto:boolean,defaultTheme:'dark'||{...newColorVars}}
   */
  install(Vue, options) {
    //组件中注入$theme 根据$theme来应用默认样式
    Vue.prototype.$theme = this;
    //可能有外部传入的定制主题。
    if (options) {
      if (options.preparedTheme) {
        this.preparedTheme = options.preparedTheme;
      }
      if (options.defaultTheme) {
        this.defaultTheme = options.defaultTheme;
      }
    }
    if (!this.defaultTheme || !this.preparedTheme[this.defaultTheme]) {
      this.defaultTheme = Object.keys(this.preparedTheme)[0];
    }
    //如果想外部指定颜色，则应该使用
    let auto = !options || options.auto;
    if (auto) {
      let lastVars = autoSave(localStorage, UKey_color_vars);
      if (!lastVars) {
        //第一次使用--使用默认值
        this.genNewStyle(this.defaultTheme);
      } else {
        //非首次设置，自动应用上次
        this.genNewStyle(lastVars);
      }
    }
  },
  /**
   * 判断参数是否是字符串，如果是则取对应的主题变量
   * @param defaultVars
   * @return {{}}
   * @private
   */
  _getVarsByThemeName(defaultVars) {
    if (defaultVars && typeof defaultVars === "string") {
      let themeName = defaultVars;
      defaultVars = this.preparedTheme[defaultVars];
      if (defaultVars) {
        defaultVars = defaultVars.vars;

        if (document.body.$theme) {
          document.body.classList.remove(document.body.$theme);
        }
        document.body.classList.add(themeName);
        document.body.$theme = themeName;
      }
    }
    if (!defaultVars) {
      defaultVars = this._getVarsByThemeName(this.defaultTheme);
    }
    return defaultVars;
  },

  //用于替换新的样式 newColorVars='theme-name'|{}
  genNewStyle(newColorVars = {}, vueInstance = null, genNewCss = true) {
    let style = this.__targetStyle;
    if (!style) {
      style = document.createElement("style");
      style.setAttribute("type", "text/css");
      style.setAttribute("id", saveKeyPrefix);

      document.head.appendChild(style);
      this.__targetStyle = style;
    }
    style.innerHTML = this.genNewCss(newColorVars, genNewCss);
    if (vueInstance) {
      vueInstance.$root.$emit("themeChange", this);
    }
  },
  destory() {
    let style = this.__targetStyle;
    if (style) {
      document.head.removeChild(style);
    }
    this.__targetStyle = null;
  },
  //根据rgb生成颜色 #F00F96
  _genColor(r, g, b) {
    if (r < 0) r = 0;
    if (r > 255) r = 255;
    if (g < 0) g = 0;
    if (g > 255) g = 255;
    if (b < 0) b = 0;
    if (b > 255) b = 255;
    return ('#'
      + Number(Math.round(r)).toString(16).padStart(2, '0')
      + Number(Math.round(g)).toString(16).padStart(2, '0')
      + Number(Math.round(b)).toString(16).padStart(2, '0')).toUpperCase();
  },
  //自身颜色百分比变化 rate (0.1 -0.9-1.9);  <1 变暗
  colorSelfRate(color, rate) {
    color = color.replace('#', '');
    let cInfo = {
      r: parseInt('0x' + color.substring(0, 2)),
      g: parseInt('0x' + color.substring(2, 4)),
      b: parseInt('0x' + color.substring(4)),
    };
    return this._genColor(cInfo.r * rate, cInfo.g * rate, cInfo.b * rate);
  },
  //颜色变白|亮 rate: 0 color原色 1白色
  colorLightRate(color, rate) {
    color = color.replace('#', '');
    let cInfo = {
      r: parseInt('0x' + color.substring(0, 2)),
      g: parseInt('0x' + color.substring(2, 4)),
      b: parseInt('0x' + color.substring(4)),
    };
    return this._genColor(cInfo.r + (255 - cInfo.r) * rate, cInfo.g + (255 - cInfo.g) * rate, cInfo.b + (255 - cInfo.b) * rate);
  },
  //替换数字，计算rate
  _radiusRate(css, rate) {
    return css.replace(/\d/g, function (s) {
      return Math.round(parseInt(s) * rate);
    });
  },
  //获取之前保存的主题变量 String|{}
  getLastSavedVars() {
    return autoSave(localStorage, UKey_color_vars);
  },
  /**
   * 颜色替换。生成新样式。
   */
  genNewCss(newColorVars = {}, saveVars = true) {
    //保存当前的变量
    saveVars && autoSave(localStorage, UKey_color_vars, newColorVars);

    //如果 newColorVars 传递的是主题名，则取对应变量
    newColorVars = this._getVarsByThemeName(newColorVars);
    //保存深背景色
    saveVars && autoSave(localStorage, UKey_color_vars_bgdark, newColorVars.bgDark);

    let radiusCssString = this.radiusTemplate;
    let templateCssString = this.template;
//element用到的颜色  浅色并不能自动生成！ 只能是深色
// 409EFF 当前主题色的值 对应的各个颜色

    //自身颜色变化 rate是自身的倍数，1.1=*1.1,0.9=*0.9
    let selfRate = this.colorSelfRate.bind(this);
    //变亮比率 1 表示完全变白色 0 不变
    let lightRate = this.colorLightRate.bind(this);

    if (!newColorVars) newColorVars = {};

    // info text3互相赋值。
    if (!newColorVars.info && newColorVars.text3) newColorVars.info = newColorVars.text3;
    if (!newColorVars.text3 && newColorVars.info) newColorVars.text3 = newColorVars.info;

    let baseColorVars = {
      borderRadius: 1,//border 圆弧半径
      bg1: '#FFFFFF', // 控件默认背景色
      bg2: '#F5F7FA', // 通用背景色  表格 穿梭框等少量控件背景 hover focus背景
      primary: '#409EFF',
      success: '#67C23A',
      warning: '#E6A23C',
      danger: '#F56C6C', //也是error的颜色
      info: '#909399', // 和3号字体色差不多
      text1: '#303133', // 字体颜色 333
      text2: '#606266',  // 最常用字体颜色 各种label 分页 下拉 表格
      text3: '#909399', // plain info tag 字体颜色
      text4: '#C0C4CC',// place-holder的字体颜色
      text5: '#FFFFFF',//深色背景下的字体颜色
      border1: '#DCDFE6', // 主border色 input边框 tab边框 按钮边框  但active focus后会切到primary相关的颜色
      border2: '#E4E7ED', // 面板类的 下拉的边框 禁用状态下的边框
      border3: '#EBEEF5', //  cb禁用色 折叠面板  table-border
      border4: '#F2F6FC',//最浅 禁用 边框 颜色
      ...newColorVars
    };

    this.currColorVars = baseColorVars;
    // let currColorMap = {
    //
    //     '#3A8EE6': selfRate(baseColorVars.primary,0.9), // 9  主题色 深一点 btn:active 字体色  s*-0.1 (原颜色-0.1)
    //     '#409EFF': baseColorVars.primary, // 142 主题色-激活状态   0  64 158 255
    //     '#66B1FF': lightRate(baseColorVars.primary,0.2), // 7  背景色 比主题色浅 按钮链接hover状态 c+(255-color)*0.2
    //     '#8CC5FF': lightRate(baseColorVars.primary,0.4), // 3 主题很浅  做阴影 disable按钮字体  0.4
    //     '#A0CFFF': lightRate(baseColorVars.primary,0.5), // 3 很浅的主题色 禁用文本使用 0.5
    //     '#B3D8FF': lightRate(baseColorVars.primary,0.6), // 2 border-color 很浅 主题色  0.6
    //     '#C6E2FF': lightRate(baseColorVars.primary,0.7), // 1 button hover border 主题色  0.7
    //     '#D9ECFF': lightRate(baseColorVars.primary,0.8), // 2 激活border 很浅主题色  0.8
    //     '#ECF5FF': lightRate(baseColorVars.primary,0.9), // 11 很浅bg 主题色  0.9
    //     '#F0F7FF': lightRate(baseColorVars.primary,0.92), //1 主题色 bg 狠狠浅 tree-node-current  0.92
    //     '#F2F8FE': lightRate(baseColorVars.primary,0.93), // 2 主题色 背景 hover 很浅  0.93
    //
    //     '#F5F7FA':  baseColorVars.bg2, // 31 很浅的背景 主题色 也是table的hover颜色   245 247 250
    //     '#FAFAFA':  baseColorVars.bg2, // 1 table 条纹 背景
    //
    //     '#E6F1FE': selfRate(baseColorVars.bg2,0.94), // 1 主题色 bg 浅 230 241 254
    //     '#EDF2FC': selfRate(baseColorVars.bg2,0.97), // 2 bg color 主题色 237 242 252
    //     '#F0F2F5': selfRate(baseColorVars.bg2,0.98), // 2 bg 主题色 很很浅 240 242 245
    //     '#F4F4F5': selfRate(baseColorVars.bg2,0.995), // 5 浅背景
    //     '#FBFDFF': selfRate(baseColorVars.bg2,1.02), // 1 el-upload--picture-card bg  251 253 255
    //
    //
    //     '#B4BCCC': selfRate(baseColorVars.border1,0.82), // 1 .el-input::-webkit-scrollbar-thumb 主题色相关  ????  180 188 204
    //     '#C0CCDA': selfRate(baseColorVars.border1,0.88), // 3 upload 边框颜色 主题色 很浅 ????  算法不同!!  192  204  218
    //     '#D1DBE5': selfRate(baseColorVars.border1,0.95), // 2  border   ???  209 219 229
    //     '#D3D4D6': selfRate(baseColorVars.border1,0.96), // 2  border  ???  211 212 214
    //     '#D3DCE6': selfRate(baseColorVars.border1,0.96), // 2 主题相关 el-tabs__new-tab border color  211 220 230
    //     '#DCDFE6': baseColorVars.border1, // 30 border使用 浅主题色 220 223 230    1 主border色
    //     '#DFE4ED': baseColorVars.border1, // 2 主题色border  223  228 237
    //     '#E4E7ED':  baseColorVars.border2, // 39 很浅的border 背景 主题色  228 231 237  2
    //     '#F2F6FC': selfRate(baseColorVars.border1,1.1), // 8 很浅的背景  主题色  242 246 252
    //     '#EBEEF5':  baseColorVars.border3, // 48 border 很浅bg 主题色  235 238 245   3
    //     '#D9D9D9': lightRate(baseColorVars.border1,-0.1), // 1 el-upload-dragger border  217 217 217
    //     '#DCDCDC': baseColorVars.border1, // 2 border  220 220 220
    //     '#E4E4E4': lightRate(baseColorVars.border1,0.2), // 6 灰色 border  228 228 228
    //     '#E6E6E6': lightRate(baseColorVars.border1,0.3), // 3 border 230 230 230
    //     '#E9E9EB': lightRate(baseColorVars.border1,0.4), // 2 border tag button  233 233 235
    //     '#F0F0F0': lightRate(baseColorVars.border1,0.6),// 2 border 240 240 240
    //
    //
    //
    //     '#303133':  baseColorVars.text1, // 49 默认字体颜色  ----
    //     '#333333': baseColorVars.text1, // 2  字体色 el-color-dropdown__btn el-picker-panel__btn
    //     '#606266': baseColorVars.text2, // 56  字体色   ---- 96 98 102
    //     '#666666': baseColorVars.text2, // 2  字体色
    //     '#72767B': lightRate(baseColorVars.text2,0.12), // 1 字体 el-drawer__header  114 118 123
    //     '#82848A': selfRate(baseColorVars.text3,0.875), // 4 字体  激活使用 btn.info.plain.active  130 132 138
    //     '#8C939D': baseColorVars.text3, // 1 上传 card 字 140 147 157
    //     '#909399': baseColorVars.text3, // 59 字体色 ----  144 147  153
    //     '#999999': baseColorVars.text3, // 4 次 字体色 picker
    //     '#BBBBBB': lightRate(baseColorVars.text3,0.396), // 3 禁用字体色 187 187 187
    //     '#BCBEC2': lightRate(baseColorVars.text3,0.396), // 1 禁用字体色 188  190 194  0.396
    //     '#C0C4CC': baseColorVars.text4, // 102 placeholder 字体色 主题色 很浅  ????  192 196 204
    //     '#A6A9AD': lightRate(baseColorVars.text3,0.225), // 4 btn-info focus背景边框 166 169 173
    //     '#C8C9CC': lightRate(baseColorVars.text3,0.52), // 3 禁用info按钮字体|背景 200 201 204
    //
    //
    //     '#5DAF34': selfRate(baseColorVars.success,0.9), // 4  绿色 success状态的背景  93 175  52   -s*0.1
    //     '#67C23A': baseColorVars.success, // 34 success 绿色  背景 边框 103 194 58   基准色
    //     '#85CE61': lightRate(baseColorVars.success,0.2), // 4 success 背景 133 206 97   *0.2
    //     '#A4DA89': lightRate(baseColorVars.success,0.4), // 1 success disable 164 218  137  *0.4
    //     '#B3E19D': lightRate(baseColorVars.success,0.47), // 3 success border 175 225 157  *0.47
    //     '#C2E7B0': lightRate(baseColorVars.success,0.53), // 2 success border 194 231 176  *0.53
    //     '#E1F3D8': lightRate(baseColorVars.success,0.8), // 3 success border 225  243 216   light*0.8
    //     '#F0F9EB': lightRate(baseColorVars.success,0.9), // 5 部分success背景 240 249 235  light*0.9
    //
    //     '#CF9236': selfRate(baseColorVars.warning,0.9), // 4 warning 背景  207 146  54
    //     '#E6A23C': baseColorVars.warning, // 29 warning 字体色 230 162 60
    //     '#EBB563': lightRate(baseColorVars.warning,0.2), // 4 warning hover bg  235 181 99
    //     '#F0C78A': lightRate(baseColorVars.warning,0.4),  //1 warning disable 字体 240 199 138
    //     '#F3D19E': lightRate(baseColorVars.warning,0.5), // 3 warning 颜色 243 209 158   0.5
    //     '#F5DAB1': lightRate(baseColorVars.warning,0.6), // 2 warning border 245 218 177  0.6
    //     '#FAECD8': lightRate(baseColorVars.warning,0.8), // 3 warning border 250 236 216 0.8
    //     '#FDF6EC': lightRate(baseColorVars.warning,0.9), // 5 warning bg 253  246  236  0.9
    //
    //     // F56C6C 245 108 108
    //     '#F78989': lightRate(baseColorVars.danger,0.2), // 4 danger hover 背景  247 137 137    0.2
    //     '#F9A7A7': lightRate(baseColorVars.danger,0.4), // 1 danger color disable 249 167 167  0.4
    //     '#FAB6B6': lightRate(baseColorVars.danger,0.5), // 3 danger disable 背景 250 182 182   0.5
    //     '#FBC4C4': lightRate(baseColorVars.danger,0.415), // 2 danger border 251 169 169         0.415
    //     '#FDE2E2': lightRate(baseColorVars.danger,0.8), // 3 error danger border 253 226 226   0.8
    //     '#FEF0F0': lightRate(baseColorVars.danger,0.9), // 5 danger bg 254 240 240             0.9
    //     '#DD6161': selfRate(baseColorVars.danger,0.9), // 4 danger 颜色  背景 字体 221 97 97
    //
    //
    //     '#F56C6C': baseColorVars.danger, // 43 error 字体色 浅红
    //     '#000000': true, // 6  极端字体 边框
    //     '#FFFFFF': true,  // 188 很多的字体颜色 背景色 不能确定所在. 可以将背景抽出来作为一个变量。
    //     '#ECECEC': true, //  1 stroke
    //     '#13CE66': true, // 3 上传el-upload的某些背景
    //     '#FF4D51': true, // 1 红色 el-table th.required > div::before
    //     '#CCCCCC': true, // 4 背景 阴影使用
    //
    // };

    let currColorMap = {
      '$colorBg1': baseColorVars.bg1,// 控件默认背景色 比如白色
      '$colorBgDarkLight1': lightRate(baseColorVars.bgDark, 0.1),//深色自定义组件对应背景  字体对应 $textDark
      '$colorPrimaryDark': selfRate(baseColorVars.primary, 0.9), // #3A8EE6 9  主题色 深一点 btn:active 字体色  s*-0.1 (原颜色-0.1)
      '$colorPrimary': baseColorVars.primary, //#409EFF 142 主题色-激活状态   0  64 158 255
      '$colorPrimaryLight1': lightRate(baseColorVars.primary, 0.1),
      '$colorPrimaryLight2': lightRate(baseColorVars.primary, 0.2), //66B1FF 7  背景色 比主题色浅 按钮链接hover状态 c+(255-color)*0.2
      '$colorPrimaryLight4': lightRate(baseColorVars.primary, 0.4), //8CC5FF 3 主题很浅  做阴影 disable按钮字体  0.4
      '$colorPrimaryLight5': lightRate(baseColorVars.primary, 0.5), //A0CFFF 3 很浅的主题色 禁用文本使用 0.5
      '$colorPrimaryLight6': lightRate(baseColorVars.primary, 0.6), //B3D8FF 2 border-color 很浅 主题色  0.6
      '$colorPrimaryLight7': lightRate(baseColorVars.primary, 0.7), //C6E2FF 1 button hover border 主题色  0.7
      '$colorPrimaryLight8': lightRate(baseColorVars.primary, 0.8), //D9ECFF 2 激活border 很浅主题色  0.8
      '$colorPrimaryLight9': lightRate(baseColorVars.primary, 0.9), //ECF5FF 11 很浅bg 主题色  0.9
      '$colorPrimaryLight9_2': lightRate(baseColorVars.primary, 0.92), //F0F7FF 1 主题色 bg 狠狠浅 tree-node-current  0.92
      '$colorPrimaryLight9_3': lightRate(baseColorVars.primary, 0.93), //F2F8FE 2 主题色 背景 hover 很浅  0.93

      '$colorTableHover': baseColorVars.bg2, //F5F7FA 31 很浅的背景 主题色 也是table的hover颜色   245 247 250
      '$colorTableStrip': baseColorVars.bg2, //FAFAFA 1 table 条纹 背景

      '$colorBg2Dark9_4': selfRate(baseColorVars.bg2, 0.94), //E6F1FE 1 主题色 bg 浅 230 241 254
      '$colorBg2Dark9_7': selfRate(baseColorVars.bg2, 0.97), //EDF2FC 2 bg color 主题色 237 242 252
      '$colorBg2Dark9_8': selfRate(baseColorVars.bg2, 0.98), //F0F2F5 2 bg 主题色 很很浅 240 242 245
      '$colorBg2Dark9_95': selfRate(baseColorVars.bg2, 0.995), //F4F4F5 5 浅背景
      '$colorBg2Dark10_2': selfRate(baseColorVars.bg2, 1.02), //FBFDFF 1 el-upload--picture-card bg  251 253 255


      '$colorBorder1Dark8_2': selfRate(baseColorVars.border1, 0.82), //B4BCCC 1 .el-input::-webkit-scrollbar-thumb 主题色相关  ????  180 188 204
      '$colorBorder1Dark8_8': selfRate(baseColorVars.border1, 0.88), //C0CCDA 3 upload 边框颜色 主题色 很浅 ????  算法不同!!  192  204  218
      '$colorBorder1Dark9_5': selfRate(baseColorVars.border1, 0.95), //D1DBE5 2  border   ???  209 219 229
      '$colorBorder1Dark9_6': selfRate(baseColorVars.border1, 0.96), //D3D4D6 2  border  ???  211 212 214
      // '$colorBorder1Dark9_6': selfRate(baseColorVars.border1, 0.96), //D3DCE6 2 主题相关 el-tabs__new-tab border color  211 220 230
      '$colorBorder1': baseColorVars.border1, //DCDFE6 30 border使用 浅主题色 220 223 230    1 主border色
      // '$colorBorder1': baseColorVars.border1, //DFE4ED 2 主题色border  223  228 237
      '$colorBorder2': baseColorVars.border2, //E4E7ED 39 很浅的border 背景 主题色  228 231 237  2
      '$colorBorder1Dark11': selfRate(baseColorVars.border1, 1.1), //F2F6FC 8 很浅的背景  主题色  242 246 252
      '$colorBorder3': baseColorVars.border3, //EBEEF5 48 border 很浅bg 主题色  235 238 245   3
      '$colorBorder1Light_F1': lightRate(baseColorVars.border1, -0.1), //D9D9D9 1 el-upload-dragger border  217 217 217
      // '$colorBorder1': baseColorVars.border1, //DCDCDC 2 border  220 220 220
      '$colorBorderLight2': lightRate(baseColorVars.border1, 0.2), //E4E4E4 6 灰色 border  228 228 228
      '$colorBorderLight3': lightRate(baseColorVars.border1, 0.3), //E6E6E6 3 border 230 230 230
      '$colorBorderLight4': lightRate(baseColorVars.border1, 0.4), //E9E9EB 2 border tag button  233 233 235
      '$colorBorderLight6': lightRate(baseColorVars.border1, 0.6),//F0F0F0 2 border 240 240 240


      '$colorText5': baseColorVars.text5,
      '$colorText1': baseColorVars.text1, //303133 49 默认字体颜色  ----
      // '$colorText1': baseColorVars.text1, //333333 2  字体色 el-color-dropdown__btn el-picker-panel__btn
      '$colorText2': baseColorVars.text2, //606266 56  字体色   ---- 96 98 102
      // '$colorText2': baseColorVars.text2, //666666 2  字体色
      '$colorText2Light1_2': lightRate(baseColorVars.text2, 0.12), //72767B 1 字体 el-drawer__header  114 118 123
      '$colorText2Light8_75': selfRate(baseColorVars.text3, 0.875), //82848A 4 字体  激活使用 btn.info.plain.active  130 132 138
      // '$colorText3': baseColorVars.text3, //8C939D 1 上传 card 字 140 147 157
      '$colorText3': baseColorVars.text3, //909399 59 字体色 ----  144 147  153
      // '$colorText3': baseColorVars.text3, //999999 4 次 字体色 picker
      '$colorText3Light3_96': lightRate(baseColorVars.text3, 0.396), //BBBBBB 3 禁用字体色 187 187 187
      // '$colorText3Light3_96': lightRate(baseColorVars.text3, 0.396), //BCBEC2 1 禁用字体色 188  190 194  0.396
      '$colorText4': baseColorVars.text4, //C0C4CC 102 placeholder 字体色 主题色 很浅  ????  192 196 204
      '$colorText3Light2_25': lightRate(baseColorVars.text3, 0.225), //A6A9AD 4 btn-info focus背景边框 166 169 173
      '$colorText3Light5_2': lightRate(baseColorVars.text3, 0.52), //C8C9CC 3 禁用info按钮字体|背景 200 201 204


      '$colorSuccessDark9': selfRate(baseColorVars.success, 0.9), //5DAF34 4  绿色 success状态的背景  93 175  52   -s*0.1
      '$colorSuccess': baseColorVars.success, //67C23A 34 success 绿色  背景 边框 103 194 58   基准色
      '$colorSuccessLight2': lightRate(baseColorVars.success, 0.2), //85CE61 4 success 背景 133 206 97   *0.2
      '$colorSuccessLight4': lightRate(baseColorVars.success, 0.4), //A4DA89 1 success disable 164 218  137  *0.4
      '$colorSuccessLight4_7': lightRate(baseColorVars.success, 0.47), //B3E19D 3 success border 175 225 157  *0.47
      '$colorSuccessLight5_3': lightRate(baseColorVars.success, 0.53), //C2E7B0 2 success border 194 231 176  *0.53
      '$colorSuccessLight8': lightRate(baseColorVars.success, 0.8), //E1F3D8 3 success border 225  243 216   light*0.8
      '$colorSuccessLight9': lightRate(baseColorVars.success, 0.9), //F0F9EB 5 部分success背景 240 249 235  light*0.9

      '$colorWarningDark9': selfRate(baseColorVars.warning, 0.9), //CF9236 4 warning 背景  207 146  54
      '$colorWarning': baseColorVars.warning, //E6A23C 29 warning 字体色 230 162 60
      '$colorWarningLight2': lightRate(baseColorVars.warning, 0.2), //EBB563 4 warning hover bg  235 181 99
      '$colorWarningLight4': lightRate(baseColorVars.warning, 0.4),  //F0C78A 1 warning disable 字体 240 199 138
      '$colorWarningLight5': lightRate(baseColorVars.warning, 0.5), //F3D19E 3 warning 颜色 243 209 158   0.5
      '$colorWarningLight6': lightRate(baseColorVars.warning, 0.6), //F5DAB1 2 warning border 245 218 177  0.6
      '$colorWarningLight8': lightRate(baseColorVars.warning, 0.8), //FAECD8 3 warning border 250 236 216 0.8
      '$colorWarningLight9': lightRate(baseColorVars.warning, 0.9), //FDF6EC 5 warning bg 253  246  236  0.9

      // F56C6C 245 108 108
      '$colorDangerLight2': lightRate(baseColorVars.danger, 0.2), //F78989 4 danger hover 背景  247 137 137    0.2
      '$colorDangerLight4': lightRate(baseColorVars.danger, 0.4), //F9A7A7 1 danger color disable 249 167 167  0.4
      '$colorDangerLight5': lightRate(baseColorVars.danger, 0.5), //FAB6B6 3 danger disable 背景 250 182 182   0.5
      '$colorDangerLight4_15': lightRate(baseColorVars.danger, 0.415), //FBC4C4 2 danger border 251 169 169         0.415
      '$colorDangerLight8': lightRate(baseColorVars.danger, 0.8), //FDE2E2 3 error danger border 253 226 226   0.8
      '$colorDangerLight9': lightRate(baseColorVars.danger, 0.9), //FEF0F0 5 danger bg 254 240 240             0.9
      // '$colorDangerLight9': selfRate(baseColorVars.danger, 0.9), //DD6161 4 danger 颜色  背景 字体 221 97 97
      '$colorDanger': baseColorVars.danger, //F56C6C 43 error 字体色 浅红
      '$colorInfo': baseColorVars.info, // --info 背景+边框

      '#000000': true, //000000 6  极端字体 边框
      '#FFFFFF': true,  //FFFFFF 188 很多的字体颜色 背景色 不能确定所在. 可以将背景抽出来作为一个变量。
      '#ECECEC': true, //ECECEC  1 stroke
      '#13CE66': true, //13CE66 3 上传el-upload的某些背景
      '#FF4D51': true, //FF4D51 1 红色 el-table th.required > div::before
      '#CCCCCC': true, //CCCCCC 4 背景 阴影使用
    };
    // baseColorVars 默认也当做颜色变量使用
    Object.keys(baseColorVars).forEach((key) => {
      let rawKey = key;
      if (!key.startsWith('$')) key = '$' + key;
      if (!currColorMap[key]) {
        currColorMap[key] = baseColorVars[rawKey];
      }
    });
    // this._printVarDeclare(currColorMap);
    //根据 变量版本的 eleVarColor.scss 计算对应样式，看是否正常!
    let rsStyleArray = [];
    let colorReg = /\$[0-9a-zA-Z_-]+/g;
    let colorReplacer = (s) => {
      let rColor = currColorMap[s];
      if (rColor && typeof rColor === 'string') {
        return rColor;
      }
      return s;
    };
    //颜色替换 /(#[0-9a-fA-F]{3,8})|(rgb\([\d,\s]+\))/g
    rsStyleArray.push(templateCssString.replace(colorReg, colorReplacer));
    //半径 圆弧
    rsStyleArray.push(this._radiusRate(radiusCssString, baseColorVars.borderRadius));
    //是否有自定义的css替换？
    return rsStyleArray.join(' ');
  },
  //打印所有的变量声明
  _printVarDeclare(currColorMap) {
    let varDeclare = '';
    Object.keys(currColorMap).forEach((k) => {
      if (k.startsWith("$"))
        varDeclare += `${k}:${currColorMap[k]};
            `;
    });
    console.log(varDeclare);
  },
  /**
   * 用于调整border-radius ,如果想跟随主题控制，radius应该是: theme-border-radius2|3|4|6|8|10
   */
  radiusTemplate: ".theme-border-radius-er{border-radius:2px}.theme-border-radius-san{border-radius:3px}.theme-border-radius-si{border-radius:4px}.theme-border-radius-liu{border-radius:6px}.theme-border-radius-ba{border-radius:8px}.theme-border-radius-shi{border-radius:10px}.el-pagination .el-select .el-input .el-input__inner{border-radius:3px}.el-pagination__editor{border-radius:3px}.el-pagination.is-background .btn-next,.el-pagination.is-background .btn-prev,.el-pagination.is-background .el-pager li{border-radius:2px}.el-dialog{border-radius:2px}.el-autocomplete-suggestion{border-radius:4px}.el-dropdown-menu{border-radius:4px}.el-menu--collapse .el-submenu .el-menu{border-radius:2px}.el-menu--popup{border-radius:2px}.el-radio-button__inner{border-radius:0}.el-radio-button:first-child .el-radio-button__inner{border-radius:4px 0 0 4px}.el-radio-button:last-child .el-radio-button__inner{border-radius:0 4px 4px 0}.el-popover,.el-radio-button:first-child:last-child .el-radio-button__inner{border-radius:4px}.el-radio-button--medium .el-radio-button__inner{border-radius:0}.el-radio-button--small .el-radio-button__inner{border-radius:0}.el-radio-button--mini .el-radio-button__inner{border-radius:0}.el-select-dropdown{border-radius:4px}.el-table-filter{border-radius:2px}.el-date-table td.selected div{border-radius:15px}.el-date-table td.selected span{border-radius:15px}.el-month-table td .cell{border-radius:18px}.el-picker-panel{border-radius:4px}.el-picker-panel__btn{border-radius:2px}.el-time-panel{border-radius:2px}.el-time-range-picker__body{border-radius:2px}.el-message-box{border-radius:4px}.el-tabs__new-tab{border-radius:3px}.el-tabs__item:focus.is-active.is-focus:not(:active){border-radius:3px}.el-tabs--card > .el-tabs__header .el-tabs__nav{border-radius:4px 4px 0 0}.el-tabs--left.el-tabs--card .el-tabs__nav{border-radius:4px 0 0 4px}.el-tabs--right.el-tabs--card .el-tabs__nav{border-radius:0 4px 4px 0}.el-alert{border-radius:4px}.el-notification{border-radius:8px}.el-input-number__increase{border-radius:0 4px 4px 0}.el-input-number__decrease{border-radius:4px 0 0 4px}.el-input-number.is-controls-right .el-input-number__increase{border-radius:0 4px 0 0}.el-input-number.is-controls-right .el-input-number__decrease{border-radius:0 0 4px}.el-tooltip__popper{border-radius:4px}.el-slider__runway{border-radius:3px}.el-slider.is-vertical .el-slider__bar{border-radius:0 0 3px 3px}.el-upload--picture-card{border-radius:6px}.el-upload-dragger{border-radius:6px}.el-upload-list__item{border-radius:4px}.el-upload-list--picture-card .el-upload-list__item{border-radius:6px}.el-upload-list--picture .el-upload-list__item{border-radius:6px}.el-progress-bar__outer{border-radius:100px}.el-progress-bar__inner{border-radius:100px}.el-card,.el-message{border-radius:4px}.el-steps--simple{border-radius:4px}.el-tag{border-radius:4px}.el-cascader__dropdown{border-radius:4px}.el-cascader__suggestion-panel{border-radius:4px}.el-color-predefine__color-selector{border-radius:4px}.el-color-predefine__color-selector > div{border-radius:3px}.el-color-hue-slider__thumb{border-radius:1px}.el-color-alpha-slider__thumb{border-radius:1px}.el-color-dropdown__btn{border-radius:2px}.el-color-picker__mask{border-radius:4px}.el-color-picker__trigger{border-radius:4px}.el-color-picker__color{border-radius:2px}.el-color-picker__panel{border-radius:4px}.el-textarea__inner{border-radius:4px}.el-input::-webkit-scrollbar-thumb{border-radius:5px}.el-input__inner{border-radius:4px}.el-input-group__append,.el-input-group__prepend{border-radius:4px}.el-transfer__button.is-with-texts{border-radius:4px}.el-transfer-panel{border-radius:4px}.el-transfer-panel__filter .el-input__inner{border-radius:16px}.el-transfer-panel .el-checkbox__inner{border-radius:3px}.el-image-viewer__actions{border-radius:22px}.el-button{border-radius:4px}.el-button.is-loading:before{border-radius:inherit}.el-button.is-round{border-radius:20px}.el-button--medium{border-radius:4px}.el-button--mini,.el-button--small{border-radius:3px}.el-button-group > .el-button:first-child:last-child{border-radius:4px}.el-button-group > .el-button:first-child:last-child.is-round{border-radius:20px}.el-button-group > .el-button:not(:first-child):not(:last-child){border-radius:0}.el-checkbox.is-bordered{border-radius:4px}.el-checkbox.is-bordered.el-checkbox--medium{border-radius:4px}.el-checkbox.is-bordered.el-checkbox--small{border-radius:3px}.el-checkbox.is-bordered.el-checkbox--mini{border-radius:3px}.el-checkbox__inner{border-radius:2px}.el-checkbox-button__inner{border-radius:0}.el-checkbox-button:first-child .el-checkbox-button__inner{border-radius:4px 0 0 4px}.el-checkbox-button:last-child .el-checkbox-button__inner{border-radius:0 4px 4px 0}.el-checkbox-button--medium .el-checkbox-button__inner{border-radius:0}.el-checkbox-button--small .el-checkbox-button__inner{border-radius:0}.el-checkbox-button--mini .el-checkbox-button__inner{border-radius:0}.el-radio.is-bordered{border-radius:4px}.el-radio--medium.is-bordered{border-radius:4px}.el-radio--small.is-bordered{border-radius:3px}.el-radio--mini.is-bordered{border-radius:3px}.el-scrollbar__thumb{border-radius:inherit}.el-scrollbar__bar{border-radius:4px}.el-cascader-panel{border-radius:4px}.el-cascader-panel.is-bordered{border-radius:4px}.el-avatar--square{border-radius:4px}",

  //auto-code
  /**
   * css样式模板
   */
  template: ".theme-bg-dark{background-color: $bgDark;}.theme-bg-dark-op{background-color: $textDark;}.theme-bg-dark-light{background-color: $colorBgDarkLight1;}.theme-bg-primary{background-color: $colorPrimary;}.theme-bg-primary-dark{background-color: $colorPrimaryDark;}.theme-bg-primary-light1{background-color: $colorPrimaryLight1;}.theme-bg-primary-light2{background-color: $colorPrimaryLight2;}.theme-bg1,.theme-bg-control,.theme-bg-pannel{background-color: $colorBg1;}.theme-bg2,.theme-bg-base,::-webkit-scrollbar-corner{background-color: $bg2;}.theme-bg-title,.theme-table-hover tr:hover{background-color: $bgTitle;}.theme-bg-th{background-color: $bgTh;}.theme-bg-success{background-color: $colorSuccess;}.theme-bg-info{background-color: $colorInfo;}.theme-bg-warning{background-color: $colorWarning;}.theme-bg-danger{background-color: $colorDanger;}.theme-color-text-primary,.el-button--text i[class*=el-icon],.el-button--default i[class*=el-icon]{color: $colorPrimary;}.theme-text-color-primary-dark{color: $colorPrimaryDark;}.theme-text-color-primary-light{color: $colorPrimaryLight2;}.theme-text-color-1,.theme-text-color-base{color: $colorText1;}.theme-text-color-2,.theme-text-color-regular,div.ql-snow .ql-picker,div.ql-editor.ql-blank::before{color: $colorText2;}.theme-text-color-3,.theme-text-color-second,.theme-text-color-info{color: $colorText3;}.theme-text-color-4,.theme-text-color-holder{color: $colorText4;}.theme-text-color-5,.theme-text-color-darkop,div.overview-AutoDialog .el-dialog__headerbtn .el-dialog__close{color: $colorText5;}.theme-text-color-custom-dark{color: $textDark;}.theme-text-color-danger,.theme-text-color-error,.theme-table-form .theme-table-form__required:before{color: $colorDanger;}.theme-text-color-success{color: $colorSuccess;}.theme-text-color-warning{color: $colorWarning;}.theme-border-color1{border-color: $colorBorder1;}.theme-border-color2{border-color: $colorBorder2;}.theme-border-color3,div.ql-toolbar.ql-snow .ql-picker.ql-expanded span.ql-picker-options,div.ql-toolbar.ql-snow,div.ql-container.ql-snow{border-color: $colorBorder3;}.theme-border-primary{border-color: $colorPrimary;}.el-pagination{color: $colorText1;font-weight: normal;}.el-pagination button:hover,.el-pagination.is-background .el-pager li:not(.disabled):hover,.el-pager li:hover,.el-pager li.active,.el-dialog__headerbtn:focus .el-dialog__close,.el-dialog__headerbtn:hover .el-dialog__close,.el-menu-item.is-active,.el-radio-button__inner:hover,.el-switch__label.is-active,.el-table th > .cell.highlight,.el-table-filter__bottom button:hover,.el-date-table td.today span,.el-date-table td.available:hover,.el-month-table td.today .cell,.el-month-table td .cell:hover,.el-month-table td.current:not(.disabled) .cell,.el-year-table td.today .cell,.el-year-table td .cell:hover,.el-year-table td.current:not(.disabled) .cell,.el-date-picker__header-label.active,.el-date-picker__header-label:hover,.time-select-item.selected:not(.disabled),.el-picker-panel__shortcut:hover,.el-picker-panel__icon-btn:hover,.el-time-spinner__arrow:hover,.el-time-panel__btn.confirm,.el-breadcrumb__inner a:hover,.el-breadcrumb__inner.is-link:hover,.el-tabs__new-tab:hover,.el-tabs__item.is-active,.el-tabs__item:hover,.el-tabs--border-card > .el-tabs__header .el-tabs__item:not(.is-disabled):hover,.el-input-number__decrease:hover,.el-input-number__increase:hover,.el-loading-spinner .el-loading-text,.el-loading-spinner i,.el-upload--picture-card i,.el-upload-dragger .el-upload__text em,.el-upload-list__item .el-icon-close-tip,.el-upload-list__item.is-success .el-upload-list__item-name:focus,.el-upload-list__item.is-success .el-upload-list__item-name:hover,.el-upload-list__item-delete:hover,.el-step__title.is-finish,.el-step__description.is-finish,.el-collapse-item__header.focusing:focus:not(:hover),.el-tag .el-tag__close,.el-tag--plain .el-tag__close,.el-cascader__suggestion-item.is-checked,.el-color-dropdown__link-btn,.el-transfer-panel__item:hover,.el-link.el-link--default:hover,.el-link.el-link--primary,.el-backtop,.el-calendar-table td.is-today,.el-checkbox__input.is-checked + .el-checkbox__label,.el-checkbox-button__inner:hover,.el-radio__input.is-checked + .el-radio__label,.el-cascader-node.in-active-path,.el-cascader-node.is-active,.el-cascader-node.is-selectable.in-checked-path{color: $colorPrimary}.el-pagination button:disabled{color: $colorText4;background-color: $colorBg1}.el-pagination .btn-next,.el-pagination .btn-prev{background: center center no-repeat $colorBg1;color: $colorText1}.el-pagination .el-pager li.disabled,.el-pagination.is-background .btn-next.disabled,.el-pagination.is-background .btn-next:disabled,.el-pagination.is-background .btn-prev.disabled,.el-pagination.is-background .btn-prev:disabled,.el-pagination.is-background .el-pager li.disabled,.el-pager li.btn-quicknext.disabled,.el-pager li.btn-quickprev.disabled,.el-cascader-menu__item.is-disabled,.el-select-dropdown__item.is-disabled,.el-select .el-input .el-select__caret,.el-select .el-input .el-select__caret.is-show-close,.el-select__close,.el-table-filter__bottom button.is-disabled,.el-date-table td.next-month,.el-date-table td.prev-month,.el-month-table td.disabled .cell:hover,.el-year-table td.disabled .cell:hover,.el-date-editor .el-range__icon,div.vue-treeselect__placeholder,.el-date-editor .el-range-input::-webkit-input-placeholder,.el-date-editor .el-range-input::placeholder,.el-date-editor .el-range__close-icon,.el-range-editor.is-disabled input::-webkit-input-placeholder,.el-range-editor.is-disabled input::placeholder,.el-range-editor.is-disabled .el-range-separator,.el-time-spinner__item.disabled,.el-breadcrumb__separator,.el-tabs__item.is-disabled,.el-tabs--border-card > .el-tabs__header .el-tabs__item.is-disabled,.el-tree-node__expand-icon,.el-tree-node__loading-icon,.el-alert.is-light .el-alert__closebtn,.el-input-number__decrease.is-disabled,.el-input-number__increase.is-disabled,.el-upload-dragger .el-icon-upload,.el-message__closeBtn,.el-rate__icon,.el-step__title.is-wait,.el-step__description.is-wait,.el-cascader.is-disabled .el-cascader__label,.el-cascader__empty-text,.el-cascader__search-input::-webkit-input-placeholder,.el-cascader__search-input::placeholder,.el-textarea__inner::-webkit-input-placeholder,.el-textarea__inner::placeholder,.el-textarea.is-disabled .el-textarea__inner::-webkit-input-placeholder,.el-textarea.is-disabled .el-textarea__inner::placeholder,.el-input .el-input__clear,.el-input__prefix,.el-input__suffix,.el-input__inner::-webkit-input-placeholder,.el-input__inner::placeholder,.el-input.is-disabled .el-input__inner::-webkit-input-placeholder,.el-input.is-disabled .el-input__inner::placeholder,.el-link.el-link--default.is-disabled,.el-image__error,.el-calendar-table:not(.is-range) td.next,.el-calendar-table:not(.is-range) td.prev,.el-checkbox__input.is-disabled + span.el-checkbox__label,.el-radio__input.is-disabled + span.el-radio__label,.el-cascader-menu__empty-text,.el-cascader-node.is-disabled{color: $colorText4}.el-date-editor .el-range-input:-ms-input-placeholder{color: $colorText4}.el-date-editor .el-range-input::-ms-input-placeholder{color: $colorText4}.el-range-editor.is-disabled input:-ms-input-placeholder{color: $colorText4}.el-range-editor.is-disabled input::-ms-input-placeholder{color: $colorText4}.el-cascader__search-input:-ms-input-placeholder{color: $colorText4}.el-cascader__search-input::-ms-input-placeholder{color: $colorText4}.el-textarea__inner:-ms-input-placeholder{color: $colorText4}.el-textarea__inner::-ms-input-placeholder{color: $colorText4}.el-textarea.is-disabled .el-textarea__inner:-ms-input-placeholder{color: $colorText4}.el-textarea.is-disabled .el-textarea__inner::-ms-input-placeholder{color: $colorText4}.el-input__inner::-ms-input-placeholder{color: $colorText4}.el-input.is-disabled .el-input__inner:-ms-input-placeholder{color: $colorText4}.el-input.is-disabled .el-input__inner::-ms-input-placeholder{color: $colorText4}.el-pagination__sizes,.el-pagination__total,.el-pagination__jump,.el-autocomplete-suggestion li,.el-dropdown,.el-dropdown-menu__item,.el-cascader-menu__item,.el-select-dropdown__item,.el-select__input,.el-table,.el-table__expand-icon,.el-table-filter__bottom button,.el-date-table.is-week-mode .el-date-table__row:hover td.available:hover,.el-date-table td.week,.el-month-table td .cell,.el-year-table td .cell,.el-date-picker__header-label,.el-date-editor .el-range-input,.el-picker-panel__shortcut,.el-time-spinner__item,.el-message-box__content,.el-breadcrumb__inner,.el-breadcrumb__item:last-child .el-breadcrumb__inner,.el-breadcrumb__item:last-child .el-breadcrumb__inner a,.el-breadcrumb__item:last-child .el-breadcrumb__inner a:hover,.el-breadcrumb__item:last-child .el-breadcrumb__inner:hover,.el-form-item__label,.el-notification__content,.el-notification__closeBtn:hover,.el-upload__tip,.el-upload-dragger .el-upload__text,.el-upload-list__item,.el-upload-list__item .el-icon-close,.el-upload-list__item-name,.el-upload-list__item-delete,.el-progress__text,.el-cascader__label,.el-cascader__label span,.el-cascader__suggestion-list,.el-cascader__search-input,div.vue-treeselect__single-value,.el-transfer-panel__item.el-checkbox,.el-transfer-panel .el-transfer-panel__footer .el-checkbox,.el-link.el-link--default,.el-calendar-table thead th,.el-checkbox,.el-radio,.el-cascader-node.is-selectable.in-active-path{color: $colorText2}.el-pagination__sizes .el-input .el-input__inner:hover,.el-select .el-input__inner:focus,.el-select .el-input.is-focus .el-input__inner,.el-range-editor.is-active,.el-range-editor.is-active:hover,.el-input-number__decrease:hover:not(.is-disabled) ~ .el-input .el-input__inner:not(.is-disabled),.el-input-number__increase:hover:not(.is-disabled) ~ .el-input .el-input__inner:not(.is-disabled),.el-slider.is-vertical.el-slider--with-input .el-slider__input:active .el-input-number__decrease,.el-slider.is-vertical.el-slider--with-input .el-slider__input:active .el-input-number__increase,.el-upload:focus .el-upload-dragger,.el-upload-dragger:hover,.el-tag.is-hit,.el-tag--dark.is-hit,.el-tag--plain.is-hit,.el-cascader .el-input .el-input__inner:focus,.el-cascader .el-input.is-focus .el-input__inner,.el-textarea__inner:focus,.el-input.is-active .el-input__inner,.el-input__inner:focus,div.vue-treeselect--open .vue-treeselect__control,.el-link.el-link--default:after,.el-link.el-link--primary.is-underline:hover:after,.el-link.el-link--primary:after,.el-checkbox.is-bordered.is-checked,.el-checkbox__input.is-focus .el-checkbox__inner,.el-checkbox__inner:hover,.el-checkbox-button.is-focus .el-checkbox-button__inner,.el-radio.is-bordered.is-checked,.el-radio__input.is-focus .el-radio__inner,.el-radio__inner:hover{border-color: $colorPrimary}.el-pagination.is-background .btn-next,.el-pagination.is-background .btn-prev,.el-pagination.is-background .el-pager li{background-color: $colorBg2Dark9_95;color: $colorText2}.el-pagination.is-background .el-pager li:not(.disabled).active,.el-table-filter__list-item.is-active,.el-date-table td.selected span,.el-tree-node.is-drop-inner > .el-tree-node__content .el-tree-node__label{background-color: $colorPrimary;color: $colorText5}.el-dialog,.el-pager li,.el-date-range-picker__time-picker-wrap .el-picker-panel,.el-time-spinner__wrapper.is-arrow .el-time-spinner__item:hover:not(.disabled):not(.active),.el-step__icon,.el-input::-webkit-scrollbar-corner,.el-input::-webkit-scrollbar-track,.el-input::-webkit-scrollbar-track-piece,.el-input .el-input__count .el-input__count-inner,.el-button.is-plain:active{background: $colorBg1}.el-pager li.btn-quicknext,.el-pager li.btn-quickprev,.el-dialog__title,.el-menu--horizontal > .el-submenu:focus .el-submenu__title,.el-menu--horizontal > .el-submenu:hover .el-submenu__title,.el-menu--horizontal .el-menu .el-menu-item.is-active,.el-menu--horizontal .el-menu .el-submenu.is-active > .el-submenu__title,.el-menu--horizontal .el-menu-item:not(.is-disabled):focus,.el-menu--horizontal .el-menu-item:not(.is-disabled):hover,.el-menu-item,.el-submenu__title,.el-switch__label,.el-year-table .el-icon,.el-date-range-picker__time-header > .el-icon-arrow-right,.el-date-editor .el-range-separator,.el-picker-panel__icon-btn,.el-time-spinner__item.active:not(.disabled),.el-time-panel__btn,.el-popover__title,.el-breadcrumb__inner a,.el-breadcrumb__inner.is-link,.el-tabs__item,.el-notification__title,.el-step__title.is-process,.el-step__description.is-process,.el-collapse-item__content,.el-transfer-panel .el-transfer-panel__header .el-checkbox .el-checkbox__label,.el-timeline-item__content,.el-page-header__content{color: $colorText1}.el-dialog__headerbtn .el-dialog__close,.el-date-table td.today.end-date span,.el-date-table td.today.start-date span,.el-date-table td.end-date div,.el-date-table td.start-date div,.el-month-table td.today.end-date .cell,.el-month-table td.today.start-date .cell,.el-month-table td.end-date div,.el-month-table td.start-date div,.el-message-box__headerbtn .el-message-box__close,.el-message-box__headerbtn:focus .el-message-box__close,.el-message-box__headerbtn:hover .el-message-box__close,.el-alert.is-dark .el-alert__closebtn,.el-alert.is-dark .el-alert__description,.el-upload-list--picture-card .el-upload-list__item .el-icon-check,.el-upload-list--picture-card .el-upload-list__item .el-icon-circle-check,.el-upload-list--picture .el-upload-list__item .el-icon-check,.el-upload-list--picture .el-upload-list__item .el-icon-circle-check,.el-upload-cover__label i,.el-upload-cover__interact .btn,.el-upload-cover__interact .btn i,.el-progress-bar__innerText,.el-carousel__arrow,.el-tag--dark .el-tag__close,.el-tag--dark.el-tag--info .el-tag__close,.el-tag--dark.el-tag--success .el-tag__close,.el-tag--dark.el-tag--warning .el-tag__close,.el-tag--dark.el-tag--danger .el-tag__close,.el-color-picker__icon,.el-timeline-item__icon,.el-image-viewer__actions__inner{color: $colorText5}.el-dialog__body{color: $colorText2;background-color: $colorBg1;}.el-autocomplete-suggestion,.el-cascader-menus,.el-select-dropdown,div.vue-treeselect__menu,.el-time-panel{border: 1px solid $colorBorder2;background-color: $colorBg1}.el-autocomplete-suggestion li.highlighted,.el-autocomplete-suggestion li:hover,div.vue-treeselect--single .vue-treeselect__option--selected:hover,div.vue-treeselect__option--highlight,.el-select-dropdown.is-multiple .el-select-dropdown__item.selected.hover,.el-cascader-menu__item.hover,.el-cascader-menu__item:hover,.el-select-dropdown__item.hover,.el-select-dropdown__item:hover,.el-cascader-menu__item:focus:not(:active),.el-table__body tr.hover-row.current-row > td,.el-table__body tr.hover-row.el-table__row--striped.current-row > td,.el-table__body tr.hover-row.el-table__row--striped > td,.el-table__body tr.hover-row > td,.el-table--enable-row-hover .el-table__body tr:hover > td,.time-select-item:hover,.el-tree-node:focus > .el-tree-node__content,.el-tree-node__content:hover,.el-upload-list__item:hover,.el-radio__input.is-disabled .el-radio__inner::after{background-color: $colorTableHover}.el-autocomplete-suggestion li.divider{border-top: 1px solid #000000}.el-autocomplete-suggestion.is-loading li,.el-menu--horizontal > .el-menu-item,.el-menu--horizontal > .el-submenu .el-submenu__title,.el-menu-item i,.el-submenu__title i,.el-menu-item-group__title,.el-select-dropdown__empty,.el-select-group__title,.el-select .el-input .el-select__caret.is-show-close:hover,.el-select__close:hover,.el-table__empty-text,.el-table thead,.el-table__column-filter-trigger i,.el-time-spinner__arrow,.el-message-box__status.el-icon-info,.el-tabs__nav-next,.el-tabs__nav-prev,.el-tabs--border-card > .el-tabs__header .el-tabs__item,.el-tree__empty-text,.el-alert--info .el-alert__description,.el-notification__closeBtn,.el-notification .el-icon-info,.el-slider__marks-text,.el-upload-list__item-name [class^=el-icon],.el-message--info .el-message__content,.el-message__closeBtn:hover,.el-message .el-icon-info,.el-tag.el-tag--info .el-tag__close,.el-tag--plain.el-tag--info .el-tag__close,.el-cascader .el-input .el-icon-circle-close:hover,.el-color-picker__empty,.el-input .el-input__clear:hover,.el-input .el-input__count,.el-transfer-panel .el-transfer-panel__header .el-checkbox .el-checkbox__label span,.el-transfer-panel .el-transfer-panel__empty,.el-timeline-item__timestamp,.el-link.el-link--info{color: $colorText3}.el-autocomplete-suggestion.is-loading li:hover,.el-dropdown-menu__item--divided:before,.el-menu--horizontal > .el-menu-item:not(.is-disabled):focus,.el-menu--horizontal > .el-menu-item:not(.is-disabled):hover,.el-menu--horizontal > .el-submenu .el-submenu__title:hover,.el-switch__core:after,.el-cascader-menu,.el-cascader-menu__item.is-disabled:hover,.el-select-dropdown__item.is-disabled:hover,.el-table,.el-table__expanded-cell,.el-table tr,.el-table__fixed-right-patch,.el-alert,.el-slider__stop,.el-upload-list--picture .el-upload-list__item-thumbnail,.el-carousel__button,.el-carousel__mask,.el-calendar,.el-backtop,.el-checkbox__input.is-indeterminate .el-checkbox__inner::before,.el-radio__inner::after,.el-drawer{background-color: $colorBg1}.el-dropdown-menu,.el-message-box,.el-color-picker__panel{background-color: $colorBg1;border: 1px solid $colorBorder3}.el-dropdown-menu__item:focus,.el-dropdown-menu__item:not(.is-disabled):hover{background-color: $bg2;color: $colorPrimaryLight2}.el-dropdown-menu__item--divided,.el-table__footer-wrapper td,.el-table-filter__bottom,.el-calendar-table tr:first-child td{border-top: 1px solid $colorBorder3}.el-dropdown-menu__item.is-disabled,.el-picker-panel__icon-btn.is-disabled,.el-collapse-item.is-disabled .el-collapse-item__header{color: $colorText3Light3_96}.el-menu{border-right: solid 1px $colorBorder3;background-color: $colorBg1}.el-menu.el-menu--horizontal,.el-date-picker__header--bordered{border-bottom: solid 1px $colorBorder3}.el-menu--horizontal > .el-submenu.is-active .el-submenu__title,.el-menu--horizontal > .el-menu-item.is-active{border-bottom: 2px solid $colorPrimary;color: $colorText1}.el-menu--horizontal .el-menu .el-menu-item,.el-menu--horizontal .el-menu .el-submenu__title{background-color: $colorBg1;color: $colorText3}.el-menu--collapse .el-submenu .el-menu,.el-time-range-picker__body,.el-tabs--card > .el-tabs__header .el-tabs__nav,.el-cascader-panel.is-bordered{border: 1px solid $colorBorder2}.el-menu-item:focus,.el-menu-item:hover,.el-submenu__title:focus,.el-submenu__title:hover,.el-submenu__title:hover,.el-table--striped .el-table__body tr.el-table__row--striped.current-row td,.el-table__body tr.current-row > td{background-color: $colorPrimaryLight9}.el-submenu.is-active .el-submenu__title,.el-table .ascending .sort-caret.ascending{border-bottom-color: $colorPrimary}.el-radio-button__inner,.el-checkbox-button__inner{background: $colorBg1;border: 1px solid $colorBorder1;color: $colorText2}.el-radio-button:first-child .el-radio-button__inner,.el-tabs--right.el-tabs--border-card .el-tabs__header.is-right,.el-input-number__increase,.el-input-number.is-controls-right .el-input-number__decrease,.el-checkbox-button:first-child .el-checkbox-button__inner{border-left: 1px solid $colorBorder1}.el-radio-button__orig-radio:checked + .el-radio-button__inner{color: $colorText5;background-color: $colorPrimary;border-color: $colorPrimary;-webkit-box-shadow: -1px 0 0 0 $colorPrimary;box-shadow: -1px 0 0 0 $colorPrimary}.el-radio-button__orig-radio:disabled + .el-radio-button__inner,.el-button.is-disabled,.el-button.is-disabled:focus,.el-button.is-disabled:hover,.el-checkbox-button.is-disabled .el-checkbox-button__inner{color: $colorText4;background-color: $colorBg1;border-color: $colorBorder3}.el-radio-button__orig-radio:disabled:checked + .el-radio-button__inner,.el-date-table td.in-range div,.el-date-table td.in-range div:hover,.el-date-table.is-week-mode .el-date-table__row.current div,.el-date-table.is-week-mode .el-date-table__row:hover div,.el-date-table td.selected div,.el-date-table td.selected div:hover,.el-month-table td.in-range div,.el-month-table td.in-range div:hover,.el-backtop:hover{background-color: $colorBorder1Dark11}.el-radio-button:focus:not(.is-focus):not(:active):not(.is-disabled),.el-radio:focus:not(.is-focus):not(:active):not(.is-disabled) .el-radio__inner{-webkit-box-shadow: 0 0 2px 2px $colorPrimary;box-shadow: 0 0 2px 2px $colorPrimary}.el-switch__core{border: 1px solid $colorBorder1;background: $colorBorder1;}.el-switch.is-checked .el-switch__core{border-color: $colorPrimary;background-color: $colorPrimary}div.vue-treeselect--single .vue-treeselect__option--selected,.el-select-dropdown.is-multiple .el-select-dropdown__item.selected{color: $colorPrimary;background-color: $colorBg1}.el-cascader-menu__item.selected,.el-cascader-menu__item.is-active,.el-select-dropdown__item.selected{color: $colorPrimary;font-weight: normal;}.el-select-group__wrap:not(:last-of-type)::after{background: $colorBorder2}.el-select:hover .el-input__inner,.el-slider__runway.disabled .el-slider__button,.el-slider.is-vertical.el-slider--with-input .el-slider__input:hover .el-input-number__decrease,.el-slider.is-vertical.el-slider--with-input .el-slider__input:hover .el-input-number__increase,.el-cascader:not(.is-disabled):hover .el-input__inner,.el-textarea__inner:hover,.el-input__inner:hover,div.vue-treeselect:not(.vue-treeselect--disabled):not(.vue-treeselect--focused) .vue-treeselect__control:hover,.el-checkbox__input.is-disabled .el-checkbox__inner::after,.el-checkbox__input.is-disabled.is-checked .el-checkbox__inner::after{border-color: $colorText4}.el-select .el-input.is-disabled .el-input__inner:hover,.el-range-editor.is-disabled:focus,.el-range-editor.is-disabled:hover{border-color: $colorBorder2}.el-select .el-tag{background-color: $colorBg2Dark9_8}.el-select .el-tag__close.el-icon-close,.el-tabs__item .el-icon-close:hover,.el-cascader__tags .el-tag .el-icon-close{background-color: $colorText4;color: $colorText5}.el-select .el-tag__close.el-icon-close:hover,.el-badge__content--info,.el-cascader__tags .el-tag .el-icon-close:hover,.el-timeline-item__node--info{background-color: $colorText3}.el-table thead.is-group th{background: $bgTh;text-align: center;}.el-table th{background-color: $bgTh;text-align: center;font-weight: normal;}.el-table th.is-right,.el-table th.is-left{text-align: center;}.el-table td,.el-table th.is-leaf,.el-table--border th.gutter:last-of-type,.el-table--border th,.el-table__fixed-right-patch,.el-card__header,.el-calendar__header{border-bottom: 1px solid $colorBorder3}.el-table th.required > div::before{background: #FF4D51}.el-table--border,.el-table--group{border: 1px solid $colorBorder3}.el-table--border::after,.el-table--group::after,.el-table::before,.el-table__fixed-right::before,.el-table__fixed::before,.el-progress-bar__outer{background-color: $colorBorder3}.el-table--border td,.el-table--border th,.el-table__body-wrapper .el-table--border.is-scrolling-left ~ .el-table__fixed{border-right: 1px solid $colorBorder3}.el-table__fixed-footer-wrapper tbody td{border-top: 1px solid $colorBorder3;background-color: $colorTableHover;color: $colorText2}.el-table__footer-wrapper tbody td,.el-table__header-wrapper tbody td{background-color: $colorTableHover;color: $colorText2}.el-table__body-wrapper .el-table--border.is-scrolling-right ~ .el-table__fixed-right,.el-table__column-resize-proxy,.el-calendar-table tr td:first-child{border-left: 1px solid $colorBorder3}.el-table .sort-caret.ascending{border-bottom-color: $colorText4}.el-table .sort-caret.descending{border-top-color: $colorText4}.el-table .descending .sort-caret.descending{border-top-color: $colorPrimary}.el-table--striped .el-table__body tr.el-table__row--striped td{background: $colorTableStrip}.el-table-filter,.el-notification{border: 1px solid $colorBorder3;background-color: $colorBg1}.el-table-filter__list-item:hover{background-color: $colorPrimaryLight9;color: $colorPrimaryLight2}.el-date-table td.current:not(.disabled) span,.el-month-table td.end-date .cell,.el-month-table td.start-date .cell,.el-tag .el-tag__close:hover,.el-tag--plain .el-tag__close:hover,.el-transfer__button{color: $colorText5;background-color: $colorPrimary}.el-date-table td.end-date span,.el-date-table td.start-date span,.el-tabs__active-bar,.el-tree__drop-indicator,.el-slider__bar,.el-progress-bar__inner,.el-badge__content--primary,.el-timeline-item__node--primary{background-color: $colorPrimary}.el-date-table td.disabled div,.el-month-table td.disabled .cell,.el-year-table td.disabled .cell,.el-range-editor.is-disabled input{background-color: $colorTableHover;color: $colorText4}.el-date-table th{color: $colorText2;border-bottom: solid 1px $colorBorder3}.el-date-range-picker__content.is-left{border-right: 1px solid $colorBorderLight2}.el-date-range-picker__time-header,.el-date-picker__time-header{border-bottom: 1px solid $colorBorderLight2}.time-select-item.disabled,.el-input-number.is-disabled .el-input-number__decrease:hover,.el-input-number.is-disabled .el-input-number__increase:hover{color: $colorBorder2}.el-range-editor.is-disabled,.el-textarea.is-disabled .el-textarea__inner,.el-input.is-disabled .el-input__inner{background-color: $colorTableHover;border-color: $colorBorder2;color: $colorText4}.el-picker-panel{color: $colorText2;border: 1px solid $colorBorder2;background: $colorBg1}.el-picker-panel__footer{border-top: 1px solid $colorBorderLight2;background-color: $colorBg1}.el-picker-panel__shortcut.active{background-color: $colorBg2Dark9_4;color: $colorPrimary}.el-picker-panel__btn,.el-color-dropdown__btn{border: 1px solid $colorBorder1;color: $colorText1}.el-picker-panel__btn[disabled],.el-color-dropdown__btn[disabled]{color: #CCCCCC}.el-picker-panel [slot=sidebar],.el-picker-panel__sidebar{border-right: 1px solid $colorBorderLight2;background-color: $colorBg1}.el-time-spinner__item:hover:not(.disabled):not(.active),.el-steps--simple,.el-cascader__suggestion-item:focus,.el-cascader__suggestion-item:hover,.el-image__error,.el-image__placeholder,.el-cascader-node:not(.is-disabled):focus,.el-cascader-node:not(.is-disabled):hover{background: $colorTableHover}.el-time-panel__content::after,.el-time-panel__content::before{border-top: 1px solid $colorBorder2;border-bottom: 1px solid $colorBorder2}.el-time-panel__footer{border-top: 1px solid $colorBorderLight2}.el-popover{background: $colorBg1;border: 1px solid $colorBorder3;color: $colorText2}.v-modal,.el-image-viewer__mask{background: #000000}.el-message-box__title{color: $colorText1;height: 28px;box-sizing: border-box;}.el-form-item.is-error .el-input__inner,.el-form-item.is-error .el-input__inner:focus,.el-form-item.is-error .el-textarea__inner,.el-form-item.is-error .el-textarea__inner:focus,.el-message-box__input input.invalid,.el-message-box__input input.invalid:focus,.el-tag.el-tag--danger.is-hit,.el-tag--dark.el-tag--danger.is-hit,.el-tag--plain.el-tag--danger.is-hit,.el-textarea.is-exceed .el-textarea__inner,.el-input.is-exceed .el-input__inner,.el-link.el-link--danger.is-underline:hover:after,.el-link.el-link--danger:after{border-color: $colorDanger}.el-message-box__status.el-icon-success,.el-alert--success.is-light .el-alert__description,.el-notification .el-icon-success,.el-upload-list__item .el-icon-upload-success,.el-progress.is-success .el-progress__text,.el-message--success .el-message__content,.el-message .el-icon-success,.el-step__title.is-success,.el-step__description.is-success,.el-tag.el-tag--success .el-tag__close,.el-tag--plain.el-tag--success .el-tag__close,.el-link.el-link--success{color: $colorSuccess}.el-message-box__status.el-icon-warning,.el-alert--warning.is-light .el-alert__description,.el-notification .el-icon-warning,.el-progress.is-warning .el-progress__text,.el-message--warning .el-message__content,.el-message .el-icon-warning,.el-tag.el-tag--warning .el-tag__close,.el-tag--plain.el-tag--warning .el-tag__close,.el-link.el-link--warning{color: $colorWarning}.el-message-box__status.el-icon-error,.el-message-box__errormsg,.el-form-item.is-required:not(.is-no-asterisk) .el-form-item__label-wrap > .el-form-item__label:before,.el-form-item.is-required:not(.is-no-asterisk) > .el-form-item__label:before,.el-form-item.is-error .el-input__validateIcon,.el-alert--error.is-light .el-alert__description,.el-notification .el-icon-error,.el-progress.is-exception .el-progress__text,.el-message--error .el-message__content,.el-message .el-icon-error,.el-step__title.is-error,.el-step__description.is-error,.el-tag.el-tag--danger .el-tag__close,.el-tag--plain.el-tag--danger .el-tag__close,.el-textarea.is-exceed .el-input__count,.el-input.is-exceed .el-input__suffix .el-input__count,.el-link.el-link--danger{color: $colorDanger}.el-form-item__error{color: $colorDanger;background: white;z-index: 999;margin-top: -6px;margin-left: 12px;padding: 2px;border-radius: 2px;border: solid 1px $colorWarning;}.el-tabs__new-tab{border: 1px solid $colorBorder1Dark9_6;color: $colorBorder1Dark9_6}.el-tabs__nav-wrap::after,.el-slider__runway,.el-timeline-item__node{background-color: $colorBorder2}.el-tabs__item:focus.is-active.is-focus:not(:active){-webkit-box-shadow: 0 0 2px 2px $colorPrimary inset;box-shadow: 0 0 2px 2px $colorPrimary inset}.el-tabs--card > .el-tabs__header,.el-tabs--left.el-tabs--card .el-tabs__nav,.el-tabs--right.el-tabs--card .el-tabs__nav{border-bottom: 1px solid $colorBorder2}.el-tabs--card > .el-tabs__header .el-tabs__item,.el-tabs--right.el-tabs--card .el-tabs__item.is-right:first-child{border-left: 1px solid $colorBorder2}.el-tabs--card > .el-tabs__header .el-tabs__item.is-active,.el-tooltip__popper.is-light[x-placement^=bottom] .popper__arrow::after,.el-popper[x-placement^=bottom] .popper__arrow::after{border-bottom-color: $colorBg1}.el-tabs--border-card{background: $colorBg1;border: 1px solid $colorBorder1}.el-tabs--border-card > .el-tabs__header{background-color: $colorTableHover;border-bottom: 1px solid $colorBorder2}.el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active{color: $colorPrimary;background-color: $colorBg1;border-right-color: $colorBorder1;border-left-color: $colorBorder1}.el-tabs--bottom.el-tabs--border-card .el-tabs__header.is-bottom,.el-upload-dragger ~ .el-upload__files{border-top: 1px solid $colorBorder1}.el-tabs--left.el-tabs--card .el-tabs__item.is-left{border-right: 1px solid $colorBorder2;border-top: 1px solid $colorBorder2}.el-tabs--left.el-tabs--card .el-tabs__item.is-left:first-child{border-right: 1px solid $colorBorder2}.el-tabs--left.el-tabs--card .el-tabs__item.is-left.is-active{border: 1px solid $colorBorder2;border-right-color: $colorBg1}.el-tabs--left.el-tabs--border-card .el-tabs__header.is-left,.el-input-number__decrease{border-right: 1px solid $colorBorder1}.el-tabs--left.el-tabs--border-card .el-tabs__item.is-left.is-active,.el-tabs--right.el-tabs--border-card .el-tabs__item.is-right.is-active{border-color: $colorBorder1Dark9_5 transparent}.el-tabs--right.el-tabs--card .el-tabs__item.is-right{border-top: 1px solid $colorBorder2}.el-tabs--right.el-tabs--card .el-tabs__item.is-right.is-active{border: 1px solid $colorBorder2;border-left-color: $colorBg1}.el-tree{background: $colorBg1;color: $colorText2}.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content{background-color: $colorPrimaryLight9_2}.el-alert--success.is-light{background-color: $colorSuccessLight9;color: $colorSuccess}.el-alert--success.is-dark{background-color: $colorSuccess;color: $colorText5}.el-alert--info.is-light{background-color: $colorBg2Dark9_95;color: $colorText3}.el-alert--info.is-dark{background-color: $colorText3;color: $colorText5}.el-alert--warning.is-light{background-color: $colorWarningLight9;color: $colorWarning}.el-alert--warning.is-dark{background-color: $colorWarning;color: $colorText5}.el-alert--error.is-light{background-color: $colorDangerLight9;color: $colorDanger}.el-alert--error.is-dark{background-color: $colorDanger;color: $colorText5}.el-input-number__decrease,.el-input-number__increase{background: $colorTableHover;color: $colorText2}.el-input-number.is-disabled .el-input-number__decrease,.el-input-number.is-disabled .el-input-number__increase{border-color: $colorBorder2;color: $colorBorder2}.el-input-number.is-controls-right .el-input-number__increase{border-bottom: 1px solid $colorBorder1}.el-tooltip__popper[x-placement^=top] .popper__arrow,.el-tooltip__popper[x-placement^=top] .popper__arrow::after,.el-tooltip__popper.is-light[x-placement^=top] .popper__arrow{border-top-color: $colorText1}.el-tooltip__popper[x-placement^=bottom] .popper__arrow,.el-tooltip__popper[x-placement^=bottom] .popper__arrow::after,.el-tooltip__popper.is-light[x-placement^=bottom] .popper__arrow{border-bottom-color: $colorText1}.el-tooltip__popper[x-placement^=right] .popper__arrow,.el-tooltip__popper[x-placement^=right] .popper__arrow::after,.el-tooltip__popper.is-light[x-placement^=right] .popper__arrow{border-right-color: $colorText1}.el-tooltip__popper[x-placement^=left] .popper__arrow,.el-tooltip__popper[x-placement^=left] .popper__arrow::after,.el-tooltip__popper.is-light[x-placement^=left] .popper__arrow{border-left-color: $colorText1}.el-tooltip__popper.is-dark{background: $colorText1;color: $colorText5}.el-tooltip__popper.is-light{background: $colorBg1;border: 1px solid $colorText1}.el-tooltip__popper.is-light[x-placement^=top] .popper__arrow::after,.el-popper[x-placement^=top] .popper__arrow::after{border-top-color: $colorBg1}.el-tooltip__popper.is-light[x-placement^=left] .popper__arrow::after,.el-popper[x-placement^=left] .popper__arrow::after{border-left-color: $colorBg1}.el-tooltip__popper.is-light[x-placement^=right] .popper__arrow::after,.el-popper[x-placement^=right] .popper__arrow::after{border-right-color: $colorBg1}.el-slider__runway.disabled .el-slider__bar,.el-step__line,.el-carousel__indicators--outside button,.el-radio__input.is-disabled.is-checked .el-radio__inner::after{background-color: $colorText4}.el-slider__button{border: 2px solid $colorPrimary;background-color: $colorBg1}.el-slider.is-vertical.el-slider--with-input .el-slider__input .el-input-number__decrease,.el-slider.is-vertical.el-slider--with-input .el-slider__input .el-input-number__increase,.el-checkbox.is-bordered,.el-radio.is-bordered{border: 1px solid $colorBorder1}.el-loading-spinner .path{stroke: $colorPrimary}.el-upload--picture-card{background-color: transparent;border: 1px dashed $colorBorder1Dark8_8}.el-upload--picture-card:hover,.el-upload:focus{border-color: $colorPrimary;color: $colorPrimary}.el-upload-dragger{background-color: $colorBg1;border: 1px dashed $colorBorder1Light_F1}.el-upload-dragger.is-dragover{border: 2px dashed $colorPrimary}.el-upload-list--picture-card .el-upload-list__item,.el-upload-list--picture .el-upload-list__item{background-color: $colorBg1;border: 1px solid $colorBorder1Dark8_8}.el-upload-list--picture-card .el-upload-list__item-status-label,.el-upload-cover__label{background: #13CE66}.el-upload-list--picture-card .el-upload-list__item-actions{color: $colorPrimary;background-color: transparent;}.el-upload-list--picture .el-upload-list__item-status-label{background: #13CE66;-webkit-box-shadow: 0 1px 1px #CCCCCC;box-shadow: 0 1px 1px #CCCCCC}.el-upload-cover__title,.el-divider__text{background-color: $colorBg1;color: $colorText1}.el-progress.is-success .el-progress-bar__inner,.el-badge__content--success,.el-timeline-item__node--success{background-color: $colorSuccess}.el-progress.is-warning .el-progress-bar__inner,.el-badge__content--warning,.el-timeline-item__node--warning{background-color: $colorWarning}.el-progress.is-exception .el-progress-bar__inner,.el-badge__content--danger,.el-timeline-item__node--danger{background-color: $colorDanger}.el-spinner-inner .path{stroke: #ECECEC}.el-message{border-color: $colorBorder3;background-color: $colorBg2Dark9_7}.el-message--success{background-color: $colorSuccessLight9;border-color: $colorSuccessLight8}.el-message--warning{background-color: $colorWarningLight9;border-color: $colorWarningLight8}.el-message--error{background-color: $colorDangerLight9;border-color: $colorDangerLight8}.el-badge__content{background-color: $colorDanger;border: none;}.el-card{border: 1px solid $colorBorder3;background-color: $colorBg1;color: $colorText1}.el-step__head.is-process{color: $colorText1;border-color: $colorText1}.el-step__head.is-wait{color: $colorText4;border-color: $colorText4}.el-step__head.is-success{color: $colorSuccess;border-color: $colorSuccess}.el-step__head.is-error{color: $colorDanger;border-color: $colorDanger}.el-step__head.is-finish,.el-color-dropdown__btn:hover{color: $colorPrimary;border-color: $colorPrimary}.el-step.is-simple .el-step__arrow::after,.el-step.is-simple .el-step__arrow::before{background: $colorText4}.el-collapse{border-top: 1px solid $colorBorder3;border-bottom: 1px solid $colorBorder3}.el-collapse-item__header{background-color: $colorBg1;color: $colorText1;border-bottom: 1px solid $colorBorder3}.el-collapse-item__wrap{background-color: $colorBg1;border-bottom: 1px solid $colorBorder3}.el-popper[x-placement^=top] .popper__arrow{border-top-color: $colorBorder3}.el-popper[x-placement^=bottom] .popper__arrow{border-bottom-color: $colorBorder3}.el-popper[x-placement^=right] .popper__arrow{border-right-color: $colorBorder3}.el-popper[x-placement^=left] .popper__arrow,.el-checkbox-button.is-disabled:first-child .el-checkbox-button__inner{border-left-color: $colorBorder3}.el-tag{background-color: $colorPrimaryLight9;border-color: $colorPrimaryLight8;color: $colorPrimary}.el-tag.el-tag--info{background-color: $colorBg2Dark9_95;border-color: $colorBorderLight4;color: $colorText3}.el-tag.el-tag--info.is-hit,.el-tag--dark.el-tag--info.is-hit,.el-tag--plain.el-tag--info.is-hit,.el-link.el-link--info.is-underline:hover:after,.el-link.el-link--info:after{border-color: $colorText3}.el-tag.el-tag--info .el-tag__close:hover,.el-tag--plain.el-tag--info .el-tag__close:hover{color: $colorText5;background-color: $colorText3}.el-tag.el-tag--success{background-color: $colorSuccessLight9;border-color: $colorSuccessLight8;color: $colorSuccess}.el-tag.el-tag--success.is-hit,.el-tag--dark.el-tag--success.is-hit,.el-tag--plain.el-tag--success.is-hit,.el-link.el-link--success.is-underline:hover:after,.el-link.el-link--success:after{border-color: $colorSuccess}.el-tag.el-tag--success .el-tag__close:hover,.el-tag--plain.el-tag--success .el-tag__close:hover{color: $colorText5;background-color: $colorSuccess}.el-tag.el-tag--warning{background-color: $colorWarningLight9;border-color: $colorWarningLight8;color: $colorWarning}.el-tag.el-tag--warning.is-hit,.el-tag--dark.el-tag--warning.is-hit,.el-tag--plain.el-tag--warning.is-hit,.el-link.el-link--warning.is-underline:hover:after,.el-link.el-link--warning:after{border-color: $colorWarning}.el-tag.el-tag--warning .el-tag__close:hover,.el-tag--plain.el-tag--warning .el-tag__close:hover{color: $colorText5;background-color: $colorWarning}.el-tag.el-tag--danger{background-color: $colorDangerLight9;border-color: $colorDangerLight8;color: $colorDanger}.el-tag.el-tag--danger .el-tag__close:hover,.el-tag--plain.el-tag--danger .el-tag__close:hover{color: $colorText5;background-color: $colorDanger}.el-tag--dark{background-color: $colorPrimary;border-color: $colorPrimary;color: $colorText5}.el-tag--dark .el-tag__close:hover{color: $colorText5;background-color: $colorPrimaryLight2}.el-tag--dark.el-tag--info{background-color: $colorText3;border-color: $colorText3;color: $colorText5}.el-tag--dark.el-tag--info .el-tag__close:hover{color: $colorText5;background-color: $colorText3Light2_25}.el-tag--dark.el-tag--success{background-color: $colorSuccess;border-color: $colorSuccess;color: $colorText5}.el-tag--dark.el-tag--success .el-tag__close:hover{color: $colorText5;background-color: $colorSuccessLight2}.el-tag--dark.el-tag--warning{background-color: $colorWarning;border-color: $colorWarning;color: $colorText5}.el-tag--dark.el-tag--warning .el-tag__close:hover{color: $colorText5;background-color: $colorWarningLight2}.el-tag--dark.el-tag--danger{background-color: $colorDanger;border-color: $colorDanger;color: $colorText5}.el-tag--dark.el-tag--danger .el-tag__close:hover{color: $colorText5;background-color: $colorDangerLight2}.el-tag--plain{background-color: $colorBg1;border-color: $colorPrimaryLight6;color: $colorPrimary}.el-tag--plain.el-tag--info{background-color: $colorBg1;border-color: $colorBorder1Dark9_6;color: $colorText3}.el-tag--plain.el-tag--success{background-color: $colorBg1;border-color: $colorSuccessLight5_3;color: $colorSuccess}.el-tag--plain.el-tag--warning{background-color: $colorBg1;border-color: $colorWarningLight6;color: $colorWarning}.el-tag--plain.el-tag--danger{background-color: $colorBg1;border-color: $colorDangerLight4_15;color: $colorDanger}.el-cascader__dropdown{background: $colorBg1;border: 1px solid $colorBorder2}.el-cascader__tags .el-tag{background: $colorBg2Dark9_8}.el-color-predefine__color-selector.selected{-webkit-box-shadow: 0 0 3px 2px $colorPrimary;box-shadow: 0 0 3px 2px $colorPrimary}.el-color-hue-slider__thumb,.el-color-alpha-slider__thumb{background: $colorBg1;border: 1px solid $colorBorderLight6}.el-color-svpanel__cursor > div{-webkit-box-shadow: 0 0 0 1.5px $colorBg1, inset 0 0 1px 1px rgba(0, 0, 0, .3), 0 0 1px 2px rgba(0, 0, 0, .4);box-shadow: 0 0 0 1.5px $colorBg1, inset 0 0 1px 1px rgba(0, 0, 0, .3), 0 0 1px 2px rgba(0, 0, 0, .4)}.el-color-dropdown__value,.el-calendar__title{color: #000000}.el-color-picker__trigger{border: 1px solid $colorBorderLight3}.el-color-picker__color{border: 1px solid $colorText3}.el-textarea__inner{color: $colorText2;background-color: $colorBg1;border: 1px solid $colorBorder1}.el-textarea .el-input__count{color: $colorText3;background: $colorBg1}.el-input::-webkit-scrollbar-thumb{background: $colorBorder1Dark8_2}.el-input__inner,div.vue-treeselect__control{background-color: $colorBg1;border: 1px solid $colorBorder1;color: $colorText2}.el-date-editor .el-range-input{background-color: $colorBg1;color: $colorText2}.el-input-group__append,.el-input-group__prepend{background-color: $colorTableHover;color: $colorText3;border: 1px solid $colorBorder1}.el-transfer__button.is-disabled,.el-transfer__button.is-disabled:hover{border: 1px solid $colorBorder1;background-color: $colorTableHover;color: $colorText4}.el-transfer-panel{border: 1px solid $colorBorder3;background: $colorBg1}.el-transfer-panel .el-transfer-panel__header{background: $colorTableHover;border-bottom: 1px solid $colorBorder3;color: #000000}.el-transfer-panel .el-transfer-panel__footer{background: $colorBg1;border-top: 1px solid $colorBorder3}.el-timeline-item__tail{border-left: 2px solid $colorBorder2}.el-link.is-underline:hover:after{border-bottom: 1px solid $colorPrimary}.el-link.el-link--primary:hover{color: $colorPrimaryLight2}.el-link.el-link--primary.is-disabled{color: $colorPrimaryLight5}.el-link.el-link--danger:hover{color: $colorDangerLight2}.el-link.el-link--danger.is-disabled{color: $colorDangerLight5}.el-link.el-link--success:hover{color: $colorSuccessLight2}.el-link.el-link--success.is-disabled{color: $colorSuccessLight4_7}.el-link.el-link--warning:hover{color: $colorWarningLight2}.el-link.el-link--warning.is-disabled{color: $colorWarningLight5}.el-link.el-link--info:hover{color: $colorText3Light2_25}.el-link.el-link--info.is-disabled{color: $colorText3Light5_2}.el-divider,.el-page-header__left::after{background-color: $colorBorder1}.el-image-viewer__actions{background-color: $colorText2;border-color: $colorBg1}.el-image-viewer__next,.el-image-viewer__prev{color: $colorText5;background-color: $colorText2;border-color: $colorBg1}.el-button{background: $colorBg1;border: 1px solid $colorBorder1;color: $colorText1;padding: 6px 15px 6px 15px;line-height: 14px;}.el-button:focus,.el-button:hover{color: $colorPrimary;border-color: $colorPrimary;background-color: $colorBg1}.el-button:active,.el-button.is-active,.el-button.is-plain:active{color: $colorPrimaryDark;border-color: $colorPrimaryDark}.el-button.is-plain:focus,.el-button.is-plain:hover{background: $colorBg1;border-color: $colorPrimary;color: $colorPrimary}.el-button.is-disabled.is-plain,.el-button.is-disabled.is-plain:focus,.el-button.is-disabled.is-plain:hover{background-color: $colorBg1;border-color: $colorBorder3;color: $colorText4}.el-button--primary{color: $colorText5;background-color: $colorPrimary;border-color: $colorPrimary}.el-button--primary:focus,.el-button--primary:hover{background: $colorPrimaryLight2;border-color: $colorPrimaryLight2;color: $colorText5}.el-button--primary.is-active,.el-button--primary:active,.el-button--primary.is-plain:active{background: $colorPrimaryDark;border-color: $colorPrimaryDark;color: $colorText5}.el-button--primary.is-disabled,.el-button--primary.is-disabled:active,.el-button--primary.is-disabled:focus,.el-button--primary.is-disabled:hover{color: $colorText5;background-color: $colorPrimaryLight5;border-color: $colorPrimaryLight5}.el-button--primary.is-plain{color: $colorPrimary;background: $colorPrimaryLight9;border-color: $colorPrimaryLight6}.el-button--primary.is-plain:focus,.el-button--primary.is-plain:hover{background: $colorPrimary;border-color: $colorPrimary;color: $colorText5}.el-button--primary.is-plain.is-disabled,.el-button--primary.is-plain.is-disabled:active,.el-button--primary.is-plain.is-disabled:focus,.el-button--primary.is-plain.is-disabled:hover{color: $colorPrimaryLight4;background-color: $colorPrimaryLight9;border-color: $colorPrimaryLight8}.el-button--success{color: $colorText5;background-color: $colorSuccess;border-color: $colorSuccess}.el-button--success:focus,.el-button--success:hover{background: $colorSuccessLight2;border-color: $colorSuccessLight2;color: $colorText5}.el-button--success.is-active,.el-button--success:active,.el-button--success.is-plain:active{background: $colorSuccessDark9;border-color: $colorSuccessDark9;color: $colorText5}.el-button--success.is-disabled,.el-button--success.is-disabled:active,.el-button--success.is-disabled:focus,.el-button--success.is-disabled:hover{color: $colorText5;background-color: $colorSuccessLight4_7;border-color: $colorSuccessLight4_7}.el-button--success.is-plain{color: $colorSuccess;background: $colorSuccessLight9;border-color: $colorSuccessLight5_3}.el-button--success.is-plain:focus,.el-button--success.is-plain:hover{background: $colorSuccess;border-color: $colorSuccess;color: $colorText5}.el-button--success.is-plain.is-disabled,.el-button--success.is-plain.is-disabled:active,.el-button--success.is-plain.is-disabled:focus,.el-button--success.is-plain.is-disabled:hover{color: $colorSuccessLight4;background-color: $colorSuccessLight9;border-color: $colorSuccessLight8}.el-button--warning{color: $colorText5;background-color: $colorWarning;border-color: $colorWarning}.el-button--warning:focus,.el-button--warning:hover{background: $colorWarningLight2;border-color: $colorWarningLight2;color: $colorText5}.el-button--warning.is-active,.el-button--warning:active,.el-button--warning.is-plain:active{background: $colorWarningDark9;border-color: $colorWarningDark9;color: $colorText5}.el-button--warning.is-disabled,.el-button--warning.is-disabled:active,.el-button--warning.is-disabled:focus,.el-button--warning.is-disabled:hover{color: $colorText5;background-color: $colorWarningLight5;border-color: $colorWarningLight5}.el-button--warning.is-plain{color: $colorWarning;background: $colorWarningLight9;border-color: $colorWarningLight6}.el-button--warning.is-plain:focus,.el-button--warning.is-plain:hover{background: $colorWarning;border-color: $colorWarning;color: $colorText5}.el-button--warning.is-plain.is-disabled,.el-button--warning.is-plain.is-disabled:active,.el-button--warning.is-plain.is-disabled:focus,.el-button--warning.is-plain.is-disabled:hover{color: $colorWarningLight4;background-color: $colorWarningLight9;border-color: $colorWarningLight8}.el-button--danger{color: $colorText5;background-color: $colorDanger;border-color: $colorDanger}.el-button--danger:focus,.el-button--danger:hover{background: $colorDangerLight2;border-color: $colorDangerLight2;color: $colorText5}.el-button--danger.is-active,.el-button--danger:active,.el-button--danger.is-plain:active{background: $colorDangerLight9;border-color: $colorDangerLight9;color: $colorText5}.el-button--danger.is-disabled,.el-button--danger.is-disabled:active,.el-button--danger.is-disabled:focus,.el-button--danger.is-disabled:hover{color: $colorText5;background-color: $colorDangerLight5;border-color: $colorDangerLight5}.el-button--danger.is-plain{color: $colorDanger;background: $colorDangerLight9;border-color: $colorDangerLight4_15}.el-button--danger.is-plain:focus,.el-button--danger.is-plain:hover{background: $colorDanger;border-color: $colorDanger;color: $colorText5}.el-button--danger.is-plain.is-disabled,.el-button--danger.is-plain.is-disabled:active,.el-button--danger.is-plain.is-disabled:focus,.el-button--danger.is-plain.is-disabled:hover{color: $colorDangerLight4;background-color: $colorDangerLight9;border-color: $colorDangerLight8}.el-button--info{color: $colorText5;background-color: $colorInfo;border-color: $colorInfo}.el-button--info:focus,.el-button--info:hover{background: $colorText3Light2_25;border-color: $colorText3Light2_25;color: $colorText5}.el-button--info.is-active,.el-button--info:active,.el-button--info.is-plain:active{background: $colorText2Light8_75;border-color: $colorText2Light8_75;color: $colorText5}.el-button--info.is-disabled,.el-button--info.is-disabled:active,.el-button--info.is-disabled:focus,.el-button--info.is-disabled:hover{color: $colorText5;background-color: $colorText3Light5_2;border-color: $colorText3Light5_2}.el-button--info.is-plain{color: $colorText3;background: $colorBg2Dark9_95;border-color: $colorBorder1Dark9_6}.el-button--info.is-plain:focus,.el-button--info.is-plain:hover{background: $colorText3;border-color: $colorText3;color: $colorText5}.el-button--info.is-plain.is-disabled,.el-button--info.is-plain.is-disabled:active,.el-button--info.is-plain.is-disabled:focus,.el-button--info.is-plain.is-disabled:hover{color: $colorText3Light3_96;background-color: $colorBg2Dark9_95;border-color: $colorBorderLight4}.el-button--text{color: $colorPrimary;background: transparent;border: none;padding: 0px 4px;}.el-button--text:focus,.el-button--text:hover{color: $colorPrimaryLight2;background: transparent;}.el-button--text:active{color: $colorPrimaryDark;background: transparent;}.el-calendar-table td{border-bottom: 1px solid $colorBorder3;border-right: 1px solid $colorBorder3}.el-calendar-table td.is-selected,.el-calendar-table .el-calendar-day:hover{background-color: $colorPrimaryLight9_3}.el-checkbox.is-bordered.is-disabled,.el-radio.is-bordered.is-disabled{border-color: $colorBorder3}.el-checkbox__input.is-disabled .el-checkbox__inner{background-color: $colorBg2Dark9_7;border-color: $colorBorder1}.el-checkbox__input.is-disabled.is-checked .el-checkbox__inner,.el-checkbox__input.is-disabled.is-indeterminate .el-checkbox__inner{background-color: $colorBorder1Dark11;border-color: $colorBorder1}.el-checkbox__input.is-disabled.is-indeterminate .el-checkbox__inner::before{background-color: $colorText4;border-color: $colorText4}.el-checkbox__input.is-checked .el-checkbox__inner,.el-checkbox__input.is-indeterminate .el-checkbox__inner{background-color: $colorPrimary;border-color: $colorPrimary}.el-checkbox__inner,.el-radio__inner{border: 1px solid $colorBorder1;background-color: $colorBg1}.el-checkbox__inner::after{border-color: $colorBg1}.el-checkbox-button.is-checked .el-checkbox-button__inner{color: $colorText5;background-color: $colorPrimary;border-color: $colorPrimary;-webkit-box-shadow: -1px 0 0 0 $colorPrimaryLight4;box-shadow: -1px 0 0 0 $colorPrimaryLight4}.el-checkbox-button.is-checked:first-child .el-checkbox-button__inner{border-left-color: $colorPrimary}.el-radio__input.is-disabled .el-radio__inner,.el-radio__input.is-disabled.is-checked .el-radio__inner{background-color: $colorTableHover;border-color: $colorBorder2}.el-radio__input.is-checked .el-radio__inner{border-color: $colorPrimary;background: $colorPrimary}.el-cascader-menu{color: $colorText2;border-right: solid 1px $colorBorder2}.el-avatar{color: $colorText5;background: $colorText4}.el-drawer__header{color: $colorText2Light1_2}.el-table--mini th,.el-table--mini td{padding: 3px 0px;}.theme-pannel{background: $bg1;color: $colorText2;border: solid 1px $border3;box-sizing: border-box;}.theme-pannel__title{background: $bgTitle;border-bottom: solid 1px $border3;line-height: 27px;padding: 0px 6px;box-sizing: border-box;}.el-message-box__title{line-height: 28px;padding: 0px 6px;color: $colorText2;border-bottom: solid 1px $border3;}.el-message-box__header{padding: 0;background: $bgTitle;}.el-message-box__headerbtn{top: 4px;right: 5px;font-size: 12px;}.el-message-box__content{padding: 10px 20px;}.el-message-box__btns{padding: 15px 15px;}.el-message-box__close.el-icon-close{background: $colorPrimary;border-radius: 50%;font-size: 12px;padding: 4px;transform: scale(0.8);color: $colorText5;}.el-message-box{padding-bottom: 0px;}.el-transfer-panel .el-transfer-panel__header .el-checkbox .el-checkbox__label{font-size: 14px;}div.overview-AutoDialog .el-dialog__header{color: $colorText2;background: $bgTitle;padding: 5px 0 0 10px;line-height: 28px;box-sizing: border-box;height: 38px;font-size: 13px;border-bottom: solid 1px $border3;}div.overview-AutoDialog .fullscreen-btn,div.overview-AutoDialog .el-dialog__headerbtn{background: $colorPrimary;border-radius: 50%;font-size: 12px;padding: 4px;transform: scale(0.8);top: 7px;right: 28px;color: $colorText5;opacity: 1;}div.overview-AutoDialog .el-dialog__headerbtn{right: 5px;box-sizing: border-box;width: 20px;height: 20px;line-height: 13px;}.el-dialog{border: solid 1px $border3;background-color: $colorBg1;}.theme-table-form{width: 100%;table-layout: fixed;border-collapse: collapse;font-size: 13px;line-height: 28px;color: $colorText2;}.theme-table-form .theme-table-form__label{text-align: right;background-color: $bgTitle;padding: 4px 10px;}.theme-table-form .theme-table-form__input{padding: 4px;}.theme-table,.theme-table th,.theme-table td,.theme-table-form,.theme-table-form th,.theme-table-form td{border: solid 1px $border3;}.theme-table-form .el-form-item--mini.el-form-item,.theme-table-form .el-form-item--small.el-form-item{margin: 0px;min-height: 28px;}.theme-table-form .el-form-item__label{display: none;}html,body,.el-message-box__title,.el-message-box__message,.el-tree-node__label,.el-checkbox__label,.el-dropdown,.el-dropdown-menu--mini .el-dropdown-menu__item,.el-cascader--mini,.el-input__inner,.el-button--mini,.el-button--small,.el-table--mini th,.el-table--mini td{font-size: 13px;}textarea,.el-textarea__inner{resize: none;}::-webkit-scrollbar{width: 9px;height: 9px;}::-webkit-scrollbar-track{width: 6px;background-color: $bg2;border-radius: 3px;}::-webkit-scrollbar-thumb{background-color: $border3;background-clip: padding-box;min-height: 28px;border-radius: 4px;}::-webkit-scrollbar-thumb:hover{background-color: $border3;}.el-loading-mask{background-color: rgba(255, 255, 255, 0.1);}input:-webkit-autofill,textarea:-webkit-autofill,select:-webkit-autofill{-webkit-text-fill-color: $colorText2 !important;}.ql-snow .ql-stroke{stroke: $colorText2 !important;}.ql-snow.ql-toolbar button:hover,.ql-snow .ql-toolbar button:hover,.ql-snow.ql-toolbar button:focus,.ql-snow .ql-toolbar button:focus,.ql-snow.ql-toolbar button.ql-active,.ql-snow .ql-toolbar button.ql-active,.ql-snow.ql-toolbar .ql-picker-label:hover,.ql-snow .ql-toolbar .ql-picker-label:hover,.ql-snow.ql-toolbar .ql-picker-label.ql-active,.ql-snow .ql-toolbar .ql-picker-label.ql-active,.ql-snow.ql-toolbar .ql-picker-item:hover,.ql-snow .ql-toolbar .ql-picker-item:hover,.ql-snow.ql-toolbar .ql-picker-item.ql-selected,.ql-snow .ql-toolbar .ql-picker-item.ql-selected{color: $colorText2 !important;}div.ql-toolbar.ql-snow .ql-picker.ql-expanded .ql-picker-label{border-color: $colorBorder3;color: $colorText2;}.ql-snow span.ql-picker-options{background: $colorTableHover;color: $colorText2;}.ql-toolbar.ql-snow .ql-picker.ql-expanded span.ql-picker-options{border: solid 1px $colorBorder3;}.ql-snow.ql-toolbar .ql-picker-label:hover{color: $colorPrimary !important;;background-color: $colorBg1;}div.ql-snow .ql-tooltip{color: $colorPrimary;background-color: $colorBg1;border: 1px solid $colorBorder3;box-shadow: none;}.el-calendar-table td.is-selected,.el-calendar-table .el-calendar-day:hover{background-color: $colorTableHover !important;}.el-color-dropdown__value,.el-calendar__title{color: $colorPrimary !important;}"
  //auto-code
};
