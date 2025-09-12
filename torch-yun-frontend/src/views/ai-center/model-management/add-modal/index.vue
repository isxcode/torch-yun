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
            <el-form-item label="编码" prop="code">
                <!-- <el-input
                    v-model="formData.code"
                    maxlength="200"
                    placeholder="请输入"
                /> -->
                <el-select v-model="formData.code" placeholder="请选择">
                    <el-option
                        v-for="item in codeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="标签" prop="code">
                <el-input
                    v-model="formData.modelLabel"
                    maxlength="200"
                    placeholder="请输入"
                />
            </el-form-item>
            <el-form-item label="模型文件" prop="modelFile" v-if="modelType !== 'API'">
                <el-select v-model="formData.modelFile" placeholder="请选择">
                    <el-option
                        v-for="item in fileList"
                        :key="item.id"
                        :label="item.fileName"
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
import { GetFileCenterList } from '@/services/file-center.service'

interface FormParams {
    name: string
    code: string
    modelLabel: string
    modelFile: string
    remark: string
    id?: string
}

const form = ref<FormInstance>()
const callback = ref<any>()
const renderSence = ref('new')
const fileList = ref<any[]>([])
const codeList = ref([
    {
        label: 'Qwen2.5-0.5B',
        value: 'Qwen2.5-0.5B'
    },
    {
        label: 'Gemma3-270M',
        value: 'Gemma3-270M'
    }
])
const modelType = ref<string>('')
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
    code: '',
    modelLabel: '',
    modelFile: '',
    remark: '',
    id: ''
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入名称', trigger: ['change', 'blur']}],
    code: [{ required: true, message: '请输入编码', trigger: ['change', 'blur']}],
    modelLabel: [{ required: true, message: '请输入标签', trigger: ['change', 'blur']}],
    modelFile: [{ required: true, message: '请选择资源文件', trigger: ['change', 'blur']}]
})

function showModal(cb: () => void, data: any): void {
    callback.value = cb
    getFileListOptions()
    if (data) {
        modelConfig.title = '编辑'
        renderSence.value = 'edit'
        modelType.value = data.modelType
        Object.keys(formData).forEach((key: string) => {
            formData[key] = data[key]
        })
    } else {
        modelType.value = 'MANUAL'
        modelConfig.title = '添加'
        renderSence.value = 'new'
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
