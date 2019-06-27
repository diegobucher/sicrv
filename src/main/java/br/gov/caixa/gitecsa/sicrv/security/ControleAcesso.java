package br.gov.caixa.gitecsa.sicrv.security;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Properties;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import br.gov.caixa.gitecsa.arquitetura.repository.ConfigurationRepository;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.ldap.AutenticadorLdap;
import br.gov.caixa.gitecsa.ldap.usuario.Credentials;
import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;
import br.gov.caixa.gitecsa.sicrv.enumerator.TipoUsuarioEnum;
import br.gov.caixa.gitecsa.sicrv.model.Empregado;
import br.gov.caixa.gitecsa.sicrv.model.Unidade;
import br.gov.caixa.gitecsa.sicrv.service.EmpregadoService;
import br.gov.caixa.gitecsa.sicrv.service.UnidadeService;
import br.gov.caixa.gitecsa.sicrv.util.Constantes;

@Named
@SessionScoped
public class ControleAcesso implements Serializable {

	private static final long serialVersionUID = 7444956049460916877L;

	@Inject
	@ConfigurationRepository("configuracoes.properties")
	private Properties configuracoes;

	// @Inject
	// private static Logger logger = Logger.getLogger(ControleAcesso.class);

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private EmpregadoService empregadoService;

	private Unidade unidade;

	private AutenticadorLdap autenticador;

	private UsuarioLdap usuario;

	private Credentials credentials;

	private TipoUsuarioEnum tipoUsuario;

	public ControleAcesso() {
		credentials = new Credentials();
		getDiaDaSemana();
		autenticador = new AutenticadorLdap();
	}

	public String autenticarLdap() {

		if (validarCampos()) {
			// try {
			// usuario = autenticador.autenticaUsuarioLdapCaixa(credentials, configuracoes.getProperty(Constantes.URL_LDAP),
			// configuracoes.getProperty(Constantes.NOME_SISTEMA), configuracoes.getProperty(Constantes.SECURITY_DOMAIN));

			usuario = new UsuarioLdap();
			usuario.setNuMatricula("c048717");
			usuario.setCoUnidade(7067);
			usuario.setNoUnidade("CESET - Centralizadora Nacional de Segurança e Continuidade de Negócios");
			usuario.setNomeUsuario("Augustinho");
			usuario.setSobreNomeUsuario("Willemann");

			if (!Util.isNullOuVazio(usuario) && !Util.isNullOuVazio(usuario.getNuMatricula())) {

				// TODO VERIFICAR PQ ESTA FIXO COMO ADM
				tipoUsuario = TipoUsuarioEnum.GERAL;

				// Tratamento para usuario que não possua Unidade cadastrada na base.
				Unidade unidade = unidadeService.buscarPorCodigo(usuario.getCoUnidade());

				if (unidade != null) {
					setUnidade(unidade);
				} else {
					JSFUtil.addErrorMessage("O usuário não possui unidade cadastrada no sistema");
					// limparCampos();
					return "login";
				}

				// Verificar se é supervisor
				Empregado empregado = empregadoService.obterEmpregadoPorMatricula(usuario.getNuMatricula());
				Boolean isSupervisor = empregadoService.isEmpregadoSupervisorEmEquipeAtiva(empregado);

				if (Boolean.TRUE.equals(isSupervisor)) {
					this.tipoUsuario = TipoUsuarioEnum.SUPERVISOR;
				}

				JSFUtil.setSessionMapValue("loggedUser", credentials.getLogin());
				JSFUtil.setSessionMapValue("loggedMatricula", getUsuario().getNuMatricula());
				JSFUtil.setSessionMapValue("usuario", getUsuario());
				JSFUtil.setSessionMapValue("usuario_unidade", getUsuario().getCoUnidade());
				JSFUtil.setSessionMapValue("usuario_nome_unidade", getUsuario().getNoUnidade());

				return redirectHome();
			}

			// } else {
			// throw new AppException(LogUtil.getMensagemPadraoLog("NÃO FOI POSSÍVEL OBTER O USUÁRIO DO LDAP", "ControleAcesso", "Login", credentials.getLogin()));
			// }

			// } catch (CommunicationException e) {
			//
			// String nomeLdap = configuracoes.getProperty(Constantes.URL_LDAP);
			//
			// String ipLdap = nomeLdap.substring(nomeLdap.indexOf("//") + 2);
			// String portaLdap = ipLdap.substring(ipLdap.indexOf(":") + 1);
			// ipLdap = ipLdap.substring(0, ipLdap.indexOf(":"));
			//
			// String mensagem = "ERRO - NAO FOI POSSIVEL CONECTAR O SERVIDOR LDAP - DADOS:" + "IP_SERVIDOR LDAP: " + ipLdap + " PORTA DO SERVIDOR LDAP: " + portaLdap;
			//
			// String mensagemLog = LogUtil.getMensagemPadraoLog(mensagem, "ControleAcesso", "Login", credentials.getLogin());
			// logger.error(mensagemLog);
			//
			// JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.message.erro.desconhecido"));
			// limparCampos();
			// return "login";
			//
			// } catch (FailedLoginException f) {
			//
			// String mensagemLog = LogUtil.getMensagemPadraoLog("FALHA DE AUTENTICAÇÃO - USUARIO OU SENHA INVALIDOS", "ControleAcesso", "Login", credentials.getLogin());
			// logger.error(mensagemLog);
			// limparCampos();
			//
			// JSFUtil.addErrorMessage("Usuário e/ou Senha inválidos.");
			// return "login";
			//
			// } catch (AuthenticationException e) {
			//
			// String mensagemLog = LogUtil.getMensagemPadraoLog("FALHA DE AUTENTICAÇÃO - USUARIO OU SENHA INVALIDOS", "ControleAcesso", "Login", credentials.getLogin());
			// logger.error(mensagemLog);
			//
			// JSFUtil.addErrorMessage("A senha ou usuário informados são inválidos. Por favor, verifique se houve erros de digitação.");
			// limparCampos();
			// return "login";
			//
			// } catch (AppException e) {
			// String mensagemLog = LogUtil.getMensagemPadraoLog("FALHA DE AUTENTICAÇÃO", "ControleAcesso", "Login", credentials.getLogin());
			// logger.error(mensagemLog);
			//
			// JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.message.erro.desconhecido"));
			// limparCampos();
			// return "login";
			//
			// } catch (Exception e) {
			//
			// String mensagemLog = LogUtil.getMensagemPadraoLog("FALHA DE AUTENTICAÇÃO", "ControleAcesso", "Login", credentials.getLogin());
			// logger.error(mensagemLog);
			//
			// limparCampos();
			// JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.message.erro.desconhecido"));
			// return "login";
			// }
		}

		return "login";
	}

	public UsuarioLdap buscarUsuarioLDAP(String matricula) {

		if (!Util.isNullOuVazio(matricula)) {
			try {
				return autenticador.pesquisarForaDoGrupoDoLdap(matricula.toUpperCase(), configuracoes.getProperty(Constantes.URL_LDAP));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String logout() {

		limparSessao();
		limparCampos();

		return "/login.xhtml?faces-redirect=true";
	}

	public boolean validarCampos() {

		if (Util.isNullOuVazio(credentials) || Util.isNullOuVazio(credentials.getLogin()) || Util.isNullOuVazio(credentials.getSenha())) {

			if (Util.isNullOuVazio(credentials.getLogin())) {
				JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.message.validation.dados.obrigatorios", MensagemUtil.obterMensagem("login.label.usuario")));
			} else if (Util.isNullOuVazio(credentials.getSenha())) {
				JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.message.validation.dados.obrigatorios", MensagemUtil.obterMensagem("login.label.senha")));
			}

			if (!Util.isNullOuVazio(credentials.getSenha())) {
				credentials.setSenha("");
			}
			return false;

		} else {
			return true;
		}
	}

	public void limparCampos() {
		if (FacesContext.getCurrentInstance().getMessageList() == null || FacesContext.getCurrentInstance().getMessageList().size() == 0) {
			usuario = new UsuarioLdap();
			credentials = new Credentials();
		}
	}

	public void limparSessao() {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		response.resetBuffer();

		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	}

	public String redirectHome() {
		return "/home.xhtml?faces-redirect=true";
	}

	private void getDiaDaSemana() {
		JSFUtil.setSessionMapValue("dataDiaExtenso", Util.formatData(Calendar.getInstance().getTime(), "EEEE, dd/MM/yyyy HH:mm"));
	}

	public UsuarioLdap getUsuario() {
		return usuario;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public TipoUsuarioEnum getTipoUsuario() {
		return tipoUsuario;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

}
