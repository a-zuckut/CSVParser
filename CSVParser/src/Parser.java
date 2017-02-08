import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Parser {

	File[] currentDir;
	
	String file_name;
	File cooresponding_file;
	
	String[] columns;
	String[][] data;
	
	final static String csvSplitChar = ";"; 
	
	public Parser() {
		File currentDir = new File("").getAbsoluteFile();
		System.out.println("Current Directory" + currentDir);
		
		File[] files = currentDir.listFiles();
		this.currentDir = files;
		
		ArrayList<File> csvFilesInCurrentDirectory = new ArrayList<>();
		for(int i = 0; i < files.length; i++) {
			if(files[i].toString().contains(".csv"))
				csvFilesInCurrentDirectory.add(files[i]);
		}
		
		System.out.printf("In the current directory, there are %d csv files.\n", csvFilesInCurrentDirectory.size());
		
		if(csvFilesInCurrentDirectory.size() > 1) {
			System.out.println("Since there are greater than 1 csv files in this current directory, this parser will parse in:");
			System.out.println(csvFilesInCurrentDirectory.get(0).toString());
		}
		
		file_name = csvFilesInCurrentDirectory.get(0).toString();
		cooresponding_file = csvFilesInCurrentDirectory.get(0);
		
		Process();
	}
	
	public Parser(String file_name) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("").getAbsoluteFile());
		if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			cooresponding_file = fileChooser.getSelectedFile();
			file_name = cooresponding_file.toString();
		}
		Process();
		
	}
	
	void Process() {
		printFile();
		parseFile();
		printColumns();
	}
	
	void printFile() {
		System.out.println("File To Parse " + file_name);
	}
	
	void printColumns() {
		System.out.println("Columns");
		for(int i = 0; i < columns.length; i++) {
			System.out.println("(" + i + ") " + columns[i]);
		}
	}
	
	boolean parseFile() {
		FileReader fileReader;
		try {
			fileReader = new FileReader(cooresponding_file);
		} catch (FileNotFoundException e) {
			return false;
		}
		
		BufferedReader inputStream = new BufferedReader(fileReader);
		
		try {
			String currentLine = inputStream.readLine();
			columns = currentLine.split(csvSplitChar);
			
			ArrayList<String> elements = new ArrayList<>();
			while((currentLine = inputStream.readLine()) != null) {
				elements.add(currentLine);
			}
			
			data = new String[elements.size()][columns.length];
			for(int i = 0; i < elements.size(); i++) {
				data[i] = elements.get(i).split(csvSplitChar);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		try {
			inputStream.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	void startGUI() {
		
		ArrayList<JCheckBox> arrayList = new ArrayList<>();
		for(String s: columns) {
			JCheckBox box = new JCheckBox(s);
			arrayList.add(box);
		}
		Object[] obj = (Object[]) arrayList.toArray(new Object[arrayList.size()]);
		JOptionPane.showConfirmDialog(null, obj);
		
		ArrayList<String> selected_columns = new ArrayList<>();
		
		for(int i = 0; i < arrayList.size(); i++) {
			if(arrayList.get(i).isSelected()) {
				selected_columns.add(arrayList.get(i).getText());
			}
		}
		
		String[] column = (String[]) selected_columns.toArray(new String[selected_columns.size()]);
		String[][] data = new String[this.data.length + 2][selected_columns.size()];
		
		for(int i = 0; i < this.data.length; i++) {
			ArrayList<String> currentdata = new ArrayList<>();
			for(int j = 0; j < this.data[i].length; j++) {
				if(arrayList.get(j).isSelected()) {
					currentdata.add(this.data[i][j]);
				}
			}
			String[] obj2 = (String[]) currentdata.toArray(new String[currentdata.size()]);
			data[i] = obj2;
		}
		
		for(int i = this.data.length - 1; i < data.length; i++) {
			data[i] = new String[selected_columns.size()];
			for(int j = 0; j < selected_columns.size(); j++) {
				data[i][j] = "";
			}
		}
		
		JFrame frame = new JFrame("Data");
		JTable table = new JTable(data, column);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		frame.add(scrollPane);
		scrollPane.setOpaque(true);
		
		table.setFillsViewportHeight(true);
		
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar x = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem reset = new JMenuItem("Reset");
		
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				startGUI();
			}
		});
		
		file.add(reset);
		
		
		x.add(file);
		
		frame.setJMenuBar(x);
		
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
	}

	public static void main(String[] args) {
		Parser parser = new Parser("");
		parser.startGUI();
	}
	
}
