import model.Sistema;
import repository.JsonMaterialRepository;
import repository.JsonProductoRepository;
import viewmodel.StockViewModel;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        Sistema sistema = new Sistema(new JsonMaterialRepository(), new JsonProductoRepository());
        StockViewModel viewModel = new StockViewModel(sistema);

        SwingUtilities.invokeLater(() -> new InterfazGrafica.ventanaPrincipal(viewModel).setVisible(true));
    }
}
