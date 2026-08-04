package model;

public class Material {
    private String nombre;
    private int cantidad;
    private double precioUnidad;

    public Material(String nombre, double precioCompraTotal, int cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnidad = calcularPrecioUnidad(precioCompraTotal, cantidad);
    }

    // Registra el costo de una compra: pagaste precioCompraTotal por la cantidad
    // actualmente en stock. Fija el costo por unidad (precioUnidad), que se mantiene
    // constante hasta la próxima vez que se llame a este método.
    public void actualizarPrecio(double precioCompraTotal) {
        this.precioUnidad = calcularPrecioUnidad(precioCompraTotal, this.cantidad);
    }

    // Actualiza SOLO la cantidad en stock (por ejemplo al producir/vender productos que
    // consumen este material, o al corregir un conteo de inventario). A propósito NO toca
    // precioUnidad: el costo por unidad ya fijado no debe cambiar por el simple hecho de
    // gastar o reponer stock (antes sí lo hacía, y ahí estaba el bug).
    public void actualizarCant(int cantidad) {
        this.cantidad = cantidad;
    }

    private static double calcularPrecioUnidad(double precioTotal, int cantidad) {
        return (cantidad != 0) ? precioTotal / cantidad : 0;
    }

    public double getPrecioUnidad() { return precioUnidad; }
    public int getCantidad() { return cantidad; }
    public String getNombre() { return nombre; }

    // El precio total del stock actual se DERIVA del costo unitario ya fijado y de la
    // cantidad en stock. Nunca se guarda por separado, así nunca puede desincronizarse.
    public double getPrecio() {
        return precioUnidad * cantidad;
    }

    // Este método nos ayuda a imprimir el objeto fácilmente en la consola
    @Override
    public String toString() {
        return "Material: " + nombre + " | Precio Total: $" + getPrecio() +
                " | Cantidad: " + cantidad + " | Precio/Unidad: $" + precioUnidad;
    }

}
