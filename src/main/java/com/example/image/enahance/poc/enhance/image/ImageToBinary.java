package com.example.image.enahance.poc.enhance.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FilenameUtils;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * Convert image to grayscale binary image directly.
 * 
 * @author akshay
 *
 */
public class ImageToBinary {

  private static AtomicReference<ImageToBinary> instaceOfImageToBinary = new AtomicReference<>(new ImageToBinary());

  /**
   * 
   */
  private ImageToBinary() {
    super();
  }


  /**
   * Get singleton instance of the {@link ImageToBinary}
   * 
   * @return the instaceOfImageToBinary
   */
  public static ImageToBinary getInstaceOfDetectFaceFromImage() {
    if (instaceOfImageToBinary != null && instaceOfImageToBinary.get() != null) {
      return instaceOfImageToBinary.get();
    } else {
      instaceOfImageToBinary.set(new ImageToBinary());
      return instaceOfImageToBinary.get();
    }
  }

  /**
   * Convert image to binary with correct while detecting correct level of colors.
   * 
   * @param sourceImageFile source image file to be converted
   * @return output image with path where its saved.
   */
  public String makeBinaryImage(File sourceImageFile) {
    ImagePlus image = new ImagePlus(sourceImageFile.getAbsolutePath());
    ImageProcessor imgProcessor = image.getProcessor();

    BufferedImage bufferedImage = imgProcessor.getBufferedImage();
    for (int y = 0; y < bufferedImage.getHeight(); y++) {
      for (int x = 0; x < bufferedImage.getWidth(); x++) {
        Color color = new Color(bufferedImage.getRGB(x, y));
        int grayLevel = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
        int r = grayLevel;
        int g = grayLevel;
        int b = grayLevel;
        int rgb = (r << 16) | (g << 8) | b;
        bufferedImage.setRGB(x, y, rgb);
      }
    }
    ImagePlus grayImg = new ImagePlus("gray", bufferedImage);

    String inputImagePath = FilenameUtils.getFullPath(sourceImageFile.getAbsolutePath());
    String outputImagePath = FilenameUtils.concat(inputImagePath, "ENHANCED_" + sourceImageFile.getName());
    IJ.saveAsTiff(grayImg, outputImagePath);
    return outputImagePath;

  }
}
