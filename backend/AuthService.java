package backend;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.*;

public class AuthService {

    private Usuario usuarioLogado = null;
    private Fazenda fazendaAtiva = null;

    private final Map<String,Integer> tentativas = new HashMap<>();
    private final Map<String,Long> bloqueados    = new HashMap<>();
    private static final int MAX_TENTATIVAS = 5;
    private static final long TEMPO_BLOQUEIO_MS = 5 * 60 * 1000L;

    public AuthService() { criarUsuariosIniciais(); }

    private void criarUsuariosIniciais() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            long total = session.createQuery("SELECT COUNT(u) FROM Usuario u", Long.class).uniqueResult();
            if (total == 0) {
                session.persist(new Usuario("admin",   "12345",   "Administrador", "Administrador"));
                session.persist(new Usuario("gerente", "fazenda", "Operador",      "Gerente"));
            }
            session.getTransaction().commit();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public String tentarLogin(String login, String senha) {
        if (bloqueados.containsKey(login)) {
            long restante = bloqueados.get(login) - System.currentTimeMillis();
            if (restante > 0) return "BLOQUEADO:" + (restante / 1000);
            else { bloqueados.remove(login); tentativas.remove(login); }
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> q = session.createQuery(
                    "FROM Usuario u WHERE u.login = :login AND u.ativo = true", Usuario.class);
            q.setParameter("login", login);
            Usuario u = q.uniqueResult();
            if (u != null && u.getSenha().equals(senha)) {
                usuarioLogado = u;
                tentativas.remove(login);
                bloqueados.remove(login);
                return "OK";
            } else {
                int t = tentativas.getOrDefault(login, 0) + 1;
                tentativas.put(login, t);
                if (t >= MAX_TENTATIVAS) {
                    bloqueados.put(login, System.currentTimeMillis() + TEMPO_BLOQUEIO_MS);
                    tentativas.remove(login);
                    return "BLOQUEADO:300";
                }
                return "FALHA:" + (MAX_TENTATIVAS - t);
            }
        } catch (Exception e) { e.printStackTrace(); return "FALHA:0"; }
    }

    public boolean login(String login, String senha) {
        return "OK".equals(tentarLogin(login, senha));
    }

    public boolean verificarSenha(String senha) {
        return usuarioLogado != null && usuarioLogado.getSenha().equals(senha);
    }

    public boolean cadastrarUsuario(String login, String senha, String perfil, String nomeCompleto) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            long existe = session.createQuery(
                    "SELECT COUNT(u) FROM Usuario u WHERE u.login = :login", Long.class)
                    .setParameter("login", login).uniqueResult();
            if (existe > 0) return false;
            session.beginTransaction();
            session.persist(new Usuario(login, senha, perfil, nomeCompleto));
            session.getTransaction().commit();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean alterarSenha(String login, String novaSenha) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createMutationQuery(
                    "UPDATE Usuario u SET u.senha = :senha WHERE u.login = :login")
                    .setParameter("senha", novaSenha)
                    .setParameter("login", login)
                    .executeUpdate();
            session.getTransaction().commit();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public List<Usuario> listarUsuarios() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Usuario", Usuario.class).list();
        } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean desativarUsuario(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Usuario u = session.get(Usuario.class, id);
            if (u != null) {
                u.setAtivo(false);
                session.merge(u);
                session.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public void setFazendaAtiva(Fazenda f) { this.fazendaAtiva = f; }
    public Fazenda getFazendaAtiva() { return fazendaAtiva; }

    public void logout() { usuarioLogado = null; fazendaAtiva = null; }
    public boolean isLogado() { return usuarioLogado != null; }
    public String getUsuarioLogado() { return usuarioLogado != null ? usuarioLogado.getLogin() : null; }
    public Usuario getUsuarioAtual() { return usuarioLogado; }
    public boolean isAdmin() { return usuarioLogado != null && usuarioLogado.isAdmin(); }
}