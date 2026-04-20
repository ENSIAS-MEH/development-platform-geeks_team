import api from '../lib/api'

export const userService = {

  updatePrivacy: async (data: { showEmail: boolean }) => {
    await api.put('/api/users/me/privacy', data)
},

  // Called by DashboardPage and MyProfilePage on load
  getMyProfile: async () => {
    const response = await api.get('/api/users/me')
    return response.data
  },


  // Called when user saves profile edit form
  updateMyProfile: async (data: {
    name?: string
    email?: string
    bio?: string
    location?: string
    avatarUrl?: string
    headline?: string
    githubUrl?: string
    linkedinUrl?: string
    portfolioUrl?: string
    websiteUrl?: string
  }) => {
    const response = await api.put('/api/users/me', data)
    return response.data
  },

  // Called when viewing another user's public profile
  getUserById: async (id: string) => {
    const response = await api.get(`/api/users/${id}`)
    return response.data
  },

  // Called by FindCollaboratorsPage
  searchUsers: async (skill: string, level?: string, page = 0, size = 10) => {
    const params: any = { skill, page, size }
    if (level) params.level = level
    const response = await api.get('/api/users/search', { params })
    return response.data  // Page<UserResponse> — has .content, .totalPages, .totalElements
  },

  // Called when user adds a skill on profile page
  // Returns updated List<String> of all skill names
  addSkill: async (name: string) => {
    const response = await api.post('/api/users/me/skills', { name })
    return response.data  // string[]
  },

  // Called when user deletes a skill
  // Pass skillId if frontend has it, or use deleteSkillByName below
  deleteSkill: async (skillId: string) => {
    const response = await api.delete(`/api/users/me/skills/${skillId}`)
    return response.data  // updated string[]
  },

  // Alternative if frontend only has the skill name
  deleteSkillByName: async (name: string) => {
    const response = await api.delete('/api/users/me/skills', { params: { name } })
    return response.data
  },

  // Called by SettingsPage
  changePassword: async (data: {
    currentPassword: string
    newPassword: string
    confirmPassword: string
  }) => {
    const response = await api.put('/api/users/me/password', data)
    return response.data
  },
}