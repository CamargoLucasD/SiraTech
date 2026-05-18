package frontend;

import javax.swing.*;

/**
 * frontend — ponto de entrada do SIRATECH.
 * MainFrame::new chama o construtor sem parâmetros, que busca Backend.getInstance() internamente.
 */
public class frontend {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tweaks globais para o tema escuro
        UIManager.put("OptionPane.background",        Tema.BG);
        UIManager.put("Panel.background",             Tema.BG);
        UIManager.put("OptionPane.messageForeground", Tema.TEXT);
        UIManager.put("Button.background",            Tema.CARD);
        UIManager.put("Button.foreground",            Tema.TEXT);
        UIManager.put("TextField.background",         Tema.BG3);
        UIManager.put("TextField.foreground",         Tema.TEXT);
        UIManager.put("ComboBox.background",          Tema.BG3);
        UIManager.put("ComboBox.foreground",          Tema.TEXT);
        UIManager.put("Label.foreground",             Tema.TEXT);
        UIManager.put("ScrollPane.background",        Tema.BG3);
        UIManager.put("Table.background",             Tema.BG3);
        UIManager.put("Table.foreground",             Tema.TEXT2);
        UIManager.put("TableHeader.background",       Tema.CARD);
        UIManager.put("TableHeader.foreground",       Tema.TEXT3);

        SwingUtilities.invokeLater(MainFrame::new);
    }
}
