package br.com.musicall.servicos;

import br.com.musicall.modelos.RedeSocialModelo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "apiRedes", url = "http://localhost:8080/sociais")
public interface RedeSocialService {

    @PostMapping("/{idUsuario}")
    Integer criar(@PathVariable Integer idUsuario, @RequestBody RedeSocialModelo redeSocial);

    @GetMapping("/{idUsuario}")
    RedeSocialModelo listarRedeSocial(@PathVariable Integer idUsuario);

    @PutMapping("/{idUsuario}/{idRedeSocial}/{redeSocial}")
    String alterarRedeSocial(@PathVariable Integer idUsuario, @PathVariable Integer idRedeSocial, @PathVariable String redeSocial, @RequestBody RedeSocialModelo rede);

    @DeleteMapping("/{idUsuario}/{idRedeSocial}")
    String deletar(@PathVariable Integer idUsuario, @PathVariable Integer idRedeSocial);
}
