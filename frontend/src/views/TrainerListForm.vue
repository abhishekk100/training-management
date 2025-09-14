<template>
  <div class="p-4 min-h-screen bg-gray-900 text-gray-100">
    <!-- Trainer List Card -->
    <div class="bg-gray-800 shadow-md rounded-lg p-4">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-4 gap-2">
        <h2 class="text-xl font-semibold mb-6">Manage Trainer</h2>
        <Button
          label="Add"
          icon="pi pi-plus"
          class="p-button-sm p-button-info"
        @click="openModal()"
        />
      </div>

      <!-- Table -->
      <DataTable
        v-if="trainers.length"
        :value="trainers"
        paginator
        :rows="5"
        :rowsPerPageOptions="[5, 10, 20, 50]"
        scrollable
        scrollHeight="60vh"
        responsiveLayout="scroll"
        class="text-sm p-datatable-sm p-datatable-striped"
      >
        <Column field="name" header="Name"></Column>
        <Column field="location" header="Location"></Column>
        <Column header="Actions" style="width: 8rem" >
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
                @click="deleteTrainer(slotProps.data.id)"
              />
            </div>
          </template>
        </Column>
      </DataTable>
      <div v-else class="text-gray-400 text-sm mt-2">No records available</div>
    </div>

    <!-- Trainer Modal -->
    <Dialog
      v-model:visible="showModal"
      :header="isEdit ? 'Edit Trainer' : 'Add Trainer'"
      :modal="true"
      :closable="true"
      class="w-full max-w-lg"
    >
      <div class="flex flex-col gap-3 mb-4">
        <InputText v-model="form.name" placeholder="Name" class="w-full" />
        <InputText v-model="form.location" placeholder="Location" class="w-full" />
      </div>
      
      <div class="flex justify-end gap-2">
        <Button
          :label="isEdit ? 'Edit' : 'Add'"
          icon="pi pi-check"
          class="p-button-sm p-button-primary"
          @click="save"
        />
        <Button
          label="Cancel"
          icon="pi pi-times"
          class="p-button-sm p-button-secondary"
          @click="closeModal"
        />
      </div>
      <div v-if="errorMessage" class="text-red-400 text-sm mb-2">{{ errorMessage }}</div>

      <!-- Availability Section -->
      <div v-if="isEdit" class="mt-6">
        <div class="flex mb-6 items-center gap-4">
          <h3 class="text-lg font-medium">Availability (upcoming month)</h3>
          <div class="sm:w-auto ml-auto">
            <Button
              icon="pi pi-plus"
              class="p-button-sm p-button-primary w-full sm:w-auto"
              @click="addAvailability"
            />
          </div>
        </div>

        <div class="flex flex-col sm:flex-row sm:items-end gap-4 mb-6">
          <!-- Date -->
          <div class="flex-1">
            <FloatLabel>
              <label for="available_date">Date</label>
              <DatePicker
                v-model="avail.availableDate"
                dateFormat="yy-mm-dd"
                placeholder="Select Date"
                size="small"
                class="w-full"
                showIcon
                inputId="available_date"
                iconDisplay="input"
                :minDate="new Date()"
              />
            </FloatLabel>
          </div>

          <!-- From Time -->
          <div class="flex-1">
            <FloatLabel>
              <label for="from_time_slot_label">From Time</label>
              <DatePicker
                v-model="avail.fromTime"
                inputId="from_time_slot_label"
                placeholder="Select Time"
                timeOnly
                showIcon
                size="small"
                class="w-full"
                iconDisplay="input"
              />
            </FloatLabel>
          </div>

          <!-- To Time -->
          <div class="flex-1">
            <FloatLabel>
              <label for="to_time_slot_label">To Time</label>
              <DatePicker
                size="small"
                v-model="avail.toTime"
                inputId="to_time_slot_label"
                placeholder="Select Time"
                timeOnly
                showIcon
                iconDisplay="input"
                class="w-full"
              />
            </FloatLabel>
          </div>
        </div>
        <!-- Availability Table -->
        <DataTable
          v-if="availability.length"
          :value="availability"
          class="text-sm p-datatable-sm p-datatable-striped"
          responsiveLayout="scroll"
        >
          <Column field="availableDate" header="Date"></Column>
          <Column field="timeSlot" header="Time Slot"></Column>
          <Column field="status" header="Available"></Column>
          <Column header="Action" style="width: 100px">
            <template #body="slotProps">
              <Button
                icon="pi pi-trash"
                class="p-button-sm p-button-danger"
                @click="removeAvailability(slotProps.data.id)"
                :disabled="slotProps.data.status?.toLowerCase() === 'no'"
              />
            </template>
          </Column>
        </DataTable>
        <div v-else class="text-gray-400 text-sm">No availability yet.</div>
      </div>
    </Dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '../api/axios.ts'

// PrimeVue components
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import DatePicker from 'primevue/datepicker'
import FloatLabel from 'primevue/floatlabel'
import { formatDateTime, isNotEmpty } from '@/utils/dateUtil.ts'
interface Trainer {
  id?: number
  name: string
  location: string
  availability:[]
}
interface Availability {
  id: number
  availableDate: string
  timeSlot: string
  status: "Yes"
}

const trainers = ref<Trainer[]>([])
const availability = ref<Availability[]>([])
const form = ref({ name: '', location: '' })
const avail = ref<{ availableDate: Date | null; fromTime: Date | null; toTime: Date | null }>({
  availableDate: null,
  fromTime: null,
  toTime: null,
})
const trainerId = ref<number | null>(null)

const showModal = ref(false)
const isEdit = ref(false)
const errorMessage = ref('');
const load = async () => {
  const res = await api.get('/trainers/list')
  const { data, success } = res.data
  trainers.value = success ? data : []
}

const loadAvailability = async (trainerId: number) => {
  try {
    const res = await api.get(`/availability/trainer/${trainerId}`)
    const { data, success } = res.data
    availability.value = success ? data : []
  } catch { 
    availability.value = []
  }
}

const openModal = (trainer?: Trainer) => {
  showModal.value = true
  if (trainer?.id) {
    isEdit.value = true
    trainerId.value = trainer.id
    form.value = { name: trainer.name, location: trainer.location }
    loadAvailability(trainer.id)
  } else {
    isEdit.value = false
    trainerId.value = null
    form.value = { name: '', location: '' }
    availability.value = []
  }
}

const closeModal = () => {
  showModal.value = false
  errorMessage.value = ''
  avail.value = { availableDate: null, fromTime: null, toTime: null }
  availability.value = []
  trainerId.value = null
  form.value = { name: '', location: '' }
  isEdit.value = false
}

const save = async () => {
  if (! isNotEmpty(form.value.name) || !isNotEmpty(form.value.location)) {
    errorMessage.value = 'Name and Location are required'
    return
  }
  const payload = { 
    name: form.value.name?.trim(), 
    location: form.value.location?.trim() 
}
  if (isEdit.value && trainerId.value) {
    await api.put(`/trainers/update/${trainerId.value}`, payload)
    alert('Trainer updated')
  } else {
    await api.post('/trainers/create', payload)
    alert('Trainer created')
  }
  closeModal()
  await load()
}

const deleteTrainer = async (id: number) => {
  if (!confirm('Are you sure you want to delete this trainer?')) return
  await api.delete(`/trainers/delete/${id}`)
  alert('Trainer deleted')
  await load()
}

const addAvailability = async () => {
  if (
    !trainerId.value ||
    !avail.value.availableDate ||
    !avail.value.fromTime ||
    !avail.value.toTime
  ) {
    errorMessage.value = 'Availability date and time slots are required'
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

  const payload = {
    availableDate: formattedDate,
    timeSlot: `${fromTimeStr}-${toTimeStr}`,
    status: "Yes"
  }
  await api.post(`/availability/trainer/${trainerId.value}`, payload)
  avail.value = { availableDate: null, fromTime: null, toTime: null }
  await loadAvailability(trainerId.value)
}

const removeAvailability = async (id: number) => {
  await api.delete(`/availability/${id}`)
  if (trainerId.value) await loadAvailability(trainerId.value)
}


onMounted(load)
</script>
