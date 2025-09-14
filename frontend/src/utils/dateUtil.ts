export const formatDateTime = (date: Date, format: string = 'yyyy-MM-dd HH:mm:ss'): string => {
  if (!date) return ''

  const yyyy = date.getFullYear()
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  const HH = String(date.getHours()).padStart(2, '0')
  const MM = String(date.getMinutes()).padStart(2, '0')
  const SS = String(date.getSeconds()).padStart(2, '0')

  return format
    .replace('yyyy', String(yyyy))
    .replace('MM', mm)
    .replace('dd', dd)
    .replace('HH', HH)
    .replace('mm', MM)
    .replace('ss', SS)
}
export function isNotEmpty(value: string): boolean {
  return value.trim().length > 0
}
export function isValidEmail(value: string): boolean {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return isNotEmpty(value) && regex.test(value)
}