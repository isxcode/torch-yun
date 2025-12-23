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
                                        'background-color': getAvatarColor(app.name)
                                    }"
                                    :size="36"
                                >
                                    {{ getAvatarText(app.name) }}
                                </el-avatar>
                            </div>
                            <div class="app-info">
                                <div class="app-name">
                                    <EllipsisTooltip :label="app.name" />
                                </div>
                                <div class="app-desc" v-if="app.remark">
                                    <EllipsisTooltip :label="app.remark" />
                                </div>
                            </div>
                            <div class="app-arrow">
                                <el-icon><ArrowRight /></el-icon>
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
import { ArrowRight } from '@element-plus/icons-vue'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'
import EmptyPage from '@/components/empty-page/index.vue'

const emit = defineEmits(['clickAppEvent'])

// 头像颜色列表
const avatarColors = [
    '#409EFF', // 蓝色
    '#67C23A', // 绿色
    '#E6A23C', // 橙色
    '#F56C6C', // 红色
    '#909399', // 灰色
    '#9B59B6', // 紫色
    '#1ABC9C', // 青色
    '#3498DB', // 天蓝色
]

// 根据应用名称生成头像颜色
function getAvatarColor(name: string): string {
    if (!name) return avatarColors[0]
    let hash = 0
    for (let i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash)
    }
    return avatarColors[Math.abs(hash) % avatarColors.length]
}

// 获取头像显示文字（取名称前两个字符）
function getAvatarText(name: string): string {
    if (!name) return ''
    return name.substring(0, 2)
}

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
        padding: 0 5px;
        box-sizing: border-box;
        .app-label {
            font-size: 16px;
            font-weight: 500;
            color: getCssVar('text-color', 'primary');
        }
        .el-input {
            width: 260px;
            .el-input__wrapper {
                border-radius: 20px;
            }
        }
    }
    .ai-app-item {
        .app-item-container {
            display: flex;
            justify-content: flex-start;
            flex-wrap: wrap;
            gap: 16px;
            max-height: calc(50vh - 28px);
            overflow: auto;
            padding: 8px 4px;
            box-sizing: border-box;
            .app-item {
                height: 72px;
                width: calc(25% - 12px);
                min-width: 200px;
                background: #ffffff;
                border: 1px solid #e8eaed;
                border-radius: 12px;
                display: inline-flex;
                align-items: center;
                padding: 0 16px;
                box-sizing: border-box;
                cursor: pointer;
                transition: all 0.2s ease;
                position: relative;
                overflow: hidden;

                &::before {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: linear-gradient(135deg, rgba(64, 158, 255, 0.03) 0%, rgba(64, 158, 255, 0.08) 100%);
                    opacity: 0;
                    transition: opacity 0.2s ease;
                }

                .app-logo {
                    flex-shrink: 0;
                    z-index: 1;
                    .el-avatar {
                        font-size: 14px;
                        font-weight: 500;
                        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                    }
                }

                .app-info {
                    flex: 1;
                    margin-left: 12px;
                    min-width: 0;
                    z-index: 1;
                    .app-name {
                        font-size: 14px;
                        font-weight: 500;
                        color: getCssVar('text-color', 'primary');
                        line-height: 1.4;
                    }
                    .app-desc {
                        font-size: 12px;
                        color: getCssVar('text-color', 'secondary');
                        margin-top: 4px;
                        line-height: 1.3;
                    }
                }

                .app-arrow {
                    flex-shrink: 0;
                    color: getCssVar('text-color', 'placeholder');
                    transition: all 0.2s ease;
                    z-index: 1;
                    .el-icon {
                        font-size: 14px;
                    }
                }

                &:hover {
                    border-color: getCssVar('color', 'primary', 'light-5');
                    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
                    transform: translateY(-2px);

                    &::before {
                        opacity: 1;
                    }

                    .app-arrow {
                        color: getCssVar('color', 'primary');
                        transform: translateX(3px);
                    }
                }

                &:active {
                    transform: translateY(0);
                    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.1);
                }

                &.app-item__empty {
                    opacity: 0;
                    cursor: default;
                    pointer-events: none;
                    border: none;
                    background: transparent;
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

// 响应式布局
@media screen and (max-width: 1200px) {
    .ai-app-list .ai-app-item .app-item-container .app-item {
        width: calc(33.33% - 11px);
    }
}

@media screen and (max-width: 900px) {
    .ai-app-list .ai-app-item .app-item-container .app-item {
        width: calc(50% - 8px);
    }
}

@media screen and (max-width: 600px) {
    .ai-app-list .ai-app-item .app-item-container .app-item {
        width: 100%;
    }
}
</style>