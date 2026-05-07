package backend;

import java.util.*;

public class FazendaService {
    private List<Fazenda> fazendas = new ArrayList<>();
    private int proximoId = 1;

    public FazendaService() {
        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        Fazenda f = new Fazenda(proximoId++, "Fazenda Boi Verde", "João Silva",
                "Votorantim", "SP", -23.5678, -47.4321, 2000);
        f.setAreaTotal(500);
        f.setAreaMonitorada(320);
        f.addLote(new Lote(1, "Lote A", 120));
        f.addLote(new Lote(2, "Lote B", 100));
        f.addLote(new Lote(3, "Lote C", 80));
        fazendas.add(f);
    }

    public Fazenda cadastrar(Fazenda fazenda) {
        fazenda.setId(proximoId++);
        fazendas.add(fazenda);
        return fazenda;
    }

    public List<Fazenda> listarTodas() {
        return Collections.unmodifiableList(fazendas);
    }

    public Optional<Fazenda> buscarPorId(int id) {
        return fazendas.stream().filter(f -> f.getId() == id).findFirst();
    }

    public Fazenda getFazendaPrincipal() {
        return fazendas.isEmpty() ? null : fazendas.get(0);
    }

    public boolean atualizar(Fazenda fazenda) {
        for (int i = 0; i < fazendas.size(); i++) {
            if (fazendas.get(i).getId() == fazenda.getId()) {
                fazendas.set(i, fazenda);
                return true;
            }
        }
        return false;
    }
}
