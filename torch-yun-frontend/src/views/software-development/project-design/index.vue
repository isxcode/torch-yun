<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />

  <div v-if="!isChatMode" class="zqy-seach-table project-design">
    <div class="zqy-table-top">
      <div style="display: flex; align-items: center; gap: 12px;">
        <el-select v-model="currentProjectId" placeholder="请选择项目" style="width: 220px;">
          <el-option v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-button type="primary" @click="addData">添加</el-button>
      </div>
      <div class="zqy-seach"><el-input v-model="keyword" placeholder="请输入搜索条件 回车进行搜索" clearable :maxlength="200" @input="inputEvent" @keyup.enter="initData(false)" /></div>
    </div>
    <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
      <div class="zqy-table">
        <BlockTable :table-config="tableConfig" @size-change="handleSizeChange" @current-change="handleCurrentChange">
          <template #nameSlot="scopeSlot"><span class="name-click" @click="enterChatMode(scopeSlot.row)">{{ scopeSlot.row.name }}</span></template>
          <template #options="scopeSlot"><div class="btn-group"><span @click="editEvent(scopeSlot.row)">编辑</span><span style="color: #f56c6c" @click="deleteItem(scopeSlot.row)">删除</span></div></template>
        </BlockTable>
      </div>
    </LoadingPage>
    <AddModal ref="addModalRef" />
  </div>

  <div v-else class="home-overview project-design-chat">
    <div class="ai-top-container">
      <div class="app-title" :class="{ 'app-title__show': !!appInfo }">
        <div class="close-chat"><el-button link type="primary" @click="exitChatMode">退出对话</el-button></div>
        <div>{{ currentChatDesignName }}</div>
      </div>
    </div>
    <ZhyChat ref="chatRef" :isTalking="isTalking" :requestLoading="requestLoading" :talkMsgList="talkMsgList" @stopThink="stopThink" />
    <div class="ai-input-container" :class="{ 'ai-input-container__bottom': isTalking }">
      <el-input
        v-model="talkMessage"
        type="textarea"
        resize="none"
        placeholder="请输入对话（Enter 发送，Shift+Enter 换行）"
        :autosize="{ minRows: 1, maxRows: 3 }"
        @keydown="onKeydownEvent"
        @compositionstart="onCompositionStart"
        @compositionend="onCompositionEnd"
      />
      <div class="option-container">
        <el-button type="primary" :disabled="!talkMessage" :loading="requestLoading" @click="sendQuestionEvent"><el-icon><Promotion /></el-icon></el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, watch, onUnmounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { BreadCrumbList, TableConfig } from './project-design.config'
import { QueryProjectDesignList, AddProjectDesignData, UpdateProjectDesignData, DeleteProjectDesignData, GetProjectDesignMaxChatData, SendProjectDesignMessageStream } from '@/services/project-design.service'
import { QueryProjectList } from '@/services/project-management.service'
import AddModal from './add-modal/index.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ZhyChat from '@/views/home-overview/zhy-chat/index.vue'
import { GetChatDetailList, StopChatThink } from '@/services/ai-cheat.service'
import { SSEClient } from '@/utils/sse-client'

const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref<any>(null)
const currentProjectId = ref('')
const projectOptions = ref<any[]>([])
const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)

const isChatMode = ref(false)
const currentChatDesignName = ref('')
const currentProjectDesignId = ref('')
const talkMessage = ref('')
const appInfo = ref<any>(null)
const chatId = ref('')
const maxChatIndexId = ref(0)
const requestLoading = ref(false)
const isTalking = ref(false)
const talkMsgList = ref<any[]>([])
const isComposing = ref(false)
const chatRef = ref<any>(null)
const sseClient = ref<SSEClient | null>(null)
const currentAiMessage = ref('')
const chatSessionId = ref('')

function initData(tableLoading?: boolean) {
  if (!currentProjectId.value) {
    tableConfig.tableData = []
    tableConfig.pagination.total = 0
    loading.value = false
    tableConfig.loading = false
    networkError.value = false
    return
  }
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  QueryProjectDesignList({ page: tableConfig.pagination.currentPage - 1, pageSize: tableConfig.pagination.pageSize, searchKeyWord: keyword.value, projectId: currentProjectId.value }).then((res: any) => {
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
  if (!currentProjectId.value) {
    ElMessage.warning('请先选择项目')
    return
  }
  addModalRef.value.showModal((formData: any) => new Promise((resolve: any, reject: any) => {
    AddProjectDesignData({ ...formData, projectId: currentProjectId.value }).then((res: any) => { ElMessage.success(res.msg); initData(); resolve() }).catch((err: any) => reject(err))
  }))
}

function editEvent(data: any) {
  addModalRef.value.showModal((formData: any) => new Promise((resolve: any, reject: any) => {
    UpdateProjectDesignData({ ...formData, projectId: currentProjectId.value }).then((res: any) => { ElMessage.success(res.msg); initData(); resolve() }).catch((err: any) => reject(err))
  }), data)
}

function deleteItem(data: any) {
  ElMessageBox.confirm(`确定要删除设计 "${data.name}" 吗？删除后无法恢复。`, '删除确认', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
    DeleteProjectDesignData({ id: data.id }).then(() => { ElMessage.success('删除成功'); initData() }).catch(() => ElMessage.error('删除失败'))
  }).catch(() => {})
}

function inputEvent(e: string) { if (e === '') initData() }
function handleSizeChange(e: number) { tableConfig.pagination.pageSize = e; initData() }
function handleCurrentChange(e: number) { tableConfig.pagination.currentPage = e; initData() }

function initProjectOptions() {
  QueryProjectList({ page: 0, pageSize: 9999, searchKeyWord: '' }).then((res: any) => {
    projectOptions.value = res.data.content || []
    if (!currentProjectId.value && projectOptions.value.length > 0) {
      currentProjectId.value = projectOptions.value[0].id
    }
  }).catch(() => {
    projectOptions.value = []
  })
}

function getSSEErrorMessage(error: any) {
  if (!error) return '聊天连接失败，请重试'
  if (typeof error === 'string') return error
  const rawMessage = error?.message
  if (typeof rawMessage === 'string') {
    try {
      const parsed = JSON.parse(rawMessage)
      if (parsed?.msg) return parsed.msg
    } catch {}
    if (rawMessage) return rawMessage
  }
  return '聊天连接失败，请重试'
}

function stopChat() {
  if (sseClient.value) {
    sseClient.value.disconnect()
    sseClient.value = null
  }
  isTalking.value = false
  talkMessage.value = ''
  maxChatIndexId.value = 0
  chatId.value = ''
  chatSessionId.value = ''
  talkMsgList.value = []
  currentAiMessage.value = ''
  requestLoading.value = false
}

function exitChatMode() {
  stopChat()
  appInfo.value = null
  currentProjectDesignId.value = ''
  isChatMode.value = false
}

function startSSEChatStream(params: any) {
  if (sseClient.value) sseClient.value.disconnect()
  currentAiMessage.value = ''
  sseClient.value = SendProjectDesignMessageStream(params, {
    onStart: (data: any) => {
      if (data.chatSessionId) chatSessionId.value = data.chatSessionId
      if (data.responseIndexId) maxChatIndexId.value = data.responseIndexId
      if (data.chatId) chatId.value = data.chatId
      talkMsgList.value.push({ type: 'ai', content: '', loading: true })
      currentAiMessage.value = ''
    },
    onChat: (data: any) => {
      const sseBody = JSON.parse(data)
      const chatContent = sseBody.chat || ''
      if (chatContent !== undefined) {
        currentAiMessage.value += chatContent
        const lastIndex = talkMsgList.value.length - 1
        if (lastIndex >= 0 && talkMsgList.value[lastIndex].type === 'ai') {
          talkMsgList.value[lastIndex].content = currentAiMessage.value
          talkMsgList.value[lastIndex].loading = false
        }
      }
    },
    onEnd: () => {
      requestLoading.value = false
      const lastIndex = talkMsgList.value.length - 1
      if (lastIndex >= 0 && talkMsgList.value[lastIndex].type === 'ai') talkMsgList.value[lastIndex].loading = false
      getMaxChatData().catch(() => {})
      sseClient.value = null
    },
    onError: (error: any) => {
      requestLoading.value = false
      const lastIndex = talkMsgList.value.length - 1
      if (lastIndex >= 0 && talkMsgList.value[lastIndex].type === 'ai' && talkMsgList.value[lastIndex].loading) talkMsgList.value.pop()
      talkMsgList.value.push({ type: 'error', content: getSSEErrorMessage(error) })
      sseClient.value = null
    }
  })
}

async function sendQuestionEvent() {
  if (!talkMessage.value || requestLoading.value) return
  const userMessage = talkMessage.value
  chatRef.value && chatRef.value.startScroll()
  isTalking.value = true
  talkMsgList.value.push({ type: 'user', content: userMessage })
  requestLoading.value = true
  try {
    if (!appInfo.value) return
    if (!chatId.value) await getMaxChatData()
    const sendContent = maxChatIndexId.value === 0 ? `use brainstorming.\n${userMessage}` : userMessage
    startSSEChatStream({
      projectId: currentProjectId.value,
      projectDesignId: currentProjectDesignId.value,
      chatId: chatId.value || null,
      maxChatIndexId: maxChatIndexId.value,
      chatContent: { content: sendContent }
    })
    talkMessage.value = ''
  } catch {
    talkMessage.value = ''
    requestLoading.value = false
    ElMessage.error('发送消息失败，请重试')
  }
}

function getMaxChatData() {
  return new Promise((resolve: any, reject: any) => {
    GetProjectDesignMaxChatData({
      projectId: currentProjectId.value,
      projectDesignId: currentProjectDesignId.value,
      chatId: chatId.value || null,
      chatType: 'PROD'
    }).then((res: any) => {
      chatId.value = res.data.chatId
      maxChatIndexId.value = res.data.chatIndexId
      resolve(null)
    }).catch((err: any) => reject(err))
  })
}

function getChatDetailListData() {
  return new Promise((resolve: any, reject: any) => {
    if (!chatId.value) {
      talkMsgList.value = []
      isTalking.value = false
      resolve(null)
      return
    }
    GetChatDetailList({ chatId: chatId.value }).then((res: any) => {
      talkMsgList.value = (res.data?.chatSessions || []).map((item: any) => ({ type: item.role, content: item.content }))
      isTalking.value = talkMsgList.value.length > 0
      resolve(null)
    }).catch((err: any) => reject(err))
  })
}

function onKeydownEvent(e: any) {
  if (e.key === 'Enter') {
    if (e.shiftKey) return
    if (isComposing.value) return
    e.preventDefault()
    sendQuestionEvent()
  }
}

function onCompositionStart() { isComposing.value = true }
function onCompositionEnd() { isComposing.value = false }

function stopThink() {
  if (sseClient.value) {
    sseClient.value.disconnect()
    sseClient.value = null
  }
  requestLoading.value = false
  const lastIndex = talkMsgList.value.length - 1
  if (lastIndex >= 0 && talkMsgList.value[lastIndex].type === 'ai' && talkMsgList.value[lastIndex].loading) {
    talkMsgList.value[lastIndex].content = '已停止思考'
    talkMsgList.value[lastIndex].loading = false
  }
  if (chatSessionId.value) {
    StopChatThink({ chatSessionId: chatSessionId.value }).catch(() => {})
  }
}

function enterChatMode(row: any) {
  const currentProject = projectOptions.value.find((item: any) => item.id === currentProjectId.value)
  if (!currentProject?.designAppId) {
    ElMessage.warning('当前项目未配置项目设计模型应用')
    return
  }
  stopChat()
  currentProjectDesignId.value = row.id
  chatId.value = row.lastChatId || ''
  currentChatDesignName.value = row.name
  appInfo.value = { id: currentProject.designAppId, name: row.name }
  isChatMode.value = true
  getMaxChatData().then(() => getChatDetailListData()).catch(() => {
    talkMsgList.value = []
    isTalking.value = false
  })
}

onMounted(() => {
  tableConfig.pagination.currentPage = 1
  tableConfig.pagination.pageSize = 10
  initProjectOptions()
})

watch(currentProjectId, () => {
  tableConfig.pagination.currentPage = 1
  initData()
})

onUnmounted(() => {
  if (sseClient.value) {
    sseClient.value.disconnect()
    sseClient.value = null
  }
})
</script>

<style lang="scss">
.project-design {
  &.zqy-seach-table {
    .zqy-table {
      .btn-group { justify-content: space-around; }
      .name-click { color: var(--el-color-primary); cursor: pointer; }
    }
  }
}

.project-design-chat.home-overview {
  position: relative;
  height: calc(100vh - 56px);
  width: 100%;
  display: flex;
  justify-content: center;
  flex-direction: column;

  .ai-top-container {
    height: calc(100% - 356px);
    overflow-x: hidden;
    overflow-y: auto;
    padding: 50px 60px 20px;

    .app-title {
      display: flex;
      justify-content: center;
      position: absolute;
      width: 100%;
      left: 0;
      top: -50px;
      height: 50px;
      align-items: center;
      border-bottom: 1px solid #0000000f;
      transition: top 0.3s cubic-bezier(0, 0, 0.48, 1.18);
      padding: 0 20px;
      box-sizing: border-box;

      &.app-title__show {
        top: 0;
        transition: top 0.15s 0.3s cubic-bezier(0, 0, 0.48, 1.18);
      }

      .close-chat {
        position: absolute;
        top: 16px;
        left: 20px;
      }
    }
  }

  .ai-input-container {
    display: flex;
    align-items: center;
    width: 64%;
    margin: auto;
    border: 1px solid #dcdfe6;
    border-radius: 999px;
    padding: 8px 10px 8px 18px;
    background: #fff;
    box-sizing: border-box;
    transition: bottom 0.15s cubic-bezier(0, 0, 0.48, 1.18);
    position: absolute;
    left: 18%;
    top: 50%;
    transform: translateY(-50%);
    z-index: 10;

    .el-textarea {
      flex: 1;
      display: flex;
      align-items: center;

      .el-textarea__inner {
        box-shadow: unset;
        border: none;
        background: transparent;
        min-height: 24px !important;
        line-height: 24px;
        padding: 2px 8px 2px 0;
      }
    }

    .option-container {
      display: flex;
      align-items: center;
      justify-content: flex-end;
      margin-top: 0;

      .el-button {
        border-radius: 50%;
        padding: 7px;
      }
    }

    &.ai-input-container__bottom {
      position: absolute;
      top: auto;
      transform: none;
      bottom: 20px;
      transition: bottom 0.15s 0.3s cubic-bezier(0, 0, 0.48, 1.18);
    }
  }
}
</style>
