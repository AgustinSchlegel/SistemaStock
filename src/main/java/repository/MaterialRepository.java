package repository;

import model.Material;

import java.util.List;

// Abstrae CÓMO se guardan/leen los materiales (JSON, base de datos, memoria...).
// Sistema solo conoce esta interfaz, nunca la implementación concreta.
public interface MaterialRepository {
    List<Material> leerTodos();
    void guardarTodos(List<Material> materiales);
}
