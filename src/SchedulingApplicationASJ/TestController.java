/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SchedulingApplicationASJ;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TestController {

    @FXML
    private Label testLabel;

    @FXML
    void onClick(ActionEvent event) {
        System.out.println("Okay - you clicked me!");
        testLabel.setText("This has been a test");

    }

}

