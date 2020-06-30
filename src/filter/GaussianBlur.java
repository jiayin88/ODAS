package filter;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * @author Robbie
 */

//to perform Gaussain Blur based on standard deviation or sigma
//ideally will only give user selection of 1.0, 2.0, 3.0 and 4.0

public class GaussianBlur {

    public static BufferedImage gaussianBlur(BufferedImage image,double sigma) {

        int height = image.getHeight(null);
        int width = image.getWidth(null);

        BufferedImage tempImage = new BufferedImage(width, height, image.getType());
        BufferedImage filteredImage = new BufferedImage(width, height, image.getType());

        //--->>
        int n = (int) (6 * sigma + 1);

        double[] window = new double[n];
        double s2 = 2 * sigma * sigma;

        window[(n - 1) / 2] = 1;
        for (int i = 0; i < (n - 1) / 2; i++) {
            window[i] = Math.exp((double) (-i * i) / (double) s2);
            window[n - i - 1] = window[i];
        }

        //--->>
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double sum = 0;
                double[] colorRgbArray = new double[]{0, 0, 0};
                for (int k = 0; k < window.length; k++) {
                    int l = x + k - (n - 1) / 2;
                    if (l >= 0 && l < width) {
                        Color imageColor = new Color(image.getRGB(l, y));
                        colorRgbArray[0] = colorRgbArray[0] + imageColor.getRed() * window[k];
                        colorRgbArray[1] = colorRgbArray[1] + imageColor.getGreen() * window[k];
                        colorRgbArray[2] = colorRgbArray[2] + imageColor.getBlue() * window[k];
                        sum += window[k];
                    }
                }
                for (int t = 0; t < 3; t++) {
                    colorRgbArray[t] = colorRgbArray[t] / sum;
                }
                Color tmpColor = new Color((int) colorRgbArray[0], (int) colorRgbArray[1], (int) colorRgbArray[2]);
                tempImage.setRGB(x, y, tmpColor.getRGB());
            }
        }

        //--->>
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double sum = 0;
                double[] colorRgbArray = new double[]{0, 0, 0};
                for (int k = 0; k < window.length; k++) {
                    int l = y + k - (n - 1) / 2;
                    if (l >= 0 && l < height) {
                        Color imageColor = new Color(tempImage.getRGB(x, l));
                        colorRgbArray[0] = colorRgbArray[0] + imageColor.getRed() * window[k];
                        colorRgbArray[1] = colorRgbArray[1] + imageColor.getGreen() * window[k];
                        colorRgbArray[2] = colorRgbArray[2] + imageColor.getBlue() * window[k];
                        sum += window[k];
                    }
                }
                for (int t = 0; t < 3; t++) {
                    colorRgbArray[t] = colorRgbArray[t] / sum;
                }
                Color tmpColor = new Color((int) colorRgbArray[0], (int) colorRgbArray[1], (int) colorRgbArray[2]);
                filteredImage.setRGB(x, y, tmpColor.getRGB());
            }
        }

        return filteredImage;
    }
}
