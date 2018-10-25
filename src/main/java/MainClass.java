
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;


public class MainClass {

    public static void main(String[] args) {
        String sharedInformation = "E:\\Tut\\npm";
        try {
            DirectoryStream<Path> htm = Files.newDirectoryStream(Paths.get(sharedInformation));
            for (Path entry : htm) {
                String old = entry.getFileName().toString();
                String[] newN = old.split("-");
                String newName = sharedInformation + "/" + newN[1] + "-" + newN[2];
                String oldName = sharedInformation + "/" + old;
                new File(oldName).renameTo(new File(newName));
            }
         /* for(Path entry: htm){
                String name = entry.getFileName().toString();
                if(!name.contains(".en."))
                    new File(sharedInformation+"/"+name).delete();
                }
        */} catch (IOException e) {
            System.out.println(e);

        }
    }

}