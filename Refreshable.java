package com.adaptivequiz.ui;

/** Panels implement this so the main window can reload their data when a tab is opened. */
public interface Refreshable {
    void refresh();
}
