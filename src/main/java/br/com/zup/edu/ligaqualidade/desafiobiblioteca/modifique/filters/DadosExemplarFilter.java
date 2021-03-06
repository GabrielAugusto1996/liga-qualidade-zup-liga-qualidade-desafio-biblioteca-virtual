package br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.filters;

import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.exceptions.ExemplarNotFoundException;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.DadosExemplar;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.TipoExemplar;

import java.util.Set;

public final class DadosExemplarFilter {

    private DadosExemplarFilter() {}

    public static DadosExemplar findByIdAndTipoExemplar(Set<DadosExemplar> exemplares, Integer idLivro, TipoExemplar tipoExemplar) throws ExemplarNotFoundException {
        return exemplares.stream()
                .filter(exemplar -> idLivro.equals(exemplar.idLivro) && tipoExemplar.equals(exemplar.tipo))
                .findFirst()
                .orElseThrow(() -> new ExemplarNotFoundException("Exemplar not found"));
    }
}
