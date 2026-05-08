package backend;

import org.hibernate.Session;
import java.util.*;

public class FazendaService {

    public FazendaService() {
        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            long total = session.createQuery("SELECT COUNT(f) FROM Fazenda f", Long.class)
                    .uniqueResult();
            if (total == 0) {
                session.beginTransaction();

                Fazenda f = new Fazenda(0, "Fazenda Boi Verde", "João Silva",
                        "Votorantim", "SP", -23.5678, -47.4321, 2000);
                f.setAreaTotal(500);
                f.setAreaMonitorada(320);

                Lote la = new Lote(0, "Lote A", 120);
                Lote lb = new Lote(0, "Lote B", 100);
                Lote lc = new Lote(0, "Lote C", 80);

                session.persist(la);
                session.persist(lb);
                session.persist(lc);

                f.addLote(la);
                f.addLote(lb);
                f.addLote(lc);

                session.persist(f);
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Fazenda cadastrar(Fazenda fazenda) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(fazenda);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fazenda;
    }

    public List<Fazenda> listarTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Fazenda", Fazenda.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Optional<Fazenda> buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Fazenda.class, id));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Fazenda getFazendaPrincipal() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Fazenda", Fazenda.class)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean atualizar(Fazenda fazenda) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(fazenda);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}