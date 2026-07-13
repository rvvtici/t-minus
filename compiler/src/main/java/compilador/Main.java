package compilador;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        CompilerService service = new CompilerService();

        while (true) {

            StringBuilder codigo = new StringBuilder();

            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║       COMPILADOR T-MINUS ==> PASCAL      ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.println("Digite seu código T-minus linha por linha.");
            System.out.println("Digite FIM para gerar Pascal.");
            System.out.println("──────────────────────────────────────────");

            while (scanner.hasNextLine()) {

                String linha = scanner.nextLine();

                if (linha.trim().equalsIgnoreCase("FIM")) {
                    break;
                }

                codigo.append(linha).append("\n");
            }

            System.out.println("──────────────────────────────────────────");

            try {
                CompilerService.ResultadoCompilacao resultado = service.compilar(codigo.toString());

                System.out.println("PASCAL GERADO:");
                System.out.println("──────────────────────────────────────────");
                System.out.println(resultado.pascal);

                System.out.println("\nDeseja visualizar a lista de tokens? (S/N)");
                String tokensResp = scanner.nextLine();

                if (tokensResp.equalsIgnoreCase("S")) {
                    System.out.println("\nLISTA DE TOKENS");
                    System.out.println("──────────────────────────────────────────");

                    for (Token t : resultado.tokens) {
                        System.out.println(
                                "<" + t.tipo + ", " + t.lexema + ">"
                        );
                    }
                }

                System.out.println("\nDeseja visualizar a AST? (S/N)");

                String arvore = scanner.nextLine();

                if (arvore.equalsIgnoreCase("S")) {

                    System.out.println("\nÁRVORE SINTÁTICA (AST)");
                    System.out.println("──────────────────────────────────────────");

                    resultado.ast.imprimir();
                }

            } catch (Exception e) {
                System.out.println("✗ ERRO: " + e.getMessage());
            }

            System.out.println("\nDeseja compilar outro código? (S/N)");

            String resposta = scanner.nextLine();

            if (!resposta.equalsIgnoreCase("S")) {
                System.out.println("Encerrando o compilador... Obrigado por usar o T-Minus!");
                break;
            }
        }

        scanner.close();
    }
}