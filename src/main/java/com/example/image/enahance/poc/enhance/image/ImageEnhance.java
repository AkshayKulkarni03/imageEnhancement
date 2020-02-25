package com.example.image.enahance.poc.enhance.image;

import static org.apache.tika.metadata.TikaMetadataKeys.RESOURCE_NAME_KEY;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ij.IJ;
import nu.pattern.OpenCV;

/**
 * Image conversion module to enhance the image quality by different methods like
 * <ul>
 * <li>Detecting faces in an image and mask the area with face and then convert image to grayscale and convert to TIFF
 * for output.
 * 
 * <pre>
 * To perform this operation {@link OpenCV} libraries are used and face detection library xml file is loaded to detect
 * the face from an image
 * 
 * </li>
 * <li>Convert image directly to grayscale wile detecting color in the image to convert to grayscale and generate output
 * tiff file
 * 
 * <pre>
 * Tho perform this operation {@link IJ} libraries are used to detect color and turn image to grayscale</li>
 * </ul>
 * 
 * @author akshay
 *
 */
public class ImageEnhance {

  private static final Logger LOG = LoggerFactory.getLogger(ImageEnhance.class);

  private static String frontalFaceConfPath;
  private static final String[] FILE_TYPES =
      {"jpeg", "tif", "bmp", "JPEG", "tiff", "jpg", "JPG", "TIF", "TIFF", "BMP", "png"};

  static {
    frontalFaceConfPath = getFrontalFaceDetectionFilePath();

    OpenCV.loadShared();
    OpenCV.loadLocally();
  }

  /**
   * Main method to run the utility. This method requires the arguments to be passed while running below is the input
   * required.
   * <ol>
   * <li>Input image file with full path of the file which can be accessed to <code>read/write</code> on file
   * system</li>
   * <li>To use the face detection functionality (this is optional parameter) values accepted are
   * <code>true</code>/<code>false</code></li>
   * </ol>
   * <h3>Typical parameters will look like</h3>
   * <ol>
   * <li>C:\LogFiles\CMT\samplePassport.jpg</li>
   * <li><code>true</code></li>
   * </ol>
   * 
   * @param args arguments to be passed to run this method. input image file path and second parameter as true or false
   *        to perform face detection
   */
  public static void main(String[] args) {
    try {
      if (!ArrayUtils.isEmpty(args)) {
        File sourceImageFile = new File(args[0]);

        LOG.info("Image conversion for input file {} is started", sourceImageFile.getAbsolutePath());

        String extension = loadExtension(sourceImageFile);
        if (StringUtils.containsAny(extension, FILE_TYPES)) {
          if (ArrayUtils.isArrayIndexValid(args, 1) && StringUtils.equalsIgnoreCase(args[1], "true")) {
            LOG.info("Detecting face(s) in an image and masking them and converting to grayscale tiff file");
            DetectFaceFromImage detectFaceFromImage = DetectFaceFromImage.getInstaceOfDetectFaceFromImage();
            String outputFile = detectFaceFromImage.detectFaceInFIle(frontalFaceConfPath, sourceImageFile);
            LOG.info("Image saved with name {}", outputFile);
          } else {
            LOG.info("Converting image to grayscale tiff file");
            ImageToBinary imageToBinary = ImageToBinary.getInstaceOfDetectFaceFromImage();
            String outputFile = imageToBinary.makeBinaryImage(sourceImageFile);
            LOG.info("Image saved with name {}", outputFile);
          }
        } else {
          throw new UnsupportedOperationException(
              String.format("File format \"%s\" is not supported for image enhacement", extension));
        }
      } else {
        throw new IllegalArgumentException(
            "Argsuments can not be empty need to provide the input image path as first argument to convert the image and if face detection required second argumnent is required.");
      }
    } catch (MimeTypeException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Load the front face detection algorithm pattern from OpenCV based on these tree parameters face detection algorithm
   * will be set.
   * 
   * @return file path of tree xml file
   */
  private static String getFrontalFaceDetectionFilePath() {
    try (InputStream inputStream =
        ImageEnhance.class.getClassLoader().getResourceAsStream("haarcascade_frontalface_alt_tree.xml")) {
      File tempFile = File.createTempFile("haarcascade_frontalface_alt_tree.xml", "");
      FileUtils.copyInputStreamToFile(inputStream, tempFile);
      return tempFile.getAbsolutePath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Detect the extension of input file, supported file formats are {@value #FILE_TYPES} The format is detected with
   * help of {@link Tika} libraries
   * 
   * @param inputFile input file for which format needs to be detected.
   * @return extension of the file for that format.
   * @throws IOException in case of not able to read or process the file
   * @throws MimeTypeException wrong mime type detected
   */
  private static String loadExtension(File inputFile) throws IOException, MimeTypeException {
    String extension = "";
    try (InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile))) {
      Metadata metadata = new Metadata();
      metadata.add(RESOURCE_NAME_KEY, inputFile.getName());
      MimeType mimeType = MimeTypes.getDefaultMimeTypes()
          .forName(TikaConfig.getDefaultConfig().getMimeRepository().detect(inputStream, metadata).toString());

      extension = RegExUtils.removeFirst(mimeType.getExtension(), "\\.");
    }
    return extension;
  }
}
