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
            <el-form-item label="模型文件" prop="modelFile">
                <el-select v-model="formData.modelFile" placeholder="请选择" filterable>
                    <el-option
                        v-for="item in fileList"
                        :key="item.id"
                        :label="item.fileName"
                        :value="item.id"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="组织名称" prop="orgName">
                <el-select v-model="selectedOrgName" placeholder="请选择" filterable @change="onOrgNameChange">
                    <el-option
                        v-for="item in orgNameList"
                        :key="item"
                        :label="item"
                        :value="item"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="模型名称" prop="modelPlazaId">
                <el-select v-model="formData.modelPlazaId" placeholder="请选择" filterable :disabled="!selectedOrgName">
                    <el-option
                        v-for="item in modelList"
                        :key="item.id"
                        :label="item.modelName"
                        :value="item.id"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="部署脚本">
                <el-input v-model="formData.deployScript" type="textarea"
                    :autosize="{ minRows: 4, maxRows: 8 }" placeholder="请输入部署脚本（可选）" />
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
import { GetFileCenterList } from '@/services/file-center.service'
import { QueryModelPlazaList } from '@/services/model-plaza.service'

interface FormParams {
    name: string
    modelPlazaId: string
    modelFile: string
    deployScript: string
    remark: string
    id?: string
}

const form = ref<FormInstance>()
const callback = ref<any>()
const renderSence = ref('new')
const fileList = ref<any[]>([])
const orgNameList = ref<string[]>([])
const modelList = ref<any[]>([])
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
    modelPlazaId: '',
    modelFile: '',
    deployScript: '',
    remark: '',
    id: ''
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入名称', trigger: ['change', 'blur']}],
    orgName: [{ required: true, message: '请选择组织名称', trigger: ['change', 'blur'], validator: validateOrgName }],
    modelPlazaId: [{ required: true, message: '请选择模型名称', trigger: ['change', 'blur']}],
    modelFile: [{ required: true, message: '请选择模型文件', trigger: ['change', 'blur']}]
})

function validateOrgName(rule: any, value: any, callback: any) {
    if (!selectedOrgName.value) {
        callback(new Error('请选择组织名称'))
    } else {
        callback()
    }
}

function onOrgNameChange() {
    // 组织名称变化时，清空模型选择并重新获取模型列表
    formData.modelPlazaId = ''
    getModelListByOrgName()
}

function showModal(cb: () => void, data: any): void {
    callback.value = cb
    getFileListOptions()
    getOrgNameList()
    if (data) {
        modelConfig.title = '编辑'
        renderSence.value = 'edit'
        Object.keys(formData).forEach((key: string) => {
            formData[key] = data[key] || ''
        })
        // 编辑时，设置orgName并获取模型列表
        if (data.orgName) {
            selectedOrgName.value = data.orgName
            getModelListByOrgName()
        }
    } else {
        modelConfig.title = '添加'
        renderSence.value = 'new'
        selectedOrgName.value = ''
        modelList.value = []
        Object.keys(formData).forEach((key: string) => {
            formData[key] = ''
        })
    }
    nextTick(() => {
        form.value?.resetFields()
    })
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

function getFileListOptions() {
    GetFileCenterList({
        page: 0,
        pageSize: 9999,
        searchKeyWord: ''
    }).then((res: any) => {
        fileList.value = res.data.content
    }).catch(() => {
        fileList.value = []
    })
}

// 获取组织名称列表（过滤离线模型，只获取在线模型的组织名称）
function getOrgNameList() {
    QueryModelPlazaList({
        page: 0,
        pageSize: 9999,
        searchKeyWord: '',
        isOnline: 'DISABLE'
    }).then((res: any) => {
        const orgNames = res.data.content.map((item: any) => item.orgName)
        orgNameList.value = [...new Set(orgNames)] as string[]
    }).catch(() => {
        orgNameList.value = []
    })
}

// 根据组织名称获取模型列表（过滤离线模型）
function getModelListByOrgName() {
    if (!selectedOrgName.value) {
        modelList.value = []
        return
    }
    QueryModelPlazaList({
        page: 0,
        pageSize: 9999,
        searchKeyWord: '',
        orgName: selectedOrgName.value,
        isOnline: 'DISABLE'
    }).then((res: any) => {
        modelList.value = res.data.content
    }).catch(() => {
        modelList.value = []
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
