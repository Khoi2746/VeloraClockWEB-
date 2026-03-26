document.addEventListener('DOMContentLoaded', function() {
    const searchBtn = document.querySelector('.search-icon-btn'); // Kính lúp ở header
    const searchInput = document.querySelector('.search-container input'); // Ô input ở header
    const searchOverlay = document.getElementById('searchOverlay');
    const closeBtn = document.getElementById('closeSearch');
    const backdrop = document.getElementById('backdrop');

    // Hàm mở search
    function openSearch() {
        searchOverlay.classList.add('active');
        document.body.style.overflow = 'hidden'; // Chặn cuộn trang
        setTimeout(() => {
            document.getElementById('overlaySearchInput').focus();
        }, 300);
    }

    // Hàm đóng search
    function closeSearch() {
        searchOverlay.classList.remove('active');
        document.body.style.overflow = 'auto'; // Cho phép cuộn lại
    }

    // Click vào kính lúp
    if(searchBtn) searchBtn.addEventListener('click', openSearch);

    // Click vào ô input header cũng mở overlay cho chuyên nghiệp
    if(searchInput) searchInput.addEventListener('focus', openSearch);

    // Click nút Cancel hoặc nền đen để đóng
    if(closeBtn) closeBtn.addEventListener('click', closeSearch);
    if(backdrop) backdrop.addEventListener('click', closeSearch);

    // Nhấn phím ESC để đóng
    document.addEventListener('keydown', function(e) {
        if (e.key === "Escape") closeSearch();
    });
});