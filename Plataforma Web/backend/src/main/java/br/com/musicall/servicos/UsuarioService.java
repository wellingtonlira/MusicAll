package br.com.musicall.servicos;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "usuarioApi", url = "http://localhost:8080/usuarios")
public interface UsuarioService {

    @GetMapping("/recuperar")
    Integer recuperarUsuario();
    
}
