import java.util.Date;
import java.util.Objects;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.ffmpeg.global.avutil;

public class Movie2Gif {

    public static void main(String[] args) {

        String inputFile = args[0]; // "big_buck_bunny.mp4"
        String outputFile = String.format("%1$s_%2$tY%2$tm%2$td%2$tH%2$tM%2$tS.gif", inputFile, new Date());
        int width = Integer.parseInt(args[1]); // 640
        int height = Integer.parseInt(args[2]); // 360
        int fps = Integer.parseInt(args[3]); // 5
        Movie2Gif.convert(inputFile, outputFile, width, height, fps);

    }

    /**
     * Convert movie to animated GIF
     * 
     * @param input  name of movie file (file name or URI)
     * @param output name of GIF file (file name or URI)
     * @param width  width of GIF file
     * @param height height of GIF file
     * @param fps    fps of GIF file
     */
    public static void convert(String input, String output, int width, int height, int fps) {

        try (
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(input);
                // use fps filter to reduce file size.
                FFmpegFrameFilter filter = new FFmpegFrameFilter(String.format("fps=fps=%d", fps), width, height);
                FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(output, width, height)) {

            grabber.start();

            filter.setSampleFormat(grabber.getSampleFormat());
            filter.setSampleRate(grabber.getSampleRate());
            filter.setPixelFormat(grabber.getPixelFormat());
            filter.setFrameRate(grabber.getFrameRate());
            filter.start();

            // specify supported pixel format.
            recorder.setPixelFormat(avutil.AV_PIX_FMT_RGB8);
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setFrameRate(fps);
            recorder.start();

            Frame frame, filteredFrame;
            while (Objects.nonNull((frame = grabber.grabFrame(false, true, true, false)))) {

                if (Objects.nonNull(frame.image) || Objects.nonNull(frame.samples)) {
                    filter.push(frame);
                }

                if (Objects.nonNull((filteredFrame = filter.pull()))) {
                    if (Objects.nonNull(filteredFrame.image) || Objects.nonNull(filteredFrame.samples)) {
                        recorder.record(filteredFrame);
                    }
                }

            }

        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        } catch (org.bytedeco.javacv.FrameFilter.Exception e) {
            e.printStackTrace();
        }

    }

}