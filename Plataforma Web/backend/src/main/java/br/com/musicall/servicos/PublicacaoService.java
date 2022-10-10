package br.com.musicall.servicos;

import br.com.musicall.modelos.PublicacaoModelo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "apiPublicacao", url = "http://localhost:8080/publicacoes")
public interface PublicacaoService {

    @PostMapping
    void publicar(@RequestBody PublicacaoModelo publicacao);

    @GetMapping
    List<PublicacaoModelo> listarPublicacao();

    @GetMapping("/cidade/{valor}")
    List<PublicacaoModelo> getPorCidade(@PathVariable String valor);

    @GetMapping("/{idPublicacao}")
    PublicacaoModelo getPorId(@PathVariable Integer idPublicacao);

    @PutMapping("/{idPublicacao}")
    void alterar(@PathVariable Integer idPublicacao, @RequestBody PublicacaoModelo publicacao);

    @DeleteMapping("/{idPublicacao}")
    void deletar(@PathVariable Integer idPublicacao);
}
