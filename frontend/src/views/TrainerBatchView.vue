<template>
  <div class="bg-gray-900 text-gray-100 h-screen flex flex-col">
    <h2 class="text-2xl font-semibold p-4 border-b border-gray-700">My Batches</h2>

    <div class="flex-1 overflow-auto p-4 space-y-6 mb-12 md:mb-10 sm:mb-6">
      <div 
        v-for="batch in batches" 
        :key="batch.id ?? `batch-${Math.random()}`" 
        class="bg-gray-800 rounded-lg shadow-md p-4 flex flex-col"
      >
        <!-- Batch Header -->
        <div class="flex flex-col md:flex-row md:justify-between md:items-center mb-4 gap-2">
          <div>
            <h3 class="text-xl font-medium">{{ batch.courseName }}</h3>
            <p class="text-gray-400 text-sm">
              {{ batch.location }} | {{ batch.scheduledDate }} | {{ batch.timeSlot }}
            </p>
            <p class="text-gray-400 text-sm">Trainer: {{ batch.trainerName }}</p>
          </div>
          <Button 
            icon="pi pi-angle-down" 
            class="p-button-rounded p-button-text text-gray-200" 
            @click="toggleDetails(batch.id)" 
          />
        </div>

        <!-- Batch Details -->
        <div v-if="batch.showDetails" class="mt-4 space-y-4">
          <!-- Trainees Table -->
          <div class="overflow-auto">
            <h4 class="text-lg font-semibold mb-2">Enrolled Trainees</h4>
            <DataTable 
              :value="batch.trainees" 
              class="text-sm text-gray-100"
              scrollable 
              scrollHeight="200px"
            >
              <Column field="name" header="Name"></Column>
              <Column field="email" header="Email"></Column>
              <Column field="batchName" header="Batch"></Column>
            </DataTable>
            <div v-if="!batch.trainees?.length" class="text-gray-400 text-sm mt-2">
              No trainees enrolled yet.
            </div>
          </div>

          <!-- Attendance Summary -->
          <div class="overflow-auto">
            <h4 class="text-lg font-semibold mb-2">Attendance Summary</h4>
            <DataTable 
              :value="batch.attendance" 
              class="text-sm text-gray-100"
              scrollable 
              scrollHeight="200px"
            >
              <Column field="attendanceDate" header="Date"></Column>
              <Column field="traineeName" header="Trainee"></Column>
              <Column field="status" header="Status"></Column>
            </DataTable>
            <div v-if="!batch.attendance?.length" class="text-gray-400 text-sm mt-2">
              No attendance recorded yet.
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!batches.length" class="text-gray-400 text-center mt-10">
      You are not assigned to any batches.
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import Button from 'primevue/button';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import api from '@/api/axios';

interface Trainee {
  id: number | null;
  name: string;
  email: string;
  batchName: string;
}

interface Attendance {
  attendanceDate: string;
  traineeName: string;
  status: string;
}

interface Batch {
  id: number | null;
  trainerId: number;
  trainerName: string;
  courseId: number;
  courseName: string;
  location: string;
  scheduledDate: string;
  timeSlot: string;
  trainees?: Trainee[];
  attendance?: Attendance[];
  showDetails?: boolean;
}

const batches = ref<Batch[]>([]);

const loadBatches = async () => {
    const res = await api.get('/batches/summary')
    const { data, success, message } = res.data
    
    if (success) {
        batches.value = data.map((batch: Batch) => ({
            ...batch,
            showDetails: false,
            trainees: batch.trainees || [],
            attendance: batch.attendance || []
        }));
    } else {
        batches.value = []
        console.warn('Failed to load batches: ' + message)
    }
};

const toggleDetails = (batchId: number | null) => {
  const batch = batches.value.find(b => b.id === batchId);
  if (batch) batch.showDetails = !batch.showDetails;
};

onMounted(loadBatches);
</script>

