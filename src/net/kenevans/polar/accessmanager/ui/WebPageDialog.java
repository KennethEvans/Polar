package net.kenevans.polar.accessmanager.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/*
 * Created on Jan 31, 2019
 * By Kenneth Evans, Jr.
 */

public class WebPageDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    private static String CODE_IDENTIFIER = "?code=";
    public static final String CODE_CHANGED = "propertyChanged";
    private String oldCode;

    public WebPageDialog(PropertyChangeListener listener, String url) {
        setSize(600, 500);
        final JFXPanel fxPanel = new JFXPanel();
        this.getContentPane().add(fxPanel);
        this.addPropertyChangeListener(CODE_CHANGED, listener);

        // Center on screen
        final Dimension screenSize = Toolkit.getDefaultToolkit()
            .getScreenSize();
        int x = (screenSize.width / 2) - (this.getSize().width / 2);
        int y = (screenSize.height / 2) - (this.getSize().height / 2);
        setLocation(x, y);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebEngine engine;
                WebView browser = new WebView();
                engine = browser.getEngine();
                VBox vbox = new VBox(10);
                Label status = new Label("Waiting for code");
                VBox.setMargin(status, new Insets(5, 20, 5, 20));
                Label location = new Label();
                VBox.setMargin(location, new Insets(5, 20, 5, 20));
                location.textProperty().bind(engine.locationProperty());
                location.textProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        if(newValue.contains(CODE_IDENTIFIER)) {
                            int index = newValue.indexOf(CODE_IDENTIFIER);
                            String code = newValue
                                .substring(index + CODE_IDENTIFIER.length());
                            if(!newValue.equals(oldCode)) {
                                status.setText("Got code=" + code);
                                firePropertyChange(CODE_CHANGED, oldCode, code);
                                oldCode = code;
                            }
                        }
                    });
                vbox.getChildren().addAll(location, browser, status);
                Scene scene = new Scene(vbox);
                fxPanel.setScene(scene);
                engine.load(url);
            }
        });
    }

}
