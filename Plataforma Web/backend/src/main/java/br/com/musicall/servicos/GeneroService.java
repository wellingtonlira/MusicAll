package br.com.musicall.servicos;

import br.com.musicall.dominios.Genero;
import br.com.musicall.modelos.GeneroModelo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "generosService", url = "http://localhost:8080/generos")
public interface GeneroService {

    @PostMapping("/{idUsuario}")
    void criar (@PathVariable Integer idUsuario, @RequestBody GeneroModelo genero);

    @GetMapping("/{idUsuario}")
    List<GeneroModelo> listarGenero(@PathVariable Integer idUsuario);

    @GetMapping("buscar/usuario/{idUsuario}")
    List<GeneroModelo> listarGeneroOutroUsuario(@PathVariable Integer idUsuario);

    @GetMapping("/{idUsuario}/{idGenero}")
    GeneroModelo getPorId(@PathVariable Integer idUsuario, @PathVariable int idGenero);

    @PutMapping("/{idUsuario}/{idGenero}")
    String alterarPreferencia(@PathVariable Integer idUsuario, @PathVariable Integer idGenero, @RequestBody GeneroModelo genero);

    @DeleteMapping("/{idUsuario}/{idGenero}")
    String deletar(@PathVariable Integer idUsuario, @PathVariable Integer idGenero);

    @GetMapping("/usuarios/{genero}")
    List<Genero> getTodosGeneros(@PathVariable String genero);

}
