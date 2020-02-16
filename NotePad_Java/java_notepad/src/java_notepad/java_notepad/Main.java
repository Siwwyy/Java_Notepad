package java_notepad;

import java.awt.EventQueue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import javax.swing.JLabel;
import java.awt.event.KeyAdapter;

public class Main extends JFrame
{

	private JFrame frame;

	  public static void main(String[] args) 
	  {
		  Main object = new Main();
		  object.setVisible(true);
	   }

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	
	
	   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		static JTextArea mainarea;
	    JMenuBar mbar;
	    JMenu mnuFile, mnuEdit, mnuFormat, mnuHelp;
	    JMenuItem itmNew, itmOpen, itmSave, itmSaveas,
	            itmExit, itmCut, itmCopy, itmPaste, itmFontColor,
	            itmFind, itmReplace, itmFont;
	    JButton btnOpen;
	    JCheckBoxMenuItem wordWrap;
	    String filename;
	    JFileChooser jc;
	    String fileContent;
	    UndoManager undo;
	    UndoAction undoAction;
	    RedoAction redoAction;
	    String findText;
	    int fnext = 1;
	    public static Main frmMain = new Main();
	    FontHelper font;
	    JToolBar toolbar;
	    private JLabel lblLetters;
	    private JLabel licznik_slow;

	    public Main() 
	    {
	        initComponent();
	        itmSave.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                save();
	            }
	        });
	        itmSaveas.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                saveAs();
	            }
	        });
	        itmOpen.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                open();
	            }
	        });
	        btnOpen.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                open();
	            }
	        });
	        itmNew.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                open_new();
	            }
	        });
	        itmExit.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                System.exit(0);
	            }
	        });
	        itmCut.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                mainarea.cut();
	            }
	        });
	        itmCopy.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                mainarea.copy();
	            }
	        });
	        itmPaste.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                mainarea.paste();
	            }
	        });
	        mainarea.getDocument().addUndoableEditListener(new UndoableEditListener() {

	            @Override
	            public void undoableEditHappened(UndoableEditEvent e) {
	                undo.addEdit(e.getEdit());
	                undoAction.update();
	                redoAction.update();
	            }
	        });

	        //mainarea.setWrapStyleWord(true);
	        wordWrap.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                if (wordWrap.isSelected()) {
	                    mainarea.setLineWrap(true);
	                    mainarea.setWrapStyleWord(true);
	                } else {
	                    mainarea.setLineWrap(false);
	                    mainarea.setWrapStyleWord(false);
	                }
	            }
	        });
	        itmFontColor.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                Color c = JColorChooser.showDialog(rootPane, "Choose Font Color", Color.BLUE);
	                mainarea.setForeground(c);
	            }
	        });
	        itmFind.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                new FindAndReplace(frmMain, false);
	            }
	        });
	        itmReplace.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                new FindAndReplace(frmMain, true);
	            }
	        });
	        itmFont.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                font.setVisible(true);
	            }
	        });
	        font.getOk().addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                mainarea.setFont(font.font());
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

	    private void initComponent() {
	        jc = new JFileChooser(".");
	        mainarea = new JTextArea();
	        mainarea.addKeyListener(new KeyAdapter() {
	        	@Override
	        	public void keyTyped(KeyEvent e) 
	        	{
	        		licznik_slow.setText(Integer.toString(mainarea.getText().length()));
	        	}
	        });
	        undo = new UndoManager();
	        font = new FontHelper();
	        toolbar = new JToolBar();
	        getContentPane().add(toolbar, BorderLayout.NORTH);
	        ImageIcon iconUndo = new ImageIcon(getClass().getResource("/img/undo.gif"));
	        ImageIcon iconRedo = new ImageIcon(getClass().getResource("/img/redo.gif"));
	        undoAction = new UndoAction(iconUndo);
	        redoAction = new RedoAction(iconRedo);

	        getContentPane().add(mainarea);
	        getContentPane().add(new JScrollPane(mainarea), BorderLayout.CENTER);
	        setTitle("Untitled Notepad");
	        setSize(800, 600);
	        //menu bar
	        mbar = new JMenuBar();
	        //menu
	        mnuFile = new JMenu("File");
	        mnuEdit = new JMenu("Edit");
	        mnuFormat = new JMenu("Format");
	        mnuHelp = new JMenu("Help");
	        //add icon to menu item
	        ImageIcon iconNew = new ImageIcon(getClass().getResource("/img/new.gif"));
	        ImageIcon iconOpen = new ImageIcon(getClass().getResource("/img/open.gif"));
	        ImageIcon iconSave = new ImageIcon(getClass().getResource("/img/save.gif"));
	        ImageIcon iconSaveAs = new ImageIcon(getClass().getResource("/img/saveAs.gif"));
	        ImageIcon iconCut = new ImageIcon(getClass().getResource("/img/cut.gif"));
	        ImageIcon iconCopy = new ImageIcon(getClass().getResource("/img/copy.gif"));
	        ImageIcon iconPaste = new ImageIcon(getClass().getResource("/img/paste.gif"));
	        ImageIcon iconFind = new ImageIcon(getClass().getResource("/img/find.gif"));
	        ImageIcon iconReplace = new ImageIcon(getClass().getResource("/img/Replace.gif"));
	        ImageIcon iconFont = new ImageIcon(getClass().getResource("/img/font.gif"));
	        btnOpen = new JButton(iconOpen); 
	        toolbar.add(btnOpen); 
	        
	        lblLetters = new JLabel("Letters: ");
	        toolbar.add(lblLetters);
	        
	        licznik_slow = new JLabel("0");
	        toolbar.add(licznik_slow);
	        // menu item
	        itmNew = new JMenuItem("New", iconNew);
	        itmOpen = new JMenuItem("Open", iconOpen);
	        itmSave = new JMenuItem("Save", iconSave);
	        itmSaveas = new JMenuItem("Save As", iconSaveAs);
	        itmExit = new JMenuItem("Exit");
	        itmCut = new JMenuItem("Cut", iconCut);
	        itmCopy = new JMenuItem("Copy", iconCopy);
	        itmPaste = new JMenuItem("Paste", iconPaste);
	        itmFind = new JMenuItem("Find", iconFind);
	        itmReplace = new JMenuItem("Replace", iconReplace);
	        itmFontColor = new JMenuItem("Font Color");
	        itmFont = new JMenuItem("Font", iconFont);
	        wordWrap = new JCheckBoxMenuItem("Word Wrap");
	        //adding shortcut
	        itmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
	        itmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
	        itmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

	        //add menu item
	        mnuFile.add(itmNew);
	        mnuFile.add(itmOpen);
	        mnuFile.add(itmSave);
	        mnuFile.add(itmSaveas);
	        mnuFile.addSeparator();
	        mnuFile.add(itmExit);
	        mnuEdit.add(undoAction);
	        mnuEdit.add(redoAction);
	        mnuEdit.add(itmCut);
	        mnuEdit.add(itmCopy);
	        mnuEdit.add(itmPaste);
	        mnuEdit.add(itmFind);
	        mnuEdit.add(itmReplace);
	        mnuEdit.add(itmFont);
	        mnuFormat.add(wordWrap);
	        mnuFormat.add(itmFontColor);
	        //add menu item to menu bar
	        mbar.add(mnuFile);
	        mbar.add(mnuEdit);
	        mbar.add(mnuFormat);
	        mbar.add(mnuHelp);
	        //add menu bar to frame
	        setJMenuBar(mbar);


	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        //setVisible(true);
	    }

	    private void save() {
	        PrintWriter fout = null;
	        //int retval = -1;

	        try {
	            if (filename == null) {
	                saveAs();
	            } else {
	                fout = new PrintWriter(new FileWriter(filename));
	                String s = mainarea.getText();
	                StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
	                while (st.hasMoreElements()) {
	                    fout.println(st.nextToken());
	                }
	                JOptionPane.showMessageDialog(rootPane, "File saved");
	                fileContent = mainarea.getText();
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (fout != null) {
	                fout.close();
	            }
	        }
	    }

	    private void saveAs() {
	        PrintWriter fout = null;
	        int retval = -1;
	        try {
	            retval = jc.showSaveDialog(this);
	            if (retval == JFileChooser.APPROVE_OPTION) {
	                if (jc.getSelectedFile().exists()) {
	                    int option = JOptionPane.showConfirmDialog(rootPane, "Do you want to replace this file?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);

	                    if (option == 0) {
	                        fout = new PrintWriter(new FileWriter(jc.getSelectedFile()));
	                        String s = mainarea.getText();
	                        StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
	                        while (st.hasMoreElements()) {
	                            fout.println(st.nextToken());
	                        }
	                        JOptionPane.showMessageDialog(rootPane, "File saved");
	                        fileContent = mainarea.getText();
	                        filename = jc.getSelectedFile().getName();
	                        setTitle(filename = jc.getSelectedFile().getName());
	                    } else {
	                        saveAs();
	                    }
	                } else {
	                    fout = new PrintWriter(new FileWriter(jc.getSelectedFile()));
	                    String s = mainarea.getText();
	                    StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
	                    while (st.hasMoreElements()) {
	                        fout.println(st.nextToken());
	                    }
	                    JOptionPane.showMessageDialog(rootPane, "File saved");
	                    fileContent = mainarea.getText();
	                    filename = jc.getSelectedFile().getName();
	                    setTitle(filename = jc.getSelectedFile().getName());
	                }

	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (fout != null) {
	                fout.close();
	            }
	        }
	    }

	    private void open() {
	        try {
	            int retval = jc.showOpenDialog(this);
	            if (retval == JFileChooser.APPROVE_OPTION) {
	                mainarea.setText(null);
	                Reader in = new FileReader(jc.getSelectedFile());
	                char[] buff = new char[100000];
	                int nch;
	                while ((nch = in.read(buff, 0, buff.length)) != -1) {
	                    mainarea.append(new String(buff, 0, nch));
	                }
	                filename = jc.getSelectedFile().getName();
	            setTitle(filename = jc.getSelectedFile().getName());
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    private void open_new() {
	        if (!mainarea.getText().equals("") && !mainarea.getText().equals(fileContent)) {
	            if (filename == null) {
	                int option = JOptionPane.showConfirmDialog(rootPane, "Do you want to save the changes?");
	                if (option == 0) {
	                    saveAs();
	                    clear();
	                } else if (option == 2) {
	                } else {
	                    clear();
	                }
	            } else {
	                int option = JOptionPane.showConfirmDialog(rootPane, "Do you want to save the changes?");
	                if (option == 0) {
	                    save();
	                    clear();
	                } else if (option == 2) {
	                } else {
	                    clear();
	                }
	            }
	        } else {
	            clear();
	        }
	    }

	    private void clear() 
	    {
	        mainarea.setText(null);
	        setTitle("Untitled Notepad");
	        filename = null;
	        fileContent = null;
	    }

	    class UndoAction extends AbstractAction {

	        public UndoAction(ImageIcon undoIcon) {
	            super("Undo", undoIcon);
	            setEnabled(false);
	        }

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            try {
	                undo.undo();
	            } catch (CannotUndoException ex) {
	                ex.printStackTrace();
	            }
	            update();
	            redoAction.update();
	        }

	        protected void update() {
	            if (undo.canUndo()) {
	                setEnabled(true);
	                putValue(Action.NAME, "Undo");
	            } else {
	                setEnabled(false);
	                putValue(Action.NAME, "Undo");
	            }
	        }
	    }

	    class RedoAction extends AbstractAction {

	        public RedoAction(ImageIcon redoIcon) {
	            super("Redo", redoIcon);
	            setEnabled(false);
	        }

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            try {
	                undo.redo();
	            } catch (CannotRedoException ex) {
	                ex.printStackTrace();
	            }
	            update();
	            undoAction.update();
	        }

	        protected void update() {
	            if (undo.canRedo()) {
	                setEnabled(true);
	                putValue(Action.NAME, "Redo");
	            } else {
	                setEnabled(false);
	                putValue(Action.NAME, "Redo");
	            }
	        }
	    }

	    public static JTextArea getArea() {
	        return mainarea;
	    }
}
