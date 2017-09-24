package views.listView;

import javafx.scene.control.ListCell;
import model.Vehicle.Car;
import model.Vehicle.Motorcycle;
import model.Vehicle.Vehicle;

/**
 * Created by dimcho on 05.06.17.
 *
 * This class binds the vehicle data to the gui and sets a new view representing the
 * list view's custom item
 */
public class VehicleCellAdapter extends ListCell<Vehicle> {
    @Override
    public void updateItem(Vehicle v, boolean empty){
        super.updateItem(v,empty);

        if(empty || v == null){
            setText(null);
            setGraphic(null);
        }else{
            // A new item view instance ( holds the individual views )
            listItemController itemController = new listItemController();

            // Bind the view to the data
            if(v instanceof Car){
                Car car = (Car) v;

                itemController.setTitle(car.getBrand() + " " + car.getModel());
                itemController.setProductionYear(car.getProductionYear());
                itemController.setRange(car.getKmRange());

                String pathToImage = car.getPathToImage();
                if(pathToImage == null){
                    itemController.setImage("/resources/carDefaultIcon.png");
                }else{
                    itemController.setImage(pathToImage);
                }
            }else{
                Motorcycle motorcycle = (Motorcycle) v;

                itemController.setTitle(motorcycle.getBrand() + " " + motorcycle.getModel());
                itemController.setProductionYear(motorcycle.getProductionYear());
                itemController.setRange(motorcycle.getKmRange());

                String pathToImage = motorcycle.getPathToImage();
                if(pathToImage == null){
                    itemController.setImage("/resources/motorcycleDefaultIcon.png");
                }else{
                    itemController.setImage(pathToImage);
                }
            }

            // Sets the custom view (the AnchorPane) for the list cell
            setGraphic(itemController.getItemView());
        }
    }
}
