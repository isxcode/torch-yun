<template>
    <BlockModal :model-config="modelConfig" @close="closeEvent">
        <LoadingPage class="log-loading" :visible="loading">
            <div id="content" class="content-box">
                <LogContainer v-if="logMsg" :logMsg="logMsg" :status="status"></LogContainer>
            </div>
        </LoadingPage>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, onUnmounted, nextTick, computed } from 'vue'
import { GetAiItemLogData, GetAiDeployLogData } from '@/services/ai-item.service'
import LoadingPage from '@/components/loading/index.vue'


const logMsg = ref<string>('')
const timer = ref<any>(null)
const loading = ref<boolean>(false)
const isRequest = ref<boolean>(false)
const status = ref<boolean>(false)

const modelConfig = reactive({
    title: '日志',
    visible: false,
    width: '820px',
    cancelConfig: {
        title: '关闭',
        cancel: closeEvent,
        disabled: false
    },
    needScale: false,
    zIndex: 1100,
    customClass: 'zqy-log-modal',
    closeOnClickModal: false
})

function showModal(data: string, logType: string = 'run'): void {
    logMsg.value = ''
    loading.value = true
    timer.value = null
    getLogData(data, logType)
    if (!timer.value && logType === 'run') {
        timer.value = setInterval(() => {
            !isRequest.value && getLogData(data, logType)
        }, 3000)
    }
    modelConfig.visible = true
}
// 获取日志
function getLogData(data: any, logType: string = 'run') {
    isRequest.value = true

    if (logType === 'deploy') {
        modelConfig.title = '部署日志'
        GetAiDeployLogData({ id: data }).then((res: any) => {
            logMsg.value = res.data.log
            status.value = true
            isRequest.value = false
            loading.value = false
        }).catch(() => {
            logMsg.value = ''
            isRequest.value = false
            loading.value = false
        })
    } else {
        modelConfig.title = '运行日志'
        GetAiItemLogData({ id: data }).then((res: any) => {
            logMsg.value = res.data.log
            status.value = true
            isRequest.value = false
            loading.value = false
        }).catch(() => {
            logMsg.value = ''
            isRequest.value = false
            loading.value = false
        })
    }
}

function closeEvent() {
    if (timer.value) {
        clearInterval(timer.value)
    }
    timer.value = null
    modelConfig.visible = false
}

onUnmounted(() => {
    if (timer.value) {
        clearInterval(timer.value)
    }
    timer.value = null
})

defineExpose({
    showModal
})
</script>

<style lang="scss">
.zqy-log-modal {
    .modal-content {
        position: relative;
        .content-box {
            min-height: 59vh;
            max-height: 60vh;
            padding: 12px 20px;
            box-sizing: border-box;
            overflow: auto;
            .zqy-download-log {
                top: 6px;
                right: 20px;
            }
        }
    }
}
</style>
