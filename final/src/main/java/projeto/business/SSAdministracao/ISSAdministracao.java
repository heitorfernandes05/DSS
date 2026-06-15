package projeto.business.SSAdministracao;

import java.time.Duration;
import java.util.List;

public interface ISSAdministracao {
    public void autenticar(String email, String password) throws IllegalArgumentException;
    public void terminarSessao();
    public String getCargoUtilizador();
    public List<Restaurante> getRestaurantesAcessiveis();
    public List<Restaurante> getAllRestaurantes();

    public Duration getTempoMedioAtendimento(String idRest);

    public double getFaturacao(String idRest);

    public void atualizarIndicadores(String idRest, double valorPedido, Duration tempoPedido);

    public void enviarMensagem(String idRest, String conteudo);

    public List<Mensagem> getMensagensAtivas(String idRest);
}
