document.addEventListener('DOMContentLoaded', () => {
    const navigationPage = document.getElementById('navigation-page');
    const viewingPage = document.getElementById('viewing-page');
    const videoList = document.getElementById('video-list');
    const videoPlayer = document.getElementById('video-player');
    const showNavigationButton = document.getElementById('show-navigation');
    const fullscreenButton = document.querySelector('.fullscreen-button');

    // Function to switch pages
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
                videoList.innerHTML = '<li>No MP4 videos found.</li>';
                return;
            }

            videos.forEach(video => {
                const listItem = document.createElement('li');
                const link = document.createElement('a');
                link.href = "#"; // Prevent default navigation
                link.textContent = video.name;
                link.dataset.filename = video.name; // Store filename
                link.dataset.dirtype = video.type;   // Store directory type

                link.addEventListener('click', (e) => {
                    e.preventDefault(); // Stop the link from navigating
                    const filename = e.target.dataset.filename;
                    const dirType = e.target.dataset.dirtype;
                    const videoSrc = `/video?name=${encodeURIComponent(filename)}&dir=${encodeURIComponent(dirType)}`;
                    videoPlayer.src = videoSrc;
                    videoPlayer.load(); // Load the new video source
                    videoPlayer.play(); // Start playback

                    showPage('viewing-page'); // Switch to viewing page
                });

                listItem.appendChild(link);
                videoList.appendChild(listItem);
            });
        } catch (error) {
            console.error("Error fetching videos:", error);
            videoList.innerHTML = '<li>Failed to load videos. Please try again.</li>';
        }
    };

    // Initial load of videos when the page loads
    fetchAndDisplayVideos();

    // --- Viewing Page Logic ---
    fullscreenButton.addEventListener('click', () => {
        if (videoPlayer.requestFullscreen) {
            videoPlayer.requestFullscreen();
        } else if (videoPlayer.mozRequestFullScreen) { /* Firefox */
            videoPlayer.mozRequestFullScreen();
        } else if (videoPlayer.webkitRequestFullscreen) { /* Chrome, Safari and Opera */
            videoPlayer.webkitRequestFullscreen();
        } else if (videoPlayer.msRequestFullscreen) { /* IE/Edge */
            videoPlayer.msRequestFullscreen();
        }
    });

    // --- Sidebar Navigation ---
    showNavigationButton.addEventListener('click', () => {
        showPage('navigation-page');
        // Optionally pause video if switching away from viewing page
        if (!videoPlayer.paused) {
            videoPlayer.pause();
        }
    });

    // Initial page display
    showPage('navigation-page');
});