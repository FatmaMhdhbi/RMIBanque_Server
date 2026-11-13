package rmiService;

import metier.Compte;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class BanqueImpl extends UnicastRemoteObject implements IBanque {

    // Stocker les comptes bancaires
    private Map<Integer, Compte> comptes;

    // Constructeur
    public BanqueImpl() throws RemoteException {
        super();
        comptes = new HashMap<>();
    }

    // Créer un compte
    @Override
    public String creerCompte(Compte c) throws RemoteException {

        if (comptes.containsKey(c.getCode())) {
            return "Ce compte existe déjà.";
        }

        comptes.put(c.getCode(), c);
        return "Compte créé avec succès.";
    }

    // Chercher un compte
    @Override
    public String getInfoCompte(int code) throws RemoteException {

        Compte c = comptes.get(code);

        if (c == null) {
            return "Compte introuvable.";
        }

        return c.toString();
    }
}
