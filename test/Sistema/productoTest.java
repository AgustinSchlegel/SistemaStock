package Sistema;

import auxiliares.tupla;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class productoTest {

    @Test
    void posibleProduccion() {
        producto libreta = new producto("libreta", getListaMateriales());
        assertEquals(16, libreta.produccionPosible());
    }

    @Test
    void posiblePrecio() {
        producto libreta = new producto("libreta", getListaMateriales());
        libreta.calcularPrecio();
        assertEquals(396, libreta.getPrecio());
    }

    @Test
    void producirDentroDeLoPosibleDescuentaMaterialesYSumaStock() {
        material hojas = hojas();
        List<tupla<material, Integer>> lista = new ArrayList<>();
        lista.add(new tupla<>(hojas, 30));
        producto libreta = new producto("libreta", lista);

        boolean resultado = libreta.producir(5);

        assertTrue(resultado);
        assertEquals(5, libreta.getDisponible());
        assertEquals(500 - 30 * 5, hojas.getCantidad());
    }

    @Test
    void producirMasDeLoPosibleNoModificaNada() {
        material hojas = hojas();
        List<tupla<material, Integer>> lista = new ArrayList<>();
        lista.add(new tupla<>(hojas, 30));
        producto libreta = new producto("libreta", lista);

        boolean resultado = libreta.producir(999);

        assertFalse(resultado);
        assertEquals(0, libreta.getDisponible());
        assertEquals(500, hojas.getCantidad());
    }

    @Test
    void producirDosVecesAcumulaStockEnVezDeSobreescribir() {
        material hojas = hojas();
        List<tupla<material, Integer>> lista = new ArrayList<>();
        lista.add(new tupla<>(hojas, 10));
        producto libreta = new producto("libreta", lista);

        libreta.producir(3);
        libreta.producir(2);

        assertEquals(5, libreta.getDisponible());
    }

    @Test
    void venderDentroDelStockDescuentaDisponible() {
        producto libreta = new producto("libreta", getListaMateriales());
        libreta.producir(5);

        boolean resultado = libreta.venderProducto(2);

        assertTrue(resultado);
        assertEquals(3, libreta.getDisponible());
    }

    @Test
    void venderMasQueElStockDisponibleFalla() {
        producto libreta = new producto("libreta", getListaMateriales());
        libreta.producir(2);

        boolean resultado = libreta.venderProducto(10);

        assertFalse(resultado);
        assertEquals(2, libreta.getDisponible());
    }

    @Test
    void produccionPosibleSinMaterialesEsCero() {
        producto vacio = new producto("vacio", new ArrayList<>());
        assertEquals(0, vacio.produccionPosible());
    }

    @Test
    void actualizarMaterialesRecalculaElPrecio() {
        producto libreta = new producto("libreta", getListaMateriales());
        assertEquals(396, libreta.getPrecio());

        List<tupla<material, Integer>> nuevaLista = new ArrayList<>();
        nuevaLista.add(new tupla<>(hojas(), 10));
        libreta.actualizarMateriales(nuevaLista);

        assertEquals(90, libreta.getPrecio());
    }

    @Test
    void constructorDeReconstruccionRespetaElStockGuardado() {
        producto libreta = new producto("libreta", getListaMateriales(), 7);
        assertEquals(7, libreta.getDisponible());
        assertEquals(396, libreta.getPrecio());
    }

    private List<tupla<material, Integer>> getListaMateriales() {
        List<tupla<material, Integer>> listaMateriales = new ArrayList<>();
        listaMateriales.add(new tupla<>(hojas(), 30));
        listaMateriales.add(new tupla<>(espiral(), 1));
        listaMateriales.add(new tupla<>(tapas(), 2));
        return listaMateriales;
    }

    private material hojas() {
        return new material("hojas", 4500, 500); //9
    }

    private material espiral() {
        return new material("espiral", 2300, 50); //46
    }

    private material tapas() {
        return new material("tapas", 2000, 50); //40
    }
}
