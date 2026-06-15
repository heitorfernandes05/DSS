package projeto.ui.Aplicacao;

import java.time.Duration;
import java.util.List;

import projeto.business.ILNFacade;
import projeto.business.SSAdministracao.Restaurante;

public class AppControlador {
    private final ILNFacade lnFacade;

    public AppControlador(ILNFacade facade) {
        this.lnFacade = facade;
    }

    public void autenticar(String email, String password) throws IllegalArgumentException {
        lnFacade.autenticar(email, password);
    }

    public String getCargoUtilizador() {
        return lnFacade.getCargoUtilizador();
    }

    public void terminarSessao() {
        lnFacade.terminarSessao();
    }

    public List<Restaurante> getListaRestaurantes() {
        return lnFacade.getRestaurantesAcessiveis();
    }

    public Duration getTempoMedioAtendimento(String idRest) {
        return lnFacade.getTempoMedioAtendimento(idRest);
    }

    public double getFaturacao(String idRest) {
        return lnFacade.getFaturacao(idRest);
    }

    public void enviarMensagem(String idRestaurante, String mensagem) {
        lnFacade.enviarMensagem(idRestaurante, mensagem);
    }
}
