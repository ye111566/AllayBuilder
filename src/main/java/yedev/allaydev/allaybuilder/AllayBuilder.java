package yedev.allaydev.allaybuilder;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.block.type.BlockState;
import org.allaymc.api.debugshape.DebugArrow;
import org.allaymc.api.debugshape.DebugBox;
import org.allaymc.api.debugshape.DebugLine;
import org.allaymc.api.plugin.Plugin;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.allaymc.api.entity.interfaces.EntityPlayer;

import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;
import org.allaymc.api.world.Dimension;
import org.joml.Vector3f;
import org.joml.Vector3ic;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AllayBuilder extends Plugin {
    public static AllayBuilder instance;
    public static HashMap<EntityPlayer, Boolean> debugarrow = new HashMap<>();
    public static List<String> getuseable_structures() {
        List<String> nameList = new ArrayList<>();
        File folder = new File("./structures");

        // 检查目录是否存在且为文件夹
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Directory ./structures does not exist or is not a directory");
            return nameList;
        }
        System.out.println("开始寻找文件");
        // 正则表达式匹配文件名格式
        Pattern pattern = Pattern.compile("^builder_(.+?)_(\\d+)_(\\d+)\\.mcstructure$");
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                System.out.println(fileName);
                Matcher matcher = pattern.matcher(fileName);

                if (matcher.matches()) {
                    // 提取第一个捕获组（name部分）
                    System.out.println("match!");
                    nameList.add(matcher.group(1));
                }
            }
        }
        return nameList;
    }
    public static Map<String, Map<String, Vector3ic>> posrecorder= new HashMap<>();
    /**
     * 按照 16x16 的区块规则分割坐标范围（蛇形扫描优化）
     * @param x1 起始 x 坐标
     * @param y1 起始 y 坐标
     * @param z1 起始 z 坐标
     * @param x2 结束 x 坐标
     * @param y2 结束 y 坐标
     * @param z2 结束 z 坐标
     * @return 包含 [起点, 终点] 的三维整数列表，每个区块表示为 [[xStart, y1, zStart], [xEnd, y2, zEnd]]
     */

    public static List<List<List<Integer>>> divideCoordinates(
            int x1, int y1, int z1,
            int x2, int y2, int z2
    ) {
        // 计算坐标范围边界
        int xMin = Math.min(x1, x2);
        int xMax = Math.max(x1, x2);
        int zMin = Math.min(z1, z2);
        int zMax = Math.max(z1, z2);

        List<List<List<Integer>>> result = new ArrayList<>();
        boolean isReversed = false;  // 蛇形扫描方向标记

        // X轴方向分块（固定步长16）
        for (int xStart = xMin; xStart <= xMax; xStart += 16) {
            // 计算当前X分块的结束坐标（不超过xMax）
            int xEnd = Math.min(xStart + 16, xMax);

            if (isReversed) {
                // 反向扫描Z轴（从大到小）
                for (int zStart = zMax; zStart >= zMin; zStart -= 16) {
                    int zEnd = Math.max(zStart - 16, zMin);
                    // 添加区块坐标对 [[xStart, y1, zStart], [xEnd, y2, zEnd]]
                    result.add(createCoordinatePair(xStart, y1, zStart, xEnd, y2, zEnd));
                }
            } else {
                // 正向扫描Z轴（从小到大）
                for (int zStart = zMin; zStart <= zMax; zStart += 16) {
                    int zEnd = Math.min(zStart + 16, zMax);
                    result.add(createCoordinatePair(xStart, y1, zStart, xEnd, y2, zEnd));
                }
            }
            // 切换下一列的扫描方向
            isReversed = !isReversed;
        }
        return result;
    }

    // 创建坐标对辅助方法
    private static List<List<Integer>> createCoordinatePair(
            int xStart, int y1, int zStart,
            int xEnd, int y2, int zEnd
    ) {
        List<List<Integer>> pair = new ArrayList<>();
        pair.add(createPoint(xStart, y1, zStart));
        pair.add(createPoint(xEnd, y2, zEnd));
        return pair;
    }

    // 创建坐标点辅助方法
    private static List<Integer> createPoint(int x, int y, int z) {
        List<Integer> point = new ArrayList<>();
        point.add(x);
        point.add(y);
        point.add(z);
        return point;
    }
    public static void onwoodenaxe(EntityPlayer player, Vector3ic posdata) {
        //player.sendMessage("onwoodenaxe:"+posdata);
        //initialize
        if(posrecorder.get(player.getOriginName()) == null){
            player.sendMessage("intialize the posdata");
            Map<String, Vector3ic> emptypostable = new HashMap<>();
            posrecorder.put(player.getOriginName(), emptypostable);
        }
        Map<String, Vector3ic> currentposdata=posrecorder.get(player.getOriginName());
        switch (currentposdata.size()){
            case 0:
                player.sendMessage("setposa"+posdata);
                currentposdata.put("posa", posdata);

                break;
            case 1:
                player.sendMessage("setposb"+posdata);
                currentposdata.put("posb", posdata);

                break;
            case 2:
                player.sendMessage("clear posdata\nset posa"+posdata);
                player.getDimension().removeAllDebugShapes();
                currentposdata.clear();
                currentposdata.put("posa", posdata);
                break;
        }
        posrecorder.put(player.getOriginName(), currentposdata);
        Vector3ic posa=posrecorder.get(player.getOriginName()).get("posa");
        Vector3ic posb=posrecorder.get(player.getOriginName()).get("posb");
        if(posa!=null){
            player.sendMessage("posa:"+posa);
        }
        if(posa==null){
            player.sendMessage("posa:null");
        }
        if(posb!=null){
            player.sendMessage("posb:"+posb);
        }

        if(posb==null){
            player.sendMessage("posb:null");
        }
        if(posa!=null&&posb!=null){
            player.sendMessage("creating the debugshape");
            DebugBox debugBox = new DebugBox(
                    new Vector3f(Math.min(posa.x(),posb.x()), Math.min(posa.y(),posb.y()), Math.min(posa.z(),posb.z())), // Position
                    Color.WHITE,                  // Color
                    1.0f,                       // Scale
                    new Vector3f(Math.abs(posa.x()-posb.x())+1,Math.abs(posa.y()-posb.y())+1,Math.abs(posa.z()-posb.z())+1)       // Box bounds
            );
            player.getDimension().addDebugShape(debugBox);
        }


    }

    public static void structuresave(EntityPlayer user, String structname, Vector3ic posa, Vector3ic posb, Dimension dimension) {

        Thread.ofVirtual().start(()->{
            int areastartx=Math.min(posa.x(),posb.x());
            int areastarty=Math.min(posa.y(),posb.y());
            int areastartz=Math.min(posa.z(),posb.z());

            List<List<List<Integer>>> poslist=divideCoordinates(
                    Math.min(posa.x(),posb.x()),
                    Math.min(posa.y(),posb.y()),
                    Math.min(posa.z(),posb.z()),
                    Math.max(posa.x(),posb.x()),
                    Math.max(posa.y(),posb.y()),
                    Math.max(posa.z(),posb.z())
            );


            for (List<List<Integer>> pos : poslist) {
                // 每个 block 包含两个点：起点和终点
                List<Integer> startPoint = pos.get(0); // [xStart, y1, zStart]
                List<Integer> endPoint = pos.get(1);   // [xEnd, y2, zEnd]

                // 提取起点坐标
                int startX =Math.min(startPoint.get(0),endPoint.get(0));
                int startY =Math.min(startPoint.get(1),endPoint.get(1));
                int startZ =Math.min(startPoint.get(2),endPoint.get(2));

                // 提取终点坐标
                int endX = Math.max(startPoint.get(0),endPoint.get(0));
                int endY = Math.max(startPoint.get(1),endPoint.get(1));
                int endZ = Math.max(startPoint.get(2),endPoint.get(2));

                String cmd="structure pick builder_"+structname+"_"+(startX-areastartx)+"_"+(startZ-areastartz)+" "+startX+" "+startY+" "+startZ+" "+endX+" "+endY+" "+endZ;
                log.info("cmd:"+cmd);

                Registries.COMMANDS.execute(user, cmd);
                // 使用坐标（示例）
            }

        });
    }

    public static void structureload(EntityPlayer user, String structurename) {

        Thread.ofVirtual().start(() -> {
            File folder = new File("./structures");

            // 检查目录是否存在
            if (!folder.exists() || !folder.isDirectory()) {
                user.sendMessage("§c错误: structures目录不存在");
                return;
            }

            // 创建正则表达式，注意对structurename进行转义
            String regex = "builder_" + Pattern.quote(structurename) + "_(\\d+)_(\\d+)\\.mcstructure";
            Pattern pattern = Pattern.compile(regex);

            File[] files = folder.listFiles();
            if (files == null || files.length == 0) {
                user.sendMessage("§e未找到匹配的结构文件");
                return;
            }

            boolean found = false;
            for (File file : files) {
                if (!file.isFile()) continue;

                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.matches()) {
                    found = true;
                    int x = Integer.parseInt(matcher.group(1));
                    int z = Integer.parseInt(matcher.group(2));

                    String cmd ="structure place builder_"+structurename+"_"+(x)+"_"+(z)+" ~"+x+" ~ ~"+z;
                    System.out.println("cmd:"+cmd);
                    Registries.COMMANDS.execute(user, cmd);
                }
            }

            if (!found) {
                user.sendMessage("§e未找到匹配的结构文件: " + structurename);
            }
        });
    }



    public record BlockData(int x, int y, int z, BlockState blockState) {}
    public static List<BlockData> blockList = new ArrayList<>();
    public static Dimension taskdimension;
    public static BlockState taskblock;
    public static int tasktotal;
    public static EntityPlayer taskuser;
    public static Boolean running=false;
    public static void fillblock(EntityPlayer user,Vector3ic posa, Vector3ic posb, BlockState block, Dimension dim){
        if(AllayBuilder.running){
            user.sendMessage("任务列表繁忙，无法创建填充任务");
            return;
        }
        AllayBuilder.taskdimension=dim;
        AllayBuilder.taskblock=block;
        AllayBuilder.taskuser=user;
        AllayBuilder.tasktotal =
                (
                        Math.abs(
                                posa.x()-posb.x()
                        )
                                +1
                )*
                        (
                                Math.abs(
                                        posa.y()-posb.y()
                                )
                                        +1
                        )*
                        (
                                Math.abs(
                                        posa.z()-posb.z()
                                )
                                        +1
                        );

        for(int x=Math.min(posa.x(),posb.x());x<=Math.max(posa.x(),posb.x());x++){
            for(int y=Math.min(posa.y(),posb.y());y<=Math.max(posa.y(),posb.y());y++){
                for(int z=Math.min(posa.z(),posb.z());z<=Math.max(posa.z(),posb.z());z++){
                    blockList.add(new BlockData(x,y,z,block));
                }
            }
        }
        AllayBuilder.running=true;
    }

    public static void replaceblock(EntityPlayer user,Vector3ic posa, Vector3ic posb, BlockState block, BlockState be_replaced_block, Dimension dim){
        if(AllayBuilder.running){
            user.sendMessage("任务列表繁忙，无法创建填充任务");
            return;
        }
        AllayBuilder.taskdimension=dim;
        AllayBuilder.taskblock=block;
        AllayBuilder.taskuser=user;
        AllayBuilder.tasktotal =
                (
                        Math.abs(
                                posa.x()-posb.x()
                        )
                                +1
                )*
                        (
                                Math.abs(
                                        posa.y()-posb.y()
                                )
                                        +1
                        )*
                        (
                                Math.abs(
                                        posa.z()-posb.z()
                                )
                                        +1
                        );

        for(int x=Math.min(posa.x(),posb.x());x<=Math.max(posa.x(),posb.x());x++){
            for(int y=Math.min(posa.y(),posb.y());y<=Math.max(posa.y(),posb.y());y++){
                for(int z=Math.min(posa.z(),posb.z());z<=Math.max(posa.z(),posb.z());z++){
                    if(dim.getBlockState(x,y,z)==be_replaced_block) {
                        blockList.add(new BlockData(x, y, z, block));
                    }
                }
            }
        }
        AllayBuilder.running=true;
    }

    public static void maintainblock(EntityPlayer user,Vector3ic posa, Vector3ic posb, BlockState block, BlockState maintain_block, Dimension dim){
        if(AllayBuilder.running){
            user.sendMessage("任务列表繁忙，无法创建填充任务");
            return;
        }
        AllayBuilder.taskdimension=dim;
        AllayBuilder.taskblock=block;
        AllayBuilder.taskuser=user;
        AllayBuilder.tasktotal =
                (
                        Math.abs(
                                posa.x()-posb.x()
                        )
                                +1
                )*
                        (
                                Math.abs(
                                        posa.y()-posb.y()
                                )
                                        +1
                        )*
                        (
                                Math.abs(
                                        posa.z()-posb.z()
                                )
                                        +1
                        );

        for(int x=Math.min(posa.x(),posb.x());x<=Math.max(posa.x(),posb.x());x++){
            for(int y=Math.min(posa.y(),posb.y());y<=Math.max(posa.y(),posb.y());y++){
                for(int z=Math.min(posa.z(),posb.z());z<=Math.max(posa.z(),posb.z());z++){
                    if(dim.getBlockState(x,y,z)!=maintain_block) {
                        blockList.add(new BlockData(x, y, z, block));
                    }
                }
            }
        }
        AllayBuilder.running=true;
    }



    @Override
    public void onLoad() {
        Registries.COMMANDS.register(new BuilderCommand());


        log.info("AllayBuilder loaded!");
        instance=this;
    }

    @Override
    public void onEnable() {
        Server.getInstance().getScheduler().scheduleRepeating(instance, () -> {

            if(!AllayBuilder.running){


                return true;
            }else
            {
                System.out.println("|task running");
                if(blockList.isEmpty()){
                    System.out.print("|task finished");
                    AllayBuilder.running=false;
                    return true;
                }else {
                    System.out.print("task not finished");
                    System.out.println(blockList.size());
                    if(blockList.size()>=512){
                        for(int i=0;i<512;i++){
                            var blockdata = blockList.getFirst();
                            taskdimension.setBlockState(blockdata.x, blockdata.y, blockdata.z, taskblock);
                            taskuser.sendMessage("filling...512:\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            taskuser.sendActionBar("filling...:512\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            blockList.removeFirst();
                        }
                    }
                    else if(blockList.size()>=256){
                        for(int i=0;i<256;i++){
                            var blockdata = blockList.getFirst();
                            taskdimension.setBlockState(blockdata.x, blockdata.y, blockdata.z, taskblock);
                            taskuser.sendMessage("filling...:256\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            taskuser.sendActionBar("filling...:256\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            blockList.removeFirst();
                        }
                    }
                    else if(blockList.size()>=128){
                        for(int i=0;i<128;i++){
                            var blockdata = blockList.getFirst();
                            taskdimension.setBlockState(blockdata.x, blockdata.y, blockdata.z, taskblock);
                            taskuser.sendMessage("filling...:128\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            taskuser.sendActionBar("filling...:128\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            blockList.removeFirst();
                        }
                    }
                    else if(blockList.size()>=64){
                        for(int i=0;i<64;i++){
                            var blockdata = blockList.getFirst();
                            taskdimension.setBlockState(blockdata.x, blockdata.y, blockdata.z, taskblock);
                            taskuser.sendMessage("filling...:64\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            taskuser.sendActionBar("filling...:64\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            blockList.removeFirst();
                        }
                    }
                    else if (blockList.size()>=32) {
                        for(int i=0;i<32;i++) {
                            var blockdata = blockList.getFirst();
                            taskdimension.setBlockState(blockdata.x, blockdata.y, blockdata.z, taskblock);
                            taskuser.sendMessage("filling...:32\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            taskuser.sendActionBar("filling...:32\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            blockList.removeFirst();
                        }
                    }
                    else if (blockList.size()>=16) {
                        for(int i=0;i<16;i++) {
                            var blockdata = blockList.getFirst();
                            taskdimension.setBlockState(blockdata.x, blockdata.y, blockdata.z, taskblock);
                            taskuser.sendMessage("filling...:16\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            taskuser.sendActionBar("filling...:16\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            blockList.removeFirst();
                        }
                    }
                    else if (blockList.size()>=8) {
                        for(int i=0;i<8;i++) {
                            var blockdata = blockList.getFirst();
                            taskdimension.setBlockState(blockdata.x, blockdata.y, blockdata.z, taskblock);
                            taskuser.sendMessage("filling...:8\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            taskuser.sendActionBar("filling...:8\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            blockList.removeFirst();
                        }
                    }
                    else if (blockList.size()>=4) {
                        for(int i=0;i<4;i++) {
                            var blockdata = blockList.getFirst();
                            taskdimension.setBlockState(blockdata.x, blockdata.y, blockdata.z, taskblock);
                            taskuser.sendMessage("filling...:4\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            taskuser.sendActionBar("filling...:4\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            blockList.removeFirst();
                        }
                    }
                    else if (blockList.size()>=2) {
                        for(int i=0;i<2;i++) {
                            var blockdata = blockList.getFirst();
                            taskdimension.setBlockState(blockdata.x, blockdata.y, blockdata.z, taskblock);
                            taskuser.sendMessage("filling...:2\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            taskuser.sendActionBar("filling...:2\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                            blockList.removeFirst();
                        }
                    }
                    else if (blockList.size()>=1){
                        var blockdata = blockList.getFirst();
                        taskdimension.setBlockState(blockdata.x, blockdata.y, blockdata.z, taskblock);
                        taskuser.sendMessage("filling...:1\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                        taskuser.sendActionBar("filling...:1\n" + ((float) (tasktotal - blockList.size() + 1) / (float) tasktotal) * 100 + "%" + "\n" + (tasktotal - blockList.size() + 1) + "/" + tasktotal);
                        blockList.removeFirst();
                    }

                    return true;
                }

            }
        }, 1);
        Server.getInstance().getEventBus().registerListener(new TheEventListener());
        Server.getInstance().getScheduler().scheduleRepeating(this, () -> {
            var olp=Server.getInstance().getPlayerManager().getPlayers().values();
            for(EntityPlayer oneplayer : olp){
                if(debugarrow.get(oneplayer)==null||debugarrow.get(oneplayer)==false){
                    return true;
                }else{
                    DebugArrow debugLinex = new DebugArrow(
                            new Vector3f((float)oneplayer.getLocation().x(), (float)oneplayer.getLocation().y(),(float)oneplayer.getLocation().z()),
                            Color.RED,
                            new Vector3f((float)oneplayer.getLocation().x()+10, (float)oneplayer.getLocation().y(),(float)oneplayer.getLocation().z()),                            0.1f, // Arrowhead length
                            0.1f, // Arrowhead radius
                            4,    // Arrowhead segments
                            1.0f  // Arrowhead scale
                    );
                    DebugArrow debugLiney = new DebugArrow(
                            new Vector3f((float)oneplayer.getLocation().x(), (float)oneplayer.getLocation().y(),(float)oneplayer.getLocation().z()),
                            Color.GREEN,
                            new Vector3f((float)oneplayer.getLocation().x(), (float)oneplayer.getLocation().y()+10,(float)oneplayer.getLocation().z()),                            0.1f, // Arrowhead length
                            0.1f, // Arrowhead radius
                            4,    // Arrowhead segments
                            1.0f  // Arrowhead scale
                    );
                    DebugArrow debugLinez = new DebugArrow(
                            new Vector3f((float)oneplayer.getLocation().x(), (float)oneplayer.getLocation().y(),(float)oneplayer.getLocation().z()),
                            Color.BLUE,
                            new Vector3f((float)oneplayer.getLocation().x(), (float)oneplayer.getLocation().y(),(float)oneplayer.getLocation().z()+10),                            0.1f, // Arrowhead length
                            0.1f, // Arrowhead radius
                            4,    // Arrowhead segments
                            1.0f  // Arrowhead scale
                            );

                    DebugArrow debugArrow = new DebugArrow(
                            new Vector3f((float)oneplayer.getLocation().x(), (float)oneplayer.getLocation().y(),(float)oneplayer.getLocation().z()),

                            Color.YELLOW,
                            new Vector3f((float)oneplayer.getLocation().x()+10, (float)oneplayer.getLocation().y(),(float)oneplayer.getLocation().z()+10),

                            0.1f, // Arrowhead length
                            0.1f, // Arrowhead radius
                            4,    // Arrowhead segments
                            1.0f  // Arrowhead scale
                    );

                    oneplayer.getDimension().addDebugShape(debugLinex);
                    oneplayer.getDimension().addDebugShape(debugLiney);
                    oneplayer.getDimension().addDebugShape(debugLinez);
                    oneplayer.getDimension().addDebugShape(debugArrow);

                    Server.getInstance().getScheduler().scheduleDelayed(instance,()->{
                        oneplayer.getDimension().removeDebugShape(debugLinex);
                        oneplayer.getDimension().removeDebugShape(debugLiney);
                        oneplayer.getDimension().removeDebugShape(debugLinez);
                        oneplayer.getDimension().removeDebugShape(debugArrow);
                        return false;
                    },1);

                }
            }
            return true;
        }, 1);
        log.info("AllayBuilder enabled!");
    }

    @Override
    public void onDisable() {
        log.info("AllayBuilder disabled!");
    }
}