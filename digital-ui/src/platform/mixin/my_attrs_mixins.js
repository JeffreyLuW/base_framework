//目的是替换$attrs每次都会导致render的问题。优化属性变化，只有实际值变化时才会导致重新渲染。
const emptyObject = {};
export default {
    data() {
        return {
            my_attrs: this.$attrs||emptyObject,
            my_listeners: this.$listeners||emptyObject,
        };
    },
    watch: {
        $attrs(n, o) {
            if (_.isEqual(n, o)) return;
            this.my_attrs = n||emptyObject;
        },
        $listeners(n, o) {
            if (_.isEqual(n, o)) return;
            this.my_listeners = n||emptyObject;
        },
    },
}