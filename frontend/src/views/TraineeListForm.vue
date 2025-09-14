<template>
  <div class="p-4 min-h-screen bg-gray-900 text-gray-100">
    <div class="bg-gray-800 shadow-md rounded-lg p-4">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-4 gap-2">
        <h2 class="text-xl font-semibold">Trainees</h2>
        <Button
          label="Add"
          icon="pi pi-plus"
          class="p-button-sm p-button-info"
          @click="openModal()"
        />
      </div>

      <!-- Table -->
      <DataTable
        v-if="trainees && trainees.length"
        :value="trainees"
        paginator
        :rows="5"
        :rowsPerPageOptions="[5, 10, 20, 50]"
        scrollable
        scrollHeight="60vh"
        responsiveLayout="scroll"
        class="text-sm p-datatable-sm p-datatable-striped"
      >
        <Column field="name" header="Trainee Name"></Column>
        <Column field="email" header="Email"></Column>
        <Column field="batchName" header="Batch Name"></Column>
        <Column field="courseName" header="Course Name"></Column>

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
                @click="deleteTrainees(slotProps.data.id)"
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
      :header="isEdit ? 'Edit Trainee' : 'Add Trainee'"
      modal
      dismissableMask
      class="w-full max-w-lg"
    >
      <div class="flex flex-col gap-4">
        <div>
          <label class="block text-sm font-medium mb-1">Name</label>
          <InputText v-model="form.name" placeholder="Enter a name" class="w-full" />
        </div>
        <div>
          <label class="block text-sm font-medium mb-1">Email</label>
          <InputText v-model="form.email" @input="validateEmail" placeholder="Enter a email" class="w-full" />
        </div>
        <div>
          <label class="block text-sm font-medium mb-1">Batch</label>
          <Select
            v-model="form.batchId"
            :options="batches"
            optionLabel="name"
            optionValue="id"
            placeholder="Select batch"
            class="w-full"
            editable
            showClear
          >
          </Select>
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
import { ref, onMounted } from 'vue'
import api from '../api/axios.ts'

// PrimeVue components
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import { isNotEmpty, isValidEmail } from '@/utils/dateUtil.ts'

interface Trainee {
  id: number | null
  name: string | ''
  email: string | ''
  batchId: number | null
  batchName: string | ''
  courseId: number | null
  courseName: string | ''
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

const batches = ref<Batch[]>([])
const trainees = ref<Trainee[]>([])

const showModal = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const errorMessage = ref('')

const form = ref({
  name: '',
  email: '',
  batchId: null as number | null,
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

const loadTrainees = async () => {
  const res = await api.get('/trainees/list')
  const { data, success, message } = res.data
  if (success) {
    trainees.value = data
  } else {
    trainees.value = []
    console.warn('Failed to load trainee: ' + message)
  }
}

const openModal = (trainee?: Trainee) => {
  showModal.value = true
  if (trainee) {
    isEdit.value = true
    editingId.value = trainee.id
    form.value.name = trainee.name
    form.value.email = trainee.email
    form.value.batchId = trainee.batchId !== undefined ? trainee.batchId : null
  } else {
    isEdit.value = false
    editingId.value = null
    form.value = { name: '', email: '', batchId: null }
  }
}

const closeModal = () => {
  showModal.value = false
  resetForm()
}
const resetForm = () => {
  errorMessage.value = ''
  form.value = { name: '', email: '', batchId: null }
  editingId.value = null
}

const save = async () => {
  if (!isNotEmpty(form.value.name) || !isNotEmpty(form.value.email) || !form.value.batchId) {
    errorMessage.value = 'Please fill all required fields'
    return
  }

  const payload: Trainee = {
    id: editingId.value ?? null,
    name: form.value.name,
    email: form.value.email,
    batchId: form.value.batchId,
    batchName: '',
    courseId: null,
    courseName: '',
  }

  const res = await api.post('/trainees/create', payload)

  const { success, message } = res.data
  if (success) {
    alert(isEdit.value ? 'Trainee updated' : 'Trainee created')
    await loadTrainees()
    closeModal()
  } else {
    alert('Failed: ' + message)
  }
}

const deleteTrainees = async (id: number) => {
  if (!confirm('Are you sure you want to delete this trainee?')) return
  const res = await api.delete(`/trainees/delete/${id}`)
  const { success, message } = res.data
  if (success) {
    alert(message || 'Trainee deleted')
    await loadTrainees()
  } else {
    alert('Failed: ' + message)
  }
}

const validateEmail = () => {
  errorMessage.value = isValidEmail(form.value.email) ? "" : "Please enter a valid email"
}

onMounted(async () => {
  await loadTrainees()
  await loadBatches()
})
</script>
