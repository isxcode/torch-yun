<template>
    <BlockModal :model-config="modelConfig">
        <el-form ref="form" class="add-computer-group" label-position="top" :model="formData" :rules="rules">
            <el-form-item label="名称" prop="name">
                <el-input
                    v-model="formData.name"
                    maxlength="200"
                    placeholder="请输入"
                />
            </el-form-item>
            <el-form-item label="类型" prop="aiType">
                <el-select v-model="formData.aiType" placeholder="请选择" @change="typeChangeEvent">
                    <el-option
                        v-for="item in typeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item v-if="formData.aiType === 'API'" label="组织名称" prop="orgName">
                <el-select v-model="selectedOrgName" placeholder="请选择" filterable @change="onOrgNameChange">
                    <el-option
                        v-for="item in orgNameList"
                        :key="item"
                        :label="item"
                        :value="item"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="模型" prop="modelId">
                <el-select v-model="formData.modelId" placeholder="请选择" :disabled="formData.aiType === 'API' && !selectedOrgName">
                    <el-option
                        v-for="item in modelIdListOptions"
                        :key="item.id"
                        :label="item.name || item.modelName"
                        :value="item.id"
                    />
                </el-select>
            </el-form-item>
            <el-form-item v-if="formData.aiType === 'API' && selectedOrgName === 'Volcengine'" label="EndPointId" prop="authConfig.endpointId">
                <el-input v-model="formData.authConfig.endpointId" maxlength="100" show-password placeholder="请输入" />
            </el-form-item>
            <el-form-item v-if="formData.aiType === 'API'" label="Key" prop="authConfig.apiKey">
                <el-input v-model="formData.authConfig.apiKey" maxlength="100" type="password" show-password placeholder="请输入" />
            </el-form-item>
            <el-form-item v-if="formData.aiType === 'LOCAL'" label="集群" prop="clusterConfig.clusterId">
                <el-select v-model="formData.clusterConfig.clusterId" placeholder="请选择">
                    <el-option
                        v-for="item in clusterIdList"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="备注">
                <el-input v-model="formData.remark" show-word-limit type="textarea" maxlength="200"
                    :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入" />
            </el-form-item>
        </el-form>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { QueryModelList } from '@/services/model-management.service';
import { QueryModelPlazaList } from '@/services/model-plaza.service';
import { GetComputerGroupList } from '@/services/computer-group.service';

interface ApiKey {
    apiKey: string
    endpointId: string
}

interface ClusterConfig {
    clusterId: string
}

interface FormParams {
    name: string
    modelId: string
    remark: string
    authConfig: ApiKey
    clusterConfig: ClusterConfig
    aiType: string
    id?: string
}

interface Option {
    label: string
    value: string
}

const form = ref<FormInstance>()
const callback = ref<any>()
const renderSence = ref('new')
const typeList = ref<Option[]>([
    {
        label: '本地部署',
        value: 'LOCAL'
    },
    {
        label: '远程接入',
        value: 'API'
    }
])
const clusterIdList = ref<any[]>([])
const orgNameList = ref<string[]>([])
const selectedOrgName = ref<string>('')
const modelConfig = reactive({
    title: '添加',
    visible: false,
    width: '520px',
    okConfig: {
        title: '确定',
        ok: okEvent,
        disabled: false,
        loading: false
    },
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false
    },
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})
const formData = reactive<FormParams>({
    name: '',
    modelId: '',
    remark: '',
    aiType: 'LOCAL',
    authConfig: {
        apiKey: '',
        endpointId: ''
    },
    clusterConfig: {
        clusterId: ''
    },
    id: ''
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入名称', trigger: ['change', 'blur']}],
    aiType: [{ required: true, message: '请选择类型', trigger: ['change', 'blur']}],
    orgName: [{ required: true, message: '请选择组织名称', trigger: ['change', 'blur'], validator: validateOrgName }],
    modelId: [{ required: true, message: '请选择模型', trigger: ['change', 'blur']}],
    'authConfig.apiKey': [{ required: true, message: '请输入Key', trigger: ['change', 'blur'], validator: validateApiKey }],
    'authConfig.endpointId': [{ required: true, message: '请输入EndPointId', trigger: ['change', 'blur'], validator: validateEndpointId }],
    'clusterConfig.clusterId': [{ required: true, message: '请选择集群', trigger: ['change', 'blur'], validator: validateClusterId }],
})

function validateOrgName(rule: any, value: any, callback: any) {
    if (formData.aiType === 'API' && !selectedOrgName.value) {
        callback(new Error('请选择组织名称'))
    } else {
        callback()
    }
}

function validateApiKey(rule: any, value: any, callback: any) {
    if (formData.aiType === 'API' && !formData.authConfig.apiKey) {
        callback(new Error('请输入Key'))
    } else {
        callback()
    }
}

function validateEndpointId(rule: any, value: any, callback: any) {
    if (formData.aiType === 'API' && selectedOrgName.value === 'Volcengine' && !formData.authConfig.endpointId) {
        callback(new Error('请输入EndPointId'))
    } else {
        callback()
    }
}

function validateClusterId(rule: any, value: any, callback: any) {
    if (formData.aiType === 'local' && !formData.clusterConfig.clusterId) {
        callback(new Error('请选择集群'))
    } else {
        callback()
    }
}

const modelIdListOptions = ref<any[]>([])

function showModal(cb: () => void, data: any): void {
    callback.value = cb
    getClusterIdListOptions()
    // 先重置表单
    nextTick(() => {
        form.value?.resetFields()
    })
    if (data) {
        modelConfig.title = '编辑'
        renderSence.value = 'edit'
        nextTick(() => {
            Object.keys(formData).forEach((key: string) => {
                if (key === 'authConfig' && !data[key]) {
                    formData[key] = {
                        apiKey: ''
                    }
                } else if (key === 'clusterConfig' && !data[key]) {
                    formData[key] = {
                        clusterId: ''
                    }
                } else {
                    formData[key] = data[key]
                }
            })
            // 编辑时，根据类型初始化
            if (data.aiType === 'API') {
                getOrgNameList()
                // 如果有orgName，设置并获取模型列表
                if (data.orgName) {
                    selectedOrgName.value = data.orgName
                    getModelListByOrgName()
                }
            } else {
                getModelListOptions()
            }
        })
    } else {
        modelConfig.title = '添加'
        renderSence.value = 'new'
        selectedOrgName.value = ''
        orgNameList.value = []
        modelIdListOptions.value = []
        // 使用nextTick确保在resetFields之后设置值
        nextTick(() => {
            formData.name = ''
            formData.modelId = ''
            formData.aiType = 'LOCAL'
            formData.remark = ''
            formData.authConfig = { apiKey: '', endpointId: '' }
            formData.clusterConfig = { clusterId: '' }
            formData.id = ''
            getModelListOptions()
        })
    }
    modelConfig.visible = true
}

function okEvent() {
    form.value?.validate((valid: boolean) => {
        if (valid) {
            modelConfig.okConfig.loading = true
            callback.value({
                ...formData,
                id: formData.id ? formData.id : undefined
            }).then((res: any) => {
                modelConfig.okConfig.loading = false
                if (res === undefined) {
                    modelConfig.visible = false
                } else {
                    modelConfig.visible = true
                }
            }).catch(() => {
                modelConfig.okConfig.loading = false
            })
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

function typeChangeEvent() {
    formData.modelId = ''
    selectedOrgName.value = ''
    modelIdListOptions.value = []
    if (formData.aiType === 'LOCAL') {
        // 本地部署：直接获取模型列表
        getModelListOptions()
    } else {
        // 远程接入：先获取组织名称列表
        getOrgNameList()
    }
}

function getModelListOptions() {
    // 本地部署：调用模型仓库接口
    QueryModelList({
        page: 0,
        pageSize: 9999,
        searchKeyWord: ''
    }).then((res: any) => {
        modelIdListOptions.value = res.data.content
    }).catch(() => {
        modelIdListOptions.value = []
    })
}

// 获取组织名称列表（查询在线模型的组织名称）
function getOrgNameList() {
    QueryModelPlazaList({
        page: 0,
        pageSize: 9999,
        searchKeyWord: '',
        isOnline: 'ENABLE'
    }).then((res: any) => {
        const orgNames = res.data.content.map((item: any) => item.orgName)
        orgNameList.value = [...new Set(orgNames)] as string[]
    }).catch(() => {
        orgNameList.value = []
    })
}

// 组织名称变化时，清空模型选择并重新获取模型列表
function onOrgNameChange() {
    formData.modelId = ''
    getModelListByOrgName()
}

// 根据组织名称获取模型列表
function getModelListByOrgName() {
    if (!selectedOrgName.value) {
        modelIdListOptions.value = []
        return
    }
    QueryModelPlazaList({
        page: 0,
        pageSize: 9999,
        searchKeyWord: '',
        orgName: selectedOrgName.value,
        isOnline: 'ENABLE'
    }).then((res: any) => {
        modelIdListOptions.value = res.data.content
    }).catch(() => {
        modelIdListOptions.value = []
    })
}

function getClusterIdListOptions() {
    GetComputerGroupList({
        page: 0,
        pageSize: 9999,
        searchKeyWord: ''
    }).then((res: any) => {
        clusterIdList.value = res.data.content
    }).catch(() => {
        clusterIdList.value = []
    })
}

function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.add-computer-group {
    padding: 12px 20px 0 20px;
    box-sizing: border-box;
}
</style>
