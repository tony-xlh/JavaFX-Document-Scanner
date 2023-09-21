package com.dynamsoft.documentscanner;

import javafx.scene.control.ListCell;

public class DocumentImageCell extends ListCell<DocumentImage> {
    @Override
    public void updateItem(DocumentImage item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            item.imageView.setFitWidth(this.getListView().getWidth());
            setGraphic(item.imageView);
        }
    }
}