<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table model-management">
        <div class="zqy-table-top">
            <el-button type="primary" @click="addData">
                添加
            </el-button>
            <div class="zqy-seach">
                <el-input v-model="keyword" placeholder="请输入搜索条件 回车进行搜索" clearable :maxlength="200" @input="inputEvent"
                    @keyup.enter="initData(false)" />
            </div>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
            <div class="zqy-table">
                <BlockTable :table-config="tableConfig" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange">
                    <template #nameSlot="scopeSlot">
                        <span class="name-click" @click="showDetail(scopeSlot.row)">{{ scopeSlot.row.name }}</span>
                    </template>
                    <template #statusTag="scopeSlot">
                        <ZStatusTag :status="scopeSlot.row.status"></ZStatusTag>
                    </template>
                    <template #defaultAppTag="scopeSlot">
                        <el-tag v-if="scopeSlot.row.defaultApp === 'ENABLE'" type="success" size="small">是</el-tag>
                        <el-tag v-else type="info" size="small">否</el-tag>
                    </template>
                    <template #logo="scopeSlot">
                        <div class="logo-container">
                            <!-- <el-avatar :size="24" :src="scopeSlot.row.logoId" /> -->
                            <el-avatar :size="24" :icon="UserFilled" />
                        </div>
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group">
                            <span @click="editEvent(scopeSlot.row)">编辑</span>
                            <el-dropdown trigger="click">
                                <span class="click-show-more">更多</span>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item v-if="scopeSlot.row.status === 'DISABLE'" @click="enableApp(scopeSlot.row)">
                                            启用
                                        </el-dropdown-item>
                                        <el-dropdown-item v-if="scopeSlot.row.status === 'ENABLE'" @click="disableApp(scopeSlot.row)">
                                            禁用
                                        </el-dropdown-item>
                                        <el-dropdown-item v-if="scopeSlot.row.defaultApp !== 'ENABLE'" @click="setDefaultApp(scopeSlot.row)">
                                            默认
                                        </el-dropdown-item>
                                        <el-dropdown-item @click="deleteApp(scopeSlot.row)" style="color: #f56c6c;">
                                            删除
                                        </el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                    </template>
                </BlockTable>
            </div>
        </LoadingPage>
        <AddModal ref="addModalRef" />
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { BreadCrumbList, TableConfig } from './list.config'
import { QueryAppList, AddAppData, UpdateAppData, EnableAppData, DisableAppData, SetDefaultAppData, DeleteAppData } from '@/services/app-management.service'
import AddModal from './add-modal/index.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref<any>(null)

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    QueryAppList({
        page: tableConfig.pagination.currentPage - 1,
        pageSize: tableConfig.pagination.pageSize,
        searchKeyWord: keyword.value
    }).then((res: any) => {
        tableConfig.tableData = res.data.content
        tableConfig.pagination.total = res.data.totalElements
        loading.value = false
        tableConfig.loading = false
        networkError.value = false
    }).catch(() => {
        tableConfig.tableData = []
        tableConfig.pagination.total = 0
        loading.value = false
        tableConfig.loading = false
        networkError.value = true
    })
}

function showDetail(data: any) {
    router.push({
        name: 'app-detail',
        query: {
            id: data.id
        }
    })
}

function addData() {
    addModalRef.value.showModal((formData: any) => {
        return new Promise((resolve: any, reject: any) => {
            AddAppData(formData).then((res: any) => {
                ElMessage.success(res.msg)
                initData()
                resolve()
            }).catch((error: any) => {
                reject(error)
            })
        })
    })
}
function editEvent(data: any) {
    addModalRef.value.showModal((formData: any) => {
        return new Promise((resolve: any, reject: any) => {
            UpdateAppData(formData).then((res: any) => {
                ElMessage.success(res.msg)
                initData()
                resolve()
            }).catch((error: any) => {
                reject(error)
            })
        })
    }, data)
}

function inputEvent(e: string) {
    if (e === '') {
        initData()
    }
}

function handleSizeChange(e: number) {
    tableConfig.pagination.pageSize = e
    initData()
}

function handleCurrentChange(e: number) {
    tableConfig.pagination.currentPage = e
    initData()
}

function enableApp(data: any) {
    EnableAppData({ id: data.id }).then((res: any) => {
        ElMessage.success('启用成功')
        initData()
    }).catch((error: any) => {
        ElMessage.error('启用失败')
    })
}

function disableApp(data: any) {
    DisableAppData({ id: data.id }).then((res: any) => {
        ElMessage.success('禁用成功')
        initData()
    }).catch((error: any) => {
        ElMessage.error('禁用失败')
    })
}

function setDefaultApp(data: any) {
    SetDefaultAppData({ id: data.id }).then((res: any) => {
        initData()
    }).catch((error: any) => {
    })
}

function deleteApp(data: any) {
    ElMessageBox.confirm(
        `确定要删除应用 "${data.name}" 吗？删除后无法恢复。`,
        '删除确认',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(() => {
        DeleteAppData({ id: data.id }).then((res: any) => {
            ElMessage.success('删除成功')
            initData()
        }).catch((error: any) => {
            ElMessage.error('删除失败')
        })
    }).catch(() => {
        // 用户取消删除
    })
}

onMounted(() => {
    tableConfig.pagination.currentPage = 1
    tableConfig.pagination.pageSize = 10
    initData()
})
</script>

<style lang="scss">
.model-management {
    &.zqy-seach-table {
        .zqy-table {
            .btn-group {
                justify-content: space-around;
            }
            .logo-container {
                display: flex;
                align-items: center;
            }
        }
    }
}
</style>