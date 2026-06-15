package projeto.ui.displays;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import projeto.business.SSStock.SolicitacaoReposicao;


public class ArmazemView {
    private ArmazemControlador controlador;

    public ArmazemView(ArmazemControlador controlador) {
        this.controlador = controlador;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println();
            System.out.println("=== Menu Armazem ===");
            System.out.println("1 - Ver solicitações pendentes");
            System.out.println("2 - Atualizar data prevista de reposição");
            System.out.println("3 - Finalizar solicitação de reposição");
            System.out.println("4 - Repor stock total");
            System.out.println("5 - Verificar stock atual");
            System.out.println("6 - Sair");

            int opcao = lerInt(scanner, "Opção: ");

            switch (opcao) {
                case 1 -> mostrarSolicitacoesPendentes();
                case 2 -> atualizarData(scanner);
                case 3 -> finalizarSolicitacao(scanner);
                case 4 -> reporStockTotal();
                case 5 -> verificarStock(scanner);
                case 6 -> continuar = false;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void mostrarSolicitacoesPendentes() {
        try {
            List<SolicitacaoReposicao> lista = controlador.obterSolicitacoesPendentes();
            if (lista.isEmpty()) {
                System.out.println("Sem solicitações pendentes.");
                return;
            }
            System.out.println("Solicitações pendentes:");
            for (SolicitacaoReposicao s : lista) {
                System.out.println(
                    "ID: " + s.getIdSolicitacao() +
                    " | Ingrediente: " + s.getIngrediente().getIdIngrediente() +
                    " | Nome: " + s.getIngrediente().getNome() +
                    " | Quantidade: " + s.getQuantidade() +
                    " | Posto: " + s.getIdPosto() 
                );
                LocalDateTime dataPrevista = s.getDataReposicaoPrevista();
                if (dataPrevista != null) {
                    System.out.println(" | Data prevista de reposição: " + dataPrevista);
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void atualizarData(Scanner scanner) {
        System.out.print("ID da solicitação: ");
        String id = scanner.nextLine().trim();
        int minutos = lerInt(scanner, "Minutos ate reposição: ");
        try {
            controlador.atualizarDataReposicao(id, minutos);
            System.out.println("Data de reposição atualizada.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void finalizarSolicitacao(Scanner scanner) {
        System.out.print("ID da solicitação: ");
        String id = scanner.nextLine().trim();
        try {
            controlador.finalizarReposicao(id);
            System.out.println("Solicitação finalizada e stock atualizado.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void reporStockTotal() {
        try {
            controlador.reporStockTotal();
            System.out.println("Stock total reposto.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void verificarStock(Scanner scanner) {
        List<String> stockList = controlador.verificarStock();
        System.out.println("Stock atual:");
        for (String stockInfo : stockList) {
            System.out.println(stockInfo);
        }
    }

    private int lerInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String texto = scanner.nextLine().trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido.");
            }
        }
    }
}
