import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    return Promise.resolve(
      err?.response
        ? err.response
        : { status: 500, data: { success: false, message: 'Network or server error' } },
    )
  },
)

export default api
