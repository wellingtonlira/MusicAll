package br.com.musicall.controllers;

import br.com.musicall.dominios.Genero;
import br.com.musicall.repositorios.GeneroRepository;
import br.com.musicall.servicos.UsuarioService;
import br.com.musicall.visoes.GeneroSimples;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/generos")
@CrossOrigin(origins = {"http://localhost:3000", "https://bandtec.github.io"})
public class GeneroController {

    @Autowired
    private GeneroRepository repository;

    @Autowired
    private UsuarioService service;
    
    @PostMapping("/{idUsuario}")
    public ResponseEntity criar(@PathVariable Integer idUsuario, @RequestBody Genero novoGenero) {
        if(novoGenero != null){
            Integer id = service.recuperarUsuario();
            repository.adicionarGenero(novoGenero.getGeneroMusical(), novoGenero.getPreferencia(), id);
            return ResponseEntity.created(null).body(true);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("buscar/usuario/{idUsuario}")
    public ResponseEntity listarGeneroOutroUsuario(@PathVariable Integer idUsuario){
        List<GeneroSimples> generos = repository.selecionarPeloIdUsuario(idUsuario);
        if (generos.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(generos);
        }
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity listarGenero(@PathVariable Integer idUsuario){
        Integer id = service.recuperarUsuario();
        List<GeneroSimples> generos = repository.selecionarPeloIdUsuario(id);
        if (generos.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(generos);
        }
    }

    @GetMapping("/{idUsuario}/{idGenero}")
    public ResponseEntity getPorId(@PathVariable Integer idUsuario, @PathVariable int idGenero) {
        if (repository.existsById(idGenero)) {
            return ResponseEntity.ok(repository.selecionarPeloIdGenero(idGenero));
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/{idUsuario}/{idGenero}")
    public ResponseEntity alterarPreferencia(@PathVariable Integer idUsuario, @PathVariable Integer idGenero, @RequestBody Genero genero){
        if(genero.getPreferencia() != null){
            repository.alterarPreferenciaPorId(genero.getPreferencia(), idGenero);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("todas/{idUsuario}/{idGenero}")
    public ResponseEntity alterarGenero(@PathVariable Integer idUsuario, @PathVariable Integer idGenero, @RequestBody Genero genero){
        if(genero.getGeneroMusical() != null){
            repository.alterarGeneroPorId(genero.getGeneroMusical(), idGenero);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @DeleteMapping("/{idUsuario}/{idGenero}")
    public ResponseEntity deletar(@PathVariable Integer idUsuario, @PathVariable Integer idGenero){
        if (repository.existsById(idGenero)) {
            repository.deleteById(idGenero);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuarios/{genero}")
    public ResponseEntity getTodosGeneros(@PathVariable String genero){
        List<Genero> generos = repository.getTodosGeneros(genero);
        if(!generos.isEmpty()){
            return ResponseEntity.ok(generos);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
