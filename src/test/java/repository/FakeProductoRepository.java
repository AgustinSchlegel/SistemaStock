package repository;

import model.Material;
import model.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FakeProductoRepository implements ProductoRepository {

    private List<Producto> datos = new ArrayList<>();

    @Override
    public List<Producto> leerTodos(Map<String, Material> materialesDisponibles) {
        return new ArrayList<>(datos);
    }

    @Override
    public void guardarTodos(List<Producto> productos) {
        this.datos = new ArrayList<>(productos);
    }
}
