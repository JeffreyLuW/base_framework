export default class Effect {
  constructor(scene, canvas, engine) {
    this.scene = scene;
    this.canvas = canvas;
    this.engine = engine;
  }

  test() {

  }

  //创建个带子路径。需要三点。如果只有两个点，你应该创建要给平面，而不是ribbon。
  createRibbonPath(points, width, upDir) {
    width = width || 1;
    upDir = upDir?.normalizeToNew() || BABYLON.Vector3.Up();

    if (points.length < 2) {
      return;
    }
    //先去掉是直线的点。
    let newLines = [];

    let projectPlane = BABYLON.Plane.FromPositionAndNormal(BABYLON.Vector3.Zero(), upDir);
    let invertUpDir = upDir.scale(-1);
    for (let i = 0; i < points.length; i++) {
      let p1 = points[i];
      let ray = new BABYLON.Ray(p1, upDir);
      let projectPoint = null;

      let num = ray.intersectsPlane(projectPlane);
      if (num == null) {
        ray = new BABYLON.Ray(p1, invertUpDir);
        num = ray.intersectsPlane(projectPlane);
        projectPoint = p1.add(invertUpDir.scale(num));
      } else {
        projectPoint = p1.add(upDir.scale(num));
      }
      newLines.push(projectPoint);
    }

    let path1, path2;
    if (points.length == 2) {
      let mainVec = newLines[1].subtract(newLines[0]);
      let dVec = BABYLON.Vector3.Cross(mainVec, upDir).normalize().scale(width / 2);
      path1 = [points[0].add(dVec), points[1].add(dVec)];
      path2 = [points[0].subtract(dVec), points[1].subtract(dVec)];
    } else {
      //3个点以上。 每3个点，确定一个平面。
      path1 = [];
      path2 = [];
      for (let i = 0; i < newLines.length - 2; i++) {
        let p1 = newLines[i];
        let p2 = newLines[i + 1];
        let p3 = newLines[i + 2];

        let angle = BABYLON.Vector3.GetAngleBetweenVectors(p3.subtract(p2), p1.subtract(p2), upDir);
        let qua = BABYLON.Quaternion.RotationAxis(upDir, angle / 2);
        let rs = p3.rotateByQuaternionAroundPointToRef(qua, p2, BABYLON.Vector3.Zero());
        rs = rs.subtract(p2).normalize();

        // let plane = BABYLON.Plane.FromPoints(p1, p2, p3);
        // let cosVal = BABYLON.Vector3.Dot(plane.normal, upDir);
        let extScale = Math.sin(angle / 2);
        rs = rs.scale(width / 2 / extScale).add(points[i + 1]);
        path1.push(rs);
      }

      //测试=== 添加首尾点代码  默认根据方向计算，也可以自定义。===
      let dVec = BABYLON.Vector3.Cross(newLines[1].subtract(newLines[0]), upDir).normalize().scale(-width / 2);
      path1.unshift(points[0].add(dVec));
      let dVecLast = BABYLON.Vector3.Cross(newLines[points.length - 1].subtract(newLines[points.length - 2]), upDir).normalize().scale(-width / 2);
      path1.push(points[points.length - 1].add(dVecLast));
      path1.forEach((v, i) => {
        path2.push(points[i].subtract(v).add(points[i]));
      });
    }
    let pathArray = [];
    path1.forEach((v, i) => {
      pathArray.push([
        path1[i], path2[i]
      ]);
    });
    return pathArray;
  }

  //可以实现水面效果。 可以添加倒影和控制波浪(需要平面细分)
  water() {
    let scene = this.scene;
    // Skybox
    var skybox = BABYLON.Mesh.CreateBox("skyBox", 1000.0, scene);
    var skyboxMaterial = new BABYLON.StandardMaterial("skyBox", scene);
    skyboxMaterial.backFaceCulling = false;

    //var hdrTexture = BABYLON.CubeTexture.CreateFromPrefilteredData("images/environment.dds", scene);

    skyboxMaterial.reflectionTexture = new BABYLON.CubeTexture("images/skybox/skybox", scene);
    skyboxMaterial.reflectionTexture.coordinatesMode = BABYLON.Texture.SKYBOX_MODE;
    skyboxMaterial.diffuseColor = new BABYLON.Color3(0, 0, 0);
    skyboxMaterial.specularColor = new BABYLON.Color3(0, 0, 0);
    skyboxMaterial.disableLighting = true;
    skybox.material = skyboxMaterial;
    skybox.infiniteDistance = true;

    // // Water
    // var waterMeshClone = scene.getMeshByID("Rectangle026").clone();
    // waterMeshClone.position.y -= 0.1;
    // var waterMeshCloneMat = new BABYLON.StandardMaterial("waterMeshCloneMat");
    // waterMeshCloneMat.diffuseTexture = new BABYLON.Texture("images/second.png");
    // waterMeshClone.material = waterMeshCloneMat;

    //renderSize 影响清晰度。
    var water = new BABYLON.WaterMaterial("water", scene, new BABYLON.Vector2(1024, 1024));//57 * 4, 90 * 4
    water.backFaceCulling = true;
    water.bumpTexture = new BABYLON.Texture("images/waterbump.png", scene);
    water.bumpTexture.vScale = water.bumpTexture.uScale = 0.2;

    water.windForce = 100;
    water.waveHeight = 0;
    // water.waveSpeed=50;
    water.bumpHeight = 0.4;
    water.windDirection = new BABYLON.Vector2(1, 0);
    water.waterColor = new BABYLON.Color3(56 / 255, 92 / 255, 142 / 255);
    water.colorBlendFactor = 0.1;
    // water.addToRenderList(waterMeshClone);
    //可以反射地面和天空，来实现更好的底色效果。
    water.addToRenderList(skybox);
    scene.getMeshByID("Rectangle026").material = water;

    window.water = water;
  }

  /**
   * 某个点创建一个棱锥。
   * @param size
   * @param pos
   */
  createPointerCylinder(size, pos) {
    let scene = this.scene;
    let box = BABYLON.MeshBuilder.CreateCylinder("", {
      height: size || 4,
      diameterTop: size || 4,
      tessellation: 3,
      diameterBottom: 0
    });
    box.position = pos || BABYLON.Vector3.Zero();
    let material = new BABYLON.StandardMaterial("", scene);
    material.diffuseColor = BABYLON.Color3.FromInts(0, 200, 255);
    material.emissiveColor = BABYLON.Color3.FromInts(0, 100, 128);
    material.alpha = 0.9;
    box.material = material;
    this.simpleAnimSin(box, "position.y", 60, [box.position.y, box.position.y + size / 4]);
    box.registerBeforeRender(() => {
      box.rotation.y += 0.01;
    });
  }

  /**
   * 用于追踪某个物体，产生飞线。
   *
   * @param target
   * @param options
   * @returns {BABYLON.TrailMesh}
   */
  trailMesh(target, options) {
    let diameter = options.diameter || 0.2;
    let length = options.length || 60;
    let color = options.color || new BABYLON.Color3.FromInts(0, 128, 255);

    let scene = target.getScene();

    var trail = new BABYLON.TrailMesh('new', target, scene, diameter, length, true);
    var sourceMat = new BABYLON.StandardMaterial('sourceMat', scene);
    sourceMat.emissiveColor = sourceMat.diffuseColor = color;
    sourceMat.specularColor = new BABYLON.Color3.Black();
    trail.material = sourceMat;
    return trail;
  }

  /**
   * 纹理叠加效果
   *  options={
   *       initTime:0,
   *       secondSpeed:0.001,
   *       dir:1,
   *       uvScale:4,
   *       textureSampler:null, //new BABYLON.Texture(baseUrl + "images/second.png")
   *       textureSecond:null,
   *       timeScale:1
   *    };
   */
  shaderTextureAddive(mesh, options = {}) {
    var shaderMaterial = new BABYLON.ShaderMaterial("", this.scene, "shader/addive_texture",
      {
        attributes: ["position", "normal", "uv"],
        uniforms: ["world", "worldView", "worldViewProjection", "view", "projection", "time", "secondSpeed", "dir", "uScale", "vScale"]
      });
    shaderMaterial.setFloat("time", options.initTime || 0);
    shaderMaterial.setFloat("secondSpeed", options.secondSpeed || 0);
    shaderMaterial.setInt("dir", options.dir || 1);
    shaderMaterial.setFloat("uScale", options.uScale || options.uvScale || 1);
    shaderMaterial.setFloat("vScale", options.vScale || options.uvScale || 1);

    if (options.textureSampler)
      shaderMaterial.setTexture("textureSampler", options.textureSampler);
    if (options.textureSecond)
      shaderMaterial.setTexture("textureSecond", options.textureSecond);

    //textureSecond
    mesh.material = shaderMaterial;

    let timeScale = options.timeScale || 1;
    mesh.registerBeforeRender(() => {
      let sec = Date.now() % 3600000 / 1000 / 4 * timeScale;
      shaderMaterial.setFloat("time", sec);
    });
  }

  //边缘高亮 移除.
  hightLightRemove(mesh) {
    try {
      let hightLight = this.scene.getHighlightLayerByName("hightLight");
      if (hightLight) {
        hightLight.removeMesh(mesh);
      }
    } catch (e) {

    }
  }

  //边缘高亮
  hightLight(mesh, color) {
    let hightLight = null;
    try {
      hightLight = this.scene.getHighlightLayerByName("hightLight");
    } catch (e) {

    }
    if (!hightLight) {
      hightLight = new BABYLON.HighlightLayer("hightLight", this.scene);
    }
    hightLight.addMesh(mesh, color);
  }

  //辉光
  glow(mesh, color) {
    let glow = null;
    try {
      glow = this.scene.getGlowLayerByName("glow");
    } catch (e) {

    }
    if (!glow) {
      glow = new BABYLON.GlowLayer("glow", this.scene, {
        mainTextureFixedSize: 1024,
        blurKernelSize: 64
      });

      glow.intensity = 0.5;
      glow.customEmissiveColorSelector = function (mesh, subMesh, material, result) {
        // result.set(0.65, 1, 1, 1);
        if (mesh.glowColor) {
          result.copyFrom(mesh.glowColor);
        } else {
          result.set(0, 0, 0, 0);
        }
      };
    }
    mesh.glowColor = color;
    glow.addIncludedOnlyMesh(mesh);
  }

  /**
   * 正弦运动. 只适合2点来回运动. 可以提前给mesh.animations设置动画。可以形成复合动画。
   * @param mesh 目标mesh
   * @param property 属性 scaling position rotate  可以添加.x|y|z
   * @param frames 总帧数 60
   * @param values [] 值的数组.
   */
  simpleAnimSin(mesh, property, frames, values) {
    let dataType = property.indexOf(".") > -1 ? BABYLON.Animation.ANIMATIONTYPE_FLOAT : BABYLON.Animation.ANIMATIONTYPE_VECTOR3;
    let anim = new BABYLON.Animation("", property, 30, dataType, BABYLON.Animation.ANIMATIONLOOPMODE_CYCLE);
    let step = Math.round(frames / (values.length - 1));
    let keys = [];
    values.forEach((v, i) => {
      keys.push({frame: i * step, value: v})
    });
    // console.log(keys);
    anim.setKeys(keys);
    var easingFunction = new BABYLON.CircleEase();
    easingFunction.easeInCore = function (g) {
      g = Math.sin(2 * Math.PI * g - Math.PI / 2) / 2 + 1 / 2;
      return g;
    };
    // 0  在第一帧 1 在最后一帧
    anim.setEasingFunction(easingFunction);
    if (!mesh.animations) mesh.animations = [];
    mesh.animations.push(anim);
    this.scene.beginAnimation(mesh, 0, frames, true);
  }
}
