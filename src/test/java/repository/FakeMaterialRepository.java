package repository;

import model.Material;

import java.util.ArrayList;
import java.util.List;

// Doble de test: guarda todo en memoria, nunca toca disco. Permite testear Sistema
// de forma rápida, aislada y sin efectos secundarios entre tests.
public class FakeMaterialRepository implements MaterialRepository {

    private List<Material> datos = new ArrayList<>();

    @Override
    public List<Material> leerTodos() {
        return new ArrayList<>(datos);
    }

    @Override
    public void guardarTodos(List<Material> materiales) {
        this.datos = new ArrayList<>(materiales);
    }
}
