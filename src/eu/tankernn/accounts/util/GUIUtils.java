package eu.tankernn.accounts.util;

import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

public class GUIUtils {
	public static <T> DefaultListModel<T> listModelFromList(List<T> list) {
		DefaultListModel<T> model = new DefaultListModel<T>();
		list.forEach(t -> model.addElement(t));
		return model;
	}
	
	public static <T> ComboBoxModel<T> comboBoxModelFromList(List<T> list) {
		DefaultComboBoxModel<T> model = new DefaultComboBoxModel<T>();
		list.forEach(t -> model.addElement(t));
		return model;
	}
}
