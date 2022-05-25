var baseApply = function (control, options, children, ignoredKey) {
  if (!ignoredKey) ignoredKey = {name: true};
  var styles = options.style;
  if (styles) {
    if (!(styles instanceof Array)) {
      styles = [styles];
    }
    styles.forEach(function (style) {
      for (var key in style) {
        if (ignoredKey[key]) continue;
        if ('style' === key) {
          continue;
        }
        control[key] = style[key];
      }
    });
  }
  for (var key in options) {
    if (ignoredKey[key]) continue;
    if ('style' === key) {
      continue;
    }
    control[key] = options[key];
  }
  if (children) {
    children.forEach(function (c) {
      control.addControl(c);
    });
  }
};

//后期也可以扩展自己的样式组件，方便拼接
//目的可以形成ast形式的代码
//具体参数设置，参考 https://doc.babylonjs.com/how_to/gui#displaygrid
//大部分的组件，尽量应该有一个指定的宽高值。否则布局容易计算错误.
//StackPanel 横向时，child的宽度应该指定；竖向时，子高度应指定
//推荐style|options单独写，width|height可以直接写到options更直观。
export default {
  patchEngine: function (engine, canvas) { //gui使用的时候，有时会报错，手动fix一下
    engine.getInputElement = function () {
      return canvas;
    };
  },
  getUIMgr: function (scene) {
    var ui = scene._ui = scene._ui || BABYLON.GUI.AdvancedDynamicTexture.CreateFullscreenUI('ui');
    return ui;
  },
  Grid: function (options, children) { // children 需要自己设定row col属性，指定放入到哪个cell
    var grid = new BABYLON.GUI.Grid(options.name);
    var cols = options.cols || [100, 100];//列数和列宽定义
    var rows = options.rows || 2;//行数和行定义 如果为数字，则为行数，高度为 1/n
    if (typeof rows === 'number') {
      var hpercent = 1 / rows;
      var count = rows;
      rows = [];
      for (var i = 0; i < count; i++) {
        rows.push(hpercent);
      }
    }
    cols.forEach(function (v) {
      grid.addColumnDefinition(v, v > 1);
    });
    rows.forEach(function (v) {
      grid.addRowDefinition(v, v > 1);
    });
    baseApply(grid, options);
    //处理children
    if (children) {
      children.forEach(function (c) {
        grid.addControl(c, c.row, c.col);
      });
    }
    return grid;
  },

  StackPanel: function (options, children) {
    var panel = new BABYLON.GUI.StackPanel(options.name);
    baseApply(panel, options, children);
    return panel;
  },
  Rectangle: function (options, children) { //自动布局较弱，该组件用于绝对布局，可能更轻松些
    //cornerRadius thickness
    var btn = new BABYLON.GUI.Rectangle(options.name);
    baseApply(btn, options, children);
    return btn;
  },
  Button: function (options, children) {
    var button = new BABYLON.GUI.Button(options.name);
    if (options.click)
      button.onPointerUpObservable.add(options.click);
    baseApply(button, options, children, {name: true, click: true});
    return button;
  },
  SimpleButton: function (options, children) {
    var button = BABYLON.GUI.Button.CreateSimpleButton(options.name, options.text);
    if (options.click)
      button.onPointerUpObservable.add(options.click);
    baseApply(button, options, children, {name: true, text: true, click: true});
    return button;
  },
  Image: function (options, children) {
    var img = new BABYLON.GUI.Image(options.name, options.url);
    baseApply(img, options, children, {name: true, url: true});
    return img;
  },
  TextBlock: function (options, children) { // horizontalAlignment  verticalAlignment
    var text = new BABYLON.GUI.TextBlock(options.name);
    baseApply(text, options, children);
    return text;
  },
  //从某个root control遍历，根据name形成缓存的控件map，方便引用。
  findNameMap: function (control, map) {
    var self = this;
    map = map || {};
    if (control.name) {
      map[control.name] = control;
    }
    if (control.children) {
      control.children.forEach(function (c) {
        return this.findNameMap(c, map)
      }.bind(this));
    }
    return map;
  }
};
