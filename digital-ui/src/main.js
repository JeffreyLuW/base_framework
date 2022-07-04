//程序入口.在app.js中自定义引入和配置。
//import './platform/main.js';
let layout = 2;
console.log('main layout:',layout);
if(layout == 1){
    import ('./platform/main1.js');
    //layout2 经典布局  layout1 流行模板布局
    window.__union_layout = "layout1";//layout2 layout1
}else{
    import ('./platform/main2.js');
    window.__union_layout = "layout2";
}

