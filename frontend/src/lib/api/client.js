import axios from 'axios'

const normalizeBaseUrl = (value) => value.replace(/\/+$/, '')

export const API_BASE_URL = normalizeBaseUrl(
  import.meta.env.VITE_API_BASE_URL?.trim() || 'http://localhost:8080',
)

export const AUTH_STORAGE_KEYS = {
  accessToken: 'subscriptor.accessToken',
  refreshToken: 'subscriptor.refreshToken',
  user: 'subscriptor.user',
}

export const getAccessToken = () => localStorage.getItem(AUTH_STORAGE_KEYS.accessToken)

export const getRefreshToken = () => localStorage.getItem(AUTH_STORAGE_KEYS.refreshToken)

export const getStoredUser = () => {
  const raw = localStorage.getItem(AUTH_STORAGE_KEYS.user)
  if (!raw) return null

  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export const isAuthenticated = () => Boolean(getAccessToken())

const setAuthHeader = () => {
  const token = getAccessToken()

  if (token) {
    apiClient.defaults.headers.common.Authorization = `Bearer ${token}`
  } else {
    delete apiClient.defaults.headers.common.Authorization
  }
}

export const storeAuthSession = ({
  accessToken,
  refreshToken,
  userId,
  firstName,
  lastName,
  email,
  role,
} = {}) => {
  if (accessToken) {
    localStorage.setItem(AUTH_STORAGE_KEYS.accessToken, accessToken)
  }

  if (refreshToken) {
    localStorage.setItem(AUTH_STORAGE_KEYS.refreshToken, refreshToken)
  }

  if (userId || firstName || lastName || email || role) {
    localStorage.setItem(
      AUTH_STORAGE_KEYS.user,
      JSON.stringify({
        userId,
        firstName,
        lastName,
        email,
        role,
      }),
    )
  }

  setAuthHeader()
}

export const clearAuthSession = () => {
  localStorage.removeItem(AUTH_STORAGE_KEYS.accessToken)
  localStorage.removeItem(AUTH_STORAGE_KEYS.refreshToken)
  localStorage.removeItem(AUTH_STORAGE_KEYS.user)
  setAuthHeader()
}

const isObject = (value) => value !== null && typeof value === 'object'

const extractErrorMessage = (value, fallback = 'Request failed') => {
  if (!value) {
    return fallback
  }

  if (typeof value === 'string') {
    return value.trim() || fallback
  }

  if (Array.isArray(value)) {
    for (const item of value) {
      const nested = extractErrorMessage(item, '')
      if (nested) {
        return nested
      }
    }

    return fallback
  }

  if (isObject(value)) {
    const directMessage = value.message || value.error || value.detail || value.title
    if (typeof directMessage === 'string' && directMessage.trim()) {
      return directMessage.trim()
    }

    for (const nestedValue of Object.values(value)) {
      const nested = extractErrorMessage(nestedValue, '')
      if (nested) {
        return nested
      }
    }
  }

  return fallback
}

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

setAuthHeader()

apiClient.interceptors.request.use((config) => {
  const token = getAccessToken()

  if (token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const responseData = error.response?.data
    const message = extractErrorMessage(responseData, error.message || 'Request failed')

    return Promise.reject({
      message,
      status: error.response?.status,
      data: responseData,
      originalError: error,
    })
  },
)