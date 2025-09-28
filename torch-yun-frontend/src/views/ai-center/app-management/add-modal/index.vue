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
            <el-form-item label="智能体" prop="aiId">
                <el-select v-model="formData.aiId" placeholder="请选择">
                    <el-option
                        v-for="item in aiIdList"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="应用类型" prop="appType">
                <el-select v-model="formData.appType" placeholder="请选择">
                    <el-option
                        v-for="item in appTypeList"
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
import { QueryAiItemList } from '@/services/ai-item.service'

interface FormParams {
    name: string
    logoId: string
    aiId: string
    remark: string
    id?: string
    appType: string
}

const form = ref<FormInstance>()
const callback = ref<any>()
const renderSence = ref('new')
const aiIdList = ref<any[]>([])
const appTypeList = ref<any[]>([
    { name: '普通对话应用', id: 'TEXT_APP' },
    { name: 'Python应用', id: 'PYTHON_APP' },
    { name: '脚本应用', id: 'BASH_APP' },
    { name: 'NodeJs应用', id: 'NODE_JS_APP' },
    { name: '报表应用', id: 'REPORT_APP' },
    { name: '视图应用', id: 'VIEW_APP' },
    { name: '日志分析应用', id: 'LOG_APP' },
    { name: '股票分析应用', id: 'IMAGE_APP' }
])
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
    logoId: '',
    aiId: '',
    remark: '',
    appType: '',
    id: ''
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入名称', trigger: ['change', 'blur']}],
    aiId: [{ required: true, message: '请选择智能体', trigger: ['change', 'blur']}],
    appType: [{ required: true, message: '请选择应用类型', trigger: ['change', 'blur']}],
})

function showModal(cb: () => void, data: any): void {
    callback.value = cb
    getAiListOptions()
    if (data) {
        modelConfig.title = '编辑'
        renderSence.value = 'edit'
        Object.keys(formData).forEach((key: string) => {
            formData[key] = data[key]
        })
        formData.logoId = ''
    } else {
        modelConfig.title = '添加'
        renderSence.value = 'new'
        formData.name = ''
        formData.logoId = 'demo'
        formData.aiId = ''
        formData.appType = ''
        formData.remark = ''
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

function getAiListOptions() {
    QueryAiItemList({
        page: 0,
        pageSize: 9999,
        searchKeyWord: ''
    }).then((res: any) => {
        aiIdList.value = res.data.content
    }).catch(() => {
        aiIdList.value = []
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
