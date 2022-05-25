<template>
    <div class="DemoSuperMap">
        <VueSuperMap ref="map" @clickFeature="clickFeature"  @overFeature="overFeature" @outFeature="outFeature" @measure="measure"
                     :serviceLayerUrls="[{name: '37000', url: 'http://222.132.115.246:8090/iserver/services/map-gis/rest/maps/370829'}]"
                     :showToolBar="true" :defaultZoom="1">
            <!--       可以添加额外的控件     -->
            <div style="position: absolute;z-index: 1500;width: 200px;height: 200px;right: 0px;bottom:20px;background: red;">
                <el-button @click="queryDataset">
                    按钮
                </el-button>
                <el-button @click="queryMap">
                    管井
                </el-button>
            </div>
        </VueSuperMap>
    </div>
</template>

<script>

    import VueSuperMap from "./VueSuperMap";
    export default {
        name: "DemoSuperMap",
        components: {VueSuperMap},
        data() {
            return {}
        },
        mounted() {

        },
        methods: {
            queryDataset() {
                let url, filterOpt, sqlOpt;
                url = "http://222.132.115.246:8090/iserver/services/data-gis/rest/data";
                filterOpt = {
                    name: "gis_gwx@GYGIS_DBST",
                    fields: ["gwxID", "GCMC", "CZMC", "MS"],
                    attributeFilter: "lx = 1"
                };
                sqlOpt = {datasetNames: ["GYGIS_DBST:gis_gwx"]};
                let vueMap = this.$refs.map;
                this.$refs.map.queryDatasets(
                    url, filterOpt, sqlOpt, 'Gwjx', (success, features, VueSuperMap, rawArg) => {
                        this.$message(`success:${success}`);
                        if (!success) return;
                        let styleGWX = {
                            strokeColor: "yellow",
                            strokeWidth: 8,
                            fillColor: "yellow",
                            fillOpacity: "0.3"
                        };
                        let styleGWX1 = {
                            strokeColor: "blue",
                            strokeWidth: 4,
                            fillColor: "blue",
                            fillOpacity: "0.3"
                        };
                        features.forEach((f) => {
                            f.style = styleGWX;
                            f.style_normal=styleGWX;
                            f.style_active=styleGWX1;
                        });
                        vueMap.layers.vectorLayer.addFeatures(features);
                    }
                );
            },
            queryMap() {
                let url, filterOpt;
                url = "http://222.132.115.246:8090/iserver/services/map-gis/rest/maps/370829";
                filterOpt = {
                    name: "gis_gj@GYGIS_DBST",
                    fields: ["SmID", "SmX", "SmY", "Gjlx", "gjmc"],
                    attributeFilter: ""
                };
                let vueMap = this.$refs.map;
                this.$refs.map.queryMapLayer(
                    url, filterOpt, 'Gj', (success, features, VueSuperMap, rawArg) => {
                        this.$message(`success:${success}`);
                        if (!success) return;
                        let styleGj = {
                            pointRadius: 8,
                            graphic: true,
                            externalGraphic: require('src/assets/img/icon-home-pannel.jpg'),
                            graphicWidth: 15,
                            graphicHeight: 15
                        };
                        let styleGj1 = {
                            pointRadius: 10,
                            graphic: true,
                            externalGraphic: require('src/assets/img/icon-home-pannel.jpg'),
                            graphicWidth: 20,
                            graphicHeight: 20
                        };
                        features.forEach((f) => {
                            f.style = styleGj;
                            f.style_normal=styleGj;
                            f.style_active=styleGj1;
                        });
                        vueMap.layers.vectorLayer.addFeatures(features);
                    }
                );
            },
            clickFeature(feature) {
                let vueMap = this.$refs.map;
                let html = "<div style='background:red;'>this is click html</div>";// feature会携带相应的数据，可以动态设置文本内容
                let lx = "html";//弹窗加载方式为html，lx=url 弹窗为iframe
                let height = 90;
                let width = 180;
                let x,y;
                if(feature.attributes.type=="Gwjx"){
                    x=feature.geometry.getCentroid().x;
                    y=feature.geometry.getCentroid().y;
                }else{
                    x=feature.geometry.x;
                    y=feature.geometry.y;
                }
                let lonlat = new SuperMap.LonLat(x,y);
                vueMap.showFeatureInfo(html, width, height, lx, lonlat);
            },
            overFeature(feature){
                let vueMap = this.$refs.map;
                let html = "<div style='font-size:.8em; opacity: 0.8; overflow-y:hidden;'  >";
                html += "<table height='2px' style='border:0px;table-layout:fixed;white-space:nowrap' >";
                let lx = "html";//弹窗加载方式为html，lx=url 弹窗为iframe
                let height = 30;
                let width = 60;
                let x,y,name;
                if(feature.attributes.type=="Gwjx"){
                    x=feature.geometry.getCentroid().x;
                    y=feature.geometry.getCentroid().y;
                    name=feature.data.GCMC;
                }else{
                    x=feature.geometry.x;
                    y=feature.geometry.y;
                    name=feature.data.gjmc;
                }
                html += "<tr><td class='td_left_noborder1' style='white-space:nowrap'> " + name + "</td></tr>";
                html += "</table></div>"
                let lonlat = new SuperMap.LonLat(x,y);
                vueMap.showFeatureInfo(html, width, height, lx, lonlat);
            },
            outFeature(feature){
                let map = this.$refs.map;
                let popupInfo = map.popupInfo;
                //清除上一个弹窗。
                if (popupInfo)
                    map.map.removePopup(popupInfo);
            },
            measure(temp){
                alert("量算结果:"+temp.value+temp.unit);
            }
        }
    }
</script>

<style lang="scss">
    .DemoSuperMap {
        .VueSuperMap {
            height: 100%;
        }
        height: 100%;
    }
</style>
