package backend;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private Map<String, String> usuarios = new HashMap<>();
    private String usuarioLogado = null;

    public AuthService() {
        usuarios.put("admin",   "12345");
        usuarios.put("gerente", "fazenda");
    }

    public boolean login(String usuario, String senha) {
        if (usuarios.containsKey(usuario) && usuarios.get(usuario).equals(senha)) {
            usuarioLogado = usuario;
            return true;
        }
        return false;
    }

    public void logout() { usuarioLogado = null; }
    public boolean isLogado() { return usuarioLogado != null; }
    public String getUsuarioLogado() { return usuarioLogado; }
}
