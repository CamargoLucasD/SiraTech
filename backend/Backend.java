package siratech.frontend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// ============================================================
//  AGROTECH - BACKEND COMPLETO
//  Contém: Animal, Colar, Fazenda, Localizacao, Alerta
//          + Todos os Services (CRUD + Geofence + Alertas)
// ============================================================

// ─────────────────────────────────────────────────────────────
// MODEL: Animal
// ─────────────────────────────────────────────────────────────
class Animal {
    private int id;
    private String nome;
    private String raca;
    private String numeroBrinco;
    private String sexo;          // "Macho" ou "Femea"
    private double peso;
    private LocalDateTime dataNascimento;
    private String lote;
    private String status;        // "Ativo", "Vendido", "Abatido"
    private String observacoes;
    private Colar colar;

    public Animal() {}

    public Animal(int id, String nome, String raca, String numeroBrinco,
                  String sexo, double peso, String lote) {
        this.id = id;
        this.nome = nome;
        this.raca = raca;
        this.numeroBrinco = numeroBrinco;
        this.sexo = sexo;
        this.peso = peso;
        this.lote = lote;
        this.status = "Ativo";
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public String getNumeroBrinco() { return numeroBrinco; }
    public void setNumeroBrinco(String numeroBrinco) { this.numeroBrinco = numeroBrinco; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public LocalDateTime getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDateTime dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Colar getColar() { return colar; }
    public void setColar(Colar colar) { this.colar = colar; }

    @Override
    public String toString() {
        return nome + " [" + numeroBrinco + "] - " + raca;
    }
}

// ─────────────────────────────────────────────────────────────
// MODEL: Colar GPS
// ─────────────────────────────────────────────────────────────
class Colar {
    private String id;
    private int bateria;          // 0 a 100 (%)
    private String nivelSinal;    // "Forte", "Medio", "Fraco", "Sem Sinal"
    private int frequenciaMinutos;
    private String firmware;
    private boolean disponivel;
    private Localizacao ultimaLocalizacao;

    public Colar() {}

    public Colar(String id, int bateria, String nivelSinal, int frequenciaMinutos) {
        this.id = id;
        this.bateria = bateria;
        this.nivelSinal = nivelSinal;
        this.frequenciaMinutos = frequenciaMinutos;
        this.firmware = "v2.4.1";
        this.disponivel = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getBateria() { return bateria; }
    public void setBateria(int bateria) { this.bateria = bateria; }

    public String getNivelSinal() { return nivelSinal; }
    public void setNivelSinal(String nivelSinal) { this.nivelSinal = nivelSinal; }

    public int getFrequenciaMinutos() { return frequenciaMinutos; }
    public void setFrequenciaMinutos(int frequenciaMinutos) { this.frequenciaMinutos = frequenciaMinutos; }

    public String getFirmware() { return firmware; }
    public void setFirmware(String firmware) { this.firmware = firmware; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    public Localizacao getUltimaLocalizacao() { return ultimaLocalizacao; }
    public void setUltimaLocalizacao(Localizacao ultimaLocalizacao) { this.ultimaLocalizacao = ultimaLocalizacao; }

    @Override
    public String toString() {
        return id + " | Bateria: " + bateria + "% | Sinal: " + nivelSinal;
    }
}

// ─────────────────────────────────────────────────────────────
// MODEL: Localizacao GPS
// ─────────────────────────────────────────────────────────────
class Localizacao {
    private int id;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
    private String status;        // "Dentro", "Fora", "Limite"
    private Animal animal;

    public Localizacao() {}

    public Localizacao(int id, double latitude, double longitude, Animal animal) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = LocalDateTime.now();
        this.animal = animal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }

    public String getTimestampFormatado() {
        return timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}

// ─────────────────────────────────────────────────────────────
// MODEL: Fazenda
// ─────────────────────────────────────────────────────────────
class Fazenda {
    private int id;
    private String nome;
    private String proprietario;
    private String municipio;
    private String estado;
    private double areaTotal;
    private double areaMonitorada;
    // Geofence circular
    private double latitudeCentro;
    private double longitudeCentro;
    private double raioMetros;
    private double toleranciaMetros;
    private String tipoArea;  // "Circular", "Poligono", "Retangular"
    private List<Lote> lotes = new ArrayList<>();

    public Fazenda() {}

    public Fazenda(int id, String nome, String proprietario,
                   String municipio, String estado,
                   double latCentro, double lonCentro, double raio) {
        this.id = id;
        this.nome = nome;
        this.proprietario = proprietario;
        this.municipio = municipio;
        this.estado = estado;
        this.latitudeCentro = latCentro;
        this.longitudeCentro = lonCentro;
        this.raioMetros = raio;
        this.toleranciaMetros = 50;
        this.tipoArea = "Circular";
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getProprietario() { return proprietario; }
    public void setProprietario(String proprietario) { this.proprietario = proprietario; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getAreaTotal() { return areaTotal; }
    public void setAreaTotal(double areaTotal) { this.areaTotal = areaTotal; }

    public double getAreaMonitorada() { return areaMonitorada; }
    public void setAreaMonitorada(double areaMonitorada) { this.areaMonitorada = areaMonitorada; }

    public double getLatitudeCentro() { return latitudeCentro; }
    public void setLatitudeCentro(double latitudeCentro) { this.latitudeCentro = latitudeCentro; }

    public double getLongitudeCentro() { return longitudeCentro; }
    public void setLongitudeCentro(double longitudeCentro) { this.longitudeCentro = longitudeCentro; }

    public double getRaioMetros() { return raioMetros; }
    public void setRaioMetros(double raioMetros) { this.raioMetros = raioMetros; }

    public double getToleranciaMetros() { return toleranciaMetros; }
    public void setToleranciaMetros(double toleranciaMetros) { this.toleranciaMetros = toleranciaMetros; }

    public String getTipoArea() { return tipoArea; }
    public void setTipoArea(String tipoArea) { this.tipoArea = tipoArea; }

    public List<Lote> getLotes() { return lotes; }
    public void addLote(Lote lote) { this.lotes.add(lote); }

    @Override
    public String toString() { return nome + " - " + municipio + "/" + estado; }
}

// ─────────────────────────────────────────────────────────────
// MODEL: Lote
// ─────────────────────────────────────────────────────────────
class Lote {
    private int id;
    private String nome;
    private double areaHa;
    private String status;
    private List<Animal> animais = new ArrayList<>();

    public Lote(int id, String nome, double areaHa) {
        this.id = id;
        this.nome = nome;
        this.areaHa = areaHa;
        this.status = "Ativo";
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public double getAreaHa() { return areaHa; }
    public void setAreaHa(double areaHa) { this.areaHa = areaHa; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<Animal> getAnimais() { return animais; }
    public void addAnimal(Animal a) { animais.add(a); }

    @Override
    public String toString() { return nome + " (" + areaHa + " ha)"; }
}

// ─────────────────────────────────────────────────────────────
// MODEL: Alerta
// ─────────────────────────────────────────────────────────────
class Alerta {
    public enum Tipo { FORA_DA_AREA, BATERIA_BAIXA, SEM_SINAL, RETORNOU }

    private int id;
    private Tipo tipo;
    private Animal animal;
    private String mensagem;
    private LocalDateTime dataHora;
    private boolean resolvido;

    public Alerta(int id, Tipo tipo, Animal animal, String mensagem) {
        this.id = id;
        this.tipo = tipo;
        this.animal = animal;
        this.mensagem = mensagem;
        this.dataHora = LocalDateTime.now();
        this.resolvido = false;
    }

    public int getId() { return id; }
    public Tipo getTipo() { return tipo; }
    public Animal getAnimal() { return animal; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getDataHora() { return dataHora; }
    public boolean isResolvido() { return resolvido; }
    public void setResolvido(boolean resolvido) { this.resolvido = resolvido; }

    public String getDataHoraFormatada() {
        return dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}

// ─────────────────────────────────────────────────────────────
// SERVICE: AnimalService — CRUD de animais
// ─────────────────────────────────────────────────────────────
class AnimalService {
    private List<Animal> animais = new ArrayList<>();
    private int proximoId = 1;

    public AnimalService() {
        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        Animal a1 = new Animal(proximoId++, "Mimosa", "Nelore", "A-012", "Femea", 420, "Lote A");
        Animal a2 = new Animal(proximoId++, "Estrela", "Gir", "A-007", "Femea", 380, "Lote A");
        Animal a3 = new Animal(proximoId++, "Flor", "Angus", "A-018", "Femea", 450, "Lote B");
        Animal a4 = new Animal(proximoId++, "Bela", "Brahman", "A-023", "Femea", 395, "Lote B");
        Animal a5 = new Animal(proximoId++, "Nuvem", "Nelore", "A-002", "Femea", 410, "Lote A");
        Animal a6 = new Animal(proximoId++, "Rosa", "Senepol", "A-031", "Femea", 360, "Lote C");
        animais.addAll(Arrays.asList(a1, a2, a3, a4, a5, a6));
    }

    public Animal cadastrar(Animal animal) {
        animal.setId(proximoId++);
        animais.add(animal);
        return animal;
    }

    public Optional<Animal> buscarPorId(int id) {
        return animais.stream().filter(a -> a.getId() == id).findFirst();
    }

    public Optional<Animal> buscarPorBrinco(String brinco) {
        return animais.stream()
                .filter(a -> a.getNumeroBrinco().equalsIgnoreCase(brinco))
                .findFirst();
    }

    public List<Animal> listarTodos() {
        return Collections.unmodifiableList(animais);
    }

    public List<Animal> listarAtivos() {
        List<Animal> ativos = new ArrayList<>();
        for (Animal a : animais) {
            if ("Ativo".equals(a.getStatus())) ativos.add(a);
        }
        return ativos;
    }

    public boolean atualizar(Animal animal) {
        for (int i = 0; i < animais.size(); i++) {
            if (animais.get(i).getId() == animal.getId()) {
                animais.set(i, animal);
                return true;
            }
        }
        return false;
    }

    public boolean remover(int id) {
        return animais.removeIf(a -> a.getId() == id);
    }

    public int totalAnimais() { return animais.size(); }
}

// ─────────────────────────────────────────────────────────────
// SERVICE: ColarService — CRUD e vinculação de colares
// ─────────────────────────────────────────────────────────────
class ColarService {
    private List<Colar> colares = new ArrayList<>();

    public ColarService() {
        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        colares.add(new Colar("C-01", 92, "Forte", 5));
        colares.add(new Colar("C-02", 78, "Forte", 5));
        colares.add(new Colar("C-04", 87, "Forte", 5));
        colares.add(new Colar("C-07", 100, "Forte", 5));  // disponivel
        colares.add(new Colar("C-09", 65, "Medio", 5));
        colares.add(new Colar("C-11", 55, "Medio", 5));
        colares.add(new Colar("C-14", 100, "Forte", 5));  // disponivel
        colares.add(new Colar("C-15", 14, "Fraco", 5));
        colares.add(new Colar("C-18", 18, "Medio", 5));
        // C-07 e C-14 permanecem disponíveis
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

// ─────────────────────────────────────────────────────────────
// SERVICE: FazendaService — CRUD de fazendas
// ─────────────────────────────────────────────────────────────
class FazendaService {
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

// ─────────────────────────────────────────────────────────────
// SERVICE: GeofenceService — verifica se animal está na área
// ─────────────────────────────────────────────────────────────
class GeofenceService {
    private static final double RAIO_TERRA_KM = 6371.0;

    /**
     * Calcula distância em metros entre dois pontos GPS (Haversine).
     */
    public double calcularDistanciaMetros(double lat1, double lon1,
                                          double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_TERRA_KM * c * 1000;
    }

    /**
     * Verifica se a localização está dentro do geofence da fazenda.
     * Retorna: "Dentro", "Limite" ou "Fora"
     */
    public String verificarStatus(Localizacao loc, Fazenda fazenda) {
        double distancia = calcularDistanciaMetros(
                loc.getLatitude(), loc.getLongitude(),
                fazenda.getLatitudeCentro(), fazenda.getLongitudeCentro()
        );
        double raio = fazenda.getRaioMetros();
        double tolerancia = fazenda.getToleranciaMetros();

        if (distancia <= raio - tolerancia) {
            return "Dentro";
        } else if (distancia <= raio + tolerancia) {
            return "Limite";
        } else {
            return "Fora";
        }
    }

    public boolean estaDentro(Localizacao loc, Fazenda fazenda) {
        return "Dentro".equals(verificarStatus(loc, fazenda));
    }
}

// ─────────────────────────────────────────────────────────────
// SERVICE: RastreamentoService — registra e consulta posições
// ─────────────────────────────────────────────────────────────
class RastreamentoService {
    private List<Localizacao> historico = new ArrayList<>();
    private int proximoId = 1;
    private GeofenceService geofenceService;
    private FazendaService fazendaService;
    private AlertaService alertaService;

    public RastreamentoService(GeofenceService geo, FazendaService faz, AlertaService alert) {
        this.geofenceService = geo;
        this.fazendaService = faz;
        this.alertaService = alert;
        carregarHistoricoInicial();
    }

    private void carregarHistoricoInicial() {
        // Apenas popula objetos sem animais reais (inicialização fictícia)
    }

    /**
     * Registra nova leitura GPS de um animal.
     * Verifica geofence e gera alertas automaticamente.
     */
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
                        animal.getNome() + " saiu da área! Brinco: " + animal.getNumeroBrinco()
                );
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

// ─────────────────────────────────────────────────────────────
// SERVICE: AlertaService — gera e gerencia alertas
// ─────────────────────────────────────────────────────────────
class AlertaService {
    private List<Alerta> alertas = new ArrayList<>();
    private int proximoId = 1;
    private List<Runnable> listeners = new ArrayList<>();

    public AlertaService() {
        carregarAlertasIniciais();
    }

    private void carregarAlertasIniciais() {
        // Alertas de demonstração sem referência a objetos Animal reais
    }

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

    public int totalAtivos() { return (int) alertas.stream().filter(a -> !a.isResolvido()).count(); }

    public void addListener(Runnable listener) { listeners.add(listener); }
    private void notificarListeners() { listeners.forEach(Runnable::run); }
}

// ─────────────────────────────────────────────────────────────
// SERVICE: AuthService — autenticação simples
// ─────────────────────────────────────────────────────────────
class AuthService {
    private Map<String, String> usuarios = new HashMap<>();
    private String usuarioLogado = null;

    public AuthService() {
        usuarios.put("admin", "12345");
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

// ─────────────────────────────────────────────────────────────
// CONTAINER: AgroTechApp — ponto de entrada do backend
//            Instancia e injeta todos os services
// ─────────────────────────────────────────────────────────────
public class Backend {

    // Services expostos para uso pela camada de UI
    public final AuthService authService;
    public final AnimalService animalService;
    public final ColarService colarService;
    public final FazendaService fazendaService;
    public final GeofenceService geofenceService;
    public final AlertaService alertaService;
    public final RastreamentoService rastreamentoService;

    private static Backend instancia;

    private Backend() {
        this.authService       = new AuthService();
        this.animalService     = new AnimalService();
        this.colarService      = new ColarService();
        this.fazendaService    = new FazendaService();
        this.geofenceService   = new GeofenceService();
        this.alertaService     = new AlertaService();
        this.rastreamentoService = new RastreamentoService(
                geofenceService, fazendaService, alertaService);
    }

    /** Singleton — uma única instância compartilhada por toda a UI */
    public static Backend getInstance() {
        if (instancia == null) instancia = new Backend();
        return instancia;
    }

    // ── Métodos de conveniência usados pela UI ──────────────────

    public boolean login(String usuario, String senha) {
        return authService.login(usuario, senha);
    }

    public int totalAnimais() {
        return animalService.totalAnimais();
    }

    public long animaisDentroArea() {
        Fazenda f = fazendaService.getFazendaPrincipal();
        if (f == null) return 0;
        return animalService.listarAtivos().stream()
                .filter(a -> {
                    if (a.getColar() == null || a.getColar().getUltimaLocalizacao() == null)
                        return true;
                    return geofenceService.estaDentro(a.getColar().getUltimaLocalizacao(), f);
                }).count();
    }

    public int totalAlertas() {
        return alertaService.totalAtivos();
    }

    public int totalColaresAtivos() {
        return (int) colarService.listarTodos().stream()
                .filter(c -> !c.isDisponivel()).count();
    }
}
