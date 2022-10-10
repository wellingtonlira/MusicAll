package br.com.musicall.controllers;


import br.com.musicall.classes.*;
import br.com.musicall.dominios.Publicacao;
import br.com.musicall.dominios.Usuario;
import br.com.musicall.modelos.GeneroModelo;
import br.com.musicall.modelos.InstrumentoModelo;
import br.com.musicall.repositorios.PublicacaoRepository;
import br.com.musicall.repositorios.RecebeRepository;
import br.com.musicall.repositorios.UsuarioRepository;
import br.com.musicall.servicos.GeneroService;
import br.com.musicall.servicos.InstrumentoService;
import br.com.musicall.servicos.MedalhaService;
import br.com.musicall.servicos.UsuarioService;
import br.com.musicall.visoes.PublicacaoDivulgada;
import br.com.musicall.visoes.PublicacaoSimples;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RestController
@RequestMapping("/publicacoes")
@CrossOrigin(origins = {"http://localhost:3000", "https://bandtec.github.io"})
public class PublicacaoController implements Subject {

    @Autowired
    private PublicacaoRepository repository;

    @Autowired
    private RecebeRepository recebeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InstrumentoService instrumentoService;

    @Autowired
    private GeneroService generoService;

    @Autowired
    private MedalhaService medalhaService;


    @Autowired
    private UsuarioService usuarioService;

    private FilaObj<Publicacao> fila = new FilaObj<>(50);
    private PilhaObj<Publicacao> pilha = new PilhaObj<>(1);
    private ListaObj<Publicacao> lista = new ListaObj<>(50);
    private ListaObj<PublicacaoSimples> listaSimples = new ListaObj<>(50);
    private Gravacao gravacao = new Gravacao();

    @Override
    public void notifica(Integer idPublicacao, List<Usuario> usuarios) {
        for (Usuario usuario : usuarios) {
            recebeRepository.notificaUsuarios(idPublicacao, usuario.getIdUsuario());
        }
    }

    @Scheduled(fixedRate = 5000)
    void trataRequisicoes(){
        if(!fila.isEmpty()){
            while (!fila.isEmpty()) {
                Publicacao publicacao = fila.poll();
                repository.delete(publicacao);
            }
        }
    }

    @PostMapping
    public ResponseEntity publicar(@RequestBody Publicacao novaPublicacao) {
        Integer idUsuario = usuarioService.recuperarUsuario();
        if (idUsuario == 0 || idUsuario == null) {
            return ResponseEntity.badRequest().build();
        }
        LocalDate dataPublicacao = LocalDate.now();
        repository.publicar(dataPublicacao, 0, novaPublicacao.getTexto(), idUsuario);

        if(pilha.isFull()){
            pilha.pop();
        }

        pilha.push(repository.getPorIdusuarioTextoData(dataPublicacao, novaPublicacao.getTexto(), idUsuario));

        medalhaService.setMedalha(idUsuario, "publicacoes");
        List<Usuario> usuarios = usuarioRepository.pesquisarPorNotificado();
        notifica(novaPublicacao.getIdPublicacao(), usuarios);
        return ResponseEntity.created(null).body(true);
    }

    @DeleteMapping
    public ResponseEntity desfazer(){
        Integer idUsuario = usuarioService.recuperarUsuario();

        if (idUsuario == 0 || idUsuario == null) {
            return ResponseEntity.badRequest().build();
        }

        if(pilha.isEmpty()){
            List<Publicacao> publicacaos = repository.getTodasPorIdUsuario(idUsuario);
            Publicacao p = publicacaos.get(publicacaos.size() - 1);
            pilha.push(p);
        }

        Publicacao p = pilha.pop();

        if(p != null){
            repository.delete(p);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("/curtir/{idPublicacao}")
    public ResponseEntity curtir(@PathVariable Integer idPublicacao) {
        Integer idUsuario = usuarioService.recuperarUsuario();
        if (idUsuario == 0 || idUsuario == null) {
            return ResponseEntity.badRequest().build();
        }
        Publicacao publicacao = repository.getPorIdPublicacao(idPublicacao);
        repository.alterarCurtidas(publicacao.getCurtida() + 1, idPublicacao);
        medalhaService.setMedalha(idUsuario, "curtidas");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/todas")
    public ResponseEntity listarTodasPublicacoes() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (!usuarios.isEmpty()) {

            List<PublicacaoDivulgada> publicacoes = new ArrayList<>();

            for (Usuario usuarioNome : usuarios) {

                List<Publicacao> pbc = repository.getTodasPorUsuarioNome(usuarioNome.getNome());
                List<InstrumentoModelo> instrumentosUsuario = instrumentoService.listaInstrumentos(usuarioNome.getIdUsuario());
                List<GeneroModelo> generosUsuario = generoService.listarGenero(usuarioNome.getIdUsuario());

                for (Publicacao p : pbc) {
                    PublicacaoDivulgada pbcD = new PublicacaoDivulgada(p.getIdPublicacao(), usuarioNome.getIdUsuario(), usuarioNome.getNome(), instrumentosUsuario.get(0).getInstrumento()
                            , usuarioNome.getInfoUsuario().getDataAniversario(), usuarioNome.getInfoUsuario().getCidade(), generosUsuario.get(0).getGeneroMusical(), p.getTexto());

                    publicacoes.add(pbcD);
                }

            }

            return ResponseEntity.ok(publicacoes);
        } else {
            return ResponseEntity.noContent().build();
        }

    }

    @GetMapping
    public ResponseEntity listarPublicacao() {
        Integer idUsuario = usuarioService.recuperarUsuario();
        if (idUsuario == 0 || idUsuario == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Publicacao> publicacoes = repository.getTodasPorIdUsuario(idUsuario);
        if (publicacoes.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(publicacoes);
        }
    }

    @GetMapping("/{idPublicacao}")
    public ResponseEntity getPorId(@PathVariable int idPublicacao) {
        Integer idUsuario = usuarioService.recuperarUsuario();
        if (idUsuario == 0 || idUsuario == null) {
            return ResponseEntity.badRequest().build();
        }
        if (repository.existsById(idPublicacao)) {
            return ResponseEntity.ok(repository.findById(idPublicacao).get());
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/cidade/{valor}")
    public ResponseEntity getPorCidade(@PathVariable String valor) {

        Integer idUsuario = usuarioService.recuperarUsuario();
        if (idUsuario == 0 || idUsuario == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Publicacao> publicacoes = repository.getTodasPorCidade(valor);

        if (!publicacoes.isEmpty()) {
            return ResponseEntity.ok(publicacoes);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/{idPublicacao}")
    public ResponseEntity alterar(@PathVariable Integer idPublicacao, @RequestBody Publicacao publicacao) {
        Integer idUsuario = usuarioService.recuperarUsuario();
        if (idUsuario == 0 || idUsuario == null) {
            return ResponseEntity.badRequest().build();
        }
        if (repository.existsById(idPublicacao)) {
            repository.alterarTexto(publicacao.getTexto(), idPublicacao);
            return ResponseEntity.ok().body(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{idPublicacao}")
    public ResponseEntity deletar(@PathVariable Integer idPublicacao) {
        Integer idUsuario = usuarioService.recuperarUsuario();
        if (idUsuario == 0 || idUsuario == null) {
            return ResponseEntity.badRequest().build();
        }
        if (repository.existsById(idPublicacao)) {
            fila.insert(repository.getPorIdPublicacao(idPublicacao));
            return ResponseEntity.ok().body(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/exportar/csv", produces = "text/csv")
    @ResponseBody
    public ResponseEntity getArquivoCsv() throws IOException {
        List<Publicacao> consulta = repository.findAll();

        if (!consulta.isEmpty()) {
            for (Publicacao publicacao : consulta) {
                lista.adiciona(publicacao);
            }
            String arquivo = gravacao.gravaCSV(lista);

            HttpHeaders headers = new HttpHeaders();

            headers.add("Content-Disposition", "attachment; filename=publicacao.csv");

            return new ResponseEntity(arquivo, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value = "/exportar/txt", produces = "application/pdf")
    @ResponseBody
    public ResponseEntity getArquivoTxt() throws IOException {
        List<PublicacaoSimples> consulta = repository.procurarTodosSimples();

        if (!consulta.isEmpty()) {
            for (PublicacaoSimples publicacao : consulta) {
                listaSimples.adiciona(publicacao);
            }
            String arquivo = gravacao.gravaTxt(listaSimples);

            HttpHeaders headers = new HttpHeaders();

            headers.add("Content-Disposition", "attachment; filename=publicacao.txt");

            return new ResponseEntity(arquivo, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.noContent().build();
        }

    }

    @PostMapping(value = "/enviar")
    @ResponseBody
    public ResponseEntity importarRegistro(@RequestParam("arquivo") MultipartFile arquivo) throws IOException {
        if (arquivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo não enviado!");
        }
        Integer idUsuario = usuarioService.recuperarUsuario();
        if (idUsuario == 0 || idUsuario == null) {
            return ResponseEntity.badRequest().build();
        }

        byte[] conteudo = arquivo.getBytes();
        Path path = Paths.get(arquivo.getOriginalFilename());
        Files.write(path, conteudo);

        FileReader arquivoLido;
        Scanner leitor;
        String reg = "";
        String tipoReg = "";
        String publicacao;
        arquivoLido = new FileReader(arquivo.getOriginalFilename());
        leitor = new Scanner(arquivoLido);
        while (leitor.hasNext()) {
            reg = leitor.nextLine();
            tipoReg = reg.substring(0, 2);

            if (tipoReg.equals("01")) {
                publicacao = reg.substring(102, 357).trim();

                if(pilha.isFull()){
                    pilha.pop();
                }
                LocalDate data = LocalDate.now();
                repository.publicar(data, 0, publicacao, idUsuario);
                pilha.push(repository.getPorIdusuarioTextoData(data, publicacao, idUsuario));
            }
        }
        return ResponseEntity.created(null).build();
    }

    @GetMapping(value = "/exportar/layout", produces = "application/pdf")
    @ResponseBody
    public ResponseEntity getArquivoLayout() throws IOException {

        File xlxs = new File("src/main/resources/static/documento-de-layout.xlsx");

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition", "attachment; filename=documento-de-layout.xlsx");

        return new ResponseEntity(Files.readAllBytes(xlxs.toPath()), headers, HttpStatus.OK);
    }

}
