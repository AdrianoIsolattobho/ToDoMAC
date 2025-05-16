package controller;

import gui.Scelta;
import gui.LogIn;
import gui.Register;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller {
    private Scelta view;

    public Controller(Scelta view) {
        this.view = view;// ciao marta

        view.getLogInButton().addActionListener(e -> {
            view.mostraLogin();

            LogIn loginView = view.getLogInView();
            loginView.getBackButton().addActionListener(ev -> view.mostraScelta());
        });

        view.getRegistratiButton().addActionListener(e -> {
            view.mostraRegistrazione();

            Register registerView = view.getRegisterView();
            registerView.getBackButton().addActionListener(ev -> view.mostraScelta());
        });

    }

}
