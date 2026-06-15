package projeto.ui;

import java.util.Scanner;
import java.util.List;

import projeto.business.ILNFacade;
import projeto.ui.Aplicacao.AppControlador;
import projeto.ui.Aplicacao.AppView;
import projeto.ui.Bancada.BancadaControlador;
import projeto.ui.Bancada.BancadaView;
import projeto.ui.Cliente.ClienteControlador;
import projeto.ui.Cliente.ClienteView;
import projeto.ui.displays.ArmazemControlador;
import projeto.ui.displays.ArmazemView;
import projeto.business.SSAdministracao.Restaurante;
import projeto.business.SSStock.PostoTrabalho;

public class TextUI {
    private final ILNFacade lnFacade;
    private Scanner scanner;

    public TextUI(ILNFacade facade) {
        this.lnFacade = facade;
        this.scanner = new Scanner(System.in);
    }

    private Menu exibirListaRestaurantes(List<Restaurante> restaurantes) {

        if (restaurantes.isEmpty()) {
            System.out.println("Nenhum restaurante disponível.");
            return null;
        }

        System.out.println("\n--- Restaurantes Disponíveis ---");
        String[] restaurantesLista = new String[restaurantes.size() + 1];
        int ind = 0;
        for (Restaurante r : restaurantes) {
            restaurantesLista[ind] = r.getLocal();
            ind++;
        }
        restaurantesLista[restaurantes.size()] = "Voltar";
        Menu restaurantesmenu = new Menu("--- Restaurantes Disponíveis ---", restaurantesLista);
        int indexVoltar = restaurantes.size() + 1;
        restaurantesmenu.setHandler(indexVoltar, () -> {
            System.out.println("A sair...");
            restaurantesmenu.sair();
        });
        return restaurantesmenu;
    }

    private Menu exibirListaPostos(List<PostoTrabalho> postos) {

        if (postos.isEmpty()) {
            System.out.println("Nenhum posto de trabalho disponível.");
            return null;
        }

        System.out.println("\n--- Postos de Trabalho Disponíveis ---");
        String[] postosLista = new String[postos.size() + 1];
        int ind = 0;
        for (PostoTrabalho p : postos) {
            postosLista[ind] = p.getIdPosto() + " - " + p.getFuncao();
            ind++;
        }
        postosLista[postos.size()] = "Voltar";
        Menu postosmenu = new Menu("--- Postos de Trabalho Disponíveis ---", postosLista);
        int indexVoltar = postos.size() + 1;
        postosmenu.setHandler(indexVoltar, () -> {
            System.out.println("A voltar...");
            postosmenu.sair();
        });
        return postosmenu;
    }

    public void run() throws Exception {
        Menu menuPrincipal = new Menu("Menu Principal", new String[] {
                "Entrar na aplicação",
                "Aceder ao display do cliente",
                "Aceder ao display da bancada",
                "Aceder ao display do armazem",
                "Sair"
        });

        menuPrincipal.setHandler(1, () -> {
            AppControlador appControlador = new AppControlador(lnFacade);
            AppView appView = new AppView(appControlador, scanner);
            try {
                appView.run();
            } catch (Exception e) {
                System.out.println("Erro na aplicação: " + e.getMessage());
            }
            System.out.println("Voltando ao menu principal...");
        });

        menuPrincipal.setHandler(2, () -> {
            List<Restaurante> restaurantes = lnFacade.getAllRestaurantes();
            Menu restaurantesMenu;
            if ((restaurantesMenu = exibirListaRestaurantes(restaurantes)) == null) {
                return;
            }

            for (int i = 1; i <= restaurantes.size(); i++) {
                int index = i - 1;
                Restaurante restaurante = restaurantes.get(index);

                restaurantesMenu.setHandler(i, () -> {
                    String idRestaurante = restaurante.getIdRestaurante();
                    ClienteControlador clienteControlador;
                    try {
                        clienteControlador = new ClienteControlador(idRestaurante, lnFacade);
                        ClienteView clienteView = new ClienteView(clienteControlador);
                        clienteView.run();
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                });
            }
            restaurantesMenu.run();
        });

        menuPrincipal.setHandler(3, () -> {
            List<Restaurante> restaurantes = lnFacade.getAllRestaurantes();
            Menu restaurantesMenu;
            if ((restaurantesMenu = exibirListaRestaurantes(restaurantes)) == null) {
                return;
            }

            for (int i = 1; i <= restaurantes.size(); i++) {
                int index = i - 1;
                Restaurante restaurante = restaurantes.get(index);

                restaurantesMenu.setHandler(i, () -> {
                    String idRestaurante = restaurante.getIdRestaurante();
                    List<PostoTrabalho> postosRestaurante = lnFacade.getPostosTrabalho(idRestaurante);

                    Menu postosMenu;
                    if ((postosMenu = exibirListaPostos(postosRestaurante)) == null) {
                        return;
                    }

                    for (int j = 1; j <= postosRestaurante.size(); j++) {
                        int indexPosto = j - 1;
                        PostoTrabalho posto = postosRestaurante.get(indexPosto);

                        postosMenu.setHandler(j, () -> {
                            String idPosto = posto.getIdPosto();
                            BancadaControlador bancadaControlador;
                            try {
                                bancadaControlador = new BancadaControlador(idRestaurante, idPosto, lnFacade);
                                BancadaView bancadaView = new BancadaView(bancadaControlador);
                                bancadaView.run();
                            } catch (IllegalArgumentException e) {
                                System.out.println("Erro: " + e.getMessage());
                            }
                        });
                    }
                    postosMenu.run();
                });
            }
            restaurantesMenu.run();
        });

        menuPrincipal.setHandler(4, () -> {
            List<Restaurante> restaurantes = lnFacade.getAllRestaurantes();
            Menu restaurantesMenu;
            if ((restaurantesMenu = exibirListaRestaurantes(restaurantes)) == null) {
                return;
            }

            for (int i = 1; i <= restaurantes.size(); i++) {
                int index = i - 1;
                Restaurante restaurante = restaurantes.get(index);

                restaurantesMenu.setHandler(i, () -> {
                    String idRestaurante = restaurante.getIdRestaurante();
                    ArmazemControlador armazemControlador;
                    try {
                        armazemControlador = new ArmazemControlador(idRestaurante, lnFacade);
                        ArmazemView armazemView = new ArmazemView(armazemControlador);
                        armazemView.run();
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                });
            }
            restaurantesMenu.run();
        });

        menuPrincipal.setHandler(5, () -> {
            menuPrincipal.sair();
        });

        menuPrincipal.run();

        System.out.println("A encerrar sistema...");
        scanner.close();
    }
}