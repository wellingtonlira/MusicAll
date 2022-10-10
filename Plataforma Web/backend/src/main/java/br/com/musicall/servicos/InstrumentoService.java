package br.com.musicall.servicos;

import br.com.musicall.dominios.Instrumento;
import br.com.musicall.modelos.InstrumentoModelo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "apiInstrumento", url = "http://localhost:8080/instrumentos")
public interface InstrumentoService {

    @PostMapping("/{idUsuario}")
    void criar(@PathVariable Integer idUsuario, @RequestBody InstrumentoModelo instrumento);

    @GetMapping("/{idUsuario}")
    List<InstrumentoModelo> listaInstrumentos(@PathVariable Integer idUsuario);

    @GetMapping("buscar/usuario/{idUsuario}")
    List<InstrumentoModelo> listaInstrumentosOutroUsuario(@PathVariable Integer idUsuario);

    @GetMapping("/{idUsuario}/{idInstrumento}")
    InstrumentoModelo unicoInstrumento(@PathVariable Integer idUsuario, @PathVariable Integer id, @PathVariable Integer idInstrumento);

    @PutMapping("/{idUsuario}/{idInstrumento}")
    String alterarNivelDominio(@PathVariable Integer idUsuario, @PathVariable Integer idInstrumento, @RequestBody InstrumentoModelo instrumento);

    @DeleteMapping("/{idUsuario}/{idInstrumento}")
    String deletar(@PathVariable Integer idUsuario, @PathVariable Integer idInstrumento);

    @GetMapping("/usuarios/{instrumento}")
    List<Instrumento> getTodosInstrumentos(@PathVariable String instrumento);

}
