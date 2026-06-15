package projeto.ui.Bancada;

import java.util.List;
import java.util.Scanner;

import projeto.ui.Menu;

public class BancadaView {
    private BancadaControlador controlador;

    public BancadaView(BancadaControlador controlador) {
        this.controlador = controlador;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;
        while (continuar) {
            System.out.println();
            System.out.println("Menu Bancada");
            System.out.println("Posto de trabalho: " + controlador.obterPostoTrabalho());
            System.out.println("1 - Solicitar ingrediente em falta");
            System.out.println("2 - Obter Pedidos Pendentes");
            System.out.println("3 - Atualizar Estado de Pedido");
            System.out.println("4 - Atrasar pedido");
            System.out.println("5 - Sair");

            int opcao = lerOpcao(scanner, "Opcao: ", 1, 5);
            switch (opcao) {
                case 1:
                    solicitarIngrediente(scanner);
                    break;
                case 2:
                    obterPedidosPendentes(scanner);
                    break;
                case 3:
                    atualizarEstadoPedido(scanner);
                    break;
                case 4:
                    atrasarPedido(scanner);
                    break;
                case 5:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    private void obterPedidosPendentes(Scanner scanner) {
        List<String> pedidos = this.controlador.obterPedidosPendentes();
        if (pedidos.isEmpty()) {
            System.out.println("Nao ha pedidos pendentes.");
            return;
        }
        System.out.println("Pedidos pendentes:");
        for (String pedido : pedidos) {
            System.out.println("- " + pedido);
        }
    }

    public void atualizarEstadoPedido(Scanner scanner) {
        List<String> pedidos = this.controlador.obterPedidosPendentesEstado();
        if (pedidos.isEmpty()) {
            System.out.println("Nao ha pedidos pendentes.");
            return;
        }
        Menu pedidosMenu = new Menu("Pedidos Pendentes", pedidos.toArray(new String[0]));
        for (int i = 0; i < pedidos.size(); i++) {
            final int index = i;
            pedidosMenu.setHandler(i + 1, () -> {
                int idPedido = Integer.parseInt(pedidos.get(index).split("-")[0]);
                String estadoPedido = pedidos.get(index).split("-")[1];
                boolean sucesso = this.controlador.atualizarEstadoPedido(idPedido, estadoPedido);
                if (sucesso) {
                    System.out.println("Estado do pedido " + idPedido + " atualizado com sucesso.");
                    pedidosMenu.sair();
                } else {
                    System.out.println("Falha ao atualizar o estado do pedido " + idPedido + ".");
                    pedidosMenu.sair();
                }
            });
        }
        pedidosMenu.run();
    }

    private void solicitarIngrediente(Scanner scanner) {
        List<String> ingredientes = controlador.obterIngredientes();
        
        if (ingredientes.isEmpty()) {
            System.out.println("Nenhum ingrediente.");
            return;
        }
        String[] opcoesIngredientes = new String[ingredientes.size() + 1];
        for (int i = 0; i < ingredientes.size(); i++) {
            String[] partes = ingredientes.get(i).split("-");
            if (partes.length >= 2) {
                opcoesIngredientes[i] = partes[1] + " (" + partes[0] + ")";
            } else {
                opcoesIngredientes[i] = ingredientes.get(i);
            }
        }
        opcoesIngredientes[ingredientes.size()] = "Voltar";
        Menu menuIngredientes = new Menu("Selecione Ingrediente para Reposição", opcoesIngredientes);
        
        for (int i = 1; i <= ingredientes.size(); i++) {
            final String ingredienteCompleto = ingredientes.get(i - 1);
            
            menuIngredientes.setHandler(i, () -> {
                String idIngrediente = ingredienteCompleto.split("-")[0];
                String nomeIngrediente = ingredienteCompleto.split("-")[1];
                System.out.println("\n=== Solicitar Reposição: " + nomeIngrediente + " ===");
                
                int quantidade = -1;
                while (quantidade <= 0) {
                    System.out.print("Quantidade a solicitar: ");
                    try {
                        quantidade = scanner.nextInt();
                        scanner.nextLine();
                        
                        if (quantidade <= 0) {
                            System.out.println("A quantidade deve ser positiva.");
                        }
                    } catch (Exception e) {
                        System.out.println("Quantidade inválida! Digite um número.");
                        scanner.nextLine();
                    }
                }
                
                System.out.println("\nConfirmar solicitação?");
                System.out.println("Ingrediente: " + nomeIngrediente);
                System.out.println("Quantidade: " + quantidade);
                System.out.print("(S/N): ");
                String confirmacao = scanner.nextLine().trim();
                if (!confirmacao.equalsIgnoreCase("S")) {
                    System.out.println("Solicitação cancelada.");
                    return;
                }
                try {
                    String idSolicitacao = controlador.solicitarReposicao(idIngrediente, quantidade);
                    
                    if (idSolicitacao == null) {
                        System.out.println("Stock insuficiente ou erro na solicitação.");
                        return;
                    }
                    
                    long minutos = controlador.obterMinutosAteReposicao(idSolicitacao);
                    
                    if (minutos < 0) {
                        System.out.println("\nSolicitação registada com ID: " + idSolicitacao);
                        System.out.println("Tempo de reposição ainda não confirmado.");
                    } else {
                        System.out.println("\n✓ Solicitação registada com ID: " + idSolicitacao);
                        System.out.println("Tempo estimado de reposição: " + minutos + " minutos.");
                    }
                    
                } catch (Exception e) {
                    System.out.println("Erro ao processar solicitação: " + e.getMessage());
                }
                
            });
        }
        menuIngredientes.setHandler(ingredientes.size() + 1, () -> {
            menuIngredientes.sair();
        });
        
        menuIngredientes.run();
    }

    private void atrasarPedido(Scanner scanner) {
        int idPedido = lerInt(scanner, "Numero do pedido: ");
        int minutos = lerInt(scanner, "Atraso em minutos: ");
        boolean ok = controlador.atrasarPedido(idPedido, minutos);
        if (!ok) {
            System.out.println("Valores invalidos.");
            return;
        }
        System.out.println("Pedido atrasado " + minutos + " minutos.");
    }

    private int lerOpcao(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            int valor = lerInt(scanner, prompt);
            if (valor >= min && valor <= max) {
                return valor;
            }
            System.out.println("Opcao invalida.");
        }
    }

    private boolean lerSimNao(Scanner scanner, String prompt) {
        while (true) {
            String resposta = lerLinha(scanner, prompt);
            if (resposta.equalsIgnoreCase("s") || resposta.equalsIgnoreCase("sim")) {
                return true;
            }
            if (resposta.equalsIgnoreCase("n") || resposta.equalsIgnoreCase("nao")) {
                return false;
            }
            System.out.println("Resposta invalida. Por favor, responda com 's' ou 'n'.");
        }
    }

    private int lerInt(Scanner scanner, String prompt) {
        while (true) {
            String texto = lerLinha(scanner, prompt);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido.");
            }
        }
    }

    private String lerLinha(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
