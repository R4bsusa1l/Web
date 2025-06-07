const express = require("express");
const fs = require("fs");
const path = require("path");

// ... (PORT environment variable check as before) ...
if (!process.env.PORT) {
    throw new Error("Please specify the port number for the HTTP server with the environment variable PORT.");
}
const PORT = process.env.PORT;

const app = express();

const MEDIA_BASE_DIR_MOVIES = process.env.MEDIA_BASE_DIR_MOVIES || '/media/movies';
const MEDIA_BASE_DIR_TVSHOWS = process.env.MEDIA_BASE_DIR_TVSHOWS || '/media/tvshows';

// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, 'public'))); // __dirname is streaming/

// API endpoint to get the list of videos as JSON
app.get("/api/videos", async (req, res) => {
    try {
        let videoFiles = [];

        // Function to get files from a directory and categorize them
        const getFiles = async (dirPath, type) => {
            try {
                const files = await fs.promises.readdir(dirPath);
                files.forEach(file => {
                    // Basic check for .mp4, you might want more robust validation
                    if (file.endsWith('.mp4')) {
                        videoFiles.push({ type: type, name: file });
                    }
                });
            } catch (error) {
                console.warn(`Could not read directory ${dirPath}: ${error.message}`);
                // Continue if one directory fails, don't block the whole list
            }
        };

        await getFiles(MEDIA_BASE_DIR_MOVIES, 'movie');
        await getFiles(MEDIA_BASE_DIR_TVSHOWS, 'tvshow');

        res.json(videoFiles); // Send JSON response
    } catch (error) {
        console.error("Error fetching video list:", error);
        res.status(500).json({ error: "Failed to fetch video list." });
    }
});

// Registers a HTTP GET route for video streaming. (This remains largely the same)
app.get("/video", async (req, res) => {
    const videoFileName = req.query.name;
    const videoDirType = req.query.dir;

    if (!videoFileName || !videoDirType) {
        return res.status(400).send("Please specify a video file name and directory type ('movie' or 'tvshow') using query parameters.");
    }

    let mediaBaseDir;
    if (videoDirType === 'movie') {
        mediaBaseDir = MEDIA_BASE_DIR_MOVIES;
    } else if (videoDirType === 'tvshow') {
        mediaBaseDir = MEDIA_BASE_DIR_TVSHOWS;
    } else {
        return res.status(400).send("Invalid directory type specified. Must be 'movie' or 'tvshow'.");
    }

    const videoPath = path.join(mediaBaseDir, videoFileName);

    try {
        const stats = await fs.promises.stat(videoPath);
        const fileSize = stats.size;
        const range = req.headers.range;

        if (range) {
            const parts = range.replace(/bytes=/, "").split("-");
            const start = parseInt(parts[0], 10);
            const end = parts[1] ? parseInt(parts[1], 10) : fileSize - 1;
            const chunkSize = (end - start) + 1;
            const headers = {
                "Content-Range": `bytes ${start}-${end}/${fileSize}`,
                "Accept-Ranges": "bytes",
                "Content-Length": chunkSize,
                "Content-Type": "video/mp4",
            };
            res.writeHead(206, headers);
            const videoStream = fs.createReadStream(videoPath, { start, end });
            videoStream.pipe(res);
        } else {
            const headers = {
                "Content-Length": fileSize,
                "Content-Type": "video/mp4",
            };
            res.writeHead(200, headers);
            fs.createReadStream(videoPath).pipe(res);
        }
    } catch (error) {
        console.error(`Error streaming video: ${videoPath}`, error);
        if (error.code === 'ENOENT') {
            return res.status(404).send("Video not found.");
        }
        res.status(500).send("Error streaming video.");
    }
});

app.listen(PORT, () => {
    console.log(`Microservice listening on port ${PORT}, point your browser at http://localhost:${PORT}/`);
});