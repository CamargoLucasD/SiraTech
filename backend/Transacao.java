package backend;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "fazenda_id")
    private Fazenda fazenda;

    @Column(name = "tipo") // Venda, Compra, Abate
    private String tipo;

    @Column(name = "valor")
    private double valor;

    @Column(name = "data_transacao")
    private LocalDate dataTransacao;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "status")
    private String status;

    public Transacao() {}

    public Transacao(Animal animal, Fazenda fazenda, String tipo,
                     double valor, LocalDate data, String descricao) {
        this.animal = animal;
        this.fazenda = fazenda;
        this.tipo = tipo;
        this.valor = valor;
        this.dataTransacao = data;
        this.descricao = descricao;
        this.status = "Concluída";
    }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public int getId() { return id; }
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    public Fazenda getFazenda() { return fazenda; }
    public void setFazenda(Fazenda fazenda) { this.fazenda = fazenda; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public LocalDate getDataTransacao() { return dataTransacao; }
    public void setDataTransacao(LocalDate dataTransacao) { this.dataTransacao = dataTransacao; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDataStr() { return dataTransacao != null ? dataTransacao.format(FMT) : "—"; }
    public String getValorStr() { return String.format("R$ %,.2f", valor); }
}
