package compilador;

import java.text.CharacterIterator;

public class Numeros extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        char current = code.current();

        // Se o primeiro caractere não for um número, esse autômato falha e passa a vez
        if (!Character.isDigit(current)) {
            return null;
        }

        StringBuilder number = new StringBuilder();
        boolean hasDot = false;

        // 1. Lê a parte inteira
        while (Character.isDigit(code.current())) {
            number.append(code.current());
            code.next();
        }

        // 2. Verifica se tem ponto decimal
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

        // 3. Verifica os sufixos especiais (f, l, b, s)
        char suffix = code.current();
        if (suffix == 'f' || suffix == 'F') {
            number.append(suffix);
            code.next();
            return new Token("NUM_FLOAT", number.toString());
            
        } else if (suffix == 'l' || suffix == 'L') {
            number.append(suffix);
            code.next();
            return new Token("NUM_LONG", number.toString());
            
        } else if (suffix == 'b' || suffix == 'B') {
            number.append(suffix);
            code.next();
            return new Token("NUM_BYTE", number.toString());
            
        } else if (suffix == 's' || suffix == 'S') {
            number.append(suffix);
            code.next();
            return new Token("NUM_SHORT", number.toString());
        }

        // 4. Se não teve nenhum sufixo, decide entre INT e DOUBLE pelo ponto
        if (hasDot) {
            return new Token("NUM_DOUBLE", number.toString());
        } else {
            return new Token("NUM_INT", number.toString());
        }
    }
}
