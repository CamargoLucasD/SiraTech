package backend;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "login", unique = true)
    private String login;

    @Column(name = "senha")
    private String senha;

    @Column(name = "perfil")
    private String perfil; // "Administrador" ou "Operador"

    @Column(name = "nome_completo")
    private String nomeCompleto;

    @Column(name = "ativo")
    private boolean ativo = true;

    public Usuario() {}

    public Usuario(String login, String senha, String perfil, String nomeCompleto) {
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
        this.nomeCompleto = nomeCompleto;
        this.ativo = true;
    }

    // backward compat
    public Usuario(String login, String senha, String perfil) {
        this(login, senha, perfil, login);
    }

    public int getId() { return id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
    public String getNomeCompleto() { return nomeCompleto != null ? nomeCompleto : login; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public boolean isAdmin() { return "Administrador".equals(perfil); }
}
