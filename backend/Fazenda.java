package backend;

import java.util.ArrayList;
import java.util.List;

public class Fazenda {
    private int id;
    private String nome;
    private String proprietario;
    private String municipio;
    private String estado;
    private double areaTotal;
    private double areaMonitorada;
    private double latitudeCentro;
    private double longitudeCentro;
    private double raioMetros;
    private double toleranciaMetros;
    private String tipoArea;
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
