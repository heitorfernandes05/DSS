package projeto.business.SSAdministracao;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import projeto.data.RestauranteDAO;
import projeto.data.UserDAO;


public class SSAdministracaoFacade implements ISSAdministracao {
    private final UserDAO users;
    private final RestauranteDAO restaurantes;
    private User sessaoIniciada;

    public SSAdministracaoFacade() {
        this.sessaoIniciada = null;
        this.users = UserDAO.getInstance();
        this.restaurantes = RestauranteDAO.getInstance(); 
    }
    @Override
    public void autenticar(String email, String password) throws IllegalArgumentException {
        User user = this.users.get(email);
        if(user.validarPassword(password)) {
            this.sessaoIniciada = user;
        } else {
            throw new IllegalArgumentException("Password inválida.");
        }
    }

    @Override
    public void terminarSessao() {
        this.sessaoIniciada = null;
    }

    @Override
    public String getCargoUtilizador() {
        return this.sessaoIniciada.getCargo();
    }

    @Override
    public List<Restaurante> getRestaurantesAcessiveis() {
        if(this.sessaoIniciada == null) {
            throw new IllegalStateException("Nenhuma sessão iniciada.");
        }
        List<String> ids = this.sessaoIniciada.getRestaurantesAcessiveis();
        List<Restaurante> restaurantesAcessiveis = new ArrayList<>();
        for(String id : ids) {
            Restaurante r = this.restaurantes.get(id);
            restaurantesAcessiveis.add(r);
        }
        return restaurantesAcessiveis;
    }

    @Override
    public List<Restaurante> getAllRestaurantes() {
        return new ArrayList<>(this.restaurantes.values());
    }
    
    @Override
    public Duration getTempoMedioAtendimento(String idRest) {
        Restaurante rest = this.restaurantes.get(idRest);
        Duration tempo = rest.getTempoMedioAtendimento();
        return tempo;
    }
    
    @Override
    public double getFaturacao(String idRest) {
        Restaurante rest = this.restaurantes.get(idRest);
        double faturacao = rest.getFaturacao();
        return faturacao;
    }
    
    @Override
    public void atualizarIndicadores(String idRest, double valorPedido, Duration tempoPedido) {
        Restaurante rest = this.restaurantes.get(idRest);
        rest.atualizarIndicadores(valorPedido, tempoPedido);
        this.restaurantes.put(idRest, rest);
    }
    
    @Override
    public void enviarMensagem(String idRest, String conteudo) {
        Restaurante restaurante = this.restaurantes.get(idRest);
        Mensagem mensagem = new Mensagem(sessaoIniciada, conteudo);
        restaurante.adicionarMensagem(mensagem);
    }
    
    @Override
    public List<Mensagem> getMensagensAtivas(String idRest) {
        Restaurante restaurante = this.restaurantes.get(idRest);
        return restaurante.getMensagensAtivas();
    }

}
