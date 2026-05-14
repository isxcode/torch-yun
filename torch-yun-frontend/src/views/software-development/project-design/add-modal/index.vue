<template>
  <BlockModal :model-config="modelConfig">
    <el-form ref="form" class="add-project-design" label-position="top" :model="formData" :rules="rules">
      <el-form-item label="名称" prop="name"><el-input v-model="formData.name" maxlength="200" placeholder="请输入" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="formData.remark" show-word-limit type="textarea" maxlength="200" :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入" /></el-form-item>
    </el-form>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'

interface FormParams { id?: string; name: string; remark: string }

const form = ref<FormInstance>()
const callback = ref<any>()
const modelConfig = reactive({ title: '添加', visible: false, width: '520px', okConfig: { title: '确定', ok: okEvent, disabled: false, loading: false }, cancelConfig: { title: '取消', cancel: closeEvent, disabled: false }, needScale: false, zIndex: 1100, closeOnClickModal: false })
const formData = reactive<FormParams>({ name: '', remark: '', id: '' })
const rules = reactive<FormRules>({ name: [{ required: true, message: '请输入名称', trigger: ['change', 'blur'] }] })

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  if (data) {
    modelConfig.title = '编辑'
    formData.id = data.id
    formData.name = data.name
    formData.remark = data.remark
  } else {
    modelConfig.title = '添加'
    formData.id = ''
    formData.name = ''
    formData.remark = ''
  }
  nextTick(() => form.value?.resetFields())
  modelConfig.visible = true
}

function okEvent() {
  form.value?.validate((valid: boolean) => {
    if (!valid) {
      ElMessage.warning('请将表单输入完整')
      return
    }
    modelConfig.okConfig.loading = true
    callback.value({ ...formData, id: formData.id ? formData.id : undefined }).then((res: any) => {
      modelConfig.okConfig.loading = false
      modelConfig.visible = res !== undefined
    }).catch(() => { modelConfig.okConfig.loading = false })
  })
}

function closeEvent() { modelConfig.visible = false }
defineExpose({ showModal })
</script>

<style lang="scss">
.add-project-design { padding: 12px 20px 0 20px; box-sizing: border-box; }
</style>
