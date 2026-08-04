package model;

import org.junit.jupiter.api.Test;
import repository.FakeMaterialRepository;
import repository.FakeProductoRepository;
import repository.MaterialRepository;
import repository.ProductoRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Gracias a la inyección de dependencias, estos tests corren 100% en memoria:
// no leen ni escriben ningún archivo real.
class SistemaTest {

    private Sistema nuevoSistema() {
        return new Sistema(new FakeMaterialRepository(), new FakeProductoRepository());
    }

    @Test
    void nuevoMaterialQuedaDisponibleParaConsultar() {
        Sistema sistema = nuevoSistema();
        sistema.nuevoMaterial("hojas", 4500, 500);

        assertTrue(sistema.existeMaterial("hojas"));
        assertEquals(9, sistema.calcularPrecioMaterial("hojas"));
    }

    @Test
    void calcularPrecioMaterialInexistenteDevuelveMenosUno() {
        Sistema sistema = nuevoSistema();
        assertEquals(-1, sistema.calcularPrecioMaterial("no existe"));
    }

    @Test
    void calcularPrecioProductoInexistenteDevuelveMenosUno() {
        Sistema sistema = nuevoSistema();
        assertEquals(-1, sistema.calcularPrecioProducto("no existe"));
    }

    @Test
    void nuevoProductoQuedaDisponibleYConPrecioCalculado() {
        Sistema sistema = nuevoSistema();
        sistema.nuevoMaterial("hojas", 4500, 500);
        List<Tupla<Material, Integer>> materiales = new ArrayList<>();
        materiales.add(new Tupla<>(sistema.getMaterial("hojas"), 30));
        sistema.nuevoProducto("libreta", materiales);

        assertTrue(sistema.existeProducto("libreta"));
        assertEquals(270, sistema.calcularPrecioProducto("libreta"));
    }

    @Test
    void actualizarMaterialCantidadModificaElStock() {
        Sistema sistema = nuevoSistema();
        sistema.nuevoMaterial("hojas", 4500, 500);

        sistema.actualizarMaterial(1000, "hojas", true);

        assertEquals(1000, sistema.getMaterial("hojas").getCantidad());
    }

    @Test
    void actualizarMaterialPrecioModificaElPrecio() {
        Sistema sistema = nuevoSistema();
        sistema.nuevoMaterial("hojas", 4500, 500);

        sistema.actualizarMaterial(3000.0, "hojas", false);

        assertEquals(3000, sistema.getMaterial("hojas").getPrecio());
    }

    @Test
    void actualizarMaterialInexistenteNoRompe() {
        Sistema sistema = nuevoSistema();
        sistema.actualizarMaterial(10, "no existe", true);
        assertFalse(sistema.existeMaterial("no existe"));
    }

    @Test
    void actualizarProductoNombreLoRenombra() {
        Sistema sistema = nuevoSistema();
        sistema.nuevoMaterial("hojas", 4500, 500);
        List<Tupla<Material, Integer>> materiales = new ArrayList<>();
        materiales.add(new Tupla<>(sistema.getMaterial("hojas"), 30));
        sistema.nuevoProducto("libreta", materiales);

        sistema.actualizarProducto("libreta", "cuaderno", true);

        assertEquals("cuaderno", sistema.getProducto("libreta").getNombre());
    }

    @Test
    void producirYVenderProductoFuncionanJuntos() {
        Sistema sistema = nuevoSistema();
        sistema.nuevoMaterial("hojas", 4500, 500);
        List<Tupla<Material, Integer>> materiales = new ArrayList<>();
        materiales.add(new Tupla<>(sistema.getMaterial("hojas"), 30));
        sistema.nuevoProducto("libreta", materiales);

        assertTrue(sistema.producirProducto("libreta", 5));
        assertEquals(5, sistema.getProducto("libreta").getDisponible());

        assertTrue(sistema.venderProducto("libreta", 3));
        assertEquals(2, sistema.getProducto("libreta").getDisponible());
    }

    @Test
    void producirProductoInexistenteDevuelveFalse() {
        Sistema sistema = nuevoSistema();
        assertFalse(sistema.producirProducto("no existe", 1));
    }

    @Test
    void venderMasDeLoDisponibleDevuelveFalse() {
        Sistema sistema = nuevoSistema();
        sistema.nuevoMaterial("hojas", 4500, 500);
        List<Tupla<Material, Integer>> materiales = new ArrayList<>();
        materiales.add(new Tupla<>(sistema.getMaterial("hojas"), 30));
        sistema.nuevoProducto("libreta", materiales);
        sistema.producirProducto("libreta", 2);

        assertFalse(sistema.venderProducto("libreta", 10));
        assertEquals(2, sistema.getProducto("libreta").getDisponible());
    }

    @Test
    void mostrarMaterialesReflejaLoCargado() {
        Sistema sistema = nuevoSistema();
        sistema.nuevoMaterial("hojas", 4500, 500);
        sistema.nuevoMaterial("tinta", 1000, 20);

        assertEquals(2, sistema.mostrarMateriales().size());
    }

    @Test
    void producirNoAlteraElCostoUnitarioNiElPrecioDelProducto() {
        // Regresión del bug encontrado en pruebas manuales: producir() consumía stock
        // y eso hacía "subir" artificialmente el precio del material y del producto.
        Sistema sistema = nuevoSistema();
        sistema.nuevoMaterial("hojas", 4500, 500);
        List<Tupla<Material, Integer>> materiales = new ArrayList<>();
        materiales.add(new Tupla<>(sistema.getMaterial("hojas"), 30));
        sistema.nuevoProducto("libreta", materiales);

        sistema.producirProducto("libreta", 4);

        assertEquals(9, sistema.getMaterial("hojas").getPrecioUnidad());
        assertEquals(270, sistema.calcularPrecioProducto("libreta"));
    }

    @Test
    void datosPersistenAtravesDelRepositorioInyectado() {
        // Reutilizamos los mismos repositorios (no una instancia nueva) para simular
        // "reabrir la app" sin depender de un archivo real en disco.
        MaterialRepository materialRepo = new FakeMaterialRepository();
        ProductoRepository productoRepo = new FakeProductoRepository();

        Sistema sistema1 = new Sistema(materialRepo, productoRepo);
        sistema1.nuevoMaterial("hojas", 4500, 500);
        List<Tupla<Material, Integer>> materiales = new ArrayList<>();
        materiales.add(new Tupla<>(sistema1.getMaterial("hojas"), 30));
        sistema1.nuevoProducto("libreta", materiales);
        sistema1.producirProducto("libreta", 4);

        Sistema sistema2 = new Sistema(materialRepo, productoRepo);

        assertTrue(sistema2.existeMaterial("hojas"));
        assertEquals(500 - 30 * 4, sistema2.getMaterial("hojas").getCantidad());
        assertTrue(sistema2.existeProducto("libreta"));
        assertEquals(4, sistema2.getProducto("libreta").getDisponible());
    }
}
