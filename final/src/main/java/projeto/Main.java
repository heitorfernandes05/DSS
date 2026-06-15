package projeto;

import projeto.business.ILNFacade;
import projeto.business.LNFacade;
import projeto.ui.TextUI;

public class Main {
    public static void main(String[] args) {
        try {
            ILNFacade facade = new LNFacade();
            
            TextUI ui = new TextUI(facade);
            ui.run();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}