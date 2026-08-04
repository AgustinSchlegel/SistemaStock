package repository;

import model.Material;
import model.Producto;
import model.Tupla;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonProductoRepositoryTest {

    private static final String RUTA_TEST = "test-tmp-productos.json";

    @AfterEach
    void limpiar() {
        new File(RUTA_TEST).delete();
    }

    @Test
    void guardarYLuegoLeerReconstruyeElProductoConSuMaterial() {
        JsonProductoRepository repo = new JsonProductoRepository(RUTA_TEST);
        Material hojas = new Material("hojas", 4500, 500);

        List<Tupla<Material, Integer>> materiales = new ArrayList<>();
        materiales.add(new Tupla<>(hojas, 30));
        Producto libreta = new Producto("libreta", materiales);
        libreta.producir(4);

        repo.guardarTodos(List.of(libreta));

        Map<String, Material> materialesDisponibles = new HashMap<>();
        materialesDisponibles.put("hojas", hojas);
        List<Producto> leidos = repo.leerTodos(materialesDisponibles);

        assertEquals(1, leidos.size());
        Producto reconstruido = leidos.get(0);
        assertEquals("libreta", reconstruido.getNombre());
        assertEquals(4, reconstruido.getDisponible());
        assertEquals(1, reconstruido.getMateriales().size());
        // Clave: debe ser la MISMA referencia de material, no una copia
        assertTrue(reconstruido.getMateriales().get(0).getItem1() == hojas);
    }

    @Test
    void siElMaterialYaNoExisteElRequisitoSeOmiteSinRomper() {
        JsonProductoRepository repo = new JsonProductoRepository(RUTA_TEST);
        Material hojas = new Material("hojas", 4500, 500);
        List<Tupla<Material, Integer>> materiales = new ArrayList<>();
        materiales.add(new Tupla<>(hojas, 30));
        repo.guardarTodos(List.of(new Producto("libreta", materiales)));

        // Simula que "hojas" fue eliminado: mapa de materiales disponibles vacío
        List<Producto> leidos = repo.leerTodos(new HashMap<>());

        assertEquals(1, leidos.size());
        assertTrue(leidos.get(0).getMateriales().isEmpty());
    }
}
