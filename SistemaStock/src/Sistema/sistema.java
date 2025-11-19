package Sistema;

import auxiliares.tupla;

import java.util.HashMap;

public class sistema {

    private HashMap<String, material> materiales;
    private HashMap<String, producto> productos;

    public sistema() {
        this.materiales = new HashMap<>();
        this.productos = new HashMap<>();
    }

    public void nuevoMaterial(String nombre, double precio, int cantidad) {
        material material = new material(nombre, precio, cantidad);
        this.materiales.put(nombre, material);
    }

    public void nuevoProducto(String nombre, tupla<material, Integer>[] materiales) {
        producto producto = new producto(nombre, materiales);
        this.productos.put(nombre, producto);
    }

    public void actualizarProducto() {}

    public <T extends Number> void actualizarMaterial(T datoAActualizar, String nombre, boolean dato) {
        if (dato) {
            materiales.get(nombre).actualizarCant((Integer)datoAActualizar);
        }else {
            materiales.get(nombre).actualizarPrecio((Double) datoAActualizar);
        }
    }

    public void calcularPrecioProducto() {}

    public void calcularPrecioMaterial() {}

    public void MostrarMateriales() {}

    public void MostrarProductos() {}

    public void MostrarTodo() {}

}

