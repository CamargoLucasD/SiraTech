package backend;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration config = new Configuration();
                config.configure("hibernate.cfg.xml");

                // Resolve o caminho do banco via propriedade do sistema
                // Passado pelo .bat como: java -DDB_PATH="C:/caminho/siratech.db"
                String dbPath = System.getProperty("DB_PATH", "siratech.db");
                config.setProperty("hibernate.connection.url", "jdbc:sqlite:" + dbPath);

                // Entidades existentes
                config.addAnnotatedClass(Animal.class);
                config.addAnnotatedClass(Colar.class);
                config.addAnnotatedClass(Localizacao.class);
                config.addAnnotatedClass(Lote.class);
                config.addAnnotatedClass(Fazenda.class);
                config.addAnnotatedClass(Alerta.class);
                config.addAnnotatedClass(Usuario.class);

                // Novas entidades
                config.addAnnotatedClass(Vacina.class);
                config.addAnnotatedClass(HistoricoVet.class);
                config.addAnnotatedClass(Transacao.class);

                sessionFactory = config.buildSessionFactory();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Erro ao iniciar Hibernate: " + e.getMessage());
            }
        }
        return sessionFactory;
    }

    public static void fechar() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}