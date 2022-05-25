/* Layout */
import Layout from '@/platform/layout1'
import Layout2 from '@/platform/layout2'
//可以通过上面 Layout 的切换调整总体布局
let defaultLayout = window.__union_layout === "layout2" ? Layout2 : Layout;
export default defaultLayout;
