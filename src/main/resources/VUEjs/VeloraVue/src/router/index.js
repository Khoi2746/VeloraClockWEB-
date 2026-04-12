import { createRouter, createWebHistory } from 'vue-router';

// Nhớ check kỹ tên file: index.vue (i thường) hay Index.vue (I hoa) nhé ku em
import Index from '../components/index.vue';
import NewArrival from '../components/NewArrival.vue';

const routes = [
  {
    path: '/',
    // Redirect (điều hướng) từ trang chủ sang /home để đồng bộ với thanh địa chỉ
    redirect: '/home' 
  },
  {
    path: '/home',
    name: 'Home',
    component: Index
  },
  {
    path: '/new-arrivals',
    name: 'NewArrival',
    component: NewArrival
  },
  // THÊM DÒNG NÀY: Để nếu người dùng gõ bậy bạ nó vẫn quay về Home, không bị trắng trang
  {
    path: '/:pathMatch(.*)*',
    redirect: '/home'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  // Thêm cái này để mỗi khi chuyển trang nó tự động cuộn lên đầu trang
  scrollBehavior() {
    return { top: 0 }
  }
});

export default router;