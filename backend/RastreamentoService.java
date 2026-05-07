package backend;

import java.time.LocalDateTime;
import java.util.*;

public class RastreamentoService {
    private List<Localizacao> historico = new ArrayList<>();
    private int proximoId = 1;
    private GeofenceService geofenceService;
    private FazendaService fazendaService;
    private AlertaService alertaService;

    public RastreamentoService(GeofenceService geo, FazendaService faz, AlertaService alert) {
        this.geofenceService = geo;
        this.fazendaService  = faz;
        this.alertaService   = alert;
    }

    public Localizacao registrarPosicao(Animal animal, double latitude, double longitude) {
        Localizacao loc = new Localizacao(proximoId++, latitude, longitude, animal);
        loc.setTimestamp(LocalDateTime.now());

        Fazenda fazenda = fazendaService.getFazendaPrincipal();
        if (fazenda != null) {
            String statusGeo = geofenceService.verificarStatus(loc, fazenda);
            loc.setStatus(statusGeo);
            if ("Fora".equals(statusGeo)) {
                alertaService.gerarAlerta(
                        Alerta.Tipo.FORA_DA_AREA, animal,
                        animal.getNome() + " saiu da área! Brinco: " + animal.getNumeroBrinco());
            }
        }

        historico.add(loc);
        if (animal.getColar() != null) {
            animal.getColar().setUltimaLocalizacao(loc);
        }
        return loc;
    }

    public List<Localizacao> buscarHistoricoPorAnimal(Animal animal) {
        List<Localizacao> resultado = new ArrayList<>();
        for (Localizacao l : historico) {
            if (l.getAnimal() != null && l.getAnimal().getId() == animal.getId()) {
                resultado.add(l);
            }
        }
        resultado.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return resultado;
    }

    public List<Localizacao> buscarUltimasPosicoes(int quantidade) {
        List<Localizacao> copia = new ArrayList<>(historico);
        copia.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return copia.subList(0, Math.min(quantidade, copia.size()));
    }

    public int totalRegistros() { return historico.size(); }
}
