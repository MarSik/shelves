package org.marsik.elshelves.scanner;

import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

import static org.bytedeco.javacpp.opencv_core.IplImage;

@Configuration
@EnableAutoConfiguration
@ComponentScan("org.marsik.elshelves.scanner")
public class Application {
    public static void main(String[] args) throws FrameGrabber.Exception {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
        builder.headless(false).run(args);
        detectMotion();
    }

    public static void detectMotion() throws FrameGrabber.Exception {
        long c = 0;

        opencv_highgui.CvCapture capture = opencv_highgui.cvCreateCameraCapture(0);
/*
        opencv_highgui.cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT, 720);
        opencv_highgui.cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_WIDTH, 1280);
*/
        IplImage grabbedImage = opencv_highgui.cvQueryFrame(capture);

        final int width = grabbedImage.width();
        final int height = grabbedImage.height();

        CanvasFrame frame = new CanvasFrame("Webcam") {
            protected void initCanvas(boolean fullScreen, DisplayMode displayMode, double gamma) {
                try {
                    super.initCanvas(fullScreen, displayMode, gamma);
                }
                catch (RuntimeException ex) {
                    // will fail at the end of the function
                }
                if (!fullScreen) {
                    canvas.setSize(width, height);
                    canvas.createBufferStrategy(2);
                    // canvas.setIgnoreRepaint(true); // you may be able to do this on the mac because of how quartz works
                }
            }
        };
        frame.setIgnoreRepaint(false);

        while (frame.isVisible() && (grabbedImage = opencv_highgui.cvQueryFrame(capture)) != null) {
            frame.showImage(grabbedImage);
            System.out.println(String.format("frame grabbed %d", c++));
        }

        frame.dispose();
        opencv_highgui.cvReleaseCapture(capture);
    }
}
