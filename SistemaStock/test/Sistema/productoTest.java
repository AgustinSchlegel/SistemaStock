package Sistema;


import auxiliares.tupla;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

class productoTest {

    @Test
    void posibleProduccion() {
        List listaMateriales = getListaMateriales();
        producto libreta = new producto("libreta", listaMateriales);
        assertEquals(16, libreta.produccionPosible());
    }

    @Test
    void posiblePrecio() {
        List listaMateriales = getListaMateriales();
        producto libreta = new producto("libreta", listaMateriales);
        libreta.calcularPrecio();
        assertEquals(396, libreta.getPrecio() );
    }

    private List<tupla<material, Integer>> getListaMateriales() {
        tupla materiales = new tupla(hojas(), 30);
        tupla materiales2 = new tupla(espiral(), 1);
        tupla materiales3 = new tupla(tapas(), 2);
        List<tupla<material, Integer>> listaMateriales = new ArrayList<>();
        listaMateriales.add(materiales);
        listaMateriales.add(materiales2);
        listaMateriales.add(materiales3);
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