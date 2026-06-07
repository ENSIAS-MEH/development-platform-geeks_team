/**
 * Event Service API client (port 8082, proxied via /api/events in vite.config.ts)
 */
import type { SpringPage } from '../types'

const API_BASE = '/api/events'

export type EventTypeApi =
  | 'HACKATHON'
  | 'CONFERENCE'
  | 'WORKSHOP'
  | 'COMPETITION'
  | 'MEETUP'

export type EventStatusApi = 'DRAFT' | 'PUBLISHED' | 'CANCELLED' | 'ENDED'

export interface EventResponseDto {
  id: string
  title: string
  description?: string
  type: EventTypeApi
  startDate: string
  endDate: string
  location?: string
  maxParticipants?: number
  organizerId: string
  status: EventStatusApi
  tags?: string[]
  participantCount: number
  userRegistered: boolean
  createdAt?: string
  updatedAt?: string
}

export interface CreateEventPayload {
  title: string
  description?: string
  type: EventTypeApi
  startDate: string
  endDate: string
  location?: string
  maxParticipants?: number
  tags?: string[]
}

export type UpdateEventPayload = Partial<CreateEventPayload>

export class EventApiError extends Error {
  constructor(
    public status: number,
    message: string,
    public body?: unknown
  ) {
    super(message)
    this.name = 'EventApiError'
  }
}

function getOptionalUserId(): string | undefined {
  try {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      const user = JSON.parse(userStr)
      if (user?.id) return user.id
    }
  } catch {
    /* ignore */
  }
  return undefined
}

async function request<T>(
  url: string,
  options: RequestInit = {},
  requireAuth = false
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  }

  const token = localStorage.getItem('accessToken')
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const userId = getOptionalUserId()
  if (userId) {
    headers['X-User-Id'] = userId
  } else if (requireAuth) {
    throw new EventApiError(401, 'You must be logged in')
  }

  const response = await fetch(`${API_BASE}${url}`, { ...options, headers })

  if (!response.ok) {
    const body = await response.json().catch(() => ({}))
    const message =
      (body as { message?: string }).message ||
      response.statusText ||
      'Request failed'
    throw new EventApiError(response.status, message, body)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json()
}

export function formatEventType(type: EventTypeApi): string {
  const map: Record<EventTypeApi, string> = {
    HACKATHON: 'Hackathon',
    CONFERENCE: 'Conference',
    WORKSHOP: 'Workshop',
    COMPETITION: 'Competition',
    MEETUP: 'Meetup',
  }
  return map[type] ?? type
}

export function uiTypeToApi(type: string): EventTypeApi {
  const map: Record<string, EventTypeApi> = {
    Hackathon: 'HACKATHON',
    Workshop: 'WORKSHOP',
    Conference: 'CONFERENCE',
    Meetup: 'MEETUP',
    Competition: 'COMPETITION',
  }
  return map[type] ?? 'HACKATHON'
}

export function formatEventDateRange(start: string, end: string): string {
  const startDate = new Date(start)
  const endDate = new Date(end)
  const opts: Intl.DateTimeFormatOptions = {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  }
  const startStr = startDate.toLocaleDateString('en-US', opts)
  const endStr = endDate.toLocaleDateString('en-US', opts)
  if (startStr === endStr) return startStr
  return `${startStr} – ${endStr}`
}

/** Date input (YYYY-MM-DD) → ISO local datetime at start of day */
export function dateInputToIso(date: string, endOfDay = false): string {
  return endOfDay ? `${date}T23:59:59` : `${date}T09:00:00`
}

/** ISO datetime → YYYY-MM-DD for &lt;input type="date" /&gt; */
export function isoToDateInput(iso: string): string {
  return iso.split('T')[0]
}

export async function searchEvents(params?: {
  type?: EventTypeApi
  status?: EventStatusApi
  organizerId?: string
  keyword?: string
  page?: number
  size?: number
}): Promise<SpringPage<EventResponseDto>> {
  const q = new URLSearchParams()
  if (params?.type) q.set('type', params.type)
  if (params?.status) q.set('status', params.status)
  if (params?.organizerId) q.set('organizerId', params.organizerId)
  if (params?.keyword) q.set('keyword', params.keyword)
  q.set('page', String(params?.page ?? 0))
  q.set('size', String(params?.size ?? 50))
  const query = q.toString()
  return request<SpringPage<EventResponseDto>>(`?${query}`)
}

export async function getEvent(id: string): Promise<EventResponseDto> {
  return request<EventResponseDto>(`/${id}`)
}

export async function createEvent(payload: CreateEventPayload): Promise<EventResponseDto> {
  return request<EventResponseDto>(
    '',
    { method: 'POST', body: JSON.stringify(payload) },
    true
  )
}

export async function updateEvent(
  id: string,
  payload: UpdateEventPayload
): Promise<EventResponseDto> {
  return request<EventResponseDto>(
    `/${id}`,
    { method: 'PUT', body: JSON.stringify(payload) },
    true
  )
}

export async function deleteEvent(id: string): Promise<void> {
  await request(`/${id}`, { method: 'DELETE' }, true)
}

export async function publishEvent(id: string): Promise<EventResponseDto> {
  return request<EventResponseDto>(`/${id}/publish`, { method: 'POST' }, true)
}

export async function registerForEvent(id: string): Promise<void> {
  await request(`/${id}/register`, { method: 'POST' }, true)
}

export async function cancelRegistration(id: string): Promise<void> {
  await request(`/${id}/register`, { method: 'DELETE' }, true)
}

export function eventToCardProps(event: EventResponseDto) {
  return {
    id: event.id,
    title: event.title,
    organizer: 'Organizer',
    date: formatEventDateRange(event.startDate, event.endDate),
    location: event.location || 'TBD',
    participants: event.participantCount,
    tags: event.tags ? [...event.tags] : [],
    type: formatEventType(event.type),
  }
}
