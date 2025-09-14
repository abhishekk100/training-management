<template>
  <div class="p-4 space-y-6 bg-gray-900 min-h-screen text-gray-100">
    <!-- Mark Attendance Form -->
    <div class="bg-gray-800 shadow rounded-lg p-4">
      <h2 class="text-xl font-semibold mb-4">Mark Attendance</h2>

      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-5 gap-4">
        <!-- Batch -->
        <div class="flex flex-col">
          <label class="block text-sm font-medium text-gray-200 mb-1">
            <span class="text-red-500">*</span> Batch
          </label>
          <Select
            v-model="form.batchId"
            :options="batches"
            optionLabel="name"
            optionValue="id"
            placeholder="Select batch"
            @change="onBatchChange"
            class="w-full"
          />
        </div>

        <!-- Trainee -->
        <div class="flex flex-col">
          <label class="block text-sm font-medium text-gray-200 mb-1">
            <span class="text-red-500">*</span> Trainee
          </label>
          <Select
            v-model="form.traineeId"
            :options="traineesInBatch"
            optionLabel="name"
            optionValue="id"
            placeholder="Select trainee"
            class="w-full"
          />
        </div>

        <!-- Date -->
        <div class="flex flex-col">
          <label class="block text-sm font-medium text-gray-200 mb-1">
            <span class="text-red-500">*</span> Date
          </label>
          <DatePicker
            v-model="form.attendanceDate"
            dateFormat="yy-mm-dd"
            class="w-full"
            placeholder="Select date"
            :minDate="new Date()"
            readonly
          />
        </div>

        <!-- Status -->
        <div class="flex flex-col">
          <label class="block text-sm font-medium text-gray-200 mb-1">
            <span class="text-red-500">*</span> Status
          </label>
          <Select
            v-model="form.status"
            :options="statusOptions"
            optionLabel="label"
            optionValue="value"
            placeholder="Select status"
            class="w-full"
          />
        </div>

        <!-- Mark Button -->
        <div class="flex items-end justify-end">
          <Button
            label="Mark Attendance"
            icon="pi pi-check"
            class="p-button-success w-full md:w-auto"
            @click="mark"
          />
        </div>
      </div>
    </div>

    <!-- Attendance Records -->
    <div class="bg-gray-800 shadow rounded-lg p-4">
      <h3 class="text-lg font-semibold mb-4">Attendance Records</h3>

      <DataTable
        v-if="records && records.length"
        :value="records"
        class="p-datatable-sm text-gray-100"
        scrollable
        scrollHeight="400px"
        responsiveLayout="scroll"
      >
        <Column field="id" header="ID" />
        <Column field="traineeName" header="Trainee" />
        <Column field="batchName" header="Batch" />
        <Column field="attendanceDate" header="Date" />
        <Column field="status" header="Status" />
        <Column header="Action" style="width: 100px">
          <template #body="slotProps">
            <Button
              icon="pi pi-trash"
              class="p-button-sm p-button-danger"
              @click="deleteAttendance(slotProps.data.id)"
            />
          </template>
        </Column>
      </DataTable>

      <div v-if="!records.length" class="text-gray-400 text-center mt-2">No records available</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '../api/axios.ts'
import Button from 'primevue/button'
import Select from 'primevue/select'

import DatePicker from 'primevue/datepicker'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import { formatDateTime } from '@/utils/dateUtil.ts'

interface Attendance {
  id: number | null
  attendanceDate: string
  status: string
  batchId: number | null
  batchName: string
  traineeId: number | null
  traineeName: string
}

interface Batch {
  id: number | null
  name: string | ''
  location: string | ''
  courseId: number | null
  courseName: string | ''
  trainerId: number | null
  trainerName: string | ''
  scheduledDate: string | ''
  timeSlot: string | ''
}

interface Trainee {
  id: number | null
  name: string | ''
  email: string | ''
  batchId: number | null
  batchName: string | ''
  courseId: number | null
  courseName: string | ''
}
const batches = ref<Batch[]>([])
const traineesInBatch = ref<Trainee[]>([])
const records = ref<Attendance[]>([])
const statusOptions = [
  { label: 'PRESENT', value: 'PRESENT' },
  { label: 'ABSENT', value: 'ABSENT' },
]
const form = ref({
  attendanceDate: new Date(),
  batchId: null as number | null,
  traineeId: null as number | null,
  status: 'PRESENT',
})

const loadBatches = async () => {
  const res = await api.get('/batches/list')
  const { data, success, message } = res.data

  if (success) {
    batches.value = data
  } else {
    batches.value = []
    console.warn('Failed to load batches: ' + message)
  }
}

const onBatchChange = async () => {
  if (!form.value.batchId) {
    traineesInBatch.value = []
    return
  }
  const res = await api.get('/trainees/list')
  traineesInBatch.value = (res.data?.data || []).filter(
    (t: Trainee) => t.batchId === form.value.batchId,
  )
}

const loadRecords = async () => {
  const res = await api.get('/attendance/list')
  records.value = res.data?.data || []
}
const normalize = (d: string | Date) => {
  return new Date(d).toISOString().split('T')[0]
} // "2025-09-13"
const mark = async () => {
  if (!form.value.traineeId || !form.value.batchId || !form.value.attendanceDate) {
    alert('Please fill all required fields')
    return
  }
  const trainee = records.value.find(
    (r) =>
      r.traineeId === form.value.traineeId &&
      r.batchId === form.value.batchId &&
      normalize(r.attendanceDate) === normalize(form.value.attendanceDate),
  )
  if (trainee) {
    alert('Trainee already marked for this date')
    return
  }
  const payload = {
    traineeId: form.value.traineeId,
    batchId: form.value.batchId,
    attendanceDate: formatDateTime(form.value.attendanceDate || new Date(), 'yyyy-MM-dd'),
    status: form.value.status?.trim(),
  }
  const res = await api.post('/attendance/create', payload)
  if (res.data?.success) {
    alert('Attendance marked successfully')
    form.value.traineeId = null
    form.value.batchId = null
    form.value.attendanceDate = new Date()
    form.value.status = 'PRESENT'
    await loadRecords()
  } else {
    alert('Failed to mark attendance: ' + res.data?.message)
  }
}

const deleteAttendance = async (id: number) => {
  if (!confirm('Are you sure you want to delete this attendance?')) return
  const res = await api.delete(`/attendance/delete/${id}`)
  const { success, message } = res.data
  if (success) {
    alert(message || 'Attendance deleted')
    await load()
  } else {
    alert('Failed: ' + message)
  }
}
const load = async () => {
  await loadBatches()
  await loadRecords()
}
onMounted(async () => {
  await load()
})
</script>

<style scoped></style>
