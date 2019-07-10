

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CopyWriter {


    private String picturesPath =
            System.getProperty("user.home") +
                    "\\AppData" +
                    "\\Local" +
                    "\\Packages" +
                    "\\Microsoft.Windows.ContentDeliveryManager_cw5n1h2txyewy" +
                    "\\LocalState" +
                    "\\Assets";

    private String homePath = "C:\\Users\\dlosev.NBKI\\Pictures\\desktop_images";
    /**
     * @param args 0 -  [help] - помощь
     *                  [-c] - с одним аргументом
     *                  [-m] - с двумя аргументами
     *             1 - папка с изображениями для копии
     *             2 - папка с домашними изображениями
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        if (args.length == 0
                || args[0].equalsIgnoreCase("help")
                || args[0].equalsIgnoreCase("-help")
                || args[0].equalsIgnoreCase("h")
                || args[0].equalsIgnoreCase("-h")
                || args[0].isEmpty()
        ) {
            printHelp();
        } else if (args[0].equalsIgnoreCase("-c")
                && args.length == 2) {
            CopyWriter copyWriter = new CopyWriter(args[1]);
            copyWriter.mode1();
        } else if (args[0].equalsIgnoreCase("-m")
                && args.length == 3) {
            CopyWriter copyWriter = new CopyWriter(args[1], args[2]);
            copyWriter.mode1();
        } else {
            printHelp();
        }

    }

    public CopyWriter(String homePath) {
        this.homePath = homePath;
    }

    public CopyWriter(String homePath, String picturesPath) {
        this.homePath = homePath;
        this.picturesPath = picturesPath;
    }

    private CopyWriter() {
    }

    private static void printHelp() {
        System.out.println("-c arg_output_path\n" +
                "-m arg_output_path arg_input_path");
    }

    public void mode1() {
        System.out.println(new File(homePath).exists());
        copy();
        renameImagesInHomePath();
    }

    private void copy() {
        List<File> files = getImagesFromPicturesPath();
        files.forEach(this::copyImage);
    }

    private void renameImagesInHomePath() {
        List<File> oldPicturesList = getImagesFromHomePath();
        oldPicturesList.forEach(image -> {
            String oldName = image.getName();
            String extension = oldName.substring(oldName.lastIndexOf("."));
            String newName = getHexCode(image) + extension;
            if (newName.equals(oldName)) return;
            String path = homePath + "\\" + newName;
            boolean renamed = image.renameTo(new File(path));
            if (renamed) {
                System.out.format("%s : renamed successful to %s%n", oldName, newName);
            } else if (image.delete()){
                System.out.format("%s : copy of %s was deleted%n", oldName, newName);
            }
        });
    }

    private List<File> getImagesFromHomePath() {
        return Arrays.asList(Objects.requireNonNull(
                new File(homePath)
                        .listFiles(f -> f.getName().endsWith(".jpg")
                                || f.getName().endsWith(".png")
                        )
                ));
    }

    private List<File> getImagesFromPicturesPath() {
        return Arrays.asList(Objects.requireNonNull(new File(picturesPath)
                .listFiles(file -> {
                    if (file.isFile()) {
                        try {
                            BufferedImage bi = ImageIO.read(file);
                            return bi.getHeight() == 1080 && bi.getWidth() == 1920;
                        } catch (IOException e) {
                            System.out.format("Reading image error : %s%n", e.getMessage());
                        }
                    }
                    return false;
                })));
    }

    private String getHexCode(File file) {
        long res = 0L;
        try {

            FileInputStream fin = new FileInputStream(file);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            fin.close();
            for (int i = 1000; i < bytes.length - 1000; i++) {
                res += bytes[i] + 31 * res;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Long.toHexString(res).toUpperCase();
    }

    private void copyImage(File file){
        try {
            BufferedImage img = ImageIO.read(file);
            File outFile = new File(homePath + "\\" + file.getName() + ".png");
            ImageIO.write(img, "png", outFile);
        } catch (IOException e) {
            System.out.format("Copy error : %s%n", e.getMessage());
        }
    }

}
