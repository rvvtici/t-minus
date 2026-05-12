package compilador;

public class Token { // Representa um token reconhecido pelo analisador léxico, com seu tipo (categoria) e lexema (valor literal).
    String tipo; 
    String lexema; 

    public Token(String tipo, String lexema) {
        this.tipo = tipo; // Categoria do token, como "id_var", "num_int", "res_nave", etc.
        this.lexema = lexema; // Valor literal do token, como "x", "42", "nave", etc.
    }

    @Override 
    public String toString() { // Formata no padrão <tipo, lexema>
        return "<" + tipo + ", " + lexema + ">"; 
    }
}

