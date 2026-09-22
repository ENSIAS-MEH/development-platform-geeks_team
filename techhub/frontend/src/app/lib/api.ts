import axios from 'axios'

const api = axios.create({
  // Let Vite proxy handle requests using relative paths
  baseURL: '',
  headers: {
    'Content-Type': 'application/json',
  },
})

function getCurrentUserId(): string | null {
  try {
    const userStr = localStorage.getItem('user')
    if (!userStr) return null
    const user = JSON.parse(userStr)
    return user?.id ?? null
  } catch {
    return null
  }
}

// Attach JWT and user id for microservices that read X-User-Id
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  const userId = getCurrentUserId()
  if (userId) {
    config.headers['X-User-Id'] = userId
  }
  return config
})

// Handle 401 globally — clear storage and redirect to login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('user')
      window.location.href = '/auth/login'
    }
    return Promise.reject(error)
  }
)

export default api