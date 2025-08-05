`
<template>
    <BlockDrawer :drawer-config="drawerConfig">
        <el-scrollbar>

        </el-scrollbar>
    </BlockDrawer>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import BlockDrawer from '@/components/block-drawer/index.vue'

const drawerConfig = reactive({
    title: '配置',
    visible: false,
    width: '400',
    customClass: 'ai-history-drawer',
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
});

function showModal(data?: any) {

    drawerConfig.visible = true;
}

function okEvent() {

}

function closeEvent() {
    drawerConfig.visible = false;
}

defineExpose({
    showModal,
})
</script>

<style lang="scss">
.ai-history-drawer {

}
</style>

`