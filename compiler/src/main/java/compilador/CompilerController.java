package compilador;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CompilerController {

    private final CompilerService service = new CompilerService();

    @PostMapping("/compilar")
    public ResponseEntity<?> compilar(@RequestBody String codigoFonte) {
        try {
            CompilerService.ResultadoCompilacao resultado = service.compilar(codigoFonte);
            return ResponseEntity.ok(resultado.pascal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro de compilação: " + e.getMessage());
        }
    }
}