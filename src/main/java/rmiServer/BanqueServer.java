package rmiServer;

import rmiService.BanqueImpl;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;

public class BanqueServer {

    public static void main(String[] args) {

        try {
            // Activer le RMIRegistry sur le port 1099
            LocateRegistry.createRegistry(1099);

            // Créer une instance de BanqueImpl
            BanqueImpl banque = new BanqueImpl();

            // Publier le service dans le registre RMI
            Naming.rebind("rmi://localhost:1099/Banque", banque);

            System.out.println("Serveur Banque RMI démarré...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
