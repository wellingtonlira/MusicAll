package br.com.musicall.servicos;

import br.com.musicall.dominios.Medalha;
import br.com.musicall.modelos.MedalhaModelo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "apiMedalha", url = "http://localhost:8080/medalhas")
public interface MedalhaService {

    @PostMapping("/{idUsuario}")
    void criarMedalhas(@PathVariable Integer idUsuario);

    @GetMapping("/{idUsuario}")
    MedalhaModelo getMedalhas(@PathVariable Integer idUsuario);

    @PutMapping("/{idUsuario}/{medalha}")
    void setMedalha(@PathVariable Integer idUsuario, @PathVariable String medalha);

    @PutMapping("/{idUsuario}/atualiza")
    void atualizaMedalhas(@PathVariable Integer idUsuario);
}
