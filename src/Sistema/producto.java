package Sistema;

import auxiliares.tupla;

import java.util.List;

public class producto {
    private String nombre;
    private double precio;
    private List<tupla <material, Integer>> materiales;
    private int disponible;

    public producto(String nombre, List<tupla<material, Integer>> materiales) {
        this.nombre = nombre;
        this.materiales = materiales;
        // Al crear un producto por primera vez, el disponible asumo que es 0
        this.disponible = 0;
        calcularPrecio();
    }

    // Constructor usado para reconstruir un producto ya existente (ej: al cargar desde JSON),
    // donde el stock disponible no debe reiniciarse a 0.
    public producto(String nombre, List<tupla<material, Integer>> materiales, int disponible) {
        this.nombre = nombre;
        this.materiales = materiales;
        this.disponible = disponible;
        calcularPrecio();
    }

    public int produccionPosible() {
        if (materiales == null || materiales.isEmpty()) return 0;

        int produccionPosible = Integer.MAX_VALUE;

        for (tupla<material, Integer> tupla : this.materiales) {
            if (tupla != null && tupla.getItem1() != null) {
                material mat = tupla.getItem1();
                int cantidadNecesaria = tupla.getItem2();

                if (cantidadNecesaria > 0) {
                    int unidadesQuePuedoHacer = mat.getCantidad() / cantidadNecesaria;
                    if (unidadesQuePuedoHacer < produccionPosible) {
                        produccionPosible = unidadesQuePuedoHacer;
                    }
                }
            }
        }
        return (produccionPosible == Integer.MAX_VALUE) ? 0 : produccionPosible;
    }

    public void calcularPrecio() {
        if (materiales == null) {
            this.precio = 0;
            return;
        }

        double nuevoPrecio = 0;

        for (tupla<material, Integer> tupla : this.materiales) {
            // CORREGIDO: cambiado || por && y == por !=
            if (tupla != null && tupla.getItem1() != null) {
                material material = tupla.getItem1();
                int cantidad = tupla.getItem2();
                nuevoPrecio += cantidad * material.getPrecioUnidad();
            }
        }
        this.precio = nuevoPrecio;
    }

    public boolean producir(int produccion) {
        // CORREGIDO: >= para permitir producir el máximo exacto
        if (produccionPosible() >= produccion) {
            for (tupla<material, Integer> tupla : materiales) {
                int cantidadActual = tupla.getItem1().getCantidad();
                int cantidadAProducir = tupla.getItem2() * produccion;
                tupla.getItem1().actualizarCant(cantidadActual - cantidadAProducir);
            }
            // CORREGIDO: Sumar al stock existente, no sobrescribirlo
            this.disponible += produccion;
            return true;
        }
        return false;
    }

    public boolean venderProducto(int venta) {
        // CORREGIDO: >= y -=
        if (disponible >= venta) {
            this.disponible -= venta;
            return true;
        }
        return false;
    }

    public double getPrecio() {
        return precio;
    }

    public int getDisponible() {
        return disponible;
    }

    public String getNombre() {
        return nombre;
    }

    public List<tupla<material, Integer>> getMateriales() {
        return materiales;
    }

    public void actualizarMateriales(List<tupla<material, Integer>> materiales) {
        this.materiales = materiales;
        calcularPrecio(); // Buena práctica: recalcular si cambian los materiales
    }

    public void actualizarNombre(String nombre) {
        this.nombre = nombre;
    }

    // CORREGIDO: Minúscula y anotación
    @Override
    public String toString() {
        return "Producto: " + nombre + " | Precio: $" + precio + " | Stock: " + disponible;
    }

}
