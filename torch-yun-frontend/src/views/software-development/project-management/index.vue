<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table project-management">
    <div class="zqy-table-top">
      <el-button type="primary" @click="addData">添加</el-button>
      <div class="zqy-seach"><el-input v-model="keyword" placeholder="请输入搜索条件 回车进行搜索" clearable :maxlength="200" @input="inputEvent" @keyup.enter="initData(false)" /></div>
    </div>
    <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
      <div class="zqy-table">
        <BlockTable :table-config="tableConfig" @size-change="handleSizeChange" @current-change="handleCurrentChange">
          <template #options="scopeSlot"><div class="btn-group"><span @click="editEvent(scopeSlot.row)">编辑</span><span style="color: #f56c6c" @click="deleteProject(scopeSlot.row)">删除</span></div></template>
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
import { BreadCrumbList, TableConfig } from './project-management.config'
import { QueryProjectList, AddProjectData, UpdateProjectData, DeleteProjectData } from '@/services/project-management.service'
import AddModal from './add-modal/index.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref<any>(null)
const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)

function initData(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  QueryProjectList({ page: tableConfig.pagination.currentPage - 1, pageSize: tableConfig.pagination.pageSize, searchKeyWord: keyword.value }).then((res: any) => {
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

function addData() {
  addModalRef.value.showModal((formData: any) => new Promise((resolve: any, reject: any) => {
    AddProjectData(formData).then((res: any) => { ElMessage.success(res.msg); initData(); resolve() }).catch((error: any) => reject(error))
  }))
}

function editEvent(data: any) {
  addModalRef.value.showModal((formData: any) => new Promise((resolve: any, reject: any) => {
    UpdateProjectData(formData).then((res: any) => { ElMessage.success(res.msg); initData(); resolve() }).catch((error: any) => reject(error))
  }), data)
}

function deleteProject(data: any) {
  ElMessageBox.confirm(`确定要删除项目 "${data.name}" 吗？删除后无法恢复。`, '删除确认', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
    DeleteProjectData({ id: data.id }).then(() => { ElMessage.success('删除成功'); initData() }).catch(() => { ElMessage.error('删除失败') })
  }).catch(() => {})
}

function inputEvent(e: string) { if (e === '') initData() }
function handleSizeChange(e: number) { tableConfig.pagination.pageSize = e; initData() }
function handleCurrentChange(e: number) { tableConfig.pagination.currentPage = e; initData() }

onMounted(() => {
  tableConfig.pagination.currentPage = 1
  tableConfig.pagination.pageSize = 10
  initData()
})
</script>

<style lang="scss">
.project-management { &.zqy-seach-table { .zqy-table { .btn-group { justify-content: space-around; } } } }
</style>
