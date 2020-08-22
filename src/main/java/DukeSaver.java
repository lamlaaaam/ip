import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DukeSaver {

    private String savePath;

    public DukeSaver(String savePath) {
        this.savePath = savePath;
        initSaveDir();
    }

    public void initSaveDir() {
        try {
            File saveFile = new File(this.savePath);
            saveFile.getParentFile().mkdirs();
            saveFile.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveData(TaskList taskList) {
        try {
            FileWriter fw = new FileWriter(savePath);
            for (Task task : taskList.getList()) {
                fw.write(task.serialize() + "\n");
            }
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void loadData(TaskList taskList) {
        try {
            File saveFile = new File(savePath);
            Scanner sc = new Scanner(saveFile);
            while (sc.hasNext()) {
                String line = sc.nextLine();
                taskList.addTask(Task.parse(line));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
