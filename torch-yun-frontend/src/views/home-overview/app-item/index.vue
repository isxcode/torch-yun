<template>
    <LoadingPage
        :visible="loading"
        :network-error="networkError"
        @loading-refresh="getAppItemList(false)"
    >
        <div class="ai-app-list">
            <div class="zqy-seach">
                <span class="app-label">我的应用</span>
                <el-input
                    v-model="keyword"
                    placeholder="请输入搜索条件 回车进行搜索"
                    clearable :maxlength="200"
                    @input="inputEvent"
                    @keyup.enter="handleCurrentChange(1)" />
            </div>
            <div class="ai-app-item">
                <template v-if="appItemList && appItemList.length">
                    <div class="app-item-container">
                        <div
                            class="app-item"
                            v-for="app in appItemList"
                            :key="app.id"
                            @click="clickAppEvent(app)"
                        >
                            <div class="app-logo">
                                <el-avatar
                                    :style="{
                                        color: '#FFFFFF',
                                        'background-color': '#fde3d0'
                                    }"
                                    :icon="UserFilled"
                                    :size="30"
                                />
                            </div>
                            <div class="app-name">
                                <EllipsisTooltip :label="app.name" />
                            </div>
                        </div>
                        <template v-for="card in appItemEmptyList">
                            <div class="app-item app-item__empty"></div>
                        </template>
                    </div>
                    <el-pagination
                        background
                        layout="prev, pager, next"
                        :total="pagination.total"
                        class="mt-4"
                        @size-change="handleCurrentChange"
                    />
                </template>
                <template v-else>
                    <EmptyPage />
                </template>
            </div>
        </div>
    </LoadingPage>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref, defineEmits, computed } from 'vue'
import { QueryAppList } from '@/services/app-management.service';
import LoadingPage from '@/components/loading/index.vue'
import { UserFilled } from '@element-plus/icons-vue'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'
import EmptyPage from '@/components/empty-page/index.vue'

const emit = defineEmits(['clickAppEvent'])

// 应用部分loading
const loading = ref(false)
const networkError = ref(false)
const keyword = ref<string>('')
const pagination = reactive<{
    currentPage: number
    pageSize: number
    total: number
}>({
    currentPage: 1,
    pageSize: 16,
    total: 0
})
const appItemList = ref<any[]>([])   // 机器人（应用）

const appItemEmptyList = computed(() => {
    if (appItemList.value?.length > 4 && (appItemList.value?.length % 4)) {
        const length = 4 - appItemList.value?.length % 4
        return new Array(length)
    } else if (appItemList.value?.length < 4 && appItemList.value?.length > 0) {
        const length = 4 - appItemList.value?.length
        return new Array(length)
    } else {
        return []
    }
})

// 查询机器人
function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    QueryAppList({
        page: pagination.currentPage - 1,
        pageSize: pagination.pageSize,
        searchKeyWord: keyword.value
    }).then((res: any) => {
        appItemList.value = res.data.content
        pagination.total = res.data.totalElements
        loading.value = false
        networkError.value = false
    }).catch(() => {
        appItemList.value = []
        pagination.total = 0
        loading.value = false
        networkError.value = true
    })
}

function getAppItem() {
    let appItem = null
    if (appItemList.value && appItemList.value.length) {
        appItem = appItemList.value[0]
    }
    return appItem
}

function clickAppEvent(data: any) {
    emit('clickAppEvent', data)
}

function inputEvent(e: string) {
    if (e === '') {
        handleCurrentChange(1)
    }
}

function handleCurrentChange(e: number) {
    pagination.currentPage = e
    initData()
}

onMounted(() => {
    initData()
})

defineExpose({
    getAppItem
})
</script>

<style lang="scss">
 .ai-app-list {
    // 与输入框对齐：使用相同的宽度和边距
    width: 80%;
    margin: 0 auto;
    .zqy-seach {
        height: 50px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 20px;
        box-sizing: border-box;
        .app-label {
            font-size: 14px;
            color: getCssVar('color', 'info')
        }
        .el-input {
            width: 260px;
        }
    }
    .ai-app-item {
        .app-item-container {
            display: flex;
            justify-content: space-between;
            flex-wrap: wrap;
            max-height: calc(50vh - 28px);
            overflow: auto;
            padding: 0 20px;
            box-sizing: border-box;
            .app-item {
                height: 60px;
                width: 24%;
                border: 1px solid getCssVar('color', 'primary', 'light-9');
                border-radius: 8px;
                margin-top: 12px;
                margin-bottom: 12px;
                display: inline-flex;
                align-items: center;
                padding: 0 20px;
                box-sizing: border-box;
                cursor: pointer;
                transition: all 0.15s linear;
                .app-name {
                    font-size: 12px;
                    margin-left: 8px;
                }
                &:hover {
                    box-shadow: getCssVar('box-shadow', 'lighter');
                    transition: all 0.15s linear;
                }
                &.app-item__empty {
                    opacity: 0;
                    cursor: default;
                    pointer-events: none;
                }
            }
        }
        .el-pagination {
            display: flex;
            justify-content: flex-end;
            margin: 20px 0;
            padding: 0 20px;
            box-sizing: border-box;
        }
    }
}
</style>