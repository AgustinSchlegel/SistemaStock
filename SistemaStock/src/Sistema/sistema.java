package Sistema;

import java.util.HashMap;

public class sistema {

    private HashMap<String, material> materiales;
    private HashMap<String, producto> productos;

    public sistema() {
        this.materiales = new HashMap<>();
        this.productos = new HashMap<>();
    }

    public void nuevoProducto(String nombre, double precio) {}

    public void nuevoMaterial() {}
    public void actualizarProducto() {}
    public void actualizarMaterial() {}
    public void calcularPrecioProducto() {}
    public void calcularPrecioMaterial() {}
    public void MostrarMateriales() {}
    public void MostrarProductos() {}
    public void MostrarTodo() {}
}

