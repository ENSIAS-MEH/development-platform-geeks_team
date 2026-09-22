import { useEffect } from 'react'
import { useNavigate } from 'react-router'
import { userService } from '../services/userService'

export default function AuthCallback() {
  const navigate = useNavigate()

  useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const token = params.get('token')
    const error = params.get('error')

    if (error) {
      navigate('/auth/login?error=' + error)
      return
    }

    if (token) {
      localStorage.setItem('accessToken', token)

      // Fetch and store user profile immediately after OAuth login
      // This populates localStorage so DashboardPage and MyProfilePage
      // can read user.name, user.skills etc. without an extra API call
      userService.getMyProfile()
        .then(user => {
          localStorage.setItem('user', JSON.stringify(user))
          navigate('/dashboard')
        })
        .catch(() => {
          // Even if profile fetch fails, still navigate — pages will fetch it themselves
          navigate('/dashboard')
        })
    } else {
      navigate('/auth/login?error=no_token')
    }
  }, [])

  return (
    <div className="flex items-center justify-center h-screen">
      <p className="text-[#717182]">Completing sign in...</p>
    </div>
  )
}