package com.curiousattemptbunny.extractframes;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Run this to extract frames from MP4 videos in a folder.
 * Frames are extracted to a subfolder with the same name as the video.
 * It will detect and skip videos who's extractions previously completed.
 */

public class Monitor {
    private final File videoFolder;

    public Monitor(File videoFolder) {
        this.videoFolder = videoFolder;
    }

    private void processVideos() {
        if (!videoFolder.exists() || !videoFolder.isDirectory()) {
            System.err.println("Does not exist or is not a directory: "+videoFolder.getAbsolutePath());
        }

        List<File> files = Arrays.asList(videoFolder.listFiles());
        files.stream()
                .filter(File::isFile)
                .filter(filename -> filename.getName().endsWith(".MP4"))
                .map(filename -> new Video(filename))
                .forEach(video -> video.extractFrames());
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: <PATH_TO_VIDEO_FOLDER>");
            System.exit(1);
        }

        new Monitor(new File(args[0])).processVideos();
    }
}
