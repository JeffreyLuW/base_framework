import Vue from "vue";
import Vuex from "vuex";
import app from "./modules/app";
import map from "./modules/map";
import user from "./modules/user";
import tagsView from "./modules/tagsView";
import permission from "./modules/permission";
import settings from "./modules/settings";
import getters from "./getters";
import mapUrl from "./modules/mapUrl";
import CreateStore from "../../store/index.js";

Vue.use(Vuex);

export default CreateStore({
  modules: {
    app,
    map,
    user,
    tagsView,
    permission,
    settings,
    mapUrl
  },
  getters
});
