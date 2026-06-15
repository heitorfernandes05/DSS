package projeto.business.SSStock;

public class PostoTrabalho {
    private String idPosto;
    private String funcao;
    private String idRestaurante;

    public PostoTrabalho(String idPosto, String funcao, String restId) {
        this.idPosto = idPosto;
        this.funcao = funcao;
        this.idRestaurante = restId;
    }

    public String getIdPosto() {
        return idPosto;
    }

    public String getFuncao() {
        return funcao;
    }
    public String getIdRestaurante() {
        return idRestaurante;
    }
    
}
