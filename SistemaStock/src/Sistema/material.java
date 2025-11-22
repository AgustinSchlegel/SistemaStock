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
        this.precioUnidad = precio/cantidad;
    }

    public void actualizarCant(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnidad() {
        return precioUnidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String ToString() {
        return this.nombre;
    }

}
