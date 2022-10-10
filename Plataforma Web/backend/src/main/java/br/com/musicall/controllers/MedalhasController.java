package br.com.musicall.controllers;

import br.com.musicall.dominios.Medalha;
import br.com.musicall.dominios.RegistroMedalha;
import br.com.musicall.modelos.RegMedalhaModelo;
import br.com.musicall.repositorios.MedalhasRepository;
import br.com.musicall.repositorios.RegMedalhaRepository;
import br.com.musicall.servicos.RegMedalhaService;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/medalhas")
public class MedalhasController {

    @Autowired
    private MedalhasRepository repository;

    @Autowired
    private RegMedalhaService service;

    @PostMapping("/{idUsuario}")
    public ResponseEntity criarMedalhas(@PathVariable Integer idUsuario){
        if(idUsuario != 0){
            repository.adicionarMedalha(LocalDate.now(), idUsuario);
            return ResponseEntity.created(null).build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity getMedalhas(@PathVariable Integer idUsuario){
        Medalha medalha = repository.getPorIdUsuario(idUsuario);
        if(medalha != null){
            return ResponseEntity.ok(medalha);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/{idUsuario}/atualiza")
    public ResponseEntity atualizaMedalhas(@PathVariable Integer idUsuario){
        Medalha medalha = repository.getPorIdUsuario(idUsuario);
        RegMedalhaModelo regMedalha = service.getMedalhas(idUsuario);

        if(medalha != null){
            if(regMedalha.getRegNumConvites() != 3){
                if (medalha.getNumConvites() >= 150) {
                    service.setMedalha(idUsuario, "convites", 3);
                } else if (medalha.getNumConvites() < 50 && medalha.getNumConvites() > 0) {
                    service.setMedalha(idUsuario, "convites", 1);
                } else if (medalha.getNumConvites() == 0) {
                    service.setMedalha(idUsuario, "convites", 0);
                } else {
                    service.setMedalha(idUsuario, "convites", 2);
                }
            }

            if(regMedalha.getRegNumCurtidas() != 3){
                if (medalha.getNumCurtidas() >= 300) {
                    service.setMedalha(idUsuario, "curtidas", 3);
                } else if (medalha.getNumCurtidas() < 150 && medalha.getNumCurtidas() > 0) {
                    service.setMedalha(idUsuario, "curtidas", 1);
                } else if (medalha.getNumCurtidas() == 0) {
                    service.setMedalha(idUsuario, "curtidas", 0);
                } else {
                    service.setMedalha(idUsuario, "curtidas", 2);
                }
            }

            if(regMedalha.getRegNumCurtidas() != 3){
                if (medalha.getNumPesquisas() >= 500) {
                    service.setMedalha(idUsuario, "pesquisas", 3);
                } else if (medalha.getNumPesquisas() < 250 && medalha.getNumConvites() > 0) {
                    service.setMedalha(idUsuario, "pesquisas", 1);
                } else if (medalha.getNumPesquisas() == 0){
                    service.setMedalha(idUsuario, "pesquisas", 0);
                } else {
                    service.setMedalha(idUsuario, "pesquisas", 2);
                }
            }

            if (regMedalha.getRegNumPublicacoes() != 3){
                if (medalha.getNumPublicacoes() >= 200) {
                    service.setMedalha(idUsuario, "publicacoes", 3);
                } else if (medalha.getNumPublicacoes() > 0 && medalha.getNumPublicacoes() < 100) {
                    service.setMedalha(idUsuario, "publicacoes", 1);
                } else if (medalha.getNumPublicacoes() == 0){
                    service.setMedalha(idUsuario, "publicacoes", 0);
                } else {
                    service.setMedalha(idUsuario, "publicacoes", 2);
                }
            }

            if (regMedalha.getRegDataInicio() != 3){
                if ((LocalDate.now().getYear() - medalha.getDataInicio().getYear()) == 2) {
                    service.setMedalha(idUsuario, "tempo", 3);
                } else if ((LocalDate.now().getYear() - medalha.getDataInicio().getYear()) == 1 ) {
                    service.setMedalha(idUsuario, "tempo", 1);
                } else if ((LocalDate.now().getYear() - medalha.getDataInicio().getYear()) == 0) {
                    service.setMedalha(idUsuario, "tempo", 0);
                } else {
                    service.setMedalha(idUsuario, "tempo", 2);
                }
            }

            if (!regMedalha.getRegTodasInfos()){
                service.setMedalha(idUsuario, "infos", 1);
            }

            return ResponseEntity.ok().body(true);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/{idUsuario}/{medalha}")
    public ResponseEntity setMedalha(@PathVariable Integer idUsuario, @PathVariable String medalha){

        Medalha medalhasUsuario = repository.getPorIdUsuario(idUsuario);

        switch (medalha) {
            case "infos":
                repository.alterarTodasInfos(idUsuario);
                return ResponseEntity.ok().body(true);
            case "pesquisas":
                repository.alterarNumPesquisas(medalhasUsuario.getNumPesquisas() + 1, idUsuario);
                return ResponseEntity.ok().body(true);
            case "publicacoes":
                repository.alterarNumPublicacoes(medalhasUsuario.getNumPublicacoes() + 1, idUsuario);
                return ResponseEntity.ok().body(true);
            case "convites":
                repository.alterarNumConvites(medalhasUsuario.getNumConvites() + 1, idUsuario);
                return ResponseEntity.ok().body(true);
            case "curtidas":
                repository.alterarNumCurtidas(medalhasUsuario.getNumCurtidas() + 1, idUsuario);
                return ResponseEntity.ok().body(true);
            default:
                return ResponseEntity.badRequest().build();
        }

    }

}
