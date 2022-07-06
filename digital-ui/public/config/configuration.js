window.sys = {
  webSocketApi: "ws://localhost:18003",
};
window.arcgis = {
  // api地址
  // api: "/arcgis_js_api/library/4.17",
  api: "http://10.20.3.12:8000/arcgis_js_api/library/4.17",
  // 坐标系
  spatialReference: { wkid: 4326 },
  initialExtent: {
    xmin: 116.32,
    ymin: 25.01,
    xmax: 116.5,
    ymax: 25.082,
    spatialReference: { wkid: 4326 },
  },
  // 限制地图的拖动范围
  constraints: {
    geometry: {
      type: "extent",
      xmin: 115.39,
      ymin: 25,
      xmax: 117.45,
      ymax: 25.07,
    },
    minScale: 500000,
  },
  // 地图中心位置
  center: [116.41968504, 25.04808395],
  // 默认缩放级别
  zoom: 14,

  // 天地图配置
  tdt: {
    token: "f8a1245ea545b76a4aa046fc1250be1b",
    cva: {
      name: "影像图",
      url: "http://{subDomain}.tianditu.gov.cn/cva_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cva&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&tk=",
    },
    vec: {
      name: "影像图",
      url: "http://{subDomain}.tianditu.gov.cn/vec_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=vec&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&tk=",
    },
    img: {
      name: "影像图",
      url: "http://{subDomain}.tianditu.gov.cn/img_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=img&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&tk=",
    },
    cia: {
      name: "影像图",
      url: "http://{subDomain}.tianditu.gov.cn/cia_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cia&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&tk=",
    },
  },

  // 图层数据
  // 加载图层的方式 包括mapImage 和 feature
  loadLayerType: "mapImage",
  layers: [
    {
      server:
        "http://10.20.3.20:6080/arcgis/rest/services/meide/Waterpipe/MapServer",
      sublayers: [
        {
          id: 0,
          name: "waterpipe",
          label: "管线",
          visible: true,
        },
      ],
    },
    {
      server:
        "http://10.20.3.20:6080/arcgis/rest/services/meide/WaterDistribution/MapServer",
      sublayers: [
        {
          id: 0,
          name: "valve",
          label: "阀门",
          visible: true,
        },
        {
          id: 1,
          name: "valveWell",
          label: "阀门井",
          visible: false,
        },
        {
          id: 2,
          name: "fireHydrant",
          label: "消防栓",
          visible: false,
        },
        {
          id: 3,
          name: "ventValve",
          label: "排气阀",
          visible: false,
        },
        {
          id: 4,
          name: "waterMeter",
          label: "水表",
          visible: false,
        },
        {
          id: 5,
          name: "waterMeterWell",
          label: "水表井",
          visible: false,
        },
        {
          id: 6,
          name: "tee",
          label: "三通",
          visible: false,
        },
        {
          id: 7,
          name: "cross",
          label: "四通",
          visible: false,
        },
        {
          id: 8,
          name: "elbow",
          label: "弯头",
          visible: false,
        },
        {
          id: 9,
          name: "pipePlug",
          label: "管堵",
          visible: false,
        },
        {
          id: 10,
          name: "user",
          label: "用户",
          visible: false,
        },
      ],
    },
  ],
  // 连通性分析配置
  connectivity: {
    url: "http://10.20.3.20:6080/arcgis/rest/services/net/NAServer/%E8%B7%AF%E5%BE%84",
  },
};
