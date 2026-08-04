package Sistema;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class materialTest {

    @Test
    void calcularPrecioUnidad() {
        material material = hojas();
        assertEquals(9, material.getPrecioUnidad());
    }

    @Test
    void calcularPrecioActualizado() {
        material material = hojas();
        material.actualizarPrecio(3000);
        assertEquals(6, material.getPrecioUnidad());
    }

    @Test
    void actualizarCantidad() {
        material material = hojas();
        material.actualizarCant(3000);
        assertEquals(3000, material.getCantidad());
    }

    @Test
    void cantidadCeroNoRompePorDivisionPorCero() {
        material material = new material("tinta", 500, 0);
        assertEquals(0, material.getPrecioUnidad());
    }

    @Test
    void actualizarCantidadRecalculaPrecioUnidad() {
        material material = new material("tinta", 500, 0);
        material.actualizarCant(50);
        assertEquals(10, material.getPrecioUnidad());
    }

    @Test
    void getNombreYGetPrecioDevuelvenLoCargado() {
        material material = hojas();
        assertEquals("hojas", material.getNombre());
        assertEquals(4500, material.getPrecio());
    }

    private material hojas() {
        return new material("hojas", 4500, 500);
    }
}
