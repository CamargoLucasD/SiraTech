package backend;

public class Backend {

    public final AuthService        authService;
    public final AnimalService      animalService;
    public final ColarService       colarService;
    public final FazendaService     fazendaService;
    public final GeofenceService    geofenceService;
    public final AlertaService      alertaService;
    public final RastreamentoService rastreamentoService;

    private static Backend instancia;

    private Backend() {
        this.authService        = new AuthService();
        this.animalService      = new AnimalService();
        this.colarService       = new ColarService();
        this.fazendaService     = new FazendaService();
        this.geofenceService    = new GeofenceService();
        this.alertaService      = new AlertaService();
        this.rastreamentoService = new RastreamentoService(
                geofenceService, fazendaService, alertaService);
    }

    /** Singleton — uma única instância compartilhada por toda a UI */
    public static Backend getInstance() {
        if (instancia == null) instancia = new Backend();
        return instancia;
    }

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
