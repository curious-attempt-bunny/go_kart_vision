package com.curiousattemptbunny.extractframes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ffmpeg {
    public static final int FRAMES_PER_SECOND = 60;

    private final File mp4;

    public Ffmpeg(File mp4) {
        this.mp4 = mp4;
    }

    public void extractFrames(File framesFolder) {
        String[] cmd = {
                "ffmpeg",
                "-i", mp4.getAbsolutePath(),
                "-r", Integer.toString(FRAMES_PER_SECOND),
                framesFolder.getAbsolutePath()+"/frame%5d.jpg"
        };

        try {
            Process exec = Runtime.getRuntime().exec(cmd);

            BufferedReader in = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
            while(true) {
                String line = in.readLine();
                if (line == null) break;
                System.out.println(mp4.getName()+": "+line);
            }

            exec.waitFor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int estimateFrameCount() {
        String[] cmd = {
                "ffmpeg",
                "-i", mp4.getAbsolutePath(),
                "-vframes", "1",
                "-f", "null",
                "/dev/null"
        };

        try {
            Process exec = Runtime.getRuntime().exec(cmd);
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
            int frameEstimate = (int)(((double)durations[2] * FRAMES_PER_SECOND) + ((double)durations[1] * FRAMES_PER_SECOND*60) + ((double)durations[0] * FRAMES_PER_SECOND*60*60));

            return frameEstimate;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
