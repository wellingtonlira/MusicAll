package br.com.musicall.controllers;

import br.com.musicall.dominios.Instrumento;
import br.com.musicall.repositorios.InstrumentoRepository;
import br.com.musicall.servicos.UsuarioService;
import br.com.musicall.visoes.InstrumentoSimples;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instrumentos")
@CrossOrigin(origins = {"http://localhost:3000", "https://bandtec.github.io"})
public class InstrumentoController {

    @Autowired
    private InstrumentoRepository repository;

    @Autowired
    private UsuarioService service;

    @PostMapping("/{idUsuario}")
    public ResponseEntity criar(@PathVariable Integer idUsuario, @RequestBody InstrumentoSimples instrumento) {
        if(instrumento != null){
            Integer id = service.recuperarUsuario();
            repository.adicionarInstrumento(instrumento.getInstrumento(), instrumento.getNvDominio(),
                    instrumento.getTipoInstrumento(),id);
            return ResponseEntity.created(null).body(true);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity listaInstrumentos(@PathVariable Integer idUsuario){
        Integer id = service.recuperarUsuario();
        List<Instrumento> instrumentos = repository.selecionarPeloIdUsuario(id);
        if (instrumentos.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(instrumentos);
        }
    }

    @GetMapping("buscar/usuario/{idUsuario}")
    public ResponseEntity listaInstrumentosOutroUsuario(@PathVariable Integer idUsuario){
        List<Instrumento> instrumentos = repository.selecionarPeloIdUsuario(idUsuario);
        if (instrumentos.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(instrumentos);
        }
    }

    @GetMapping("/{idUsuario}/{idInstrumento}")
    public ResponseEntity getPorId(@PathVariable Integer idUsuario, @PathVariable int idInstrumento) {
        if (repository.existsById(idInstrumento)) {
            return ResponseEntity.ok(repository.findById(idInstrumento));
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/{idUsuario}/{idInstrumento}")
    public ResponseEntity alterarNivelDominio(@PathVariable Integer idUsuario, @PathVariable int idInstrumento, @RequestBody InstrumentoSimples instrumento){

        if(instrumento.getNvDominio()!= null){
            repository.alterarNvDominioPorId(instrumento.getNvDominio(), idInstrumento);
            return ResponseEntity.ok("Alterado");
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("todas/{idUsuario}/{idInstrumento}")
    public ResponseEntity alterarInstrumento(@PathVariable Integer idUsuario, @PathVariable int idInstrumento, @RequestBody InstrumentoSimples instrumento){

        if(instrumento.getInstrumento() != null){
            repository.alterarInstrumentoPorId(instrumento.getInstrumento(), idInstrumento);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @DeleteMapping("/{idUsuario}/{idInstrumento}")
    public ResponseEntity deletar(@PathVariable Integer idUsuario, @PathVariable Integer idInstrumento){
        if (repository.existsById(idInstrumento)) {
            repository.deleteById(idInstrumento);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuarios/{instrumento}")
    public ResponseEntity getTodosInstrumentos(@PathVariable String instrumento){
        List<Instrumento> instrumetos = repository.selecionarTodosInstrumentos(instrumento);
        if(!instrumetos.isEmpty()){
            return ResponseEntity.ok(instrumetos);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
