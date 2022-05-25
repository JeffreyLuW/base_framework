import store from '../../store/index';
import simple_util from '../../utils/simple_util.js';
import {commonUploadFiles} from '../../api/common/common.js';

//自动转换文件上传组件的value和input事件。使得文件上传只需要一个字段value值即可。
//否则需要自行传入文件列表和提交时处理文件上传
//import vmodelTransformer from './upload_vmodel_transformer.js';
export default {
  //给定value时，转换为实际文件列表
  setValue: (value, next, dataUpload) => {
    if (!value || typeof value != "string") {
      //null或者是文件列表
      next(value || []);
    } else {
      let rs = value.trim().split(" ").map((p) => {
        let name = p.substring(p.lastIndexOf("/") + 1);
        if (name.indexOf("__") > -1) {
          name = name.substring(name.indexOf("__") + 2);
        }
        let url = dataUpload._pathBlob && dataUpload._pathBlob[p];
        let hasBlobUrl = !!url;
        if (!url) {
          url = simple_util.fixFileUrl(p);
        }
        return {
          hasBlobUrl,
          url,
          name: name,
          path: p
        }
      });
      next(rs);
    }
  },
  //由文件列表，转model的value值。
  //value=fileList,next(value,ok,err)回调  dataUpload 上传组件  rightNow？
  getValue: (value, next, dataUpload, rightNow) => {
    if (!value || !value.length) {
      next(null, true);
      return;
    }
    let uploadFiles = value.filter((item) => !!item.raw);
    //无上传文件
    if (!uploadFiles || !uploadFiles.length) {
      let rs = value.map((item) => item.path).join(" ");
      next(rs, true);
      return;
    }
    //有上传的文件。上传，然后回调 上传失败，则只显示原来的图片结果。
    Promise.all(
      uploadFiles.map((item) => commonUploadFiles(item.raw))
    ).then((values) => {
      //防止本地文件，重新下载，记录path对应的blob路径。优先展示blob路径。
      if (!dataUpload._pathBlob)
        dataUpload._pathBlob = {};
      uploadFiles.forEach((u, i) => {
        let newPath = values[i].fileName;
        if (u.url)
          dataUpload._pathBlob[newPath] = u.url;
        u.path = newPath;
      });
      let rs = value.map((item) => item.path).join("");
      next(rs, true);
    }).catch(() => {
      let rs = value.filter((item) => !item.raw).map((item) => item.path).join(" ");
      next(rs, false);
    });
  }
}
