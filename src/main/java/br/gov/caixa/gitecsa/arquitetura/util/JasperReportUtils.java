package br.gov.caixa.gitecsa.arquitetura.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.primefaces.model.StreamedContent;


public class JasperReportUtils {
    
  public static StreamedContent run(final InputStream jasper, final String filename, final Map<String, Object> parameters, List<?> dataSource) throws JRException {
    try {
      JRDataSource ds = null;
      if (dataSource != null && !dataSource.isEmpty()){
        ds = new JRBeanCollectionDataSource(dataSource);
      } else {
        ds = new JREmptyDataSource();
      }

      File report = new File(filename);
      byte[] out = JasperRunManager.runReportToPdf(jasper, parameters, ds);
      
      FileOutputStream outputStream = new FileOutputStream(report);
      outputStream.write(out);
      outputStream.close();
      
      return RequestUtils.download(report, filename);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return null;
  }
}
