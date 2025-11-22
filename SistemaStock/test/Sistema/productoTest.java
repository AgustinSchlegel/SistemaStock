package Sistema;

import auxiliares.tupla;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class productoTest {

    @Test
    void posibleProduccion() {
        tupla[] listaMateriales = getListaMateriales();
        producto libreta = new producto("libreta", listaMateriales);
        assertEquals(16, libreta.produccionPosible());
    }

    @Test
    void posiblePrecio() {
        tupla[] listaMateriales = getListaMateriales();
        producto libreta = new producto("libreta", listaMateriales);
        libreta.calcularPrecio();
        assertEquals(396, libreta.getPrecio() );
    }

    private tupla[] getListaMateriales() {
        tupla materiales = new tupla(hojas(), 30);
        tupla materiales2 = new tupla(espiral(), 1);
        tupla materiales3 = new tupla(tapas(), 2);
        tupla [] listaMateriales = new tupla[3];
        listaMateriales[0] = materiales;
        listaMateriales[1] = materiales2;
        listaMateriales[2] = materiales3;
        return listaMateriales;
    }

    private material hojas(){
        material material = new material("hojas", 4500, 500); //9
        return material;
    }

    private material espiral(){
        material material = new material("espiral", 2300, 50); //46
        return material;
    }

    private material tapas(){
        material material = new material("tapas", 2000, 50);//40
        return material;
    }

}