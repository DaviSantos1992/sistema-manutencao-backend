package com.manutencao.sistema_manutencao.service;

import com.manutencao.sistema_manutencao.entity.Usuario;
import com.manutencao.sistema_manutencao.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRole())
                .build();
    }

    @Transactional(readOnly = true)
    public java.util.List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario salvar(String username, String password, String role) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Já existe um usuário com o login: " + username);
        }
        Usuario usuario = new Usuario(username, passwordEncoder.encode(password), role);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Long id, String username, String password, String role) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));

        if (!usuario.getUsername().equals(username)
                && usuarioRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Já existe um usuário com o login: " + username);
        }

        usuario.setUsername(username);
        usuario.setRole(role);
        if (password != null && !password.isBlank()) {
            usuario.setPassword(passwordEncoder.encode(password));
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));
    }

    @Transactional
    public void atualizarDarkMode(String username, boolean darkMode) {
        Usuario usuario = buscarPorUsername(username);
        usuario.setDarkMode(darkMode);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void alterarSenha(Long id, String senhaAntiga, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));

        // Valida a senha antiga
        if (!passwordEncoder.matches(senhaAntiga, usuario.getPassword())) {
            throw new RuntimeException("A senha antiga está incorreta.");
        }

        // Atualiza para a nova senha
        usuario.setPassword(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        usuarioRepository.deleteById(id);
    }
}