package ta.graph;

import ta.TA;
import ta.TaLocation;
import ta.ota.DOTA;
import ta.ota.DOTAUtil;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GraphUtil {

    private final StringBuilder code = new StringBuilder("digraph G {" + "\r\n");

    public static void main(String[] args) throws IOException, InterruptedException {
        String base = ".\\src\\main\\resources\\dota\\";
        String path = base + "4_4_20\\4_4_20-1.json";
        DOTA ota = DOTAUtil.getDOTAFromJsonFile(path);
        GraphUtil.convert(ota, ".\\src\\main\\resources\\pic.jpg");
    }


    public static void convert(TA ta, String savePath) throws IOException, InterruptedException {
        GraphUtil util = new GraphUtil();
        List<TaLocation> locations = ta.getLocations();
        TaLocation initLocation = ta.getInitLocation();
        List<TaLocation> acceptLocations = ta.getAcceptedLocations();

        locations.forEach(taLocation -> {
            StringBuilder name = new StringBuilder(taLocation.getName() + "@" + taLocation.getId());
            if (acceptLocations.contains(taLocation)) {
                name.append(",accept");
            }
            if (initLocation.equals(taLocation)) {
                name.append(",init");
            }
            util.node(taLocation.getId(), name.toString());
        });

        ta.getTransitions().forEach(transition -> {
            util.link(transition.getSourceId(), transition.getTargetId(), transition.toString());
        });
        util.genAndOpenGraph(savePath.replace(".jpg", ".txt"), null);
    }

    /**
     * 节点A到节点B画一条有向边
     */
    public void link(String dotA, String dotB) {
        String linkCode = dotA + " -> " + dotB + "\r\n";
        this.code.append(linkCode);
    }

    /**
     * 节点A到节点B画一条有向边，边权写上label
     */
    public void link(String nodeNameFrom, String nodeNameTo, String label) {
        String linkCode = nodeNameFrom + " -> " + nodeNameTo + "[label=\"" + label + "\"]" + "\r\n";
        this.code.append(linkCode);
    }

    public void node(String nodeName, String label) {
        String nodeCode = nodeName + "[label=\"" + label + "\"]" + "\r\n";
        this.code.append(nodeCode);
    }

    /**
     * 打开文件
     */
    public static void openFile(String filePath) {
        try {
            File file = new File(filePath);
            Desktop.getDesktop().open(file);
        } catch (IOException | NullPointerException e) {
            System.err.println(e);
        }
    }

    /**
     * 调用dot命令从sourcePath中读取dot文件并把图片生产到targetPath
     */
    public static void genGraph(String sourcePath, String targetPath) throws IOException, InterruptedException {
        Runtime run = Runtime.getRuntime();
        run.exec("dot " + sourcePath + " -T jpg -o " + targetPath);
        Thread.sleep(1000);
    }

    /**
     * 生成图片，然后自动打开
     */
    public void genAndOpenGraph(String sourcePath, String targetPath) throws InterruptedException, IOException {
        if (targetPath == null) {
            targetPath = sourcePath.replace(".txt", ".jpg");
        }
        saveCodeToFile(sourcePath, getCode());
        genGraph(sourcePath, targetPath);
        openFile(targetPath);
    }

    //保存dot指令到文件  后续利用这个指令文件 就可以用dot命令生成图了
    public static void saveCodeToFile(String filePath, String content) {
        FileWriter fwriter = null;
        try {
            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
            fwriter = new FileWriter(filePath);
            fwriter.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //一些setter和getter方法
    public String getCode() {
        return code.append("\n}").toString();
    }

}
