
<template>
    <div class="">
        <div class="__header__">
            <SelectYear @change-year="changeYear"></SelectYear>
            <el-button type="primary" style="margin-left: 10px">保存</el-button>
        </div>
        <div class="__content__">
            <!-- <div class="__left__">
                <div
                    v-for="i in 12"
                    :key="i"
                    @click="changeMonth(i)"
                    :class="[
                        '__item',
                        'theme-border-color3',
                        currentMonth == i
                            ? 'theme-bg-dark-light theme-text-color-custom-dark'
                            : 'theme-text-color-primary-light',
                    ]"
                >
                    {{ i }}月
                </div>
            </div> -->
            <div class="__right__">
                <el-table
                    id="table"
                    :stripe="true"
                    :data="tableData"
                    border
                    style="width: 100%"
                >
                    <el-table-column
                        type="index"
                        width="50"
                        label="序号"
                        align="center"
                    ></el-table-column>
                    <el-table-column
                        prop="a"
                        label="河段"
                        width="100"
                        align="center"
                    ></el-table-column>
                    <el-table-column
                        prop="b"
                        label="涵闸名称"
                        min-width="120"
                        align="left"
                    ></el-table-column>
                    <el-table-column
                        prop="e"
                        label="需水量"
                        width="120"
                        align="center"
                    ></el-table-column>
                    <el-table-column
                        prop="f"
                        label="申请时间"
                        width="120"
                        align="center"
                    ></el-table-column>
                    <el-table-column
                        prop="g"
                        label="状态"
                        width="120"
                        align="center"
                        ><template slot-scope="scope">
                            <span>{{
                                scope.row.g == 1 ? "草稿" : "审批"
                            }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="220" align="center">
                        <template slot-scope="scope">
                            <el-button
                                type="primary"
                                @click="addOrUpdateHandle(scope.row)"
                                size="small"
                                >编辑</el-button
                            >
                            <el-button type="danger" size="small"
                                >删除</el-button
                            >
                            <el-button type="primary" size="small"
                                >审核</el-button
                            >
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination
                    background
                    :current-page="page"
                    :page-sizes="[10, 20, 50, 100]"
                    :page-size="pageSize"
                    :total="total"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="pageSizeChangeHandle"
                    @current-change="pageCurrentChangeHandle"
                ></el-pagination>
            </div>
        </div>
    </div>
</template>

<script>
import SelectYear from "../../../__comom/SelectYear";
export default {
    data() {
        return {
            // 分页器属性
            page: 1,
            pageSize: 10,
            total: 0,
            tableData: [],
            currentMonth: 1,
        };
    },
    components: {
        SelectYear,
    },
    methods: {
        //分页器事件
        pageSizeChangeHandle(pageSize) {
            this.pageSize = pageSize;
            this.page = 1;
            this.fetchData();
        },
        pageCurrentChangeHandle(page) {
            this.page = page;
            this.fetchData();
        },
        changeYear(){},
        changeMonth(v) {
            this.currentMonth = v;
        },
    },
};
</script>

<style lang="scss" scoped>
.__header__ {
    display: flex;
}
.__content__ {
    display: flex;
    padding: 10px 0;
    width: 100%;
    .__left__ {
        width: 160px;
        .__item {
            margin: 5px;
            border-width: 1px;
            border-style: solid;
            cursor: pointer;
            height: 40px;
            line-height: 40px;
            text-align: center;
            font-weight: bold;
            font-size: 16px;
        }
    }
    .__right__ {
        width: calc(100% - 0px);
    }
}
</style>