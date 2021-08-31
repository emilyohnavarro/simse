// ImageLoader is a utility class that fetches images and returns them given a
// URL

package simse.modelbuilder.mapeditor;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ImageLoader {
  public static Image getImageFromZippedURL(String zipURL,
      String relativeImageURL) {
    try {
      ZipInputStream zis = new ZipInputStream(ImageLoader.class
          .getResourceAsStream(zipURL));
      while (zis.available() == 1) {
        ZipEntry ze = zis.getNextEntry();
        if (ze == null)
          break;
        if (!ze.getName().equals(relativeImageURL))
          continue;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buf[] = new byte[2048];
        while (true) {
          int len = zis.read(buf, 0, buf.length);
          if (len != -1)
            baos.write(buf, 0, len);
          else {
            zis.close();
            baos.flush();
            baos.close();
            //System.err.println(imgPath);
            return Toolkit.getDefaultToolkit().createImage(baos.toByteArray());
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void copyImagesToDir(String dir) {
    try {
      ZipInputStream zis = new ZipInputStream(ImageLoader.class
          .getResourceAsStream("res/images.zip"));
      //ZipFile zf = new ZipFile(new File(url.getPath()));
      //Enumeration en = zf.entries();

      String tmp = "images\\"; //(zf.getEntry("images") == null) ? "images\\" :
      // "";
      dir += tmp;

      // makes the images directory if non-existent
      if (tmp.length() > 0)
        new File(dir).mkdir();

      while (true) {
        ZipEntry ze = zis.getNextEntry();
        if (ze == null) {
          break;
        }

        if (ze.isDirectory()) {
          new File(dir + ze.getName()).mkdir();
          continue;
        }

        // makes the temp files in the images directory if images dir is non-
        // existent:
        if (tmp.length() > 0)
          new File(dir + ze.getName()).createNewFile();

        byte[] buffer = new byte[1024];
        int len = 1024;
        BufferedOutputStream out = new BufferedOutputStream(
            new FileOutputStream(dir + ze.getName()));

        while ((len = zis.read(buffer, 0, len)) >= 0) {
          out.write(buffer, 0, len);
        }
        out.close();

        zis.closeEntry();
      }

      zis.close();
    } catch (IOException ioe) {
      System.out.println("IOE");
      ioe.printStackTrace();
    }
  }

  public static Image getImageFromURL(String url) {
    URL imgURL = ImageLoader.class.getResource(url);
    Image img = Toolkit.getDefaultToolkit().getImage(imgURL);
    return (img);
  }
}