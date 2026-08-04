package viewmodel;

// DTO de solo lectura pensado para mostrarse en la interfaz gráfica.
// La View trabaja con esto, nunca con model.Material directamente.
public class MaterialView {
    public final String nombre;
    public final double precio;
    public final int cantidad;
    public final double precioUnidad;

    public MaterialView(String nombre, double precio, int cantidad, double precioUnidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.precioUnidad = precioUnidad;
    }

    @Override
    public String toString() {
        return nombre + " | $" + precio + " | cant: " + cantidad + " | $/u: " + precioUnidad;
    }
}
