<template>
  <div class="p-4 min-h-screen bg-gray-900 text-gray-100">
    <div class="bg-gray-800 shadow-md rounded-lg p-4">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-4 gap-2">
        <h2 class="text-xl font-semibold">Batches</h2>
        <Button
          label="Add"
          icon="pi pi-plus"
          class="p-button-sm p-button-info"
          @click="openModal()"
        />
      </div>

      <!-- Table -->
      <DataTable
        v-if="batches && batches.length"
        :value="batches"
        paginator
        :rows="5"
        :rowsPerPageOptions="[5, 10, 20, 50]"
        scrollable
        scrollHeight="60vh"
        responsiveLayout="scroll"
        class="text-sm p-datatable-sm p-datatable-striped"
      >
        <Column field="name" header="Batch Name"></Column>
        <Column field="courseName" header="Course Name"></Column>
        <Column field="trainerName" header="Trainer Name"></Column>
        <Column field="location" header="Location"></Column>

        <Column header="Actions" style="width: 8rem">
          <template #body="slotProps">
            <div class="flex gap-2">
              <Button
                icon="pi pi-pencil"
                class="p-button-sm p-button-info"
                @click="openModal(slotProps.data)"
              />
              <Button
                icon="pi pi-trash"
                class="p-button-sm p-button-danger"
                @click="deleteBatch(slotProps.data.id)"
              />
            </div>
          </template>
        </Column>
      </DataTable>

      <div v-else class="text-gray-500 text-sm mt-2">No records available</div>
    </div>

    <!-- PrimeVue Dialog for Modal -->
    <Dialog
      v-model:visible="showModal"
      :header="isEdit ? 'Edit Batch' : 'Add Batch'"
      modal
      dismissableMask
      class="w-full max-w-lg"
    >
      <div class="flex flex-col gap-4">
        <div>
          <label class="block text-sm font-medium mb-1">Batch Name</label>
          <InputText v-model="form.name" placeholder="Enter a name" class="w-full" />
        </div>
        <div>
          <label class="block text-sm font-medium mb-1">Course</label>
          <Select
            v-model="form.courseId"
            :options="courses"
            optionLabel="name"
            optionValue="id"
            placeholder="Select course"
            class="w-full"
            editable
            showClear
          >
            <template #option="slotProps">
              <div class="flex flex-col">
                <span>{{ slotProps.option.name }}</span>
                <span class="text-xs text-gray-500">
                  {{ slotProps.option.description }}
                </span>
              </div>
            </template>
          </Select>
        </div>

        <div>
          <label class="block text-sm font-medium mb-1">Trainer</label>
          <Select
            v-model="form.trainerId"
            :options="trainers"
            optionLabel="name"
            optionValue="id"
            placeholder="Select trainer"
            class="w-full"
            editable
            showClear
            @change="(e) => onSelectTrainer(e.value)"
          >
            <template #option="slotProps">
              <div class="flex flex-col">
                <span>{{ slotProps.option.name }}</span>
                <span class="text-xs text-gray-500">
                  {{ slotProps.option.location }}
                </span>
              </div>
            </template>
          </Select>
        </div>

        <div>
          <label class="block text-sm font-medium mb-1">Location</label>
          <InputText v-model="form.location" readonly placeholder="Location" class="w-full" />
        </div>
        <div v-if="isTrainerSelected">
          <label class="block text-sm font-medium mb-1">Trainer Availability</label>
          <Select
            v-model="form.availabilityId"
            :options="trainerAvailabilities"
            optionLabel="label"
            optionValue="id"
            placeholder="Select trainer"
            class="w-full"
            editable
            showClear
            @change="e => onSelectAavailability(e.value)"
          >
            <template #option="slotProps">
              <div class="flex flex-col">
                <span>{{ slotProps.option.availableDate }} ({{ slotProps.option.timeSlot }})</span>
              </div>
            </template>
          </Select>
        </div>

        <div class="block text-sm font-medium mb-1">
          <p>Batch Schedule Details</p>
        </div>
        <div
          class="flex flex-col sm:flex-row sm:items-end gap-4 mb-6 border border-gray-700 p-4 rounded"
        >
          <div class="flex-1">
            <label for="available_date" class="block text-sm font-medium mb-1">Date</label>
            <DatePicker
              v-model="avail.availableDate"
              dateFormat="yy-mm-dd"
              size="small"
              class="w-full"
              showIcon
              inputId="available_date"
              iconDisplay="input"
              :minDate="new Date()"
              readonly
            />
          </div>

          <div class="flex-1">
            <label for="from_time_slot_label" class="block text-sm font-medium mb-1"
              >From Time</label
            >
            <DatePicker
              v-model="avail.fromTime"
              inputId="from_time_slot_label"
              timeOnly
              showIcon
              size="small"
              class="w-full"
              iconDisplay="input"
              readonly
            />
          </div>

          <div class="flex-1">
            <label for="to_time_slot_label" class="block text-sm font-medium mb-1">To Time</label>
            <DatePicker
              size="small"
              v-model="avail.toTime"
              inputId="to_time_slot_label"
              timeOnly
              showIcon
              iconDisplay="input"
              class="w-full"
              readonly
            />
          </div>
        </div>
      </div>
      <div v-if="errorMessage" class="text-red-400 text-sm mb-2">{{ errorMessage }}</div>
      <template #footer>
        <div class="flex justify-end gap-2">
          <Button label="Cancel" class="p-button-text" @click="closeModal" />
          <Button
            :label="isEdit ? 'Update' : 'Create'"
            icon="pi pi-check"
            class="p-button-primary"
            @click="save"
          />
        </div>
      </template>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import api from '../api/axios.ts'

// PrimeVue components
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import DatePicker from 'primevue/datepicker'
import { formatDateTime, isNotEmpty } from '@/utils/dateUtil.ts'
interface Course {
  id: number | null
  name: string
  description: string
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
interface Trainer {
  id?: number
  name: string
  location: string
  availability: Availability[]
}
interface Availability {
  id: number
  timeSlot: string
  availableDate: string
  trainerId: number
  trainerName: string
}
const batches = ref<Batch[]>([])
const courses = ref<Course[]>([])
const trainers = ref<Trainer[]>([])
const showModal = ref(false)
const isEdit = ref(false)
const isTrainerSelected = ref(false)
const editingId = ref<number | null>(null)
const errorMessage = ref('')

const form = ref({
  name: '',
  courseId: null as number | null,
  location: '',
  trainerId: null as number | null,
  trainerName: '',
  availabilityId: null as number | null,
})
const avail = ref<{ availableDate: Date | null; fromTime: Date | null; toTime: Date | null }>({
  availableDate: null,
  fromTime: null,
  toTime: null,
})

const load = async () => {
  const res = await api.get('/batches/list')
  const { data, success, message } = res.data

  if (success) {
    batches.value = data
  } else {
    batches.value = []
    console.warn('Failed to load batches: ' + message)
  }
}

const loadTrainer = async () => {
  try {
    const trRes = await api.get('/trainers/list')
    const { data: trData, success: trSuccess } = trRes.data
    trainers.value = trSuccess ? trData : []
  } catch {
    trainers.value = []
  }
}
const loadCourses = async () => {
  const res = await api.get('/courses/list')
  const { data, success, message } = res.data
  if (success) {
    courses.value = data
  } else {
    courses.value = []
    console.warn('Failed to load courses: ' + message)
  }
}

const openModal = (batch?: Batch) => {
  showModal.value = true
  if (batch) {
    isEdit.value = true
    editingId.value = batch.id
    form.value.name = batch.name
    form.value.courseId = batch.courseId !== undefined ? batch.courseId : null
    form.value.location = batch.location
    form.value.trainerId = batch.trainerId !== undefined ? batch.trainerId : null
    form.value.trainerName = batch.trainerName !== undefined ? batch.trainerName : ''
    avail.value = {
      availableDate: new Date(batch.scheduledDate),
      fromTime: new Date('1970-01-01T' + batch.timeSlot?.split('-')[0] + ':00'),
      toTime: new Date('1970-01-01T' + batch.timeSlot?.split('-')[1] + ':00'),
    }
    // isTrainerSelected.value = !!batch.trainerId
  } else {
    isEdit.value = false
    isTrainerSelected.value = false
    editingId.value = null
    form.value.name = ''
    form.value.courseId = null
    form.value.location = ''
    form.value.trainerId = null
    form.value.trainerName = ''
    form.value.availabilityId = null
    avail.value = { availableDate: null, fromTime: null, toTime: null }
  }
}
const onSelectTrainer = (value: number) => {
  const selected = trainers.value.find((t) => t.id === value)
  form.value.trainerId = value
  form.value.trainerName = selected ? selected.name : ''
  form.value.location = selected ? selected.location : ''
  isTrainerSelected.value = !!selected
  form.value.availabilityId = null
  avail.value = { availableDate: null, fromTime: null, toTime: null }
}
const onSelectAavailability = (value: number) => {
  const selected = trainers.value
    .find((t) => t.id === form.value.trainerId)
    ?.availability.find((a: Availability) => a.id === value)
  avail.value.availableDate = selected ? new Date(selected.availableDate) : null
  avail.value.fromTime = selected
    ? new Date('1970-01-01T' + selected.timeSlot?.split('-')[0] + ':00')
    : null
  avail.value.toTime = selected
    ? new Date('1970-01-01T' + selected.timeSlot?.split('-')[1] + ':00')
    : null
}
const trainerAvailabilities = computed(() => {
  const trainer = trainers.value.find((t) => t.id === form.value.trainerId)
  return trainer
    ? trainer.availability.map((a) => ({
        ...a,
        label: `${a.availableDate} (${a.timeSlot})`, // ✅ combined label
      }))
    : []
})
const closeModal = () => {
  showModal.value = false
  resetForm()
}
const resetForm = () => {
  errorMessage.value = ''
  avail.value = { availableDate: null, fromTime: null, toTime: null }
  form.value = {
    name: '',
    courseId: null,
    location: '',
    trainerId: null,
    trainerName: '',
    availabilityId: null,
  }
  isTrainerSelected.value = false
  isEdit.value = false
  editingId.value = null
}

const save = async () => {
  if (!form.value.courseId || !isNotEmpty(form.value.location)) {
    errorMessage.value = 'Please fill all required fields'
    return
  }

  if (!avail.value.availableDate || !avail.value.fromTime || !avail.value.toTime) {
    errorMessage.value = 'Please select date and time slots'
    return
  }
  // Convert Date to string before sending to API
  const formattedDate = formatDateTime(avail.value.availableDate, 'yyyy-MM-dd')
  const fromTimeStr = formatDateTime(avail.value.fromTime, 'HH:mm')
  const toTimeStr = formatDateTime(avail.value.toTime, 'HH:mm')
  if (avail.value.fromTime >= avail.value.toTime) {
    errorMessage.value = 'From Time must be earlier than To Time'
    return
  }

  const payload: Batch = {
    id: editingId.value ?? null,
    name: form.value.name?.trim(),
    courseId: form.value.courseId,
    courseName: '',
    location: form.value.location?.trim(),
    trainerId: form.value.trainerId,
    trainerName: form.value.trainerName?.trim(),
    scheduledDate: formattedDate,
    timeSlot: `${fromTimeStr}-${toTimeStr}`,
  }

  const res = await api.post('/batches/create', payload)

  const { success, message } = res.data
  if (success) {
    alert(isEdit.value ? 'Batch updated' : 'Batch created')
    closeModal()
    await load()
  } else {
    alert('Failed: ' + message)
  }
}

const deleteBatch = async (id: number) => {
  if (!confirm('Are you sure you want to delete this batch?')) return
  const res = await api.delete(`/batches/delete/${id}`)
  const { success, message } = res.data
  if (success) {
    alert(message || 'Batch deleted')
    await load()
  } else {
    alert('Failed: ' + message)
  }
}

onMounted(async () => {
  await load()
  await loadCourses()
  await loadTrainer()
})
</script>
