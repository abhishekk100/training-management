<template>
  <div class="p-4 min-h-screen bg-gray-900 text-gray-100">
    <div class="bg-gray-800 shadow-md rounded-lg p-4">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-4 gap-2">
        <h2 class="text-xl font-semibold">courses</h2>
        <Button
          label="Add"
          icon="pi pi-plus"
          class="p-button-sm p-button-info"
          @click="openModal()"
        />
      </div>

      <!-- Table -->
      <DataTable
        v-if="courses.length"
        :value="courses"
        paginator
        :rows="5"
        :rowsPerPageOptions="[5, 10, 20, 50]"
        scrollable
        scrollHeight="60vh"
        responsiveLayout="scroll"
        class="text-sm p-datatable-sm p-datatable-striped"
      >
        <Column field="name" header="Course Name"></Column>
        <Column field="description" header="Description"></Column>

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
                @click="deleteCourse(slotProps.data.id)"
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
        <!-- Course -->
        <div>
          <label class="block text-sm font-medium mb-1">Course Name</label>
          <InputText v-model="form.name" placeholder="Course name" class="w-full" />
        </div>
        <div>
          <label class="block text-sm font-medium mb-1">Description</label>
          <InputText v-model="form.description" placeholder="Description" class="w-full" />
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
import { isNotEmpty } from '@/utils/dateUtil.ts'

interface Course {
  id: number | null
  name: string
  description: string
}

const courses = ref<Course[]>([])

const showModal = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const errorMessage = ref('');

const form = ref({
  name: '',
  description: '',
})

const load = async () => {
  const res = await api.get('/courses/list')
  if (res.status === 200 && res.data.success) {
    courses.value = res.data.data
  } else {
    courses.value = []
  }
}

const openModal = (course?: Course) => {
  showModal.value = true
  if (course) {
    isEdit.value = true
    editingId.value = course.id
    form.value.name = course.name
    form.value.description = course.description
  } else {
    isEdit.value = false
    editingId.value = null
    form.value = { name: '', description: '' }
  }
}

const closeModal = () => {
  showModal.value = false
  resetForm();
}
const resetForm = () => {
    errorMessage.value = ''
    form.value = { name: '', description: '' }
    editingId.value = null
}


const save = async () => {
  if (!isNotEmpty(form.value.name)) {
    errorMessage.value = 'Course name is required'
    return
  }
    const payload = {
        id:null,
        name: form.value.name?.trim(),
        description: form.value.description?.trim(),
    }
    let res = null
    if (isEdit.value && editingId.value) {
        res = await api.put(`/courses/update/${editingId.value}`, payload)
    } else {
        res = await api.post('/courses/create', payload)
    }   
    if (res?.status === 200 && res?.data.success) {
        alert(isEdit.value ? 'Course updated' : 'Course created')
        await load()
        closeModal()
    } else {
        alert('Failed: ' + res?.data.message)
    }

}

const deleteCourse = async (id: number) => {
    if (confirm('Are you sure you want to delete this course?')) {
        const res = await api.delete(`/courses/delete/${id}`)
        if (res.status === 200 && res.data.success) {
        alert('Course deleted')
        await load()
        } else {
        alert('Failed: ' + res.data.message)
        }
    }
}

onMounted(async () => {
  await load()
})
</script>
