package com.curiousattemptbunny.extractframes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Run this to extract frames from MP4 videos in a folder.
 * Frames are extracted to a subfolder with the same name as the video.
 * It will detect and skip videos who's extractions previously completed.
 */

public class Monitor {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: <PATH_TO_VIDEO_FOLDER>");
            System.exit(1);
        }

        File videoFolder = new File(args[0]);
        if (!videoFolder.exists() || !videoFolder.isDirectory()) {
            System.err.println("Does not exist or is not a directory: "+args[0]);
        }

        List<File> files = Arrays.asList(videoFolder.listFiles());
        files.stream()
                .filter(File::isFile)
                .filter(filename -> filename.getName().endsWith(".MP4"))
                .forEach(video -> processVideo(video));
    }

    private static void processVideo(File video) {
        File framesFolder = new File(video.getParentFile(), video.getName().substring(0, video.getName().length() - ".MP4".length()));

        if (framesFolder.exists()) {
            if (lastFrameIndex(framesFolder) >= estimateFrameCount(video) - 60) {
                return;
            }
        }

        framesFolder.mkdir();

        String[] cmd = { "ffmpeg","-i", video.getAbsolutePath(), "-r", "60", framesFolder.getAbsolutePath()+"/frame%5d.jpg" };
        try {
            Process exec = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
            while(true) {
                String line = in.readLine();
                if (line == null) break;
                System.out.println(video.getName()+": "+line);
            }
            exec.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int estimateFrameCount(File video) {
        String[] cmd = { "ffmpeg","-i", video.getAbsolutePath(), "-vframes", "1", "-f", "null", "/dev/null" };
        Process exec = null;
        try {
            exec = Runtime.getRuntime().exec(cmd);
            exec.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
            List<String> lines = new ArrayList<>();
            while(true) {
                String line = in.readLine();
                if (line == null) break;
                lines.add(line);
            }
            String[] elements = lines.stream().filter(line -> line.startsWith("  Duration")).findFirst().get().split("\\s|,");
            String duration = elements[3];
            Object[] durations = Arrays.asList(duration.split(":")).stream().map(Double::parseDouble).toArray();
            int frameEstimate = (int)(((double)durations[2] * 60) + ((double)durations[1] * 60*60) + ((double)durations[0] * 60*60*60));
            System.out.println("Estimate: "+frameEstimate);
            return frameEstimate;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static int lastFrameIndex(File framesFolder) {
        File[] frames = framesFolder.listFiles();
        File lastFrame = frames[frames.length - 1];
        int lastFrameIndex = Integer.parseInt(lastFrame.getName().replaceAll("[^0-9]", ""));
        System.out.println(lastFrameIndex);
        return lastFrameIndex;
    }
}
