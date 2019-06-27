package br.gov.caixa.gitecsa.arquitetura.util;

import java.util.Map;

import javax.persistence.Query;

import org.primefaces.model.SortOrder;

public class QueryUtils {

  /**
   * Recebe um query e seta os query parameter passados por parametro no Map
   * 
   * @param query
   *          Query que tera os parametros setados
   * @param parametros
   *          Parametros a serem setados
   */
  public static void addParameters(Query query, Map<String, Object> parametros) {
    if (!Util.isNullOuVazio(parametros) && !Util.isNullOuVazio(query)) {
      for (String key : parametros.keySet()) {
        query.setParameter(key, parametros.get(key));
      }
    }
  }

  /**
   * Método para geração do HQL de Order By
   * 
   * @param sortField
   *          Campo a ser ordenado
   * @param sortOrder
   *          Enum do tipo de ordenação
   * @param alias
   *          Alias da tabela
   * @param defaulSortField
   *          Campo de ordenação padrão caso não tenha sortField
   * @return String HQL do ORDER BY
   */
  public static String addOrder(String sortField, SortOrder sortOrder, String alias,  String defaulSortField) {

    StringBuilder hql = new StringBuilder();

    if (sortField == null) {
      sortField = defaulSortField;
    }

    hql.append(" ORDER BY "+alias+"." + sortField + " ");

    if (sortOrder != null) {
      switch (sortOrder) {
      case ASCENDING:
        hql.append("ASC");
        break;
      case DESCENDING:
        hql.append("DESC");
        break;
      default:
        break;
      }
    }

    return hql.toString();
  }
}
