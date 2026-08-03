export const apiRoutes = {
  auth: {
    base: '/api/auth',
    register: '/api/auth/register',
    login: '/api/auth/login',
    refreshToken: '/api/auth/refresh-token',
    verifyEmail: '/api/auth/verify-email',
    logout: '/api/auth/logout',
  },
  users: {
    base: '/api/users',
    me: '/api/users/me',
    changePassword: '/api/users/me/change-password',
  },
  roles: {
    base: '/api/roles',
  },
  organizations: {
    base: '/api/organizations',
    mine: '/api/organizations/my-organizations',
    generateLink: '/api/organizations/generate-link',
  },
  superAdminOrganizations: {
    base: '/api/super-admin/organizations',
    pending: '/api/super-admin/organizations/pending',
    approve: (organizationId) => `/api/super-admin/organizations/${organizationId}/approve`,
    reject: (organizationId) => `/api/super-admin/organizations/${organizationId}/reject`,
    suspend: (organizationId) => `/api/super-admin/organizations/${organizationId}/suspend`,
  },
  otp: {
    base: '/api/otp',
    generate: '/api/otp/generate',
    verify: '/api/otp/verify',
    resend: '/api/otp/resend',
  },
  plans: {
    base: '/api/plans',
  },
  subscriptions: {
    base: '/api/subscriptions',
  },
  invoices: {
    base: '/api/invoices',
  },
  payments: {
    base: '/api/payments',
    createOrder: (paymentId) => `/api/payments/${paymentId}/create-order`,
  },
  public: {
    organizationPlans: (token) => `/public/org/${encodeURIComponent(token)}`,
  },
  test: {
    hello: '/api/test/hello',
  },
}