/**************************************************
 * dom-drag.js
 * 09.25.2001
 * www.youngpup.net
 * Script featured on Dynamic Drive (http://www.dynamicdrive.com) 12.08.2005
 **************************************************
 * 10.28.2001 - fixed minor bug where events
 * sometimes fired off the handle, not the root.
 **************************************************/

var Drag = {

    obj: null,
    // o=>handler  oRoot => real movedObject  oRoot不提供，则默认移动o
    // initFn(o,o.root, Drag); 允许在实际拖拽时再设置值! 保证设置的值更加准确. 可以设置 o.initOpts.init=true，避免每次拖动时初始化。o.initOpts.canceled=true则不再能拖动
    // initFn返回的options=initOpts: {canceled,autoInWindow,}
    // canceled 取消移动   autoInWindow 自动根据win窗口设置拖动边界
    init: function(o, oRoot, initFn, minX, maxX, minY, maxY, bSwapHorzRef, bSwapVertRef, fXMapper, fYMapper, startResizeFn, endtResizeFn) {
        o.onmousedown = Drag.start;
        o.root = oRoot && oRoot != null ? oRoot : o;
        o.initFn = initFn;
        o.startResizeFn = startResizeFn;
        o.endResizeFn = endtResizeFn;

        o.initOpts = {
            init: false,
            canceled: false, //取消移动
            minX: minX,
            maxX: maxX,
            minY: minY,
            maxY: maxY,
            autoInWindow: false, //自动根据 window 计算 oRoot 移动边界，限定在window范围内
            bSwapHorzRef: bSwapHorzRef,
            bSwapVertRef: bSwapVertRef,
            fXMapper: fXMapper,
            fYMapper: fYMapper
        };

    },
    //重新设置区域。
    resetAreaByWindow(o) {
        this.resetArea(o,
            0,
            window.innerWidth - o.root.offsetWidth,
            0,
            window.innerHeight - o.root.offsetHeight);
    },

    //重新设置区域。
    resetArea(o, minX, maxX, minY, maxY) {
        if (typeof minX === "number") o.minX = minX;
        if (typeof maxX === "number") o.maxX = maxX;
        if (typeof minY === "number") o.minY = minY;
        if (typeof maxY === "number") o.maxY = maxY;
    },

    lazyInit(o) {
        // flag, init once ？？？
        if (o.initOpts.init) {
            return;
        }
        // o.initOpts.init = true;
        // if initFn exists, use it! you can use it to create new opts!
        if (o.initFn) {
            var newOptions = o.initFn(o, o.root, Drag);
            if (newOptions) {
                for (var k in newOptions) {
                    o.initOpts[k] = newOptions[k];
                }
            }
        }
        var minX = o.initOpts.minX;
        var maxX = o.initOpts.maxX;
        var minY = o.initOpts.minY;
        var maxY = o.initOpts.maxY;
        var bSwapHorzRef = o.initOpts.bSwapHorzRef;
        var bSwapVertRef = o.initOpts.bSwapVertRef;
        var fXMapper = o.initOpts.fXMapper;
        var fYMapper = o.initOpts.fYMapper;

        o.hmode = bSwapHorzRef ? false : true;
        o.vmode = bSwapVertRef ? false : true;

        if (o.hmode && isNaN(parseInt(o.root.style.left))) o.root.style.left = "0px";
        if (o.vmode && isNaN(parseInt(o.root.style.top))) o.root.style.top = "0px";
        if (!o.hmode && isNaN(parseInt(o.root.style.right))) o.root.style.right = "0px";
        if (!o.vmode && isNaN(parseInt(o.root.style.bottom))) o.root.style.bottom = "0px";

        o.minX = typeof minX != 'undefined' ? minX : null;
        o.minY = typeof minY != 'undefined' ? minY : null;
        o.maxX = typeof maxX != 'undefined' ? maxX : null;
        o.maxY = typeof maxY != 'undefined' ? maxY : null;

        //如果是自动设置边界
        if (o.initOpts.autoInWindow) {
            this.resetAreaByWindow(o);
        }
        o.xMapper = fXMapper ? fXMapper : null;
        o.yMapper = fYMapper ? fYMapper : null;

        o.root.onDragStart = new Function();
        o.root.onDragEnd = new Function();
        o.root.onDrag = new Function();
    },
    start: function(e) {
        var o = Drag.obj = this;
        o.startResizeFn()
        Drag.lazyInit(o);
        e = Drag.fixE(e);
        var y = parseInt(o.vmode ? o.root.style.top : o.root.style.bottom);
        var x = parseInt(o.hmode ? o.root.style.left : o.root.style.right);
        o.root.onDragStart(x, y);

        o.lastMouseX = e.clientX;
        o.lastMouseY = e.clientY;
        //记录初始位置
        o.mStart = {
            x: x,
            y: y,
            ex: e.clientX,
            ey: e.clientY
        };

        if (o.hmode) {
            if (o.minX != null) o.minMouseX = e.clientX - x + o.minX;
            if (o.maxX != null) o.maxMouseX = o.minMouseX + o.maxX - o.minX;
        } else {
            if (o.minX != null) o.maxMouseX = -o.minX + e.clientX + x;
            if (o.maxX != null) o.minMouseX = -o.maxX + e.clientX + x;
        }

        if (o.vmode) {
            if (o.minY != null) o.minMouseY = e.clientY - y + o.minY;
            if (o.maxY != null) o.maxMouseY = o.minMouseY + o.maxY - o.minY;
        } else {
            if (o.minY != null) o.maxMouseY = -o.minY + e.clientY + y;
            if (o.maxY != null) o.minMouseY = -o.maxY + e.clientY + y;
        }

        document.onmousemove = Drag.drag;
        document.onmouseup = Drag.end;

        return false;
    },

    drag: function(e) {

        e = Drag.fixE(e);
        var o = Drag.obj;


        // 如果取消了，则不再移动
        if (o.initOpts.canceled) {
            return false;
        }

        var ey = e.clientY;
        var ex = e.clientX;
        var y = parseInt(o.vmode ? o.root.style.top : o.root.style.bottom);
        var x = parseInt(o.hmode ? o.root.style.left : o.root.style.right);
        var nx, ny;

        if (o.minX != null) ex = o.hmode ? Math.max(ex, o.minMouseX) : Math.min(ex, o.maxMouseX);
        if (o.maxX != null) ex = o.hmode ? Math.min(ex, o.maxMouseX) : Math.max(ex, o.minMouseX);
        if (o.minY != null) ey = o.vmode ? Math.max(ey, o.minMouseY) : Math.min(ey, o.maxMouseY);
        if (o.maxY != null) ey = o.vmode ? Math.min(ey, o.maxMouseY) : Math.max(ey, o.minMouseY);

        nx = o.mStart.x + ((ex - o.mStart.ex) * (o.hmode ? 1 : -1));
        ny = o.mStart.y + ((ey - o.mStart.ey) * (o.vmode ? 1 : -1));

        if (o.xMapper) nx = o.xMapper(y);
        else if (o.yMapper) ny = o.yMapper(x);

        Drag.obj.root.style[o.hmode ? "left" : "right"] = nx + "px";
        Drag.obj.root.style[o.vmode ? "top" : "bottom"] = ny + "px";
        Drag.obj.lastMouseX = ex;
        Drag.obj.lastMouseY = ey;

        Drag.obj.root.onDrag(nx, ny);
        return false;
    },

    end: function() {
        var o = Drag.obj;
        o.endResizeFn()
        document.onmousemove = null;
        document.onmouseup = null;
        Drag.obj.root.onDragEnd(parseInt(Drag.obj.root.style[Drag.obj.hmode ? "left" : "right"]),
            parseInt(Drag.obj.root.style[Drag.obj.vmode ? "top" : "bottom"]));
        Drag.obj = null;

    },

    fixE: function(e) {
        if (typeof e == 'undefined') e = window.event;
        if (typeof e.layerX == 'undefined') e.layerX = e.offsetX;
        if (typeof e.layerY == 'undefined') e.layerY = e.offsetY;
        return e;
    }
};

export default Drag;