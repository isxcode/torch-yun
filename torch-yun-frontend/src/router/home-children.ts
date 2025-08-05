// 开源免费部分
import HomeOverview from '@/views/home-overview/index.vue'
import ComputerGroup from '@/views/computer-group/index.vue'
import ComputerPointer from '@/views/computer-group/computer-pointer/index.vue'

import UserCenter from '@/views/user-center/index.vue'
import TenantList from '@/views/tenant-list/index.vue'
import License from '@/views/license/index.vue'
import TenantUser from '@/views/tenant-user/index.vue'
import PersonalInfo from '@/views/personal-info/index.vue'
import fileCenter from '@/views/file-center/index.vue'

import AiItem from '@/views/ai-center/ai-item/index.vue'
import ModelManagement from '@/views/ai-center/model-management/index.vue'
import AppManagement from '@/views/ai-center/app-management/index.vue'
import AppDetail from '@/views/ai-center/app-management/detail-page/index.vue'

export default [
  {
    path: 'index',
    name: 'index',
    component: HomeOverview
  },
  {
    path: 'computer-group',
    name: 'computer-group',
    component: ComputerGroup
  },
  {
    path: 'computer-pointer',
    name: 'computer-pointer',
    component: ComputerPointer
  },
  {
    path: 'tenant-user',
    name: 'tenant-user',
    component: TenantUser
  },
  {
    path: 'user-center',
    name: 'user-center',
    component: UserCenter
  },
  {
    path: 'tenant-list',
    name: 'tenant-list',
    component: TenantList
  },
  {
    path: 'license',
    name: 'license',
    component: License
  },
  {
    path: 'file-center',
    name: 'file-center',
    component: fileCenter
  },
  {
    path: 'personal-info',
    name: 'personalInfo',
    component: PersonalInfo
  },
  {
    path: 'model-management',
    name: 'model-management',
    component: ModelManagement
  },
  {
    path: 'ai-item',
    name: 'ai-item',
    component: AiItem
  },
  {
    path: 'app-management',
    name: 'app-management',
    component: AppManagement
  },
  {
    path: 'app-management',
    name: 'app-management',
    component: AppManagement,
  },
  {
    path: 'app-detail',
    name: 'app-detail',
    component: AppDetail,
  }
]
