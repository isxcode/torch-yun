<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="model-plaza">
        <div class="model-plaza__header">
            <div class="model-plaza__search">
                <el-input v-model="keyword" placeholder="搜索模型名称" clearable :maxlength="200"
                    :prefix-icon="Search" @input="inputEvent" @keyup.enter="initData(false)" />
            </div>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
            <div class="model-plaza__content">
                <el-row :gutter="16">
                    <el-col v-for="item in modelList" :key="item.id" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-tooltip :content="item.remark || '暂无备注'" placement="top" :show-after="500" popper-class="model-card-tooltip">
                            <div class="model-card" :class="{ 'model-card--offline': item.isOnline === 'DISABLE' }">
                                <div class="model-card__header">
                                    <div class="model-card__icon" :style="{ background: getOrgColor(item.orgName) }">
                                        {{ getOrgInitial(item.orgName) }}
                                    </div>
                                    <div class="model-card__status">
                                        <el-tag v-if="item.isOnline !== 'DISABLE'" type="success" size="small">在线模型</el-tag>
                                        <el-tag v-else type="info" size="small">离线模型</el-tag>
                                    </div>
                                </div>
                                <div class="model-card__body">
                                    <div class="model-card__name" :title="item.modelName">{{ item.modelName }}</div>
                                    <div class="model-card__org">{{ item.orgName }}</div>
                                </div>
                                <div class="model-card__footer">
                                    <div class="model-card__tags">
                                        <el-tag v-if="item.label" size="small" type="info" effect="plain">{{ formatLabel(item.label) }}</el-tag>
                                        <el-tag v-if="item.modelType" size="small" type="info" effect="plain">{{ formatModelType(item.modelType) }}</el-tag>
                                        <el-tag v-if="item.modelParam" size="small" type="warning" effect="plain">{{ item.modelParam }}</el-tag>
                                    </div>
                                </div>
                            </div>
                        </el-tooltip>
                    </el-col>
                </el-row>
                <div v-if="modelList.length === 0 && !loading" class="model-plaza__empty">
                    <el-empty description="暂无模型数据" />
                </div>
                <div class="model-plaza__pagination">
                    <el-pagination
                        v-model:current-page="pagination.currentPage"
                        v-model:page-size="pagination.pageSize"
                        :page-sizes="[12, 24, 48, 96]"
                        :total="pagination.total"
                        layout="total, sizes, prev, pager, next, jumper"
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                    />
                </div>
            </div>
        </LoadingPage>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { BreadCrumbList } from './list.config'
import { QueryModelPlazaList } from '@/services/model-plaza.service'

const keyword = ref<string>('')
const loading = ref<boolean>(false)
const networkError = ref<boolean>(false)
const modelList = ref<any[]>([])

const breadCrumbList = reactive(BreadCrumbList)
const pagination = reactive({
    currentPage: 1,
    pageSize: 12,
    total: 0
})

// 根据orgName生成颜色
const orgColorMap = new Map<string, string>()
const colorPalette = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)',
    'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
    'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)'
]

function getOrgInitial(orgName: string): string {
    if (!orgName) return '?'
    return orgName.charAt(0).toUpperCase()
}

function getOrgColor(orgName: string): string {
    if (!orgName) return colorPalette[0]
    if (!orgColorMap.has(orgName)) {
        const index = orgColorMap.size % colorPalette.length
        orgColorMap.set(orgName, colorPalette[index])
    }
    return orgColorMap.get(orgName) || colorPalette[0]
}

function formatLabel(label: string): string {
    const labelMap: Record<string, string> = {
        'Natural Language Processing': '自然语言处理',
        'Computer Vision': '计算机视觉',
        'Audio': '音频处理',
        'Multimodal': '多模态'
    }
    return labelMap[label] || label
}

function formatModelType(modelType: string): string {
    const typeMap: Record<string, string> = {
        'Text Generation': '文本生成',
        'Text Classification': '文本分类',
        'Image Classification': '图像分类',
        'Speech Recognition': '语音识别'
    }
    return typeMap[modelType] || modelType
}

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    QueryModelPlazaList({
        page: pagination.currentPage - 1,
        pageSize: pagination.pageSize,
        searchKeyWord: keyword.value
    }).then((res: any) => {
        modelList.value = res.data.content
        pagination.total = res.data.totalElements
        loading.value = false
        networkError.value = false
    }).catch(() => {
        modelList.value = []
        pagination.total = 0
        loading.value = false
        networkError.value = true
    })
}

function inputEvent(e: string) {
    if (e === '') {
        initData()
    }
}

function handleSizeChange(e: number) {
    pagination.pageSize = e
    initData()
}

function handleCurrentChange(e: number) {
    pagination.currentPage = e
    initData()
}

onMounted(() => {
    pagination.currentPage = 1
    pagination.pageSize = 12
    initData()
})
</script>

<style lang="scss">
.model-plaza {
    padding: 20px;
    height: calc(100% - 50px);
    display: flex;
    flex-direction: column;

    &__header {
        display: flex;
        justify-content: flex-end;
        margin-bottom: 20px;
    }

    &__search {
        width: 280px;
    }

    &__content {
        flex: 1;
        overflow-y: auto;
        overflow-x: hidden;
        padding-top: 4px;
    }

    &__empty {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 300px;
    }

    &__pagination {
        display: flex;
        justify-content: flex-end;
        margin-top: 20px;
        padding: 10px 0;
    }
}

.model-card {
    background: #fff;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 16px;
    border: 1px solid #e4e7ed;
    transition: all 0.3s ease;
    cursor: pointer;
    height: 180px;
    display: flex;
    flex-direction: column;
    box-sizing: border-box;

    &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        transform: translateY(-2px);
    }

    &--offline {
        // 离线模型样式与在线模型保持一致
    }

    &__header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 12px;
    }

    &__icon {
        width: 48px;
        height: 48px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 20px;
        font-weight: 600;
    }

    &__status {
        .el-tag {
            border-radius: 4px;
        }
    }

    &__body {
        margin-bottom: 12px;
    }

    &__name {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    &__org {
        font-size: 13px;
        color: #909399;
    }

    &__footer {
        margin-bottom: 8px;
    }

    &__tags {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;

        .el-tag {
            border-radius: 4px;
        }
    }
}

.model-card-tooltip {
    max-width: 300px !important;
    word-wrap: break-word !important;
    white-space: pre-wrap !important;
}
</style>

