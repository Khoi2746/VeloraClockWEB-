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