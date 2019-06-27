package br.gov.caixa.gitecsa.sicrv.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.gov.caixa.gitecsa.sicrv.model.dto.RelatorioTaxaOcupacaoDTO;
import br.gov.caixa.gitecsa.sicrv.service.EscalaService;
import br.gov.caixa.gitecsa.sicrv.service.RelatorioTaxaOcupacaoService;

import com.google.gson.Gson;

@WebServlet("/ConsultaTaxaOcupacaoGrafico")
public class ConsultaTaxaOcupacaoGraficoServlet extends HttpServlet {

    private static final long serialVersionUID = -2602251948704898634L;

    @EJB
    private EscalaService escalaService;

    @EJB
    private RelatorioTaxaOcupacaoService relatorioTaxaOcupacaoService;

    public ConsultaTaxaOcupacaoGraficoServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        // Request
        String dataInicioParam = request.getParameter("dataInicio");
        String dataFimParam = request.getParameter("dataFim");
        String idEquipeParam = request.getParameter("idEquipe");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date dataInicio;
        Date dataFim;

        try {
            dataInicio = sdf.parse(dataInicioParam);
            dataFim = sdf.parse(dataFimParam);
            Long idEquipe = Long.parseLong(idEquipeParam);

            RelatorioTaxaOcupacaoDTO taxaOcupacao =
                    relatorioTaxaOcupacaoService.getRelatorioTaxaOcupacao(idEquipe, dataInicio, dataFim);

            // Response
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(taxaOcupacao));
        } catch (ParseException e) {
        }

    }
}
