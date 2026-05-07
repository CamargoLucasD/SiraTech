package backend;

import java.util.*;

public class AnimalService {
    private List<Animal> animais = new ArrayList<>();
    private int proximoId = 1;

    public AnimalService() {
        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        Animal a1 = new Animal(proximoId++, "Mimosa",  "Nelore",  "A-012", "Femea", 420, "Lote A");
        Animal a2 = new Animal(proximoId++, "Estrela", "Gir",     "A-007", "Femea", 380, "Lote A");
        Animal a3 = new Animal(proximoId++, "Flor",    "Angus",   "A-018", "Femea", 450, "Lote B");
        Animal a4 = new Animal(proximoId++, "Bela",    "Brahman", "A-023", "Femea", 395, "Lote B");
        Animal a5 = new Animal(proximoId++, "Nuvem",   "Nelore",  "A-002", "Femea", 410, "Lote A");
        Animal a6 = new Animal(proximoId++, "Rosa",    "Senepol", "A-031", "Femea", 360, "Lote C");
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
