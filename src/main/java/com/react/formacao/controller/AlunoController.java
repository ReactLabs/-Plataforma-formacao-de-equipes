package com.react.formacao.controller;


import com.react.formacao.entity.Aluno;
import com.react.formacao.entity.Turma;
import com.react.formacao.repository.AlunoRepository;
import com.react.formacao.repository.TurmaRepository;
import com.react.formacao.service.QuestionarioPerguntas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Controller
public class AlunoController {

    @Autowired
    AlunoRepository alunoRepository;


    @Autowired
    private TurmaRepository turmaRepository;

    @RequestMapping(value = { "aluno/questionario/{idTurma}"}, method = RequestMethod.GET)
    public String questionario(@PathVariable Long idTurma,  Model model){


        Turma turma = this.turmaRepository.findByIdTurma(idTurma);


        if(!turma.isAberta()){      //se a turma nao ta aberta
            model.addAttribute("mensagem", "A turma que você tentou entrar já não está mais aberta");
            return("homePageClear");
        }
        QuestionarioPerguntas questionarioPerguntas = new QuestionarioPerguntas();
        questionarioPerguntas.setId_turma(turma);
        //questionarioPerguntas.setSenha(turma.getSenha());
        model.addAttribute("aluno", questionarioPerguntas);
        return "aluno_form";
    }

    @RequestMapping(value = { "/aluno/form"}, method = RequestMethod.POST)
    public String receberAluno(@ModelAttribute QuestionarioPerguntas questionarioPerguntas, Model model){


        //se a senha informada pelo aluno é diferente da turma
        if (!(questionarioPerguntas.getSenha().equals(questionarioPerguntas.getId_turma().getSenha()))){
            model.addAttribute("mensagem","A senha informada está incorreta");
            model.addAttribute("aluno", questionarioPerguntas);
            return "aluno_form";
        }


        String tipo = "";
        try {
            Aluno aluno =  new Aluno();
            aluno.setNome(questionarioPerguntas.getNome());
            aluno.setTipoSocial(questionarioPerguntas.definir());
            tipo = aluno.getTipoSocial().getTipoSocail();
            aluno.setidTurma(questionarioPerguntas.getId_turma());
            alunoRepository.save(aluno);

        }catch (Exception e){
            e.printStackTrace();
            return "500";
        }


        model.addAttribute("mensagem", "Pronto, seu tipo caracteristico é " + tipo +  " agora você precisa apenas aguardar");
        return "homePageClear";
    }

    @GetMapping(value = "/aluno/excluir/{id}")
    public String excluir(@PathVariable Long id){
        Aluno aluno = this.alunoRepository.findById(id).orElse(null);

        if(aluno == null){
            return "redirect:turma/index";
        }
        Long idRetorno = aluno.getidTurma().getIdTurma();
        this.alunoRepository.delete(aluno);

        return "redirect:/turma/visualizar/" + idRetorno;
    }


    @GetMapping(value = "/aluno/editar/{id}")
    public String editar(@PathVariable Long id, Model model){
        Aluno aluno = this.alunoRepository.findById(id).orElse(null);

        if(aluno == null){
            return "redirect:/turma/index";
        }

        model.addAttribute("aluno",aluno);
        return "editar_aluno";

    }

    @GetMapping("/aluno/edit/{id}")
    public String editarPost(@PathVariable Long id, @RequestParam String nomeAluno, Model model){
        Aluno alunoBanco = this.alunoRepository.findById(id).orElse(null);

        if(alunoBanco == null){
            return "redirect:/turma/index";
        }

        alunoBanco.setNome(nomeAluno);
        Long idRetorno = alunoBanco.getidTurma().getIdTurma();
        this.alunoRepository.save(alunoBanco);


        return "redirect:/turma/visualizar/" + idRetorno;
    }

    /*editar equipe do aluno*/
    @GetMapping(value = "/aluno/editar/equipe/{id}")
    public String editarEquipe(@PathVariable Long id, Model model){
        Aluno aluno = this.alunoRepository.findById(id).orElse(null);

        if(aluno == null){
            return "redirect:/turma/index";
        }

        model.addAttribute("aluno",aluno);
        return "editar_aluno_equipe";

    }

    @GetMapping("/aluno/edit/equipe/{id}")
    public String editarPost(@PathVariable Long id, @RequestParam int idEquipe, Model model){
        Aluno alunoBanco = this.alunoRepository.findById(id).orElse(null);

        if(alunoBanco == null){
            return "redirect:/turma/index";
        }

        alunoBanco.setEquipe(idEquipe);
        this.alunoRepository.save(alunoBanco);


        return "redirect:/";
    }

}
