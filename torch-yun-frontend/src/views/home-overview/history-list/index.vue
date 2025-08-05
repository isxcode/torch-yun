<template>
    <BlockDrawer :drawer-config="drawerConfig">
        <div class="search-container">
            <el-input
                v-model="info.searchKeyWord"
                placeholder="请输入搜索条件 回车进行搜索"
                clearable
                :maxlength="200"
                @input="inputEvent"
                @keyup.enter="handleCurrentChange(1)"
            />
        </div>
        <LoadingPage
            class="zqy-loading__home"
            :visible="loading"
            :network-error="networkError"
            @loading-refresh="handleCurrentChange(1)"
        >
            <el-scrollbar>
                <div class="chat-history-list">
                    <div
                        class="history-item"
                        v-for="(item, index) in historyList"
                        :key="index"
                        @click="clickItemEvent(item)"
                    >
                        <div class="item-name"><EllipsisTooltip :label="item?.chatContent?.content" /></div>
                        <div class="item-time">{{ item?.createDateTime }}</div>
                    </div>
                </div>
            </el-scrollbar>
            <div class="pagination-container">
                <el-pagination
                    background
                    layout="prev, pager, next"
                    :total="total"
                    class="mt-4"
                    @size-change="handleCurrentChange"
                />
            </div>
        </LoadingPage>
    </BlockDrawer>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref, defineEmits } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import BlockDrawer from '@/components/block-drawer/index.vue'
import { GetHistoryChatList } from '@/services/ai-cheat.service.ts'
import LoadingPage from '@/components/loading/index.vue'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'

interface queryParam {
    page: number
    pageSize: number
    searchKeyWord: string
    appId?: string | null
}

const emit = defineEmits(['historyClickEvent'])

const loading = ref<boolean>(false)
const networkError = ref<boolean>(false)
const info = ref<queryParam>({
    page: 0,
    pageSize: 20,
    searchKeyWord: '',
    appId: ''
})
const total = ref<number>(0)
const historyList = ref<any[]>([])
const drawerConfig = reactive({
    title: '历史记录',
    visible: false,
    width: '460',
    customClass: 'ai-history-drawer',
    cancelConfig: {
        title: '关闭',
        cancel: closeEvent,
        disabled: false,
    },
    zIndex: 1100,
    closeOnClickModal: false,
});

function showModal(data: queryParam) {
    historyList.value = []
    info.value = data
    handleCurrentChange(1)
    drawerConfig.visible = true;
}

function getChatHistory() {
    loading.value = true
    GetHistoryChatList(info.value).then((res: any) => {
        historyList.value = res.data.content || []
        total.value = res.data.totalElements
        loading.value = false
        networkError.value = false
    }).catch(() => {
        loading.value = false
        networkError.value = false
        historyList.value = []
        total.value = 0
    })
}

function clickItemEvent(item: any) {
    emit('historyClickEvent', item)
    closeEvent()
}

function inputEvent(e: string) {
    if (e === '') {
        handleCurrentChange(1)
    }
}

function handleCurrentChange(e: number) {
    info.page = e
    getChatHistory()
}

function closeEvent() {
    drawerConfig.visible = false;
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.ai-history-drawer {
    .search-container {
        padding: 12px 20px;
        box-sizing: border-box;
    }
    .el-scrollbar {
        height: calc(100vh - 216px);
        .el-scrollbar__view {
            height: 100%;
        }
    }
    .zqy-loading {
        height: calc(100vh - 156px);
    }
    .chat-history-list {
        padding: 0 20px;
        .history-item {
            height: 60px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            padding: 8px 4px;
            box-sizing: border-box;
            border-bottom: 1px solid getCssVar('border-color');
            &:hover {
                cursor: pointer;
                background-color: getCssVar('color', 'primary', 'light-8');
            }
            .item-name {
                font-size: 13px;
                color: getCssVar('text-color', 'primary');
            }
            .item-time {
                font-size: getCssVar('font-size', 'extra-small');
                color: getCssVar('color', 'info');
            }
        }
    }
    .pagination-container {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        height: 60px;
        padding: 0 20px;
        box-sizing: border-box;
    }
}
</style>
