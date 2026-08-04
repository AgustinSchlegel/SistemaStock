package Sistema;

public class material {
    private String nombre;
    private double precio;
    private int cantidad;
    private double precioUnidad;

    public material(String nombre, double precio, int cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        if (cantidad!=0){
            this.precioUnidad = precio/cantidad;
        }else{
            this.precioUnidad = 0;
        }
    }

    public void actualizarPrecio(double precio) {
        this.precio = precio;
        if (this.cantidad != 0) {
            this.precioUnidad = precio / this.cantidad;
        }
    }

    public void actualizarCant(int cantidad) {
        this.cantidad = cantidad;
        if (this.cantidad != 0) {
            this.precioUnidad = this.precio / this.cantidad;
        }
    }

    public double getPrecioUnidad() { return precioUnidad; }
    public int getCantidad() { return cantidad; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }

    // Este método nos ayuda a imprimir el objeto fácilmente en la consola
    @Override
    public String toString() {
        return "Material: " + nombre + " | Precio Total: $" + precio +
                " | Cantidad: " + cantidad + " | Precio/Unidad: $" + precioUnidad;
    }

}
