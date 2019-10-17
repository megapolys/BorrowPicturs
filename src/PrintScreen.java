import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class PrintScreen {

    private BufferedImage image;
    private String savePath;

    public static void main(String[] args) throws Exception {
        final String savePath = "C:\\Tests\\images";
        System.out.println("Start program with outPath = " + savePath);
        final PrintScreen printScreen = new PrintScreen(savePath);
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    printScreen.process();
                } catch (Exception e) {
                    throw new RuntimeException("Something go wrong ...", e);
                }
            }
        }, 0, 1000);
        Thread.sleep(5_000);
        timer.cancel();
        System.out.println("Exit program");
    }

    public PrintScreen(String savePath) {
        this.savePath = savePath;
    }

    public void process() throws AWTException, IOException {
        final BufferedImage screenshort = getScreenshot();
        if (image == null) {
            image = screenshort;
        } else {
            if (bufferedImagesEqual(image, screenshort)) {
                System.out.println("File is already exists.");
            } else {
                saveImage(screenshort);
                image = screenshort;
            }
        }
    }

    public BufferedImage getScreenshot() throws AWTException {
        Rectangle rec = new Rectangle(
            Toolkit.getDefaultToolkit().getScreenSize());
        Robot robot = new Robot();
        BufferedImage img = robot.createScreenCapture(rec);
        return img;
    }

    public void saveImage(BufferedImage image) throws IOException {
        final File newImageFile = createNewImageFile(image);
        ImageIO.write(image, "jpg", newImageFile);
        System.out.println("File " + newImageFile + " is saved.");
    }

    private File createNewImageFile(BufferedImage image) throws IOException {
        final File file = new File(String.format("%s/%tH_%<tM_%<tS.jpg", savePath, Calendar.getInstance()));
        file.createNewFile();
        return file;
    }

    private boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
//                        System.out.printf("x = %s, y = %s%n", x, y);
                        return false;
                    }
                }
            }
        } else {
//            System.out.printf("Different sizes width = { %d : %d }; height = { %d : %d }%n",
//                img1.getWidth(), img2.getWidth(), img1.getHeight(), img2.getHeight());
            return false;
        }
        return true;
    }

}
