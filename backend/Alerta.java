package backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Alerta {
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
