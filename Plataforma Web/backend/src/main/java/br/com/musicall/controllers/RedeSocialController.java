package br.com.musicall.controllers;

import br.com.musicall.dominios.InfoUsuario;
import br.com.musicall.dominios.RedeSocial;
import br.com.musicall.dominios.Usuario;
import br.com.musicall.repositorios.RedeSocialRepository;
import br.com.musicall.repositorios.UsuarioRepository;
import br.com.musicall.servicos.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sociais")
@CrossOrigin(origins = {"http://localhost:3000", "https://bandtec.github.io"})
public class RedeSocialController {

    @Autowired
    private RedeSocialRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService service;

    @PostMapping("/{idUsuario}")
    public ResponseEntity criar(@PathVariable Integer idUsuario, @RequestBody RedeSocial novaRedeSocial) {
        repository.adicionarRedeSocial(novaRedeSocial.getFacebook(),novaRedeSocial.getInstagram(), novaRedeSocial.getTwitter());
        RedeSocial redeSocial = repository.getRedeSocialSegundoParametros(novaRedeSocial.getFacebook(),novaRedeSocial.getInstagram(), novaRedeSocial.getTwitter());
        usuarioRepository.updateRedeSocial(redeSocial.getIdRedeSocial(), idUsuario);
        return ResponseEntity.created(null).body(redeSocial.getIdRedeSocial());
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity listarRedeSocial(@PathVariable Integer idUsuario){
        Integer id = service.recuperarUsuario();
        RedeSocial redeSocial = repository.getPorId(usuarioRepository.getOne(id).getRedeSocial().getIdRedeSocial());
        if (redeSocial == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(redeSocial);
        }
    }

    @PutMapping("/{idUsuario}/{idRedeSocial}/{redeSocial}")
    public ResponseEntity alterarPorId(@PathVariable Integer idUsuario, @PathVariable int idRedeSocial, @PathVariable String redeSocial, @RequestBody RedeSocial endereco) {
        if (repository.existsById(idRedeSocial)) {
            switch (redeSocial){
                case "facebook":
                    repository.alterarFacebookPorId(endereco.getFacebook(), idRedeSocial);
                    return ResponseEntity.ok().build();
                case "instagram":
                    repository.alterarInstagramPorId(endereco.getFacebook(), idRedeSocial);
                    return ResponseEntity.ok().build();
                case  "twitter":
                    repository.alterarTwitterPorId(endereco.getFacebook(), idRedeSocial);
                    return ResponseEntity.ok().build();
                default:
                    return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("todas/{idUsuario}/{idRedeSocial}")
    public ResponseEntity alterarTodasInfosUsuario(@PathVariable Integer idUsuario, @PathVariable Integer idRedeSocial, @RequestBody RedeSocial redeSocial) {
        if(redeSocial != null){
            repository.alterarTodasPorId(redeSocial.getFacebook(), redeSocial.getInstagram(), redeSocial.getTwitter(), idRedeSocial);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{idRedeSocial}")
    public ResponseEntity deletar(@PathVariable Integer idRedeSocial){
        if (repository.existsById(idRedeSocial)) {
            repository.apagarConteudoPorId(idRedeSocial);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }

}
