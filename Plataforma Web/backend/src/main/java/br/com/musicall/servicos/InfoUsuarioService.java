package br.com.musicall.servicos;

import br.com.musicall.dominios.InfoUsuario;
import br.com.musicall.modelos.InfoUsuarioModelo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "apiInfo", url = "http://localhost:8080/infos")
public interface InfoUsuarioService {

    @PostMapping("/{idUsuario}")
    Integer criar(@PathVariable Integer idUsuario, @RequestBody InfoUsuarioModelo info);

    @GetMapping("/{idUsuario}")
    InfoUsuarioModelo getInfo(@PathVariable Integer idUsuario);

    @PutMapping("/{idUsuario}/{idInfoUsuario}/{detalhe}")
    Boolean alterarInfo(@PathVariable Integer idUsuario, @PathVariable Integer idInfoUsuario, @PathVariable String detalhe, @RequestBody InfoUsuarioModelo infoUsuario);

    @DeleteMapping("/{idUsuario}/{idInfo}")
    Boolean deletar(@PathVariable Integer idUsuario, @PathVariable Integer idInfo);

    @GetMapping("/usuarios/{cidade}")
    List<InfoUsuario> getInfoUsuario(@PathVariable String cidade);

}
