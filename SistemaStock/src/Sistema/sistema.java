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

    public <T> void actualizarProducto(String producto, T valorActualizar, boolean dato) {
        if (this.productos.containsKey(producto)) {
            if (dato) {
                productos.get(producto).actualizarNombre((String) valorActualizar);
            }else {
                productos.get(producto).actualizarMateriales((tupla<material, Integer>[]) valorActualizar);
            }
        }

    }

    public <T extends Number> void actualizarMaterial(T datoAActualizar, String nombre, boolean dato) {
        if (this.materiales.containsKey(nombre)) {
            if (dato) {
                materiales.get(nombre).actualizarCant((Integer)datoAActualizar);
            }else {
                materiales.get(nombre).actualizarPrecio((Double) datoAActualizar);
            }
        }
    }

    public double calcularPrecioProducto(String nombreProducto) {
        if (this.productos.containsKey(nombreProducto)) {
            productos.get(nombreProducto).calcularPrecio();
            return productos.get(nombreProducto).getPrecio();
        }
        return -1;
    }

    public double calcularPrecioMaterial(String nombreMaterial) {
        if (this.materiales.containsKey(nombreMaterial)) {
            return materiales.get(nombreMaterial).getPrecioUnidad();
        }
        return -1;
    }
/// ///////////////////////////////////////////
/// A TERMINAR DE DESARROLLAR @TODO
/// ///////////////////////////////////////////
    public HashMap MostrarMateriales() {
        return  this.materiales;
    }

    public HashMap MostrarProductos() {
        return  this.productos;
    }

    public HashMap MostrarTodo() {
        return MostrarMateriales() + MostrarProductos();
    }

}

