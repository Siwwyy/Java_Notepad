package np;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class Notepad extends JFrame {

    private JTextArea mainTarea;
    private JMenuBar mnuBar;
    private JMenu mnufile, mnuedit;
    private JMenuItem itmnew, itmopen, itmsave, itmcut, itmcopy, itmpaste, itmundo, itmredo, itmfont ;
    private ImageIcon iconnew, iconopen, iconsave, iconcut, iconcopy, iconpaste, iconundo, iconredo, iconfont;
    private JToolBar toolbar;
    private JButton btnnew;
    private String filename;
    private JFileChooser fc;
    private String fileContent;
    private UndoManager undoManager;
    FontClass font;

    public Notepad() {
        initcomponents();
    }

    private void initcomponents() {
        //creating objects
        font = new FontClass();
        fc = new JFileChooser(".");
        undoManager = new UndoManager();
        mainTarea = new JTextArea();
        toolbar = new JToolBar("Toolbar");
        iconnew = new ImageIcon(getClass().getResource("/img/new.gif"));
        iconopen = new ImageIcon(getClass().getResource("/img/open.gif"));
        iconsave = new ImageIcon(getClass().getResource("/img/save.gif"));
        iconcut = new ImageIcon(getClass().getResource("/img/cut.gif"));
        iconcopy = new ImageIcon(getClass().getResource("/img/copy.gif"));
        iconpaste = new ImageIcon(getClass().getResource("/img/paste.gif"));
        iconundo = new ImageIcon(getClass().getResource("/img/undo.gif"));
        iconredo = new ImageIcon(getClass().getResource("/img/redo.gif"));
        iconfont = new ImageIcon(getClass().getResource("/img/font.gif"));
        mnuBar = new JMenuBar();
        mnufile = new JMenu("File");
        mnuedit = new JMenu("Edit");
        itmnew = new JMenuItem("New", iconnew);
        itmopen = new JMenuItem("Open", iconopen);
        itmsave = new JMenuItem("Save", iconsave);
        itmundo = new JMenuItem("Undo", iconundo);
        itmredo = new JMenuItem("Redo", iconredo);
        itmcut = new JMenuItem("Cut", iconcut);
        itmcopy = new JMenuItem("Copy", iconcopy);
        itmpaste = new JMenuItem("Paste", iconpaste);
        itmfont = new JMenuItem("Font", iconfont);
        btnnew = new JButton(iconnew);
        toolbar.add(btnnew);
        add(toolbar, BorderLayout.NORTH);

        //adding menu item to menu
        mnufile.add(itmnew);
        mnufile.add(itmopen);
        mnufile.add(itmsave);
        mnuedit.add(itmundo);
        mnuedit.add(itmredo);
        mnuedit.add(itmcut);
        mnuedit.add(itmcopy);
        mnuedit.add(itmpaste);
        mnuedit.add(itmfont);

        //adding menu to menubar
        mnuBar.add(mnufile);
        mnuBar.add(mnuedit);

        //adding menubar to frame
        setJMenuBar(mnuBar);
        itmundo.setEnabled(false);
        itmredo.setEnabled(false);

        mainTarea.setFont(new Font("Arial", Font.PLAIN, 14));
        add(new JScrollPane(mainTarea), BorderLayout.CENTER);
        setTitle("Untitled Notepad");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //set shortcut
        itmsave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

        //applying action
        itmsave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        itmopen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                open();
            }
        });
        itmnew.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openNew();
            }
        });
        btnnew.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openNew();
            }
        });
        itmcut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mainTarea.cut();
            }
        });
        itmcopy.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mainTarea.copy();
            }
        });
        itmpaste.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mainTarea.paste();
            }
        });
        mainTarea.getDocument().addUndoableEditListener(
                new UndoableEditListener() {

            @Override
                    public void undoableEditHappened(UndoableEditEvent e) {
                        undoManager.addEdit(e.getEdit());
                        updateButtons();
                    }
                });
        itmundo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    undoManager.undo();
                } catch (CannotUndoException cre) {
                    cre.printStackTrace();
                }
                updateButtons();
            }
        });
        itmredo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    undoManager.redo();
                } catch (CannotRedoException cre) {
                    cre.printStackTrace();
                }
                updateButtons();
            }
        });
        itmfont.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                font.setVisible(true);
            }
        });
        font.getOk().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mainTarea.setFont(font.font());
                font.setVisible(false);
            }
        });
        font.getCancel().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                font.setVisible(false);
            }
        });
        
    }

    public void updateButtons() {
//        itmundo.setText(undoManager.getUndoPresentationName());
//        itmredo.setText(undoManager.getRedoPresentationName());
        itmundo.setEnabled(undoManager.canUndo());
        itmredo.setEnabled(undoManager.canRedo());
    }

    private void save() {
        try {
            if (filename == null) {
                saveas();
            } else {
                String s = mainTarea.getText();
                PrintWriter pw = new PrintWriter(new FileWriter(fc.getSelectedFile()));
                StringTokenizer token = new StringTokenizer(s, System.getProperty("line.separator"));
                while (token.hasMoreTokens()) {
                    pw.println(token.nextToken());
                }
                pw.close();
            }
            filename = fc.getSelectedFile().getName();
            setTitle(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveas() {
        try {
            int rtval = fc.showSaveDialog(this);
            if (rtval == JFileChooser.APPROVE_OPTION) {
                String s = mainTarea.getText();
                PrintWriter pw = new PrintWriter(new FileWriter(fc.getSelectedFile()));
                StringTokenizer token = new StringTokenizer(s, System.getProperty("line.separator"));
                while (token.hasMoreTokens()) {
                    pw.println(token.nextToken());
                }
                pw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void open() {
        try {
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                mainTarea.setText(null);
                Reader in = new FileReader(fc.getSelectedFile());
                char[] buff = new char[100000];
                int nch;
                while ((nch = in.read(buff, 0, buff.length)) != -1) {
                    mainTarea.append(new String(buff, 0, nch));

                }
                fileContent = mainTarea.getText();
            }
            setTitle(fc.getSelectedFile().getName());
            filename = fc.getSelectedFile().getName();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openNew() {
        if (!mainTarea.getText().equals("") && !mainTarea.getText().equals(fileContent)) {
            if (filename == null) {
                int x = JOptionPane.showConfirmDialog(null, "Do you want save changes?", "Confirm Changes", JOptionPane.OK_CANCEL_OPTION);
                if (x == 0) {
                    saveas();
                    //to create new textArea after saving the old textArea
                    mainTarea.setText("");
                } else {
                    mainTarea.setText("");
                    setTitle("Untitled Notepad");
                    filename = null;
                }
            } else {
                int x = JOptionPane.showConfirmDialog(null, "Do you want save changes?", "Confirm Changes", JOptionPane.OK_CANCEL_OPTION);
                if (x == 0) {
                    save();
                    //to create new textArea after saving the old textArea
                    mainTarea.setText("");
                    setTitle("Untitled Notepad");
                    filename = null;
                } else {
                    mainTarea.setText("");
                    setTitle("Untitled Notepad");
                    filename = null;
                }
            }
        } else {
            mainTarea.setText("");
            setTitle("Untitled Notepad");
            filename = null;
        }
    }

    public static void main(String[] args) {
        Notepad notepad = new Notepad();
        notepad.setVisible(true);
    }
}
