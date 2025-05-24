const express = require("express");
const fs = require("fs");
const path = require("path"); // Import the 'path' module

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

//
// Registers a HTTP GET route for video streaming.
//
app.get("/video", async (req, res) => {
    // Get the video filename from a query parameter, e.g., /video?name=my_movie.mp4
    const videoFileName = req.query.name;

    if (!videoFileName) {
        return res.status(400).send("Please specify a video file name using the 'name' query parameter.");
    }

    // Determine the base directory for your media.
    // You could also pass this as an environment variable if you have multiple root directories.
    const mediaBaseDir = process.env.MEDIA_BASE_DIR || '/media/movies'; // Default to movies directory

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
    console.log(`Microservice listening on port ${PORT}, point your browser at http://localhost:${PORT}/video?name=YOUR_VIDEO_FILENAME.mp4`);
});