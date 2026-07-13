package compilador;

import java.util.List;

public class CompilerService {

    public static class ResultadoCompilacao {
        public String pascal;
        public List<Token> tokens;
        public Node ast;

        public ResultadoCompilacao(String pascal, List<Token> tokens, Node ast) {
            this.pascal = pascal;
            this.tokens = tokens;
            this.ast = ast;
        }
    }

    public ResultadoCompilacao compilar(String codigoFonte) throws Exception {
        List<Token> tokens = new Lexer(codigoFonte).getTokens();
        Node raiz = new Parser(tokens).parseProg();
        String pascal = new Gerador().gerar(raiz);

        return new ResultadoCompilacao(pascal, tokens, raiz);
    }
}