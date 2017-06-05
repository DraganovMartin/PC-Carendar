package views.listView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;



/**
 * Created by dimcho on 05.06.17.
 */
public class listItemController {
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblProductionYear;
    @FXML
    private Label lblRange;
    @FXML
    private ImageView imageView;
    @FXML
    private AnchorPane itemView;

    public listItemController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("list_view_item.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        }
        catch (Exception e) {
           e.printStackTrace();
        }
    }

    public void setTitle(String title) {
       lblTitle.setText(title);
    }


    public void setProductionYear(int productionYear) {
        lblProductionYear.setText(String.valueOf(productionYear));
    }

    public void setRange(String range) {
       lblRange.setText(range);
    }

    public void setImage(String imagePath) {
        imageView.setImage(new Image(imagePath));
    }

    public AnchorPane getItemView(){
        return itemView;
    }
}
