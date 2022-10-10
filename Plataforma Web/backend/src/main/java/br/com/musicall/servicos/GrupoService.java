package br.com.musicall.servicos;

import br.com.musicall.dominios.Grupo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "grupoService", url = "http://localhost:8080/grupos")
public interface GrupoService {

    @PostMapping
    ResponseEntity criar(@RequestBody Grupo novoGrupo);

    @PostMapping("/{idGrupo}")
    ResponseEntity entrarGrupo(@PathVariable Integer idGrupo, Integer idUsuario);

    @GetMapping
    ResponseEntity listarGrupos(Integer idUsuario);

    @GetMapping("/{idGrupo}")
    ResponseEntity getPorId(@PathVariable int idGrupo);

    @DeleteMapping("/{idGrupo}")
    ResponseEntity deletar(@PathVariable Integer idGrupo, Integer idUsuario);

}
