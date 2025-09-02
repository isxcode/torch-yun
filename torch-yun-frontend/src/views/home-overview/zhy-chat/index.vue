<template>
    <div class="zhy-chat" :class="{ 'zhy-chat__show': isTalking }">
        <el-scrollbar ref="elScrollbarRef">
            <div
                v-for="(item, index) in talkMsgList"
                class="card-item"
                :key="index"
                :class="{ 'card-item__ai': item.type !== 'user' }"
            >
                <el-avatar
                    :style="{
                        left: item.type === 'user' ? '' : '-44px',
                        right: item.type === 'user' ? '-44px' : '',
                        color: '#FFFFFF',
                        'background-color': item.type !== 'user' ? '#516bfe' : '#88d069'
                    }"
                    :icon="UserFilled"
                    :size="30"
                />
                <div class="chat-message-item" v-html="content(item.content)">
                </div>
            </div>
            <!-- 显示流式加载状态 -->
            <div class="card-item card-item__ai" v-if="requestLoading && !hasLoadingMessage">
                <el-avatar
                    :style="{
                        left: '-44px',
                        color: '#FFFFFF',
                        'background-color': '#fde3d0'
                    }"
                    :icon="UserFilled"
                    :size="30"
                />
                <div class="chat-message-item chat-message-item__loading">
                    <span class="loading-text__chat">
                        <el-icon class="is-loading">
                            <Loading />
                        </el-icon>
                        思考中{{ loadingPoint }}
                    </span>
                </div>
            </div>
            <div v-if="requestLoading">
                <!-- <span class="stop-think" @click="stopThink">停止思考</span> -->
            </div>
        </el-scrollbar>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, watch, nextTick, computed, onMounted, onUnmounted, toRefs, defineEmits } from 'vue'
import { UserFilled, Loading } from '@element-plus/icons-vue'
import { marked } from 'marked';
import hljs from 'highlight.js/lib/common';
import 'highlight.js/styles/agate.css'

interface ChatItem {
    type: string
    content: string
    loading?: boolean
}

const emit = defineEmits(['stopThink'])

const elScrollbarRef = ref<any>()
const props = defineProps<{
    talkMsgList: ChatItem[]
    requestLoading: boolean
    isTalking: boolean
}>()

const loadingTimer = ref<any>()
const loadingPoint = ref<string>('.')

/**
 * 使用 marked 解析 Markdown
 * @param markdown 解析的文本
 */
 const renderMarkdown = (markdown: string): any => {
    const renderer = new marked.Renderer();
    renderer.code = ({ text, lang, escaped }: Tokens.Code): string => {
        const language = hljs.getLanguage(lang) ? lang : 'plaintext';
        const highlighted = hljs.highlight(text, { language }).value;
        return `<pre><code class="hljs language-${language}">${highlighted}</code></pre>`;
    };
    return marked(markdown, { renderer });
};

const content = computed(() => {
  return (content: string) => {
    return renderMarkdown(content)
  }
})

// 检查是否有加载中的消息
const hasLoadingMessage = computed(() => {
  return props.talkMsgList.some(item => item.loading === true)
})

watch(() => props.talkMsgList, (v: any) => {
    if (v) {
        refrashScrollEvent()
    }
}, {
    immediate: true,
    deep: true
})

function refrashScrollEvent() {
    nextTick(() => {
        elScrollbarRef.value?.scrollTo({
            top: elScrollbarRef.value.wrapRef.scrollHeight,
            behavior: 'smooth'
        })
    })
}

function stopThink() {
    emit('stopThink')
}

onMounted(() => {
    if (loadingTimer.value) {
        clearInterval(loadingTimer.value)
    }
    loadingTimer.value = null
    loadingTimer.value = setInterval(() => {
        if (loadingPoint.value.length < 5) {
            loadingPoint.value = loadingPoint.value + '.'
        } else {
            loadingPoint.value = '.'
        }
    }, 1000)

    hljs.highlightAll()
})

onUnmounted(() => {
  if (loadingTimer.value) {
    clearInterval(loadingTimer.value)
  }
  loadingTimer.value = null
})

defineExpose({
    refrashScrollEvent
})
</script>

<style lang="scss">
.zhy-chat {
    position: absolute;
    width: 100%;
    top: 70px;
    bottom: 158px;
    padding: 0 10%;
    box-sizing: border-box;
    overflow: auto;
    visibility: hidden;
    transition: visibility 0.05s cubic-bezier(0, 0, 0.48, 1.18);
    &.zhy-chat__show {
        visibility: visible;
        transition: visibility 0.3s 0.3s linear;
    }
    .el-scrollbar__view {
        padding: 0 60px;
        box-sizing: border-box;
        .card-item {
            position: relative;
            display: flex;
            justify-content: flex-end;
            &.card-item__ai {
                justify-content: flex-start;
            }
            .el-avatar {
                position: absolute;
            }
            .chat-message-item {
                margin-bottom: 14px;
                background-color: #0000000f;
                line-height: 1.5;
                padding: 3px 16px;
                font-size: 13px;
                box-sizing: border-box;
                display: inline-block;
                border-radius: 8px;
                &.chat-message-item__loading {
                    padding: 16px 16px;
                }

                .loading-text__chat {
                    color: getCssVar('color', 'primary');
                    display: flex;
                    align-items: center;
                    .el-icon {
                        margin-right: 4px;
                    }
                }
            }
        }
        .stop-think {
            font-size: 12px;
            color: getCssVar('color', 'primary');
            cursor: pointer;
            &:hover {
                text-decoration: underline;
            }
        }
    }
}
</style>
