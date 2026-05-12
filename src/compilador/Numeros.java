package compilador;

import java.text.CharacterIterator;

public class Numeros extends AFD { // Autômato para reconhecer literais númericos: inteiros, decimais e com sufixos (f, l, b, s)

    private void verificaSufixo(StringBuilder number, CharacterIterator code) { // Verifica se o sufixo é seguido por um separador de tokens (espaço, operador, fim de linha, etc). Se tiver outro caractere alfanumérico depois do sufixo, é um erro léxico (número mal formado).
         if (!isTokenSeparator(code)) {
            throw new RuntimeException("Erro léxico: número mal formado: " + number + code.current());
        }
    }

    @Override
    public Token evaluate(CharacterIterator code) {
        char current = code.current();

        // Começa com dígito? (Se o primeiro caractere não for um número, esse autômato falha e passa a vez)
        if (!Character.isDigit(current)) {
            return null;
        }

        StringBuilder number = new StringBuilder();
        boolean hasDot = false;

        // Lê a parte inteira
        while (Character.isDigit(code.current())) {
            number.append(code.current());
            code.next();
        }

        // Verifica se tem ponto decimal
        if (code.current() == '.') {
            hasDot = true;
            number.append('.');
            code.next();

            // Lê as casas decimais depois do ponto
            while (Character.isDigit(code.current())) {
                number.append(code.current());
                code.next();
            }
        }

        // Verifica se tem sufixos especiais (f, l, b, s)
        char suffix = code.current();
        switch (suffix) {
            case 'f', 'F' -> {
                number.append(suffix); code.next();
                verificaSufixo(number, code);
                return new Token("NUM_FLOAT", number.toString());
            }
            case 'l', 'L' -> {
                number.append(suffix); code.next();
                verificaSufixo(number, code);
                return new Token("NUM_LONG", number.toString());
            }
            case 'b', 'B' -> {
                number.append(suffix); code.next();
                verificaSufixo(number, code);
                return new Token("NUM_BYTE", number.toString());
            }
            case 's', 'S' -> {
                number.append(suffix); code.next();
                verificaSufixo(number, code);
                return new Token("NUM_SHORT", number.toString());
            }
            default -> { }
        }
        

        // Se não teve nenhum sufixo, decide entre INT e DOUBLE pelo ponto
        if (hasDot) {
            return new Token("NUM_DOUBLE", number.toString());
        } else {
            return new Token("NUM_INT", number.toString());
        }
    }
}
