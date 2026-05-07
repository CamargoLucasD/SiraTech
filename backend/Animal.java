package backend;

import java.time.LocalDateTime;

public class Animal {
    private int id;
    private String nome;
    private String raca;
    private String numeroBrinco;
    private String sexo;
    private double peso;
    private LocalDateTime dataNascimento;
    private String lote;
    private String status;
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
