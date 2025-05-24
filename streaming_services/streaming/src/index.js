const express = require("express");
const fs = require("fs");
const path = require("path");

//
// Throws an error if the PORT environment variable is missing.
//
if (!process.env.PORT) {
    throw new Error("Please specify the port number for the HTTP server with the environment variable PORT.");
}

//
// Extracts the PORT environment variable.
//
const PORT = process.env.PORT;

const app = express();

// Define the base directory for your media
// This should match your Docker volume mount point
const MEDIA_BASE_DIR_MOVIES = process.env.MEDIA_BASE_DIR_MOVIES || '/media/movies';
const MEDIA_BASE_DIR_TVSHOWS = process.env.MEDIA_BASE_DIR_TVSHOWS || '/media/tvshows';

// Route for the home page - lists available videos
app.get("/", async (req, res) => {
    try {
        let videoFiles = [];

        // List files from the movies directory
        const movieFiles = await fs.promises.readdir(MEDIA_BASE_DIR_MOVIES);
        movieFiles.forEach(file => {
            if (file.endsWith('.mp4')) {
                videoFiles.push({ type: 'movie', name: file, path: path.join(MEDIA_BASE_DIR_MOVIES, file) });
            }
        });

        // List files from the TV shows directory (you might want to organize this better for TV shows, e.g., by season/episode)
        const tvShowFiles = await fs.promises.readdir(MEDIA_BASE_DIR_TVSHOWS);
        tvShowFiles.forEach(file => {
            if (file.endsWith('.mp4')) {
                videoFiles.push({ type: 'tvshow', name: file, path: path.join(MEDIA_BASE_DIR_TVSHOWS, file) });
            }
        });

        // Generate HTML for the list of videos
        let html = `
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Video Streamer</title>
                <style>
                    body { font-family: sans-serif; margin: 20px; background-color: #f4f4f4; color: #333; }
                    h1 { color: #0056b3; }
                    ul { list-style: none; padding: 0; }
                    li { margin-bottom: 10px; background-color: #fff; padding: 10px; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    a { text-decoration: none; color: #007bff; font-weight: bold; }
                    a:hover { text-decoration: underline; color: #0056b3; }
                </style>
            </head>
            <body>
                <h1>Available Videos</h1>
                <ul>
        `;

        if (videoFiles.length === 0) {
            html += `<p>No MP4 videos found in the configured media directories.</p>`;
        } else {
            videoFiles.forEach(video => {
                // Encode the filename for the URL to handle spaces and special characters
                html += `<li><a href="/video?name=${encodeURIComponent(video.name)}&dir=${encodeURIComponent(video.type)}">${video.name} (${video.type})</a></li>`;
            });
        }

        html += `
                </ul>
            </body>
            </html>
        `;

        res.writeHead(200, { "Content-Type": "text/html" });
        res.end(html);

    } catch (error) {
        console.error("Error listing videos:", error);
        res.status(500).send("Error fetching video list.");
    }
});


// Registers a HTTP GET route for video streaming.
app.get("/video", async (req, res) => {
    // Get the video filename from a query parameter, e.g., /video?name=my_movie.mp4
    const videoFileName = req.query.name;
    const videoDirType = req.query.dir; // 'movie' or 'tvshow'

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

    // Construct the full path to the video file inside the container
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

            res.writeHead(206, headers); // 206 Partial Content
            const videoStream = fs.createReadStream(videoPath, { start, end });
            videoStream.pipe(res);
        } else {
            const headers = {
                "Content-Length": fileSize,
                "Content-Type": "video/mp4",
            };
            res.writeHead(200, headers); // 200 OK
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

//
// Starts the HTTP server.
//
app.listen(PORT, () => {
    console.log(`Microservice listening on port ${PORT}, point your browser at http://localhost:${PORT}/`);
});