package backend;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.*;

public class ColarService {

    public ColarService() {
        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            long total = session.createQuery("SELECT COUNT(c) FROM Colar c", Long.class)
                    .uniqueResult();
            if (total == 0) {
                session.beginTransaction();
                Colar c1  = new Colar("C-01", 92,  "Forte", 5);
                Colar c2  = new Colar("C-02", 78,  "Forte", 5);
                Colar c4  = new Colar("C-04", 87,  "Forte", 5);
                Colar c7  = new Colar("C-07", 100, "Forte", 5);
                Colar c9  = new Colar("C-09", 65,  "Medio", 5);
                Colar c11 = new Colar("C-11", 55,  "Medio", 5);
                Colar c14 = new Colar("C-14", 100, "Forte", 5);
                Colar c15 = new Colar("C-15", 14,  "Fraco", 5);
                Colar c18 = new Colar("C-18", 18,  "Medio", 5);

                for (Colar c : Arrays.asList(c1, c2, c4, c7, c9, c11, c14, c15, c18)) {
                    if (!c.getId().equals("C-07") && !c.getId().equals("C-14")) {
                        c.setDisponivel(false);
                    }
                    session.persist(c);
                }
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Colar> listarDisponiveis() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Colar c WHERE c.disponivel = true", Colar.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Colar> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Colar", Colar.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Optional<Colar> buscarPorId(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Colar.class, id));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean vincularAoAnimal(String colarId, Animal animal) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Colar colar = session.get(Colar.class, colarId);
            if (colar != null && colar.isDisponivel()) {
                colar.setDisponivel(false);
                animal.setColar(colar);
                session.merge(colar);
                session.merge(animal);
                session.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void liberarColar(String colarId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Colar colar = session.get(Colar.class, colarId);
            if (colar != null) {
                colar.setDisponivel(true);
                session.merge(colar);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Colar> colaresBateriaBaixa(int limiar) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Colar> query = session.createQuery(
                    "FROM Colar c WHERE c.bateria <= :limiar", Colar.class);
            query.setParameter("limiar", limiar);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}