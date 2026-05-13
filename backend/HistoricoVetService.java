package backend;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.*;

public class HistoricoVetService {

    public HistoricoVet salvar(HistoricoVet h) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            if (h.getId() == 0) session.persist(h);
            else session.merge(h);
            session.getTransaction().commit();
        } catch (Exception e) { e.printStackTrace(); }
        return h;
    }

    public List<HistoricoVet> listarPorFazenda(int fazendaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<HistoricoVet> q = session.createQuery(
                    "FROM HistoricoVet h WHERE h.fazenda.id = :fid ORDER BY h.dataAtendimento DESC", HistoricoVet.class);
            q.setParameter("fid", fazendaId);
            return q.list();
        } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public List<HistoricoVet> listarPorAnimal(int animalId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<HistoricoVet> q = session.createQuery(
                    "FROM HistoricoVet h WHERE h.animal.id = :aid ORDER BY h.dataAtendimento DESC", HistoricoVet.class);
            q.setParameter("aid", animalId);
            return q.list();
        } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean excluir(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            HistoricoVet h = session.get(HistoricoVet.class, id);
            if (h != null) { session.remove(h); session.getTransaction().commit(); return true; }
            return false;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
