import api from '../lib/api'

export interface RegisterData {
  name: string
  email: string
  password: string
  role: string
}

export interface LoginData {
  email: string
  password: string
}

export const authService = {

  register: async (data: RegisterData) => {
    const response = await api.post('/api/auth/register', data)
    // Save token and user to localStorage
    localStorage.setItem('accessToken', response.data.accessToken)
    localStorage.setItem('user', JSON.stringify(response.data.user))
    return response.data
  },

  login: async (data: LoginData) => {
    const response = await api.post('/api/auth/login', data)
    localStorage.setItem('accessToken', response.data.accessToken)
    localStorage.setItem('user', JSON.stringify(response.data.user))
    return response.data
  },

  logout: () => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('user')
    window.location.href = '/auth/login'
  },

  getCurrentUser: () => {
    const user = localStorage.getItem('user')
    return user ? JSON.parse(user) : null
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('accessToken')
  },
}