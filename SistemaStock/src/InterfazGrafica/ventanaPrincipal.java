package InterfazGrafica;

import javax.swing.*;
import java.awt.*;

public class ventanaPrincipal extends JFrame {

    public  ventanaPrincipal() {
        setTitle("Menu Principal");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton verProductos = new JButton("Ver Productos");
        verProductos.setPreferredSize(new Dimension(200, 50));
        verProductos.addActionListener(e -> {

            String cuadrado = JOptionPane.showInputDialog("Ingrese tamaño del tablero");

        });

        JButton verMateriales = new JButton("Ver Materiales");
        verMateriales.setPreferredSize(new Dimension(200, 50));
        verMateriales.addActionListener(e -> {

            String cuadrado = JOptionPane.showInputDialog("Ingrese tamaño del tablero");


        });
    }


    private void mostrarProductos() {
        JPanel panel = new JPanel();
        panel.setVisible(true);
        this.dispose();
    }

    private void mostrarMateriales() {
        JPanel panel = new JPanel();
        panel.setVisible(true);
        this.dispose();
    }
}
