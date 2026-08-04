package model;

import java.util.List;

public class Producto {
    private String nombre;
    private double precio;
    private List<Tupla<Material, Integer>> materiales;
    private int disponible;

    public Producto(String nombre, List<Tupla<Material, Integer>> materiales) {
        this.nombre = nombre;
        this.materiales = materiales;
        // Al crear un producto por primera vez, el disponible asumo que es 0
        this.disponible = 0;
        calcularPrecio();
    }

    // Constructor usado para reconstruir un producto ya existente (ej: al cargar desde JSON),
    // donde el stock disponible no debe reiniciarse a 0.
    public Producto(String nombre, List<Tupla<Material, Integer>> materiales, int disponible) {
        this.nombre = nombre;
        this.materiales = materiales;
        this.disponible = disponible;
        calcularPrecio();
    }

    public int produccionPosible() {
        if (materiales == null || materiales.isEmpty()) return 0;

        int produccionPosible = Integer.MAX_VALUE;

        for (Tupla<Material, Integer> tupla : this.materiales) {
            if (tupla != null && tupla.getItem1() != null) {
                Material mat = tupla.getItem1();
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

        for (Tupla<Material, Integer> tupla : this.materiales) {
            if (tupla != null && tupla.getItem1() != null) {
                Material material = tupla.getItem1();
                int cantidad = tupla.getItem2();
                nuevoPrecio += cantidad * material.getPrecioUnidad();
            }
        }
        this.precio = nuevoPrecio;
    }

    public boolean producir(int produccion) {
        if (produccionPosible() >= produccion) {
            for (Tupla<Material, Integer> tupla : materiales) {
                int cantidadActual = tupla.getItem1().getCantidad();
                int cantidadAProducir = tupla.getItem2() * produccion;
                tupla.getItem1().actualizarCant(cantidadActual - cantidadAProducir);
            }
            this.disponible += produccion;
            return true;
        }
        return false;
    }

    public boolean venderProducto(int venta) {
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

    public List<Tupla<Material, Integer>> getMateriales() {
        return materiales;
    }

    public void actualizarMateriales(List<Tupla<Material, Integer>> materiales) {
        this.materiales = materiales;
        calcularPrecio();
    }

    public void actualizarNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Producto: " + nombre + " | Precio: $" + precio + " | Stock: " + disponible;
    }

}
