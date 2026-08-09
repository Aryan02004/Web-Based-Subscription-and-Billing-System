import {
  apiClient,
  clearAuthSession,
  getRefreshToken,
  storeAuthSession,
} from './client'
import { apiRoutes } from './endpoints'

const createCrudApi = (basePath) => ({
  create: (payload) => apiClient.post(basePath, payload),
  list: (params) => apiClient.get(basePath, { params }),
  getById: (id) => apiClient.get(`${basePath}/${id}`),
  update: (id, payload) => apiClient.put(`${basePath}/${id}`, payload),
  remove: (id) => apiClient.delete(`${basePath}/${id}`),
})

const authApi = {
  register: async (payload) => apiClient.post(apiRoutes.auth.register, payload),
  login: async (payload) => {
    const response = await apiClient.post(apiRoutes.auth.login, payload)
    storeAuthSession(response)
    return response
  },
  refreshToken: async (payload) => {
    const response = await apiClient.post(apiRoutes.auth.refreshToken, payload)
    storeAuthSession(response)
    return response
  },
  verifyEmail: (payload) => apiClient.post(apiRoutes.auth.verifyEmail, payload),
  logout: async (refreshToken = getRefreshToken()) => {
    if (!refreshToken) {
      clearAuthSession()
      return { message: 'Session cleared locally.' }
    }

    const response = await apiClient.post(apiRoutes.auth.logout, null, {
      headers: {
        'Refresh-Token': refreshToken,
      },
    })

    clearAuthSession()
    return response
  },
}

const userApi = {
  getCurrentUser: () => apiClient.get(apiRoutes.users.me),
  updateCurrentUser: (payload) => apiClient.put(apiRoutes.users.me, payload),
  changePassword: (payload) => apiClient.put(apiRoutes.users.changePassword, payload),
  deleteCurrentUser: () => apiClient.delete(apiRoutes.users.me),
}

const organizationApi = {
  ...createCrudApi(apiRoutes.organizations.base),
  getMyOrganizations: () => apiClient.get(apiRoutes.organizations.mine),
  generateLink: (payload) => apiClient.post(apiRoutes.organizations.generateLink, payload),
}

const superAdminOrganizationApi = {
  getPendingOrganizations: () => apiClient.get(apiRoutes.superAdminOrganizations.pending),
  approve: (organizationId) => apiClient.put(apiRoutes.superAdminOrganizations.approve(organizationId)),
  reject: (organizationId, reason) =>
    apiClient.put(apiRoutes.superAdminOrganizations.reject(organizationId), {
      organizationId,
      reason,
    }),
  suspend: (organizationId, reason) =>
    apiClient.put(apiRoutes.superAdminOrganizations.suspend(organizationId), {
      organizationId,
      reason,
    }),
}

const otpApi = {
  generate: (payload) => apiClient.post(apiRoutes.otp.generate, payload),
  verify: (payload) => apiClient.post(apiRoutes.otp.verify, payload),
  resend: (payload) => apiClient.post(apiRoutes.otp.resend, payload),
}

const roleApi = createCrudApi(apiRoutes.roles.base)
const planApi = createCrudApi(apiRoutes.plans.base)
const subscriptionApi = createCrudApi(apiRoutes.subscriptions.base)
const invoiceApi = createCrudApi(apiRoutes.invoices.base)
const customerApi = createCrudApi(apiRoutes.customers.base)
const paymentApi = {
  ...createCrudApi(apiRoutes.payments.base),
  createOrder: (paymentId) => apiClient.post(apiRoutes.payments.createOrder(paymentId)),
}

const publicApi = {
  getOrganizationPlans: (token) => apiClient.get(apiRoutes.public.organizationPlans(token)),
  billingPage: (token) => apiClient.get(`/billing/${encodeURIComponent(token)}`),
  checkout: (token, payload) => apiClient.post(`/billing/${encodeURIComponent(token)}/subscribe`, payload),
  verifyPayment: (token, paymentId, payload) => apiClient.post(`/billing/${encodeURIComponent(token)}/verify/${paymentId}`, payload),
}

const aiApi = {
  dashboardSummary: () => apiClient.get('/api/ai/dashboard-summary'),
}

const testApi = {
  hello: () => apiClient.get(apiRoutes.test.hello),
}

export const api = {
  auth: authApi,
  user: userApi,
  organization: organizationApi,
  superAdminOrganization: superAdminOrganizationApi,
  otp: otpApi,
  role: roleApi,
  plan: planApi,
  subscription: subscriptionApi,
  invoice: invoiceApi,
  customer: customerApi,
  payment: paymentApi,
  public: publicApi,
  test: testApi,
  ai: aiApi,
}