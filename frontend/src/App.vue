<template>
  <div id="app" class="app-container bg-gray-900 text-white flex flex-col h-screen">
    <!-- Navbar -->
    <nav class="navbar flex items-center justify-between px-6 bg-gray-800 shadow-md">
      <h1 class="text-2xl font-bold">Training Management</h1>
      
      <!-- Mobile sidebar toggle button -->
      <button 
        class="md:hidden p-2 rounded hover:bg-gray-700" 
        @click="sidebarOpen = !sidebarOpen"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none"
             viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M4 6h16M4 12h16M4 18h16"/>
        </svg>
      </button>
    </nav>

    <div class="flex flex-1 overflow-hidden">
      <!-- Sidebar -->
      <aside 
        :class="['z-10',
          'bg-gray-800 text-gray-100 w-64 md:w-52 p-4 overflow-y-auto fixed md:static h-full transform transition-transform duration-300',
          sidebarOpen ? 'translate-x-0' : '-translate-x-full',
          'md:translate-x-0'
        ]"
      >
        <!-- Dashboard -->
        <router-link
          to="/dashboard"
          class="block py-2 rounded hover:bg-gray-700 mb-2"
          active-class="bg-gray-700 font-bold"
        >
          Dashboard
        </router-link>

        <!-- Trainer Management -->
        <div class="mb-4">
          <h3 class="text-gray-300 font-semibold mb-1">Trainer Management</h3>
          <router-link
            to="/trainers"
            class="block py-1 pl-4 text-sm rounded hover:bg-gray-700 text-gray-200"
            active-class="bg-gray-700 font-bold text-white"
          >
            Trainers & Availability
          </router-link>
          <router-link
            to="/courses"
            class="block py-1 pl-4 text-sm rounded hover:bg-gray-700 text-gray-200 mt-1"
            active-class="bg-gray-700 font-bold text-white"
          >
            Add / Edit Course
          </router-link>
        </div>

        <!-- Trainee Management -->
        <div class="mb-4">
          <h3 class="text-gray-300 font-semibold mb-1">Trainee Management</h3>
          <router-link
            to="/trainees"
            class="block py-1 pl-4 text-sm rounded hover:bg-gray-700 text-gray-200"
            active-class="bg-gray-700 font-bold text-white"
          >
            Trainees List & Enroll
          </router-link>
          <router-link
            to="/attendance"
            class="block py-1 pl-4 text-sm rounded hover:bg-gray-700 text-gray-200 mt-1"
            active-class="bg-gray-700 font-bold text-white"
          >
            Attendance
          </router-link>
        </div>

        <!-- Batch Management -->
        <div class="mb-4">
          <h3 class="text-gray-300 font-semibold mb-1">Batch Management</h3>
          <router-link
            to="/batches"
            class="block py-1 pl-4 text-sm rounded hover:bg-gray-700 text-gray-200"
            active-class="bg-gray-700 font-bold text-white"
          >
            Manage Batches
          </router-link>
          <router-link
            to="/batch-details"
            class="block py-1 pl-4 text-sm rounded hover:bg-gray-700 text-gray-200 mt-1"
            active-class="bg-gray-700 font-bold text-white"
          >
            Batch Details
          </router-link>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="flex-1 overflow-auto p-4">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const sidebarOpen = ref(false);
</script>

<style scoped>
.app-container {
  height: 100vh;
  flex-direction: column;
}

.navbar {
  height: 60px;
  flex-shrink: 0;
}

.main {
  background: #111827;
  color: #f3f4f6;
}
</style>
