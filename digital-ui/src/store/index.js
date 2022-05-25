import Vuex from 'vuex'

export default function(options) {
    // 额外需要保存的模块
    // options.modules.test = {
    //     state,
    //     mutations
    // };
    return new Vuex.Store(options);
}

// const store = new Vuex.Store({
//     state: {
//         //存放的键值对就是所要管理的状态
//         name: 'helloVueX'
//     }
// })

// export default store