package backend;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.ArrayList;
import java.util.List;

public class LoteService {

    /** Todos os lotes do banco. */
    public List<Lote> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Lote", Lote.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /** Lotes ativos de uma fazenda específica, filtrados pelo fazendaId. */
    public List<Lote> listarAtivosPorFazenda(int fazendaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Lote> q = session.createQuery(
                "FROM Lote l WHERE l.fazendaId = :fid AND l.status = 'Ativo'", Lote.class);
            q.setParameter("fid", fazendaId);
            return q.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /** Todos os lotes ativos (sem filtro de fazenda). */
    public List<Lote> listarAtivos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Lote l WHERE l.status = 'Ativo'", Lote.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Lote buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Lote.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean atualizar(Lote lote) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(lote);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean remover(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Lote lote = session.get(Lote.class, id);
            if (lote != null) {
                session.remove(lote);
                session.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
