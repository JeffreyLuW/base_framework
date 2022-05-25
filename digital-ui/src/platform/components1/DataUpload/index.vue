<template>
  <div class="data-upload">
    <el-upload
      :class="{noShowAdd:!editable||(limit>0&&fileList.length>=limit)}"
      action="#"
      :auto-upload="false"
      :show-file-list="false"
      :http-request="httpRequest"
      :on-change="onFileChange"
    >
      <template slot="tip">
        <span class="tip-span" v-for="(item,index) in fileList" :key="index" :title="item.name">
          <span class="yulan-span">
            <el-image class="yulan-image" v-if="item.isImage" :src="item.url" fit="cover"></el-image>
            <span v-else>{{item.suffix}}</span>
          </span>
          <span class="file-name">{{item.name}}</span>
          <i v-if="!!item.raw" class="el-icon-loading icon-loading"></i>
          <span class="op-span">
            <i
              v-if="item.isImage"
              class="op-btn el-icon-zoom-in"
              @click="onClickImageView(item,index)"
            ></i>
            <i
              v-else-if="item.canBrowserView&&canOpenWindow"
              class="op-btn el-icon-view"
              @click="onClickBrowserView(item,index)"
            ></i>

            <!-- 本地刚刚选择的 不需要下载 -->
            <i v-if="!item.raw&&!item.hasBlobUrl" class="op-btn el-icon-download" @click="onClickSave(item,index)"></i>
            <i v-if="editable" class="op-btn el-icon-delete" @click="onClickDel(item,index)"></i>
          </span>
        </span>
      </template>
      <template slot="default">
        <i v-if="editable" class="el-icon-plus data-upload-icon"></i>
      </template>
    </el-upload>
    <!-- 图片预览 -->
    <viewer @inited="initedViewer" v-show="false" :images="imgs">
      <template slot-scope="scope">
        <img v-for="(item,index) in scope.images" :title="item.name" :src="item.url" :key="index"/>
      </template>
    </viewer>
  </div>
</template>
<script>
  import vmodelTransformer from './upload_vmodel_transformer.js';

  const imgSuffix = ["ico", "png", "jpg", "jpeg", "svg", "gif"];
  const browserView = ["txt", "pdf", "html"];
  // import emitter from 'element-ui/src/mixins/emitter.js';

  //用于文件上传 预览等操作
  //可以使用 vmodelTransformer 转换输入输出的值。默认输入输出都是批次id,需要手动调用 upload() 方法
  export default {
    name: "DataUpload",
    // mixins: [emitter],
    props: {
      value: {
        type: String | Array, // v-model即可 {name,url} 或者为附件的id,后者需要vmodelTransformer配合使用，自行转换value值。
        default: () => []
      },
      limit: {
        type: Number, //限制的数量 0不限制
        default: 0
      },
      editable: {
        type: Boolean,
        default: true
      },
      beforeAdd: {
        type: Function, //(file,fileList) 可以自定义各种类型的判断拦截 return false即可。
        default: null
      },
      vmodelTransformer: {
        type: Object,//用于转换 value input 属性，简化转换操作。 {setValue(value,next,dataUpload),getValue(value, next, dataUpload,rightNow)}.
        default: () => vmodelTransformer
      },
      opt: {
        type: Object,//element-ui组件额外的设置 比如 accept
      },
      validateEvent: {
        type: Boolean,
        default: true
      },
    },
    data() {
      //支持多种文件  { name: "", url: null }
      let browserType = this.$util.browserType();
      //Chrome 78.0.3904.97   Edge 18.17763 IE 11.0  Chrome 69.0.3497.100
      return {
        fileList: [],//name url
        imgs: [],
        tmpValue: null,//parent直接赋予的值
        tmpFileList: [],//parent赋予的文件列表
        canOpenWindow: browserType.startsWith("Chrome "),
        userEditedFile: false,//优化上传逻辑，用户未操作时，不需要调用上传接口。
      };
    },
    created() {
    },
    watch: {
      value: {
        immediate: true,
        handler(value) {
          //临时存储value
          this.tmpValue = value;
          if (this.vmodelTransformer) {
            this.vmodelTransformer.setValue(value, this.setValue, this);
            return;
          }
          this.setValue(value);
        }
      }
    },
    methods: {
      httpRequest() {
        //默认不自动上传，自定义transformer处理。
      },
      setValue(value) {
        if (!value) {
          this.fileList = [];
        } else {
          this.fileList = value.map(f => {
            return this.transformFile(f);
          });
        }
        this.tmpFileList = [...this.fileList];
      },
      //使用 transformer 上传文件,成功后 更新值。
      upload() {
        let tmpLen = this.tmpFileList ? this.tmpFileList.length : 0;
        let filesLen = this.fileList ? this.fileList.length : 0;
        let same = true;
        if (tmpLen != filesLen) {
          same = false;
        } else if (tmpLen === 0 && filesLen === 0) {
          same = true;
        } else {
          for (let i = 0; i < tmpLen; i++) {
            if (this.tmpFileList[i] !== this.fileList[i]) {
              same = false;
              break;
            }
          }
        }
        if (!same && this.vmodelTransformer) {
          return new Promise((resolve, reject) => {
            this.emitInputValue(this.fileList, (ok, rs) => {
              if (ok) {
                resolve(rs);
              } else {
                reject(rs);
              }
            });
          });
        }
        return Promise.resolve();
      },
      //{"status", "name":"xx.jpg", "size", "percentage", "uid", "raw":File, "response"}
      onFileChange(file, fileList) {
        //超过文件数量限制 不操作
        if (this.limit > 0 && this.fileList.length >= this.limit) {
          return;
        }
        file = this.transformFile(file);
        let goon = true;
        if (this.beforeAdd) {
          goon = this.beforeAdd(file, this.fileList);
        }
        if (!goon) {
          return;
        }
        //清空 fileList
        fileList.splice(0, fileList.length);
        let arr = [...this.fileList, file];
        this.emitInputValue(arr);
      },
      //抛出 value
      emitInputValue(value, cb) {
        let oldFileList = this.fileList;
        this.fileList = value;
        if (this.vmodelTransformer) {
          this.vmodelTransformer.getValue(value, (newValue, ok, error) => {
            if (ok) {
              this.$emit("change", newValue, this.value);
              this.$emit("input", newValue);
              if (this.validateEvent) {
                this.dispatch&&this.dispatch('ElFormItem', 'el.form.change', [newValue]);
              }
              cb && cb(ok, newValue);
            } else {
              this.fileList = oldFileList;
              cb && cb(ok, error);
            }
          }, this);
        } else {
          this.$emit("change", value, this.value);
          this.$emit("input", value);
          if (this.validateEvent) {
            this.dispatch&&this.dispatch('ElFormItem', 'el.form.change', [value]);
          }
          cb && cb(true, value);
        }
      },
      dispatch(componentName, eventName, params) {
        var parent = this.$parent || this.$root;
        var name = parent.$options.componentName;

        while (parent && (!name || name !== componentName)) {
          parent = parent.$parent;

          if (parent) {
            name = parent.$options.componentName;
          }
        }
        if (parent) {
          parent.$emit.apply(parent, [eventName].concat(params));
        }
      },
      //处理file
      transformFile(file) {
        if (!file || file._DataUpload) return file;
        file = {...file};

        file._DataUpload = true;
        let pIndex = file.name.lastIndexOf(".");
        if (pIndex > -1) {
          file.suffix = file.name.substring(pIndex + 1);
          file.isImage = imgSuffix.indexOf(file.suffix) > -1;
          file.canBrowserView = browserView.indexOf(file.suffix) > -1;
        }

        if (!file.url && file.raw) {
          file.url = URL.createObjectURL(file.raw);
        }
        return file;
      },
      //点击删除
      onClickDel(item, index) {
        this.confirm("确定删除该文件?", () => {
          this.$emit("remove", item, index);
          let arr = [...this.fileList];
          arr.splice(index, 1);
          this.emitInputValue(arr);
        });
      },
      initedViewer(viewer) {
        this.$viewer = viewer;
      },
      //点击图片预览
      onClickImageView(item, index) {
        let currIndex = 0;
        let imgs = [];
        this.fileList.forEach((_item, _index) => {
          if (_item.isImage) {
            imgs.push(_item);
            if (_item == item) {
              currIndex = imgs.length - 1;
            }
          }
        });
        this.imgs = imgs;
        requestAnimationFrame(() => {
          this.$viewer.view(currIndex);
        });
      },
      //点击浏览器中浏览
      onClickBrowserView(item, index) {
        let url = item.url;
        if (url) window.open(url, "", "width = 1200,height = 600");
        // if (url) window.open(url, "_blank");
      },
      //下载文件
      onClickSave(item, index) {
        this.downloadResource(item.path);
      }
    }
  };
</script>
<style lang="scss">
  //上传头像的样式
  .data-upload {
    padding-left: 0rem;
    padding-top: 0rem;

    .noShowAdd .el-upload {
      display: none;
    }

    .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      margin-right: 0.5rem;
      margin-bottom: 0.5rem;
      display: inline-flex;
      box-sizing: border-box;
      width: 128px;
      height: 128px;
    }

    .el-upload:hover {
      border-color: #409eff;
    }

    .data-upload-icon {
      font-size: 64px;
      color: #8c939d;
      width: 128px;
      height: 128px;
      line-height: 128px !important;
      text-align: center;
    }

    .avatar {
      width: 128px;
      height: 128px;
      display: block;
    }

    .viewer {
      display: none;
    }

    .tip-span {
      width: 128px;
      height: 128px;
      display: inline-block;
      vertical-align: top;
      margin-right: 0.5rem;
      margin-bottom: 0.5rem;
      position: relative;
      overflow: hidden;
      box-sizing: border-box;
      border: solid 1px #d9d9d9;
      border-radius: 0.5rem;

      .yulan-image {
        width: 100%;
        height: 100%;
        display: block;
      }

      .yulan-span,
      .op-span {
        position: absolute;
        left: 0;
        width: 100%;
        top: 0;
        height: 100%;
        display: block;
      }

      .file-name {
        position: absolute;
        left: 0;
        bottom: 0;
        height: 2rem;
        line-height: 2rem;
        width: 100%;
        background: rgba(0, 0, 0, 0.5);
        color: white;
        text-align: center;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .yulan-span {
        text-align: center;
        font-size: 4rem;
        line-height: 108px;
      }

      .op-span {
        opacity: 0;
        text-align: center;
        line-height: 128px;
        background: rgba(0, 0, 0, 0.5);
        color: white;
        transition: all 0.35s;
        letter-spacing: 1rem;
        font-size: 1.4rem;

        .op-btn {
          cursor: pointer;
        }
      }

      &:hover .op-span {
        opacity: 1;
      }
    }

    .icon-loading {
      position: absolute;
      right: 4px;
      bottom: 22px;
      color: white;
    }
  }
</style>
