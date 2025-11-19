package Sistema;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class materialTest {

    @Test
    void calcularPrecioUnidad() {
        material material = hojas();

        assertEquals(9,material.getPrecioUnidad());
    }

    @Test
    void calcularPrecioActualizado() {
        material material = hojas();
        material.actualizarPrecio(3000);
        assertEquals(6,material.getPrecioUnidad());
    }

    @Test
    void actualizarCantidad() {
        material material = hojas();
        material.actualizarCant(3000);
        assertEquals(3000,material.getCantidad());
    }

    private material hojas(){
        material material = new material("hojas", 4500, 500);
        return material;
    }
}