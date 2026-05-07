package backend;

import java.util.ArrayList;
import java.util.List;

public class Lote {
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
