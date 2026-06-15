package projeto.ui.Aplicacao;

import java.util.List;
import java.util.Scanner;

import projeto.business.SSAdministracao.Restaurante;
import projeto.ui.Menu;

public class AppView {
    private AppControlador controlador;
    private Scanner scanner;

    public AppView(AppControlador controlador, Scanner scanner) {
        this.controlador = controlador;
        this.scanner = scanner;
    }

    public void run() {
        if (!fazerLogin()) {
            System.out.println("Login falhou ou cancelado.");
            return;
        }

        String cargo = controlador.getCargoUtilizador();
        System.out.println("\nBem-vind@. Perfil identificado: " + cargo);

        menuApp();

        controlador.terminarSessao();
        System.out.println("Sessão terminada.");
    }

    private boolean fazerLogin() {
        System.out.println("\n=== LOGIN ===");
        final int MAX_TENTATIVAS = 3;
        int tentativas = 0;
        
        while (tentativas < MAX_TENTATIVAS) {
            System.out.print("Email (ou 'sair' para cancelar): ");
            String email = scanner.nextLine().trim();
            
            if (email.equalsIgnoreCase("sair")) {
                System.out.println("Login cancelado.");
                return false;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();

            try {
                controlador.autenticar(email, password);
                return true;
            } catch (Exception e) {
                tentativas++;
                int restantes = MAX_TENTATIVAS - tentativas;
                if (restantes > 0) {
                    System.out.println("Dados incorretos. Tentativas restantes: " + restantes);
                } else {
                    System.out.println("Número máximo de tentativas excedido.");
                    return false;
                }
            }
        }
        return false;
    }

    private void menuApp() {
        Menu menuApp = new Menu("Menu App", new String[]{
            "Consultar Indicadores",
            "Enviar Mensagem para displays",
            "Terminar Sessão"
        });
        
        menuApp.setHandler(1, this::consultarIndicadores);
        menuApp.setHandler(2, this::enviarMensagem);
        menuApp.setHandler(3, () -> menuApp.sair());
        
        menuApp.run();
    }


    private Menu exibirListaRestaurantes(List<Restaurante> restaurantes) {
        
        if (restaurantes.isEmpty()) {
            System.out.println("Nenhum restaurante disponível.");
            return null;
        }

        System.out.println("\n--- Restaurantes Disponíveis ---");
        String[] restaurantesLista = new String[restaurantes.size()+1];
        int ind = 0;
        for (Restaurante r : restaurantes) {
            restaurantesLista[ind] = r.getLocal();
            ind++;
        }
        restaurantesLista[restaurantes.size()] = "Voltar";
        Menu restaurantesmenu =  new Menu("--- Restaurantes Disponíveis ---", restaurantesLista);
        int indexVoltar = restaurantes.size() + 1;
        restaurantesmenu.setHandler(indexVoltar, () -> {System.out.println("A sair..."); restaurantesmenu.sair();} );
        return restaurantesmenu;
    }

    private void consultarIndicadores() {
        List<Restaurante> restaurantes = controlador.getListaRestaurantes();
        Menu restaurantesMenu;
        if ((restaurantesMenu = exibirListaRestaurantes(restaurantes))==null) {
            return;
        }
        for (int i = 1; i <= restaurantes.size(); i++) {
            int index = i - 1;
            Restaurante restaurante = restaurantes.get(index);
        
            restaurantesMenu.setHandler(i, () -> {
                try {
                    System.out.println("\n=== Indicadores - " + restaurante.getLocal() + " ===");
                    System.out.println("Tempo Médio de Atendimento: " + 
                                    controlador.getTempoMedioAtendimento(restaurante.getIdRestaurante()));
                    System.out.println("Faturação: " + 
                                    controlador.getFaturacao(restaurante.getIdRestaurante()) + " €");                    
                    System.out.println("\nPressione Enter para continuar...");
                    scanner.nextLine();
                } catch (Exception e) {
                    System.out.println("Erro ao consultar indicadores: " + e.getMessage());
                }
            });
        }
    
        restaurantesMenu.run();
    }

    private void enviarMensagem() {
        List<Restaurante> restaurantes = controlador.getListaRestaurantes();
        Menu restaurantesMenu;
        if ((restaurantesMenu = exibirListaRestaurantes(restaurantes))==null) {
            return;
        }

        for (int i = 1; i <= restaurantes.size(); i++) {
            int index = i - 1;
            Restaurante restaurante = restaurantes.get(index);
            
            restaurantesMenu.setHandler(i, () -> {
                System.out.println("\n=== Enviar Mensagem para: " + restaurante.getLocal() + " ===");
                System.out.print("Digite a mensagem a enviar (ou ENTER para voltar): ");
                String mensagem = scanner.nextLine();
                
                if (mensagem.trim().isEmpty()) {
                    System.out.println("Mensagem vazia. Operação cancelada.");
                    return;
                }
                
                try {
                    controlador.enviarMensagem(restaurante.getIdRestaurante(), mensagem);
                    System.out.println("\nMensagem enviada com sucesso para " + restaurante.getLocal() + "!");
                    System.out.println("\nPressione Enter para continuar...");
                    scanner.nextLine();
                } catch (Exception e) {
                    System.out.println("\nErro ao enviar mensagem: " + e.getMessage());
                    System.out.println("\nPressione Enter para continuar...");
                    scanner.nextLine();
                }
            });
        }
        restaurantesMenu.run();
    }
}