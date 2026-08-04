package repository;

import model.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonMaterialRepositoryTest {

    private static final String RUTA_TEST = "test-tmp-materiales.json";

    @AfterEach
    void limpiar() {
        new File(RUTA_TEST).delete();
    }

    @Test
    void siElArchivoNoExisteDevuelveListaVacia() {
        JsonMaterialRepository repo = new JsonMaterialRepository(RUTA_TEST);
        assertTrue(repo.leerTodos().isEmpty());
    }

    @Test
    void guardarYLuegoLeerDevuelveLosMismosDatos() {
        JsonMaterialRepository repo = new JsonMaterialRepository(RUTA_TEST);
        List<Material> materiales = new ArrayList<>();
        materiales.add(new Material("hojas", 4500, 500));
        materiales.add(new Material("tinta", 1000, 20));

        repo.guardarTodos(materiales);
        List<Material> leidos = repo.leerTodos();

        assertEquals(2, leidos.size());
        assertEquals("hojas", leidos.get(0).getNombre());
        assertEquals(9, leidos.get(0).getPrecioUnidad());
    }
}
