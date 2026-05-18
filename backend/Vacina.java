package backend;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "vacinas")
public class Vacina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "fazenda_id")
    private Fazenda fazenda;

    @Column(name = "tipo_vacina")
    private String tipoVacina;

    @Column(name = "data_aplicacao")
    private LocalDate dataAplicacao;

    @Column(name = "proxima_dose")
    private LocalDate proximaDose;

    @Column(name = "veterinario")
    private String veterinario;

    @Column(name = "observacoes")
    private String observacoes;

    @Column(name = "status")
    private String status; // "Em dia", "Vencida", "Vence em breve"

    public Vacina() {}

    public Vacina(Animal animal, Fazenda fazenda, String tipoVacina,
                  LocalDate dataAplicacao, LocalDate proximaDose, String veterinario, String observacoes) {
        this.animal = animal;
        this.fazenda = fazenda;
        this.tipoVacina = tipoVacina;
        this.dataAplicacao = dataAplicacao;
        this.proximaDose = proximaDose;
        this.veterinario = veterinario;
        this.observacoes = observacoes;
        calcularStatus();
    }

    public void calcularStatus() {
        if (proximaDose == null) { this.status = "Sem data"; return; }
        LocalDate hoje = LocalDate.now();
        long dias = java.time.temporal.ChronoUnit.DAYS.between(hoje, proximaDose);
        if (dias < 0) this.status = "⚠ Vencida";
        else if (dias <= 30) this.status = "⚠ Vence em " + dias + " dias";
        else this.status = "✔ Em dia";
    }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public int getId() { return id; }
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    public Fazenda getFazenda() { return fazenda; }
    public void setFazenda(Fazenda fazenda) { this.fazenda = fazenda; }
    public String getTipoVacina() { return tipoVacina; }
    public void setTipoVacina(String tipoVacina) { this.tipoVacina = tipoVacina; }
    public LocalDate getDataAplicacao() { return dataAplicacao; }
    public void setDataAplicacao(LocalDate dataAplicacao) { this.dataAplicacao = dataAplicacao; calcularStatus(); }
    public LocalDate getProximaDose() { return proximaDose; }
    public void setProximaDose(LocalDate proximaDose) { this.proximaDose = proximaDose; calcularStatus(); }
    public String getVeterinario() { return veterinario; }
    public void setVeterinario(String veterinario) { this.veterinario = veterinario; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDataAplicacaoStr() { return dataAplicacao != null ? dataAplicacao.format(FMT) : "—"; }
    public String getProximaDoseStr() { return proximaDose != null ? proximaDose.format(FMT) : "—"; }
}
