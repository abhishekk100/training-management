import { createRouter, createWebHistory } from 'vue-router'
import TrainerListForm from '../views/TrainerListForm.vue'
import BatchListForm from '../views/BatchListForm.vue'
import TraineeListForm from '../views/TraineeListForm.vue'
import MarkAttendance from '../views/MarkAttendance.vue'
import TraniningReport from '../views/TraniningReport.vue'
import CourseListForm from '@/views/CourseListForm.vue'
import TrainerBatchView from '@/views/TrainerBatchView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/dashboard', component: TraniningReport },
    { path: '/trainers', component: TrainerListForm },
    { path: '/courses', component: CourseListForm },
    { path: '/batches', component: BatchListForm },
    { path: '/trainees', component: TraineeListForm },
    { path: '/attendance', component: MarkAttendance },
    { path: '/batch-details', component: TrainerBatchView },
  ],
})

export default router
