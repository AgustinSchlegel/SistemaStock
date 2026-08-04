package repository;

import model.Material;
import model.Producto;

import java.util.List;
import java.util.Map;

public interface ProductoRepository {
    // Necesita el mapa de materiales ya cargados para poder reconstruir las referencias
    // reales (y no copias) de cada material que usa cada producto.
    List<Producto> leerTodos(Map<String, Material> materialesDisponibles);
    void guardarTodos(List<Producto> productos);
}
