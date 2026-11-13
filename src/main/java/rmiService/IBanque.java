package rmiService;

import metier.Compte;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBanque extends Remote {

    // Créer un nouveau compte bancaire
    String creerCompte(Compte c) throws RemoteException;

    // Chercher les informations d'un compte
    String getInfoCompte(int code) throws RemoteException;
}
