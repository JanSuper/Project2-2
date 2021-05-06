
package Webcam;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class WebcamTest {

    public static void main(String[] args){
        Webcam cam = Webcam.getDefault();
        cam.open();
        try {
            ImageIO.write(cam.getImage(), "PNG", new File("test.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

