package backend;

import org.hibernate.Session;
import org.hibernate.query.Query;

public class AuthService {

    private String usuarioLogado = null;

    public AuthService() {
        criarUsuariosIniciais();
    }

    private void criarUsuariosIniciais() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            long total = session.createQuery("SELECT COUNT(u) FROM Usuario u", Long.class)
                    .uniqueResult();

            if (total == 0) {
                session.persist(new Usuario("admin", "12345", "Administrador"));
                session.persist(new Usuario("gerente", "fazenda", "Gerente"));
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean login(String usuario, String senha) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> query = session.createQuery(
                    "FROM Usuario u WHERE u.login = :login AND u.senha = :senha", Usuario.class);
            query.setParameter("login", usuario);
            query.setParameter("senha", senha);
            Usuario u = query.uniqueResult();
            if (u != null) {
                usuarioLogado = usuario;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logout() { usuarioLogado = null; }
    public boolean isLogado() { return usuarioLogado != null; }
    public String getUsuarioLogado() { return usuarioLogado; }
}