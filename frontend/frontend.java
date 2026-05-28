package frontend;

import javax.swing.*;
import com.formdev.flatlaf.FlatDarkLaf;

/**
 * frontend — ponto de entrada do SIRATECH.
 * MainFrame::new chama o construtor sem parâmetros, que busca Backend.getInstance() internamente.
 */
public class frontend {

    public static void main(String[] args) {
        // Silencia todo output do terminal (logs, prints de debug, etc.)
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        System.setErr(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));

        // FlatLaf Dark — compatível com tema escuro do SIRATECH
        FlatDarkLaf.setup();

        // Cor global para todos os ícones SVG do sistema
        UIManager.put("Objects.Grey", Tema.TEXT3);

        // Tweaks globais para o tema escuro (chaves corretas para FlatLaf)
        UIManager.put("OptionPane.background",              Tema.BG);
        UIManager.put("Panel.background",                   Tema.BG);
        UIManager.put("OptionPane.messageForeground",       Tema.TEXT);

        // Botões — FlatLaf usa essas chaves específicas
        UIManager.put("Button.background",                  Tema.CARD);
        UIManager.put("Button.foreground",                  Tema.TEXT);
        UIManager.put("Button.hoverBackground",             Tema.BG3);
        UIManager.put("Button.pressedBackground",           Tema.BG2);
        UIManager.put("Button.default.background",          Tema.GREEN);
        UIManager.put("Button.default.foreground",          Tema.TEXT);
        UIManager.put("Button.default.hoverBackground",     Tema.GREEN2);
        UIManager.put("Button.arc",                         6);
        UIManager.put("Button.borderWidth",                 1);

        UIManager.put("TextField.background",               Tema.BG3);
        UIManager.put("TextField.foreground",               Tema.TEXT);
        UIManager.put("TextField.caretForeground",          Tema.GREENL);

        UIManager.put("ComboBox.background",                Tema.BG3);
        UIManager.put("ComboBox.foreground",                Tema.TEXT);

        UIManager.put("Label.foreground",                   Tema.TEXT);

        UIManager.put("ScrollPane.background",              Tema.BG3);

        UIManager.put("Table.background",                   Tema.BG3);
        UIManager.put("Table.foreground",                   Tema.TEXT2);
        UIManager.put("Table.selectionBackground",          new java.awt.Color(34, 80, 36));
        UIManager.put("Table.selectionForeground",          Tema.TEXT);

        UIManager.put("TableHeader.background",             Tema.CARD);
        UIManager.put("TableHeader.foreground",             Tema.TEXT3);

        // Cores globais do FlatLaf
        UIManager.put("Component.focusColor",               Tema.GREEN);
        UIManager.put("Component.borderColor",              Tema.BORDER);
        UIManager.put("Component.disabledBorderColor",      Tema.BORDER);

        SwingUtilities.invokeLater(MainFrame::new);
    }
}
