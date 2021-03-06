package br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.filters;

import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.exceptions.UserNotFoundException;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.DadosUsuario;

import java.util.Set;

public final class DadosUsuarioFilter {

    private DadosUsuarioFilter() {}

    public static DadosUsuario findById(final Set<DadosUsuario> usuarios, final Integer idUsuario) throws UserNotFoundException {
        return usuarios.stream()
                .filter(dadosUsuario -> idUsuario.equals(dadosUsuario.idUsuario)).findFirst()
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}