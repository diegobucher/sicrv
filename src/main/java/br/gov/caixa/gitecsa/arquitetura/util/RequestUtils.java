package br.gov.caixa.gitecsa.arquitetura.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;

public class RequestUtils {

  public static StreamedContent download(File file, String filename) throws BusinessException {
    try {
      return new DefaultStreamedContent(new FileInputStream(file), FileUtils.getMimeType(file), filename);
    } catch (FileNotFoundException e) {
      throw new BusinessException(MensagemUtil.obterMensagem("MI028"));
    } catch (IOException e) {
      throw new BusinessException(MensagemUtil.obterMensagem("MI028"));
    }
  }
}
