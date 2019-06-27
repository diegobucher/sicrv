package br.gov.caixa.gitecsa.arquitetura.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class DateUtil {

    public static final String TIME_FORMAT = "HH:mm";

    public static String format(Date date, String format) {
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.format(date);
        }

        return StringUtils.EMPTY;
    }
}
