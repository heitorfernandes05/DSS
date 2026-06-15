package projeto.ui.Cliente;

import java.util.Scanner;

public class ClienteView {
    private ClienteControlador controlador;

    public ClienteView(ClienteControlador controlador) {
        this.controlador = controlador;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            int idPedido = controlador.iniciarPedido();
            System.out.println("Pedido iniciado. Numero: " + idPedido);

            if (controlador.obterProdutosDisponiveisTexto().isEmpty()) {
                System.out.println("Sem produtos disponiveis. Pedido cancelado.");
                controlador.cancelarPedido();
                return;
            }
            boolean pedidoAtivo = true;
            boolean localDefinido = false;

            while (pedidoAtivo) {
                System.out.println();
                System.out.println("Menu Cliente");
                System.out.println("1 - Ver produtos disponiveis");
                System.out.println("2 - Ver produtos selecionados");
                System.out.println("3 - Adicionar produto");
                System.out.println("4 - Remover produto");
                System.out.println("5 - Personalizar item");
                System.out.println("6 - Adicionar nota");
                System.out.println("7 - Definir local de consumo");
                System.out.println("8 - Finalizar e pagar");
                System.out.println("9 - Cancelar pedido");

                int opcao = lerOpcao(scanner, "Opcao: ", 1, 9);
                switch (opcao) {
                    case 1:
                        mostrarProdutos();
                        break;
                    case 2:
                        mostrarSelecionados();
                        break;
                    case 3:
                        mostrarProdutos();
                        String idProduto = lerLinha(scanner, "ID do produto: ");
                        int quantidade = lerInt(scanner, "Quantidade: ");
                        boolean ok = controlador.adicionarProduto(idProduto, quantidade);
                        if (!ok) {
                            System.out.println("Produto indisponivel.");
                            int opcaoIndisp = lerOpcao(scanner, "1 - escolher outro, 2 - cancelar pedido: ", 1, 2);
                            if (opcaoIndisp == 2) {
                                controlador.cancelarPedido();
                                pedidoAtivo = false;
                            }
                        }
                        break;
                    case 4:
                        if (!controlador.temProdutosSelecionados()) {
                            System.out.println("Nao existem produtos no pedido.");
                            break;
                        }
                        mostrarSelecionados();
                        int idProdutoPedido = lerInt(scanner, "Indice do produto a remover: ");
                        controlador.removerProduto(idProdutoPedido);
                        break;
                    case 5:
                        if (!controlador.temProdutosSelecionados()) {
                            System.out.println("Nao existem produtos no pedido.");
                            break;
                        }
                        mostrarSelecionados();
                        idProdutoPedido = lerInt(scanner, "Indice do produto a personalizar: ");
                        int idItem = lerInt(scanner, "Id do item (se menu) ou 0: ");
                        boolean removerMais = true;
                        while (removerMais) {
                            String idIngrediente = lerLinha(scanner, "Id do ingrediente a remover (vazio para terminar): ");
                            if (idIngrediente.isBlank()) {
                                break;
                            }
                            controlador.removerIngredienteItem(idProdutoPedido, idItem, idIngrediente);
                            removerMais = lerSimNao(scanner, "Remover outro ingrediente? (s/n): ");
                        }
                        break;
                    case 6:
                        String nota = lerLinha(scanner, "Nota: ");
                        controlador.adicionarNota(nota);
                        break;
                    case 7:
                        boolean localOk = false;
                        while (!localOk) {
                            String local = lerLinha(scanner, "Local de consumo (local/takeaway): ");
                            try {
                                controlador.definirLocalEntrega(local);
                                localOk = true;
                                localDefinido = true;
                            } catch (IllegalArgumentException e) {
                                System.out.println("Local invalido.");
                            }
                        }
                        break;
                    case 8:
                        if (!controlador.temProdutosSelecionados()) {
                            System.out.println("Selecione pelo menos um produto.");
                            break;
                        }
                        if (!localDefinido) {
                            boolean localOk2 = false;
                            while (!localOk2) {
                                String local = lerLinha(scanner, "Local de consumo (local/takeaway): ");
                                try {
                                    controlador.definirLocalEntrega(local);
                                    localOk2 = true;
                                    localDefinido = true;
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Local invalido.");
                                }
                            }
                        }
                        controlador.confirmarPedido();
                        float total = controlador.calcularTotal();
                        System.out.println("Total a pagar: " + total);
                        String metodo = lerLinha(scanner, "Metodo de pagamento (multibanco, mbway, numerário): ");
                        metodo = normalizarMetodoPagamento(metodo);
                        if ("mbway".equalsIgnoreCase(metodo)) {
                            System.out.println("Mostrando QR Code para pagamento MBWay.");
                        }
                        if ("multibanco".equalsIgnoreCase(metodo)) {
                            System.out.println("Insira o cartão no terminal Multibanco.");
                        }
                        boolean pago = false;
                        boolean cancelado = false;
                        while (!pago) {
                            float valorPago = lerFloat(scanner, "Valor pago: ");
                            pago = controlador.efetuarPagamento(metodo, valorPago);
                            if (!pago) {
                                System.out.println("Pagamento invalido.");
                                boolean tentar = lerSimNao(scanner, "Tentar novamente? (s/n): ");
                                if (!tentar) {
                                    controlador.cancelarPedido();
                                    pedidoAtivo = false;
                                    cancelado = true;
                                    System.out.println("Pedido cancelado.");
                                    break;
                                }
                            }
                        }
                        if (!cancelado) {
                            System.out.println("Pedido confirmado. Numero: " + idPedido + ".");
                            String fatura = controlador.obterFatura();
                            if (fatura != null && !fatura.isBlank()) {
                                System.out.println(fatura);
                            }
                        }
                        pedidoAtivo = false;
                        break;
                    case 9:
                        controlador.cancelarPedido();
                        pedidoAtivo = false;
                        break;
                    default:
                        System.out.println("Opcao invalida.");
                        break;
                }
            }

            continuar = lerSimNao(scanner, "Fazer novo pedido? (s/n): ");
        }
    }

    private void mostrarProdutos() {
        System.out.println("Produtos disponiveis:");
        String produtosTexto = controlador.obterProdutosDisponiveisTexto();
        if (produtosTexto.equals("")) {
            System.out.println("Nenhum produto disponivel.");
            return;
        }
        System.out.println(produtosTexto);
    }

    private void mostrarSelecionados() {
        System.out.println("Produtos selecionados:");
        System.out.print(controlador.obterProdutosSelecionadosTexto());
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
        String resposta = lerLinha(scanner, prompt);
        boolean respostaValida = resposta.equalsIgnoreCase("s") || resposta.equalsIgnoreCase("sim") || resposta.equalsIgnoreCase("n") || resposta.equalsIgnoreCase("nao") || resposta.equalsIgnoreCase("não");
        if (!respostaValida) {
            System.out.println("Resposta invalida. Por favor, responda com 's' ou 'n'.");
            return lerSimNao(scanner, prompt);
        }
        return resposta.equalsIgnoreCase("s") || resposta.equalsIgnoreCase("sim");
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

    private float lerFloat(Scanner scanner, String prompt) {
        while (true) {
            String texto = lerLinha(scanner, prompt);
            try {
                return Float.parseFloat(texto);
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido.");
            }
        }
    }

    private String lerLinha(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String normalizarMetodoPagamento(String metodo) {
        String texto = metodo.trim().toLowerCase();
        if (texto.equals("mb way")) {
            return "mbway";
        }
        if (texto.equals("dinheiro") || texto.equals("numerario")) {
            return "numerário";
        }
        return texto;
    }
}
