package backend;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ObsidianExportService {

    private static final String VAULT =
            "C:\\Users\\User\\Desktop\\windows\\Programas\\siratech\\SIRATECH-OBSIDIAN";

    public static void exportarAnimal(int id, String nome, String status) {
        try {
            String md = """
                    # Animal %d - %s

                    Status: %s
                    """.formatted(id, nome, status);

            // ✅ Use Paths.get() com múltiplos argumentos — ele adiciona o separador correto
            Path path = Paths.get(VAULT, "Animais", "animal-" + id + ".md");

            // ✅ Garante que a pasta "Animais" existe antes de escrever
            Files.createDirectories(path.getParent());

            Files.writeString(path, md);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}