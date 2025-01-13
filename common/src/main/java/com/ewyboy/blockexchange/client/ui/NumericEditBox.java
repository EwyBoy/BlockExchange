package com.ewyboy.blockexchange.client.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class NumericEditBox extends EditBox {

    public NumericEditBox(Font font, int width, int height, Component component) {
        super(font, width, height, component);
        this.setFilter(input -> input.matches("[0-9]*"));
    }

    public NumericEditBox(Font font, int x, int y, int width, int height, @Nullable EditBox editBox, Component component) {
        super(font, x, y, width, height, editBox, component);
        this.setFilter(input -> input.matches("[0-9]*"));
        this.setValue("0");
    }

    public NumericEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        this.setFilter(input -> input.matches("[0-9]*"));
        this.setValue("0");
    }

    /**
     * Get the numeric value of the edit box
     *
     * @return the numeric value of the edit box
     */
    public int getNumericValue() {
        try {
            return Integer.parseInt(this.getValue());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Set the numeric value of the edit box
     *
     * @param value the numeric value to set
     */
    public void setNumericValue(int value) {
        this.setValue(String.valueOf(value));
    }

    @Override
    public void setValue(String value) {
        super.setValue(value.isEmpty() ? "0" : value);
    }

}
