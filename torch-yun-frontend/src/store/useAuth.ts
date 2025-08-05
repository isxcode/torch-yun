import { defineStore } from "pinia";

interface ChatInfo {
  chatId: string
  isTalking: boolean,
  appInfo: any
}

interface AuthState {
  userInfo: Record<string, any>
  token: string
  tenantId: string
  role: string
  currentMenu: string
  isCollapse: boolean
  chatInfo: ChatInfo
}

export const useAuthStore = defineStore('authStore', {
  state: (): AuthState => ({
    userInfo: {},
    token: '',
    tenantId: '',
    role: '',
    currentMenu: '',
    isCollapse: false,
    chatInfo: {
      chatId: '',
      isTalking: false,
      appInfo: null
    }
  }),
  actions: {
    setUserInfo(this: AuthState, userInfo: Record<string, any>): void {
      this.userInfo = userInfo
    },
    setToken(this: AuthState, data: string): void {
      this.token = data
    },
    setTenantId(this: AuthState, tenantId: string): void {
      this.tenantId = tenantId
    },
    setRole(this: AuthState, role: string): void {
      this.role = role
    },
    setCurrentMenu(this: AuthState, menu: string): void {
      this.currentMenu = menu
    },
    setCollapse(this: AuthState, isCollapse: boolean): void {
      this.isCollapse = isCollapse
    },
    setChatInfo(this: AuthState, chatInfo: any): void {
      this.chatInfo = chatInfo
    }
  },
  persist: true
})