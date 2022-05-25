//放置一个文本
function putText(scene, text, w, h, x, y, z, ) {
  var textPlane = BABYLON.MeshBuilder.CreatePlane("textPlane", {
    width: w,
    height: h
  }, scene);
  textPlane.position.set(x, y, z);
  var textPlaneMat = new BABYLON.StandardMaterial("textPlaneMat", scene);
  textPlaneMat.diffuseTexture = createTextTexture("text", scene, 4, 2, text, "#ffffff");
  textPlaneMat.backFaceCulling = false;
  textPlane.material = textPlaneMat;
}


/**
 * 创建文本纹理
 * @param width
 * @param height
 * @param text
 * @return {*}
 */
function createTextTexture(name, scene, width, height, text, color, clearColor) {
  color = color || "black";
  clearColor = clearColor || 'transparent';

  //越大越清晰
  var ratio = 128;
  width *= ratio;
  height *= ratio;

  var myDynamicTexture = new BABYLON.DynamicTexture(name, {
    width: width,
    height: height
  }, scene);
  myDynamicTexture.hasAlpha = true;
  var ctx = myDynamicTexture.getContext();
  var size = width;
  ctx.font = "bold " + size + "px Arial";
  var metrics = ctx.measureText(text);
  size *= width / metrics.width;
  ctx.font = "bold " + size + "px Arial";
  myDynamicTexture.drawText(text, 0, height / 2 + size * 0.4, ctx.font, color, clearColor, true, true);
  return myDynamicTexture;
}


export default {
  createDefault(canvas, engine, DebugTools) {
    let scene = new BABYLON.Scene(engine);
    scene.debugInfo = DebugTools;
    scene.fogMode = BABYLON.Scene.FOGMODE_NONE; // 线性 才能用 fogStart fogEnd
    scene.fogColor = BABYLON.Color3.FromHexString("#60ACFC");
    scene.fogDensity = 0.01;
    scene.fogStart = 300;
    scene.fogEnd = 500;

    // Fog 这个雾的效果 不咋地
    // scene.fogMode = BABYLON.Scene.FOGMODE_EXP;
    // scene.fogDensity = 0.005;

    // 1.4   3.14
    let camera = new BABYLON.ArcRotateCamera("camera", -Math.PI / 2, Math.PI / 2 * 0.7, 100, BABYLON.Vector3.Zero(), scene);
    //显示镜头远景和俯仰角度
    camera.lowerRadiusLimit = 4;
    camera.upperRadiusLimit = 200;
    camera.wheelPrecision = 10;
    camera.panningSensibility = 150; //鼠标移动的速度。
    camera.wheelDeltaPercentage = .05; //每次缩放的比例
    camera.upperBetaLimit = Math.PI / 2 * 0.9;
    // camera.useBouncingBehavior = true;
    camera.useFramingBehavior = false;
    camera.attachControl(canvas, true);

    //标记初始位置
    camera._initView = {
      target: camera.target.clone(),
      alpha: camera.alpha,
      beta: camera.beta,
      radius: camera.radius
    };

    // let dirLight = new BABYLON.DirectionalLight("dirLight", new BABYLON.Vector3(0, -2, 3), scene);
    // dirLight.intensity = 1;

    let light = new BABYLON.HemisphericLight("envLight", new BABYLON.Vector3(10, 10, 10), scene);
    light.intensity = 1;
    light.groundColor = new BABYLON.Color3(0, 0, 0);


    return scene;
  },

  /**
   * 指定世界坐标,添加个html文本
   * @param render
   * @param text
   * @param targetPos
   * @returns {Element}
   */
  addTagAtPostion(render, text, targetPos) {
    let dom = null;
    let needRemove = false;
    if (text instanceof Element) {
      dom = text;
    } else {
      dom = document.createElement("div");
      dom.innerHTML = text;
      render.canvas.parentElement.appendChild(dom);
      needRemove = true;
      dom.classList.add("Cmd3dCore-tag");
    }
    //给相机添加一个获取方向的node
    let camera = render.scene.activeCamera;
    let cameraDirNode = render.scene.getTransformNodeByName("cameraDir");
    if (!cameraDirNode) {
      cameraDirNode = new BABYLON.TransformNode("cameraDir", render.scene);
      cameraDirNode.parent = camera;
    }
    this.onGetScreenPoint(render.scene, targetPos, (pos) => {
      //背面剔除
      let dir1 = targetPos.subtract(camera.position).normalize();
      let dir2 = cameraDirNode.forward;
      if (BABYLON.Vector3.Dot(dir1, dir2) < 0) {
        dom.style.left = (-1000 + 0) + 'px';
        dom.style.top = (-1000 - 0) + 'px';
      } else {
        dom.style.left = (pos.x + 0) + 'px';
        dom.style.top = (pos.y - 0) + 'px';
      }
      //解决tag覆盖的问题。
      // dom.style.zIndex = 100000 - parseInt(camera.position.subtract(targetPos).length());
    });
    render.scene.onDisposeObservable.add(() => {
      if (needRemove)
        dom.parentElement.removeChild(dom);
    });
    return dom;
  },


  /**
   * 坐标系转换。 局部转世界坐标系.
   * mesh的世界坐标位置:  mesh.getAbsolutePosition()
   * @param localPos
   * @param localWoldMatrix
   * @returns {BABYLON.Vector3}
   */
  localToWold(localPos, localWoldMatrix) {
    let woldPos = BABYLON.Vector3.TransformCoordinates(localPos, localWoldMatrix);
    return woldPos;
  },

  /**
   * 世界坐标转局部
   * @param woldPos
   * @param localWoldMatrix
   * @returns {BABYLON.Vector3}
   */
  woldToLocal(woldPos, localWoldMatrix) {
    return BABYLON.Vector3.TransformCoordinates(woldPos, localWoldMatrix.clone().invert());
  },

  /**
   * 创建一个简易坐标系
   * @param scene
   * @param size
   */
  createAxis(scene, size = 1) {
    var colorX = BABYLON.Color4.FromHexString("#00ff00ff");
    var colorY = BABYLON.Color4.FromHexString("#ff0000ff");
    var colorZ = BABYLON.Color4.FromHexString("#0000ffff");
    var dsize = size / 20;
    BABYLON.MeshBuilder.CreateLines("line", {
      points: [
        new BABYLON.Vector3(0, 0, 0),
        new BABYLON.Vector3(size, 0, 0),
        new BABYLON.Vector3(size - dsize, dsize, 0),
        new BABYLON.Vector3(size, 0, 0),
        new BABYLON.Vector3(size - dsize, -dsize, 0),
      ],
      colors: [colorX, colorX, colorX, colorX, colorX]
    }, scene);
    BABYLON.MeshBuilder.CreateLines("line", {
      points: [
        new BABYLON.Vector3(0, 0, 0),
        new BABYLON.Vector3(0, size, 0),
        new BABYLON.Vector3(dsize, size - dsize, 0),
        new BABYLON.Vector3(0, size, 0),
        new BABYLON.Vector3(-dsize, size - dsize, 0),
      ],
      colors: [colorY, colorY, colorY, colorY, colorY]
    }, scene);

    BABYLON.MeshBuilder.CreateLines("line", {
      points: [
        new BABYLON.Vector3(0, 0, 0),
        new BABYLON.Vector3(0, 0, size),
        new BABYLON.Vector3(0, dsize, size - dsize),
        new BABYLON.Vector3(0, 0, size),
        new BABYLON.Vector3(0, -dsize, size - dsize),
      ],
      colors: [colorZ, colorZ, colorZ, colorZ, colorZ]
    }, scene);
  },

  //计算当前理论和实际帧率 cb(sceneRate, theoryRate);
  //如果cb是dom,则自动创建dom显示.
  createRater(scene, cb) {
    var instrumentation = new BABYLON.SceneInstrumentation(scene);
    instrumentation.captureFrameTime = true;

    //计算真事每秒帧率
    function FrameRater(duration) {
      this.count = 0;
      this.countTime = -1;
      this.duration = duration || 500; //500ms统计一次
      this.rate = -1;

      this.getRate = function () {
        var now = Date.now();
        if (this.countTime < 0) {
          this.countTime = now;
        }
        this.count++;
        //统计时间到了，则计算帧率
        var dTime = now - this.countTime;
        if (dTime > this.duration) {
          this.rate = Math.round(this.count * 1000 / (dTime));
          //计数重置
          this.count = 0;
          this.countTime = now;
        }
        return this.rate;
      }
    }

    var customFrameRater = new FrameRater(500);
    var lastTheoryRate = 0;
    var lastSceneRate = 0;

    var displayDom = null;
    scene.registerBeforeRender(function () {
      var theoryRate = Math.floor(1000 / (instrumentation.frameTimeCounter.lastSecAverage));
      var sceneRate = customFrameRater.getRate();
      if (theoryRate != lastTheoryRate || lastSceneRate != sceneRate) {
        if (cb) {
          if (cb instanceof Element) {
            if (!displayDom) {
              displayDom = document.createElement("div");
              displayDom.classList.add("Cmd3dCore-rater");
              cb.appendChild(displayDom);
            }
            displayDom.innerText = `FPS:${sceneRate},理论FPS:${theoryRate}`;
          } else {
            cb(sceneRate, theoryRate);
          }
        }
        lastTheoryRate = theoryRate;
        lastSceneRate = sceneRate;
      }
    });
    scene.onDisposeObservable.add(function () {
      instrumentation.dispose();
    });
  },
  //网格点击事件
  meshClick(mesh, cb) {
    // add actionManager on each cyl
    if (!mesh.actionManager)
      mesh.actionManager = new BABYLON.ActionManager(mesh.getScene());
    mesh.actionManager.registerAction(
      new BABYLON.ExecuteCodeAction(BABYLON.ActionManager.OnPickTrigger, cb)
    );
  },

  /**
   * 添加gizmo工具，帮助操作mesh.可以用于快速定位。
   * 可以给GizmoManager添加监听 onAttachedToMeshObservable.add((mesh,else)=>{}) 信息
   * @param mesh
   */
  gizmo(mesh) {
    if (!(mesh instanceof Array)) {
      mesh = [mesh];
    }
    let scene = mesh[0].getScene();
    var gizmoManager = scene.__gizmoManager;
    if (!gizmoManager) {
      gizmoManager = new BABYLON.GizmoManager(scene);
      gizmoManager.positionGizmoEnabled = true;
      gizmoManager.rotationGizmoEnabled = true;
      gizmoManager.scaleGizmoEnabled = true;
      gizmoManager.boundingBoxGizmoEnabled = true;
      gizmoManager.attachableMeshes = [];
      scene.__gizmoManager = gizmoManager;
    }
    mesh.forEach((v) => {
      gizmoManager.attachableMeshes.push(v);
    });
    return gizmoManager;
  },


  screenToWorld(point, camera, engine) {
    var viewport = camera.viewport.toGlobal(engine.getRenderWidth(), engine.getRenderHeight());
    return BABYLON.Vector3.Unproject(new BABYLON.Vector3(point.x, point.y, 0), viewport.width, viewport.height, BABYLON.Matrix.Identity(), camera.getViewMatrix(), camera.getProjectionMatrix());
  },

  //世界坐标转屏幕坐标.
  //console.log(worldToScreen(m.absolutePosition, this.render.scene.activeCamera, this.render.scene, this.render.engine))
  worldToScreen(point, camera, scene, engine) {
    return BABYLON.Vector3.Project(point,
      BABYLON.Matrix.Identity(),
      scene.getTransformMatrix(),
      camera.viewport.toGlobal(engine.getRenderWidth(), engine.getRenderHeight()));
  },

  /**
   * 监听世界坐标在屏幕上的位置.
   * @param scene
   * @param position
   * @param cb
   */
  onGetScreenPoint(scene, position, cb) {
    let lastPos = {
      x: 0,
      y: 0
    };
    scene.registerBeforeRender(() => {
      let sp = this.worldToScreen(position, scene.activeCamera, scene, scene.getEngine());
      // sp.x = Math.round(sp.x);
      // sp.y = Math.round(sp.y);
      if (sp.x != lastPos.x || sp.y != lastPos.y) {
        lastPos.x = sp.x;
        lastPos.y = sp.y;
        cb(lastPos);
      }
    });
  },

  
  //鼠标选择mesh然后打印输出。辅助函数
  pickMeshAndLog(scene, cb) {
    let hitMesh = null;
    window.addEventListener("click", () => {
      if (hitMesh)
        hitMesh.visibility = 1;
      let hitInfo = scene.pick(scene.pointerX, scene.pointerY);
      if (hitInfo.hit && hitMesh != hitInfo.pickedMesh) {
        console.log(hitInfo.pickedMesh, hitInfo.pickedMesh.name);
        hitInfo.pickedMesh.visibility = 0.65;
        hitMesh = hitInfo.pickedMesh;
        cb && cb(hitMesh);
      } else {
        hitMesh = null;
      }
    })
  },
}
