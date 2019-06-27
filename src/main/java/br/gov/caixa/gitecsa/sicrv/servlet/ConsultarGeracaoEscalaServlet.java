package br.gov.caixa.gitecsa.sicrv.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;

import com.google.gson.Gson;

import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Escala;
import br.gov.caixa.gitecsa.sicrv.service.AtividadeService;
import br.gov.caixa.gitecsa.sicrv.service.EscalaService;
import br.gov.caixa.gitecsa.sicrv.util.CoresUtil;

@WebServlet("/ConsultarGeracaoEscala")
public class ConsultarGeracaoEscalaServlet extends HttpServlet {

	private static final long serialVersionUID = -2602251948704898634L;

	@EJB
	private AtividadeService atividadeService;

	@EJB
	private EscalaService escalaService;

	public ConsultarGeracaoEscalaServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	private Date getSegundaFeiraDaSemana(Date dataInicio) {
		Calendar calHoje = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.truncate(dataInicio, Calendar.DAY_OF_MONTH));

		while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			cal.add(Calendar.DAY_OF_WEEK, -1);

			// Caso ficou uma data igual ou inferior a hoje retorna a propria data
			if (!cal.getTime().after(calHoje.getTime())) {
				return dataInicio;
			}
		}

		return cal.getTime();
	}

	private Date getDomingoDaSemana(Date dataFim) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataFim);

		while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			cal.add(Calendar.DAY_OF_WEEK, 1);
		}

		return cal.getTime();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			String idEquipeParam = request.getParameter("equipe");
			String dataInicioParam = request.getParameter("dataInicial");
			String dataFimParam = request.getParameter("dataFim");

			SimpleDateFormat americanFormat = new SimpleDateFormat("yyyy-MM-dd");

			Long idEquipe = Long.parseLong(idEquipeParam);
			Date dataInicio = americanFormat.parse(dataInicioParam);
			Date dataFim = americanFormat.parse(dataFimParam);

			List<Escala> escalaList = escalaService.obterEscalasPorEquipeEPeriodo(idEquipe, getSegundaFeiraDaSemana(dataInicio), getDomingoDaSemana(dataFim));

			// List<String> cores = getCoresFuncionariosList();

			// Lista dos Json das escalas
			List<Map<String, String>> mapaJsonList = new ArrayList<Map<String, String>>();
			// Relação Empregado x Cor
			HashMap<Long, String> empregadoCorMap = new HashMap<Long, String>();

			SimpleDateFormat fullCalendarFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

			Random random = new Random();

			// código inserido por André
			String[] listaCores = { "#588c7e", "#990000", "#ff0000", "#992600", "#ff4000", "#ffb399", "#ff9f80", "#ff8000", "#ffd9b3", "#ffbf00", "#ffe699", "#ffff00",
					"#ffff99", "#bfff00", "#ecffb3", "#00ff00", "#ecffb3", "#009900", "#00ff80", "#00ffff", "#00bfff", "#0080ff", "#0000ff", "#8000ff", "#e6ccff", "#350066",
					"#ecb3ff", "#bf00ff", "#4d0066", "#ffb3ff", "#ff00ff", "#660066", "#ffb3d9", "#ff0080", "#660033", "#DCDCDC", "#808080", "#000000", "#674d3c", "#a2836e",
					"#d9ad7c", "#fff2df", "#e0876a", "#f4a688", "#f9ccac", "#fbefcc", "#667292", "#8d9db6", "#bccad6", "#87bdd8", "#ff3333", "#ff6633", "#ff9933", "#ffcc33",
					"#ffff33", "#ccff33", "#99ff33", "#66ff33", "#33ff33", "#33ff66", "#33ff99", "#33ffcc", "#33ffff", "#3399ff", "#3366ff", "#3333ff", "#6633ff", "#9933ff",
					"#cc33ff", "#ff33ff", "#ff33cc", "#ff3399", "#ff3366", "#ff3333", "#ff0000", "#ff4000", "#ff8000", "#ffbf00", "#ffff00", "#bfff00", "#80ff00", "#40ff00",
					"#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff", "#0040ff", "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf",
					"#ff0080", "#ff0040", "#ff0000" };
			Hashtable coresSelecionadas = new Hashtable();

			for (Escala escala : escalaList) {
				Empregado empregado = escala.getEquipeEmpregado().getEmpregado();
				if (!empregadoCorMap.containsKey(empregado.getId())) {

					// Integer r = random.nextInt(255);
					// Integer g = random.nextInt(255);
					// Integer b = random.nextInt(255);
					// String cor = "#"+Integer.toHexString(r)+Integer.toHexString(g)+Integer.toHexString(b);

					String corSorteada = listaCores[random.nextInt(98)];
					while (coresSelecionadas.containsKey(corSorteada) && coresSelecionadas.size() < 95) {
						corSorteada = listaCores[random.nextInt(98)];
					}
					coresSelecionadas.put(corSorteada, "");
					empregadoCorMap.put(empregado.getId(), corSorteada);
					if (coresSelecionadas.size() > 95) {
						coresSelecionadas = new Hashtable();
					}
				}

				Map<String, String> eventoJsonMap = new HashMap<String, String>();
				eventoJsonMap.put("id", escala.getId().toString());
				eventoJsonMap.put("title", empregado.getNomeAbreviado().trim());
				eventoJsonMap.put("start", fullCalendarFormat.format(escala.getInicio()));
				eventoJsonMap.put("end", fullCalendarFormat.format(escala.getFim()));
				eventoJsonMap.put("borderColor", empregadoCorMap.get(empregado.getId()));
				eventoJsonMap.put("backgroundColor", "#F5F5F5");
				eventoJsonMap.put("textColor", "#000000");
				eventoJsonMap.put("className", "celulaEscala");

				mapaJsonList.add(eventoJsonMap);
			}

			Gson gson = new Gson();
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();

			out.print(gson.toJson(mapaJsonList));
			out.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	private List<String> getCoresFuncionariosList() {
		List<String> cores = new ArrayList<String>();
		// cores.add(CoresUtil.BEIGE);
		cores.add(CoresUtil.BLUE);
		cores.add(CoresUtil.GRAY);
		cores.add(CoresUtil.GREEN);

		cores.add(CoresUtil.ORANGE);
		cores.add(CoresUtil.PINK);
		cores.add(CoresUtil.PURPLE);
		cores.add(CoresUtil.RED);

		cores.add(CoresUtil.TURQUOISE);
		cores.add(CoresUtil.WHITE);
		cores.add(CoresUtil.YELLOW);
		return cores;
	}

}
