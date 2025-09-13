<template>
  <div class="bg-gray-900 text-gray-100 min-h-screen p-4">
    <div class="bg-gray-800 shadow-md rounded-lg p-4 md:p-6 flex flex-col h-[calc(100vh-64px)]">
      <h2 class="text-xl md:text-2xl font-semibold mb-4 md:mb-6">Reports Dashboard</h2>

      <div class="flex flex-col md:flex-row flex-1 gap-4 overflow-auto">
        <!-- Left Column: 2 rows -->
        <div class="flex flex-col md:flex-1 gap-4">
          <!-- Trainer Availability vs Occupancy -->
          <div class="bg-gray-700 border border-gray-600 rounded-lg shadow p-4 flex flex-col flex-1 ">
            <h3 class="font-medium mb-2 md:mb-4 text-gray-100">
              Trainer Availability vs Occupancy
            </h3>
            <div class="flex-1 min-h-[14rem]">
              <canvas ref="availChart" class="w-full h-full"></canvas>
            </div>
          </div>

          <!-- Batch Enrollments -->
          <div class="bg-gray-700 border border-gray-600 rounded-lg shadow p-4 flex flex-col flex-1 ">
            <h3 class="text-lg font-medium mb-2 md:mb-4 text-gray-100">
              Batch Enrollments
            </h3>
            <div class="flex-1 min-h-[12rem]">
              <canvas ref="enrollChart" class="w-full h-full"></canvas>
            </div>
          </div>
        </div>

        <!-- Right Column: Attendance Trends -->
        <div class="bg-gray-700 border border-gray-600 rounded-lg shadow p-4 flex-1 flex flex-col">
          <h3 class="text-lg font-medium mb-2 md:mb-4 text-gray-100">
            Attendance Trends
          </h3>
          <div class="flex-1 w-full h-full overflow-auto">
            <canvas ref="attChart" class="w-full h-full"></canvas>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>



<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { Chart, registerables } from 'chart.js'
Chart.register(...registerables)
import api from '../api/axios.ts'

type Availability = { trainerName: string; availableDays: number; occupiedDays: number }
type Enrollment = { label: string; traineeCount: number }
type Trend = { date: string; presentCount: number }

const availability = ref<Availability[]>([
{ trainerName: 'Alice', availableDays: 18, occupiedDays: 12 },
{ trainerName: 'Bob', availableDays: 15, occupiedDays: 15 },
{ trainerName: 'Charlie', availableDays: 20, occupiedDays: 10 }
])
const enrollments = ref<Enrollment[]>([
{ label: 'Batch A', traineeCount: 25 },
{ label: 'Batch B', traineeCount: 30 },
{ label: 'Batch C', traineeCount: 20 },
{ label: 'Batch D', traineeCount: 28 },
{ label: 'Batch E', traineeCount: 22 },
{ label: 'Batch F', traineeCount: 26 }
])
const trends = ref<Trend[]>([
{ date: '2024-10-01', presentCount: 22 },
{ date: '2024-10-02', presentCount: 25 },
{ date: '2024-10-03', presentCount: 20 },
{ date: '2024-10-04', presentCount: 27 },
{ date: '2024-10-05', presentCount: 30 },
{ date: '2024-10-06', presentCount: 24 },
{ date: '2024-10-07', presentCount: 28 },
{ date: '2024-10-08', presentCount: 26 },
{ date: '2024-10-09', presentCount: 29 },
{ date: '2024-10-10', presentCount: 23 },
{ date: '2024-10-11', presentCount: 31 },
{ date: '2024-10-12', presentCount: 27 },
{ date: '2024-10-13', presentCount: 25 },
{ date: '2024-10-14', presentCount: 30 },
{ date: '2024-10-15', presentCount: 28 }
])

const availChart = ref<HTMLCanvasElement | null>(null)
const enrollChart = ref<HTMLCanvasElement | null>(null)
const attChart = ref<HTMLCanvasElement | null>(null)

let availChartInstance: Chart | null = null
let enrollChartInstance: Chart | null = null
let attChartInstance: Chart | null = null

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { labels: { color: '#f3f4f6' } }
  },
  scales: {
    x: { ticks: { color: '#f3f4f6' }, grid: { color: '#374151' } },
    y: { ticks: { color: '#f3f4f6' }, grid: { color: '#374151' } }
  }
}

const load = async () => {
  try {
    const [a, e, t] = await Promise.all([
      api.get('/reports/trainer-availability-occupancy'),
      api.get('/reports/batch-enrollments'),
      api.get('/reports/attendance-trends'),
    ])
    availability.value = a.data.data ?? a.data
    enrollments.value = e.data.data ?? e.data
    trends.value = t.data.data ?? t.data
  } catch (err) {
    availability.value = []
    enrollments.value = []
    trends.value = []
    console.warn('Reports endpoints missing', err)
  }
}

const renderAvailability = (data: Availability[]) => {
  if (!availChart.value) return
  availChartInstance?.destroy()
  availChartInstance = new Chart(availChart.value.getContext('2d')!, {
    type: 'bar',
    data: {
      labels: data.map(d => d.trainerName),
      datasets: [
        { label: 'Available Days', data: data.map(d => d.availableDays), backgroundColor: '#38bdf8' },
        { label: 'Occupied Days', data: data.map(d => d.occupiedDays), backgroundColor: '#facc15' }
      ]
    },
    options: chartOptions
  })
}

const renderEnrollments = (data: Enrollment[]) => {
  if (!enrollChart.value) return
  enrollChartInstance?.destroy()
  enrollChartInstance = new Chart(enrollChart.value.getContext('2d')!, {
    type: 'bar',
    data: {
      labels: data.map(d => d.label),
      datasets: [{ label: 'Trainees', data: data.map(d => d.traineeCount), backgroundColor: '#a78bfa' }]
    },
    options: chartOptions
  })
}

const renderTrends = (data: Trend[]) => {
  if (!attChart.value) return
  attChartInstance?.destroy()
  attChartInstance = new Chart(attChart.value.getContext('2d')!, {
    type: 'line',
    data: {
      labels: data.map(d => d.date),
      datasets: [{ label: 'Present Count', data: data.map(d => d.presentCount), borderColor: '#34d399', backgroundColor: 'transparent', tension: 0.2 }]
    },
    options: chartOptions
  })
}

onMounted(async () => {
  await load()
  renderAvailability(availability.value)
  renderEnrollments(enrollments.value)
  renderTrends(trends.value)
})

onBeforeUnmount(() => {
  availChartInstance?.destroy()
  enrollChartInstance?.destroy()
  attChartInstance?.destroy()
})
</script>
