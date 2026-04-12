<template>
  <div class="new-arrival-page">
    <header>
      <div class="top-bar-wrapper">
        <div class="inner-content top-bar-content">
          <div class="jordan-logo">
            <img src="/image/Icon.png" alt="Icon">
          </div>
          <div class="top-right-links">
            <div class="nav-actions">
              <template v-if="user && user.roleId === 1">
                <a href="/admin/dashboard" style="font-weight: bold;">ADMIN DASHBOARD</a>
                <span>|</span>
              </template>
              <a href="#">Find a Store</a><span>|</span>
              <a href="#">Help</a><span>|</span>
              <template v-if="!user">
                <a href="/login">Login</a><span>|</span>
                <a href="/checkMail">Sign In</a>
              </template>
              <template v-else>
                <div class="user-dropdown">
                  <div class="user-name-wrapper">
                    Hi, <span>{{ user.firstName }}</span>
                    <i class="fa-solid fa-angle-down arrow-icon"></i>
                  </div>
                  <div class="dropdown-content">
                    <a href="/profile">Profile Detail</a>
                    <hr>
                    <a href="/change-password">Change Password</a>
                    <hr>
                    <a href="/logout" style="color: #d43f3a !important;">Log Out</a>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>

      <nav class="main-nav-wrapper">
        <div class="inner-content">
          <a href="/home" class="nike-logo">
            <img src="/image/VeloraIcon.jpg" alt="Velora">
          </a>
          <div class="nav-links">
            <div class="nav-item">
              <a href="#">Trang Sản Phẩm</a>
            </div>
          </div>
          <div class="nav-actions">
            <div class="search-container" @click="isSearchOpen = true">
              <i class="fa-solid fa-magnifying-glass search-icon-btn"></i>
              <input type="text" placeholder="Search" readonly>
            </div>
            <a href="/favorites" class="action-icon"><i class="fa-solid fa-heart"></i></a>
            <a href="/customer/orders" class="action-icon"><i class="fa-solid fa-box-archive"></i></a>
            <a href="/cart" class="action-icon"><i class="fa-solid fa-cart-shopping"></i></a>
          </div>
        </div>
      </nav>
    </header>

    <div class="page-header">
      <h1>
        {{ searchQuery ? `Search Result for "${searchQuery}"` : 'New' }} 
        <span>({{ filteredProducts.length }})</span>
      </h1>
      <div class="header-actions">
        <span @click="isSidebarVisible = !isSidebarVisible" class="clickable">
          {{ isSidebarVisible ? 'Hide Filters' : 'Show Filters' }} <i class="fa-solid fa-sliders"></i>
        </span>
      </div>
    </div>

    <div class="main-container">
      <aside :class="['sidebar', { hidden: !isSidebarVisible }]">
        <h3 class="filter-title">Lọc Sản Phẩm</h3>
        
        <ul class="category-list">
          <li v-for="cat in categories" :key="cat">
            <a href="javascript:void(0)" 
               :class="{ active: selectedCategory === cat }"
               @click="setCategory(cat)">
              {{ cat }}
            </a>
          </li>
          <li v-if="selectedCategory" class="clear-filter">
            <a href="javascript:void(0)" @click="setCategory(null)">
              <i class="fa-solid fa-xmark"></i> Xóa bộ lọc này
            </a>
          </li>
        </ul>

        <div class="filter-section">
          <div class="filter-group">
            <div class="filter-header" @click="toggleFilterGroup('gender')">
              Gender 
              <i :class="['fa-solid fa-chevron-up arrow', { rotated: !filterStates.gender }]"></i>
            </div>
            <div v-show="filterStates.gender" class="filter-content">
              <label v-for="g in ['Nam', 'Nữ']" :key="g" class="radio-label">
                <input type="radio" name="gender" :value="g" v-model="selectedGender"> {{ g }}
              </label>
              <a v-if="selectedGender" href="javascript:void(0)" @click="selectedGender = null" class="reset-link">Xóa lọc giới tính</a>
            </div>
          </div>

          <div class="filter-group">
            <div class="filter-header" @click="toggleFilterGroup('price')">
              Shop By Price
              <i :class="['fa-solid fa-chevron-up arrow', { rotated: !filterStates.price }]"></i>
            </div>
            <div v-show="filterStates.price" class="filter-content">
              <a href="javascript:void(0)" @click="sortOrder = 'desc'" :class="{ bold: sortOrder === 'desc' }">Price: High-Low</a>
              <a href="javascript:void(0)" @click="sortOrder = 'asc'" :class="{ bold: sortOrder === 'asc' }">Price: Low-High</a>
              <a v-if="sortOrder" href="javascript:void(0)" @click="sortOrder = null" class="reset-link">Xóa sắp xếp</a>
            </div>
          </div>
        </div>
      </aside>

      <main class="product-grid">
        <div class="product-card" v-for="product in filteredProducts" :key="product.id">
          <a :href="'/product/detail/' + product.id">
            <div class="product-img-box">
              <img :src="getFirstImage(product.imageUrl)" alt="Product Image">
            </div>
          </a>
          <div class="product-info">
            <h3>{{ product.name }}</h3>
            <p class="product-price">{{ formatPrice(product.price) }}₫</p>
          </div>
        </div>
        <div v-if="filteredProducts.length === 0" class="no-result">
          Không tìm thấy sản phẩm nào phù hợp.
        </div>
      </main>
    </div>

    <div :class="['search-overlay', { active: isSearchOpen }]">
      <div class="search-overlay-content">
        <div class="search-top-bar">
          <img src="/image/Icon.png" alt="Velora" class="overlay-logo">
          <div class="big-search-container">
            <i class="fa-solid fa-magnifying-glass search-icon-big"></i>
            <input type="text" v-model="searchQuery" placeholder="Search products...">
          </div>
          <button class="btn-cancel" @click="isSearchOpen = false">Cancel</button>
        </div>
      </div>
      <div class="overlay-backdrop" @click="isSearchOpen = false"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';

// --- STATES ---
const user = ref({ firstName: 'Khoi', roleId: 1 });
const products = ref([]);
const isSidebarVisible = ref(true);
const isSearchOpen = ref(false);
const searchQuery = ref('');

// Filters
const categories = ['Đồng Hồ Cơ', 'Đồng Hồ Kim', 'Đồng Hồ Thông Minh'];
const selectedCategory = ref(null);
const selectedGender = ref(null);
const sortOrder = ref(null);
const filterStates = ref({ gender: true, price: true });

// --- API FETCHING ---
const fetchProducts = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/products');
    products.value = response.data;
  } catch (error) {
    console.error("Error fetching products:", error);
  }
};

onMounted(() => {
  fetchProducts();
});

// --- LOGIC LỌC SẢN PHẨM (MẠNH MẼ NHẤT) ---
const filteredProducts = computed(() => {
  let result = [...products.value];

  // 1. Lọc theo Category
  if (selectedCategory.value) {
    result = result.filter(p => p.category === selectedCategory.value);
  }

  // 2. Lọc theo Gender
  if (selectedGender.value) {
    result = result.filter(p => p.gender === selectedGender.value);
  }

  // 3. Lọc theo Search Query
  if (searchQuery.value) {
    result = result.filter(p => p.name.toLowerCase().includes(searchQuery.value.toLowerCase()));
  }

  // 4. Sắp xếp giá
  if (sortOrder.value === 'asc') {
    result.sort((a, b) => a.price - b.price);
  } else if (sortOrder.value === 'desc') {
    result.sort((a, b) => b.price - a.price);
  }

  return result;
});

// --- ACTIONS ---
const setCategory = (cat) => selectedCategory.value = cat;
const toggleFilterGroup = (group) => filterStates.value[group] = !filterStates.value[group];

// Helpers
const getFirstImage = (url) => url ? url.split(',')[0].trim() : '/image/default.jpg';
const formatPrice = (p) => new Intl.NumberFormat('vi-VN').format(p);
</script>

<style scoped>
/* ==========================================================================
   1. GLOBAL & RESET
   ========================================================================== */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
}

body {
    background-color: #f5f3f1;
    color: #111;
}

a {
    text-decoration: none;
    color: white;
    cursor: pointer;
    transition: color 0.2s;
}
/* Override color inherit ở phần reset sau của em để đảm bảo đồng bộ */
a:hover { color: white; }

ul { list-style: none; }
img { display: block;
      width: 100%; }

/* Tiện ích Layout */
.inner-content {
    max-width: 1920px;
    margin: 0 auto;
    padding: 0 48px;
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
}

.container {
    display: flex;
    padding: 0 255px; /* Giữ nguyên lề layout Shop */
    margin-bottom: 60px;
    align-items: flex-start;
}

/* ==========================================================================
   2. HEADER & NAVIGATION
   ========================================================================== */
/* --- Top Bar --- */
.top-bar-wrapper {
    background-color: #24201d;
    width: 100%;
    height: 36px;
    position: relative;
    z-index: 202;
}

.top-bar-content { font-size: 12px; font-weight: 500; }

.top-right-links { display: flex; gap: 15px; }
.top-right-links a:hover { color: #fff; }

.jordan-logo img { width: 40px; height: auto; }

/* --- Main Nav --- */
.main-nav-wrapper {
    background-color: #24201d;
    width: 100%;
    height: 60px;
    z-index: 201;
    position: relative;
}

.nike-logo img { width: 255px; height: auto; }

.nav-links {
    display: flex;
    gap: 0;
    position: absolute;
    left: 0;
    width: 100%;
    justify-content: center;
    height: 100%;
    pointer-events: none;
}

.nav-item {
    pointer-events: auto;
    height: 100%;
    display: flex;
    align-items: center;
    padding: 0 12px;
    cursor: pointer;
}

.nav-item > a {
    font-weight: 600;
    font-size: 16px;
    padding-bottom: 2px;
    border-bottom: 2px solid transparent;
    color: white;
}

.nav-item:hover > a { border-bottom: 2px solid white; }

/* --- Mega Menu Dropdown --- */
.dropdown-menu {
    position: fixed;
    top: 96px;
    left: 0;
    width: 100%;
    background-color: white;
    padding: 40px 0 60px 0;
    z-index: 200;
    opacity: 0;
    visibility: hidden;
    transform: translateY(-5px);
    transition: all 0.1s ease-in-out;
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
    pointer-events: auto;
}

.nav-item:hover .dropdown-menu {
    opacity: 1;
    visibility: visible;
    transform: translateY(0);
}

.dropdown-inner {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 48px;
    display: flex;
    justify-content: center;
    gap: 80px;
}

.dropdown-column { min-width: 140px; }

.dropdown-column h3 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    color: #111;
}

.dropdown-column ul li { margin-bottom: 8px; }

.dropdown-column ul li a {
    font-size: 14px;
    color: #757575;
    font-weight: 500;
    display: block;
}

.dropdown-column ul li a:hover { color: #111; }

.nav-backdrop {
    position: fixed;
    top: 96px;
    left: 0;
    width: 100%;
    height: 100vh;
    background: rgba(0, 0, 0, 0.3);
    z-index: 99;
    opacity: 0;
    visibility: hidden;
    transition: opacity 0.2s ease;
    backdrop-filter: blur(2px);
    pointer-events: none;
}

.nav-links:hover ~ .nav-backdrop {
    opacity: 1;
    visibility: visible;
}

/* --- Nav Actions (Search, Cart, User) --- */
.nav-actions {
    color: #fff;
    display: flex;
    align-items: center;
    gap: 20px;
    z-index: 203;
    pointer-events: auto;
}

.search-container {
    position: relative;
    width: 180px;
}

.search-container input {
    width: 100%;
    padding: 8px 10px 8px 40px;
    border-radius: 20px;
    border: none;
    background-color: #f5f5f5;
    font-size: 16px;
    outline: none;
}

.search-container input:hover { background-color: #e5e5e5; }

.search-icon-btn {
    position: absolute;
    left: 10px;
    top: 50%;
    transform: translateY(-50%);
    color: #111;
    font-size: 20px;
}

.action-icon { font-size: 22px; color: #fff; }

/* User Dropdown */
.user-dropdown {
    position: relative;
    display: inline-block;
    cursor: pointer;
    padding: 0 5px;
}

.user-dropdown:hover .dropdown-content { display: block; }

.user-name-wrapper {
    font-weight: 600;
    font-size: 13px;
    color: #fff;
    display: flex;
    align-items: center;
    gap: 4px;
}

.arrow-icon { font-size: 9px; color: #757575; }

.dropdown-content {
    display: none;
    position: absolute;
    top: 100%;
    right: 0;
    background-color: #fff;
    min-width: 100px;
    box-shadow: 0px 4px 10px rgba(0,0,0,0.1);
    z-index: 1000;
    border-radius: 2px;
    border: 1px solid #f0f0f0;
}

.dropdown-content a {
    color: #111 !important;
    padding: 8px 12px;
    text-decoration: none;
    display: block;
    font-size: 12px;
    text-align: left;
}

.dropdown-content a:hover {
    background-color: #f5f5f5;
    color: #d43f3a !important;
}

/* Promo Bar */
.promo-bar-wrapper {
    background-color: #f5f5f5;
    border-top: 1px solid #e5e5e5;
    border-bottom: 1px solid #e5e5e5;
    width: 100%;
    padding: 10px 0;
    text-align: center;
}

.promo-content { display: block; text-align: center; }
.promo-text { font-size: 16px; font-weight: 500; margin-bottom: 4px; }
.promo-subtext { font-size: 12px; font-weight: 600; }
.promo-subtext a { text-decoration: underline; margin: 0 5px; }

/* ==========================================================================
   3. SEARCH OVERLAY
   ========================================================================== */
/* ==========================================================================
   3. SEARCH OVERLAY (UPDATED ANIMATION)
   ========================================================================== */
.search-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background-color: rgba(0, 0, 0, 0.4);
    z-index: 9999;
    visibility: hidden;
    opacity: 0;
    transition: all 0.3s ease;
}

.search-overlay.active {
    visibility: visible;
    opacity: 1;
}

.search-overlay-content {
    background-color: #24201d;
    width: 100%;
    padding: 20px 48px 40px 48px;
    transform: translateY(-100%);
    transition: transform 0.4s cubic-bezier(0.165, 0.84, 0.44, 1); /* Easing mượt hơn */
}

.search-overlay.active .search-overlay-content {
    transform: translateY(0);
}

/* --- ANIMATION KEYFRAMES --- */
@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* Ẩn các phần tử con trước khi active để chờ hiệu ứng */
.search-overlay .big-search-container,
.search-overlay .suggestion-title,
.search-overlay .search-tag {
    opacity: 0;
    transform: translateY(20px);
}

/* Khi Active thì kích hoạt animation lần lượt */
.search-overlay.active .big-search-container {
    animation: fadeInUp 0.5s ease forwards;
    animation-delay: 0.2s; /* Hiện sau khi màn trắng trượt xuống */
}

.search-overlay.active .suggestion-title {
    animation: fadeInUp 0.5s ease forwards;
    animation-delay: 0.3s;
}

/* Hiệu ứng từng tag nhảy vào lần lượt (Stagger) */
.search-overlay.active .search-tag:nth-child(1) { animation: fadeInUp 0.4s ease forwards 0.35s; }
.search-overlay.active .search-tag:nth-child(2) { animation: fadeInUp 0.4s ease forwards 0.40s; }
.search-overlay.active .search-tag:nth-child(3) { animation: fadeInUp 0.4s ease forwards 0.45s; }
.search-overlay.active .search-tag:nth-child(4) { animation: fadeInUp 0.4s ease forwards 0.50s; }
.search-overlay.active .search-tag:nth-child(5) { animation: fadeInUp 0.4s ease forwards 0.55s; }
.search-overlay.active .search-tag:nth-child(6) { animation: fadeInUp 0.4s ease forwards 0.60s; }
/* Nếu có nhiều tag hơn thì nó sẽ hiện cùng lúc với cái cuối, hoặc anh copy thêm dòng */

/* --- CÁC PHẦN CÒN LẠI GIỮ NGUYÊN --- */
.search-top-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 1920px;
    margin: 0 auto;
    height: 60px;
}

.overlay-logo img { width: 60px; }

.big-search-container {
    flex: 1;
    margin: 0 40px;
    position: relative;
    max-width: 600px;
}

.big-search-container input {
    width: 100%;
    padding: 12px 20px 12px 50px;
    font-size: 20px;
    background-color: #f5f5f5;
    border: none;
    border-radius: 24px;
    outline: none;
    font-weight: 500;
}
.big-search-container input:hover { background-color: #e5e5e5; }

.search-icon-big {
    position: absolute;
    left: 15px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 24px;
    color: #111;
}

.btn-cancel {
    background: none;
    border: none;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    color: #111;
}
.btn-cancel:hover { color: #757575; }

.search-suggestions {
    max-width: 1000px;
    margin: 30px auto 0 auto;
    padding-left: 100px;
}
.suggestion-title { font-size: 14px; color: #757575; margin-bottom: 15px; font-weight: 500; }
.tags-container { display: flex; flex-wrap: wrap; gap: 12px; }

.search-tag {
    background-color: #f5f5f5;
    color: #111;
    padding: 8px 20px;
    border-radius: 20px;
    font-size: 16px;
    font-weight: 500;
    transition: background 0.2s;
    /* Reset lại opacity để tránh xung đột khi hover */
    /* animation sẽ set opacity: 1 khi chạy xong */
}
.search-tag:hover { background-color: #d5d5d5; }
.overlay-backdrop { width: 100%; height: 100%; }
/* ==========================================================================
   4. HOME PAGE (Hero & Features)
   ========================================================================== */
/* Hero Slider */
.hero-slider {
    position: relative;
    width: 100%;
    height: 61vh;
    background: #000;
    overflow: hidden;
}

.bg-video {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.hero-content {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    color: #fff;
    width: 100%;
    z-index: 2;
}

.coming-soon {
    font-weight: 500;
    font-size: 1.1rem;
    margin-bottom: 10px;
}

.hero-title {
    font-size: 5rem;
    font-weight: 900;
    letter-spacing: -2px;
    margin-bottom: 5px;
}

.hero-desc {
    font-size: 1.2rem;
    margin-bottom: 25px;
}

.hero-actions button {
    padding: 8px 24px;
    border-radius: 20px;
    border: none;
    font-weight: 600;
    cursor: pointer;
    margin: 0 5px;
    transition: opacity 0.3s;
}

.btn-notify, .btn-watch { background: #fff; color: #000; }
.btn-notify:hover, .btn-watch:hover { opacity: 0.8; }

.slider-controls-bottom {
    position: absolute;
    bottom: 30px;
    right: 40px;
    display: flex;
    align-items: center;
    gap: 20px;
    z-index: 3;
}

.playback-controls { display: flex; gap: 10px; }

.playback-controls button {
    background: rgba(255, 255, 255, 0.2);
    border: none;
    color: #fff;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background 0.3s;
}

.playback-controls button:hover { background: rgba(255, 255, 255, 0.4); }

.dots-container {
    display: flex;
    gap: 8px;
    position: absolute;
    left: -50%;
    transform: translateX(-100%);
}

.dot {
    width: 8px;
    height: 8px;
    background: rgba(255, 255, 255, 0.5);
    border-radius: 50%;
}

.dot.active { background: #fff; }

.slide {
    display: none;
    position: absolute;
    width: 100%;
    height: 100%;
}

.slide.active { display: block; }

/* Featured Section */
.featured-section { padding: 30px 0; }
.section-title { font-size: 1.5rem; font-weight: 500; margin-bottom: 25px; }

.featured-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
}

.featured-item {
    position: relative;
    width: 100%;
    aspect-ratio: 4 / 4;
    overflow: hidden;
    cursor: pointer;
}

.featured-item img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
}



.featured-item:hover img { transform: scale(1.03); }

.item-overlay {
    position: absolute;
    bottom: 40px;
    left: 40px;
    color: #fff;
    z-index: 2;
}

.item-overlay .category { font-size: 1rem; margin-bottom: 5px; }
.item-overlay .item-title { font-size: 1.5rem; font-weight: 500; margin-bottom: 20px; }

.btn-shop {
    display: inline-block;
    background: #fff;
    color: #000;
    padding: 8px 20px;
    border-radius: 20px;
    text-decoration: none;
    font-weight: 500;
    transition: background 0.3s;
}

.btn-shop:hover { background: #e5e5e5; }

@media (max-width: 768px) {
    .featured-grid { grid-template-columns: 1fr; }
}


.sidebar li a {
    color:#000
}
.filter-group a {
    color: #000;
}

/* ==========================================================================
   5. SHOP PAGE LAYOUT
   ========================================================================== */
/* Page Header (Sticky) */
.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 250px; /* Giữ thông số của phần shop layout */
    position: sticky;
    top: 0;
    background: #24201d;
    z-index: 100;
    transition: transform 0.3s;
}

.page-header h1 { font-size: 24px; font-weight: 500; color: #fff; }

.header-actions {
    display: flex;
    gap: 25px;
    align-items: center;
    color: #fff;
}

.clickable {
    cursor: pointer;
    font-size: 16px;
    user-select: none;
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 500;
}

.clickable:hover { color: #757575; }

/* Sort Dropdown */
.sort-wrapper { position: relative; }

.sort-menu {
    position: absolute;
    top: 30px;
    right: 0;
    left: auto;
    background-color: white;
    width: 150px;
    padding: 10px 0;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    opacity: 0;
    visibility: hidden;
    transform: translateY(-10px);
    transition: all 0.2s ease;
    z-index: 101;
    text-align: right;
}

.sort-menu.active {
    opacity: 1;
    visibility: visible;
    transform: translateY(0);
}

.sort-menu a {
    display: block;
    padding: 8px 20px;
    font-size: 15px;
    color: #111;
    text-decoration: none;
    transition: color 0.2s;
}

.sort-menu a:hover { color: #757575; }
.arrow-sort { transition: transform 0.3s; }
.arrow-sort.rotated { transform: rotate(180deg); }


/* ==========================================================================
   THIẾT KẾ LẠI PHẦN SIDEBAR FILTER (LỌC)
   ========================================================================== */

.sidebar {
    width: 260px;
    padding-right: 40px;
    position: sticky;
    top: 50px;
    padding-top: 40px;
    height: calc(100vh - 80px);
    overflow-y: auto;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    opacity: 1;
}

.sidebar.hidden {
    width: 0;
    padding-right: 0;
    opacity: 0;
    overflow: hidden;
}

/* 1. Lọc Danh mục */
.category-list {
    padding-bottom: 24px;
    border-bottom: 1px solid #e5e5e5; /* Đổi màu viền cho nhẹ nhàng hơn */
    margin-bottom: 24px;
}

.category-list li {
    padding: 8px 0;
    font-weight: 500;
    font-size: 16px; /* Chữ to lên xíu cho dễ đọc */
}

.category-list li a {
    color: #111;
    transition: color 0.2s;
}

.category-list li a:hover { 
    color: #757575; 
}

/* 2. Style chung cho các Group (Gender, Price) */
.filter-group { 
    border-top: 1px solid #e5e5e5; 
    padding: 16px 0; 
}
.filter-group:first-of-type {
    border-top: none; /* Bỏ viền top của group đầu tiên để không bị đè viền */
}

.filter-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 500;
    font-size: 16px;
    cursor: pointer;
    color: #111;
}

/* 3. Nội dung bên trong Filter */
.filter-content {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-top: 16px;
    overflow: hidden;
    max-height: 400px;
    opacity: 1;
    transition: all 0.3s ease;
}

.filter-content.collapsed {
    max-height: 0;
    margin-top: 0;
    opacity: 0;
}

/* Form Gender */
.filter-content form {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

/* Style cho label của Radio Button */
.filter-content label {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 15px;
    font-weight: 400;
    cursor: pointer;
    color: #111;
    transition: color 0.2s ease;
}

.filter-content label:hover {
    color: #757575;
}

/* Tùy chỉnh ô Radio Button */
.filter-content input[type="radio"] {
    appearance: none;
    -webkit-appearance: none;
    width: 20px;
    height: 20px;
    border: 1.5px solid #ccc;
    border-radius: 50%; /* Radio button phải hình tròn */
    cursor: pointer;
    position: relative;
    transition: all 0.2s ease;
}

.filter-content input[type="radio"]:checked {
    border-color: #111;
}

/* Dấu chấm đen ở giữa khi chọn Radio */
.filter-content input[type="radio"]:checked::after {
    content: "";
    width: 10px;
    height: 10px;
    background-color: #111;
    border-radius: 50%;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
}

/* Nút Apply */
.filter-apply-btn {
    margin-top: 8px;
    padding: 10px 16px;
    background-color: #111;
    color: #fff;
    border: none;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.2s ease, opacity 0.2s ease;
    width: 100%; /* Cho nút tràn ngang nhìn khoẻ khoắn */
}

.filter-apply-btn:hover {
    opacity: 0.8;
}

/* Các link lọc Giá (Price) */
.filter-group a {
    color: #111;
    font-size: 15px;
    transition: color 0.2s ease;
    padding: 4px 0;
}

.filter-group a:hover {
    color: #757575;
}

/* Icon mũi tên */
.arrow i { transition: transform 0.3s; }
.arrow.rotated i { transform: rotate(180deg); }

/* ==========================================================================
   KẾT THÚC PHẦN SIDEBAR FILTER
   ========================================================================== */



/* ==========================================================================
   6. PRODUCTS & GRID
   ========================================================================== */
/* Grid Layout */
.product-grid {
    flex: 1;
    display: grid;
    padding: 20px;
    grid-template-columns: repeat(3, 1fr);
    gap: 40px; /* Giữ gap 40px theo định nghĩa Shop Layout */
    transition: width 0.4s ease;
}

/* Card trong Grid (Override riêng cho layout grid) */
.product-grid .product-card {
    min-width: 0 !important;
    width: 100%;
    margin: 0;
}

/* Style riêng cho ảnh trong Grid (khi nằm trong product-grid) */
.product-grid .product-img-box {
    width: 100%;
    aspect-ratio: 1/1;
    overflow: hidden;
    margin-bottom: 12px;
    background-color: #ffff;
}

.product-grid .product-img-box img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    mix-blend-mode: multiply;
    transform: scale(0.9);
}

/* Promo Card (Ảnh to) */
.promo-card {
    grid-column: span 1;
    position: relative;
    background-color: #f5f5f5;
    aspect-ratio: 1/1;
    overflow: hidden;
}

.promo-card img {
    width: 150%;
    height: 150%;
    object-fit: cover;
}

.promo-text-overlay {
    position: absolute;
    bottom: 30px;
    left: 24px;
    color: white;
    z-index: 2;
}

.promo-text-overlay h2 {
    font-size: 20px;
    margin-bottom: 16px;
    font-weight: 500;
}

.btn-shop-promo {
    background: white;
    color: #111;
    padding: 8px 20px;
    border-radius: 30px;
    font-weight: 600;
    font-size: 14px;
    transition: background 0.2s;
}

.btn-shop-promo:hover { background: #e5e5e5; }

/* Product Card Generic (Style chung cho thẻ sản phẩm lẻ) */
.product-card { cursor: pointer; }

/* NOTE: Đây là style product-img-box em ghi đè ở cuối file gốc (width 104%, aspect 4/3) */
/* Nó sẽ áp dụng cho các product card KHÔNG nằm trong .product-grid, hoặc bị ghi đè bởi style grid bên trên */
.product-img-box {
    background-color: #f5f5f5;
    width: 104%;
    aspect-ratio: 4/3;
    margin-bottom: 12px;
    position: relative;
}

.product-img-box img {
    width: 100%;
    height: 130%;
    object-fit: contain;
    mix-blend-mode: multiply;
}
/* Thông tin sản phẩm */
.product-info { padding: 0 0px; }
.tag-just-in { color: #9E3500; font-weight: 500; font-size: 15px; margin-bottom: 4px; }
.product-name { font-size: 30px; font-weight: 500; margin-bottom: 4px; line-height: 1.5; }
.product-category { color: #757575; font-size: 16px; margin-bottom: 8px; }
.product-price { font-weight: 500; font-size: 16px; margin-top: 4px; }

/* Product Carousel Specifics */
.product-carousel-section { padding: 50px 0; overflow: hidden; }
.carousel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.carousel-title { font-size: 1.5rem; font-weight: 500; }
.carousel-nav { display: flex; align-items: center; gap: 15px; }
.shop-link { text-decoration: none; color: #000; font-weight: 500; }
.nav-btn { width: 48px; height: 48px; border-radius: 50%; border: none; background: #f5f5f5; cursor: pointer; transition: background 0.3s; }
.nav-btn:hover { background: #e5e5e5; }

.product-container {
    display: flex;
    gap: 15px;
    overflow-x: auto;
    scroll-behavior: smooth;
    scrollbar-width: none;
}
.product-container::-webkit-scrollbar { display: none; }
.product-container .product-card { min-width: 450px; flex-shrink: 0; }

/* ==========================================================================
   7. FOOTER
   ========================================================================== */
.nike-footer {
    background-color: #111;
    color: #fff;
    padding: 40px 0 20px;
    font-size: 14px;
}

.footer-container {
    width: 90%;
    margin: auto;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 30px;
}

.footer-column h4 {
    margin-bottom: 15px;
    font-size: 14px;
    text-transform: uppercase;
}

.footer-column ul { list-style: none; padding: 0; }
.footer-column ul li { margin-bottom: 8px; }

.footer-column ul li a {
    color: #aaa;
    text-decoration: none;
    font-size: 13px;
}
.footer-column ul li a:hover { color: #fff; }

.social-icons a { color: #fff; margin-right: 12px; font-size: 18px; }

.footer-bottom {
    border-top: 1px solid #333;
    margin-top: 30px;
    padding-top: 15px;
    width: 90%;
    margin-left: auto;
    margin-right: auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
}

.footer-links a {
    color: #aaa;
    margin-left: 15px;
    text-decoration: none;
    font-size: 12px;
}

.footer-links a:hover { color: #fff; }

/* ==========================================================================
   8. PRODUCT DETAIL - XÓA NỀN TRẮNG CỦA ẢNH
   ========================================================================== */

/* Ép tàng hình nền trắng cho ảnh to trên Slider */
.product-slide {
    mix-blend-mode: multiply; 
}

/* Ép tàng hình nền trắng cho 4 cái ảnh nhỏ ở dưới */
.color-options img {
    mix-blend-mode: multiply; 
}

/* Đảm bảo khung chứa ảnh to có nền trùng với nền web (màu trắng hoặc xám tùy em) */
.product-slider-container {
    background-color: #f5f5f5; /* Em đổi thành #fff nếu muốn nền trắng muốt */
}
</style>