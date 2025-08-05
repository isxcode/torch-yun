<template>
    <div class="home-overview">
        <LoadingPage
            class="zqy-loading__home"
            :visible="loading"
            :network-error="networkError"
            @loading-refresh="getChatDetailListData"
        >
            <div class="ai-top-container">
                <div class="guide-title" v-if="!appInfo">欢迎使用至慧云！</div>
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
                    placeholder="请输入对话"
                    :autosize="{ minRows: 2, maxRows: 2 }"
                    @keydown.enter.prevent="onKeyupEvent"
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
import { onMounted, reactive, ref, nextTick } from 'vue'
import ZhyChat from './zhy-chat/index.vue'
import AppItem from './app-item/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { GetChatDetailData, GetChatDetailList, GetMaxChatData, SendMessageToAi, StopChatThink } from '@/services/ai-cheat.service.ts'
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
    })
}

// 结束对话
function stopChat() {
    isTalking.value = false
    appInfo.value = null
    talkMessage.value = ''
    maxChatIndexId.value = 0
    chatId.value = ''
    talkMsgList.value = []

    authStore.setChatInfo({
        chatId: chatId.value,
        isTalking: isTalking.value,
        appInfo: null
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
    if (!appInfo.value) {
        await getMaxChatData()
    }
    SendMessageToAi({
        chatId: chatId.value || null,
        appId: appInfo.value ? appInfo.value.id : null,
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
    })
}

function onKeyupEvent(e: any) {
    if (e.type === 'keydown' && e.key === 'Enter') {
        sendQuestionEvent()
    }
}

function stopThink() {
    StopChatThink({
        chatSessionId: chatId.value || null
    }).then((res: any) => {
        
    }).catch((err: any) => {
    })
}

onMounted(() => {
    const chatInfo = authStore.chatInfo
    isTalking.value = chatInfo.isTalking
    appInfo.value = chatInfo.appInfo
    chatId.value = chatInfo.chatId

    if (chatId.value) {
        getChatDetailListData().then(() => {
            getMaxChatData()
        })
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