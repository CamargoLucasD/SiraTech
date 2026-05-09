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
    private String perfil;

    public Usuario() {}

    public Usuario(String login, String senha, String perfil) {
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    public int getId() { return id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
}