package backend;

public class Backend {

    public final AuthService          authService;
    public final AnimalService        animalService;
    public final ColarService         colarService;
    public final FazendaService       fazendaService;
    public final GeofenceService      geofenceService;
    public final AlertaService        alertaService;
    public final RastreamentoService  rastreamentoService;
    public final VacinaService        vacinaService;
    public final HistoricoVetService  historicoVetService;
    public final TransacaoService     transacaoService;

    private static Backend instancia;

    private Backend() {
        this.authService         = new AuthService();
        this.animalService       = new AnimalService();
        this.colarService        = new ColarService();
        this.fazendaService      = new FazendaService();
        this.geofenceService     = new GeofenceService();
        this.alertaService       = new AlertaService();
        this.rastreamentoService = new RastreamentoService(geofenceService, fazendaService, alertaService);
        this.vacinaService       = new VacinaService();
        this.historicoVetService = new HistoricoVetService();
        this.transacaoService    = new TransacaoService();

        Runtime.getRuntime().addShutdownHook(new Thread(HibernateUtil::fechar));
    }

    public static Backend getInstance() {
        if (instancia == null) instancia = new Backend();
        return instancia;
    }

    public boolean login(String usuario, String senha) {
        return authService.login(usuario, senha);
    }

    // Respeita fazenda ativa
    public int totalAnimais() {
        Fazenda fa = authService.getFazendaAtiva();
        if (fa != null) return animalService.totalAnimaisPorFazenda(fa.getId());
        return animalService.totalAnimais();
    }

    public long animaisDentroArea() {
        Fazenda f = authService.getFazendaAtiva() != null
                ? authService.getFazendaAtiva()
                : fazendaService.getFazendaPrincipal();
        if (f == null) return 0;
        return animalService.listarAtivosPorFazenda(f.getId()).stream()
                .filter(a -> {
                    if (a.getColar() == null || a.getColar().getUltimaLocalizacao() == null) return true;
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

    public Fazenda getFazendaAtiva() {
        Fazenda fa = authService.getFazendaAtiva();
        if (fa != null) return fa;
        return fazendaService.getFazendaPrincipal();
    }
}