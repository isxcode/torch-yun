<template>
    <BlockDrawer :drawer-config="drawerConfig">
        <el-scrollbar>
            <div class="app-config-container">
                <el-form
                    ref="formRef"
                    label-position="left"
                    label-width="120px"
                    :model="formData"
                    :rules="basicFormRules"
                >
                    <!-- 数据源配置 -->
                    <div class="config-item">
                        <div class="item-title">基础配置</div>
                        <el-form-item label="文本长度" prop="maxTokens">
                            <el-tooltip
                                content="控制生成文本的最大长度"
                                placement="top"
                            >
                                <el-icon style="left: -68px" class="tooltip-msg"><QuestionFilled /></el-icon>
                            </el-tooltip>
                            <el-input-number v-model="formData.baseConfig.maxTokens" :min="0" controls-position="right" />
                        </el-form-item>
                        <el-form-item label="随机数" prop="seed">
                            <el-tooltip
                                content="随机数生成器的种子，用于确保结果可复现"
                                placement="top"
                            >
                                <el-icon style="left: -82px" class="tooltip-msg"><QuestionFilled /></el-icon>
                            </el-tooltip>
                            <el-input-number v-model="formData.baseConfig.seed" :min="0" controls-position="right" />
                        </el-form-item>
                        <el-form-item label="候选词数" prop="topK">
                            <el-tooltip
                                content="示例：设为 50 时，仅考虑模型认为最可能的50个候选词"
                                placement="top"
                            >
                                <el-icon style="left: -68px" class="tooltip-msg"><QuestionFilled /></el-icon>
                            </el-tooltip>
                            <el-input-number v-model="formData.baseConfig.topK" :min="0" controls-position="right" />
                        </el-form-item>
                        <el-form-item label="累积概率" prop="topP">
                            <el-tooltip
                                content="示例：设为 0.9 时，选择概率累加达90%的最小词集"
                                placement="top"
                            >
                                <el-icon style="left: -68px" class="tooltip-msg"><QuestionFilled /></el-icon>
                            </el-tooltip>
                            <el-input-number
                                v-model="formData.baseConfig.topP"
                                :min="0"
                                :max="1"
                                :step="0.01"
                                controls-position="right"
                            />
                        </el-form-item>
                        <el-form-item label="采样随机性" prop="temperature">
                            <el-tooltip
                                content="调节采样随机性。值越高（如 1.0）结果越多样；值越低（如 0.1）越保守"
                                placement="top"
                            >
                                <el-icon style="left: -58px" class="tooltip-msg"><QuestionFilled /></el-icon>
                            </el-tooltip>
                            <el-input-number
                                v-model="formData.baseConfig.temperature"
                                :min="0"
                                :max="1"
                                :step="0.01"
                                controls-position="right"
                            />
                        </el-form-item>
                        <el-form-item label="是否联网检索" prop="enableSearch">
                            <el-tooltip
                                content="是否结合外部搜索（如实时检索）增强生成内容的事实性"
                                placement="top"
                            >
                                <el-icon style="left: -46px" class="tooltip-msg"><QuestionFilled /></el-icon>
                            </el-tooltip>
                            <el-switch v-model="formData.baseConfig.enableSearch" />
                        </el-form-item>
                    </div>
                    <!-- 数据源配置 -->
                    <div class="config-item">
                        <div class="item-title">提示词</div>
                        <el-form-item label="提示词" prop="datasourceId">
                            <el-input v-model="formData.prompt" placeholder="请输入"></el-input>
                            <!-- <el-select v-model="formData.baseConfig.datasourceId" placeholder="请选择">
                                <el-option v-for="item in dataSourceList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select> -->
                        </el-form-item>
                    </div>
                    <!-- 数据源配置 -->
                    <div class="config-item">
                        <div class="item-title">知识库</div>
                        <el-form-item label="资源文件" prop="resources">
                            <el-select
                                v-model="formData.resources"
                                placeholder="请选择"
                                multiple
                                clearable
                                collapse-tags
                                :collapse-tags-tooltip="true"
                            >
                                <el-option
                                    v-for="item in resourcesList"
                                    :key="item.id"
                                    :label="item.fileName"
                                    :value="item.id"
                                />
                            </el-select>
                        </el-form-item>
                    </div>
                </el-form>
            </div>
        </el-scrollbar>
    </BlockDrawer>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import BlockDrawer from '@/components/block-drawer/index.vue'
import { useRoute } from 'vue-router'
import { GetConfigAppData } from '@/services/app-management.service'
import { GetFileCenterList } from '@/services/file-center.service';

const route = useRoute()

const basicFormRules = ref([])
const formRef = ref<any>()
const callback = ref<any>()
const resourcesList = ref<any[]>([])
const drawerConfig = reactive({
    title: '配置',
    visible: false,
    width: '400',
    okConfig: {
        title: '确定',
        ok: okEvent,
        disabled: false,
        loading: false,
    },
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false,
    },
    zIndex: 1100,
    closeOnClickModal: false,
})

const formData = reactive<{
    baseConfig: any
    prompt: any
    resources: any
}>({
    baseConfig: {
        maxTokens: 0,
        seed: 0,
        topK: 0,
        topP: 0,
        temperature: 0,
        repetitionPenalty: 0,
        enableSearch: false
    },
    prompt: '',
    resources: null
})

function showModal(cb: () => void) {
    callback.value = cb
    getConfigData()
    getFileListOptions()

    drawerConfig.visible = true;
}

function getConfigData() {
    GetConfigAppData({
        id: route.query.id
    }).then((res: any) => {
        Object.keys(formData).forEach((key: string) => {
            if (res.data[key]) {
                formData[key] = res.data[key]
            }
        })
        formData.prompt = res.data.prompt || ''
        formData.resources = res.data.resources || []
    }).catch((error: any) => {
        console.error('查询详情报错', error)
    })
}

function okEvent() {
    // 获取cron表达式
    formRef.value?.validate((valid: boolean) => {
        if (valid) {
            callback.value(formData).then((res: any) => {
                drawerConfig.okConfig.loading = false
                if (res === undefined) {
                    drawerConfig.visible = false
                } else {
                    drawerConfig.visible = true
                }
            }).catch((err: any) => {
                drawerConfig.okConfig.loading = false
            })
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

function getFileListOptions() {
    GetFileCenterList({
        page: 0,
        pageSize: 9999,
        searchKeyWord: ''
    }).then((res: any) => {
        resourcesList.value = res.data.content
    }).catch(() => {
        resourcesList.value = []
    })
}

function closeEvent() {
    drawerConfig.visible = false;
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.app-config-container {
    padding: 12px;
    .el-form {
        padding: 0 0 0;
        box-sizing: border-box;
        .config-item {
            .item-title {
                font-size: 12px;
                padding-bottom: 12px;
                box-sizing: border-box;
                border-bottom: 1px solid #ebeef5;
                margin-bottom: 16px;
            }

            .el-form-item {
                position: relative;

                .tooltip-msg {
                    position: absolute;
                    top: 8px;
                    color: getCssVar('color', 'info');
                    font-size: 16px;
                }

                // 全屏样式
                &.show-screen__full {
                    position: fixed;
                    width: 100%;
                    height: 100%;
                    top: 0;
                    left: 0;
                    background-color: #ffffff;
                    padding: 12px 20px;
                    box-sizing: border-box;
                    transition: all 0.15s linear;
                    z-index: 10;
                    display: flex;
                    flex-direction: column;

                    .el-form-item__content {
                        align-items: flex-start;
                        height: 100%;
                        margin-top: 18px;

                        .modal-full-screen {
                            top: -36px;
                            right: 0;
                            left: unset;
                        }

                        .vue-codemirror {
                            height: calc(100% - 48px);
                        }
                    }
                }

                .el-form-item__label {
                    pointer-events: none;
                }

                .el-form-item__content {
                    .el-radio-group {
                        justify-content: flex-end;
                    }

                    .vue-codemirror {
                        height: 100px;
                        width: 100%;

                        .cm-editor {
                            height: 100%;
                            outline: none;
                            border: 1px solid #dcdfe6;
                        }

                        .cm-gutters {
                            font-size: 12px;
                            font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                        }

                        .cm-content {
                            font-size: 12px;
                            font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                        }

                        .cm-tooltip-autocomplete {

                            // display: none !important;
                            ul {
                                li {
                                    height: 40px;
                                    display: flex;
                                    align-items: center;
                                    font-size: 12px;
                                    background-color: #ffffff;
                                    font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                                }

                                li[aria-selected] {
                                    background: #409EFF;
                                }

                                .cm-completionIcon {
                                    margin-right: -4px;
                                    opacity: 0;
                                }
                            }
                        }
                    }

                    // 全屏样式
                    .modal-full-screen {
                        position: absolute;
                        top: 8px;
                        left: -22px;
                        cursor: pointer;

                        &:hover {
                            color: getCssVar('color', 'primary');
                            ;
                        }
                    }
                }
            }
        }
    }

    .el-divider {
        margin: 0 0 12px;
    }
}
</style>