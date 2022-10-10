package br.com.musicall.servicos;

import br.com.musicall.modelos.RegMedalhaModelo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "apiRegistros", url = "http://localhost:8080/registros")
public interface RegMedalhaService {

    @PostMapping("/{idUsuario}")
    void criarMedalhas(@PathVariable Integer idUsuario);

    @GetMapping("/{idUsuario}")
    RegMedalhaModelo getMedalhas(@PathVariable Integer idUsuario);

    @PutMapping("/{idUsuario}/{medalha}/{nivel}")
    void setMedalha(@PathVariable Integer idUsuario, @PathVariable String medalha, @PathVariable Integer nivel);
}
