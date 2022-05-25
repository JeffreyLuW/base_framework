<template>
  <div class="ThemeUpload" :class="rootClass">
    <el-upload
      action="#"
      :class="{
        noShowAdd:
          !disabled || ($attrs.limit > 0 && innerFileList.length >= $attrs.limit),
      }"
      v-bind="$attrs"
      :list-type="showType"
      :on-change="handleChange"
      :file-list="innerFileList"
      :auto-upload="false"
      :accept="accept"
    >
      <i slot="default" class="el-icon-plus"></i>
      <div slot="file" slot-scope="{ file }">
        <img
          crossorigin="Anonymous"
          v-if="file._isImg && showType == 'picture-card'"
          class="el-upload-list__item-thumbnail"
          :src="file.path"
          alt=""
        />
        <div class="file-suffix" v-else>
          {{ file._suffix }}
        </div>
        <span class="file-name theme-bg-control">
          {{ file.name }}
        </span>

        <span class="el-upload-list__item-actions">
          <span class="mask theme-bg-control match"></span>
          <div class="actions-wrapper match">
            <!--view-->
            <span
              v-if="file._browserCanView"
              class="el-upload-list__item-preview"
              @click="handlePictureCardPreview(file)"
            >
              <i class="el-icon-view"></i>
            </span>
            <!--download-->
            <span
              v-if="!file.raw"
              class="el-upload-list__item-delete"
              @click="handleDownload(file)"
            >
              <i class="el-icon-download"></i>
            </span>
            <!--delete-->
            <span
              v-if="disabled"
              class="el-upload-list__item-delete"
              @click="handleRemove(file)"
            >
              <i class="el-icon-delete"></i>
            </span>
          </div>
        </span>
      </div>
    </el-upload>
    <el-dialog
      class="ThemeUpload-dialog"
      :append-to-body="true"
      :visible.sync="dialogVisible"
      ><img width="100%" :src="dialogImageUrl" alt="" />
    </el-dialog>

    <!-- 图片预览 -->
    <viewer @inited="initedViewer" v-show="false" :images="imgs">
      <template slot-scope="scope">
        <img
          crossorigin="Anonymous"
          v-for="(item, index) in scope.images"
          :title="item.name"
          :src="item.path"
          :key="index"
        />
      </template>
    </viewer>
  </div>
</template>

<script>
import { uploadFile } from "@/platform/api/common/common.js";
import simple_util from "@/platform/utils/simple_util.js";
let fileId = 1;
//用于文件上传
export default {
  name: "ThemeUpload",
  props: {
    showType: {
      type: String,
      default: "picture-card",
    },
    value: {
      type: Array, // [{name,url}],
      default: () => {
        return [];
      },
    },
    disabled: {
      type: Boolean,
      default: true,
    },
    fileSize: {
      type: Number,
      default: 0,
    },
    accept: {
      type: String,
      default: "*",
    },
  },
  data() {
    return {
      dialogImageUrl: "",
      dialogVisible: false,
      imgs: [],
      //disabled: false,
      innerFileList: [], // [{url,name,_suffix,_rawData,_isImg,_browserCanView,_canDownLoad}]
    };
  },
  created() {
    window.upload = this;
  },
  watch: {
    value: {
      immediate: true,
      handler(fileList) {
        this.resetFileList(fileList);
        //修改原value值。
        this.$emit("input", this.innerFileList);
      },
    },
  },
  computed: {
    rootClass() {
      return {
        defaultColor: !this.$theme,
      };
    },
  },
  methods: {
    initedViewer(viewer) {
      this.$viewer = viewer;
    },
    resetFileList(fileList) {
      if (fileList === this.innerFileList) {
        return;
      }
      if (!fileList) {
        this.innerFileList = [];
      } else {
        this.innerFileList = fileList.map((item) => {
          return this.genExtraInfoFile(item);
        });
      }
    },
    genExtraInfoFile(item) {
      //有_id则认为已处理过
      //if (item._id) return item;
      let suffix = this.getSuffix(item.name || item.fileName);
      return {
        ...item,
        name: item.name || item.fileName,
        _id: item.id ? item.id : item.uid ? "uid_" + item.uid : "" + ++fileId,
        _rawData: item,
        path: simple_util.fixFileUrl(item.path),
        _suffix: suffix,
        _isImg: this.isImgFile(suffix),
        _isPDF: this.isPDF(suffix),
        _browserCanView: this.browserCanView(suffix),
        _canDownLoad: item.url && item.url.startsWith("http"),
      };
    },
    isImgFile(suffix) {
      return ["jpg", "gif", "ico", "svg", "jpeg"].indexOf(suffix) > -1;
    },
    isPDF(suffix) {
      return ["pdf"].indexOf(suffix) > -1;
    },
    browserCanView(suffix) {
      if (!suffix) return false;
      suffix = suffix.toLowerCase();
      return (
        ["jpg", "jpeg", "png", "gif", "ico", "svg", "pdf", "txt", "html"].indexOf(
          suffix
        ) > -1
      );
    },
    getSuffix(name) {
      if (!name) return "";
      let lastDotIndex = name.lastIndexOf(".");
      if (lastDotIndex === -1) {
        return "无格式";
      }
      let suffix = name.substring(lastDotIndex + 1).toLowerCase(); // jpg gif png ico svg
      return suffix;
    },
    //文件变化时调用,比如添加
    handleChange(file, fileList) {
      if (this.fileSize != 0 && file.size / 1024 > this.fileSize) {
        this.msgInfo(`请上传文件大于${parseFloat(file.size / 1024)}M`);
        return;
      }
      this.$emit("changeFile", "change");
      let oldIdMap = {};
      this.innerFileList.forEach((item) => {
        oldIdMap[item._id] = true;
      });
      let currIdMap = {}; //现存的_id
      fileList.forEach((item) => {
        if (item._id) {
          currIdMap[item._id] = true;
        } else {
          //新添加的文件
          let newFileInfo = this.genExtraInfoFile(item);
          currIdMap[newFileInfo._id] = true;
          //如果当前id已经记录过，说明是重复文件，不重复添加
          if (oldIdMap[newFileInfo._id]) {
            return;
          }
          this.innerFileList.push(newFileInfo);
          this.onFileListEdit("add", newFileInfo, this.innerFileList);
        }
      });
      //前后文件做对比，如果文件不存在，多增，少删。
      for (let i = this.innerFileList.length - 1; i >= 0; i--) {
        let _id = this.innerFileList[i]._id;
        if (!currIdMap[_id]) {
          this.innerFileList.splice(i, 1);
          this.onFileListEdit("delete", this.innerFileList[i], this.innerFileList);
        }
      }
    },
    //文件移除时调用
    handleRemove(file) {
      this.innerFileList.splice(this.innerFileList.indexOf(file), 1);
      this.onFileListEdit("delete", file, this.innerFileList);
    },
    //文件增删时的回调
    onFileListEdit(type, file, rsFileList) {
      let arr = [];
      let arrN = [];
      rsFileList.map((item) => {
        if (item.raw && !item.id) {
          arr.push(item);
        } else {
          arrN.push(item);
        }
      });
      Promise.all(arr.map((item) => uploadFile(item.raw)))
        .then((values) => {
          rsFileList.forEach((n) => {
            if (n.raw && !n.id) {
              let v = values[0];
              n.id = v.id;
              n.path = v.url;
            }
          });
          this.$emit("input", rsFileList);
        })
        .catch(() => {
          this.$emit("input", rsFileList);
        });
    },
    handlePictureCardPreview(file) {
      if (file._isImg) {
        // this.dialogImageUrl = file.path;
        // this.dialogVisible = true;
        let currIndex = 0;
        let imgs = [];
        console.log(this.innerFileList);
        this.innerFileList.forEach((_item, _index) => {
          if (_item._isImg) {
            imgs.push(_item);
            if (_item == file) {
              currIndex = imgs.length - 1;
            }
          }
        });
        this.imgs = imgs;
        console.log(currIndex);
        console.log(this.imgs);
        requestAnimationFrame(() => {
          this.$viewer.view(currIndex);
        });
      } else if (file._isPDF) {
        window.open(file.path, "blank");
      } else {
        window.open(file.path, "blank");
      }
    },
    handleDownload(file) {
      if (file._isImg) {
        this.getUrlBase64(file.path).then((base64) => {
          let link = document.createElement("a");
          console.log(base64);
          link.href = base64;
          link.download = file.name;
          link.click();
        });
      } else {
        window.location.href = file.path;
      }
    },

    getUrlBase64(url) {
      return new Promise((resolve) => {
        let canvas = document.createElement("canvas");
        let ctx = canvas.getContext("2d");
        let img = new Image();
        img.crossOrigin = "Anonymous"; //允许跨域
        img.src = url;
        img.onload = function () {
          canvas.height = 300;
          canvas.width = 300;
          ctx.drawImage(img, 0, 0, 300, 300);
          let dataURL = canvas.toDataURL("image/png");
          canvas = null;
          resolve(dataURL);
        };
      });
    },

    downloadPicture(imgSrc, name) {
      const image = new Image();
      // 解决跨域 Canvas 污染问题
      image.setAttribute("crossOrigin", "anonymous");
      image.onload = () => {
        const canvas = document.createElement("canvas");
        canvas.width = image.width;
        canvas.height = image.height;
        const context = canvas.getContext("2d");
        context.drawImage(image, 0, 0, image.width, image.height);
        const url = canvas.toDataURL("image/jpg");
        const a = document.createElement("a");
        a.download = name || "phone";
        a.href = url;
        a.click();
        a.remove();
      };
      image.src = imgSrc;
    },
  },
};
</script>

<style lang="scss">
.ThemeUpload {
  .noShowAdd {
    .el-upload {
      display: none !important;
    }
  }
  .match {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
  }
  .mask {
    content: "";
    opacity: 0.9;
  }

  .actions-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .file-name {
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    opacity: 0.5;
    text-align: center;
  }

  .file-suffix {
    position: absolute;
    width: 100%;
    height: 50px;
    left: 0%;
    top: 36px;
    text-align: center;
    line-height: 50px;
    font-size: 40px;
    font-weight: bold;
    font-style: italic;
    text-shadow: 0 0 10px rgba(255, 255, 255, 1);
  }
}

/*默认颜色样式*/
.ThemeUpload.defaultColor {
}
</style>
<style lang="scss">
.ThemeUpload-dialog {
  .el-dialog {
    border: none;
    background: transparent;
  }

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 0;
  }
}

.ThemeUpload.disabled {
  .el-upload.el-upload--picture-card {
    display: none;
  }
}
</style>
