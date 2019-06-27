package br.gov.caixa.gitecsa.arquitetura.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

  public static final String STRING_VAZIA = "";

  private static final String WILDCARD = "%";

  public static final String LETRAS_COM_ACENTUACAO = "ÁÀÃÂÄÉÈÊËÍÌÏÎÓÒÕÔÖÚÙÛÜÇÑÝŸáàãâäéèêëíìïîóòõôöúùûüçñýÿ";
  public static final String LETRAS_SEM_ACENTUACAO = "AAAAAEEEEIIIIOOOOOUUUUCNYYaaaaaeeeeiiiiooooouuuucnyy";
  
  public static final String EDITAR = "EDITAR";
  public static final String DETALHAR = "DETALHAR";
  public static final String ACAO = "ACAO";
  public static final String OBJETO = "OBJETO";
  
  public static Boolean isNull(Object objeto) {
    return (objeto == null) ? Boolean.TRUE : Boolean.FALSE;
  }

  public static Boolean isEmptyString(String str) {
    return (!Util.isNull(str) && str.length() == 0) ? Boolean.TRUE : Boolean.FALSE;
  }

  @SuppressWarnings("rawtypes")
  public static boolean isNullOuVazio(Object pObjeto) {

    if (pObjeto == null) {

      return true;

    } else if (pObjeto instanceof Collection) {

      return ((Collection) pObjeto).isEmpty();

    } else if (pObjeto instanceof String) {

      return ((String) pObjeto).trim().equals(STRING_VAZIA);

    } else if (pObjeto instanceof Integer) {

      return ((Integer) pObjeto).intValue() == 0;
    }

    return false;
  }

  public static boolean isNullOuVazio(Integer integer) {
    return integer == null;
  }

  public static String removeAcentos(String string) {
    return Normalizer.normalize(string.trim(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
  }

  public static String pesquisaPorNome(String nomeParametroPesquisa, String campoBanco) {
    StringBuilder hql = new StringBuilder();

    String nomeModificado = WILDCARD + removeAcentos(nomeParametroPesquisa.trim().toUpperCase()) + WILDCARD;

    hql.append(" AND TRANSLATE(UPPER(" + campoBanco + "), '" + LETRAS_COM_ACENTUACAO + "', '" + LETRAS_SEM_ACENTUACAO
        + "') like '" + nomeModificado + "'");

    return hql.toString();
  }

  public static String formatData(Date data, String pFormato) {
    SimpleDateFormat sdate = new SimpleDateFormat(pFormato);
    return sdate.format(data);
  }

  public static String formatDataHoraNomeArquivo(Date data) {
    SimpleDateFormat sdate = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
    return sdate.format(data);
  }

  public static String formatDataHora(Date data) {
    SimpleDateFormat sdate = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    return sdate.format(data);
  }

  public static String formatDataHoraCompleta(Date data) {
    SimpleDateFormat sdate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    return sdate.format(data);
  }
  
  /**
   * Método responsável por validar se a hora informada está entre 00:00 e 23:59
   * @param hora
   * @return
   */
  public static boolean horaValida(String hora) {
	  if (isNullOuVazio(hora)) {
		return false;
	  }
	  final Integer HORA_MINIMA = 0;
	  final Integer HORA_MAXIMA = 2359;
	  final Integer horaInt = Integer.valueOf(hora.replace(":", ""));
	
	  if (horaInt >= HORA_MINIMA && horaInt <= HORA_MAXIMA) {
		  return true;
	  }
	  return false; 
  }

  public static String lpad(String input, char padding, int length) {
    String output = "";

    if (input != null) {

      output = input;

    }

    if (output.length() >= length) {

      return output;

    } else {

      StringBuffer result = new StringBuffer();

      int numChars = length - output.length();

      for (int i = 0; i < numChars; i++) {

        result.append(padding);

      }

      result.append(output);

      return result.toString();

    }

  }

  public static String rpad(String input, String padding, int length) {

    String output = "";

    if (input != null) {
      output = input;
    }

    if (output.length() >= length) {
      return output.substring(0, length);
    } else {

      StringBuffer result = new StringBuffer();

      int numChars = length - output.length();
      result.append(output);

      for (int i = 0; i < numChars; i++) {
        result.append(padding);
      }

      return result.toString();

    }

  }

  public static String formataCPF(Long cpf) {
    String cpfString = lpad(Long.toString(cpf), '0', 11);

    if (cpfString != null) {

      // cpfString = cpfString.substring(0, 9) + "-" +
      // cpfString.substring(9, 11);
      cpfString =
          cpfString.substring(0, 3) + "." + cpfString.substring(3, 6) + "." + cpfString.substring(6, 9) + "-"
              + cpfString.substring(9, 11);

    }

    return cpfString;
  }

  public static String formataCnpj(Long pCnpj) {
    String cnpjString = lpad(Long.toString(pCnpj), '0', 14);

    if (!isNullOuVazio(cnpjString)) {
      cnpjString =
          cnpjString.substring(0, 2) + "." + cnpjString.substring(2, 5) + "." + cnpjString.substring(5, 8) + "/"
              + cnpjString.substring(8, 12) + "-" + cnpjString.substring(12, 14);
    }

    return cnpjString;
  }

  public static boolean validarNomePorExpressaoRegular(String pExpressaoRegular, String pNome) {
    Pattern pattern = Pattern.compile(pExpressaoRegular);

    Matcher matcher = pattern.matcher(pNome);

    if (matcher.find()) {
      return true;
    } else {
      return false;
    }
  }

  public static String formataCodigo(Integer codigo) {
    String codigoFormatado = lpad(Integer.toString(codigo), '0', 4);
    if (codigoFormatado != null) {
      return codigoFormatado;
    } else {
      return "";
    }

  }

  public static Boolean validaCNPJ(String cnpj) {

    if (cnpj == null || cnpj.length() != 14) {
      return false;
    }

    try {
      Long.parseLong(cnpj);
    } catch (NumberFormatException e) {
      return false;
    }

    int soma = 0;
    String cnpj_calc = cnpj.substring(0, 12);

    char chr_cnpj[] = cnpj.toCharArray();

    for (int i = 0; i < 4; i++)
      if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
        soma += (chr_cnpj[i] - 48) * (6 - (i + 1));

    for (int i = 0; i < 8; i++)
      if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9)
        soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));

    int dig = 11 - soma % 11;
    cnpj_calc =
        (new StringBuilder(String.valueOf(cnpj_calc))).append(dig != 10 && dig != 11 ? Integer.toString(dig) : "0").toString();
    soma = 0;

    for (int i = 0; i < 5; i++)
      if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
        soma += (chr_cnpj[i] - 48) * (7 - (i + 1));

    for (int i = 0; i < 8; i++)
      if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9)
        soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));

    dig = 11 - soma % 11;
    cnpj_calc =
        (new StringBuilder(String.valueOf(cnpj_calc))).append(dig != 10 && dig != 11 ? Integer.toString(dig) : "0").toString();

    if (!cnpj.equals(cnpj_calc) || cnpj.equals("00000000000000")) {
      return false;
    }

    return true;
  }

  public static String convertStringToMD5(String input) {

    String md5 = null;

    if (null == input)
      return null;

    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");

      digest.update(input.getBytes(), 0, input.length());

      md5 = new BigInteger(1, digest.digest()).toString(64);

    } catch (NoSuchAlgorithmException e) {

      e.printStackTrace();
    }
    return md5;
  }

  public static String formataLista(List<String> lista) {
    String r = "";
    if (lista.size() >= 1) {
      r += lista.get(0);
      if (lista.size() > 1) {
        for (int i = 1; i < (lista.size() - 1); i++) {
          r += ", " + lista.get(i);
        }
        r += " e " + lista.get(lista.size() - 1);
      }
    }
    return r;
  }

  public static String calculaTempoEntreDatas(Date d1, Date d2) {

    if (!isNullOuVazio(d1) && !isNullOuVazio(d2)) {

      long segundos = (d2.getTime() / 1000) - (d1.getTime() / 1000);
      long segundo = segundos % 60;
      long minutos = segundos / 60;
      long minuto = minutos % 60;
      long hora = minutos / 60;
      String hms = String.format("%02d:%02d:%02d", hora, minuto, segundo);
      return hms;

    }

    return "";
  }

  public static Date parseDate(String valor, String formato) {
    try {
      SimpleDateFormat formatoData = new SimpleDateFormat(formato);
      return formatoData.parse(valor);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Date parseDateEng(String valor) {
    try {
      DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
      return format.parse(valor);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String parseDateToString(Date vlr, String formato) {
    DateFormat format = new SimpleDateFormat(formato);
    return format.format(vlr);
  }

  public static String acrescentaZeroEsquerda(String valor, int tamanho) {
    while (valor.length() < tamanho) {
      valor = "0" + valor;
    }
    return valor;
  }

  public static Date getDataInicial(Date data) {

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(data);
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.getTime();
  }

  public static Date getDataFinal(Date data) {

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(data);
    calendar.set(Calendar.HOUR, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);

    return calendar.getTime();
  }

  /**
   * Valida CPF do usuario. Não aceita cpfs padrão, como 11111111111 ou 22222222222
   * 
   * @param cpf
   *          String valor com 11 dígitos
   */
  public static boolean validaCPF(String cpf) {
    if (cpf == null || cpf.length() != 11 || isCPFPadrao(cpf))
      return false;

    try {
      Long.parseLong(cpf);
    } catch (NumberFormatException e) { // CPF não possui somente n�meros
      return false;
    }

    if (!calcDigVerif(cpf.substring(0, 9)).equals(cpf.substring(9, 11)))
      return false;

    return true;
  }

  /**
   * 
   * @param cpf
   *          String valor a ser testado
   * @return boolean indicando se o usuário entrou com um CPF padrão
   */
  private static boolean isCPFPadrao(String cpf) {
    if (cpf.equals("00000000000") || cpf.equals("11111111111") || cpf.equals("22222222222") || cpf.equals("33333333333")
        || cpf.equals("44444444444") || cpf.equals("55555555555") || cpf.equals("66666666666") || cpf.equals("77777777777")
        || cpf.equals("88888888888") || cpf.equals("99999999999")) {

      return true;
    }

    return false;
  }

  private static String calcDigVerif(String num) {
    Integer primDig, segDig;
    int soma = 0, peso = 10;
    for (int i = 0; i < num.length(); i++)
      soma += Integer.parseInt(num.substring(i, i + 1)) * peso--;

    if (soma % 11 == 0 | soma % 11 == 1)
      primDig = new Integer(0);
    else
      primDig = new Integer(11 - (soma % 11));

    soma = 0;
    peso = 11;
    for (int i = 0; i < num.length(); i++)
      soma += Integer.parseInt(num.substring(i, i + 1)) * peso--;

    soma += primDig.intValue() * 2;
    if (soma % 11 == 0 | soma % 11 == 1)
      segDig = new Integer(0);
    else
      segDig = new Integer(11 - (soma % 11));

    return primDig.toString() + segDig.toString();
  }

  public static boolean validaDvCaixa(String conta, String dv) {

    int peso = 8, soma = 0;

    for (char c : conta.toCharArray()) {
      int n = Integer.parseInt(String.valueOf(c));
      soma += n * peso;
      peso--;
      if (peso < 2) {
        peso = 9;
      }
    }

    int modulo = (soma * 10) % 11;

    if (modulo == 10) {
      modulo = 0;
    }

    return (modulo == Integer.parseInt(dv)) ? Boolean.TRUE : Boolean.FALSE;
  }

  public static int calculaDiferencaEntreDatasEmDias(Date d1, Date d2) {
    int MILLIS_IN_DAY = 86400000;

    Calendar c1 = Calendar.getInstance();
    c1.setTime(d1);
    c1.set(Calendar.MILLISECOND, 0);
    c1.set(Calendar.SECOND, 0);
    c1.set(Calendar.MINUTE, 0);
    c1.set(Calendar.HOUR_OF_DAY, 0);

    Calendar c2 = Calendar.getInstance();
    c2.setTime(d2);
    c2.set(Calendar.MILLISECOND, 0);
    c2.set(Calendar.SECOND, 0);
    c2.set(Calendar.MINUTE, 0);
    c2.set(Calendar.HOUR_OF_DAY, 0);
    return (int) ((c1.getTimeInMillis() - c2.getTimeInMillis()) / MILLIS_IN_DAY);
  }

  public static boolean contemAcento(String text) {
    Pattern padrao = Pattern.compile("[a-z A-Z]*");
    Matcher pesquisa = padrao.matcher(text);
    return !pesquisa.matches();

  }

  public static String dataPorExtenso(Calendar data) {
    String[] meses =
        new String[] { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro",
            "Novembro", "Dezembro" };
    return data.get(Calendar.DAY_OF_MONTH) + " de " + meses[data.get(Calendar.MONTH)] + " de " + data.get(Calendar.YEAR);
  }

}