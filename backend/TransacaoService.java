package backend;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.*;

public class TransacaoService {

    public Transacao salvar(Transacao t) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            if (t.getId() == 0) session.persist(t);
            else session.merge(t);
            session.getTransaction().commit();
        } catch (Exception e) { e.printStackTrace(); }
        return t;
    }

    public List<Transacao> listarPorFazenda(int fazendaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Transacao> q = session.createQuery(
                    "FROM Transacao t WHERE t.fazenda.id = :fid ORDER BY t.dataTransacao DESC", Transacao.class);
            q.setParameter("fid", fazendaId);
            return q.list();
        } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public double totalVendasPorFazenda(int fazendaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Double total = session.createQuery(
                    "SELECT SUM(t.valor) FROM Transacao t WHERE t.fazenda.id = :fid AND t.tipo = 'Venda'", Double.class)
                    .setParameter("fid", fazendaId).uniqueResult();
            return total != null ? total : 0.0;
        } catch (Exception e) { e.printStackTrace(); return 0.0; }
    }

    public double totalComprasPorFazenda(int fazendaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Double total = session.createQuery(
                    "SELECT SUM(t.valor) FROM Transacao t WHERE t.fazenda.id = :fid AND t.tipo = 'Compra'", Double.class)
                    .setParameter("fid", fazendaId).uniqueResult();
            return total != null ? total : 0.0;
        } catch (Exception e) { e.printStackTrace(); return 0.0; }
    }

    public boolean excluir(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Transacao t = session.get(Transacao.class, id);
            if (t != null) { session.remove(t); session.getTransaction().commit(); return true; }
            return false;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
