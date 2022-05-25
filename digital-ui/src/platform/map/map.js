
export function InitPoint(ol) {
  var point = new ol.geom.Point([117, 36.8]);
  var point2 = new ol.geom.Point([118, 36.8]);
  let src = require('../assets/img/map/normal.png');
  var iconStyle = new ol.style.Style({
    image: new ol.style.Icon(({
      src: src
    }))
  });
  var feature = new ol.Feature({
    geometry: point,
    name: "第一个",
    water:"3.0m",
  });
  var feature2 = new ol.Feature({
    geometry: point2,
    name: "第二个",
    water:"4.2m",
  });
  feature.setStyle(iconStyle);
  feature2.setStyle(iconStyle);
  var pointSource = new ol.source.Vector({
    features: [feature, feature2],
    // wrapX: false
  });
  var vectorLayer = new ol.layer.Vector({
    source: pointSource
  });
  vectorLayer.setZIndex(10)
  return vectorLayer
};

export function InitLine(ol) {

  var feature = new ol.Feature({
    geometry: new ol.geom.LineString(
      [[117.244, 36.8], [117.675, 36.8]])
  });


  feature.setStyle(new ol.style.Style({
    stroke: new ol.style.Stroke({
      width: 3,
      color: [255, 0, 0, 1]
    })
  }));


  var lineSource = new ol.source.Vector({
    features: [feature],
    // wrapX: false
  });
  var lineLayer = new ol.layer.Vector({
    source: lineSource
  });

  lineLayer.setZIndex(10)
  return lineLayer
};

export function InitPolygon(ol) {

  var feature = new ol.Feature({
    geometry: new ol.geom.Polygon(
      [[[117.1, 35.8], [118.4, 35.8], [118.2, 37.8], [117.3, 37.8], [116.4, 31.8]]])
  });

  feature.setStyle(new ol.style.Style({
    fill: new ol.style.Fill({         
      color: 'rgba(255, 0, 0, 0.2)'
    }),
  }));

  var PolygonSource = new ol.source.Vector({
    features: [feature],
    // wrapX: false
  });
  var PolygonLayer = new ol.layer.Vector({
    source: PolygonSource
  });
  PolygonLayer.setZIndex(10)
  return PolygonLayer
};

export function initGQQD (ol){
  let GQQDlayer = new ol.layer.Tile({
    Zoom:15,
    source: new ol.source.TileSuperMapRest({
        // url: 'http://124.128.194.82:8090/iserver/services/map-GQ/rest/maps/GQQD',
        wrapX: true,
        transparent:false,
    }),
    projection: 'EPSG:4326'
});
  return GQQDlayer
}
