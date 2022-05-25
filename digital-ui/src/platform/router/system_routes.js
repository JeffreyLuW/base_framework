import Layout from './layout';

/**
 * 系统相关的路由
 * 这里的路由和菜单有一定对应关系(非强一致性，只需要路径匹配即可)。
 * meta中的 shouldInMenuList: true,//标记只有用户菜单中有才可以进入。
 * @Date 2020-8-18 调整原若依的强依赖关系
 */
export default [
  {
    "name": "System",
    "path": "/system",
    "component": Layout,
    "meta": {
      shouldInMenuList: true,
    },
    "children": [
      {
        "name": "User",
        "path": "user",
        "component": () => import("../views/system/user/index")
      },
      {
        "name": "Role",
        "path": "role",
        "component": () => import("../views/system/role/index"),
      },
      {
        "name": "Menu",
        "path": "menu",
        "component": () => import("../views/system/menu/index"),
      },
      {
        "name": "Dept",
        "path": "dept",
        "component": () => import("../views/system/dept/index"),
      },
      {
        "name": "Post",
        "path": "post",
        "component": () => import("../views/system/post/index"),
      },
      {
        "name": "Dict",
        "path": "dict",
        "component": () => import("../views/system/dict/index"),
      },
      {
        "name": "Config",
        "path": "config",
        "component": () => import("../views/system/config/index"),
      },

      {
        "name": "Notice",
        "path": "notice",
        "component": () => import("../views/system/notice/index"),
      },
      {
        "name": "Log",
        "path": "log",
        "component": () => import("../views/system/log/index"),

        "children": [
          {
            "name": "Operlog",
            "path": "operlog",
            "component": () => import("../views/monitor/operlog/index"),
          },
          {
            "name": "Logininfor",
            "path": "logininfor",
            "component": () => import("../views/monitor/logininfor/index"),
          }
        ]
      }
    ]
  },
  {
    "name": "Monitor",
    "path": "/monitor",
    "component": Layout,
    "meta": {
      shouldInMenuList: true
    },
    "children": [
      {
        "name": "Online",
        "path": "online",
        "component": () => import("../views/monitor/online/index")
      },
      {
        "name": "Job",
        "path": "job",
        "component": () => import("../views/monitor/job/index"),
      },
      {
        "name": "Druid",
        "path": "druid",
        "component": () => import("../views/monitor/druid/index"),
      },
      {
        "name": "Server",
        "path": "server",
        "component": () => import("../views/monitor/server/index"),
      }
    ]
  },
  {
    "name": "Tool",
    "path": "/tool",
    "component": Layout,
    "meta": {
      shouldInMenuList: true
    },
    "children": [
      {
        "name": "Build",
        "path": "build",
        "component": () => import("../views/tool/build/index")
      },
      {
        "name": "Gen",
        "path": "gen",
        "component": () => import("../views/tool/gen/index")
      },
      {
        "name": "Swagger",
        "path": "swagger",
        "component": () => import("../views/tool/swagger/index")
      }
    ]
  }
];
