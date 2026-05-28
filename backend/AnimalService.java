package backend;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.*;

public class AnimalService {

    public AnimalService() {
        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        // Só insere dados iniciais se não houver nenhum animal E houver uma fazenda cadastrada
        if (totalAnimais() > 0) return;

        Fazenda fazenda = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            fazenda = session.createQuery("FROM Fazenda f ORDER BY f.id ASC", Fazenda.class)
                    .setMaxResults(1).uniqueResult();
        } catch (Exception e) { e.printStackTrace(); }

        if (fazenda == null) return; // sem fazenda, não insere

        final int fazendaId   = fazenda.getId();
        final String fazNome  = fazenda.getNome();

        Animal[] iniciais = {
            new Animal(0, "Mimosa",  "Nelore",  "A-012", "Femea", 420, "Lote A"),
            new Animal(0, "Estrela", "Gir",     "A-007", "Femea", 380, "Lote A"),
            new Animal(0, "Flor",    "Angus",   "A-018", "Femea", 450, "Lote B"),
            new Animal(0, "Bela",    "Brahman", "A-023", "Femea", 395, "Lote B"),
            new Animal(0, "Nuvem",   "Nelore",  "A-002", "Femea", 410, "Lote A"),
            new Animal(0, "Rosa",    "Senepol", "A-031", "Femea", 360, "Lote C"),
        };

        for (Animal a : iniciais) {
            a.setFazendaId(fazendaId);
            a.setFazendaNome(fazNome);
            cadastrar(a);
        }
    }

    public Animal cadastrar(Animal animal) {
        // Valida brinco duplicado antes de tentar inserir
        if (animal.getNumeroBrinco() != null && !animal.getNumeroBrinco().isBlank()) {
            Optional<Animal> existente = buscarPorBrinco(animal.getNumeroBrinco());
            if (existente.isPresent()) {
                throw new IllegalArgumentException(
                    "Já existe um animal cadastrado com o brinco: " + animal.getNumeroBrinco());
            }
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(animal);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar animal: " + e.getMessage(), e);
        }
        return animal;
    }

    public Optional<Animal> buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Animal.class, id));
        } catch (Exception e) { e.printStackTrace(); return Optional.empty(); }
    }

    public Optional<Animal> buscarPorBrinco(String brinco) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Animal> q = session.createQuery(
                    "FROM Animal a WHERE LOWER(a.numeroBrinco) = LOWER(:brinco)", Animal.class);
            q.setParameter("brinco", brinco);
            return Optional.ofNullable(q.uniqueResult());
        } catch (Exception e) { e.printStackTrace(); return Optional.empty(); }
    }

    public List<Animal> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Animal", Animal.class).list();
        } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public List<Animal> listarPorFazenda(int fazendaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Animal> q = session.createQuery(
                    "FROM Animal a WHERE a.fazendaId = :fid", Animal.class);
            q.setParameter("fid", Integer.valueOf(fazendaId));
            List<Animal> result = q.list();
            return result;
        } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public List<Animal> listarAtivosPorFazenda(int fazendaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Animal> q = session.createQuery(
                    "FROM Animal a WHERE a.fazendaId = :fid AND a.status = 'Ativo'", Animal.class);
            q.setParameter("fid", Integer.valueOf(fazendaId));
            return q.list();
        } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public List<Animal> listarAtivos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Animal a WHERE a.status = 'Ativo'", Animal.class).list();
        } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public List<Animal> buscarPorTermo(String termo, int fazendaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String t = "%" + termo.toLowerCase() + "%";
            Query<Animal> q = session.createQuery(
                    "FROM Animal a WHERE a.fazendaId = :fid AND (LOWER(a.nome) LIKE :t OR LOWER(a.numeroBrinco) LIKE :t OR LOWER(a.lote) LIKE :t OR LOWER(a.raca) LIKE :t)", Animal.class);
            q.setParameter("fid", fazendaId);
            q.setParameter("t", t);
            return q.list();
        } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean atualizar(Animal animal) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(animal);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean remover(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Animal animal = session.get(Animal.class, id);
            if (animal != null) {
                session.remove(animal);
                session.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public int totalAnimais() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(a) FROM Animal a", Long.class)
                    .uniqueResult().intValue();
        } catch (Exception e) { e.printStackTrace(); return 0; }
    }

    public int totalAnimaisPorFazenda(int fazendaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT COUNT(a) FROM Animal a WHERE a.fazendaId = :fid", Long.class)
                    .setParameter("fid", fazendaId).uniqueResult().intValue();
        } catch (Exception e) { e.printStackTrace(); return 0; }
    }
}