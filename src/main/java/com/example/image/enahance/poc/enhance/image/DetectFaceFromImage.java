package com.example.image.enahance.poc.enhance.image;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 * Detect face in the image and mask only the rectangular area of face. This is singleton class for better reference and
 * usage.
 * 
 * @author akshay
 *
 */
public class DetectFaceFromImage {

  private static AtomicReference<DetectFaceFromImage> instaceOfDetectFaceFromImage =
      new AtomicReference<>(new DetectFaceFromImage());


  /**
   * 
   */
  private DetectFaceFromImage() {
    super();
  }


  /**
   * Get singleton instance of the {@link DetectFaceFromImage}
   * 
   * @return the instaceOfDetectFaceFromImage
   */
  public static DetectFaceFromImage getInstaceOfDetectFaceFromImage() {
    if (instaceOfDetectFaceFromImage != null && instaceOfDetectFaceFromImage.get() != null) {
      return instaceOfDetectFaceFromImage.get();
    } else {
      instaceOfDetectFaceFromImage.set(new DetectFaceFromImage());
      return instaceOfDetectFaceFromImage.get();
    }
  }

  /**
   * Detect faces in input file
   * 
   * @param frontalFaceConfPath configuration file path of face tree detection
   * @param sourceImageFile source image file
   * @return newly created output file path
   */
  public String detectFaceInFIle(String frontalFaceConfPath, File sourceImageFile) {
    CascadeClassifier faceClassifier = new CascadeClassifier();
    faceClassifier.load(frontalFaceConfPath);
    Mat image = Imgcodecs.imread(sourceImageFile.getAbsolutePath());

    // Detecting faces
    MatOfRect faceDetections = new MatOfRect();
    faceClassifier.detectMultiScale(image, faceDetections);
    Mat rotatedImage = new Mat();
    if (ArrayUtils.isEmpty(faceDetections.toArray())) {
      Integer count = 0;
      while (count < 4) {
        rotatedImage = image;
        rotateMatCW(rotatedImage, rotatedImage, 90);
        faceClassifier.detectMultiScale(rotatedImage, faceDetections);
        count++;
        if (!ArrayUtils.isEmpty(faceDetections.toArray())) {
          break;
        }
      }
    } else {
      rotatedImage = image;
    }

    // Creating a rectangular box showing faces detected
    for (Rect rect : faceDetections.toArray()) {
      Imgproc.rectangle(rotatedImage, new Point((rect.x - 30), (rect.y - 50)),
          new Point((rect.x + 20 + rect.width), (rect.y + 40 + rect.height)), Scalar.all(0), -1);
    }
    Mat enhancedImage = new Mat(rotatedImage.rows(), rotatedImage.cols(), rotatedImage.type());
    Imgproc.GaussianBlur(rotatedImage, enhancedImage, new Size(0, 0), 5);
    Core.addWeighted(rotatedImage, 1.5, enhancedImage, -0.5, 0, enhancedImage);
    Mat grayScaleImg = new Mat();
    Imgproc.cvtColor(enhancedImage, grayScaleImg, Imgproc.COLOR_RGB2GRAY);

    // Saving the output image
    String inputImagePath = FilenameUtils.getFullPath(sourceImageFile.getAbsolutePath());
    String outputImagePath = FilenameUtils.concat(inputImagePath,
        "ENHANCED_" + FilenameUtils.getBaseName(sourceImageFile.getName()) + ".tif");
    Imgcodecs.imwrite(outputImagePath, grayScaleImg);
    return outputImagePath;
  }

  /**
   * Rotate the image 360 degree and try to match the face in correct position and return the image in that rotation
   * 
   * @param srcImage source image matrix to be rotated
   * @param dest destination image matrix created after rotation
   * @param deg degree by which image needs to be rotated
   */
  private void rotateMatCW(final Mat srcImage, final Mat dest, double deg) {
    if (deg == 270 || deg == -90) {
      // Rotate clockwise 270 degrees
      Core.transpose(srcImage, dest);
      Core.flip(dest, srcImage, 0);
    } else if (deg == 180 || deg == -180) {
      Core.flip(srcImage, dest, -1);
    } else if (deg == 90 || deg == -270) {
      // Rotate clockwise 90 degrees
      Core.transpose(srcImage, dest);
      Core.flip(dest, srcImage, 1);
    } else if (deg == 360 || deg == 0 || deg == -360) {
      if (srcImage.dataAddr() != dest.dataAddr()) {
        srcImage.copyTo(dest);
      }
    } else {
      Point point = new Point(srcImage.cols() / 2.0F, srcImage.rows() / 2.0F);
      Mat rotatatedImage = Imgproc.getRotationMatrix2D(point, 360 - deg, 1.0);
      Imgproc.warpAffine(srcImage, dest, rotatatedImage, srcImage.size());
    }
  }


}
