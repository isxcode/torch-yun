export interface Menu {
  icon: string;
  name: string;
  code: string;
  authType?: Array<string>;
  childPage?: Array<string>
  children?: Array<Menu>
}

// ROLE_SYS_ADMIN
export const menuListData: Array<Menu> = [
  {
    code: 'index',
    name: '首页',
    icon: 'Monitor',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
    childPage: []
  },
  {
    code: 'resource-management',
    name: '资源管理',
    icon: 'School',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
    children: [
      {
        code: 'computer-group',
        name: '计算集群',
        icon: 'UploadFilled',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: ['computer-pointer']
      },
      {
        code: 'file-center',
        name: '资源中心',
        icon: 'Paperclip',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      }
    ]
  },
  {
    code: 'ai-center',
    name: 'AI中心',
    icon: 'Service',
    authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
    children: [
      {
        code: 'model-plaza',
        name: '模型广场',
        icon: 'Shop',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      },
      {
        code: 'model-management',
        name: '模型仓库',
        icon: 'Connection',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      },
      {
        code: 'ai-item',
        name: '智能体',
        icon: 'ChatLineSquare',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: []
      },
      {
        code: 'app-management',
        name: '应用管理',
        icon: 'HelpFilled',
        authType: [ 'ROLE_TENANT_MEMBER', 'ROLE_TENANT_ADMIN' ],
        childPage: ['app-detail']
      }
    ]
  },
  {
    code: 'tenant-management',
    name: '租户管理',
    icon: 'OfficeBuilding',
    authType: [ 'ROLE_TENANT_ADMIN' ],
    children: [
      {
        code: 'tenant-user',
        name: '租户成员',
        icon: 'User',
        authType: [ 'ROLE_TENANT_ADMIN' ],
        childPage: []
      }
    ]
  },
  {
    code: 'user-center',
    name: '用户中心',
    icon: 'UserFilled',
    authType: [ 'ROLE_SYS_ADMIN' ],
    childPage: []
  },
  {
    code: 'tenant-list',
    name: '租户列表',
    icon: 'List',
    authType: [ 'ROLE_SYS_ADMIN' ],
    childPage: []
  },
  {
    code: 'tenant-user',
    name: '租户成员',
    icon: 'User',
    authType: [ 'ROLE_SYS_ADMIN' ],
    childPage: []
  },
  {
    code: 'license',
    name: '证书安装',
    icon: 'Files',
    authType: [ 'ROLE_SYS_ADMIN' ],
    childPage: []
  }
]
