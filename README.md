# Go Kart Vision

I enjoy go karting. I want to get better at it. I bought a GoPro to record my races at 1080p @ 60 fps. This is me looking to learn something from analysing the video.

# Before you try this at home

I'm using OSX 10.9.3, Java, IntelliJ IDEA, ffmpeg, and OpenCV 2.4.9. This project is probably not very portable (pull requests accepted)! It includes my OSX build of OpenCV, requires *nix as it makes assumptions about the existance of /dev/null, and is somewhat IntelliJ-centric.

# How I've cheated

I've found that putting blue duct tape along the top of the steering wheel gets me from recognizing the steering wheel angle 90% of the time to 99.9%+ of the time as it's much more robust to speed blur due to bumps. It's also a quick, easy, low tech, change to make that doesn't damage the go kart in anyway.

# Getting started

1. Install ffmpeg and have it available on the path. E.g. `brew install ffmpeg`.
1. Generate your IDEA project files using `./gradlew idea`.

# Usage

## Extracting video frames

* Run `com.curiousattemptbunny.extractframes.Monitor` with a single argument - the path to the folder containing your GoPro videos. Note that this can consume a lot of i-nodes on your filesystem (~130 minutes of video frames used just under 5% of my journal's free i-nodes).