package opticDiscAnalyser;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Directory {
	static File title;
	public static String s;
	MainWindow mw = new MainWindow();
	
	public Directory(File title){
		this.setTitle(title);
	}

	public void getDirectory() throws Exception{
		getDirectoryUsingJFileChooser(title);
	}

	public File getTitle() {
		return title;
	}

	public void setTitle(File title) {
		Directory.title = title;
	}
	

	public static File getDirectoryUsingJFileChooser(File title) throws Exception{
		

				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG Images", "jpg","jpeg");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				Object menuOpen = null;
				if (chooser.showOpenDialog((Component) menuOpen) == JFileChooser.APPROVE_OPTION){
					title = chooser.getSelectedFile();
					s = title.getName();
				}
					return title;
	}
	
	
}



