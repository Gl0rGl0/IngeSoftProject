package GUI.it.proj.frame;

import java.util.ArrayList;
import java.util.List;

import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.Luogo;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LuoghiViewController implements ListViewController<Luogo> {
    public static final String ID = "luoghi";

    @FXML
    private ListView<Luogo> listLuoghi;

    private LuoghiVisiteViewController parent;

    @FXML
    private void initialize() {
        VBox.setVgrow(listLuoghi, Priority.ALWAYS);

        listLuoghi.setCellFactory(e -> new Cell<Luogo>(this, ID, true));
        listLuoghi.getItems().addAll(generateLuoghi(20));
    }

    public void setParentController(LuoghiVisiteViewController parent) {
        this.parent = parent;
    }

    @Override
    public void removeItem(Luogo luogo) {
        System.out.println("RIMUOVO " + luogo.getTitolo());
        listLuoghi.getItems().removeIf(p -> p.getTitolo().equals(luogo.getTitolo()));
    }

    // @Override
    public void addItem(Luogo luogo) {
        // AGGIUNGI A DB parent.addVisita...
        System.out.println("AGGIUNGO " + luogo.getTitolo());
        listLuoghi.getItems().add(luogo);
    }

    private List<Luogo> generateLuoghi(int count) {
        List<Luogo> visite = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            visite.add(new Luogo("luogo" + i, "descr" + i, "1:1"));
        }
        visite.add(new Luogo("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "1:1"));
        return visite;
    }

    @Override
    public void modifyItem(Luogo luogo) {
        System.out.println("MODIFICO " + luogo.getTitolo());
        parent.editItem(luogo);
    }

    @FXML
    public void onAggiungiLuogoClick() {
        System.out.println("AGGIUNGO");
        parent.addLuogo();
    }

}