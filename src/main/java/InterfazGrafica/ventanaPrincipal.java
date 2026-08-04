package InterfazGrafica;

import viewmodel.StockViewModel;

import javax.swing.*;

public class ventanaPrincipal extends JFrame {

    public ventanaPrincipal(StockViewModel viewModel) {
        setTitle("Sistema de Stock");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.addTab("Materiales", new MaterialesPanel(viewModel));
        pestanias.addTab("Productos", new ProductosPanel(viewModel));

        setContentPane(pestanias);
    }
}
