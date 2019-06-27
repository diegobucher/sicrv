package br.gov.caixa.gitecsa.sicrv.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.gov.caixa.gitecsa.sicrv.model.Atividade;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;
import br.gov.caixa.gitecsa.sicrv.model.dto.AtividadeDTO;
import br.gov.caixa.gitecsa.sicrv.model.dto.SubAtividadeDTO;
import br.gov.caixa.gitecsa.sicrv.service.AtividadeService;

import com.google.gson.Gson;

@WebServlet("/ConsultarAtividade")
public class ConsultarAtividadeServlet extends HttpServlet {

  private static final long serialVersionUID = -2602251948704898634L;
  
  @EJB
  private AtividadeService atividadeService;

  public ConsultarAtividadeServlet() {
    super();
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    String nomeAtividade = request.getParameter("nomeAtividade");
    Long idUnidade = Long.parseLong(request.getParameter("idUnidade"));
    
    HashMap<String, Object> mapaJson = new HashMap<String, Object>();

    try {
      Atividade atividadeFiltro = new Atividade();
      atividadeFiltro.setNome(nomeAtividade);

      Unidade unidade = new Unidade();
      unidade.setId(idUnidade);
      atividadeFiltro.setUnidade(unidade);
      
      List<Atividade> atividadeList = atividadeService.consultar(atividadeFiltro);
      List<AtividadeDTO> atividadeDTOList = new ArrayList<AtividadeDTO>();
      
      for (Atividade atividade : atividadeList) {
        AtividadeDTO atividadeDTO = new AtividadeDTO();
        atividadeDTO.setId(atividade.getId());
        atividadeDTO.setNome(atividade.getNome());
        atividadeDTO.setPeriodicidade(atividade.getPeriodicidade().getDescricao());
        
        List<SubAtividadeDTO> subAtividadeDTOList = new ArrayList<SubAtividadeDTO>();
        
        for (Atividade subAtividade : atividade.getAtividadeList()) {
          if(subAtividade.getAtivo()){
            SubAtividadeDTO subAtividadeDTO = new SubAtividadeDTO();
            subAtividadeDTO.setId(subAtividade.getId());
            subAtividadeDTO.setNome(subAtividade.getNome());
            subAtividadeDTO.setPrioridade(subAtividade.getPrioridade());
            subAtividadeDTOList.add(subAtividadeDTO);
          }
        }
        Collections.sort(subAtividadeDTOList);
        if(!subAtividadeDTOList.isEmpty()){
          atividadeDTO.setTemFilhos(true);
        }
        atividadeDTO.setListaSubAtividade(subAtividadeDTOList);
        
        atividadeDTOList.add(atividadeDTO);
      }

      Gson gson = new Gson();
      response.setContentType("application/json");
      PrintWriter out = response.getWriter();
      
      mapaJson.put("data", atividadeDTOList);
      out.print(gson.toJson(mapaJson));
      out.flush();
      
//    } catch (BusinessException e) {
    } catch (Exception e) {
    	e.printStackTrace();
    }
  }

}
