const express = require("express");
const fs = require("fs");
const path = require("path");
const mongodb = require("mongodb");

//
// Throws an error if any required environment variables are missing.
//

if (!process.env.PORT) {
    throw new Error("Please specify the port number for the HTTP server with the environment variable PORT.");
}

if (!process.env.DBHOST) {
    throw new Error("Please specify the database host using environment variable DBHOST.");
}

if (!process.env.DBNAME) {
    throw new Error("Please specify the name of the database using environment variable DBNAME");
}

//
// Extracts environment variables to globals for convenience.
//

const PORT = process.env.PORT;
const DBHOST = process.env.DBHOST;
const DBNAME = process.env.DBNAME;

// Define the base directories for your media, matching your Docker volumes
const MEDIA_BASE_DIR_MOVIES = process.env.MEDIA_BASE_DIR_MOVIES || '/media/movies';
const MEDIA_BASE_DIR_TVSHOWS = process.env.MEDIA_BASE_DIR_TVSHOWS || '/media/tvshows';

// Main async function to connect to the DB and start the app
async function main() {
    try {
        const client = await mongodb.MongoClient.connect(DBHOST); // Connects to the database.
        const db = client.db(DBNAME);
        const videosCollection = db.collection("videos");
        
        // --- Express.js Application Setup ---
        const app = express();

        // Serve static files from the 'public' directory
        app.use(express.static(path.join(__dirname, 'public')));
        
        // --- API endpoint to get the list of videos as JSON ---
        app.get("/api/videos", async (req, res) => {
            try {
                // Fetch all video records from the 'videos' collection
                const videos = await videosCollection.find({}).toArray();
                res.json(videos); // Send the video list as a JSON array
            } catch (error) {
                console.error("Error fetching videos from database:", error);
                res.status(500).json({ error: "Failed to fetch video list from database." });
            }
        });

        // --- HTTP GET route for video streaming from storage ---
        app.get("/video", async (req, res) => {
            const videoId = req.query.id; // Expecting an ID, not a filename
            
            if (!videoId) {
                return res.status(400).send("Please specify a video ID using the 'id' query parameter.");
            }

            let videoRecord;
            try {
                videoRecord = await videosCollection.findOne({ _id: new mongodb.ObjectId(videoId) });
            } catch (error) {
                console.error(`Invalid video ID: ${videoId}`, error);
                return res.status(400).send("Invalid video ID format.");
            }

            if (!videoRecord) {
                // The video was not found.
                return res.sendStatus(404);
            }

            // Construct the full path to the video file from the database record
            const videoPath = path.join(videoRecord.dir, videoRecord.filename);

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
                    return res.status(404).send("Video file not found on disk.");
                }
                res.status(500).send("Error streaming video.");
            }
        });

        // --- Start the HTTP server. ---
        app.listen(PORT, () => {
            console.log(`Microservice listening on port ${PORT}, point your browser at http://localhost:${PORT}/`);
        });
    } catch (err) {
        console.error("Microservice failed to start.");
        console.error(err && err.stack || err);
        process.exit(1);
    }
}

main();