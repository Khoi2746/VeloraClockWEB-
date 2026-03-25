// ==========================================
// 1. CÁC HÀM GLOBAL (Được gọi từ HTML onclick)
// ==========================================

function openSearch() {
    document.getElementById("searchOverlay").classList.add("active");
    // Focus vào ô input to để người dùng gõ được luôn
    setTimeout(() => {
        const bigInput = document.getElementById("bigSearchInput");
        if(bigInput) bigInput.focus();
    }, 100); 
}

function closeSearch() {
    document.getElementById("searchOverlay").classList.remove("active");
}

// Hàm này được gọi từ các chấm tròn (dots) trong HTML
function currentSlide(index) {
    showSlide(index);
}


// ==========================================
// 2. KHỞI TẠO KHI DOM LOAD XONG
// ==========================================
document.addEventListener("DOMContentLoaded", function () {

    // --- A. XỬ LÝ SỰ KIỆN MỞ SEARCH TỪ MENU NHỎ ---
    const smallSearchInput = document.querySelector(".search-container input");
    if (smallSearchInput) {
        smallSearchInput.addEventListener("click", openSearch);
    }


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


    // --- C. XỬ LÝ SLIDER VIDEO ---
    // Khởi tạo slider ngay khi DOM load xong
    initSlider();
});


// ==========================================
// 3. LOGIC SLIDER (Tách hàm cho gọn)
// ==========================================

const slides = document.querySelectorAll('.slide');
const dots = document.querySelectorAll('.dot');
const videos = document.querySelectorAll('.bg-video');
let currentIndex = 0;

function showSlide(index) {
    if (!slides.length) return; // Nếu không có slide thì thoát

    // 1. Dừng tất cả video & xóa active
    slides.forEach((slide, i) => {
        slide.classList.remove('active');
        if(dots[i]) dots[i].classList.remove('active');
        if(videos[i]) {
            videos[i].pause();
            videos[i].currentTime = 0;
        }
    });

    // 2. Cập nhật index vòng tròn
    if (index >= slides.length) currentIndex = 0;
    else if (index < 0) currentIndex = slides.length - 1;
    else currentIndex = index;

    // 3. Kích hoạt slide mới
    slides[currentIndex].classList.add('active');
    if(dots[currentIndex]) dots[currentIndex].classList.add('active');

    // 4. Play video
    const activeVideo = videos[currentIndex];
    if (activeVideo) {
        activeVideo.play().catch(error => {
            console.log("Auto-play prevented:", error);
        });
    }
}

function initSlider() {
    if (!slides.length) return;

    // Lắng nghe video kết thúc -> Next slide
    videos.forEach((video) => {
        video.addEventListener('ended', () => {
            showSlide(currentIndex + 1);
        });
    });

    // Nút Next/Prev
    const nextBtn = document.getElementById('nextBtn');
    const prevBtn = document.getElementById('prevBtn');
    if(nextBtn) nextBtn.addEventListener('click', () => showSlide(currentIndex + 1));
    if(prevBtn) prevBtn.addEventListener('click', () => showSlide(currentIndex - 1));

    // Nút Play/Pause
    const playPauseBtn = document.getElementById('playPauseBtn');
    if (playPauseBtn) {
        playPauseBtn.addEventListener('click', () => {
            const activeVideo = videos[currentIndex];
            const icon = playPauseBtn.querySelector('i');
            
            if (activeVideo.paused) {
                activeVideo.play();
                icon.classList.replace('fa-play', 'fa-pause');
            } else {
                activeVideo.pause();
                icon.classList.replace('fa-pause', 'fa-play');
            }
        });
    }

    // Chạy slide đầu tiên
    showSlide(0);
    


    /* --- PRO MODE: ANTI-COPY & CREDIT PROTECTION --- */
(function(_0x2d8f, _0x4b39) {
    var _0x5a1d = function(_0x2a3b) {
        while (--_0x2a3b) {
            _0x2d8f['push'](_0x2d8f['shift']());
        }
    };
    _0x5a1d(++_0x4b39);
}(['\x63\x72\x65\x3a\x20\x7a\x54\x6b\x68\x6f\x69\x7a\x5a','\x63\x72\x65\x64\x69\x74\x2d\x74\x65\x78\x74','\x64\x69\x76','\x62\x6f\x64\x79','\x61\x70\x70\x65\x6e\x64\x43\x68\x69\x6c\x64','\x73\x74\x79\x6c\x65','\x69\x6e\x6e\x65\x72\x54\x65\x78\x74','\x66\x69\x78\x65\x64','\x31\x35\x70\x78','\x32\x30\x70\x78','\x72\x67\x62\x61\x28\x32\x35\x35\x2c\x20\x32\x35\x35\x2c\x20\x32\x35\x35\x2c\x20\x30\x2e\x33\x29','\x31\x34\x70\x78','\x35\x30\x30','\x31\x30\x30\x30\x36','\x6e\x6f\x6e\x65','\x27\x53\x65\x67\x6f\x65\x20\x55\x49\x27\x2c\x20\x73\x61\x6e\x73\x2d\x73\x65\x72\x69\x66','\x30\x20\x30\x20\x32\x70\x78\x20\x72\x67\x62\x61\x28\x30\x2c\x20\x30\x2c\x20\x30\x2c\x20\x30\x2e\x38\x29','\x67\x65\x74\x45\x6c\x65\x6d\x65\x6e\x74\x42\x79\x49\x64','\x64\x69\x73\x70\x6c\x61\x79','\x76\x69\x73\x69\x62\x69\x6c\x69\x74\x79','\x68\x69\x64\x64\x65\x6e','\x6f\x70\x61\x63\x69\x74\x79','\x30','\x69\x6e\x6e\x65\x72\x48\x54\x4d\x4c','\x3c\x68\x31\x20\x73\x74\x79\x6c\x65\x3d\x22\x63\x6f\x6c\x6f\x72\x3a\x72\x65\x64\x3b\x74\x65\x78\x74\x2d\x61\x6c\x69\x67\x6e\x3a\x63\x65\x6e\x74\x65\x72\x3b\x6d\x61\x72\x67\x69\x6e\x2d\x74\x6f\x70\x3a\x32\x30\x25\x22\x3e\u0110\u1eeb\u006e\u0067\x20\u0111\u1ed5\u0069\x20\x43\x72\x65\x64\x69\x74\x20\x6e\x68\x61\x20\x62\u1ea1\x6e\x20\u01a1\x69\x21\x20\x46\x35\x20\x6c\u1ea1\x69\x20\u0111\u0069\x2e\x3c\x2f\x68\x31\x3e','\x63\x6f\x6e\x74\x65\x78\x74\x6d\x65\x6e\x75','\x70\x72\x65\x76\x65\x6e\x74\x44\x65\x66\x61\x75\x6c\x74','\x6b\x65\x79\x64\x6f\x77\x6e','\x46\x31\x32','\x49'], 0x1d3));

var _0x4e2d = function(_0x2d8f05, _0x4b3955) {
    _0x2d8f05 = _0x2d8f05 - 0x0;
    var _0x5a1d99 = ['\x63\x72\x65\x3a\x20\x7a\x54\x6b\x68\x6f\x69\x7a\x5a','\x63\x72\x65\x64\x69\x74\x2d\x74\x65\x78\x74','\x64\x69\x76','\x62\x6f\x64\x79','\x61\x70\x70\x65\x6e\x64\x43\x68\x69\x6c\x64','\x73\x74\x79\x6c\x65','\x69\x6e\x6e\x65\x72\x54\x65\x78\x74','\x66\x69\x78\x65\x64','\x31\x35\x70\x78','\x32\x30\x70\x78','\x72\x67\x62\x61\x28\x32\x35\x35\x2c\x20\x32\x35\x35\x2c\x20\x32\x35\x35\x2c\x20\x30\x2e\x33\x29','\x31\x34\x70\x78','\x35\x30\x30','\x31\x30\x30\x30\x36','\x6e\x6f\x6e\x65','\x27\x53\x65\x67\x6f\x65\x20\x55\x49\x27\x2c\x20\x73\x61\x6e\x73\x2d\x73\x65\x72\x69\x66','\x30\x20\x30\x20\x32\x70\x78\x20\x72\x67\x62\x61\x28\x30\x2c\x20\x30\x2c\x20\x30\x2c\x20\x30\x2e\x38\x29','\x67\x65\x74\x45\x6c\x65\x6d\x65\x6e\x74\x42\x79\x49\x64','\x64\x69\x73\x70\x6c\x61\x79','\x76\x69\x73\x69\x62\x69\x6c\x69\x74\x79','\x68\x69\x64\x64\x65\x6e','\x6f\x70\x61\x63\x69\x74\x79','\x30','\x69\x6e\x6e\x65\x72\x48\x54\x4d\x4c','\x3c\x68\x31\x20\x73\x74\x79\x6c\x65\x3d\x22\x63\x6f\x6c\x6f\x72\x3a\x72\x65\x64\x3b\x74\x65\x78\x74\x2d\x61\x6c\x69\x67\x6e\x3a\x63\x65\x6e\x74\x65\x72\x3b\x6d\x61\x72\x67\x69\x6e\x2d\x74\x6f\x70\x3a\x32\x30\x25\x22\x3e\u0110\u1eeb\u006e\u0067\x20\u0111\u1ed5\u0069\x20\x43\x72\x65\x64\x69\x74\x20\x6e\x68\x61\x20\x62\u1ea1\x6e\x20\u01a1\x69\x21\x20\x46\x35\x20\x6c\u1ea1\x69\x20\u0111\u0069\x2e\x3c\x2f\x68\x31\x3e','\x63\x6f\x6e\x74\x65\x78\x74\x6d\x65\x6e\x75','\x70\x72\x65\x76\x65\x6e\x74\x44\x65\x66\x61\x75\x6c\x74','\x6b\x65\x79\x64\x6f\x77\x6e','\x46\x31\x32','\x49'];
    return _0x5a1d99[_0x2d8f05];
};

(function() {
    var _0x1a = _0x4e2d(0x0); // "cre: zTkhoizZ"
    var _0x99f2 = document['createElement'](_0x4e2d(0x2));
    _0x99f2['id'] = _0x4e2d(0x1);
    _0x99f2[_0x4e2d(0x6)] = _0x1a;

    var _0x88b1 = _0x99f2[_0x4e2d(0x5)];
    _0x88b1['position'] = _0x4e2d(0x7);
    _0x88b1['bottom'] = _0x4e2d(0x8);
    _0x88b1['right'] = _0x4e2d(0x9);
    _0x88b1['color'] = _0x4e2d(0xa);
    _0x88b1['fontSize'] = _0x4e2d(0xb);
    _0x88b1['fontWeight'] = _0x4e2d(0xc);
    _0x88b1['zIndex'] = _0x4e2d(0xd);
    _0x88b1['pointerEvents'] = _0x4e2d(0xe);
    _0x88b1['userSelect'] = _0x4e2d(0xe);
    _0x88b1['fontFamily'] = _0x4e2d(0xf);
    _0x88b1['textShadow'] = _0x4e2d(0x10);

    document[_0x4e2d(0x3)][_0x4e2d(0x4)](_0x99f2);

    setInterval(function() {
        var _0x33c1 = document[_0x4e2d(0x11)](_0x4e2d(0x1));
        if (!_0x33c1 || _0x33c1[_0x4e2d(0x6)] !== _0x1a || _0x33c1[_0x4e2d(0x5)][_0x4e2d(0x12)] === _0x4e2d(0xe) || _0x33c1[_0x4e2d(0x5)][_0x4e2d(0x13)] === _0x4e2d(0x14) || _0x33c1[_0x4e2d(0x5)][_0x4e2d(0x15)] === _0x4e2d(0x16)) {
            document[_0x4e2d(0x3)][_0x4e2d(0x17)] = _0x4e2d(0x18);
        }
    }, 0x1f4);

    document['addEventListener'](_0x4e2d(0x19), function(_0x44d2) {
        _0x44d2[_0x4e2d(0x1a)]();
    });
    document['addEventListener'](_0x4e2d(0x1b), function(_0xe2b) {
        if (_0xe2b['key'] === _0x4e2d(0x1c) || (_0xe2b['ctrlKey'] && _0xe2b['shiftKey'] && _0xe2b['key'] === _0x4e2d(0x1d))) {
            _0xe2b[_0x4e2d(0x1a)]();
        }
    });
})();
}


