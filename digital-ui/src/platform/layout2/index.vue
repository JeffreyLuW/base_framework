<template>
  <div class="Layout2 theme-bg-pannel">
    <div class="top-nav" :style="topNavStyle">
      <!--logo 60px-->
      <img class="banner-logo" :src="logoUrl" alt="" />
      <!--右侧用户-->
      <div class="user-info">
        <!--用户头像-->
        <el-dropdown
          class="update-pwd-btn-dd"
          trigger="click"
          @command="handleUserOpCommand"
        >
          <img class="user-photo" :src="userPhoto" alt="头像" />
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="theme">
              <span>主题切换</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <div class="user-extra">
          <div class="user-name" :title="loginUserName">{{ loginUserName }}</div>
          <div class="user-op">
            <span class="exit-btn" @click="logout">退出</span>
            <span class="update-pwd-btn" @click="clickUserCenter">个人中心</span>
          </div>
        </div>
      </div>
      <!--顶部一级菜单-->
      <HScroll
        class="nav-menus"
        :step="75"
        :size="(menus && menus.length) || 0"
        height="100%"
        width="calc(100% - 540px)"
      >
        <div
          class="nav-menus-item"
          :class="{ active: rootMenuItem === item }"
          v-for="item in menus"
          :key="item._fullPath"
          @click="onClickTopNavMenuItem(item)"
        >
          <div>
            <svg-icon :icon-class="item.meta.icon"></svg-icon>
          </div>
          <div>
            {{ item.meta.title }}
          </div>
        </div>
      </HScroll>
    </div>
    <div class="left-side" :style="sideMenuStyle" id="layout2-left-side">
      <Jsx v-if="sideMenus" :jsx="menuJsx"></Jsx>
    </div>
    <div class="right-main" :style="rightMainStyle" id="layout2-right-main">
      <transition name="fade-transform">
        <router-view />
      </transition>
    </div>
    <!--主题切换-->
    <template v-if="isColorTheme">
      <AutoDialog
        :show.sync="showThemeDialog"
        title="修改主题"
        width="400px"
        :modal="false"
      >
        <div v-if="showThemeDialog" class="theme-dialog-inner-content">
          <span class="theme-dialog-label">请选择主题色:</span>
          <ThemePicker @change="themeChange"></ThemePicker>
        </div>
        <template slot="footer">
          <DataButton type="primary" text="关闭" @click="showThemeDialog = false" />
        </template>
      </AutoDialog>
    </template>
    <template v-else>
      <AutoDialog
        class="Layout2_theme_dialog"
        :show.sync="showThemeDialog"
        :show-footer="false"
        title="选择主题"
        width="400px"
        :modal="false"
      >
        <div v-if="showThemeDialog" class="theme-dialog-inner-content">
          <div
            class="theme-block"
            v-for="item in themeBlocks"
            :key="item.name"
            :style="{ background: item.vars.bgDark }"
            @click="onApplyNewTheme(item)"
            :title="item.name"
          >
            {{ item.name }}
          </div>
        </div>
      </AutoDialog>
    </template>
  </div>
</template>

<script lang="jsx">
import { mapGetters } from "vuex";
import { isExternal } from "../utils/validate.js";
import userDefaultPhoto from "../assets/image/photo.jpg";
import ThemePicker from "../components2/ThemePicker/index.vue";
import bannerLogoUrl from "../../assets/logo/banner-logo.png";
import HScroll from "./components/HScroll.vue";
import { getToken } from "@/platform/utils/auth";

export default {
  name: "Layout2",
  components: {
    ThemePicker,
    HScroll,
  },
  beforeCreate() {
    //记录当前的布局组件 其他组件可以查询该参数
    window.rootLayoutComponent = "Layout2";
  },
  data() {
    return {
      rootMenuItem: null,
      sideMenus: null, //侧边菜单。如果没有，则应该全屏。
      activeIndex: null,
      showThemeDialog: false,
      isColorTheme: window.__theme_color,
      forceCounter: 0,
    };
  },
  created() {
    this.$root.$on("themeChange", this.onThemeChange);
  },
  computed: {
    ...mapGetters(["menus", "rootMenuMap", "avatar", "loginUserName"]),//对象展开运输符将getter混入computed
    logoUrl() {
      let sysConfig = this.$store.state.settings.sysConfigs;
      let sysLogoConfig = sysConfig["page.sys.logo"];
      let configUrl = this.$util.getSysConfigParamUrl(sysLogoConfig);
      if (configUrl) {
        return configUrl;
      }
      return bannerLogoUrl;
    },
    //定制主题的色块
    themeBlocks() {
      let rs = [];
      if (this.$theme) {
        rs = Object.values(this.$theme.preparedTheme);
      }
      return rs;
    },
    defaultTheme() {
      return this.$store.state.settings.theme;
    },
    sideMenuStyle() {
      return {
        left: this.sideMenus && this.sideMenus.length ? "0" : "-200px",
      };
    },
    rightMainStyle() {
      return {
        left: this.sideMenus && this.sideMenus.length ? "200px" : "0",
      };
    },
    userPhoto() {
      if (this.avatar) {
        return this.avatar;
      }

      return userDefaultPhoto;
    },
    //导航栏的主题
    topNavStyle() {
      let background = null;
      if (window.__theme_color) {
        background = this.defaultTheme;
      }
      if (this.$theme) {
        background = this.$theme.currColorVars.bgDark;
      }
      this.forceCounter = this.forceCounter;
      return { background };
    },
  },
  watch: {
    $route: {
      immediate: true,
      handler(route) {
        let fullPath = route.fullPath;
        this.rootMenuItem = this.rootMenuMap[fullPath];
        let sideMenus = (this.rootMenuItem && this.rootMenuItem.children) || null;
        if (sideMenus) {
          // let leaf = this.$util.treeFirstLeaf(sideMenus);
          this.activeIndex = fullPath; // leaf && leaf._fullPath;
        } else {
          this.activeIndex = null;
        }
        this.sideMenus = sideMenus;
      },
    },
  },
  methods: {
    clickUserCenter() {
      this.$router.push("/user/profile");
    },
    logout() {
      this.$confirm("确定注销并退出系统吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        sessionStorage.removeItem("userId");
        this.$store.dispatch("LogOut").then(() => {
          // location.href = '/index';
          this.$router.push("/login");
        });
      });
    },
    //点击顶部导航菜单
    onClickTopNavMenuItem(item) {
      let path = item._fullPath;
      let hasChildren = item.children && item.children.length;
      if (!hasChildren) {
        if (isExternal(path)) {
          window.open(path, "_blank");
          //外部地址，当前页不变化。
          return;
        }
        //没有子 直接跳转
        this.sideMenus = null;
        this.goPath(path);
      } else {
        //有子，查询子的第一个路由。
        let firstLeaf = this.$util.treeFirstLeaf(item);
        this.sideMenus = item.children;
        this.goPath(firstLeaf._fullPath);
      }
    },
    goPath(path) {
      let token = getToken();
      if (!path) return;
      if (isExternal(path)) {
        window.open(`${path}?token=${token}`, "_blank");
      } else {
        this.$router.push(path);
      }
    },
    //点击菜单
    handleSelect(index, indexPath) {
      this.goPath(index);
    },
    //侧边菜单Jsx
    menuJsx() {
      let menuList = this.sideMenus;
      let createItem = (item) => {
        if (item.children && item.children.length) {
          return (
            <el-submenu index={item._fullPath}>
              <template slot="title">
                {item.meta.icon && <svg-icon icon-class={item.meta.icon}></svg-icon>}
                <span slot="title">{item.meta.title}</span>
              </template>
              {item.children.map(createItem)}
            </el-submenu>
          );
        } else {
          return (
            <el-menu-item index={item._fullPath}>
              {item.meta.icon && <svg-icon icon-class={item.meta.icon}></svg-icon>}
              <span slot="title">{item.meta.title}</span>
            </el-menu-item>
          );
        }
      };
      let list = menuList.map(createItem); //#7eb148 ffd04b
      //背景
      let background = this.topNavStyle.background;
      return (
        <el-menu
          default-active={this.activeIndex}
          class="el-menu-jsx"
          onSelect={this.handleSelect}
          background-color={background}
          text-color="#fff"
          collapse-transition={true}
          active-text-color="#ffd04b"
          key={this.rootMenuItem && this.rootMenuItem._fullPath}
        >
          {list}
        </el-menu>
      );
    },
    //点击右上角用户头像下拉
    handleUserOpCommand(command) {
      if ("theme" === command) {
        this.showThemeDialog = true;
      }
    },
    //定制皮肤修改
    onApplyNewTheme(item) {
      this.$theme.genNewStyle(item.key);
      this.onThemeChange();
    },
    onThemeChange() {
      this.forceCounter++;
    },
    //改变主题
    themeChange(val) {
      this.$store.dispatch("settings/changeSetting", {
        key: "theme",
        value: val,
      });
    },
  },
  beforeDestroy() {
    this.$root.$off("themeChange", this.onThemeChange);
  },
};
</script>

<style scoped lang="scss">
.Layout2 {
  position: absolute;
  left: 0;
  width: 100%;
  height: 100%;
  top: 0;
  overflow: hidden;

  .top-nav {
    height: 60px;
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    background: #2661a7;
    color: white;
    font-size: 12px;
    z-index: 1;

    .banner-logo {
      height: 60px;
    }

    box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  }

  .left-side {
    position: absolute;
    left: 0px;
    width: 200px;
    top: 60px;
    bottom: 0;
    background: #2661a7;
    transition: left ease 0.25s;

    .el-menu-jsx {
      height: 100%;
      width: 100%;
      overflow-y: auto;
      overflow-x: hidden;
    }
  }

  .right-main {
    position: absolute;
    right: 0;
    top: 60px;
    left: 200px;
    bottom: 0;
    transition: left ease 0.25s;
    overflow: auto;
  }

  .nav-menus {
    float: right;
    position: relative;
    height: 100%;
    box-sizing: border-box;
    overflow: hidden;
    min-width: 80px;
    margin-right: 0px;
    max-width: calc(100% - 540px);

    &:after {
      content: "";
      clear: both;
    }

    .nav-menus-item {
      float: left;
      min-width: 70px;
      box-sizing: border-box;
      height: 100%;
      cursor: pointer;
      text-align: center;
      padding: 10px 8px 0 8px;

      &:hover,
      &.active {
        background: #7eb148;
      }
    }
  }

  .user-info {
    float: right;
    position: relative;
    height: 100%;
    box-sizing: border-box;
    padding: 1px 1px 1px 14px;
    width: 160px;

    &:before {
      content: "";
      display: block;
      width: 1px;
      background: white;
      height: 40px;
      position: absolute;
      left: 0;
      top: 10px;
      cursor: auto;
      opacity: 0.5;
    }
  }

  .user-photo {
    border-radius: 50%;
    width: 40px;
    height: 40px;
    margin-top: 10px;
    display: inline-block;
    margin-right: 10px;
  }

  .user-extra {
    display: inline-block;
    vertical-align: top;

    .user-name {
      margin-bottom: 10px;
      margin-top: 13px;
      width: 75px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .exit-btn,
    .update-pwd-btn {
      cursor: pointer;

      &:hover {
        text-decoration: underline;
      }
    }

    .exit-btn {
      margin-right: 6px;
    }
  }
}
</style>
<style lang="scss">
.Layout2 {
  .top-nav .svg-icon {
    width: 22px !important;
    height: 22px !important;
    margin-bottom: 6px;
  }

  .left-side .svg-icon {
    vertical-align: middle;
    margin-right: 6px;
  }

  .right-main > div {
    position: absolute;
    left: 0;
    width: 100%;
    height: 100%;
    top: 0;
    overflow: auto;
  }
}

.Layout2_theme_dialog .el-dialog__body {
  background: rgba(255, 255, 255, 0.2);
}

.theme-dialog-inner-content {
  text-align: left;

  .theme-dialog-label {
    margin-right: 10px;
    position: relative;
    top: -10px;
  }

  .theme-block {
    width: 100px;
    height: 100px;
    line-height: 100px;
    display: inline-block;
    margin-left: 56px;
    text-align: center;
    border-radius: 10px;
    cursor: pointer;
    margin-bottom: 10px;
    opacity: 0.9;
    transition: all 0.35s ease;
    color: white;

    &:hover {
      opacity: 1;
    }
  }
}
</style>
