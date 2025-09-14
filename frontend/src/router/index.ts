import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/dashboard', component: () => import('../views/TraniningReport.vue')},
    { path: '/trainers', component: () => import('../views/TrainerListForm.vue') },
    { path: '/courses', component: () => import('../views/CourseListForm.vue') },
    { path: '/batches', component: () => import('../views/BatchListForm.vue') },
    { path: '/trainees', component: () => import('../views/TraineeListForm.vue') },
    { path: '/attendance', component: () => import('../views/MarkAttendance.vue') },
    { path: '/batch-details', component: () => import('../views/TrainerBatchView.vue') },
  ],
})

export default router
