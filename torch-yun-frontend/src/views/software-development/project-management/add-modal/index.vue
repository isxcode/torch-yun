<template>
  <BlockModal :model-config="modelConfig">
    <el-form ref="form" class="add-project" label-position="top" :model="formData" :rules="rules">
      <el-form-item label="项目名" prop="name"><el-input v-model="formData.name" maxlength="200" placeholder="请输入" /></el-form-item>
      <el-form-item label="工作空间" prop="workspace"><el-input v-model="formData.workspace" maxlength="500" placeholder="请输入" /></el-form-item>
      <el-form-item label="资产目录" prop="assetsDir"><el-input v-model="formData.assetsDir" maxlength="500" placeholder="请输入" /></el-form-item>
      <el-form-item label="项目设计" prop="designAppId"><el-select v-model="formData.designAppId" placeholder="请选择"><el-option v-for="item in appList" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
      <el-form-item label="项目计划" prop="planAppId"><el-select v-model="formData.planAppId" placeholder="请选择"><el-option v-for="item in appList" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
      <el-form-item label="项目开发" prop="developAppId"><el-select v-model="formData.developAppId" placeholder="请选择"><el-option v-for="item in appList" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
      <el-form-item label="备注"><el-input v-model="formData.remark" show-word-limit type="textarea" maxlength="200" :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入" /></el-form-item>
    </el-form>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { QueryAppList } from '@/services/app-management.service'

interface FormParams { id?: string; name: string; workspace: string; assetsDir: string; designAppId: string; planAppId: string; developAppId: string; remark: string }

const form = ref<FormInstance>()
const callback = ref<any>()
const appList = ref<any[]>([])
const modelConfig = reactive({ title: '添加', visible: false, width: '520px', okConfig: { title: '确定', ok: okEvent, disabled: false, loading: false }, cancelConfig: { title: '取消', cancel: closeEvent, disabled: false }, needScale: false, zIndex: 1100, closeOnClickModal: false })
const formData = reactive<FormParams>({ name: '', workspace: '', assetsDir: 'spec', designAppId: '', planAppId: '', developAppId: '', remark: '', id: '' })
const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入项目名', trigger: ['change', 'blur'] }],
  workspace: [{ required: true, message: '请输入工作空间', trigger: ['change', 'blur'] }],
  assetsDir: [{ required: true, message: '请输入资产目录', trigger: ['change', 'blur'] }],
  designAppId: [{ required: true, message: '请选择项目设计模型应用', trigger: ['change', 'blur'] }],
  planAppId: [{ required: true, message: '请选择项目计划模型应用', trigger: ['change', 'blur'] }],
  developAppId: [{ required: true, message: '请选择项目开发模型应用', trigger: ['change', 'blur'] }]
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  getAppListOptions()
  if (data) {
    modelConfig.title = '编辑'
    Object.keys(formData).forEach((key: string) => { formData[key] = data[key] })
  } else {
    modelConfig.title = '添加'
    formData.id = ''
    formData.name = ''
    formData.workspace = ''
    formData.assetsDir = 'spec'
    formData.designAppId = ''
    formData.planAppId = ''
    formData.developAppId = ''
    formData.remark = ''
  }
  nextTick(() => form.value?.resetFields())
  modelConfig.visible = true
}

function okEvent() {
  form.value?.validate((valid: boolean) => {
    if (valid) {
      modelConfig.okConfig.loading = true
      callback.value({ ...formData, id: formData.id ? formData.id : undefined }).then((res: any) => {
        modelConfig.okConfig.loading = false
        modelConfig.visible = res !== undefined
      }).catch(() => { modelConfig.okConfig.loading = false })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}

function getAppListOptions() {
  QueryAppList({ page: 0, pageSize: 9999, searchKeyWord: '' }).then((res: any) => { appList.value = res.data.content || [] }).catch(() => { appList.value = [] })
}

function closeEvent() { modelConfig.visible = false }

defineExpose({ showModal })
</script>

<style lang="scss">
.add-project { padding: 12px 20px 0 20px; box-sizing: border-box; }
</style>
