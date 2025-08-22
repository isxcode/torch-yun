<template>
    <div class="app-config-panel">
        <el-scrollbar class="panel-content">
            <div class="app-config-container">
                <el-form
                    ref="formRef"
                    label-position="left"
                    label-width="80px"
                    :model="formData"
                    :rules="basicFormRules"
                    size="small"
                >
                    <!-- 基础配置 -->
                    <div class="config-item">
                        <div class="item-title">基础配置</div>
                        <el-form-item label="文本长度" prop="maxTokens">
                            <el-tooltip
                                content="控制生成文本的最大长度"
                                placement="left"
                            >
                                <el-icon class="tooltip-msg"><QuestionFilled /></el-icon>
                            </el-tooltip>
                            <el-input-number
                                v-model="formData.baseConfig.maxTokens"
                                :min="0"
                                controls-position="right"
                                style="width: 100%"
                                size="small"
                            />
                        </el-form-item>
                        <el-form-item label="随机数" prop="seed">
                            <el-tooltip
                                content="随机数生成器的种子，用于确保结果可复现"
                                placement="left"
                            >
                                <el-icon class="tooltip-msg"><QuestionFilled /></el-icon>
                            </el-tooltip>
                            <el-input-number
                                v-model="formData.baseConfig.seed"
                                :min="0"
                                controls-position="right"
                                style="width: 100%"
                                size="small"
                            />
                        </el-form-item>
                        <el-form-item label="候选词数" prop="topK">
                            <el-tooltip
                                content="示例：设为 50 时，仅考虑模型认为最可能的50个候选词"
                                placement="left"
                            >
                                <el-icon class="tooltip-msg"><QuestionFilled /></el-icon>
                            </el-tooltip>
                            <el-input-number
                                v-model="formData.baseConfig.topK"
                                :min="0"
                                controls-position="right"
                                style="width: 100%"
                                size="small"
                            />
                        </el-form-item>
                        <el-form-item label="累积概率" prop="topP">
                            <el-tooltip
                                content="示例：设为 0.9 时，选择概率累加达90%的最小词集"
                                placement="left"
                            >
                                <el-icon class="tooltip-msg"><QuestionFilled /></el-icon>
                            </el-tooltip>
                            <el-input-number
                                v-model="formData.baseConfig.topP"
                                :min="0"
                                :max="1"
                                :step="0.01"
                                controls-position="right"
                                style="width: 100%"
                                size="small"
                            />
                        </el-form-item>
                        <el-form-item label="采样随机性" prop="temperature">
                            <el-tooltip
                                content="调节采样随机性。值越高（如 1.0）结果越多样；值越低（如 0.1）越保守"
                                placement="left"
                            >
                                <el-icon class="tooltip-msg"><QuestionFilled /></el-icon>
                            </el-tooltip>
                            <el-input-number
                                v-model="formData.baseConfig.temperature"
                                :min="0"
                                :max="1"
                                :step="0.01"
                                controls-position="right"
                                style="width: 100%"
                                size="small"
                            />
                        </el-form-item>
                        <el-form-item label="联网检索" prop="enableSearch">
                            <el-tooltip
                                content="是否结合外部搜索（如实时检索）增强生成内容的事实性"
                                placement="left"
                            >
                                <el-icon class="tooltip-msg"><QuestionFilled /></el-icon>
                            </el-tooltip>
                            <el-switch v-model="formData.baseConfig.enableSearch" size="small" />
                        </el-form-item>
                    </div>
                    
                    <!-- 提示词配置 -->
                    <div class="config-item">
                        <div class="item-title">提示词</div>
                        <el-form-item label="提示词" prop="prompt">
                            <el-input 
                                v-model="formData.prompt" 
                                type="textarea"
                                :rows="3"
                                placeholder="请输入提示词"
                            />
                        </el-form-item>
                    </div>
                    
                    <!-- 知识库配置 -->
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
                                style="width: 100%"
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

                <!-- 保存按钮 -->
                <div class="save-button-container">
                    <el-button
                        type="primary"
                        :loading="saveLoading"
                        @click="saveConfig"
                        style="width: 100%"
                    >
                        保存配置
                    </el-button>
                </div>
            </div>
        </el-scrollbar>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import { GetConfigAppData, ConfigAppData } from '@/services/app-management.service'
import { GetFileCenterList } from '@/services/file-center.service'

const route = useRoute()

const basicFormRules = ref([])
const formRef = ref<any>()
const resourcesList = ref<any[]>([])
const saveLoading = ref(false)

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
    resources: []
})

// 获取配置数据
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

// 获取文件列表
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

// 保存配置
function saveConfig() {
    formRef.value?.validate((valid: boolean) => {
        if (valid) {
            saveLoading.value = true
            ConfigAppData({
                id: route.query.id,
                ...formData
            }).then((res: any) => {
                ElMessage.success(res.msg)
                saveLoading.value = false
            }).catch((err: any) => {
                saveLoading.value = false
            })
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

// 监听路由变化，重新加载数据
watch(() => route.query.id, (newId) => {
    if (newId) {
        getConfigData()
    }
})

onMounted(() => {
    getConfigData()
    getFileListOptions()
})

defineExpose({
    saveConfig,
    getConfigData
})
</script>

<style lang="scss" scoped>
.app-config-panel {
    height: 100%;
    display: flex;
    flex-direction: column;
    background: #fff;
    border-left: 1px solid #e4e7ed;

    .panel-content {
        flex: 1;
        height: 100%;
    }

    .save-button-container {
        padding: 16px 20px 20px;
        border-top: 1px solid #f0f0f0;
        background: #fff;
        margin-top: 16px;
    }
}
</style>

<style lang="scss">
.app-config-panel {
    .app-config-container {
        padding: 40px;

        .el-form {
            .config-item {
                margin-bottom: 20px;

                .item-title {
                    font-size: 14px;
                    font-weight: 500;
                    padding-bottom: 10px;
                    border-bottom: 1px solid #ebeef5;
                    margin-bottom: 14px;
                    color: #303133;
                }

                .el-form-item {
                    position: relative;
                    margin-bottom: 14px;

                    .tooltip-msg {
                        position: absolute;
                        top: 6px;
                        right: -18px;
                        color: #909399;
                        font-size: 13px;
                        cursor: help;
                    }

                    .el-form-item__label {
                        font-size: 12px;
                        color: #606266;
                        padding-right: 8px;
                    }

                    .el-form-item__content {
                        margin-left: 0 !important;
                    }
                }
            }
        }

        .save-button-container {
            margin-top: 0;
        }
    }
}
</style>
