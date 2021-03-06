package br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique;

import br.com.zup.edu.ligaqualidade.desafiobiblioteca.DadosDevolucao;
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
import static java.util.Objects.nonNull;

public class EmprestimoHandler {
    Set<DadosEmprestimo> emprestimos;
    Set<DadosUsuario> usuarios;
    Set<DadosExemplar> exemplares;
    LocalDate dataParaSerConsideradaNaExpiracao;
    Set<DadosDevolucao> devolucoes;

    public EmprestimoHandler(
            Set<DadosUsuario> usuarios,
            Set<DadosEmprestimo> emprestimos,
            Set<DadosExemplar> exemplares,
            LocalDate dataParaSerConsideradaNaExpiracao,
            Set<DadosDevolucao> devolucoes
    ) {
        this.usuarios = usuarios;
        this.emprestimos = emprestimos;
        this.exemplares = exemplares;
        this.dataParaSerConsideradaNaExpiracao = dataParaSerConsideradaNaExpiracao;
        this.devolucoes = devolucoes;
    }

    public Set<EmprestimoConcedido> concedeEmprestimos() {
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

        this.devolverLivros(emprestimosConcedidos);
        return emprestimosConcedidos;
    }

    //TODO: Melhorar o custo do código (Branch de código)
    private void devolverLivros(final Set<EmprestimoConcedido> emprestimosConcedidos) {
        devolucoes.forEach(dadosDevolucao -> emprestimos.forEach(emprestimo -> registrarDevolucao(emprestimosConcedidos, dadosDevolucao, emprestimo)));
    }

    private void registrarDevolucao(final Set<EmprestimoConcedido> emprestimosConcedidos, final DadosDevolucao dadosDevolucao, final DadosEmprestimo emprestimo) {
        final EmprestimoConcedido emprestimoParaDevolver = emprestimosConcedidos.stream()
                .filter(dadosEmprestimo -> dadosDevolucao.idEmprestimo == emprestimo.idPedido)
                .findFirst()
                .orElse(null);

        if (nonNull(emprestimoParaDevolver)) {
            final DadosUsuario usuario = usuarios.stream()
                    .filter(dadosUsuario -> dadosUsuario.idUsuario == emprestimoParaDevolver.idUsuario)
                    .findFirst()
                    .orElse(null);

            if (nonNull(usuario)) {
                emprestimoParaDevolver.registraDevolucao();
            }
        }
    }

    private LocalDate calculateDataPrevistaDevolucao(final int tempo) {
        return now()
                .plusDays(tempo);
    }
}