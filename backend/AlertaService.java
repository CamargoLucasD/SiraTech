package backend;

import java.util.*;

public class AlertaService {
    private List<Alerta> alertas = new ArrayList<>();
    private int proximoId = 1;
    private List<Runnable> listeners = new ArrayList<>();

    public AlertaService() {}

    public Alerta gerarAlerta(Alerta.Tipo tipo, Animal animal, String mensagem) {
        Alerta alerta = new Alerta(proximoId++, tipo, animal, mensagem);
        alertas.add(alerta);
        notificarListeners();
        return alerta;
    }

    public List<Alerta> listarAtivos() {
        List<Alerta> ativos = new ArrayList<>();
        for (Alerta a : alertas) {
            if (!a.isResolvido()) ativos.add(a);
        }
        return ativos;
    }

    public List<Alerta> listarTodos() {
        return Collections.unmodifiableList(alertas);
    }

    public boolean resolverAlerta(int id) {
        for (Alerta a : alertas) {
            if (a.getId() == id) {
                a.setResolvido(true);
                notificarListeners();
                return true;
            }
        }
        return false;
    }

    public void resolverTodos() {
        alertas.forEach(a -> a.setResolvido(true));
        notificarListeners();
    }

    public int totalAtivos() {
        return (int) alertas.stream().filter(a -> !a.isResolvido()).count();
    }

    public void addListener(Runnable listener) { listeners.add(listener); }
    private void notificarListeners() { listeners.forEach(Runnable::run); }
}
