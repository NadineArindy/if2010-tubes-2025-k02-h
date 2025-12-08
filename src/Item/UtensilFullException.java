package src.Item;

import src.Exception.ItemException;

public class UtensilFullException extends ItemException { //kalo kapasitas kitchenutensils penuh

    public UtensilFullException(String message) {
        super(message);
        
    } 
    
}
