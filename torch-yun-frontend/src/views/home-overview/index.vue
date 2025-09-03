<template>
    <div class="home-overview">
        <LoadingPage
            class="zqy-loading__home"
            :visible="loading"
            :network-error="networkError"
            @loading-refresh="getChatDetailListData"
        >
            <div class="ai-top-container">
                <div class="guide-title" v-if="!appInfo">欢迎使用至数云</div>
                <div class="app-title" :class="{ 'app-title__show': !!appInfo }">
                    <div class="close-chat">
                        <el-button link type="primary" @click="stopChat">退出对话</el-button>
                    </div>
                    <div>{{ appInfo?.name }}</div>
                </div>
                <div class="history-btn">
                    <el-button link @click="showHistoryEvent">历史记录</el-button>
                </div>
            </div>
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
                    placeholder="请输入对话（Enter 发送，Shift+Enter 换行）"
                    :autosize="{ minRows: 2, maxRows: 2 }"
                    @keydown="onKeydownEvent"
                    @compositionstart="onCompositionStart"
                    @compositionend="onCompositionEnd"
                ></el-input>
                <div class="option-container">
                    <el-button v-if="isTalking" link @click="stopChat">新对话</el-button>
                    <el-button
                        type="primary"
                        :disabled="!talkMessage"
                        :loading="requestLoading"
                        @click="sendQuestionEvent"
                    >
                        <el-icon><Promotion /></el-icon>
                    </el-button>
                </div>
            </div>
            <AppItem
                class="ai-app-container"
                :class="{ 'ai-app-container__hide': isTalking }"
                @clickAppEvent="clickAppEvent"
            ></AppItem>
        </LoadingPage>
        <HistoryList ref="historyListRef" @historyClickEvent="historyClickEvent"></HistoryList>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref, nextTick, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import ZhyChat from './zhy-chat/index.vue'
import AppItem from './app-item/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { GetChatDetailData, GetChatDetailList, GetMaxChatData, SendMessageToAiStream, StopChatThink } from '@/services/ai-cheat.service.ts'
import { SSEClient } from '@/utils/sse-client'
import { useAuthStore } from '@/store/useAuth'
import HistoryList from './history-list/index.vue'

const authStore = useAuthStore()

const loading = ref(false)
const networkError = ref(false)

// 消息部分
const talkMessage = ref<string>('')
const appInfo = ref<any>(null)    // 应用的信息
const chatId = ref<string>('')
const maxChatIndexId = ref<number>(0)

const historyListRef = ref<any>()
const requestLoading = ref<boolean>(false)  // 发送消息-加载状态
const isTalking = ref<boolean>(false)       // 是否已经开启了对话
const talkMsgList = ref<any[]>([])          // 当前对话的记录
const isComposing = ref<boolean>(false)     // 输入法是否正在输入

// SSE 相关状态
const sseClient = ref<SSEClient | null>(null)  // SSE 客户端实例
const currentAiMessage = ref<string>('')        // 当前 AI 回复的消息（用于流式显示）
const chatSessionId = ref<string>('')           // 当前聊天会话ID

// 打开历史记录
function showHistoryEvent() {
    historyListRef.value.showModal({
        page: 0,
        pageSize: 20,
        searchKeyWord: '',
        appId: appInfo.value ? appInfo.value.id : null
    })
}

// 选择应用
function clickAppEvent(e: any) {
    appInfo.value = e
    isTalking.value = true

    getMaxChatData().then(() => {
        getChatDetailListData()
    }).catch(() => {
        // getMaxChatData 调用失败时，退出对话模式
        isTalking.value = false
        appInfo.value = null
    })
}

// 结束对话
function stopChat() {
    // 断开 SSE 连接
    if (sseClient.value) {
        sseClient.value.disconnect()
        sseClient.value = null
    }

    isTalking.value = false
    appInfo.value = null
    talkMessage.value = ''
    maxChatIndexId.value = 0
    chatId.value = ''
    chatSessionId.value = ''
    talkMsgList.value = []
    currentAiMessage.value = ''
    requestLoading.value = false

    authStore.setChatInfo({
        chatId: chatId.value,
        isTalking: isTalking.value,
        appInfo: null
    })
}

// SSE 流式获取对话结果
function startSSEChatStream(params: any) {
    // 断开之前的连接
    if (sseClient.value) {
        sseClient.value.disconnect()
    }

    // 初始化当前 AI 消息
    currentAiMessage.value = ''

    // 创建新的 SSE 连接
    sseClient.value = SendMessageToAiStream(params, {
        onStart: (data: any) => {
            console.log('SSE 连接开始:', data)
            // 保存聊天会话ID和响应索引
            if (data.chatSessionId) {
                chatSessionId.value = data.chatSessionId
            }
            if (data.responseIndexId) {
                maxChatIndexId.value = data.responseIndexId
            }
            if (data.chatId) {
                chatId.value = data.chatId
            }

            // 添加一个空的 AI 消息占位符
            talkMsgList.value.push({
                type: 'ai',
                content: '',
                loading: true
            })

            // 重置当前消息内容
            currentAiMessage.value = ''
        },
        onMessage: (data: any) => {
            console.log('SSE 消息:', data)
            // 更新 AI 消息内容 - 累积拼接内容
            if (data.chatContent && data.chatContent.content) {
                // 将新内容拼接到当前消息中
                currentAiMessage.value += data.chatContent.content

                // 更新最后一条 AI 消息
                const lastIndex = talkMsgList.value.length - 1
                if (lastIndex >= 0 && talkMsgList.value[lastIndex].type === 'ai') {
                    talkMsgList.value[lastIndex].content = currentAiMessage.value
                    talkMsgList.value[lastIndex].loading = false
                }
            }
        },
        onComplete: () => {
            console.log('SSE 连接完成')
            requestLoading.value = false

            // 确保最后一条消息不再显示加载状态
            const lastIndex = talkMsgList.value.length - 1
            if (lastIndex >= 0 && talkMsgList.value[lastIndex].type === 'ai') {
                talkMsgList.value[lastIndex].loading = false
            }

            // 获取最新的索引
            getMaxChatData().catch(() => {
                // 获取最新索引失败，不影响对话显示
            })

            // 清理 SSE 客户端
            sseClient.value = null
        },
        onError: (error: any) => {
            console.error('SSE 连接错误:', error)
            requestLoading.value = false

            // 移除加载中的 AI 消息
            const lastIndex = talkMsgList.value.length - 1
            if (lastIndex >= 0 && talkMsgList.value[lastIndex].type === 'ai' && talkMsgList.value[lastIndex].loading) {
                talkMsgList.value.pop()
            }

            // 显示错误消息
            ElMessage.error(typeof error === 'string' ? error : '聊天连接失败，请重试')

            // 清理 SSE 客户端
            sseClient.value = null
        }
    })
}

// 发送问题 - 使用 SSE 流式响应
async function sendQuestionEvent() {
    if (!talkMessage.value) {
        return
    }
    // 防止重复调用：如果正在请求中，直接返回
    if (requestLoading.value) {
        return
    }

    // 记录是否是第一次对话（用于错误处理时判断是否需要退出全屏模式）
    const wasAlreadyTalking = isTalking.value
    const userMessage = talkMessage.value

    isTalking.value = true
    talkMsgList.value.push({
        type: 'user',
        content: userMessage
    })
    requestLoading.value = true

    try {
        if (!appInfo.value) {
            await getMaxChatData()
        }

        // 使用 SSE 流式发送消息
        startSSEChatStream({
            chatId: chatId.value || null,
            appId: appInfo.value ? appInfo.value.id : null,
            maxChatIndexId: maxChatIndexId.value,
            chatContent: {
                content: userMessage
            }
        })

        // 清空输入框
        talkMessage.value = ''

    } catch (error) {
        // getMaxChatData 调用失败的处理
        console.error('发送消息失败:', error)
        talkMessage.value = ''
        requestLoading.value = false

        // 如果之前没有在对话状态（即这是第一次输入），则退出全屏对话模式
        if (!wasAlreadyTalking) {
            isTalking.value = false
            // 移除刚添加的用户消息
            talkMsgList.value.pop()
        }

        ElMessage.error('发送消息失败，请重试')
    }
}

// 获取最大对话
function getMaxChatData() {
    return new Promise((resolve: any, reject: any) => {
        GetMaxChatData({
            chatId: chatId.value || null,
            appId: appInfo.value ? appInfo.value.id : null,
        }).then((res: any) => {
            appInfo.value = {
                id: res.data.appId,
                name: res.data?.appName || appInfo.value?.name
            }
            chatId.value = res.data.chatId
            maxChatIndexId.value = res.data.chatIndexId

            authStore.setChatInfo({
                chatId: chatId.value,
                isTalking: isTalking.value,
                appInfo: appInfo.value
            })
            resolve()
        }).catch((err: any) => {
            reject(err)
        })
    })
}


// 获取对话记录
function getChatDetailListData() {
    return new Promise((resolve: any, reject: any) => {
        loading.value = true
        networkError.value = networkError.value || false
        GetChatDetailList({
            chatId: chatId.value || null,
        }).then((res: any) => {
            talkMsgList.value = (res.data?.chatSessions || []).map((item: any) => {
                return {
                    type: item.role,
                    content: item.content
                }
            })
            loading.value = false
            networkError.value = false
            resolve()
        }).catch((err: any) => {
            loading.value = false
            networkError.value = false
            reject(err)
        })
    })
}

// 选择历史对话
function historyClickEvent(data: any) {
    chatId.value = data.chatId
    isTalking.value = true
    authStore.setChatInfo({
        chatId: data.chatId,
        isTalking: true,
        appInfo: {
            id: data.appId,
            name: data.appName
        }
    })
    getMaxChatData().then(() => {
        getChatDetailListData()
    }).catch(() => {
        // getMaxChatData 调用失败时，退出对话模式
        isTalking.value = false
        appInfo.value = null
    })
}

function onKeydownEvent(e: any) {
    if (e.key === 'Enter') {
        if (e.shiftKey) {
            // Shift+Enter 换行，不阻止默认行为
            return
        } else {
            // 如果输入法正在输入，不发送消息
            if (isComposing.value) {
                return
            }
            // 单独按 Enter 发送消息
            e.preventDefault()
            sendQuestionEvent()
        }
    }
}

// 输入法开始输入
function onCompositionStart() {
    isComposing.value = true
}

// 输入法结束输入
function onCompositionEnd() {
    isComposing.value = false
}

function stopThink() {
    // 断开 SSE 连接
    if (sseClient.value) {
        sseClient.value.disconnect()
        sseClient.value = null
    }

    // 停止加载状态
    requestLoading.value = false

    // 移除加载中的 AI 消息
    const lastIndex = talkMsgList.value.length - 1
    if (lastIndex >= 0 && talkMsgList.value[lastIndex].type === 'ai' && talkMsgList.value[lastIndex].loading) {
        talkMsgList.value[lastIndex].content = '已停止思考'
        talkMsgList.value[lastIndex].loading = false
    }

    // 调用后端停止接口
    if (chatSessionId.value) {
        StopChatThink({
            chatSessionId: chatSessionId.value
        }).then((res: any) => {
            console.log('停止思考成功')
        }).catch((err: any) => {
            console.error('停止思考失败:', err)
        })
    }
}

onMounted(() => {
    const chatInfo = authStore.chatInfo
    isTalking.value = chatInfo.isTalking
    appInfo.value = chatInfo.appInfo
    chatId.value = chatInfo.chatId

    if (chatId.value) {
        getChatDetailListData().then(() => {
            getMaxChatData().catch(() => {
                // 页面初始化时获取最新索引失败，不影响页面显示
            })
        })
    }
})

// 组件卸载时清理 SSE 连接
onUnmounted(() => {
    if (sseClient.value) {
        sseClient.value.disconnect()
        sseClient.value = null
    }
})
</script>

<style lang="scss">
.home-overview {
    position: relative;
    height: 100vh;
    width: 100%;
    display: flex;
    justify-content: center;
    flex-direction: column;
    .zqy-loading__home {
        display: flex;
        justify-content: center;
        flex-direction: column;
    }
    .ai-top-container {
        height: calc(100% - 356px);
        overflow-x: hidden;
        overflow-y: auto;
        padding: 50px 60px;
        padding-bottom: 20px;

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

        .guide-title {
            font-size: 27px;
            color: getCssVar('color', 'primary');
            letter-spacing: 0;
            text-align: center;
            font-weight: 500;
            margin-bottom: 30px;
        }
        .history-btn {
            position: absolute;
            top: 16px;
            right: 20px;
        }
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

    .ai-app-container {
        visibility: visible;
        transition: visibility 0.3s 0.3s cubic-bezier(0, 0, 0.48, 1.18);
        &.ai-app-container__hide {
            visibility: hidden;
            z-index: 0;
            transition: visibility 0.15s linear;
        }
    }

}
</style>