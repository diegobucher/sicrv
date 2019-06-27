package br.gov.caixa.gitecsa.arquitetura.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;

import org.apache.commons.lang.StringUtils;

public class FileUtils {

  public static String getMimeType(File file) throws IOException {
    FileInputStream stream = new FileInputStream(file);
    String mime = URLConnection.guessContentTypeFromStream(stream);

    if (StringUtils.isBlank(mime)) {
      mime = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
    }

    return mime;
  }

}
