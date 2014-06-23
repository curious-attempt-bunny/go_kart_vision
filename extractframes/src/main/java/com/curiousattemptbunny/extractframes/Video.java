package com.curiousattemptbunny.extractframes;

import java.io.File;

/**
 * Created by home on 6/22/14.
 */
public class Video {
    public static final int ESTIMATION_ACCURACY_MARGIN = Ffmpeg.FRAMES_PER_SECOND / 5;

    private final File video;

    public Video(File mp4) {
        this.video = mp4;
    }

    public void extractFrames() {
        File framesFolder = new File(video.getParentFile(), video.getName().substring(0, video.getName().length() - ".MP4".length()));
        Ffmpeg ffmpeg = new Ffmpeg(video);

        if (framesFolder.exists()) {
            if (lastFrameIndex(framesFolder) >= ffmpeg.estimateFrameCount() - ESTIMATION_ACCURACY_MARGIN) {
                return;
            }
        }

        framesFolder.mkdir();

        ffmpeg.extractFrames(framesFolder);
    }

    private static int lastFrameIndex(File framesFolder) {
        File[] frames = framesFolder.listFiles();
        if (frames.length == 0) return 0;

        File lastFrame = frames[frames.length - 1];
        int lastFrameIndex = Integer.parseInt(lastFrame.getName().replaceAll("[^0-9]", ""));

        return lastFrameIndex;
    }
}
