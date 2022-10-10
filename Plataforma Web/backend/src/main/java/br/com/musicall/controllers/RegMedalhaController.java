package br.com.musicall.controllers;

import br.com.musicall.dominios.RegistroMedalha;
import br.com.musicall.repositorios.RegMedalhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registros")
public class RegMedalhaController {

    @Autowired
    private RegMedalhaRepository repository;


    @PostMapping("/{idUsuario}")
    public ResponseEntity criarMedalhas(@PathVariable Integer idUsuario){
        if(idUsuario != null){
            repository.adicionarRegistroMedalha(idUsuario);
            return ResponseEntity.created(null).body(true);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity getMedalhas(@PathVariable Integer idUsuario){
        RegistroMedalha medalha = repository.getPorIdUsuario(idUsuario);
        if(medalha != null){
            return ResponseEntity.ok(medalha);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/{idUsuario}/{medalha}/{nivel}")
    public ResponseEntity setMedalha(@PathVariable Integer idUsuario, @PathVariable String medalha, @PathVariable Integer nivel){
        switch (medalha) {
            case "infos":
                repository.alterarTodasInfos(idUsuario);
                return ResponseEntity.ok().body(true);
            case "pesquisas":
                repository.alterarNumPesquisas(nivel, idUsuario);
                return ResponseEntity.ok().body(true);
            case "publicacoes":
                repository.alterarNumPublicacoes(nivel, idUsuario);
                return ResponseEntity.ok().body(true);
            case "convites":
                repository.alterarNumConvites(nivel, idUsuario);
                return ResponseEntity.ok().body(true);
            case "curtidas":
                repository.alterarNumCurtidas(nivel, idUsuario);
                return ResponseEntity.ok().body(true);
            case "tempo":
                repository.alterarDataInicio(idUsuario);
                return ResponseEntity.ok().body(true);
            default:
                return ResponseEntity.badRequest().build();
        }

    }


}

