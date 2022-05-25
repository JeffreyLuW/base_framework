$(document).ready(function () {
  //菜单列表。支持多级嵌套
  var menuList = window.menuList;
  //初始化菜单
  initSideMenu(menuList, function (item) {
    //点击菜单时，加载对应的md文件
    loadMarkdown2Html(item.url);
  })
});

/**
 * 初始化侧边菜单
 * @param menuList [{title,children}]
 * @param onClickItem(item)
 */
function initSideMenu(menuList, onClickItem) {
  if (!onClickItem) onClickItem = function (item) {
    console.warn("no listener config! current menu item:", item)
  };

  function activeItem(item) {
    //移除 .active 当前节点添加active
    item._rootDom.find('.active').removeClass('active');
    item._dom.addClass("active");
    onClickItem(item);
  }

  var menuDom = $("<ul class=\"side-menu-ul\"></ul>");
  var leafDataList = [];
  eachTree(null, menuList, function (item, index, parent) {
    if (!parent) {
      item._level = 0;
    } else {
      parent._hasChildren = true;
      item._level = parent._level + 1;
    }
  });

  eachTree(null, menuList, function (item, index, parent) {
    //创建节点
    var itemDom = null;
    if (item._hasChildren) {
      itemDom = $("<li class=\"side-menu-li side-menu-group\"><div class=\"side-menu-group-title\">" + item.title + "</div><ul class=\"side-menu-ul\"></ul></li>");
    } else {
      leafDataList.push(item);
      itemDom = $("<li class=\"side-menu-li side-menu-leaf\">" + item.title + "</li>");
      //叶子节点添加点击事件
      itemDom.click(function () {
        activeItem(this);
      }.bind(item));
    }
    item._dom = itemDom;
    item._rootDom = menuDom;
    if (!parent) {
      //添加到根元素
      menuDom.append(itemDom);
    } else {
      parent._dom.find('ul.side-menu-ul').append(itemDom);
    }
  });

  if (leafDataList.length) {
    activeItem(leafDataList[0]);
  }
  $('.side-menu').append(menuDom);
}

/**
 * 遍历tree结构的集合
 * @param parent
 * @param menuList
 * @param callback
 */
function eachTree(parent, menuList, callback) {
  if (menuList) {
    menuList.forEach(function (item, index) {
      callback(item, index, parent);
      eachTree(item, item.children, callback);
    });
  }
}


// hljs.configure({useBR: true});
function loadMarkdown2Html(url) {
  if (!url) return;
  var dom = $("#doc-content");
  $.ajax({
    url: url,
    success: function (resp) {
      dom.hide();
      markdownToHtml(resp);
      dom.fadeIn();
    }
  });

  function markdownToHtml(md_content) {
    // var html_content = markdown.toHTML(md_content);
    var html_content = marked(md_content);
    dom.html(html_content);
    //语法高亮
    dom.find("pre code").each(function (index, block) {
      hljs.highlightBlock(block);
    });
    window.scrollTo(0, 0);
  }

}
