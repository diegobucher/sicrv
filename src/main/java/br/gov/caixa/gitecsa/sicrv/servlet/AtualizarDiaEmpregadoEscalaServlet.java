package br.gov.caixa.gitecsa.sicrv.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.sicrv.model.dto.folga.EventoFullCalendarDTO;
import br.gov.caixa.gitecsa.sicrv.service.EscalaService;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

@WebServlet("/AtualizarDiaEmpregadoEscala")
public class AtualizarDiaEmpregadoEscalaServlet extends HttpServlet {

    private static final long serialVersionUID = -2602251948704898634L;

    @EJB
    private EscalaService escalaService;

    public AtualizarDiaEmpregadoEscalaServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        try {
            // Request
            String eventoJson = request.getParameter("eventoJson");
            SimpleDateFormat sdfAmericano = new SimpleDateFormat("yyyy-MM-dd");
            Date dataInicial = sdfAmericano.parse(request.getParameter("dataInicial"));
            Date dataFim = sdfAmericano.parse(request.getParameter("dataFim"));

            EventoFullCalendarDTO evento = gson.fromJson(eventoJson, EventoFullCalendarDTO.class);

            // Response
            JsonObject retornoJson = new JsonObject();

            try {
                escalaService.alterarDiaEscala(evento, dataInicial, dataFim);

                retornoJson.addProperty("erro", "false");
                retornoJson.addProperty("mensagem", "Registro alterado com sucesso!");
            } catch (BusinessException be) {
                retornoJson.addProperty("erro", "true");
                String preMsg = "ATENÇÃO! Ocorreu um erro! \n";
                retornoJson.addProperty("mensagem", preMsg + be.getMessage());
            }

            response.setContentType("application/json");
            response.getWriter().write(retornoJson.toString());

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
