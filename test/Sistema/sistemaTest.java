package Sistema;

import auxiliares.tupla;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// NOTA: sistema lee y escribe src/Sistema/materiales.json y src/Sistema/productos.json
// en cada operación. Por eso estos tests limpian esos archivos antes y después de correr,
// para no pisar datos reales ni dejar basura entre tests. A futuro conviene desacoplar
// sistema de la persistencia (inyectar un repositorio) para no depender del disco acá.
class sistemaTest {

    private static final File ARCHIVO_MATERIALES = new File("src/Sistema/materiales.json");
    private static final File ARCHIVO_PRODUCTOS = new File("src/Sistema/productos.json");

    @BeforeEach
    void limpiarAntes() {
        ARCHIVO_MATERIALES.delete();
        ARCHIVO_PRODUCTOS.delete();
    }

    @AfterEach
    void limpiarDespues() {
        ARCHIVO_MATERIALES.delete();
        ARCHIVO_PRODUCTOS.delete();
    }

    @Test
    void nuevoMaterialQuedaDisponibleParaConsultar() {
        sistema sistema = new sistema();
        sistema.nuevoMaterial("hojas", 4500, 500);

        assertTrue(sistema.existeMaterial("hojas"));
        assertEquals(9, sistema.calcularPrecioMaterial("hojas"));
    }

    @Test
    void calcularPrecioMaterialInexistenteDevuelveMenosUno() {
        sistema sistema = new sistema();
        assertEquals(-1, sistema.calcularPrecioMaterial("no existe"));
    }

    @Test
    void calcularPrecioProductoInexistenteDevuelveMenosUno() {
        sistema sistema = new sistema();
        assertEquals(-1, sistema.calcularPrecioProducto("no existe"));
    }

    @Test
    void nuevoProductoQuedaDisponibleYConPrecioCalculado() {
        sistema sistema = new sistema();
        sistema.nuevoMaterial("hojas", 4500, 500);
        List<tupla<material, Integer>> materiales = new ArrayList<>();
        materiales.add(new tupla<>(sistema.getMaterial("hojas"), 30));
        sistema.nuevoProducto("libreta", materiales);

        assertTrue(sistema.existeProducto("libreta"));
        assertEquals(270, sistema.calcularPrecioProducto("libreta"));
    }

    @Test
    void actualizarMaterialCantidadModificaElStock() {
        sistema sistema = new sistema();
        sistema.nuevoMaterial("hojas", 4500, 500);

        sistema.actualizarMaterial(1000, "hojas", true);

        assertEquals(1000, sistema.getMaterial("hojas").getCantidad());
    }

    @Test
    void actualizarMaterialPrecioModificaElPrecio() {
        sistema sistema = new sistema();
        sistema.nuevoMaterial("hojas", 4500, 500);

        sistema.actualizarMaterial(3000.0, "hojas", false);

        assertEquals(3000, sistema.getMaterial("hojas").getPrecio());
    }

    @Test
    void actualizarMaterialInexistenteNoRompe() {
        sistema sistema = new sistema();
        sistema.actualizarMaterial(10, "no existe", true);
        assertFalse(sistema.existeMaterial("no existe"));
    }

    @Test
    void actualizarProductoNombreLoRenombra() {
        sistema sistema = new sistema();
        sistema.nuevoMaterial("hojas", 4500, 500);
        List<tupla<material, Integer>> materiales = new ArrayList<>();
        materiales.add(new tupla<>(sistema.getMaterial("hojas"), 30));
        sistema.nuevoProducto("libreta", materiales);

        sistema.actualizarProducto("libreta", "cuaderno", true);

        assertEquals("cuaderno", sistema.getProducto("libreta").getNombre());
    }

    @Test
    void producirYVenderProductoFuncionanJuntos() {
        sistema sistema = new sistema();
        sistema.nuevoMaterial("hojas", 4500, 500);
        List<tupla<material, Integer>> materiales = new ArrayList<>();
        materiales.add(new tupla<>(sistema.getMaterial("hojas"), 30));
        sistema.nuevoProducto("libreta", materiales);

        assertTrue(sistema.producirProducto("libreta", 5));
        assertEquals(5, sistema.getProducto("libreta").getDisponible());

        assertTrue(sistema.venderProducto("libreta", 3));
        assertEquals(2, sistema.getProducto("libreta").getDisponible());
    }

    @Test
    void producirProductoInexistenteDevuelveFalse() {
        sistema sistema = new sistema();
        assertFalse(sistema.producirProducto("no existe", 1));
    }

    @Test
    void venderMasDeLoDisponibleDevuelveFalse() {
        sistema sistema = new sistema();
        sistema.nuevoMaterial("hojas", 4500, 500);
        List<tupla<material, Integer>> materiales = new ArrayList<>();
        materiales.add(new tupla<>(sistema.getMaterial("hojas"), 30));
        sistema.nuevoProducto("libreta", materiales);
        sistema.producirProducto("libreta", 2);

        assertFalse(sistema.venderProducto("libreta", 10));
        assertEquals(2, sistema.getProducto("libreta").getDisponible());
    }

    @Test
    void mostrarMaterialesYProductosReflejanLoCargado() {
        sistema sistema = new sistema();
        sistema.nuevoMaterial("hojas", 4500, 500);
        sistema.nuevoMaterial("tinta", 1000, 20);

        assertEquals(2, sistema.MostrarMateriales().size());
    }

    @Test
    void datosPersistenYSeRecuperanEnUnaNuevaInstanciaDeSistema() {
        sistema sistema1 = new sistema();
        sistema1.nuevoMaterial("hojas", 4500, 500);
        List<tupla<material, Integer>> materiales = new ArrayList<>();
        materiales.add(new tupla<>(sistema1.getMaterial("hojas"), 30));
        sistema1.nuevoProducto("libreta", materiales);
        sistema1.producirProducto("libreta", 4);

        // Simula "reabrir la app": una nueva instancia debe leer lo guardado en disco
        sistema sistema2 = new sistema();

        assertTrue(sistema2.existeMaterial("hojas"));
        assertEquals(500 - 30 * 4, sistema2.getMaterial("hojas").getCantidad());
        assertTrue(sistema2.existeProducto("libreta"));
        assertEquals(4, sistema2.getProducto("libreta").getDisponible());
    }

    // NOTA: no hay forma de probar automáticamente "un producto cuyo material fue borrado"
    // porque sistema todavía no tiene un método para eliminar materiales. Si en productos.json
    // queda un requisito apuntando a un material que ya no existe en materiales.json,
    // gestorProducto.leerProductos() lo omite en vez de romper la carga (avisa por consola).
    // Cuando agreguemos "eliminarMaterial" a sistema, este es un buen test para sumar.
}
