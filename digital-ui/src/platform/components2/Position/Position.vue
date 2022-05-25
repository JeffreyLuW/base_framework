<template>
    <AutoDialog
        :show="positionVisible"
        title="定位"
        sizeMode="auto"
        width="820px"
        :showFooter="false"
        fullscreen.sync="null"
        :closeOnClickModal="false"
        @close="handleClose"
    >
        <map-shell @init="handelInit" ref="map"></map-shell>
        <div class="control">
            <el-radio-group
                v-model="activeIndex"
                @change="changeLayer"
                size="small"
            >
                <el-radio-button :label="1">矢量图</el-radio-button>
                <el-radio-button :label="2">卫星图</el-radio-button>
            </el-radio-group>
            <!-- <div class="switch">
                <div
                    class="left"
                    @click="leftClick"
                    :class="{ active: activeIndex === '1' }"
                >
                    矢量图
                </div>
                <div
                    class="right"
                    @click="rightClick"
                    :class="{ active: activeIndex === '2' }"
                >
                    卫星图
                </div>
            </div> -->
        </div>
    </AutoDialog>
</template>
<script>
import mapShell from "./MapShell.vue";
export default {
    props: {
        positionVisible: {
            type: Boolean,
            default: false,
        },
        positionData: {
            type: Array,
            default: () => {
                return [];
            },
        },
    },
    components: { mapShell },
    data() {
        return {
            isLoad: false,
            dialogVisible: false,
            mapId: "",
            view1: null,
            view2: null,
            ly1: null,
            ly2: null,
            map: null,
            clickCoordinates: "",
            vectorLayer: null,
            activeIndex: "1",
        };
    },

    methods: {
        parentsReady() {
            console.log(this.isLoad);
            console.log(this.$refs);
        },

        handleClose() {
            this.$emit("close");
        },
        handelInit(id) {
            this.mapId = id;
            this.InitMap();
            this.$emit("scriptReady");
        },
        InitMap() {
            this.view1 = new ol.View({
                center: [117.0, 36.7],
                zoom: 10,
                minZoom: 6,
                projection: "EPSG:4326",
            });
            //卫星图中国
            let chinaWX = new ol.layer.Tile({
                source: new ol.source.Tianditu({
                    layerType: "img",
                    key: "1d109683f4d84198e37a38c442d68311",
                    projection: "EPSG:4326",
                }),
            });
            //卫星图中国标准
            let chinaWXBZ = new ol.layer.Tile({
                source: new ol.source.Tianditu({
                    layerType: "img",
                    isLabel: true,
                    key: "1d109683f4d84198e37a38c442d68311",
                    projection: "EPSG:4326",
                }),
            });
            // 矢量图中国
            let chinaSL = new ol.layer.Tile({
                source: new ol.source.Tianditu({
                    layerType: "vec",
                    key: "1d109683f4d84198e37a38c442d68311",
                    projection: "EPSG:4326",
                }),
            });
            // 矢量图标注中国
            let chinaSLBZ = new ol.layer.Tile({
                source: new ol.source.Tianditu({
                    layerType: "vec",
                    isLabel: true,
                    key: "1d109683f4d84198e37a38c442d68311",
                    projection: "EPSG:4326",
                }),
            });
            //初始化图层组，方便显示与隐藏
            this.ly1 = new ol.layer.Group({
                layers: [chinaSL, chinaSLBZ],
            });

            this.ly2 = new ol.layer.Group({
                layers: [chinaWX, chinaWXBZ],
            });

            // 初始化地图信息
            this.map = new ol.Map({
                target: this.mapId,
                view: this.view1,
                interactions: new ol.interaction.defaults({
                    doubleClickZoom: false, //屏蔽双击放大事件
                }),
            });

            this.map.addLayer(this.ly2);
            this.map.addLayer(this.ly1);

            this.map.on("click", (e) => {
                this.clickCoordinates = e.coordinate;
                this.addIcon(this.clickCoordinates);
            });
            this.map.on("dblclick", () => {
                this.$emit("dblclick", this.clickCoordinates);
            });
        },
        addIcon(data) {
            if (this.vectorLayer) {
            }
            this.map.removeLayer(this.vectorLayer);
            var point = new ol.geom.Point(data);
            let src = require("@/platform/assets/map_img/normal.png");
            var iconStyle = new ol.style.Style({
                image: new ol.style.Icon({
                    src: src,
                }),
            });
            var feature = new ol.Feature({
                geometry: point,
            });
            feature.setStyle(iconStyle);
            var pointSource = new ol.source.Vector({
                features: [feature],
                // wrapX: false
            });
            this.vectorLayer = new ol.layer.Vector({
                source: pointSource,
            });
            this.vectorLayer.setZIndex(10);
            this.map.addLayer(this.vectorLayer);
        },
        changeLayer(v) {
            if (v == 1) {
                this.leftClick();
            } else {
                this.rightClick();
            }
        },
        //控件按钮点击
        leftClick() {
            this.activeIndex = "1";
            this.ly2.setVisible(false);
            this.ly1.setVisible(true);
        },
        rightClick() {
            this.activeIndex = "2";
            this.ly1.setVisible(false);
            this.ly2.setVisible(true);
        },
    },
    watch: {
        positionData: {
            deep: true,
            handler: function (val, oldVal) {
                if (val.length > 0) {
                    this.view2 = new ol.View({
                        center: val,
                        zoom: 10,
                        minZoom: 6,
                        projection: "EPSG:4326",
                    });
                    this.map.setView(this.view2);
                    this.addIcon(val);
                }
            },
        },
    },
    mounted() {
        this.$bus.$on("posiTiongDataOk", () => {
            if (this.positionData.length > 0) {
                this.view2 = new ol.View({
                    center: this.positionData,
                    zoom: 10,
                    minZoom: 6,
                    projection: "EPSG:4326",
                });
                this.map.setView(this.view2);
                this.addIcon(this.positionData);
            }
        });
    },
    beforeDestroy() {
        this.$bus.$off("posiTiongDataOk");
    },
};
</script>
<style scoped lang="scss">
// /deep/.el-dialog {
//     height: 500px;
//     .control {
//         position: relative;
//         .switch {
//             position: absolute;
//             display: flex;
//             left: 0;
//             top: -25px;
//             .left,
//             .right {
//                 width: 50px;
//                 line-height: 25px;
//                 background-color: #fff;
//                 text-align: center;
//                 cursor: pointer;
//             }
//             .active {
//                 background-color: #ccc;
//             }
//         }
//     }
//     .el-dialog__body {
//         height: 100%;
//         background-color: #fff;
//     }
//     .el-dialog__header {
//         height: 45px;
//         background-color: #eee;
//     }
// }
</style>
