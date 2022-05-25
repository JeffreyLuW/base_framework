import Pagination from "../Pagination";
import RightToolbar from "../RightToolbar"
import Jsx from "../Jsx";
import DataFormItem from "../DataFormItem";
import DataButton from "../DataButton";
import FormToolsRow from "../FormToolsRow";
import AutoDialog from "../AutoDialog";
import DataRadio from "../DataRadio";
import DataCheckbox from "../DataCheckbox";
import DataSelect from "../DataSelect";
import RawEcharts from "../RawEcharts";
import DataUpload from "../DataUpload";
import DataEditForm from "../DataEditForm";
import TableLayout from "../TableLayout";
import FormItemLabel from '../TableLayout/FormItemLabel.vue';
import FormTableLayout from '../TableLayout/FormTableLayout.vue';
import DataFooter from '../DataFooter/index.vue';
import Win from '../Win/index.vue';


export default {
  install(Vue) {
    Vue.component('Pagination', Pagination);
    Vue.component('RightToolbar', RightToolbar);
    Vue.component('Jsx', Jsx);
    Vue.component('DataFormItem', DataFormItem);
    Vue.component('DataButton', DataButton);
    Vue.component('FormToolsRow', FormToolsRow);
    Vue.component('AutoDialog', AutoDialog);
    Vue.component('DataRadio', DataRadio);
    Vue.component('DataCheckbox', DataCheckbox);
    Vue.component('DataSelect', DataSelect);
    Vue.component('RawEcharts', RawEcharts);
    Vue.component('DataUpload', DataUpload);  // 这里IE下不兼容，需要排查
    //用于简单通用的表单展示
    Vue.component('DataEditForm', DataEditForm);
    //table布局 通过配置实现快速table布局
    Vue.component('TableLayout', TableLayout);
    Vue.component('FormTableLayout', FormTableLayout);
    Vue.component("FormItemLabel", FormItemLabel);
    Vue.component("DataFooter", DataFooter);
    Vue.component("Win", Win);
  }
}
