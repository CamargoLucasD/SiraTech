package backend;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.*;

public class VacinaService {

    public Vacina salvar(Vacina vacina) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            if (vacina.getId() == 0) session.persist(vacina);
            else session.merge(vacina);
            session.getTransaction().commit();
        } catch (Exception e) { e.printStackTrace(); }
        return vacina;
    }

    public List<Vacina> listarPorFazenda(int fazendaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Vacina> q = session.createQuery(
                    "FROM Vacina v WHERE v.fazenda.id = :fid ORDER BY v.proximaDose ASC", Vacina.class);
            q.setParameter("fid", fazendaId);
            return q.list();
        } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public List<Vacina> listarPorAnimal(int animalId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Vacina> q = session.createQuery(
                    "FROM Vacina v WHERE v.animal.id = :aid ORDER BY v.dataAplicacao DESC", Vacina.class);
            q.setParameter("aid", animalId);
            return q.list();
        } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public List<Vacina> listarVencendoEm30Dias(int fazendaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            java.time.LocalDate hoje = java.time.LocalDate.now();
            java.time.LocalDate limite = hoje.plusDays(30);
            Query<Vacina> q = session.createQuery(
                    "FROM Vacina v WHERE v.fazenda.id = :fid AND v.proximaDose <= :limite AND v.proximaDose >= :hoje ORDER BY v.proximaDose ASC", Vacina.class);
            q.setParameter("fid", fazendaId);
            q.setParameter("limite", limite);
            q.setParameter("hoje", hoje);
            return q.list();
        } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean excluir(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Vacina v = session.get(Vacina.class, id);
            if (v != null) { session.remove(v); session.getTransaction().commit(); return true; }
            return false;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
