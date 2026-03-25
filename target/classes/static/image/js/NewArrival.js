
        function openSearch() {
            document.getElementById("searchOverlay").classList.add("active");
            document.getElementById("bigSearchInput").focus();
        }
        function closeSearch() {
            document.getElementById("searchOverlay").classList.remove("active");
        }
        document.addEventListener("DOMContentLoaded", function () {
            const smallSearchInput = document.querySelector(".search-container input");
            if (smallSearchInput) {
                smallSearchInput.addEventListener("click", openSearch);
            }
        });

        const slides = document.querySelectorAll('.slide');
const dots = document.querySelectorAll('.dot');
const videos = document.querySelectorAll('.bg-video');
let currentIndex = 0;
    // --- B. XỬ LÝ TÌM KIẾM (SEARCH) ---
    const bigSearchInput = document.getElementById('bigSearchInput');
    if (bigSearchInput) {
        // Dùng 'keydown' để bắt phím Enter nhạy hơn
        bigSearchInput.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') {
                e.preventDefault(); 
                
                const keyword = bigSearchInput.value.trim();
                
                if (keyword.length > 0) {
                    // Chuyển hướng sang trang tìm kiếm
                    const url = `/search?keyword=${encodeURIComponent(keyword)}`;
                    window.location.href = url;
                }
            }
        });
    }
//SIDE BAR FILTERS
document.addEventListener("DOMContentLoaded", function() {
        
        // 1. Xử lý nút Hide Filters / Show Filters
        const btnHideFilter = document.getElementById('btnHideFilter');
        const sidebar = document.getElementById('mainSidebar');
        
        if (btnHideFilter && sidebar) {
            btnHideFilter.addEventListener('click', function() {
                // Thêm/Bỏ class 'hidden' cho sidebar
                sidebar.classList.toggle('hidden');
                
                // Đổi text và icon của nút bấm
                if (sidebar.classList.contains('hidden')) {
                    this.innerHTML = 'Show Filters <i class="fa-solid fa-sliders"></i>';
                } else {
                    this.innerHTML = 'Hide Filters <i class="fa-solid fa-sliders"></i>';
                }
            });
        }

        // 2. Xử lý Accordion (Mở lên mở xuống từng mục filter)
        const filterHeaders = document.querySelectorAll('.filter-header');
        
        filterHeaders.forEach(header => {
            header.addEventListener('click', function() {
                // Tìm nội dung (div filter-content) ngay bên dưới header
                const content = this.nextElementSibling;
                const arrow = this.querySelector('.arrow');
                
                // Toggle class để đóng/mở
                content.classList.toggle('collapsed');
                
                // Xoay mũi tên
                if (arrow) arrow.classList.toggle('rotated');
            });
        });
    });

    // --- 2. CHỨC NĂNG SORT BY (XỔ MENU) ---
    const btnSort = document.getElementById('btnSort');
    const sortMenu = document.getElementById('sortMenu');
    const arrowSort = document.querySelector('.arrow-sort');

    if (btnSort && sortMenu) {
        btnSort.addEventListener('click', function(e) {
            e.stopPropagation(); // Ngăn chặn click lan ra ngoài
            
            // Toggle hiện menu
            sortMenu.classList.toggle('active');
            
            // Xoay mũi tên
            if(arrowSort) arrowSort.classList.toggle('rotated');
        });

        // Click ra ngoài thì đóng menu Sort
        document.addEventListener('click', function(e) {
            if (!btnSort.contains(e.target) && !sortMenu.contains(e.target)) {
                sortMenu.classList.remove('active');
                if(arrowSort) arrowSort.classList.remove('rotated');
            }
        });
    }