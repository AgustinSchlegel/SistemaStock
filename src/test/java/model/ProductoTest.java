package model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductoTest {

    @Test
    void posibleProduccion() {
        Producto libreta = new Producto("libreta", getListaMateriales());
        assertEquals(16, libreta.produccionPosible());
    }

    @Test
    void posiblePrecio() {
        Producto libreta = new Producto("libreta", getListaMateriales());
        libreta.calcularPrecio();
        assertEquals(396, libreta.getPrecio());
    }

    @Test
    void producirDentroDeLoPosibleDescuentaMaterialesYSumaStock() {
        Material hojas = hojas();
        List<Tupla<Material, Integer>> lista = new ArrayList<>();
        lista.add(new Tupla<>(hojas, 30));
        Producto libreta = new Producto("libreta", lista);

        boolean resultado = libreta.producir(5);

        assertTrue(resultado);
        assertEquals(5, libreta.getDisponible());
        assertEquals(500 - 30 * 5, hojas.getCantidad());
    }

    @Test
    void producirMasDeLoPosibleNoModificaNada() {
        Material hojas = hojas();
        List<Tupla<Material, Integer>> lista = new ArrayList<>();
        lista.add(new Tupla<>(hojas, 30));
        Producto libreta = new Producto("libreta", lista);

        boolean resultado = libreta.producir(999);

        assertFalse(resultado);
        assertEquals(0, libreta.getDisponible());
        assertEquals(500, hojas.getCantidad());
    }

    @Test
    void producirDosVecesAcumulaStockEnVezDeSobreescribir() {
        Material hojas = hojas();
        List<Tupla<Material, Integer>> lista = new ArrayList<>();
        lista.add(new Tupla<>(hojas, 10));
        Producto libreta = new Producto("libreta", lista);

        libreta.producir(3);
        libreta.producir(2);

        assertEquals(5, libreta.getDisponible());
    }

    @Test
    void venderDentroDelStockDescuentaDisponible() {
        Producto libreta = new Producto("libreta", getListaMateriales());
        libreta.producir(5);

        boolean resultado = libreta.venderProducto(2);

        assertTrue(resultado);
        assertEquals(3, libreta.getDisponible());
    }

    @Test
    void venderMasQueElStockDisponibleFalla() {
        Producto libreta = new Producto("libreta", getListaMateriales());
        libreta.producir(2);

        boolean resultado = libreta.venderProducto(10);

        assertFalse(resultado);
        assertEquals(2, libreta.getDisponible());
    }

    @Test
    void produccionPosibleSinMaterialesEsCero() {
        Producto vacio = new Producto("vacio", new ArrayList<>());
        assertEquals(0, vacio.produccionPosible());
    }

    @Test
    void actualizarMaterialesRecalculaElPrecio() {
        Producto libreta = new Producto("libreta", getListaMateriales());
        assertEquals(396, libreta.getPrecio());

        List<Tupla<Material, Integer>> nuevaLista = new ArrayList<>();
        nuevaLista.add(new Tupla<>(hojas(), 10));
        libreta.actualizarMateriales(nuevaLista);

        assertEquals(90, libreta.getPrecio());
    }

    @Test
    void constructorDeReconstruccionRespetaElStockGuardado() {
        Producto libreta = new Producto("libreta", getListaMateriales(), 7);
        assertEquals(7, libreta.getDisponible());
        assertEquals(396, libreta.getPrecio());
    }

    private List<Tupla<Material, Integer>> getListaMateriales() {
        List<Tupla<Material, Integer>> listaMateriales = new ArrayList<>();
        listaMateriales.add(new Tupla<>(hojas(), 30));
        listaMateriales.add(new Tupla<>(espiral(), 1));
        listaMateriales.add(new Tupla<>(tapas(), 2));
        return listaMateriales;
    }

    private Material hojas() {
        return new Material("hojas", 4500, 500); //9
    }

    private Material espiral() {
        return new Material("espiral", 2300, 50); //46
    }

    private Material tapas() {
        return new Material("tapas", 2000, 50); //40
    }
}
