package Sistema;

import Sistema.Gestores.gestorMaterial;
import Sistema.Gestores.gestorProducto;
import auxiliares.tupla;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class sistema {

    private HashMap<String, material> materiales;
    private HashMap<String, producto> productos;

    public sistema() {
        this.materiales = new HashMap<>();
        this.productos = new HashMap<>();
        cargarDatos();
    }

    // Carga lo guardado en disco al arrancar el programa.
    private void cargarDatos() {
        for (material m : gestorMaterial.leerMateriales()) {
            this.materiales.put(m.getNombre(), m);
        }
        // Los productos necesitan los materiales ya cargados para reconstruir sus referencias
        for (producto p : gestorProducto.leerProductos(this.materiales)) {
            this.productos.put(p.getNombre(), p);
        }
    }

    // Persiste el estado completo. Se llama después de cualquier operación que modifique datos.
    private void guardarTodo() {
        gestorMaterial.guardarMateriales(new ArrayList<>(this.materiales.values()));
        gestorProducto.guardarProductos(new ArrayList<>(this.productos.values()));
    }

    public void nuevoMaterial(String nombre, double precio, int cantidad) {
        material material = new material(nombre, precio, cantidad);
        this.materiales.put(nombre, material);
        guardarTodo();
    }

    public void nuevoProducto(String nombre, List<tupla<material, Integer>> materiales) {
        producto producto = new producto(nombre, materiales);
        this.productos.put(nombre, producto);
        guardarTodo();
    }

    public <T> void actualizarProducto(String producto, T valorActualizar, boolean dato) {
        if (this.productos.containsKey(producto)) {
            if (dato) {
                productos.get(producto).actualizarNombre((String) valorActualizar);
            }else {
                productos.get(producto).actualizarMateriales((List<tupla<material, Integer>>) valorActualizar);
            }
            guardarTodo();
        }

    }

    public <T extends Number> void actualizarMaterial(T datoAActualizar, String nombre, boolean dato) {
        if (this.materiales.containsKey(nombre)) {
            if (dato) {
                materiales.get(nombre).actualizarCant((Integer)datoAActualizar);
            }else {
                materiales.get(nombre).actualizarPrecio((Double) datoAActualizar);
            }
            guardarTodo();
        }
    }

    // Antes no existía forma de producir/vender desde sistema: producto.producir() y
    // venderProducto() estaban implementados pero nada los exponía hacia afuera.
    public boolean producirProducto(String nombreProducto, int cantidad) {
        if (!this.productos.containsKey(nombreProducto)) return false;
        boolean resultado = productos.get(nombreProducto).producir(cantidad);
        if (resultado) guardarTodo(); // el stock de materiales también cambió
        return resultado;
    }

    public boolean venderProducto(String nombreProducto, int cantidad) {
        if (!this.productos.containsKey(nombreProducto)) return false;
        boolean resultado = productos.get(nombreProducto).venderProducto(cantidad);
        if (resultado) guardarTodo();
        return resultado;
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

    public producto simularProducto(String nombre, List<tupla<material, Integer>> materiales) {
        producto producto = new producto(nombre, materiales);
        producto.produccionPosible();
        producto.getPrecio();
        return producto;
    }

    public ArrayList<String> MostrarMateriales() {
        ArrayList<String> materiales = new ArrayList<>();
        for (material material : this.materiales.values()) {
            materiales.add(material.toString());
        }
        return  materiales;
    }

    public ArrayList<String> MostrarProductos() {
        ArrayList<String> productos = new ArrayList<>();
        for (producto producto : this.productos.values()) {
            productos.add(producto.toString());
        }
    return  productos;
    }

    // Getters necesarios para que la interfaz gráfica (o los tests) trabajen con los
    // objetos reales y no solo con strings de toString().
    public material getMaterial(String nombre) {
        return this.materiales.get(nombre);
    }

    public producto getProducto(String nombre) {
        return this.productos.get(nombre);
    }

    public List<material> getMateriales() {
        return new ArrayList<>(this.materiales.values());
    }

    public List<producto> getProductos() {
        return new ArrayList<>(this.productos.values());
    }

    public boolean existeMaterial(String nombre) {
        return this.materiales.containsKey(nombre);
    }

    public boolean existeProducto(String nombre) {
        return this.productos.containsKey(nombre);
    }

}
