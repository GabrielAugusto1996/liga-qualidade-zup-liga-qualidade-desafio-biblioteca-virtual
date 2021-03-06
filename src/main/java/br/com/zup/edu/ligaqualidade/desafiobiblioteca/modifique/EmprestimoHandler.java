package br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique;

import br.com.zup.edu.ligaqualidade.desafiobiblioteca.DadosEmprestimo;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.EmprestimoConcedido;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.exceptions.EmprestimoBusinessException;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.exceptions.EmprestimoValidationException;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.filters.DadosExemplarFilter;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.filters.DadosUsuarioFilter;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.DadosExemplar;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.DadosUsuario;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static java.time.LocalDate.now;

public class EmprestimoHandler {
    final Set<DadosEmprestimo> emprestimos;
    final Set<DadosUsuario> usuarios;
    final Set<DadosExemplar> exemplares;

    public EmprestimoHandler(final Set<DadosUsuario> usuarios, final Set<DadosEmprestimo> emprestimos, final Set<DadosExemplar> exemplares) {
        this.usuarios = usuarios;
        this.emprestimos = emprestimos;
        this.exemplares = exemplares;
    }

    public Set<EmprestimoConcedido> concedeEmprestimos(){
        Set<EmprestimoConcedido> emprestimosConcedidos = new HashSet<>();

        emprestimos.forEach((DadosEmprestimo emprestimo) -> {
            try {
                DadosUsuario usuario = DadosUsuarioFilter.findById(usuarios, emprestimo.idUsuario);
                DadosExemplar exemplar = DadosExemplarFilter.findByIdAndTipoExemplar(exemplares, emprestimo.idLivro, emprestimo.tipoExemplar);

                EmprestimoValidator emprestimoValidator = new EmprestimoValidator(emprestimo, usuario);
                emprestimoValidator.validate();

                LocalDate dataPrevistaDevolucao = calculateDataPrevistaDevolucao(emprestimo.tempo);

                emprestimosConcedidos.add(new EmprestimoConcedido(usuario.idUsuario, exemplar.idExemplar, dataPrevistaDevolucao));
            } catch (EmprestimoValidationException | EmprestimoBusinessException e) {
                System.out.println(e.getMessage());
            }
        });

        return emprestimosConcedidos;
    }

    private LocalDate calculateDataPrevistaDevolucao(final int tempo) {
        return now()
                .plusDays(tempo);
    }
}