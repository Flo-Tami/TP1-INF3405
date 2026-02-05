import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageProcessor {

    public static byte[] process(byte[] imageBytes) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        BufferedImage result = Sobel.process(image);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(result, "png", out);
        return out.toByteArray();
    }
}
