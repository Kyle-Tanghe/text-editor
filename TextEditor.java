//******************************************************************************
//Author: Kyle Tanghe
//Purpose: This program is a simple text editor
//******************************************************************************
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.io.*;
public class TextEditor
{
	public static void main(String[] args)
	{
		JFrame frame = new Frame();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent winEvt)
			{
				if (JOptionPane.showConfirmDialog(frame,
					"Are you sure you want to exit?", "Exit?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					System.exit(0);
				}
			}
		});
	}
}
class Frame extends JFrame
{
	//component(s)/variables for Frame()
	private JMenuBar jmb = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu editMenu = new JMenu("Edit");

	private JMenuItem fileOpen = new JMenuItem("Open");
	private JMenuItem fileSave = new JMenuItem("Save");
	private JMenuItem fileSaveAs = new JMenuItem("Save As");
	private JMenuItem fileExit = new JMenuItem("Exit");
	private JMenuItem editCut = new JMenuItem("Cut");
	private JMenuItem editCopy = new JMenuItem("Copy");
	private JMenuItem editPaste = new JMenuItem("Paste");

	private JTextArea textArea = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane(textArea);
	private JFileChooser chooser = new JFileChooser();

	//component(s)/variables for TextStyle()
	private JButton button;
	private String[] comboBoxOptions = {"Sans Serif", "Serif", "Monospaced", "Dialog", "DialogInput"};
	private JComboBox comboBox = new JComboBox<String>(comboBoxOptions);
	private JCheckBox boldCheckBox = new JCheckBox("Bold");
	private JCheckBox italicCheckBox = new JCheckBox("Italic");

	//components(s)/variables for TextSize()
	private ButtonGroup textSizes = new ButtonGroup();
	private JRadioButton small = new JRadioButton("Small", false);
	private JRadioButton medium = new JRadioButton("Medium", true);
	private JRadioButton large = new JRadioButton("Large", false);

	//component(s)/variables for TextReplace()
	private JButton replaceButton = new JButton("Replace");;
	private JTextField replace1 = new JTextField(10);
	private JLabel label = new JLabel("With");
	private JTextField replace2 = new JTextField(10);

	//variables for changing the font in the JTextArea
	private String fontFamily = "Sans Serif";
	private int fontStyle = Font.PLAIN;
	private int fontSize = new Integer(16);

	public Frame()
	{
		setSize(900, 500);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Swing Text Editor");
		add(new NorthPanel(), BorderLayout.NORTH);
		add(new TextReplace(), BorderLayout.SOUTH);

		setJMenuBar(jmb);
		jmb.add(fileMenu);
		fileMenu.add(fileOpen);
		fileMenu.add(fileSave);
		fileMenu.add(fileSaveAs);
		fileMenu.addSeparator();
		fileMenu.add(fileExit);

		jmb.add(editMenu);
		editMenu.add(editCut);
		editMenu.add(editCopy);
		editMenu.add(editPaste);

		fileOpen.addActionListener(e -> {
			chooser.setCurrentDirectory(new File("."));
			int result = chooser.showOpenDialog(this);
			if(result == chooser.APPROVE_OPTION){
				try
				{
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(chooser.getSelectedFile().getName()));
					StringBuilder inputStreamText = new StringBuilder();
					int input = bis.read();
					while(input != -1)
					{
						inputStreamText = inputStreamText.append((char)input);
						input = bis.read();
					}
					textArea.setText(inputStreamText.toString());
					bis.close();
					setTitle("Swing text editor [" + chooser.getSelectedFile().getName() + "]");
					}
					catch(FileNotFoundException fnf)
					{
					System.out.println("Cannot find file. " + fnf.getMessage());
					}
					catch(IOException ioe)
					{
					System.out.println("Problem with reading file. " + ioe.getMessage());
					}
				}
		});
		fileSave.addActionListener(e -> {

			chooser.setCurrentDirectory(new File("."));
			if(chooser.getSelectedFile() == null)
				{
					int result = chooser.showSaveDialog(this);
					if(result == JFileChooser.APPROVE_OPTION)
					{
						saveFile();
					}
				}
			else
			{
				saveFile();
			}
		});
		fileSaveAs.addActionListener(e -> {
			chooser.setCurrentDirectory(new File("."));
			int save = chooser.showSaveDialog(this);
			if(save == JFileChooser.APPROVE_OPTION)
			{
				if(chooser.getSelectedFile().exists())
				{
					int result = JOptionPane.showConfirmDialog(this,
						"This file already exists, do you want to overwrite?", "File already exists",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
					if(result == JOptionPane.YES_OPTION)
					{
						saveFile();
					}
				}
				else
				{
					saveFile();
				}
			}
		});
		fileExit.addActionListener(e -> {
			int result = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to exit?", "Exit?",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
				);
			if(result == JOptionPane.YES_OPTION)
			{
				System.exit(0);
			}
		});

		editCut.addActionListener(e -> textArea.cut());
		editCopy.addActionListener(e -> textArea.copy());
		editPaste.addActionListener(e -> textArea.paste());

		add(scrollPane);
		textArea.setFont(new Font(fontFamily,fontStyle,fontSize));
		setVisible(true);
		textArea.requestFocusInWindow();
	}
	public void saveFile()
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(chooser.getSelectedFile().getName())));
			String text = textArea.getText();
			writer.write(text);
			writer.close();
			setTitle("Swing text editor [" + chooser.getSelectedFile().getName() + "]");
		}
		catch(FileNotFoundException fnf)
		{
			System.out.println("Cannot find file. " + fnf.getMessage());
		}
		catch(IOException ioe)
		{
			System.out.println("Problems writing to file" + ioe.getMessage());
		}
	}
	class NorthPanel extends JPanel
	{
		public NorthPanel()
		{
			setLayout(new BorderLayout());
			add(new TextStyle(), BorderLayout.CENTER);
			add(new TextSize(), BorderLayout.SOUTH);
		}
	}

	class TextStyle extends JPanel
	{
		public TextStyle()
		{
			add(comboBox);
			add(boldCheckBox);
			add(italicCheckBox);

			comboBox.addActionListener(e -> changeFont());
			boldCheckBox.addActionListener(e -> changeFont());
			italicCheckBox.addActionListener(e -> changeFont());
		}
		public void changeFont()
		{
			fontFamily = (String)comboBox.getSelectedItem();
			if(!(boldCheckBox.isSelected()) && !(italicCheckBox.isSelected())) fontStyle = Font.PLAIN;
			else if(boldCheckBox.isSelected() && !(italicCheckBox.isSelected())) fontStyle = Font.BOLD;
			else if(italicCheckBox.isSelected() && !(boldCheckBox.isSelected())) fontStyle = Font.ITALIC;
			else if(boldCheckBox.isSelected() && italicCheckBox.isSelected()) fontStyle = Font.BOLD + Font.ITALIC;
			if(small.isSelected()) fontSize = 12;
			else if(medium.isSelected()) fontSize = 16;
			else if(large.isSelected()) fontSize = 20;
			textArea.setFont(new Font(fontFamily, fontStyle, fontSize));
		}
	}
	class TextSize extends JPanel
	{
		public TextSize()
		{
			textSizes.add(small);
			textSizes.add(medium);
			textSizes.add(large);

			add(small);
			add(medium);
			add(large);

			small.addActionListener(e -> textArea.setFont(textArea.getFont().deriveFont(12f)));
			medium.addActionListener(e -> textArea.setFont(textArea.getFont().deriveFont(16f)));
			large.addActionListener(e -> textArea.setFont(textArea.getFont().deriveFont(20f)));
		}
	}
	class TextReplace extends JPanel
	{
		public TextReplace()
		{
			add(replaceButton);
			add(replace1);
			add(label);
			add(replace2);

			replaceButton.addActionListener(e -> textArea.setText(textArea.getText().replaceFirst(replace1.getText(), replace2.getText())));
		}
	}
}
