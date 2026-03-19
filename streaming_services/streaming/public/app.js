document.addEventListener('DOMContentLoaded', () => {
    const navigationPage = document.getElementById('navigation-page');
    const viewingPage = document.getElementById('viewing-page');
    const videoList = document.getElementById('video-list');
    const videoPlayer = document.getElementById('video-player');
    const showNavigationButton = document.getElementById('show-navigation');
    const fullscreenButton = document.querySelector('.fullscreen-button');

    const showPage = (pageId) => {
        document.querySelectorAll('.page').forEach(page => {
            page.classList.remove('active');
        });
        document.getElementById(pageId).classList.add('active');
    };

    // --- Navigation Page Logic ---
    const fetchAndDisplayVideos = async () => {
        try {
            const response = await fetch('/api/videos');
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const videos = await response.json();

            videoList.innerHTML = ''; // Clear skeleton items

            if (videos.length === 0) {
                videoList.innerHTML = '<li>No videos found in the database.</li>';
                return;
            }

            videos.forEach(video => {
                const listItem = document.createElement('li');
                const link = document.createElement('a');
                link.href = "#"; // Prevent default navigation
                link.textContent = video.name;
                link.dataset.videoId = video._id; // Store the unique database ID

                link.addEventListener('click', (e) => {
                    e.preventDefault();
                    const videoId = e.target.dataset.videoId;
                    const videoSrc = `/video?id=${encodeURIComponent(videoId)}`;
                    videoPlayer.src = videoSrc;
                    videoPlayer.load();
                    videoPlayer.play();

                    showPage('viewing-page');
                });

                listItem.appendChild(link);
                videoList.appendChild(listItem);
            });
        } catch (error) {
            console.error("Error fetching videos:", error);
            videoList.innerHTML = '<li>Failed to load videos. Please check your server and database connection.</li>';
        }
    };

    fetchAndDisplayVideos();

    // --- Viewing Page Logic ---
    fullscreenButton.addEventListener('click', () => {
        if (videoPlayer.requestFullscreen) {
            videoPlayer.requestFullscreen();
        } else if (videoPlayer.mozRequestFullScreen) {
            videoPlayer.mozRequestFullScreen();
        } else if (videoPlayer.webkitRequestFullscreen) {
            videoPlayer.webkitRequestFullscreen();
        } else if (videoPlayer.msRequestFullscreen) {
            videoPlayer.msRequestFullscreen();
        }
    });

    // --- Sidebar Navigation ---
    showNavigationButton.addEventListener('click', () => {
        showPage('navigation-page');
        if (!videoPlayer.paused) {
            videoPlayer.pause();
        }
    });

    showPage('navigation-page');
});