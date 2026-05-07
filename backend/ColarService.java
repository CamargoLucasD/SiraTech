package backend;

import java.util.*;

public class ColarService {
    private List<Colar> colares = new ArrayList<>();

    public ColarService() {
        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        colares.add(new Colar("C-01", 92,  "Forte", 5));
        colares.add(new Colar("C-02", 78,  "Forte", 5));
        colares.add(new Colar("C-04", 87,  "Forte", 5));
        colares.add(new Colar("C-07", 100, "Forte", 5));
        colares.add(new Colar("C-09", 65,  "Medio", 5));
        colares.add(new Colar("C-11", 55,  "Medio", 5));
        colares.add(new Colar("C-14", 100, "Forte", 5));
        colares.add(new Colar("C-15", 14,  "Fraco", 5));
        colares.add(new Colar("C-18", 18,  "Medio", 5));
        for (Colar c : colares) {
            if (!c.getId().equals("C-07") && !c.getId().equals("C-14")) {
                c.setDisponivel(false);
            }
        }
    }

    public List<Colar> listarDisponiveis() {
        List<Colar> disp = new ArrayList<>();
        for (Colar c : colares) {
            if (c.isDisponivel()) disp.add(c);
        }
        return disp;
    }

    public List<Colar> listarTodos() {
        return Collections.unmodifiableList(colares);
    }

    public Optional<Colar> buscarPorId(String id) {
        return colares.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public boolean vincularAoAnimal(String colarId, Animal animal) {
        Optional<Colar> opt = buscarPorId(colarId);
        if (opt.isPresent() && opt.get().isDisponivel()) {
            Colar c = opt.get();
            c.setDisponivel(false);
            animal.setColar(c);
            return true;
        }
        return false;
    }

    public void liberarColar(String colarId) {
        buscarPorId(colarId).ifPresent(c -> c.setDisponivel(true));
    }

    public List<Colar> colaresBateriaBaixa(int limiar) {
        List<Colar> lista = new ArrayList<>();
        for (Colar c : colares) {
            if (c.getBateria() <= limiar) lista.add(c);
        }
        return lista;
    }
}
