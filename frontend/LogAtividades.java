package frontend;

import java.text.SimpleDateFormat;
import java.util.*;

public class LogAtividades {
    private static final List<String> log = new ArrayList<>();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm:ss");

    public static void registrar(String usuario, String acao) {
        log.add(0, sdf.format(new Date()) + " | " + (usuario != null ? usuario : "?") + " | " + acao);
        if (log.size() > 500) log.remove(log.size() - 1);
    }

    public static List<String> getLogs() { return Collections.unmodifiableList(log); }
}
