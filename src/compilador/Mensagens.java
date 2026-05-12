package compilador;

import java.text.CharacterIterator;

public class Mensagens extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        char current = code.current();

        // Se não começar com aspas duplas, ignora e passa a vez
        if (current != '"') {
            return null;
        }

        StringBuilder texto = new StringBuilder();
        
        // Adiciona a primeira aspa na string final
        texto.append(current);
        code.next();

        // Lê tudo até encontrar a aspa de fechamento, o fim da linha ou o fim do arquivo
        while (code.current() != '"' && code.current() != '\n' && code.current() != CharacterIterator.DONE) {
            texto.append(code.current());
            code.next();
        }

        // Se parou de ler e não é uma aspa, significa que a string ficou aberta (erro de sintaxe)
        if (code.current() != '"') {
            throw new RuntimeException("String literal não foi fechada com aspas duplas.");
        }

        // Adiciona a última aspa na string final e avança o iterador
        texto.append(code.current());
        code.next();

        return new Token("MENSAGEM", texto.toString());
    }
}