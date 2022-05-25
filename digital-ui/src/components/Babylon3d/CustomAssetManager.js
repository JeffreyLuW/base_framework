/**
 * 扩展的资源加载管理器。统一监听贴图、模型加载
 * @param scene
 * @param onProgress
 * @param onFinish
 * @constructor
 */
function CustomAssetManager(scene, onProgress, onFinish) {

  var that = this;
  this.onProgress = onProgress; // (remainingCount, totalCount)=>void
  this.onFinish = onFinish; // (isFirstFinished)=>void
  var extCount = 0; //额外加载的数量
  var exRemainCount = 0; //额外未加载的数量
  var assetsManager = new BABYLON.AssetsManager(scene);
  var assetMap = {}; //存储加载的模型
  that.assetMap = assetMap;
  var isFirstFinished = true;
  //禁用默认加载动画
  assetsManager.useDefaultLoadingScreen = false;

  assetsManager.onProgress = function (remainingCount, totalCount, task) {
    remainingCount = assetsManager._waitingTasksCount + exRemainCount;
    totalCount = assetsManager._totalTasksCount + extCount;
    if (that.onProgress) {
      setTimeout(function () {
        that.onProgress(remainingCount, totalCount, task);
      });
    }
    if (remainingCount <= 0 && that.onFinish) {
      setTimeout(function () {
        that.onFinish(isFirstFinished);
      });
      isFirstFinished = false;
    }
  };

  //用于加载各种模型到container:AssetContainer。 需要添加 babylonjs.loaders.min.js
  this.LoadAssetContainer = function (name, rootUrl, sceneFilename, scene, onSuccess, onProgress, onError, pluginExtension) {
    extCount++;
    exRemainCount++;
    BABYLON.SceneLoader.LoadAssetContainer(rootUrl, sceneFilename, scene, function (container) {
      that.setAssetByName(name, container);
      exRemainCount--;
      assetsManager.onProgress(assetsManager._waitingTasksCount, assetsManager._totalTasksCount);
      if (onSuccess) {
        onSuccess(container);
      }
    }, onProgress, onError, pluginExtension);
  };

  //扩展一些自己的task  autoCache=true|undefined可以用
  //加载纹理贴图
  this.addTextureTask = function (taskName, url, autoCache) {
    if (autoCache === undefined) autoCache = true;
    var textureTask = assetsManager.addTextureTask(taskName, url);
    if (autoCache)
      textureTask.onSuccess = function (task) {
        that.setAssetByName(taskName, task.texture);
      };
    return textureTask;
  };

  //mesh
  this.addMeshTask = function (taskName, meshesNames, rootUrl, sceneFilename, autoCache) {
    if (autoCache === undefined) autoCache = true;
    var textureTask = assetsManager.addMeshTask(taskName, meshesNames, rootUrl, sceneFilename);
    if (autoCache)
      textureTask.onSuccess = function (task) {
        that.setAssetByName(taskName, task.loadedMeshes);
      };
    return textureTask;
  };

  //根据name 保存资源引用 名称不能重复.。  LoadAssetContainer  addTextureTask可自动保存
  this.setAssetByName = function (name, asset) {
    if (assetMap[name]) {
      console.warn("CusomAssetManager,重复的资源名称,将被覆盖!", name, asset);
    }
    assetMap[name] = asset;
    return assetMap[name];
  };

  //根据name 获取资源 加载完成后调用
  this.getAssetByName = function (name, autoClear) {
    var rs = assetMap[name];
    if (autoClear) {
      delete assetMap[name];
    }
    return rs;
  };

  //仅仅清空缓存，但需要自己释放对应的资源
  this.clearAssets = function () {
    assetMap = {};
  };
  //获取资源加载器
  this.getManager = function () {
    return assetsManager;
  };

  //加载
  this.load = function () {
    //无任务
    if (assetsManager._tasks.length + exRemainCount <= 0) {
      assetsManager.onProgress(0, 0, null);
      return;
    }
    assetsManager.load();
  }
}
