<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="app-detail-options">
        <el-button type="primary" @click="showAppConfigEvent">应用配置</el-button>
    </div>
    <div class="app-detail-preview">
        <ZhyChat
            :isTalking="isTalking"
            :requestLoading="requestLoading"
            :talkMsgList="talkMsgList"
            @stopThink="stopThink"
        ></ZhyChat>
        <div class="ai-input-container" :class="{ 'ai-input-container__bottom': isTalking }">
            <el-input
                v-model="talkMessage"
                type="textarea"
                resize="none"
                :readonly="!isTalking"
                placeholder="请输入对话"
                :autosize="{ minRows: 2, maxRows: 2 }"
                @keydown.enter.prevent="onKeyupEvent"
            ></el-input>
            <div class="option-container">
                <el-button
                    :disabled="!talkMessage"
                    :loading="requestLoading"
                    @click="sendQuestionEvent"
                >
                    <el-icon><Promotion /></el-icon>
                </el-button>
            </div>
        </div>
    </div>
    <AppConfigModal ref="appConfigModalRef"></AppConfigModal>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, onUnmounted, nextTick } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import { useRoute, useRouter } from 'vue-router'
import AppConfigModal from '../app-config-modal/index.vue'
import ZhyChat from '../../../home-overview/zhy-chat/index.vue'
import { ElMessage } from 'element-plus'
import { ConfigAppData } from '@/services/app-management.service'
import { GetChatDetailData, GetChatDetailList, GetMaxChatData, SendMessageToAi, StopChatThink } from '@/services/ai-cheat.service'

const route = useRoute()
const router = useRouter()

const appConfigModalRef = ref<any>()
// 消息部分
const talkMessage = ref<string>('')
const appId = ref<any>('')
const chatId = ref<string>('')
const maxChatIndexId = ref<number>(0)
const isTalking = ref<boolean>(true)
const requestLoading = ref<boolean>(false)  // 发送消息-加载状态
const talkMsgList = ref<any[]>([])          // 当前对话的记录

const breadCrumbList = reactive([
    {
        name: '应用管理',
        code: 'app-management'
    },
    {
        name: '应用预览',
        code: 'app-detail'
    }
])

function initData() {
    if (route.query.id) {
        appId.value = route.query.id

        chatId.value = ''
    } else {
        ElMessage.warning('应用不存在')
        router.push({ name: 'app-management' })
    }
}

function onKeyupEvent(e: any) {
    if (e.type === 'keydown' && e.key === 'Enter') {
        sendQuestionEvent()
    }
}

// 发送问题
async function sendQuestionEvent() {
    if (!talkMessage.value) {
        return
    }
    isTalking.value = true
    talkMsgList.value.push({
        type: 'user',
        content: talkMessage.value
    })
    requestLoading.value = true
    if (!chatId.value) {
        await getMaxChatData()
    }
    SendMessageToAi({
        chatId: chatId.value || null,
        appId: appId.value ? appId.value : null,
        maxChatIndexId: maxChatIndexId.value,
        chatContent: {
            content: talkMessage.value,
            // index: talkMsgList.length - 1,
            // role: ''
        }
    }).then((res: any) => {
        talkMessage.value = ''
        maxChatIndexId.value = res.data.responseIndexId
        getChatResult()
    }).catch(() => {
        talkMessage.value = ''
        requestLoading.value = false
    })
}

// 获取对话结果
function getChatResult() {
    GetChatDetailData({
        chatId: chatId.value || null,
        chatIndex: maxChatIndexId.value
    }).then((res: any) => {
        if (res.data.status === 'CHATTING') {
            setTimeout(() => {
                getChatResult()
            }, 1000);
        } else {
            requestLoading.value = false
            nextTick(() => {
                talkMsgList.value.push({
                    type: 'ai',
                    content: res.data.chatContent.content
                })
            })
            // 会话结束获取最新的索引
            getMaxChatData()
        }
    }).catch(() => {
        requestLoading.value = false
    })
}

// 获取最大对话
function getMaxChatData() {
    return new Promise((resolve: any, reject: any) => {
        GetMaxChatData({
            chatId: chatId.value || null,
            appId: appId.value ? appId.value : null,
        }).then((res: any) => {
            chatId.value = res.data.chatId
            maxChatIndexId.value = res.data.chatIndexId
            resolve()
        }).catch((err: any) => {
            reject(err)
        })
    })
}

// 应用配置弹窗
function showAppConfigEvent() {
    appConfigModalRef.value.showModal((formData: any) => {
        return new Promise((resolve: any, reject) => {
            ConfigAppData({
                id: route.query.id,
                ...formData
            }).then((res: any) => {
                ElMessage.success(res.msg)
                resolve()
            }).catch((err: any) => {
                reject(err)
            })
        })
    })
}

function stopThink() {
    StopChatThink({
        chatSessionId: chatId.value || null
    }).then((res: any) => {
        
    }).catch((err: any) => {
    })
}

// 结束对话
function stopChat() {
    isTalking.value = false
    talkMessage.value = ''
    chatId.value = ''
    talkMsgList.value = []
}

onMounted(() => {
    initData()
})
</script>

<style lang="scss">
.app-detail-options {
    position: absolute;
    right: 20px;
    top: 0;
    height: 56px;
    display: flex;
    align-items: center;
}
.app-detail-preview {
    position: relative;
    height: calc(100vh - 56px);
    width: 100%;
    display: flex;
    justify-content: center;
    flex-direction: column;
    .zhy-chat__show {
        top: 20px;
    }
    .ai-input-container {
        width: 80%;
        margin: auto;
        border: 1px solid #0000000f;
        border-radius: 15px;
        padding: 12px 20px 12px 20px;
        box-sizing: border-box;
        transition: bottom 0.15s cubic-bezier(0, 0, 0.48, 1.18);
        position: absolute;
        left: 10%;
        bottom: 65%;
        z-index: 10;
        .el-textarea {
            // height: 80px;
            .el-textarea__inner {
                box-shadow: unset;
            }
        }
        .option-container {
            display: flex;
            justify-content: flex-end;
            margin-top: 12px;
            .el-button {
                border-radius: 20px;
            }
        }

        &.ai-input-container__bottom {
            position: absolute;
            bottom: 20px;
            transition: bottom 0.15s 0.3s cubic-bezier(0, 0, 0.48, 1.18);
        }
    }
}
</style>