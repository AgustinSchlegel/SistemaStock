package viewmodel;

import java.util.List;

public class ProductoView {
    public final String nombre;
    public final double precio;
    public final int disponible;
    public final List<RequisitoView> requisitos;

    public ProductoView(String nombre, double precio, int disponible, List<RequisitoView> requisitos) {
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
        this.requisitos = requisitos;
    }

    @Override
    public String toString() {
        return nombre + " | $" + precio + " | stock: " + disponible;
    }
}
