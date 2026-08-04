package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaterialTest {

    @Test
    void calcularPrecioUnidad() {
        Material material = hojas();
        assertEquals(9, material.getPrecioUnidad());
    }

    @Test
    void calcularPrecioActualizado() {
        Material material = hojas();
        material.actualizarPrecio(3000);
        assertEquals(6, material.getPrecioUnidad());
    }

    @Test
    void actualizarCantidadModificaLaCantidad() {
        Material material = hojas();
        material.actualizarCant(3000);
        assertEquals(3000, material.getCantidad());
    }

    // Regresión del bug: consumir/ajustar stock NO debe alterar el costo por unidad.
    @Test
    void actualizarCantidadNoModificaElPrecioUnidad() {
        Material material = hojas();
        material.actualizarCant(380); // ej: producir() consumió 120 unidades
        assertEquals(9, material.getPrecioUnidad());
    }

    @Test
    void elPrecioTotalSeDerivaDelPrecioUnidadYLaCantidadActual() {
        Material material = hojas();
        material.actualizarCant(380);
        assertEquals(380 * 9, material.getPrecio());
    }

    @Test
    void cantidadCeroNoRompePorDivisionPorCero() {
        Material material = new Material("tinta", 500, 0);
        assertEquals(0, material.getPrecioUnidad());
    }

    @Test
    void establecerPrecioRequiereCargarLaCantidadPrimero() {
        // Si arrancás con cantidad 0, primero cargás el stock y recién después el costo:
        // actualizarPrecio() calcula el costo por unidad usando la cantidad ACTUAL.
        Material material = new Material("tinta", 0, 0);
        material.actualizarCant(50);
        material.actualizarPrecio(500);
        assertEquals(10, material.getPrecioUnidad());
    }

    @Test
    void getNombreYGetPrecioDevuelvenLoCargado() {
        Material material = hojas();
        assertEquals("hojas", material.getNombre());
        assertEquals(4500, material.getPrecio());
    }

    private Material hojas() {
        return new Material("hojas", 4500, 500);
    }
}
